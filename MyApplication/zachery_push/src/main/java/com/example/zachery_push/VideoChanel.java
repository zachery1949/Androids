package com.example.zachery_push;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;

public class VideoChanel {
    private int mCameraId;
    private Activity mActivity;
    private SurfaceHolder mSurfaceHolder;
    public VideoChanel(int mCameraId, Activity mActivity,SurfaceHolder surfaceHolder) {
        this.mCameraId = mCameraId;
        this.mActivity = mActivity;
        mSurfaceHolder = surfaceHolder;
        CameraHelper cameraHelper = new CameraHelper(mCameraId,mActivity,surfaceHolder);
        cameraHelper.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {

            }
        });
        cameraHelper.setOnChangedSizeListener(new CameraHelper.OnChangedSizeListener() {
            @Override
            public void onChanged(int width, int height) {

            }
        });
    }
}
