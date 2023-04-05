package com.example.myapplication.RecycleviewUse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.example.myapplication.R;

import java.util.ArrayList;

public class MainRecycleviewActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    TestRecyclerAdapter mTestRecyclerAdapter;
    private ArrayList<String> mNameList,mNewsList;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_recycleview);
        mRecyclerView = findViewById(R.id.rlv_list);
        createData();

        mTestRecyclerAdapter = new TestRecyclerAdapter(this,mNameList,mNewsList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mTestRecyclerAdapter);
    }
    private void createData(){
        mNameList = new ArrayList<>();
        mNewsList = new ArrayList<>();
        mNameList.add("张三");
        mNewsList.add("打篮球");
        mNameList.add("李四");
        mNewsList.add("踢足球");
        mNameList.add("王二");
        mNewsList.add("赌博");
        mNameList.add("张三");
        mNewsList.add("打篮球");
        mNameList.add("李四");
        mNewsList.add("踢足球");
        mNameList.add("王二");
        mNewsList.add("赌博");
        mNameList.add("张三");
        mNewsList.add("打篮球");
        mNameList.add("李四");
        mNewsList.add("踢足球");
        mNameList.add("王二");
        mNewsList.add("赌博");
        mNameList.add("张三");
        mNewsList.add("打篮球");
        mNameList.add("李四");
        mNewsList.add("踢足球");
        mNameList.add("王二");
        mNewsList.add("赌博");
        mNameList.add("张三");
        mNewsList.add("打篮球");
        mNameList.add("李四");
        mNewsList.add("踢足球");
        mNameList.add("王二");
        mNewsList.add("赌博");
    }
}