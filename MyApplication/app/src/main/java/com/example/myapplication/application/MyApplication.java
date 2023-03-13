package com.example.myapplication.application;

import android.app.Application;
import android.util.Log;

public class MyApplication extends Application {
    final static String TAG = MyApplication.class.getSimpleName();
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: "+System.currentTimeMillis());
    }
}
