package com.example.vcamera;

public class JniUtils {
    static {
        System.loadLibrary("native-vcamera");
    }

    public native String helloJni();
    public native void liveStart();
}
