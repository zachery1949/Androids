package com.example.myapplication.GlassManage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.myapplication.R;
import android.os.Environment;
import android.util.Log;
import dalvik.system.DexClassLoader;
import java.io.File;
import java.lang.reflect.Method;


public class MainActivityGlass extends AppCompatActivity {
    final static String TAG = MainActivityGlass.class.getSimpleName();
    private GlassLagerRelativeLayout mG40GroupView;
    private View mG40GlassView;

    //    private View mScrollView;
    private LinearLayout layoutTop;

    private Button btn1;
    private Button btn2;
    private Button btn4;

    /**
     * 动态加载，app运行之后使用Dexclassloader加载dex文件，并使用反射调用dex文件中方法
     */
    private void getOutData(){
        //        DexClassLoader dexClassLoader = new DexClassLoader("/sdcard/fix.dex", getCodeCacheDir().getAbsolutePath(), null, getClassLoader());

        File optimizadDexOutputPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"mySdktmp.jar");
        if(optimizadDexOutputPath.exists()){
            Log.d(TAG, "getOutData: optimizadDexOutputPath is exit ");
        } else {
            Log.d(TAG, "getOutData: optimizadDexOutputPath is not exit ");
            return;
        }
        File dexOutputDir = this.getDir("dex",0);
        DexClassLoader dexClassLoader = new DexClassLoader(optimizadDexOutputPath.getAbsolutePath(),dexOutputDir.getAbsolutePath(),null,ClassLoader.getSystemClassLoader());
        Class libProviderClazz = null;
        try {
            libProviderClazz = dexClassLoader.loadClass("com.zachary.mylibrary.ITest");
            //遍历所有的方法
            Method[] methods = libProviderClazz.getDeclaredMethods();
            for (int i = 0;i<methods.length;i++){
                Log.e("test",methods[i].toString());
            }
            //通过方法名获取func方法
            Method func= libProviderClazz.getDeclaredMethod("testPrint");
            //外部可以调用
            func.setAccessible(true);
            //调用该方法获得值
            func.invoke(libProviderClazz.newInstance());
            //Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_scroll);

        layoutTop = findViewById(R.id.layout_top);

        btn1 = findViewById(R.id.btn1);

        btn2 = findViewById(R.id.btn2);

        btn4 = findViewById(R.id.btn4);


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutTop.scrollTo(0, -300);
                getOutData();
//                ITest iTest = new ITest();
//                iTest.testPrint();
//                ITest iTest = new ITest();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutTop.scrollBy(0, 100);
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivityGlass.this, btn4.getText().toString() + "is clicked", Toast.LENGTH_LONG).show();
            }
        });
        //----------------------------------
//        setContentView(R.layout.g40_glass_layout);
//        mG40GroupView =  findViewById(R.id.g40view);
//        mG40GlassView = LayoutInflater.from(this).inflate(R.layout.activity_main_glass, null);
//        addG40LargePreView(mG40GlassView, 2052, 2600);
    }

    /**
     * G40大屏View添加，添加成功后隐藏下面的视图
     * @param view 添加的View
     * @param width 视图的宽度
     * @param height 视图的高度
     */
    public void addG40LargePreView(View view, int width, int height){
        if (mG40GroupView == null){
            return;
        }
        mG40GroupView.post((Runnable) () -> {
            for (int i = 0; i < mG40GroupView.getChildCount(); i++) {
                if (mG40GroupView.getChildAt(i) == view) {
                    mG40GroupView.removeView(view);
                }
                //隐藏所有VIEW展示当前AddView，避免VIEW遮挡重叠
                if (mG40GroupView.getChildAt(i) != null) {
                    mG40GroupView.getChildAt(i).setVisibility(View.GONE);
                }
            }

            if (view.getParent() != null) {
                if (view.getParent() instanceof ViewGroup) {
                    ((ViewGroup) view.getParent()).removeView(view);
                }
            }
            ViewGroup.LayoutParams viewLp = new ViewGroup.LayoutParams(width, height);
            view.setLayoutParams(viewLp);
            ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) mG40GroupView.getLayoutParams();
//            lp.width = width + 640;
//            lp.height = height + 400;
//        if (mG40GroupView.getChildCount() > 0 ){
//            View oldView = mG40GroupView.getChildAt(mG40GroupView.getChildCount() - 1);
//            oldView.setVisibility(View.GONE);
//            Log.i(TAG,"old view gone:"+oldView.hashCode() +"new view :"+view.hashCode());
//        }
            mG40GroupView.setLayoutParams(lp);
            view.setVisibility(View.VISIBLE);
            mG40GroupView.addView(view);
            RelativeLayout.LayoutParams lpView = (RelativeLayout.LayoutParams) view.getLayoutParams();
            lpView.addRule(RelativeLayout.CENTER_IN_PARENT);
            view.setLayoutParams(lpView);
            startViewShowAnimation(view);
        });
    }
    private Animation mScaleanimation;
    /**
     * 界面显示时动画
     * @param view
     */
    private synchronized void startViewShowAnimation(View view){
        if (mScaleanimation != null ){
//            mScaleanimation.cancel();
            return;
        }
        mScaleanimation = AnimationUtils.loadAnimation(this,R.anim.g40_anim);
        mScaleanimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mScaleanimation = null;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mScaleanimation.setDuration(300);
        view.startAnimation(mScaleanimation);
    }
}