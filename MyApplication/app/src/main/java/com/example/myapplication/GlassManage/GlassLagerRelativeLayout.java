package com.example.myapplication.GlassManage;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * ------------------------------------------------------
 * <p>
 * Copyright (c) 2022/2/21. llvision. All rights reserved.
 * <p>
 * ------------------------------------------------------
 *
 * @Author haijianming
 * @Date Created in 2022/2/21 4:17 下午
 * @version:
 * @Modified by:
 * @Title: TODO
 * @Description:  继续G40 sensor定义的无线大屏，根据水平、上下、倾角。移动View达到头动作用，根据View移动及坐标做事件分发响应上层View的回调
 */
public class GlassLagerRelativeLayout extends RelativeLayout {
    private static String TAG = GlassLagerRelativeLayout.class.getName();
    private float first;
    /**
     * 上一次的仰角
     */
    private float glassOldlYaw;
    /**
     * 最新的水平角度
     */
    private float glassNewYaw;
    /**
     * 刚开启时的仰角度
     */
    private float firstYoll;
    /**
     * 上一次仰角度
     */
    private float oldYoll;
    /**
     * 屏幕中间坐标
     */
    private int centerX;
    /**
     * 屏幕中间坐标
     */
    private int centerY;
    /**
     * 上一次移动位置记录
     */
    private int oldX;
    /**
     * 上一次移动位置记录
     */
    private int oldY;
    /**
     * 当前屏幕宽度
     */
    private int mWidth;
    /**
     * 当前屏幕高度
     */
    private int mHeight;
    /**
     * 上下移动最大角度
     */
    private int maxPitch = 60;
    /**
     * 水平移动最大角度
     */
    private int maxRoll = 100;
    /**
     * Y轴移动参数
     */
    private int scrollYBase;
    /**
     * X轴移动参数
     */
    private int scrollXBase;
    /**
     * 真实屏幕的宽度
     */
    private int screenWidth = 640;
    /**
     * 真实屏幕的高度
     */
    private int screenHeight = 400;
    /**
     * 选中时间
     */
    private long time;
    /**
     * 选中后点击次数
     */
    private int clickCount;
    /**
     * 是否允许头动选中
     */
    private boolean enableClick;
    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    public GlassLagerRelativeLayout(Context context) {
        super(context);
        initView();
    }

