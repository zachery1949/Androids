#include "MyRTSPClient.hpp"

#undef LOG_TAG
#define LOG_TAG "MyRTSPClient"
#include "jnilog.h"



namespace VCAMERA {

#define DUMMY_SINK_RECEIVE_BUFFER_SIZE (1024 * 1024)

class DummySink: public MediaSink {
 public:
  static DummySink* createNew(UsageEnvironment& env, MediaSubsession& subsession,
        IRtspClientCallback& callback, char const* streamId = NULL) {
    return new DummySink(env, subsession, callback, streamId);
  }

 private:
  DummySink(UsageEnvironment& env, MediaSubsession& subsession, IRtspClientCallback& callback, char const* streamId)
    : MediaSink(env),
      fSubsession(subsession),
      mCallback(callback) {
    fStreamId = strDup(streamId);
    fReceiveBuffer = new u_int8_t[DUMMY_SINK_RECEIVE_BUFFER_SIZE];
  }
  virtual ~DummySink() {
    delete[] fReceiveBuffer;
    delete[] fStreamId;
  }
  static void AfterGettingFrame(void* clientData, unsigned frameSize, unsigned numTruncatedBytes, struct timeval presentationTime, unsigned durationInMicroseconds) {
    DummySink* pSelf = (DummySink*)clientData;
    pSelf->afterGettingFrame(frameSize, numTruncatedBytes, presentationTime, durationInMicroseconds);
  }
  void afterGettingFrame(unsigned frameSize, unsigned numTruncatedBytes, struct timeval presentationTime, unsigned durationInMicroseconds);
 private:
  // redefined virtual functions:
  virtual Boolean continuePlaying() override final {
    if (fSource == nullptr) return False; // sanity check (should not happen)
    fSource->getNextFrame(fReceiveBuffer, DUMMY_SINK_RECEIVE_BUFFER_SIZE,
                        &DummySink::AfterGettingFrame, this,
                        onSourceClosure, this);
    return True;
  }
private:
  MediaSubsession& fSubsession;
  IRtspClientCallback& mCallback;
  char* fStreamId;
  u_int8_t* fReceiveBuffer;
};

void DummySink::afterGettingFrame(unsigned frameSize, unsigned numTruncatedBytes, struct timeval presentationTime, unsigned durationInMicroseconds) {
  //TRACK("[afterGettingFrame]frameSize:%zu", frameSize);
  int64_t timeUs = presentationTime.tv_sec * 1000 * 1000 + presentationTime.tv_usec;
  mCallback.OnFrame(fReceiveBuffer, frameSize, timeUs);
  // Then continue, to request the next frame of data:
  continuePlaying();
}

MyRTSPClient* MyRTSPClient::Create(IRtspClientCallback& callback,
                                   UsageEnvironment& env,
                                   char const* rtspURL,
                                   int verbosityLevel,
                                   char const* applicationName,
                                   unsigned short desiredPort,
                                   portNumBits tunnelOverHTTPPortNum ) {
  return new MyRTSPClient(callback, env, rtspURL, verbosityLevel, applicationName, desiredPort, tunnelOverHTTPPortNum);
}

MyRTSPClient::MyRTSPClient(IRtspClientCallback& callback, UsageEnvironment& env, char const* rtspURL,
		int verbosityLevel, char const* applicationName, unsigned short desiredPort, portNumBits tunnelOverHTTPPortNum)
  : RTSPClient(env, rtspURL, verbosityLevel, applicationName, tunnelOverHTTPPortNum, -1),
    IRtspClient(),
    mCallback(callback),
    mDesiredPort(desiredPort),
    mScs() {}

void MyRTSPClient::Describe() {
  sendDescribeCommand(&MyRTSPClient::ContinueAfterDESCRIBE);
}

void MyRTSPClient::Teardown() {
  if (mScs.mpSession != nullptr) {
    Boolean someSubsessionsWereActive = False;
    MediaSubsessionIterator iter(*mScs.mpSession);
    MediaSubsession* pSubsession;
    while ((pSubsession = iter.next()) != nullptr) {
      if (pSubsession->sink != nullptr) {
        Medium::close(pSubsession->sink);
        pSubsession->sink = nullptr;
        if (pSubsession->rtcpInstance() != nullptr) {
          // in case the server sends a RTCP "BYE" while handling "TEARDOWN"
          pSubsession->rtcpInstance()->setByeHandler(nullptr, nullptr); 
        }
        someSubsessionsWereActive = True;
      }
    }
    if (someSubsessionsWereActive) {
      // Send a RTSP "TEARDOWN" command, to tell the server to shutdown the stream.
      // Don't bother handling the response to the "TEARDOWN".
      TRACK("[Teardown]Send a RTSP TEARDOWN command");
      sendTeardownCommand(*mScs.mpSession, nullptr);
    }
  }
  Medium::close(this);
}

void MyRTSPClient::ContinueAfterDESCRIBE(RTSPClient* pRtspClient, int resultCode, char* resultString) {
  auto pSelf = (MyRTSPClient*)pRtspClient;
  do {
    UsageEnvironment& env = pRtspClient->envir();
    StreamClientState& scs = pSelf->mScs; // alias
    pSelf->mCallback.OnDescribe(resultCode);
    if (resultCode != 0) {
      TRACK("[ContinueAfterDESCRIBE]Failed to get a SDP description:%s", resultString);
      delete[] resultString;
      break;
    }
    char* const sdpDescription = resultString;
    TRACK("[ContinueAfterDESCRIBE]SDP description:%s", sdpDescription);
    scs.mpSession = MediaSession::createNew(env, sdpDescription);
    delete[] sdpDescription; // because we don't need it anymore
    if (scs.mpSession == nullptr) {
      TRACK("[ContinueAfterDESCRIBE]Failed to create a MediaSession from the SDP description");
      break;
    } else if (!scs.mpSession->hasSubsessions()) {
      TRACK("[ContinueAfterDESCRIBE]This session has no media subsessions");
      break;
    }
    // Then, create and set up our data source objects for the session.  We do this by iterating over the session's 'subsessions',
    // calling "MediaSubsession::initiate()", and then sending a RTSP "SETUP" command, on each one.
    // (Each 'subsession' will have its own data source.)
    scs.mpIter = new MediaSubsessionIterator(*scs.mpSession);
    pSelf->SetupNextSubsession();
    return;
  } while (0);
  // An unrecoverable error occurred with this stream.
  pSelf->ShutdownStream();
}

#define REQUEST_STREAMING_OVER_TCP False

void MyRTSPClient::SetupNextSubsession() {
  UsageEnvironment& env = this->envir(); // alias
  mScs.mpSubsession = mScs.mpIter->next();
  if (mScs.mpSubsession != nullptr) {
    mScs.mpSubsession->setClientPortNum(mDesiredPort);
    if (!mScs.mpSubsession->initiate()) {
      TRACK("[SetupNextSubsession]Failed to initiate the subsession");
      SetupNextSubsession();
    } else {
      //TRACK("[SetupNextSubsession]Initiate the subsession");
      unsigned short client_port = mScs.mpSubsession->clientPortNum();
      if (mScs.mpSubsession->rtcpIsMuxed()) {
        TRACK("[SetupNextSubsession]ClientPort:%d", client_port);
      } else {
        TRACK("[SetupNextSubsession:MDCID]ClientPorts:%d-%d", client_port, client_port+1);
      }
      // Continue setting up this subsession, by sending a RTSP "SETUP" command:
      sendSetupCommand(*mScs.mpSubsession, &MyRTSPClient::ContinueAfterSETUP, False, REQUEST_STREAMING_OVER_TCP);
    }
    return;
  }
  // We've finished setting up all of the subsessions.  Now, send a RTSP "PLAY" command to start the streaming:
  if (mScs.mpSession->absStartTime() != nullptr) {
    // Special case: The stream is indexed by 'absolute' time, so send an appropriate "PLAY" command:
    sendPlayCommand(*mScs.mpSession, &MyRTSPClient::ContinueAfterPLAY, mScs.mpSession->absStartTime(), mScs.mpSession->absEndTime());
  } else {
    mScs.mDuration = mScs.mpSession->playEndTime() - mScs.mpSession->playStartTime();
    sendPlayCommand(*mScs.mpSession, &MyRTSPClient::ContinueAfterPLAY);
  }
}

void MyRTSPClient::ContinueAfterSETUP(RTSPClient* pRtspClient, int resultCode, char* resultString) {
  auto pSelf = (MyRTSPClient*)pRtspClient;
  do {
    UsageEnvironment& env = pSelf->envir();
    StreamClientState& scs = pSelf->mScs; // alias
    pSelf->mCallback.OnSetup(resultCode);
    if (resultCode != 0) {
      TRACK("[ContinueAfterSETUP]Failed to set up the subsession:%s", resultString);
      break;
    }
    unsigned short client_port = scs.mpSubsession->clientPortNum();
    if (scs.mpSubsession->rtcpIsMuxed()) {
      TRACK("[ContinueAfterSETUP]SUCCESS ClientPort:%d", client_port);
    } else {
      TRACK("[ContinueAfterSETUP]SUCCESS ClientPorts:%d-%d", client_port, client_port+1);
    }
    // Having successfully setup the subsession, create a data sink for it, and call "startPlaying()" on it.
    // (This will prepare the data sink to receive data; the actual flow of data from the client won't start happening until later,
    // after we've sent a RTSP "PLAY" command.)
    scs.mpSubsession->sink = DummySink::createNew(env, *scs.mpSubsession, pSelf->mCallback, pSelf->url());
    if (scs.mpSubsession->sink == nullptr) {
      TRACK("[ContinueAfterSETUP]Failed to create a data sink:%s", env.getResultMsg());
      break;
    }
    //TRACK("[ContinueAfterSETUP]SUCCESS Create DataSink");
    scs.mpSubsession->miscPtr = pSelf; // a hack to let subsession handler functions get the "RTSPClient" from the subsession 
    scs.mpSubsession->sink->startPlaying(*(scs.mpSubsession->readSource()),
                       &MyRTSPClient::SubsessionAfterPlaying, scs.mpSubsession);
    // Also set a handler to be called if a RTCP "BYE" arrives for this subsession:
    if (scs.mpSubsession->rtcpInstance() != NULL) {
//      scs.mpSubsession->rtcpInstance()->setByeWithReasonHandler(
//        &MyRTSPClient::SubsessionByeHandler, scs.mpSubsession);
    }
//    if (scs.subsession->rtcpInstance() != NULL) {
//      scs.subsession->rtcpInstance()->setByeHandler(subsessionByeHandler, scs.subsession);
//    }
  } while (0);
  delete[] resultString;
  // Set up the next subsession, if any:
  pSelf->SetupNextSubsession();
}

void MyRTSPClient::ContinueAfterPLAY(RTSPClient* pRtspClient, int resultCode, char* resultString) {
  Boolean success = False;
  auto pSelf = (MyRTSPClient*)pRtspClient;
  do {
    UsageEnvironment& env = pSelf->envir();
    StreamClientState& scs = pSelf->mScs; // alias
    pSelf->mCallback.OnPlay(resultCode);
    if (resultCode != 0) {
      TRACK("[ContinueAfterPLAY]Failed to start playing session:%s", resultString);
      break;
    }
    // Set a timer to be handled at the end of the stream's expected duration (if the stream does not already signal its end
    // using a RTCP "BYE").  This is optional.  If, instead, you want to keep the stream active - e.g., so you can later
    // 'seek' back within it and do another RTSP "PLAY" - then you can omit this code.
    // (Alternatively, if you don't want to receive the entire stream, you could set this timer for some shorter value.)
    if (scs.mDuration > 0) {
      unsigned const delaySlop = 2; // number of seconds extra to delay, after the stream's expected duration.  (This is optional.)
      scs.mDuration += delaySlop;
      unsigned uSecsToDelay = (unsigned)(scs.mDuration*1000000);
      scs.mStreamTimerTask = env.taskScheduler().scheduleDelayedTask(uSecsToDelay, &MyRTSPClient::StreamTimerHandler, pSelf);
    }
    //TRACK("[ContinueAfterPLAY]Started playing session (up to %f secondes)", pScs->mDuration);
    success = True;
  } while (0);
  delete[] resultString;
  if (!success) {
    // An unrecoverable error occurred with this stream.
    pSelf->ShutdownStream();
  }
}

void MyRTSPClient::SubsessionAfterPlaying(void* clientData) {
  MediaSubsession* pSubsession = (MediaSubsession*)clientData;
  auto pSelf = (MyRTSPClient*)(pSubsession->miscPtr);
  // Begin by closing this subsession's stream:
  //TRACK("[SubsessionAfterPlaying]closing this subsession's stream");
  Medium::close(pSubsession->sink);
  pSubsession->sink = NULL;
  // Next, check whether *all* subsessions' streams have now been closed:
  MediaSession& session = pSubsession->parentSession();
  MediaSubsessionIterator iter(session);
  while ((pSubsession = iter.next()) != NULL) {
    if (pSubsession->sink != NULL) return; // this subsession is still active
  }
  TRACK("[SubsessionAfterPlaying]All subsessions have now been closed, shutdown the client");
  pSelf->ShutdownStream();
}

void MyRTSPClient::SubsessionByeHandler(void* clientData, char const* reason) {
  MediaSubsession* pSubsession = (MediaSubsession*)clientData;
  auto pSelf = (MyRTSPClient*)(pSubsession->miscPtr);
  UsageEnvironment& env = pSelf->envir(); // alias
  TRACK("[SubsessionByeHandler]Received RTCP \"BYE\"");
  if (reason != NULL) {
    TRACK("[SubsessionByeHandler]reason:%s", reason);
    delete[] (char*)reason;
  }
  // Now act as if the subsession had closed:
  SubsessionAfterPlaying(pSubsession);
}

void MyRTSPClient::StreamTimerHandler(void* clientData) {
  //TRACK("[StreamTimerHandler]Enter");
  auto pSelf = (MyRTSPClient*)(clientData);
  StreamClientState& scs = pSelf->mScs; // alias
  scs.mStreamTimerTask = nullptr;
  pSelf->ShutdownStream();
  //TRACK("[StreamTimerHandler]Exit");
}

void MyRTSPClient::ShutdownStream() {
  TRACK("[ShutdownStream]Enter");
  /*
  // First, check whether any subsessions have still to be closed:
  if (mScs.mpSession != nullptr) { 
    Boolean someSubsessionsWereActive = False;
    MediaSubsessionIterator iter(*mScs.mpSession);
    MediaSubsession* pSubsession;

    while ((pSubsession = iter.next()) != nullptr) {
      if (pSubsession->sink != nullptr) {
        Medium::close(pSubsession->sink);
        pSubsession->sink = nullptr;
        if (pSubsession->rtcpInstance() != nullptr) {
          // in case the server sends a RTCP "BYE" while handling "TEARDOWN"
          pSubsession->rtcpInstance()->setByeHandler(nullptr, nullptr); 
        }
        someSubsessionsWereActive = True;
      }
    }
    if (someSubsessionsWereActive) {
      // Send a RTSP "TEARDOWN" command, to tell the server to shutdown the stream.
      // Don't bother handling the response to the "TEARDOWN".
      sendTeardownCommand(*mScs.mpSession, nullptr);
    }
  }
  */
  mCallback.OnShutdownStream();
  TRACK("[ShutdownStream]Exit");
}

} // namespace VCAMERA
