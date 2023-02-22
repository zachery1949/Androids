//
// Created by BigBrother on 2023/2/22.
//

#ifndef MY_APPLICATION_BUFFERAREA_H
#define MY_APPLICATION_BUFFERAREA_H
#include <pthread.h>
#include <unistd.h>
class BufferArea {

public:
    BufferArea();
    ~BufferArea();
    void testlog();
    void set();
    void get();
private:
    int currNum = 0;//当前仓库的产品数量
    int maxNum = 10;//仓库最大产品容量
    pthread_mutex_t mutex;
    pthread_cond_t cond;


};
#endif //MY_APPLICATION_BUFFERAREA_H
