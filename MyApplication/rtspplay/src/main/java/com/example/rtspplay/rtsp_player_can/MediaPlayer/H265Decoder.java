package com.example.rtspplay.rtsp_player_can.MediaPlayer;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;
public class H265Decoder {
    private MediaCodec mediaCodec;
final static String TAG = H265Decoder.class.getSimpleName();
    public void decodeH265Video(String videoFilePath, Surface outputSurface) throws IOException {
        MediaExtractor extractor = new MediaExtractor();
        extractor.setDataSource(videoFilePath);
        for (int i = 0; i < extractor.getTrackCount(); i++) {
            MediaFormat format = extractor.getTrackFormat(i);
            Log.d(TAG, "decodeH265Video, format i:"+i + ":"+format);
            String mine = format.getString(MediaFormat.KEY_MIME);
            Log.d(TAG, "decodeH265Video, mine i:"+i + ":"+mine);
            if(mine.startsWith("video/")){
                extractor.selectTrack(i);
                mediaCodec = MediaCodec.createDecoderByType(mine);
                mediaCodec.configure(format, outputSurface, null, 0);
                break;
            }
        }
//        int videoTrackIndex = getVideoTrackIndex(extractor);
//        MediaFormat videoFormat = extractor.getTrackFormat(videoTrackIndex);
//        String mimeType = videoFormat.getString(MediaFormat.KEY_MIME);


        mediaCodec.start();

        ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
//        ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

        boolean isInputDone = false;
        boolean isOutputDone = false;

        while (!isOutputDone) {
            if (!isInputDone) {
                int inputBufferIndex = mediaCodec.dequeueInputBuffer(10000);
                if (inputBufferIndex >= 0) {
                    ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                    int sampleSize = extractor.readSampleData(inputBuffer, 0);

                    if (sampleSize < 0) {
                        mediaCodec.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        isInputDone = true;
                    } else {
                        long presentationTimeUs = extractor.getSampleTime();
                        mediaCodec.queueInputBuffer(inputBufferIndex, 0, sampleSize, presentationTimeUs, 0);
                        extractor.advance();
                    }
                }else {
                    //等待查询空的buffer
                    continue;
                }
            }

            int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 1000);
            if (outputBufferIndex >= 0) {
                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    isOutputDone = true;
                }

//                ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                // 处理解码后的视频数据，例如渲染到Surface上
//                renderOutputBuffer(outputBuffer);

                mediaCodec.releaseOutputBuffer(outputBufferIndex, true);
            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                MediaFormat newFormat = mediaCodec.getOutputFormat();
                // 可以在这里获取新的格式信息，例如视频宽高等
            }
        }

        mediaCodec.stop();
        mediaCodec.release();

        extractor.release();
    }

    private int getVideoTrackIndex(MediaExtractor extractor) {
        for (int i = 0; i < extractor.getTrackCount(); i++) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mimeType = format.getString(MediaFormat.KEY_MIME);
            if (mimeType.startsWith("video/")) {
                return i;
            }
        }
        return -1;
    }

    private void renderOutputBuffer(ByteBuffer outputBuffer) {
        // 将解码后的视频数据渲染到Surface上
        // 这里可以使用OpenGL、SurfaceView或其它渲染方式进行处理
    }
}


