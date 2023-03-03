//
// Created by zc1942 on 2022/11/15.
//
#include <jni.h>
#include <string>
#include <android/log.h>
#include<android/log.h>
#include"BufferArea.h"
#ifdef __cplusplus
extern "C" {
#endif
#include "bspatch.h"
#ifdef __cplusplus
}
#endif
using namespace std;

#ifndef LOG_TAG
#define LOG_TAG "HELLO_JNI"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG ,__VA_ARGS__) // 定义LOGI类型
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,LOG_TAG ,__VA_ARGS__) // 定义LOGW类型
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG ,__VA_ARGS__) // 定义LOGE类型
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,LOG_TAG ,__VA_ARGS__) // 定义LOGF类型
#endif

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
//    jclass staticclazz = env->FindClass("com/example/myapplication/Student");
//    jmethodID StaticshowID = env->GetStaticMethodID(staticclazz, "func2", "(Ljava/lang/String;I)V");
//    env->CallStaticVoidMethod(staticclazz,StaticshowID,env->NewStringUTF("Hello JNI456"),10);
    return env->NewStringUTF("Hello JNI123");
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_myapplication_NDKtools_helloJni(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF("Hello JNI456");
}


extern "C"
JNIEXPORT void JNICALL
Java_com_example_myapplication_NDKtools_helloJniCanshu(JNIEnv *env, jobject thiz, jstring canshu) {
    char * name ="jniname";
    LOGD("My name is %s.", name);
    // (2) jstring 转换成 const char * charstr
    const char *charstr = env->GetStringUTFChars(canshu, 0);
    LOGD(" %s.", charstr);
    // (3) 释放 const char *
    env->ReleaseStringUTFChars(canshu, charstr);

//    LOGD("123%s",name);
}
pthread_t pthread;//线程对象
pthread_t pthreadp1;//生产者1
pthread_t pthreadp2;//生产者2
pthread_t pthreadp3;//生产者3
pthread_t pthreadc1;//消费者1
pthread_t pthreadc2;//消费者2
pthread_t pthreadc3;//消费者3
BufferArea *bufferArea;
JavaVM* global_jvm;
jobject  gInstance; //全局JNI对象引用
void *threadProduct(void *tmp){
    while(true){
        sleep(2);
        bufferArea->set();//生产产品
    }
}
void *threadConsume(void *tmp){
    while(true){
        sleep(1);
        bufferArea->get();//生产产品
    }
}
void *threadDoThings(void *tmp){
    //jobject * instance = static_cast<jobject *>(instancetmp);
    JNIEnv* env;
    if(global_jvm->AttachCurrentThread(&env, NULL)!=JNI_OK){
        LOGD("error");
        return NULL;
    }
    //获取类名
    jclass  clazz = env->GetObjectClass(gInstance);
    if(clazz == NULL) return NULL;
    jmethodID  javaMethod = env->GetMethodID(clazz,"StudentHelloWorld","(Ljava/lang/String;)V");
    if(javaMethod == NULL)return NULL;
    const char * msg = "nancy";
    jstring  jmsg = env->NewStringUTF(msg);
    env->CallVoidMethod(gInstance,javaMethod,jmsg);
    env->DeleteGlobalRef(gInstance);//在我们不需要gThis这个全局JNI对象应用时，可以将其删除
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_myapplication_NDKtools_JniCalljava(JNIEnv *env, jobject thiz, jobject instance) {


    env->GetJavaVM(&global_jvm);
    gInstance = env->NewGlobalRef(instance);
    pthread_create(&pthread, NULL, threadDoThings, NULL);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_myapplication_NDKtools_JniConsumer(JNIEnv *env, jobject thiz) {
    bufferArea = new BufferArea();
    pthread_create(&pthreadp1, NULL, threadProduct, NULL);
    pthread_create(&pthreadp2, NULL, threadProduct, NULL);
    pthread_create(&pthreadp3, NULL, threadProduct, NULL);
    pthread_create(&pthreadc1, NULL, threadConsume, NULL);
    pthread_create(&pthreadc2, NULL, threadConsume, NULL);
    pthread_create(&pthreadc3, NULL, threadConsume, NULL);
//    bufferArea->set();
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_myapplication_NDKtools_patchAPK(JNIEnv *env, jobject thiz, jstring old_apk_file,
                                                 jstring new_apk_file, jstring patch_file) {
    int argc = 4;
    char * argv[argc];
    argv[0] = "bspatch";
    argv[1] = (char*) (env->GetStringUTFChars(old_apk_file, 0));
    argv[2] = (char*) (env->GetStringUTFChars(new_apk_file, 0));
    argv[3] = (char*) (env->GetStringUTFChars(patch_file, 0));

    //调用合并的方法
    main(argc, argv);

    env->ReleaseStringUTFChars(old_apk_file, argv[1]);
    env->ReleaseStringUTFChars(new_apk_file, argv[2]);
    env->ReleaseStringUTFChars(patch_file, argv[3]);
}