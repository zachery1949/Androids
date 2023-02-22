package com.example.myapplication;


import android.util.Log;

public class Student {
    static final String TAG = Student.class.getSimpleName();
    public void StudentHelloWorld(String s){
        Log.d(TAG, "StudentHelloWorld: "+s);
    }
}
