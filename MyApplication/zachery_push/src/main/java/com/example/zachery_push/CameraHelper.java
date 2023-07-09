package com.example.zachery_push;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.util.Iterator;
import java.util.List;

public class CameraHelper {
    private final static String TAG = CameraHelper.class.getSimpleName();
    private int mWidth;
    private int mHeight;
    private byte[] buffer;
    private int mCameraId;
    private int mRotation;
    //SurfaceView surfaceView;
    Camera mCamera;
    private Activity mActivity;
    SurfaceHolder mSurfaceHolder;



    Camera.PreviewCallback mPreviewCallback;
    private OnChangedSizeListener mOnChangedSizeListener;

    public void setPreviewCallback(Camera.PreviewCallback previewCallback) {
        mPreviewCallback = previewCallback;
    }
    public CameraHelper(int cameraId, Activity activity,SurfaceHolder surfaceHolder) {
        mCameraId = cameraId;
        mActivity = activity;
        mSurfaceHolder = surfaceHolder;
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
                stopPreview();
                startPreview();
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

            }
        });
    }

    private void startPreview() {
        try {
            // 获得camera对象
            mCamera = Camera.open(mCameraId);
            // 配置camera的属性
            Camera.Parameters parameters = mCamera.getParameters();
            // 设置预览数据格式为nv21
            parameters.setPreviewFormat(ImageFormat.NV21); // yuv420类型的子集
            // 这是摄像头宽、高
            setPreviewSize(parameters);
            // 设置摄像头 图像传感器的角度、方向
            setPreviewOrientation(parameters);
            mCamera.setParameters(parameters);
            buffer = new byte[mWidth * mHeight * 3 / 2]; // 请看什么的细节
            // 数据缓存区
            mCamera.addCallbackBuffer(buffer);
            mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    camera.addCallbackBuffer(data);
                    if (mPreviewCallback != null) {
                        mPreviewCallback.onPreviewFrame(data, camera);
                    }
                }
            });
            // 设置预览画面
            mCamera.setPreviewDisplay(mSurfaceHolder); // SurfaceView 和 Camera绑定
            if (mOnChangedSizeListener != null) { // 你的宽和高发生改变，就会回调此接口
                mOnChangedSizeListener.onChanged(mWidth, mHeight);
            }
            // 开启预览
            mCamera.startPreview();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    /**
     * 停止预览
     */
    public void stopPreview() {
        if (mCamera != null) {
            // 预览数据回调接口
            mCamera.setPreviewCallback(null);
            // 停止预览
            mCamera.stopPreview();
            // 释放摄像头
            mCamera.release();
            mCamera = null;
        }
    }
    /**
     * 在设置宽和高的同时，能够打印 支持的分辨率
     * @param parameters
     */
    private void setPreviewSize(Camera.Parameters parameters) {
        // 获取摄像头支持的宽、高
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        Camera.Size size = supportedPreviewSizes.get(0);
        Log.d(TAG, "Camera支持: " + size.width + "x" + size.height);
        // 选择一个与设置的差距最小的支持分辨率
        int m = Math.abs(size.height * size.width - mWidth * mHeight);
        supportedPreviewSizes.remove(0);
        Iterator<Camera.Size> iterator = supportedPreviewSizes.iterator();
        // 遍历
        while (iterator.hasNext()) {
            Camera.Size next = iterator.next();
            Log.d(TAG, "支持 " + next.width + "x" + next.height);
            int n = Math.abs(next.height * next.width - mWidth * mHeight);
            if (n < m) {
                m = n;
                size = next;
            }
        }
        mWidth = size.width;
        mHeight = size.height;
        parameters.setPreviewSize(mWidth, mHeight);
        Log.d(TAG, "预览分辨率 width:" + size.width + " height:" + size.height);
    }

    /**
     * 旋转画面角度（因为默认预览是歪的，所以就需要旋转画面角度）
     * 这个只是画面的旋转，但是数据不会旋转，你还需要额外处理
     * @param parameters
     */
    private void setPreviewOrientation(Camera.Parameters parameters) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraId, info);
        mRotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (mRotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90: // 横屏 左边是头部(home键在右边)
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:// 横屏 头部在右边
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        // 设置角度, 参考源码注释，从源码里面copy出来的，Google给出旋转的解释
        mCamera.setDisplayOrientation(result);
    }

    public void setOnChangedSizeListener(OnChangedSizeListener listener) {
        mOnChangedSizeListener = listener;
    }

    public interface OnChangedSizeListener {
        void onChanged(int width, int height);
    }
}
