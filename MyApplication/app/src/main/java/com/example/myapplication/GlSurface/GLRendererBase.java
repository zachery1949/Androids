package com.example.myapplication.GlSurface;

//public class GLRendererBase {
//}

import android.opengl.GLSurfaceView;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**

 * 基本框架

 * https://blog.csdn.net/weixin_36070282/article/details/117775239?ops_request_misc=&request_id=&biz_id=102&utm_term=android%20opengl%20%E7%AE%80%E4%B9%A6&utm_medium=distribute.pc_search_result.none-task-blog-2~blog~sobaiduweb~default-1-117775239.nonecase&spm=1018.2226.3001.4450
 * Created by mazaiting on 2017/8/9.

 */

public class GLRendererBase implements GLSurfaceView.Renderer {

    /**

     * 在窗口被创建时被调用，需要做一些必要的初始化工作:

     */

    @Override public void onSurfaceCreated(GL10 gl, EGLConfig config) {

// 启动阴影平滑

        gl.glShadeModel(GL10.GL_SMOOTH);

// 黑色背景，设置清楚屏幕时所用的颜色取值：RGBA.0f-1.0f

        gl.glClearColor(0, 0, 0, 1.0f);

// 设置深度缓存--决定哪个物体先画

        gl.glClearDepthf(1.0f);

// 启动深度测试

        gl.glEnable(GL10.GL_DEPTH_TEST);

// 所作深度测试的类型

        gl.glDepthFunc(GL10.GL_LEQUAL);

// 告诉系统对透视进行修正

        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

    }

    /**

     * 当窗口大小发生改变时被调用，不管窗口的大小是否已经改变，在程序开始时至少运行一次。

     */

    @Override public void onSurfaceChanged(GL10 gl, int width, int height) {

        float ratio = (float) width / height;

// 设置OpenGL场景的大小

        gl.glViewport(0, 0, width, height);

// 设置投影矩阵--增加透视

        gl.glMatrixMode(GL10.GL_PROJECTION);

// 重置投影矩阵--恢复原始状态

        gl.glLoadIdentity();

// 设置视图的大小

        gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);

// 选择模型观察矩阵

        gl.glMatrixMode(GL10.GL_MODELVIEW);

// 重置模型观察矩阵

        gl.glLoadIdentity();

    }

    /**

     * 在窗口内进行绘图操作。 在绘图之前，需要将屏幕清楚成前面指定的颜色，清楚深度缓存并且重置场景

     */

    @Override public void onDrawFrame(GL10 gl) {

// 清楚屏幕和深度缓存

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

// 重置当前的模型观察矩阵

        gl.glLoadIdentity();

// 具体绘图开始...

    }

    /**

     * OpenGL 是一个非常底层的画图接口，它所使用的缓冲区存储结构是和我们的 java 程序中不相同的。

     * Java 是大端字节序(BigEdian)，而 OpenGL 所需要的数据是小端字节序(LittleEdian)。

     * 所以，我们在将 Java 的缓冲区转化为 OpenGL 可用的缓冲区时需要作一些工作。建立buff的方法如下

     **/

    public Buffer bufferUtil(int []arr){

// 先初始化buffer,数组的长度*4,因为一个int占4个字节
        ByteBuffer qbb = ByteBuffer.allocateDirect(arr.length * 4);
// 数组排列用nativeOrder
        qbb.order(ByteOrder.nativeOrder());
// 将ByteBuffer转换为IntBuffer
        IntBuffer mBuffer = qbb.asIntBuffer();
// 将数组设置进去
        mBuffer.put(arr);
//mBuffer.position(0);
// 重置
        mBuffer.flip();
        return mBuffer;
    }

}
