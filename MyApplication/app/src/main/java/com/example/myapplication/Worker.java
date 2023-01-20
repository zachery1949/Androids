package com.example.myapplication;

import android.os.Looper;
import android.util.Log;

/**
 * https://blog.csdn.net/u013261366/article/details/123471994
 */
public class Worker implements Runnable {
    private final Object mLock = new Object();
    private Looper mLooper;
final String TAG = Worker.class.getSimpleName();
    /**
     * Creates a worker thread with the given name. The thread
     * then runs a {@link android.os.Looper}.
     * @param name A name for the new thread

     */
    public Worker(String name) {
        Thread t = new Thread(null, this, name);
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
        synchronized (mLock) {
            while (mLooper == null) {
                try {
                    Log.d(TAG, "currentThread: "+Thread.currentThread().getName() +" wait");
                    mLock.wait();
                    Log.d(TAG, "currentThread: "+Thread.currentThread().getName() +" wait finish");
                } catch (InterruptedException ex) {
                }
            }
        }
    }

    public Looper getLooper() {
        return mLooper;
    }

    public void run() {
        Log.d(TAG, "currentThread: "+Thread.currentThread().getName() +" run");
        synchronized (mLock) {
            Log.d(TAG, "currentThread: "+Thread.currentThread().getName() +" mLock");
            Looper.prepare();
            mLooper = Looper.myLooper();
            mLock.notifyAll();
        }
        Looper.loop();
    }

    public void quit() {
        mLooper.quit();
    }
}
