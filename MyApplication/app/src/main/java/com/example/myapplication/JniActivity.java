package com.example.myapplication;

import androidx.annotation.LongDef;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class JniActivity extends AppCompatActivity {
//    @BindView(R.id.tv_zhu)
//    TextView tvShow;
//    @Retention(RetentionPolicy.RUNTIME)
//    @Target({ElementType.FIELD, ElementType.METHOD})
//    public @interface BindView {
//        int value();
//    }
    /**
     * 反射处理findViewById
     */
//    public static void processView(Activity activity) {
//        // 获取Activity中所有的字段
//        Field[] fields = activity.getClass().getDeclaredFields();
//        if (fields == null) {
//            return;
//        }
//        // 遍历字段数组，找到带有BindView注解的字段
//        for (Field field : fields) {
//            BindView bindView = field.getAnnotation(BindView.class);
//            if (bindView == null) {
//                continue;
//            }
//            // 获取注解值——即ViewID
//            int value = bindView.value();
//            // 通过ViewID找到View
//            View viewById = activity.findViewById(value);
//            try {
//                // 给字段View赋值
//                field.set(activity, viewById);
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        }
//    }
final static String TAG = JniActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jni);
        try{
            Log.d(TAG, "Hello World");
        }catch(Exception e){
            Log.d(TAG, "echo");
        }
        finally{
            Log.d(TAG, "Hello");
        }
        findViewById(R.id.tv_zhu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //用于rxjava测试
                rxjavaTest rxjavat = new rxjavaTest();
//                rxjavat.rxjavaTestNormal();
//                rxjavat.rxjavaNormalAndSend();
//                rxjavat.rxjavaNormalAndJustsend();
//                rxjavat.rxjavaNormalAndJustsendAndfilter();
                rxjavat.rxjavaNormalThread();
                //用于rxjava测试 END

//                        new Thread(){
//            @Override
//            public void run() {
//                File patchFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/apkPatch/patch");
//                String oldApkPath = UpdateUtil.getSelfApkPath(getApplicationContext());
//                File oldApk = new File(oldApkPath);
//                File newApk = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/apkPatch/new.apk");
//                if(!patchFile.exists() || !oldApk.exists()){
//                    Log.e(TAG, "onCreate: path is not exit");
//                    return;
//                }
//                Log.d(TAG, "run: file is OK");
//                ndKtools.patchAPK(oldApk.getAbsolutePath(),newApk.getAbsolutePath(), patchFile.getAbsolutePath());
//            }
//        }.start();
            }
        });
//        processView(this);
//        tvShow.setText("hello zhujie");
        ndKtools = new NDKtools();
        ndKtools.helloJniCanshu("ceshicanshu");
        Student student = new Student();
//        ndKtools.JniCalljava(student);
//        ndKtools.JniConsumer();
//        UpdateUtil util = new UpdateUtil(getApplicationContext());
        



        //Log.d("TAG", "onCreate: "+ndKtools.helloJniCanshu("ceshi"););
    }
    NDKtools ndKtools;


}