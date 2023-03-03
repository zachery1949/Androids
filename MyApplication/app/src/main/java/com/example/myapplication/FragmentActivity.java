package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

public class FragmentActivity extends AppCompatActivity {
private OneFragment oneFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        oneFragment = new OneFragment();
        findViewById(R.id.bt_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if (oneFragment == null) {
                    oneFragment = new OneFragment();
                }
                transaction.replace(R.id.fragment_layout, oneFragment);
                //replace是先移除所有存在的Fragment， 然后把新的Fragment 添加进来。
                //但是Android7.0以下的replace有未移除所有的bug
                transaction.commit();
            }
        });
    }
}