//
// Created by BigBrother on 2023/2/22.
//

#include "BufferArea.h"


#include<android/log.h>


#ifndef LOG_TAG
#define LOG_TAG "HELLO_JNI"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG ,__VA_ARGS__) // 定义LOGI类型
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,LOG_TAG ,__VA_ARGS__) // 定义LOGW类型
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG ,__VA_ARGS__) // 定义LOGE类型
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,LOG_TAG ,__VA_ARGS__) // 定义LOGF类型
#endif
BufferArea::BufferArea() {
    pthread_mutex_init(&mutex, 0); // 动态初始化互斥锁
    pthread_cond_init(&cond, 0);
}

BufferArea::~BufferArea() {
    pthread_mutex_destroy(&mutex);
    pthread_cond_destroy(&cond);
}

void BufferArea::set(){
    pthread_mutex_lock(&mutex); // 先锁起来
    if(currNum<maxNum){
        currNum++;
        //System.out.println(Thread.currentThread().getName()+" 生产了一件产品！当前产品数为："+currNum);
        LOGD(" 生产了一件产品！当前产品数为：%d",currNum);
        pthread_cond_signal(&cond);
        //notifyAll();
    }else{//当前产品数大于仓库的最大容量
        LOGD(" 开始等待！当前仓库已满，产品数为：%d",currNum);
        pthread_cond_wait(&cond, &mutex);
//        wait();
    }
    pthread_mutex_unlock(&mutex); // 解锁
}

void BufferArea::get(){
    if(currNum>0){//仓库中有产品
        currNum--;
        LOGD(" 获得了一件产品！当前产品数为：%d",currNum);
//        System.out.println(Thread.currentThread().getName()+" 获得了一件产品！当前产品数为："+currNum);
//        notifyAll();
        pthread_cond_signal(&cond);
    }else{
        LOGD(" 开始等待！当前仓库为空，产品数为：%d",currNum);
        pthread_cond_wait(&cond, &mutex);
//        try {
//            System.out.println(Thread.currentThread().getName()+" 开始等待！当前仓库为空，产品数为："+currNum);
//            wait();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}

void BufferArea::testlog() {
    LOGE("打开音频编码器失败");
}





