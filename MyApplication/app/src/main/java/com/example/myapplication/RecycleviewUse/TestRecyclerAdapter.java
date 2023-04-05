package com.example.myapplication.RecycleviewUse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;

public class TestRecyclerAdapter extends RecyclerView.Adapter<TestRecyclerAdapter.MyHolder> {
    private View mView;
    private ArrayList<String> mNameList,mNewsList;
    public TestRecyclerAdapter(Context mContext,ArrayList nameList,ArrayList newsList) {
        this.mContext = mContext;
        mNameList = nameList;
        mNewsList = newsList;
    }

    private Context mContext;
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mView = LayoutInflater.from(mContext).inflate(R.layout.rlv_list,parent,false);
        MyHolder myHolder = new MyHolder(mView);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.name.setText(mNameList.get(position));
        holder.news.setText(mNewsList.get(position));
    }

    @Override
    public int getItemCount() {
        return mNameList.size();
    }


    public class MyHolder extends RecyclerView.ViewHolder{
        TextView name,news;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            news = itemView.findViewById(R.id.news);
        }
    }
}
