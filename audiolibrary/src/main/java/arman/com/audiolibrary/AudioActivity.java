package arman.com.audiolibrary;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Route(path = "/audiolibrary/AudioActivity")
public class AudioActivity extends AppCompatActivity {

    private File file=null;
    private boolean isRecording;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
    }

    public void click(View view){
        int i = view.getId();
        if (i == R.id.btn_start) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    StartRecord();
                    Log.e("tag", "start");
                }
            });
            thread.start();
//                ButtonEnabled(false, true, false);

        } else if (i == R.id.btn_stop) {
            isRecording = false;
            Log.i("Tag", "停止录音");

        } else if (i == R.id.btn_player) {
            PlayRecord();

        }
    }

    /**
     * Android提供了两个API用于实现录音功能：android.media.AudioRecord、android.media.MediaRecorder。
     1、AudioRecord
     主要是实现边录边播（AudioRecord+AudioTrack）以及对音频的实时处理（如会说话的汤姆猫、语音）

     优点：语音的实时处理，可以用代码实现各种音频的封装

     缺点：输出是PCM语音数据，如果保存成音频文件，是不能够被播放器播放的，所以必须先写代码实现数据编码以及压缩

     示例：

     使用AudioRecord类录音，并实现WAV格式封装。录音20s，输出的音频文件大概为3.5M左右（已写测试代码）

     2、MediaRecorder

     已经集成了录音、编码、压缩等，支持少量的录音音频格式，大概有.aac（API = 16） .amr .3gp

     优点：大部分以及集成，直接调用相关接口即可，代码量小

     缺点：无法实时处理音频；输出的音频格式不是很多，例如没有输出mp3格式文件

     示例：

     使用MediaRecorder类录音，输出amr格式文件。录音20s，输出的音频文件大概为33K（已写测试代码）

     3、音频格式比较

     WAV格式：录音质量高，但是压缩率小，文件大

     AAC格式：相对于mp3，AAC格式的音质更佳，文件更小；有损压缩；一般苹果或者Android SDK4.1.2（API 16）及以上版本支持播放

     AMR格式：压缩比比较大，但相对其他的压缩格式质量比较差，多用于人声，通话录音

     至于常用的mp3格式，使用MediaRecorder没有该视频格式输出。一些人的做法是使用AudioRecord录音，然后编码成wav格式，再转换成mp3格式
     */
    public void StartRecord() {
        //16K采集率
        int frequency = 16000;
        //格式
        int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
        //16Bit
        int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
        //生成PCM文件
        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/reverseme.pcm");
        Log.i("tag","生成文件");
        //如果存在，就先删除再创建
        if (file.exists())
            file.delete();
        Log.i("tag","删除文件");
        try {
            file.createNewFile();
            Log.i("tag","创建文件");
        } catch (IOException e) {
            Log.i("tag","未能创建");
            throw new IllegalStateException("未能创建" + file.toString());
        }

        try {
            //输出流
            OutputStream os = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(os);
            //打开一个输出流，指向创建的文件
            DataOutputStream dos = new DataOutputStream(bos);
            //录制缓冲大小
            int bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
            /**
             * 1：录音源 mic:麦克风、default:默认、voice_call:语言播出的与对方说话的声音、voice_communication：摄像头旁边的麦克风、VOICE_RECOGNITION、VOICE_UPLINK
             * 2：录制频率：8000hz或者11025hz等，不同的硬件设备这个值不同；
             * 3：录制通道，可以为AudioFormat.CHANNEL_CONFIGURATION_MONO和AudioFormat.CHANNEL_CONFIGURATION_STEREO
             * 4：录制编码格式，可以为AudioFormat.ENCODING_16BIT和8BIT,其中16BIT的仿真性比8BIT好，但是需要消耗更多的电量和存储空间，16BIT可以兼容更多的手机
             * 5：录制缓冲大小
             */
            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration, audioEncoding, bufferSize);
            short[] buffer = new short[bufferSize];
            audioRecord.startRecording();
            Log.i("tag", "开始录音");
            isRecording = true;
            while (isRecording) {
                int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
                for (int i = 0; i < bufferReadResult; i++) {
                    dos.writeShort(buffer[i]);
                }
            }
            audioRecord.stop();
            dos.close();
        } catch (Throwable t) {
            Log.e("tag", "录音失败");
        }
    }

    /**
     * 播放
     * android中播放声音可以用MediaPlayer和AudioTrack两种方案的，但是两种方案是有很大区别的，MediaPlayer可以播放多种格式的声音文件，
     * 例如MP3，AAC，WAV，OGG，MIDI等。而AudioTrack只能播放PCM数据流。
     * 事实上，两种本质上是没啥区别的，MediaPlayer在播放音频时，在framework层还是会创建AudioTrack，把解码后的PCM数据流传递给AudioTrack，
     * 最后由AudioFlinger进行混音，传递音频给硬件播放出来。利用AudioTrack播放只是跳过Mediaplayer的解码部分而已。
     * Mediaplayer的解码核心部分是基于OpenCORE 来实现的，支持通用的音视频和图像格式，codec使用的是OpenMAX接口来进行扩展。
     * 因此使用audiotrack播放mp3文件的话，要自己加入一个音频解码器，如libmad。否则只能播放PCM数据，如大多数WAV格式的音频文件。
     */
    public void PlayRecord() {
        if(file == null){
            return;
        }
        //读取文件
        int musicLength = (int) (file.length() / 2);
        short[] music = new short[musicLength];
        try {
            InputStream is = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            DataInputStream dis = new DataInputStream(bis);
            int i = 0;
            while (dis.available() > 0) {
                music[i] = dis.readShort();
                i++;
            }
            dis.close();
            /**
             * 1:streamType:stream_music:音乐声，RING:铃声，ALARM:警告，SYSTEM:系统提示,VOICE_CALL:电话声
             * 2:sampleRateInHz:采样率 播放的音频每秒会有多少次采样,可选为 8000 16000 22050 24000 32000 44100 48000
             * 3：channelConfig：CHANNEL_CONFIGURATION_MONO：单声道 ；CHANNEL_CONFIGURATION_STEREO：双声道（大多数手机都是伪立体声采集，为了性能考虑所以使用单声道）
             * 4：audioFormat：AudioRecord录制格式同；
             * 5：bufferSizeInBytes：AudioTrack内部音频缓冲区大小，建议使用getMinBufferSize计算出缓冲区并设置
             * 6：mode:播放模式：MODE_STREAM：按照一定的时间间隔不间断的写入音频数据，MODE_STSTIC：需要一次性将所有的数据都写入播放缓冲区，一般用户铃声和提示音；
             */
            AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    16000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    musicLength * 2,
                    AudioTrack.MODE_STREAM);
            if(null!=audioTrack && audioTrack.getState()!=AudioTrack.STATE_UNINITIALIZED){//audioTrack不为空且当前状态已经初始化完成
                audioTrack.play();//切换到播放状态
            }
            audioTrack.write(music, 0, musicLength); //将数据写入到AudioTrack中
            audioTrack.stop();
        } catch (Throwable t) {
            Log.e("tag", "播放失败");
        }
    }
}
