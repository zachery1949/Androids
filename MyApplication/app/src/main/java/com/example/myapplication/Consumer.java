package com.example.myapplication;

/**
 * 消费者类
 * 实现runnable接口
 * @author DH
 *
 */
public class Consumer implements Runnable{
    private BufferArea ba;

    public Consumer(BufferArea ba){
        this.ba = ba;
    }

    @Override
    public void run() {
        while(true){
            setIntervalTime();
            ba.get();//消费产品
        }
    }

    //设置时间间隔
    public void setIntervalTime(){
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
