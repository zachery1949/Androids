#ifndef I_RTSP_CLIENT_HPP
#define I_RTSP_CLIENT_HPP

#include <cstdint>

namespace VCAMERA {

class IRtspClientCallback {
 public:
  virtual ~IRtspClientCallback() = default;

  virtual void OnDescribe(int resultCode) = 0;
  virtual void OnSetup(int resultCode) = 0;
  virtual void OnPlay(int resultCode) = 0;
  virtual void OnFrame(const unsigned char* pData, size_t len, int64_t presentationTime) = 0;
  virtual void OnShutdownStream() = 0;
};

class IRtspClient {
 public:
  virtual ~IRtspClient() = default;
  virtual void Describe() = 0;
  virtual void Teardown() = 0;
 public:
};

} // namespace VCAMERA

#endif  // I_RTSP_CLIENT_HPP
