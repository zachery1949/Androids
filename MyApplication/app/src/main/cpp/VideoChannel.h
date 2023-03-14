#ifndef DERRY_PUSH_VIDEOCHANNEL_H
#define DERRY_PUSH_VIDEOCHANNEL_H

#include <pthread.h>
//#include <x264.h>

class VideoChannel {

public:
    VideoChannel();
    ~VideoChannel();

    typedef void (*VideoCallback)(RTMPPacket *packet);
private:
    pthread_mutex_t mutex;
    int mWidth;
    int mHeight;
    int mFps;
    int mBitrate;
    int y_len; // Y分量的长度
    int uv_len; // uv分量的长度
//    x264_t *videoEncoder = 0; // x264编码器
//    x264_picture_t *pic_in = 0; // 先理解是每一张图片 pic
    VideoCallback videoCallback;

public:
    void initVideoEncoder(int width, int height, int fps, int bitrate);

    void encodeData(signed char *data);

    void sendSpsPps(uint8_t sps[100], uint8_t pps[100], int sps_len, int pps_len);

    void setVideoCallback(void (*param)(RTMPPacket *));

    void sendFrame(int type, int payload, uint8_t *payload1);
};


#endif //DERRY_PUSH_VIDEOCHANNEL_H
