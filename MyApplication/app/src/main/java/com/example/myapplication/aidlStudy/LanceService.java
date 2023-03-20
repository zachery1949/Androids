package com.example.myapplication.aidlStudy;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class LanceService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Stub();
    }
}
