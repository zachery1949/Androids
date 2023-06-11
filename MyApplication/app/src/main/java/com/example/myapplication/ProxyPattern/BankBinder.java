package com.example.myapplication.ProxyPattern;

import android.os.Binder;
import android.util.Log;

/*
 * 同一进程用extends Binder implements IBank
 * 跨进程用extends IBankAidl.Stub
 */
public class BankBinder extends IBankAidl.Stub {
    private int count = 0;
    @Override
    public String openAccount(String name, String password) {
        if(name.equals("123")){
            try {
                Log.d("TAG", "openAccount: 123 sleep");
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        count++;
        Log.d("TAG", "openAccount: " + name + password+" count:"+count);
        Log.d("TAG", "openAccount: "+Thread.currentThread().getName());
        return "开户成功" + name + password;
    }
}
