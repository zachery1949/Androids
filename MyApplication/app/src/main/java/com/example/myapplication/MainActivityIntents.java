package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

//public class MainActivityIntents extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main_intents);
//    }
//}




import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivityIntents extends AppCompatActivity implements View.OnClickListener {

    public static final String ACTION_DOWN_LOAD_IMAGE = "action_down_load_image";
    public static final String DOWN_LOAD_IMAGE_PATH = "down_load_image_path";
    public static final String RESULT_DOWNLOAD_IMAGE = "result_download_image";
    public static final String EXTRA_DOWNLOAD_IMAGE = "extra_download_image";
    private TextView addTask;
    private int i;
    private BroadcastReceiver receiver = new DownloadBroadCast();
    private LinearLayout content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_intents);
        addTask = findViewById(R.id.add_task);
        content = findViewById(R.id.content_container);
        addTask.setOnClickListener(this);

        //注册广播
        IntentFilter filter=new IntentFilter(RESULT_DOWNLOAD_IMAGE);
        registerReceiver(receiver,filter);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.add_task) {
            // 点击按钮，添加图片下载任务
            TextView textView = new TextView(this);
            String path="/sdcard/imgs"+(++i)+".png";//模拟下载路径
            textView.setText(path + " is downloading");
            textView.setTag(path);
            content.addView(textView);
            startIntentServcieForDownload(path);
        }
    }

    private void startIntentServcieForDownload(String imagePath) {
        Intent intent = new Intent();
        intent.setClass(this,MyIntentService.class);
        intent.setAction(ACTION_DOWN_LOAD_IMAGE);
        intent.putExtra(DOWN_LOAD_IMAGE_PATH,imagePath);
        startService(intent);
    }

    public class DownloadBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(),RESULT_DOWNLOAD_IMAGE)) {
                String imageResult = intent.getStringExtra(EXTRA_DOWNLOAD_IMAGE);
                TextView textView = (TextView) content.findViewWithTag(imageResult);
                textView.setText(imageResult + "is successful download");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}