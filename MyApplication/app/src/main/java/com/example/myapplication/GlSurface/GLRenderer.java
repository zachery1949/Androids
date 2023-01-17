package com.example.myapplication.GlSurface;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRenderer implements GLSurfaceView.Renderer{
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig eglConfig) {
        //glClearColor函数是设置清屏的颜色，参数分别对应RGBA
        gl.glClearColor(1f, 0f, 0f, 0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //决定绘制的矩形区域的大小
        gl.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //使用glClearColor函数所设置的颜色进行清屏。
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    }
}
