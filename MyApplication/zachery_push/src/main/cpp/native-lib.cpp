#include <jni.h>
#include <string>

// #include "librtmp/xx/xxx/xx/xx/xx/xx/xx/xxx/x/xxx/xxxxxxxxx/xxxxxx/xxxx/rtmp.h"
// #include "librtmp/rtmp.h"

//extern "C" { //不用管了， 由于这些C源码，内部已经做了 extern "C" 你就管了， FFmpeg没有做
    #include <rtmp.h> // 查找系统的环境变量 <>
    #include <x264.h> // 查找系统的环境变量 <>
    #include "VideoChannel.h"
    #include "util.h"
    #include "safe_queue.h"

#include "safe_queue_s.h"
    #include "AudioChannel.h"
// }

extern "C" JNIEXPORT jstring JNICALL
Java_com_derry_pusher_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {

    std::string hello = "Hello from C++";
    char version[50];
    //  2.3
    sprintf(version, "librtmp version: %d", RTMP_LibVersion());

    // ___________________________________> 下面是x264 验证  视频编码
    x264_picture_t *picture = new x264_picture_t;

    // ___________________________________> 下面是faac 验证 音频编码


    return env->NewStringUTF(version);
}

VideoChannel * videoChannel = 0;
AudioChannel * audioChannel = 0;
bool isStart;
pthread_t pid_start;
bool readyPushing;
SafeQueue<RTMPPacket *> packets; // 不区分音频和视频，（音频 & 视频）一直存储，  start直接拿出去发送给流媒体服务器（添加到队列中的是压缩包）
uint32_t start_time; // 记录时间搓，为了计算下，时间搓

// 释放RTMPPacket * 包的函数指针实现
// T无法释放， 让外界释放
void releasePackets(RTMPPacket **packet) {
    if (packet) {
        RTMPPacket_Free(*packet);
        //delete packet;
        packet = 0;
    }
}

// videoCallback 函数指针的实现（存放packet到队列）
void callback(RTMPPacket *packet) {
    if (packet) {
        if (packet->m_nTimeStamp == -1) {
            packet->m_nTimeStamp = RTMP_GetTime() - start_time; // 如果是sps+pps 没有时间搓，如果是I帧就需要有时间搓
        }
        packets.push(packet); // 存入队列里面  把刚刚的音频Packet丢进去
    }
}

