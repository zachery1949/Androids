#include <string>
#include "Nalu.hpp"
#include "jnilog.h"



#undef LOG_TAG
#define LOG_TAG "Nalu"




enum NaluType {
  SLICE = 1,
  IDR = 19,
  VPS = 32,
  SPS = 33,
  PPS = 34,
  PREFIX_SEI = 39,
  SUFFIX_SEI = 40,
};

const static std::vector<unsigned char> START_CODE { 0x00, 0x00, 0x00, 0x01 };
void Nalu::SetData(const unsigned char* pData, size_t len, size_t startCodeLen) {
  mData.clear();
  mData.assign(pData, pData + len);
  mStartCodeLen = startCodeLen;
}

void Nalu::SetEBSP(const unsigned char* pData, size_t len) {
  mData.clear();
  mData.insert(mData.end(), START_CODE.begin(), START_CODE.end());
  mData.insert(mData.end(), pData, pData + len);
  mStartCodeLen = START_CODE.size();
}

const unsigned char* Nalu::GetData(size_t* pDataLen) const {
  if (pDataLen != NULL) {
    *pDataLen = mData.size();
  }
  return mData.data();
}

int Nalu::GetNaluType() const {
  unsigned char naluHead = mData[mStartCodeLen];
  int naluType = (naluHead >> 1) & 0x3F;
  //ALOGD("[GetNaluType]startCodeLen:%zu  naluType:%d", mStartCodeLen, naluType);
  //TRACK("[GetNaluType]startCodeLen:%zu  naluType:%d", mStartCodeLen, naluType);
  return naluType;
}

bool Nalu::IsVps() const {
  return GetNaluType() == NaluType::VPS;

}
bool Nalu::IsSps() const {
  return GetNaluType() == NaluType::SPS;
}

bool Nalu::IsPps() const {
  return GetNaluType() == NaluType::PPS;
}

bool Nalu::IsIdr() const {
  return GetNaluType() == NaluType::IDR;
}
bool Nalu::IsP() const {
  return GetNaluType() == NaluType::SLICE;
}
std::string ByteArrayToHexString(const char* pArr, int len) {
  static std::string HEXS("0123456789ABCDEF");
  std::string result;
  for (int i = 0; i < len; i++) {
    result += HEXS[((pArr[i] & 0xF0) >> 4)];
    result += HEXS[(pArr[i] & 0x0F)];
    if (i < (len - 1)) {
      result += " ";
    }
  }
  return result;
}
void Nalu::Dump(const char* pTag) const {
  std::string str = ByteArrayToHexString((const char*)mData.data(), mData.size());
  if (pTag != NULL) {
    TRACK("[Dump][%s]size:%zu  %s", pTag, mData.size(), str.c_str());
  } else {
    TRACK("[Dump]size:%zu %s", mData.size(), str.c_str());
  }
}

Nalu::Nalu() {

}

Nalu::~Nalu() {

}


