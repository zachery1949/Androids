package com.example.zachery_push;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;

public class VideoChanel {
    static {
        System.loadLibrary("native-lib");
    }
    private int mCameraId;
    private Activity mActivity;
    private SurfaceHolder mSurfaceHolder;
    public VideoChanel(int mCameraId, Activity mActivity,SurfaceHolder surfaceHolder) {
        native_init();
        this.mCameraId = mCameraId;
        this.mActivity = mActivity;
        mSurfaceHolder = surfaceHolder;
        CameraHelper cameraHelper = new CameraHelper(mCameraId,mActivity,surfaceHolder);
        cameraHelper.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                native_pushVideo(data);
            }
        });
        cameraHelper.setOnChangedSizeListener(new CameraHelper.OnChangedSizeListener() {
            @Override
            public void onChanged(int width, int height) {
                native_initVideoEncoder(width,height,25, 800000);
                //pusher.startLive("rtmp://192.168.0.104:2935/live/livestream");
            }
        });
    }
    public native void native_init(); // 初始化
    public native void native_start(String path); // 开始直播start(音频视频通用一套代码) path:rtmp推流地址
    public native void native_initVideoEncoder(int width, int height, int mFps, int bitrate); // 初始化x264编码器
    public native void native_pushVideo(byte[] data); // 相机画面的数据 byte[] 推给 C++层
}
