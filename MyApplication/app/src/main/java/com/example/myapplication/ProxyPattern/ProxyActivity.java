package com.example.myapplication.ProxyPattern;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.example.myapplication.R;


public class ProxyActivity extends AppCompatActivity {
    BankBinder mBankBinder;
    ServiceConnection serviceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder service) {
        mBankBinder = (BankBinder)service;
        Log.d("TAG", "onServiceConnected: ");
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        Log.d("TAG", "onServiceDisconnected: ");
    }
};
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxy);
//        bt_qwe
        findViewById(R.id.bt_qwe).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "onClick123: ");
                String qwe = mBankBinder.openAccount("123","456");
                Log.d("TAG", "onClick123: "+qwe);
            }
        });
        Intent intent = new Intent("com.example.myapplication.ProxyPattern.BankService");
        intent.setPackage("com.example.myapplication");
//        intent.setClass()
        boolean result = bindService(intent,serviceConnection,BIND_AUTO_CREATE);
        Log.d("TAG", "onCreate bindService: "+result);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }
}