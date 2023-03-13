package com.example.myapplication;

import android.content.Context;
import android.util.Log;

import com.rousetime.android_startup.AndroidStartup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SampleFirstStartup extends AndroidStartup<String> {
    final static String TAG = SampleFirstStartup.class.getSimpleName();
    @Nullable
    @Override
    public String create(@NonNull Context context) {
        //todo something
        Log.d(TAG, "create: "+Thread.currentThread());
        return this.getClass().getSimpleName();
    }

    @Override
    public boolean callCreateOnMainThread() {
        return true;
    }

    @Override
    public boolean waitOnMainThread() {
        return false;
    }

    @Nullable
    @Override
    public List<String> dependenciesByName() {
        return null;
    }
}
