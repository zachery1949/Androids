#ifndef MY_RTSP_CLIENT_HPP
#define MY_RTSP_CLIENT_HPP
#include "IRtspClient.hpp"


#include "jniLibs/arm64-v8a/include/BasicUsageEnvironment/include/BasicUsageEnvironment.hh"
#include "jniLibs/arm64-v8a/include/liveMedia/include/RTSPClient.hh"

namespace VCAMERA {

class MyRTSPClient : public RTSPClient, public IRtspClient {
 public:
  static MyRTSPClient* Create(IRtspClientCallback& callback, UsageEnvironment& env, char const* rtspURL,
		int verbosityLevel, char const* applicationName, unsigned short desiredPort, portNumBits tunnelOverHTTPPortNum = 0);
  virtual void Describe() override final;
  virtual void Teardown() override final;
 protected:
  MyRTSPClient(IRtspClientCallback& callback, UsageEnvironment& env, char const* rtspURL,
		int verbosityLevel, char const* applicationName, unsigned short desiredPort, portNumBits tunnelOverHTTPPortNum);
  virtual ~MyRTSPClient() = default;
 public:
  class StreamClientState {
   public:
    StreamClientState()
      : mpIter(nullptr),
        mpSession(nullptr),
        mpSubsession(nullptr),
        mStreamTimerTask(nullptr),
        mDuration(0.0) {}
    ~StreamClientState() {
      if (mpIter != nullptr) {
        delete mpIter;
        mpIter = nullptr;
      }
      if (mpSession != nullptr) {
        if (mStreamTimerTask != nullptr) {
          UsageEnvironment& env = mpSession->envir();
          env.taskScheduler().unscheduleDelayedTask(mStreamTimerTask);
          mStreamTimerTask = nullptr;
        }
        Medium::close(mpSession);
        mpSession = nullptr;
      }
      mDuration = 0.0;
    }
    MediaSubsessionIterator* mpIter;
    MediaSession* mpSession;
    MediaSubsession* mpSubsession;
    TaskToken mStreamTimerTask;
    double mDuration;
  };
 public:
  /* data */
  IRtspClientCallback& mCallback;
  int mCameraId;
  unsigned short mDesiredPort;
  StreamClientState mScs;
 private:
  static void ContinueAfterDESCRIBE(RTSPClient* pRtspClient, int resultCode, char* resultString);
  static void ContinueAfterSETUP(RTSPClient* pRtspClient, int resultCode, char* resultString);
  static void ContinueAfterPLAY(RTSPClient* pRtspClient, int resultCode, char* resultString);
  void SetupNextSubsession();
  // called when a stream's subsession (e.g., audio or video substream) ends
  static void SubsessionAfterPlaying(void* clientData);
  static void SubsessionByeHandler(void* clientData, char const* reason);
  static void StreamTimerHandler(void* clientData);
  void ShutdownStream();
};



















} // namespace VCAMERA

#endif  // MY_RTSP_CLIENT_HPP
