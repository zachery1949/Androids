package com.example.myapplication.ProxyPattern;

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

import com.example.myapplication.R;
import com.zachary.amodule.AModuleMainActivity;
import com.zachary.bmodule.UserInstallService;
import com.zachary.common.CommonMainActivity;
import com.zachary.common.ServiceFactory;


public class ProxyActivity extends AppCompatActivity {
    /*
     * 同一进程 BankBinder extends Binder implements IBank
     * 跨进程 BankBinder extends IBankAidl.Stub
     */
//    BankBinder mBankBinder;
    final static String TAG = ProxyActivity.class.getSimpleName();
    IBankAidl mBankBinder;
    IBankAidl mBankBinder2;
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            /*
             * 同一进程 (BankBinder)service
             * 跨进程 Stub.asInterface(service)
             */
            //        mBankBinder = (BankBinder)service;
            mBankBinder = IBankAidl.Stub.asInterface(service);
            Log.d("TAG", "onServiceConnected: ");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("TAG", "onServiceDisconnected: ");
        }
    };
    ServiceConnection serviceConnection2 = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            /*
             * 同一进程 (BankBinder)service
             * 跨进程 Stub.asInterface(service)
             */
            //        mBankBinder = (BankBinder)service;
            mBankBinder2 = IBankAidl.Stub.asInterface(service);
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
        Log.d(TAG, "onCreate: ");
//        ServiceFactory.getInstance().setIUserInstallService(new UserInstallService());
        setContentView(R.layout.activity_proxy);
//        bt_qwe
        findViewById(R.id.bt_qwe).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(ProxyActivity.this, AModuleMainActivity.class);
//                startActivity(intent);
//                Log.d("TAG", "onClick123: ");
                String qwe = null;
                try {
                    Log.d("TAG", "proxy openAccount: "+Thread.currentThread().getName());
                    qwe = mBankBinder.openAccount("123","456");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                Log.d("TAG", "onClick123 qwe: "+qwe);            }
        });
        findViewById(R.id.bt_second).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.d("TAG", "proxy openAccount: "+Thread.currentThread().getName());
                    mBankBinder2.openAccount("456","789");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        Intent intent = new Intent("com.example.myapplication.ProxyPattern.BankService");
        intent.setPackage("com.example.myapplication");
//        intent.setClass()
        boolean result = bindService(intent,serviceConnection,BIND_AUTO_CREATE);
//        boolean result2 = bindService(intent,serviceConnection2,BIND_AUTO_CREATE);
        Log.d("TAG", "onCreate proxy openAccount bindService: "+result);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d(TAG, "onWindowFocusChanged: "+System.currentTimeMillis());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }
}