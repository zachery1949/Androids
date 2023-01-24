package com.example.myapplication;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

class MyThread extends Thread{
    Object mLock = new Object();
    public Handler handler;
    public Looper mLooper;

    public Looper getLooper() {
        return mLooper;
    }

    final static String TAG = MyThread.class.getSimpleName();
    MyThread(String name){
        super(name);
        start();
        synchronized (mLock){
            while(mLooper == null){
                try {
                    mLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void run() {
        synchronized (mLock){
            Looper.prepare();
            mLooper=Looper.myLooper();
            mLock.notifyAll();
        }

//        handler=new Handler(){
//            public void handleMessage(android.os.Message msg) {
//                System.out.println("-----------123"+Thread.currentThread());
//            };
//        };
        Looper.loop();
    }
};