    public GlassLagerRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public GlassLagerRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public GlassLagerRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr,
                                    int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        setMinimumWidth(screenWidth + screenWidth/2);
        setMinimumHeight(screenHeight * 2);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        centerX = w / 2 - screenWidth / 2;
        scrollXBase = mWidth / maxRoll;
        scrollYBase = mHeight / maxPitch;
        centerY = h / 2 - screenHeight / 2;
        scrollTo(centerX, centerY);
        first = 0;
        firstYoll = 0;
        glassOldlYaw = 0;
    }

    /**
     * 四元数值添加，这个地方已经是进过转换的参数
     * @param euler 0 yoll 1 yaw 2 roll
     */
    public synchronized void addEular(float[] euler) {
//        if (euler[2] > 30){
//            return;
//        }
        if (first == 0) {
            first = euler[1];
        }
        if (firstYoll == 0) {
            firstYoll = euler[0];
        }
        if (glassOldlYaw == 0) {
            glassOldlYaw = euler[1];
            oldYoll = euler[0];
            return;
        }
        glassNewYaw = euler[1];
        //90边境不处理
        if ((int) Math.abs(glassNewYaw) == 90){
            return;
        }
        int scrollY = getScrollY();
        int scrollX = getScrollX();
        float moveX;
        if (glassOldlYaw < 0) {
            if (euler[1] > glassOldlYaw) {
                //右移动
                moveX = -(Math.abs(glassOldlYaw) - Math.abs(euler[1]));
                if (mWidth <= scrollX + screenWidth) {
                    glassOldlYaw = glassNewYaw;
                    moveX = 0;
                }

            } else {
                moveX = Math.abs(euler[1]) - Math.abs(glassOldlYaw);
                if (scrollX <= 0) {
                    glassOldlYaw = glassNewYaw;
                    moveX = 0;
                }

            }
        } else {
            if (euler[1] > glassOldlYaw) {
                //右移动
                moveX = -(Math.abs(euler[1]) - Math.abs(glassOldlYaw));
                if (mWidth <= scrollX + screenWidth) {
                    glassOldlYaw = glassNewYaw;
                    moveX = 0;
                }

            } else {
                moveX = Math.abs(glassOldlYaw) - Math.abs(euler[1]);
                if (scrollX <= 0) {
                    glassOldlYaw = glassNewYaw;
                    moveX = 0;
                }

            }
        }
        float moveY;
        if (euler[0] < 0) {
            if (euler[0] > oldYoll) {
                //向上
                moveY = (Math.abs(oldYoll) - Math.abs(euler[0]));
                if (scrollY <= 0) {
                    moveY = 0;
                }
            } else {
                moveY = -(Math.abs(euler[0]) - Math.abs(oldYoll));
                if (scrollY + screenHeight >= mHeight) {
                    moveY = 0;
                }
            }
        } else {
            if (euler[0] > oldYoll) {
                //向上
                moveY = (Math.abs(euler[0]) - Math.abs(oldYoll));
                if (scrollY <= 0) {
                    moveY = 0;
                }
            } else {
                moveY = -(Math.abs(oldYoll) - Math.abs(euler[0]));
                if (scrollY + screenHeight >= mHeight) {
                    moveY = 0;
                }
            }
        }
        oldYoll = euler[0];
        moveY = moveY * scrollYBase;
        moveX = moveX * scrollXBase;
        if (scrollY <= 0 && moveY > 0){
            moveY = 0;
        }
        if (scrollX <= 0 && moveX > 0){
            moveX = 0;
        }
        //移动后X位置
        int afterMoveX = (int) (getScrollX() -moveX);
        if (afterMoveX < 0){
            moveX = Math.abs(getScaleX());
        }else if (afterMoveX >= (mWidth)){
            moveX = -((mWidth) - getScrollX());

        }
        int afterMoveY = (int) (scrollY - moveY);
        if (afterMoveY < 0){
            moveY = scrollY;
        }else if (afterMoveY >= (mHeight)){
            moveY = -((mHeight) - scrollY);
        }
        glassOldlYaw = glassNewYaw;
        scrollBy((int) -moveX, (int) -moveY);
        int newX = getScrollX();
        int newY = getScrollY();
        if (Math.abs(oldX - newX) <= 10 && Math.abs(newY-oldY) <= 10){
            clickCount++;
        }else {
            clickCount = 0;
        }
        if (enableClick && oldX == newX && oldY == newY &&  clickCount >= 10) {
            if (time == 0) {
                time = System.currentTimeMillis();
            }
            if (System.currentTimeMillis() - time >= 4000) {
                time = 0;
                clickCount = 0;
               mMainHandler.post(new Runnable() {
                   @Override
                   public void run() {
                       onViewClick(getScrollX(),getScrollY());
                   }
               });
            }
        }
//        Log.i(TAG,"the x:"+oldX+"movex:"+moveX +"scrollx:"+getScrollX());
        oldX = getScrollX();
        oldY = getScrollY();
    }

    /**
     * 设置头动是否选中
     * @param isEnable
     */
    public void enableClick(boolean isEnable){
        this.enableClick = isEnable;
    }
    /**
     * 分发模拟点击事件
     * @param x 当前大屏中坐标X
     * @param y 当前大屏中坐标Y
     */
   private void onViewClick(int x,int y){
       long downTime = SystemClock.uptimeMillis();
       long eventTime = SystemClock.uptimeMillis() + 100;
       MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime,
               MotionEvent.ACTION_DOWN, x, y, 0);
       long newDowTime = downTime + 1000;
       MotionEvent motionEventUp = MotionEvent.obtain(newDowTime, newDowTime,
               MotionEvent.ACTION_UP, x, y, 0);
       for (int i = 0; i < getChildCount(); i++) {
           getChildAt(i).dispatchTouchEvent(motionEvent);
           getChildAt(i).dispatchTouchEvent(motionEventUp);
       }
       motionEvent.recycle();
       motionEventUp.recycle();
   }
}
