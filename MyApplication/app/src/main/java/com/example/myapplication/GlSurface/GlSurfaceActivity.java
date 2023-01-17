package com.example.myapplication.GlSurface;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;

/**
 * https://blog.csdn.net/weixin_42504327/article/details/117775243
 */
public class GlSurfaceActivity extends AppCompatActivity {
    private boolean supportsEs2 = false;

    private GLSurfaceView glSurfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_gl_surface);
        checkSupported();

        if (supportsEs2) {
            Toast.makeText(this, "支持OpenGL ES 2.0!", Toast.LENGTH_SHORT).show();

            glSurfaceView = new GLSurfaceView(this);
//            glSurfaceView.setRenderer(new GLRenderer());
            glSurfaceView.setRenderer(new PolygonRenderer());

            setContentView(glSurfaceView);

        } else {

            setContentView(R.layout.activity_gl_surface);

            Toast.makeText(this, "当前设备不支持OpenGL ES 2.0!", Toast.LENGTH_SHORT).show();

        }
    }
    @Override
    protected void onPause() {

        super.onPause();

        if (glSurfaceView != null) {

            glSurfaceView.onPause();

        }

    }

    @Override
    protected void onResume() {

        super.onResume();

        if (glSurfaceView != null) {

            glSurfaceView.onResume();

        }

    }
    public void checkSupported(){
        final ActivityManager activityManager=(ActivityManager)getSystemService(ACTIVITY_SERVICE);

        final ConfigurationInfo configurationInfo=activityManager.getDeviceConfigurationInfo();

        supportsEs2=configurationInfo.reqGlEsVersion>=0x2000;
    }
}