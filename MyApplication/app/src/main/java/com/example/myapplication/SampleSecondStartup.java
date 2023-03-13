package com.example.myapplication;

import android.content.Context;
import android.util.Log;

import com.rousetime.android_startup.AndroidStartup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;



public class SampleSecondStartup extends AndroidStartup<Boolean> {
    final static String TAG = SampleSecondStartup.class.getSimpleName();
    @Nullable
    @Override
    public Boolean create(@NonNull Context context) {
        Log.d(TAG, "create: "+Thread.currentThread());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean callCreateOnMainThread() {
        return false;
    }

    @Override
    public boolean waitOnMainThread() {
        return true;
    }
    @Nullable
    @Override
    public List<String> dependenciesByName() {
        //SampleSecondStartup
        List<String> list = new ArrayList<>();
        list.add("com.example.myapplication.SampleFirstStartup");
        return list;
    }
}