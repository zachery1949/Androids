package com.example.rtspplay.rtsp_player_can.MediaCodec;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.example.rtspplay.R;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

//mediacodec example
public class MainActivity extends AppCompatActivity {
    private SurfaceView mSurfaceView;
    SurfaceHolder mSurfaceHolder;
    private WorkThread mWorkThread = null;
    private final static String TAG = MainActivity.class.getSimpleName();
    private String[] permiss = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE","android.permission.MANAGE_EXTERNAL_STORAGE"};


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        checkPermiss();
        // 绑定View
        mSurfaceView = findViewById(R.id.sv_player);
        // 获取Holder
        mSurfaceHolder = mSurfaceView.getHolder();
        // 设置屏幕常量
        mSurfaceHolder.setKeepScreenOn(true);
        // 设置SurfaceView回调
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                // SurfaceView 创建
                if(mWorkThread==null){
                    mWorkThread = new WorkThread(holder.getSurface());
                    mWorkThread.start();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                // SurfaceView 改变
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // SurfaceView 销毁
                if(mWorkThread!=null){
                    mWorkThread.interrupt();
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void checkPermiss() {
        int code = ActivityCompat.checkSelfPermission(this, permiss[2]);
        if (code != PackageManager.PERMISSION_GRANTED) {
            // 没有写的权限，去申请写的权限
            ActivityCompat.requestPermissions(this, permiss, 11);
        }
    }

    private class WorkThread extends Thread {
        private MediaCodec mMediaCodec;
        private MediaExtractor mMediaExtractor;
        private Surface mSurface;
        public WorkThread(Surface surface){
            this.mSurface = surface;
        }

        @Override
        public void run() {
            File file = new File(Environment.getExternalStorageDirectory(),"/night.mp4");//本地文件只能播放mp4
            if(!file.exists()){
                Log.d("TAG", "surfaceCreated123,not file.exists(): "+file.getPath());
                return;
            }
            String videoPath = file.getPath();
            mMediaExtractor = new MediaExtractor();
            try {
                mMediaExtractor.setDataSource(videoPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            for (int i = 0; i < mMediaExtractor.getTrackCount(); i++) {
                MediaFormat format = mMediaExtractor.getTrackFormat(i);
                Log.d(TAG, "decodeH265Video, format i:"+i + ":"+format);
                String mine = format.getString(MediaFormat.KEY_MIME);
                Log.d(TAG, "decodeH265Video, mine i:"+i + ":"+mine);
                if(mine.startsWith("video/")){
                    mMediaExtractor.selectTrack(i);
                    try {
                        mMediaCodec = MediaCodec.createDecoderByType(mine);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    mMediaCodec.configure(format, mSurface, null, 0);
                    break;
                }
            }
            if(mMediaCodec == null){
                Log.e(TAG,"NULL mMediaCodec");
                return;
            }
            mMediaCodec.start();
            ByteBuffer[] inputBuffers = mMediaCodec.getInputBuffers();
            ByteBuffer[] outputBuffers = mMediaCodec.getOutputBuffers();
            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            Boolean isEos = false;
            long startMs = System.currentTimeMillis();
            while (!Thread.interrupted()) {
                if(!isEos){
                    // 查询10000毫秒后，如果dSP芯片的buffer全部被占用，返回-1；存在则大于0
                    int inIndex = mMediaCodec.dequeueInputBuffer(10000);
                    if (inIndex >= 0) {
                        //根据返回的index拿到可以用的buffer
                        ByteBuffer byteBuffer = inputBuffers[inIndex];
                        int sampleSize = mMediaExtractor.readSampleData(byteBuffer, 0);

                        if (sampleSize < 0) {
                            mMediaCodec.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                            isEos = true;
                        } else {
                            long presentationTimeUs = mMediaExtractor.getSampleTime();
                            mMediaCodec.queueInputBuffer(inIndex, 0, sampleSize, presentationTimeUs, 0);
                            mMediaExtractor.advance();
                        }
                    } else {
                        //等待查询空的buffer
                        continue;
                    }
                    //mediaCodec 查询 "mediaCodec的输出方队列"得到索引
                    int outIndex = mMediaCodec.dequeueOutputBuffer(info, 10000);
                    Log.e(TAG, "outIndex " + outIndex);
                    switch (outIndex){
                        case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                            outputBuffers = mMediaCodec.getOutputBuffers();
                            break;
                        case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                                break;
                        case MediaCodec.INFO_TRY_AGAIN_LATER:
                            break;
                        default:
                            ByteBuffer buffer = outputBuffers[outIndex];
                            while(info.presentationTimeUs / 100 > System.currentTimeMillis() - startMs){
                                try {
                                    sleep(10);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    break;
                                }
                            }
                            mMediaCodec.releaseOutputBuffer(outIndex,true);
                            break;
                    }
                    if((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM)!=0){
                        Log.d(TAG,"BUFFER_FLAG_END_OF_STREAM");
                        break;
                    }
                }


            }
            mMediaCodec.stop();
            mMediaCodec.release();
            mMediaExtractor.release();
        }
    }
}
