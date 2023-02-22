package com.example.myapplication;

public class NDKtools {
    static {
        System.loadLibrary("native-lib");
    }

    public static native String func();
    public native String helloJni();
    public native void helloJniCanshu(String canshu);
    public native void JniCalljava(Student student);
}