/**
 * DerryPusher构造函数调用的 ---> 初始化工作
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_derry_pusher_DerryPusher_native_1init(JNIEnv *env, jobject thiz) {
    // C++层的初始化工作而已
    videoChannel = new VideoChannel();
    audioChannel = new AudioChannel();

    // 存入队列的 关联
    videoChannel->setVideoCallback(callback);

    // 存入队列的 关联
    audioChannel->setAudioCallback(callback);

    // 队列的释放工作 关联
    packets.setReleaseCallback(releasePackets);
}

void *task_start(void *args) {
    char *url = static_cast<char *>(args);
    // TODO RTMPDump API 九部曲
    RTMP *rtmp = 0;
    int ret; // 返回值判断成功失败

    do { // 为了方便流程控制，方便重试，习惯问题而已，如果不用（方便错误时重试）
        // 1.1，rtmp 初始化
        rtmp = RTMP_Alloc();
        if (!rtmp) {
            LOGE("rtmp 初始化失败");
            break;
        }

        // 1.2，rtmp 初始化
        RTMP_Init(rtmp);
        rtmp->Link.timeout = 5; // 设置连接的超时时间（以秒为单位的连接超时）

        // 2，rtmp 设置流媒体地址
        ret = RTMP_SetupURL(rtmp, url);
        if (!ret) { // ret == 0 和 ffmpeg不同，0代表失败
            LOGE("rtmp 设置流媒体地址失败");
            break;
        }

        // 3，开启输出模式
        RTMP_EnableWrite(rtmp);

        // 4，建立连接
        ret = RTMP_Connect(rtmp, 0);
        if (!ret) { // ret == 0 和 ffmpeg不同，0代表失败
            LOGE("rtmp 建立连接失败:%d, url: %s", ret, url);
            break;
        }

        // 5，连接流
        ret = RTMP_ConnectStream(rtmp, 0);
        if (!ret) { // ret == 0 和 ffmpeg不同，0代表失败
            LOGE("rtmp 连接流失败");
            break;
        }

        start_time = RTMP_GetTime();

        // 准备好了，可以开始向服务器推流了
        readyPushing = true;

        // TODO 测试是不用发送序列头信息，是没有任何问题的，但是还是规矩
        // callback(audioChannel->getAudioSeqHeader());

        // 我从队列里面获取包，直接发给服务器

        // 队列开始工作
        packets.setWork(1);

        RTMPPacket * packet = 0;

        while (readyPushing) {
            packets.pop(packet); // 阻塞式

            if (!readyPushing) {
                break;
            }

            if (!packet) {
                continue;
            }

            // TODO 到这里就是成功的去除队列的ptk了，就可以发送给流媒体服务器

            // 给rtmp的流id
            packet->m_nInfoField2 = rtmp->m_stream_id;

            // 成功取出数据包，发送
            ret = RTMP_SendPacket(rtmp, packet, 1); // 1==true 开启内部缓冲

            // packet 你都发给服务器了，可以大胆释放
            releasePackets(&packet);

            if (!ret) { // ret == 0 和 ffmpeg不同，0代表失败
                LOGE("rtmp 失败 自动断开服务器");
                break;
            }
        }
        releasePackets(&packet); // 只要跳出循环，就释放
    } while (false);

    // 本次一系列释放工作
    isStart = false;
    readyPushing = false;
    packets.setWork(0);
    packets.clear();

    if (rtmp) {
        RTMP_Close(rtmp);
        RTMP_Free(rtmp);
    }
    delete url;

    return 0;
}

/**
 * 开始直播 ---> 启动工作
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_derry_pusher_DerryPusher_native_1start(JNIEnv *env, jobject thiz, jstring path_) {
    // 子线程  1.连接流媒体服务器， 2.发包
    if (isStart) {
        return;
    }
    isStart = true;
    const char *path = env->GetStringUTFChars(path_, 0);

    // 深拷贝
    // 以前我们在做player播放器的时候，是用Flag来控制（isPlaying）
    char *url = new char(strlen(path) + 1); // C++的堆区开辟 new -- delete
    strcpy(url, path);

    // 创建线程来进行直播
    pthread_create(&pid_start, 0, task_start, url);

    env->ReleaseStringUTFChars(path_, path); // 你随意释放，我已经深拷贝了
}

/**
 * 初始化x264编码器 给下面的pushVideo函数，去编码
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_derry_pusher_DerryPusher_native_1initVideoEncoder(JNIEnv *env, jobject thiz, jint width,
                                                           jint height, jint fps, jint bitrate) {
    if (videoChannel) {
        videoChannel->initVideoEncoder(width, height, fps, bitrate);
    }
}

/**
 * 往队列里面 加入 RTMPPkt（x264编码后的RTMPPkt）
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_derry_pusher_DerryPusher_native_1pushVideo(JNIEnv *env, jobject thiz, jbyteArray data_) {
    // data == nv21数据  编码  加入队列
    if (!videoChannel || !readyPushing) { return; }

    // 把jni ---> C语言的
    jbyte *data = env->GetByteArrayElements(data_, 0);
    videoChannel->encodeData(data);
    env->ReleaseByteArrayElements(data_, data, 0); // 释放byte[]
}


// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 下面是 音频工作工作

// faac编码器 实例化成功
extern "C"
JNIEXPORT void JNICALL
Java_com_derry_pusher_DerryPusher_native_1initAudioEncoder(JNIEnv *env, jobject thiz,
                                                           jint sample_rate, jint num_channels) {
    if (audioChannel) {
        audioChannel->initAudioEncoder(sample_rate, num_channels);
    }
}

// 获取 faac的样本数 给 Java层
extern "C"
JNIEXPORT jint JNICALL
Java_com_derry_pusher_DerryPusher_native_1getInputSamples(JNIEnv *env, jobject thiz) {
    if (audioChannel) {
        return audioChannel->getInputSamples();
    }
    return 0;
}

// 使用上面的faac编码器，编码，封包，入队， start线程发送给流媒体服务器
extern "C"
JNIEXPORT void JNICALL
Java_com_derry_pusher_DerryPusher_native_1pushAudio(JNIEnv *env, jobject thiz, jbyteArray data_) {
    if (!audioChannel || !readyPushing) {
        return;
    }
    jbyte *data = env->GetByteArrayElements(data_, 0); // 此data数据就是AudioRecord采集到的原始数据
    audioChannel->encodeData(data); // 核心函数：对音频数据 【进行faac的编码工作】
    env->ReleaseByteArrayElements(data_, data, 0); // 释放byte[]
}


// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 下面是 释放工作

extern "C"
JNIEXPORT void JNICALL
Java_com_derry_pusher_DerryPusher_native_1stop(JNIEnv *env, jobject thiz) {
    isStart = false; // start的jni函数拦截的标记，为false
    readyPushing = false; // 九部曲推流的标记，为false
    packets.setWork(0); // 队列不准工作了
    pthread_join(pid_start, 0); // 稳稳等待start线程执行完成后，我在做后面的处理工作
    // ... 为以后做预留
}

extern "C"
JNIEXPORT void JNICALL
Java_com_derry_pusher_DerryPusher_native_1release(JNIEnv *env, jobject thiz) {
    DELETE(videoChannel);
    DELETE(audioChannel);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_zachery_1push_VideoChanel_native_1init(JNIEnv *env, jobject thiz) {
    // C++层的初始化工作而已
    videoChannel = new VideoChannel();
    audioChannel = new AudioChannel();

    // 存入队列的 关联
    videoChannel->setVideoCallback(callback);

    // 存入队列的 关联
    audioChannel->setAudioCallback(callback);

    // 队列的释放工作 关联
    packets.setReleaseCallback(releasePackets);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_zachery_1push_VideoChanel_native_1start(JNIEnv *env, jobject thiz, jstring path_) {
    // 子线程  1.连接流媒体服务器， 2.发包
    if (isStart) {
        return;
    }
    isStart = true;
    const char *path = env->GetStringUTFChars(path_, 0);

    // 深拷贝
    // 以前我们在做player播放器的时候，是用Flag来控制（isPlaying）
    char *url = new char(strlen(path) + 1); // C++的堆区开辟 new -- delete
    strcpy(url, path);

    // 创建线程来进行直播
    pthread_create(&pid_start, 0, task_start, url);

    env->ReleaseStringUTFChars(path_, path); // 你随意释放，我已经深拷贝了
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_zachery_1push_VideoChanel_native_1initVideoEncoder(JNIEnv *env, jobject thiz,
                                                                    jint width, jint height,
                                                                    jint fps, jint bitrate) {
    if (videoChannel) {
        videoChannel->initVideoEncoder(width, height, fps, bitrate);
    }
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_zachery_1push_VideoChanel_native_1pushVideo(JNIEnv *env, jobject thiz,
                                                             jbyteArray data_) {
    // data == nv21数据  编码  加入队列
    if (!videoChannel || !readyPushing) { return; }

    // 把jni ---> C语言的
    jbyte *data = env->GetByteArrayElements(data_, 0);
    videoChannel->encodeData(data);
    env->ReleaseByteArrayElements(data_, data, 0); // 释放byte[]
}