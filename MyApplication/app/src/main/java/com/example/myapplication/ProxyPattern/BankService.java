package com.example.myapplication.ProxyPattern;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class BankService extends Service {
    BankBinder mBankBinder;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if(null == mBankBinder){
            mBankBinder = new BankBinder();
        }
        return mBankBinder;
    }
}
