package com.example.myapplication.task;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.myapplication.R;

public class StandardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standard);
    }
    public void mySaClick(View view){
        //Log.d(“, "myClick: ");
        Intent intent = new Intent(StandardActivity.this,StandardActivity.class);
        startActivity(intent);
    }
    public void mySaToSTAClick(View view){
        //Log.d(“, "myClick: ");
        Intent intent = new Intent(StandardActivity.this,SingleTopActivity.class);
        startActivity(intent);
    }
    public void back(View view){
        finish();
    }
}