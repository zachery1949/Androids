package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AudioMainActivity extends AppCompatActivity {

    private static final String FILE_NAME = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + File.separator + "test.pcm";

    private AudioRecord audioRecord = null;
    private int recordBufsize = 0;
    private boolean isRecording = false;

    private Button startRecordBtn;
    private Button stopRecordBtn;

    private Thread recordingThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 2000);
        }
        startRecordBtn = findViewById(R.id.start_record_btn);
        stopRecordBtn = findViewById(R.id.stop_record_btn);
        createAudioRecord();
        startRecordBtn.setOnClickListener(v -> {
            startRecord();
        });
        stopRecordBtn.setOnClickListener(v -> {
            stopRecord();
        });
    }

    private void createAudioRecord() {
        recordBufsize = AudioRecord
                .getMinBufferSize(44100,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);
        Log.i("audioRecordTest", "size->" + recordBufsize);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Log.e("TAG", "createAudioRecord fail: ");
            return;
        }
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                44100,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                recordBufsize);
    }

    private void startRecord() {
        if (isRecording) {
            return;
        }
        isRecording = true;
        audioRecord.startRecording();
        Log.i("audioRecordTest", "开始录音");
        recordingThread = new Thread(() -> {
            byte data[] = new byte[recordBufsize];
            File file = new File(FILE_NAME);
            FileOutputStream os = null;
            try {
                if (!file.exists()) {
                    file.createNewFile();
                    Log.i("audioRecordTest", "创建录音文件->" + FILE_NAME);
                }
                os = new FileOutputStream(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
            int read;
            if (os != null) {
                while (isRecording) {
                    read = audioRecord.read(data, 0, recordBufsize);
                    if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                        try {
                            os.write(data);
                            Log.i("audioRecordTest", "写录音数据->" + read);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        recordingThread.start();
    }

    private void stopRecord() {
        isRecording = false;
        if (audioRecord != null) {
            audioRecord.stop();
            Log.i("audioRecordTest", "停止录音");
            audioRecord.release();
            audioRecord = null;
            recordingThread = null;
        }
    }
}
