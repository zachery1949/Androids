package com.example.myapplication.application;

import android.app.Application;
import android.util.Log;

import com.example.myapplication.SampleFirstStartup;
import com.example.myapplication.SampleSecondStartup;
import com.rousetime.android_startup.StartupManager;

public class MyApplication extends Application {
    final static String TAG = MyApplication.class.getSimpleName();
    public MyApplication(){
//        Debug.startMethodTracing("enjoy");
//        Debug.stopMethodTracing();
        new StartupManager.Builder()
                .addStartup(new SampleFirstStartup())
                .addStartup(new SampleSecondStartup())
//                .addStartup(SampleThirdStartup())
//                .addStartup(SampleFourthStartup())
                .build(this)
                .start()
                .await();
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: "+System.currentTimeMillis());
    }
}
