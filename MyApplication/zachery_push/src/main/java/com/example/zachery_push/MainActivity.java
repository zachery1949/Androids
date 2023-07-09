package com.example.zachery_push;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();

    VideoChanel videoChanel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SurfaceView surfaceView = findViewById(R.id.sv_preview);
        videoChanel = new VideoChanel(Camera.CameraInfo.CAMERA_FACING_FRONT,this,surfaceView.getHolder());
    }

    public void startLive(View view) {
        videoChanel.native_start("rtmp://192.168.0.104:2935/live/livestream");
    }
}