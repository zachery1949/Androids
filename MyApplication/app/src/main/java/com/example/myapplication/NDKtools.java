package com.example.myapplication;

public class NDKtools {
    static {
        System.loadLibrary("native-lib");
    }

    public static native String func();
}
