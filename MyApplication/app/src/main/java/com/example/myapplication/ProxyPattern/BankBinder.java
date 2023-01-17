package com.example.myapplication.ProxyPattern;

import android.os.Binder;
/*
 * 同一进程用extends Binder implements IBank
 * 跨进程用extends IBankAidl.Stub
 */
public class BankBinder extends IBankAidl.Stub {

    @Override
    public String openAccount(String name, String password) {
        return "开户成功";
    }
}
