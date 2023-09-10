#ifndef NALU_HPP
#define NALU_HPP

#include <vector>
#include <stdlib.h>


class Nalu {
 public:
  Nalu();
  ~Nalu();

  void SetData(const unsigned char* pData, size_t len, size_t startCodeLen);
  void SetEBSP(const unsigned char* pData, size_t len);
  const unsigned char* GetData(size_t* pDataLen = NULL) const;
  size_t GetStartCodeLen() const { return mStartCodeLen; }

  int GetNaluType() const;
  bool IsVps() const;
  bool IsSps() const;
  bool IsPps() const;
  bool IsIdr() const;
  bool IsP() const;
  void Dump(const char* pTag) const;
 private:
  std::vector<unsigned char> mData;
  size_t mStartCodeLen;


};









#endif  // NALU_HPP
