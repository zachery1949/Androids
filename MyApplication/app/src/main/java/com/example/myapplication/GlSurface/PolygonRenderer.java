package com.example.myapplication.GlSurface;

import javax.microedition.khronos.opengles.GL10;

/**

 * 绘制多边形

 * Created by mazaiting on 2017/8/9.

 */

public class PolygonRenderer extends GLRendererBase{
    int one = 0x00010000;

    //三角形三个顶点
    private int[] triggerBuffer = new int[] {
            0, one, 0,//上顶点
            - one, -one, 0, //左下点
            one, -one, 0,}; //右下点

    //正方形的4个顶点
    private int[] quaterBuffer = new int[]{
            one,one,0,
            -one,one,0,
            one,-one,0,
            -one,-one,0};

    @Override public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        // 此函数，就是将画笔沿X轴左移1.5f个单位，Y轴保持不变，Z轴向屏幕里面移动6.0f个单位。
        //gl.glTranslatef(-1.5f, 0.0f, -6.0f);
        // 左移 1.5 单位，并移入屏幕 6.0
        gl.glTranslatef(-1.5f, 0.0f, -6.0f);// z 轴值小于-1.0f
        // 允许设置顶点
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        // 设置三角形
        gl.glVertexPointer(3, GL10.GL_FIXED, 0, bufferUtil(triggerBuffer));
        // 绘制三角形
        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);
        // 重置当前模型观察矩阵
        gl.glLoadIdentity();
        // 右移 1.5 单位，并移入屏幕 6.0
        gl.glTranslatef(1.5f, 0.0f, -6.0f);// z 轴值小于-1.0f
        // 设置四边形
        gl.glVertexPointer(3,GL10.GL_FIXED,0,bufferUtil(quaterBuffer));
        // 绘制四边形
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP,0,4);
        //取消顶点设置
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        // 重置当前的模型观察矩阵
        gl.glLoadIdentity();
    }

}
