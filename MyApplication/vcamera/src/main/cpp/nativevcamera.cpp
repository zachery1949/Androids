#include <jni.h>



extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_vcamera_JniUtils_helloJni(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF("Hello JNI_VCAMERA");
}
//extern "C"
//JNIEXPORT void JNICALL
//Java_com_example_rtspplay_rtsp_1player_1can_JniUtils_LiveStart(JNIEnv *env, jobject thiz) {
//    // TODO: implement LiveStart()
//    live555_start("rtsp://192.168.0.105:554/testfile.265");
//}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_vcamera_JniUtils_liveStart(JNIEnv *env, jobject thiz) {
    // TODO: implement liveStart()
    //live555_start("rtsp://192.168.0.105:554/testfile.265");
}