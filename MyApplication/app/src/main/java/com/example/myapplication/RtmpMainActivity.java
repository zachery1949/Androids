package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

public class RtmpMainActivity extends AppCompatActivity {
    static {
        System.loadLibrary("native-lib");
    }
    private DerryPusher pusher; // 中转站（C++层打交道） 视频 和 音频
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtmp_main);
        String s = stringFromJNI();
        Log.d("Derry", "onCreate: s=" + s);
        SurfaceView surfaceView = findViewById(R.id.surfaceView);


        // 前置摄像头，宽，高，fps(每秒25帧)，码率/比特率：https://blog.51cto.com/u_7335580/2058648
        pusher = new DerryPusher(this, Camera.CameraInfo.CAMERA_FACING_FRONT, 640, 480, 25, 800000);
        pusher.setPreviewDisplay(surfaceView.getHolder());
    }
    public native String stringFromJNI();
    /**
     * 切换摄像头
     * @param view
     */
    public void switchCamera(View view) {
        pusher.switchCamera();
    }

    /**
     * 开始直播
     * @param view
     */
    public void startLive(View view) {
        // pusher.startLive("rtmp://139.224.136.101/myapp");

        pusher.startLive("rtmp://sendtc3a.douyu.com/live/9835435rvBddb3av?wsSecret=71a52cc241fe730a2e10d27f1c9c0899&wsTime=60c2205f&wsSeek=off&wm=0&tw=0&roirecognition=0&record=flv&origin=tct");

        // 视频 音频 所有的代码，按标准来的， ffplay
        // 斗鱼的音频怎么处理的还不知道
    }

    /**
     * 停止直播
     * @param view
     */
    public void stopLive(View view) {
        pusher.stopLive();
    }

    /**
     * 释放工作
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        pusher.release();
    }
}