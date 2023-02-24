package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

/**
 * android:configChanges="keyboardHidden|screenSize|orientation"，横竖屏切换不会销毁activity再创建
 * 横竖屏切换可以在onpause保存bundle和在oncreate恢复
 */
public class OrientationActivity extends AppCompatActivity {
final static String TAG = OrientationActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(null != savedInstanceState){
            int qwe = savedInstanceState.getInt("qwe");
            Log.d(TAG, "onCreate: qwe:"+qwe);
        }else{
            Log.d(TAG, "onCreate: savedInstanceState is null");
        }
        setContentView(R.layout.activity_orientation);
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged: ");
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        Bundle bundle = new Bundle();
        bundle.putInt("qwe",123);
        onSaveInstanceState(bundle);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }
}