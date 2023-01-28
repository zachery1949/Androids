package com.example.myapplication;





import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import static com.example.myapplication.MainActivityIntents.ACTION_DOWN_LOAD_IMAGE;
import static com.example.myapplication.MainActivityIntents.DOWN_LOAD_IMAGE_PATH;
import static com.example.myapplication.MainActivityIntents.EXTRA_DOWNLOAD_IMAGE;
import static com.example.myapplication.MainActivityIntents.RESULT_DOWNLOAD_IMAGE;


public class MyIntentService extends IntentService {

    private static final String TAG = "MyIntentService";

    public MyIntentService() {
        super("download image");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG,"onCreate enter");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.e(TAG,"onStartCommand enter");
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * @param name
     * @deprecated
     */
    public MyIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.e(TAG,"onHandleIntent enter");
        if (intent != null) {
            if (TextUtils.equals(intent.getAction(),ACTION_DOWN_LOAD_IMAGE)) {
                String path = intent.getStringExtra(DOWN_LOAD_IMAGE_PATH);
                //根据下载路径，模拟图片的下载,在后头 service 并且开启线程来 完成图片下载任务
                downLoadWork(path);
            }
        }
    }

    private void downLoadWork(String path) {
        try {
            Log.e(TAG,"downLoadWork thread = " + Thread.currentThread().getName());
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 一般后台任务执行完毕后，怎么将 任务执行的结果给 传递出去了，就是通过广播的方式
        Intent intent=new Intent(RESULT_DOWNLOAD_IMAGE);
        intent.putExtra(EXTRA_DOWNLOAD_IMAGE,path);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"onDestroy enter");
    }
}
