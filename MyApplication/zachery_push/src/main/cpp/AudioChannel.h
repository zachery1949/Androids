#ifndef DERRY_PUSH_AUDIOCHANNEL_H
#define DERRY_PUSH_AUDIOCHANNEL_H

// #include "libfaac/include/faac.h" 这样写没有任何问题
#include <faac.h>
#include <sys/types.h>

class AudioChannel {

public:
    typedef void (*AudioCallback)(RTMPPacket *packet);
    AudioChannel();
    ~AudioChannel();

    void initAudioEncoder(int sample_rate, int num_channels);

    int getInputSamples();

    void encodeData(signed char *data);

    void setAudioCallback(AudioCallback audioCallback);

    RTMPPacket * getAudioSeqHeader();

private:
    u_long inputSamples; // faac输出的样本数
    u_long maxOutputBytes; // faac 编码器 最大能输出的字节数
    int mChannels; // 通道数
    faacEncHandle audioEncoder = 0; // 音频编码器
    u_char *buffer = 0; // 后面要用到的 缓冲区
    AudioCallback audioCallback;


};


#endif //DERRY_PUSH_AUDIOCHANNEL_H