//以下代码是修改后可用的MediaExtor示例代码，之后需要对比和当前的H265Decoder的区别
//package com.example.rtspplay.rtsp_player_can.MediaPlayer;
//
//
//
//import android.media.MediaCodec;
//import android.media.MediaExtractor;
//import android.media.MediaFormat;
//import android.util.Log;
//import android.view.Surface;
//
//import java.io.ByteArrayOutputStream;
//import java.io.DataInputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.ByteBuffer;
//
///**
// * @author zhangqingfa
// * @createDate 2020/12/10 11:39
// * @description 解码H264播放
// */
//public class H265DeCodePlay {
//
//    private static final String TAG = "zqf-dev";
//    //视频路径
//    private String videoPath;
//    //使用android MediaCodec解码
//    private MediaCodec mediaCodec;
//    private Surface surface;
//
//    public H265DeCodePlay(String videoPath, Surface surface) {
//        this.videoPath = videoPath;
//        this.surface = surface;
//        initMediaCodec();
//    }
//    MediaExtractor extractor;
//    private void initMediaCodec() {
//        extractor = new MediaExtractor();
//        try {
//            extractor.setDataSource(videoPath);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        for (int i = 0; i < extractor.getTrackCount(); i++) {
//            MediaFormat format = extractor.getTrackFormat(i);
//            Log.d(TAG, "decodeH265Video, format i:"+i + ":"+format);
//            String mine = format.getString(MediaFormat.KEY_MIME);
//            Log.d(TAG, "decodeH265Video, mine i:"+i + ":"+mine);
//            if(mine.startsWith("video/")){
//                extractor.selectTrack(i);
//                try {
//                    mediaCodec = MediaCodec.createDecoderByType(mine);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//                mediaCodec.configure(format, surface, null, 0);
//                break;
//            }
//        }
////        try {
////            Log.e(TAG, "videoPath " + videoPath);
////            //创建解码器 H264的Type为  AAC
////            mediaCodec = MediaCodec.createDecoderByType("video/hevc");
////            //创建配置
////            MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/hevc", 368, 384);
////            //设置解码预期的帧速率【以帧/秒为单位的视频格式的帧速率的键】
////            mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 15);
////            //配置绑定mediaFormat和surface
////            mediaCodec.configure(mediaFormat, surface, null, 0);
////        } catch (IOException e) {
////            e.printStackTrace();
////            //创建解码失败
////            Log.e(TAG, "创建解码失败");
////        }
//    }
//
//    /**
//     * 解码播放
//     */
//    public void decodePlay() {
//        mediaCodec.start();
//        new Thread(new MyRun()).start();
//    }
//
//    private class MyRun implements Runnable {
//
//        @Override
//        public void run() {
////            try {
//                //1、IO流方式读取h264文件【太大的视频分批加载】
//                byte[] bytes = null;
//                //bytes = getBytes(videoPath);
////                Log.e(TAG, "bytes size " + bytes.length);
//                //2、拿到 mediaCodec 所有队列buffer[]
//                ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
//                //开始位置
//                int startIndex = 0;
//                //h264总字节数
////                int totalSize = bytes.length;
//                //3、解析
//                while (true) {
//                    //判断是否符合
////                    if (totalSize == 0 || startIndex >= totalSize) {
////                        break;
////                    }
//                    //寻找索引
////                    int nextFrameStart = findByFrame(bytes, startIndex + 1, totalSize);
////                    if (nextFrameStart == -1) break;
////                    Log.e(TAG, "nextFrameStart " + nextFrameStart);
//                    MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
//                    // 查询10000毫秒后，如果dSP芯片的buffer全部被占用，返回-1；存在则大于0
//                    int inIndex = mediaCodec.dequeueInputBuffer(10000);
//                    if (inIndex >= 0) {
//                        //根据返回的index拿到可以用的buffer
//                        ByteBuffer byteBuffer = inputBuffers[inIndex];
//                        int sampleSize = extractor.readSampleData(byteBuffer, 0);
//
//                        if (sampleSize < 0) {
//                            mediaCodec.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
////                            isInputDone = true;
//                        } else {
//                            long presentationTimeUs = extractor.getSampleTime();
//                            mediaCodec.queueInputBuffer(inIndex, 0, sampleSize, presentationTimeUs, 0);
//                            extractor.advance();
//                        }
////                        //清空byteBuffer缓存
////                        byteBuffer.clear();
////                        //开始为buffer填充数据
////                        byteBuffer.put(bytes, startIndex, nextFrameStart - startIndex);
////                        //填充数据后通知mediacodec查询inIndex索引的这个buffer,
////                        mediaCodec.queueInputBuffer(inIndex, 0, nextFrameStart - startIndex, 0, 0);
////                        //为下一帧做准备，下一帧首就是前一帧的尾。
////                        startIndex = nextFrameStart;
//                    } else {
//                        //等待查询空的buffer
//                        continue;
//                    }
//                    //mediaCodec 查询 "mediaCodec的输出方队列"得到索引
//                    int outIndex = mediaCodec.dequeueOutputBuffer(info, 10000);
//                    Log.e(TAG, "outIndex " + outIndex);
//                    if (outIndex >= 0) {
//                        try {
//                            //暂时以休眠线程方式放慢播放速度
//                            Thread.sleep(33);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        //如果surface绑定了，则直接输入到surface渲染并释放
//                        mediaCodec.releaseOutputBuffer(outIndex, true);
//                    } else {
//                        Log.e(TAG, "没有解码成功");
//                    }
//                }
////            }
//        }
//    }
//
//
//    //读取一帧数据
//    private int findByFrame(byte[] bytes, int start, int totalSize) {
//        for (int i = start; i < totalSize - 4; i++) {
//            //对output.h264文件分析 可通过分隔符 0x00000001 读取真正的数据
//            if (bytes[i] == 0x00 && bytes[i + 1] == 0x00 && bytes[i + 2] == 0x00 && bytes[i + 3] == 0x01) {
//                return i;
//            }
//        }
//        return -1;
//    }
//
//    private byte[] getBytes(String videoPath) throws IOException {
//        InputStream is = new DataInputStream(new FileInputStream(new File(videoPath)));
//        int len;
//        int size = 1024;
//        byte[] buf;
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        buf = new byte[size];
//        while ((len = is.read(buf, 0, size)) != -1)
//            bos.write(buf, 0, len);
//        buf = bos.toByteArray();
//        return buf;
//    }
//}