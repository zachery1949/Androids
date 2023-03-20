package com.example.myapplication.aidlStudy;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Stub extends Binder implements ILanceIInterface{
    @Override
    public String testData(String s) {
        Log.d("TAG", "testData: "+s);
        String s1 = new String("result:123456");
        return s1;
    }

    @Override
    public IBinder asBinder() {
        return this;
    }

    @Override
    protected boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {
        switch (code){
            case 100:
                String s = data.readString();
                String r = testData(s);
                reply.writeString(r);
                return true;
            default:
                return super.onTransact(code, data, reply, flags);
        }
    }
}
