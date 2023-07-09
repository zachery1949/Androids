//
// Created by zachery on 2023/7/9.
//

#ifndef DERRY_PUSH_SAFE_QUEUE_S_H
#define DERRY_PUSH_SAFE_QUEUE_S_H

#include <queue>
#include <pthread.h>

using namespace std;

template<typename T>
class SafeQueueS {
    typedef void(*ReleaseCallback)(T *t);

public:
    SafeQueueS() {
        pthread_mutex_init(&mutex, 0);
        pthread_cond_init(&cond, 0);
    }

    ~SafeQueueS() {
        pthread_mutex_destroy(&mutex);
        pthread_cond_destroy(&cond);
    }

    void push(T t) {
        pthread_mutex_lock(&mutex);
        //如果队列满了，则wait等待pop的通知
        //如果队列未满，则push
        if (work) {
            q.push(t);
            pthread_cond_signal(&cond);
        } else {
            releaseCallback(&t);
        }
        pthread_mutex_unlock(&mutex);
    }

    int pop(T &t) {
        int ret = 0;
        pthread_mutex_lock(&mutex);
        //如果队列空了，则wait等待push的通知
        if (!work || q.size() == 0) {
            pthread_cond_wait(&cond, &mutex);
        } else {
            t = q.front();
            q.pop();
            ret = 1;
        }
        //如果队列非空，则pop
        pthread_mutex_unlock(&mutex);
        return ret;
    }

    void registReleaseCallback(ReleaseCallback callback) {
        releaseCallback = callback;
    }

    void setWork(bool work) {
        pthread_mutex_lock(&mutex);
        this->work = work;
        pthread_mutex_unlock(&mutex);
    }

private:
    ReleaseCallback releaseCallback;
    pthread_mutex_t mutex;
    pthread_cond_t cond;
    queue<T> q;
    bool work = 0;
};

#endif //DERRY_PUSH_SAFE_QUEUE_S_H
