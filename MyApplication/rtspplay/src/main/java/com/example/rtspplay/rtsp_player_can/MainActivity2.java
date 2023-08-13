package com.example.rtspplay.rtsp_player_can;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.rtspplay.R;

import java.io.File;

//VideoView + MediaController播放mp4
//https://blog.csdn.net/weixin_42182599/article/details/124882207
public class MainActivity2 extends AppCompatActivity {
    private VideoView videoView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        videoView = findViewById(R.id.videoView);
        initVideoPath();
//        // 设置RTSP流的URL
//        //"rtsp://10.2.11.68:8555/test.264"
//        String rtspUrl = "rtsp://10.2.11.68:8555/test.264";
////        String rtspUrl = "rtsp://10.2.11.68:8555/test123.265";
//
//        // 设置VideoView的MediaController
//        MediaController mediaController = new MediaController(this);
//        videoView.setMediaController(mediaController);
//
//        // 设置VideoView的URI为RTSP流的URL
//        videoView.setVideoURI(Uri.parse(rtspUrl));
//
//        // 开始播放
//        videoView.start();
    }

    private void initVideoPath(){
        // 加载指定的视频文件
        File file = new File(Environment.getExternalStorageDirectory(),"/testfile.mp4");//本地文件只能播放mp4
        videoView.setVideoPath(file.getPath());//指定视频文件的路径
//        Uri uri = Uri.parse("https://img.qunliao.info/4oEGX68t_9505974551.mp4");
////        Uri uri = Uri.parse("rtsp://192.168.0.101:554/test.264");//"rtsp://192.168.0.101:554/testfile.265"//rtsp流只能播放264，不能播放265
//        videoView.setVideoURI(uri);
        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);//VideoView和MediaController关联
        videoView.requestFocus(); //获取焦点

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.i("通知","完成");
                Toast.makeText(MainActivity2.this,"播放完成",Toast.LENGTH_SHORT).show();
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.i("通知","播放就绪");
                mp.setLooping(true);
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.i("通知","播放中出现错误");
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(videoView != null ){
            videoView.suspend();
        }
    }
}
