package com.example.myapplication;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
public class MysThread extends Thread{
    Handler mHandler;
    public MysThread(@NonNull String name) {
        super(name);

    }

    @Override
    public void run() {
        mHandler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                Log.d("MysThread", "handleMessage: "+Thread.currentThread());
            }
        };
        Message message = new Message(); //或Message message = handler.obtainMessage();这是也可以用message.sendToTarget();发送消息
        message.arg1=66;
        message.arg2=33;
        message.obj="cx";
        mHandler.sendMessage(message);
    }
}
