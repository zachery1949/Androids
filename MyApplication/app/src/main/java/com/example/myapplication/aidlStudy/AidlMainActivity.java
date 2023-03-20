package com.example.myapplication.aidlStudy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import com.example.myapplication.ProxyPattern.IBankAidl;
import com.example.myapplication.ProxyPattern.ProxyActivity;
import com.example.myapplication.R;

public class AidlMainActivity extends AppCompatActivity {
    final static String TAG = AidlMainActivity.class.getSimpleName();
    IBankAidl mBankBinder;
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Parcel inData = Parcel.obtain();
            Parcel outData = Parcel.obtain();
            inData.writeString("这是参数");
            try {
                service.transact(100,inData,outData,0);
                String result = outData.readString();
                Log.d(TAG, "testData return: "+result);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }finally {
                inData.recycle();
                outData.recycle();
            }
            Log.d(TAG, "onServiceConnected: 1111111111111");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("TAG", "onServiceDisconnected: ");
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl_main);
        Intent intent = new Intent("com.example.myapplication.aidlStudy.LanceService");
        intent.setPackage("com.example.myapplication");
//        intent.setClass()
        boolean result = bindService(intent,serviceConnection,BIND_AUTO_CREATE);
    }
}