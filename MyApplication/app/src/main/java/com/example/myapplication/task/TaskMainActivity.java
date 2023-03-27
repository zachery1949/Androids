package com.example.myapplication.task;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.myapplication.R;

public class TaskMainActivity extends AppCompatActivity {
    final static String TAG = TaskMainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_main);
    }
    public void myClick(View view){
        Log.d(TAG, "myClick: ");
        Intent intent = new Intent(TaskMainActivity.this,StandardActivity.class);
        startActivity(intent);
    }
}