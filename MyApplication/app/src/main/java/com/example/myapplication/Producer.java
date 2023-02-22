package com.example.myapplication;

/**
 * 生产者类
 * 实现runnable接口
 * @author DH
 *
 */
public class Producer implements Runnable{

    private BufferArea ba;

    //通过传入参数的方式是使得对象相同，具有互斥锁的效果。
    public Producer(BufferArea ba){
        this.ba = ba;
    }

    @Override
    public void run() {
        while(true){
            setIntervalTime();
            ba.set();//生产产品
        }
    }

    //设置时间间隔
    public void setIntervalTime(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

