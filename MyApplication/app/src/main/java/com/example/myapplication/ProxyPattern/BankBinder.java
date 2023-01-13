package com.example.myapplication.ProxyPattern;

import android.os.Binder;

public class BankBinder extends Binder implements IBank {

    @Override
    public String openAccount(String name, String password) {
        return "开户成功";
    }
}
