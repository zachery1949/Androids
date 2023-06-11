package com.zachery.consumer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.example.myapplication.ProxyPattern.IBankAidl;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bind();
        findViewById(R.id.tv_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mBankBinder.openAccount("qwe","asd");
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    IBankAidl mBankBinder;
    private void Bind(){
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mBankBinder = IBankAidl.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
//        Intent intent = new Intent("com.example.myapplication.ProxyPattern.BankService");
//        intent.setPackage("com.example.myapplication");
        Intent intent = new Intent();
        String pkg = "com.example.myapplication";//需要调用的服务端【另一个APP】的报名
        String name = "com.example.myapplication.ProxyPattern.BankService";
        ComponentName componentName = new ComponentName(pkg, name);
        intent.setComponent(componentName);
        boolean result = bindService(intent,serviceConnection,BIND_AUTO_CREATE);
        Log.d("TAG", "proxy openAccount Bind: "+result);
    }
}