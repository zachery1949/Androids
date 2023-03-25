package com.example.myapplication.aidlStudy;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

public class Proxy implements ILanceIInterface{
    IBinder mService;
    public Proxy(IBinder service) {
        mService = service;
    }
    @Override
    public String testData(String s){
        String result = null;
        Parcel inData = Parcel.obtain();
        Parcel outData = Parcel.obtain();
        inData.writeString(s);
        try {
            mService.transact(100,inData,outData,0);
            result = outData.readString();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }finally {
            inData.recycle();
            outData.recycle();
        }
        return result;
    }

    @Override
    public IBinder asBinder() {
        return mService;
    }
}
