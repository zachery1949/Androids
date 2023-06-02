package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Camera2Activity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CAMERA = 0;
    private String mCameraId;
    /**预览窗口*/
    private SurfaceView mSurfaceView;
    /**预览窗口Holder*/
    private SurfaceHolder mSurfaceHolder;
    /**子线程Handler*/
    private Handler mChildHandler;
    /**主线程Handler*/
    private Handler mMainHandler;
    /**照相机ID，标识前置，后置*/
//    private String mCameraId;
    /**图片读取器*/
    private ImageReader mImageReader;
    /**摄像头管理者*/
    private CameraManager mCameraManager;
    /**照相机设备*/
    private CameraDevice mCameraDevice;
    /**照相会话*/
    private CameraCaptureSession mCameraCaptureSession;
    /**方向列表*/
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
//    private Size[] supportedSizes;
//    private HandlerThread mBackgroundThread;
//    private Handler mBackgroundHandler;
//    private ImageReader mImageReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            initView();
//            startCamera();
        } else {
            requestCameraPermission();
        }
    }

    /**
     * 初始化View
     */
    private void initView() {
        // 绑定View
        mSurfaceView = findViewById(R.id.sv_camera);
        // 获取Holder
        mSurfaceHolder = mSurfaceView.getHolder();
        // 设置屏幕常量
        mSurfaceHolder.setKeepScreenOn(true);
        // 设置SurfaceView回调
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                // SurfaceView 创建
                initCamera();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                // SurfaceView 改变
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // SurfaceView 销毁
                // 销毁照相机设备
                if (null != mCameraDevice) {
                    mCameraDevice.close();
                    mCameraDevice = null;
                }
            }
        });
    }
    /**
     * 初始化照相机
     */
    private void initCamera() {
        // 创建Handler线程并启动
        HandlerThread handlerThread = new HandlerThread("Camera");
        handlerThread.start();
        // 创建子线程Handler
        mChildHandler = new Handler(handlerThread.getLooper());
        // 创建主线程Handler
        mMainHandler = new Handler(Looper.getMainLooper());
        // 设置后置摄像头ID
        mCameraId = String.valueOf(CameraCharacteristics.LENS_FACING_FRONT);
        // 创建图片读取器
        mImageReader = ImageReader.newInstance(1080, 1920, ImageFormat.JPEG, 1);
        // 图片读取器设置图片可用监听
        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                showImage(reader);
            }
        }, mMainHandler);
        // 获取摄像头管理
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        // 打开摄像头
        try {
            if (ActivityCompat.checkSelfPermission
                    (this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                // 申请权限
//                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_CODE);
            } else {
                // 打开摄像头
                mCameraManager.openCamera(mCameraId, mStateCallback, mMainHandler);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    /**
     * 摄像头状态监听
     */
    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            // 打开摄像头
            mCameraDevice = camera;
            // 开启预览
            takePreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            // 关闭摄像头
            if (null != mCameraDevice) {
                // 关闭摄像头
                mCameraDevice.close();
                mCameraDevice = null;
            }
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            // 摄像头异常
            Toast.makeText(Camera2Activity.this, "摄像头开启失败", Toast.LENGTH_SHORT).show();
        }
    };
    /**
     * 预览
     */
    private void takePreview() {
        try {
            // 创建预览需要的CaptureRequest.Builder
            final CaptureRequest.Builder builder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            // 将SurfaceView的surface作为Builder的目标
            builder.addTarget(mSurfaceHolder.getSurface());
            builder.addTarget(mImageReader.getSurface());
            // 创建CameraCaptureSession,该对象负责管理处理预览请求和拍照请求
            mCameraDevice.createCaptureSession(Arrays.asList(mSurfaceHolder.getSurface(), mImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    // 检测设备是否为空
                    if (null == mCameraDevice) return;
                    // 配置
                    // 当摄像头已经准备好时，开始显示预览
                    mCameraCaptureSession = session;
                    try {
                        // 自动对焦
                        builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        // 打开闪光灯
                        builder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                        // 显示预览
                        CaptureRequest request = builder.build();
                        // 会话设置重复请求
                        mCameraCaptureSession.setRepeatingRequest(request, null, mChildHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Toast.makeText(Camera2Activity.this, "配置失败", Toast.LENGTH_SHORT).show();
                }
            }, mChildHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    /**
     * 图片可用后，读取并显示图片
     * @param reader 图片读取器
     */
    private void showImage(ImageReader reader) {
        // 拿到图片数据
        Image image = reader.acquireNextImage();
        // 获取字节缓冲
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        // 创建与缓冲区相同的字节数组
        byte[] bytes = new byte[buffer.remaining()];
        // 将数据读取字节数组
        buffer.get(bytes);
        String tmp = new String(bytes);
        Log.d("TAG", "showImage: "+tmp);
        image.close();
//        // 创建图片
//        final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//        // 显示图片
//        if (null != bitmap) {
//            mIvShow.setImageBitmap(bitmap);
//        }
    }
//    private void startCamera() {
//        startBackgroundThread();
//        mImageReader = ImageReader.newInstance(500,500, ImageFormat.YUV_420_888,3);
//        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
//            @Override
//            public void onImageAvailable(ImageReader reader) {
//                Log.d("TAG", "onImageAvailable: ");
//            }
//        },mBackgroundHandler);
//        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
//        try {
//            String[] cameraIdList = cameraManager.getCameraIdList();
//            String backCameraId = null;
//            for (String cameraId : cameraIdList) {
//                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
//                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
//                    mCameraId = cameraId;
////                    supportedSizes = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageReader.class);
//                    break;
//                }
//            }
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
//            cameraManager.openCamera(mCameraId, stateCallback, mBackgroundHandler);
//        } catch (CameraAccessException e) {
//            throw new RuntimeException(e);
//        }
//    }
//    private void startBackgroundThread() {
//        mBackgroundThread = new HandlerThread("CameraBackground");
//        mBackgroundThread.start();
//        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
//    }
//
//    private void stopBackgroundThread() {
//        mBackgroundThread.quitSafely();
//        try {
//            mBackgroundThread.join();
//            mBackgroundThread = null;
//            mBackgroundHandler = null;
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//    private CameraDevice mCameraDevice;
//    private CaptureRequest.Builder mPreviewBuilder;
//    CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
//        @Override
//        public void onOpened(@NonNull CameraDevice cameraDevice) {
//            mCameraDevice = cameraDevice;
//            //  回调函数的代码在子线程中执行，所以不能直接发出Toast消息，只能通过主线程发出
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(getBaseContext(), "Camera opened", Toast.LENGTH_SHORT).show();
//                }
//            });
//            try {
//                //    创建Surface列表
//                List<Surface> surfaceList = new ArrayList<Surface>();
//                surfaceList.add(mImageReader.getSurface());
//                mPreviewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
//                mPreviewBuilder.addTarget(mImageReader.getSurface());
//                cameraDevice.createCaptureSession(surfaceList, new CameraCaptureSession.StateCallback() {
//                    @Override
//                    public void onConfigured(@NonNull CameraCaptureSession session) {
//                        CaptureRequest request = mPreviewBuilder.build();
//                        //
//                        //  创建处理结果的回调函数（监听器）
//                        CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback(){};
//                        //  发送重复请求，让相机不断向预览组件发送图像数据
//                        try {
//                            session.setRepeatingRequest(request, captureCallback, mBackgroundHandler);
//                        } catch (CameraAccessException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//
//                    @Override
//                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
//
//                    }
//                },mBackgroundHandler);
//            } catch (CameraAccessException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        @Override
//        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
//            mCameraDevice = null;
//        }
//
//        @Override
//        public void onError(@NonNull CameraDevice cameraDevice, int i) {
//            cameraDevice.close();
//            mCameraDevice = null;
//        }
//
//    };


    private void requestCameraPermission() {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                startCamera();
            } else {

            }
        }
    }

}