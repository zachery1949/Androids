//
// Created by zc1942 on 2022/11/15.
//
#include <jni.h>
#include <string>
#include <android/log.h>

using namespace std;


#ifdef __cplusplus
extern "C" {
#endif
//OnvifBeDiscovered
JNIEXPORT jstring JNICALL Java_com_example_cpufull_NDKtools_NDKutils_FullCpu
        (JNIEnv *env, jclass jclazz){
    //maain();
    return env->NewStringUTF("Hello JNI");
}


#ifdef __cplusplus
}
#endif


extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_myapplication_NDKtools_func(JNIEnv *env, jclass clazz) {
    jclass staticclazz = env->FindClass("com/example/myapplication/Student");
    jmethodID StaticshowID = env->GetStaticMethodID(staticclazz, "func2", "(Ljava/lang/String;I)V");
    env->CallStaticVoidMethod(staticclazz,StaticshowID,env->NewStringUTF("Hello JNI456"),10);
    return env->NewStringUTF("Hello JNI123");
}