package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

public class HandlerActivity extends AppCompatActivity {
    Handler handler;
    TextView textView;
    String TAG = HandlerActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler);
        textView = findViewById(R.id.tv_content);
//        MysThread mysThread = new MysThread("fenzhis");
//        mysThread.start();
        MyThread myThread = new MyThread("fenzhi");
//        Worker myThread = new Worker("fenzhi");
////        HandlerThread myThread = new HandlerThread("fenzhi");
//        //myThread.start();
//        Message message = new Message(); //或Message message = handler.obtainMessage();这是也可以用message.sendToTarget();发送消息
//        message.arg1=66;
//        message.arg2=33;
//        message.obj="cx";
//        Log.d(TAG, "out currentThread: "+Thread.currentThread().getName());
//        handler = new Handler(myThread.getLooper()){
//            @Override
//            public void handleMessage(@NonNull Message msg) {
//                Log.d(TAG, "currentThread: "+Thread.currentThread().getName());
//                textView.setText("收到消息："+msg.toString());
//            }
//        };
//        handler.sendMessage(message);
    }
}