package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

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

//        processView(this);
//        tvShow.setText("hello zhujie");
        NDKtools ndKtools = new NDKtools();
        ndKtools.helloJniCanshu("ceshicanshu");
        Student student = new Student();
        //ndKtools.JniCalljava(student);
        ndKtools.JniConsumer();
        //Log.d("TAG", "onCreate: "+ndKtools.helloJniCanshu("ceshi"););
    }



}