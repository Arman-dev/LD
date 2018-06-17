package demo.arman.com.mediacodece;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 1:创建MediaCodec
 * 2:通过MediaFormat配置MediaCodec;配置相关流控等
 * 3：打开编码器 获取输入输出缓冲区
 * 4：获取输出数据
 *
 * 硬编码流控：
 *  在配置时设置目标码率等控制
 *  mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);
 *  mediaFormat.setInteger(MediaFormat.KEY_BITRATE_MODE,
 *  MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_VBR);
 *  动态调整目标码率；（api>19）
 *  Bundle param = new Bundle();
 *  param.putInt(MediaCodec.PARAMETER_KEY_VIDEO_BITRATE, bitrate);
 *  mediaCodec.setParameters(param);
 */
public class Encoder {
    public static final int TRY_AGAIN_LATER = -1;
    public static final int BUFFER_OK = 0;
    public static final int BUFFER_TOO_SMALL = 1;
    public static final int OUTPUT_UPDATE = 2;

    private int format = 0;
    private final String MIME_TYPE = "video/avc";
    private MediaCodec mMC = null;
    private MediaFormat mMF;
    private ByteBuffer[] inputBuffers;
    private ByteBuffer[] outputBuffers;
    private long BUFFER_TIMEOUT = 0;
    private MediaCodec.BufferInfo mBI;

    /**
     * 初始化编码器
     * @throws IOException 创建编码器失败会抛出异常
     */
    public void init() throws IOException {
        //根据类型创建 ，createByCodecName
        mMC = MediaCodec.createEncoderByType(MIME_TYPE);
        //本机支持的颜色空间。一般是yuv420p或者yuv420sp，Camera预览格式一般是yv12或者NV21，所以在编码之前需要进行格式转换
//        format = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar;
        format=MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar;
        mBI = new MediaCodec.BufferInfo();
    }

    /**
     * 配置编码器，需要配置颜色、帧率、比特率以及视频宽高
     * @param width 视频的宽
     * @param height 视频的高
     * @param bitrate 视频比特率
     * @param framerate 视频帧率
     */
    public void configure(int width,int height,int bitrate,int framerate){
        if(mMF == null){
            //配置编码器
            mMF = MediaFormat.createVideoFormat(MIME_TYPE, width, height);
            mMF.setInteger(MediaFormat.KEY_BIT_RATE, bitrate);
            mMF.setInteger(MediaFormat.KEY_FRAME_RATE, framerate);
            if (format != 0){
                //本机支持的颜色空间，一般是yuv420p或yuv420sp
                mMF.setInteger(MediaFormat.KEY_COLOR_FORMAT, format);
            }
            mMF.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, -1); //关键帧间隔时间 单位s
            /**
             * 码率控制模式
             * CQ 对应于 OMX_Video_ControlRateDisable，它表示完全不控制码率，尽最大可能保证图像质量；
             * CBR 对应于 OMX_Video_ControlRateConstant，它表示编码器会尽量把输出码率控制为设定值”；
             * VBR 对应于 OMX_Video_ControlRateVariable，它表示编码器会根据图像内容的复杂度（实际上是帧间变化量的大小）来动态调整输出码率，图像复杂则码率高，图像简单则码率低；
             */
            mMF.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CQ);

            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                //这2个值需同时设置
                mMF.setInteger(MediaFormat.KEY_PROFILE,MediaCodecInfo.CodecProfileLevel.AVCProfileHigh);
                mMF.setInteger(MediaFormat.KEY_LEVEL,MediaCodecInfo.CodecProfileLevel.AVCLevel31);
            }
        }
        initCallBck(mMC);
        mMC.configure(mMF,null,null, MediaCodec.CONFIGURE_FLAG_ENCODE);
    }

    /**
     * 开启编码器，获取输入输出缓冲区
     */
    public void start(){
        mMC.start();
        //在kitkatwatch和以前的版本中，输入和输出缓冲区由ByteBuffer数组表示。
        // 在成功调用start()之后，使用getinput/outputbuffer()检索缓冲区数组
        inputBuffers = mMC.getInputBuffers();
        outputBuffers = mMC.getOutputBuffers();
    }

    /**
     * 向编码器输入数据，此处要求输入YUV420P的数据
     * @param data YUV数据
     * @param len 有效数据长度
     * @param timestamp 时间戳
     * @return
     * 通过dequeueInputBuffer 获取到可用的缓冲区位置索引，通过之前拿到的输入缓冲区的ByteBuffer，将帧数据放进缓冲区ByteBuffer;
     * 然后排队执行编码
     */
    public int input(byte[] data,int len,long timestamp){
        // 如果存在可用的缓冲区,此方法会返回其位置索引,否则返回-1,
        // 参数为超时时间,单位是毫秒,如果此参数是0,则立即返回,
        // 如果参数小于0,则无限等待直到有可使用的缓冲区,如果参数大于0,则等待时间为传入的毫秒值。
        int index = mMC.dequeueInputBuffer(BUFFER_TIMEOUT);
        Log.e("...","" + index);
        if(index >= 0){
            ByteBuffer inputBuffer = inputBuffers[index];
            inputBuffer.clear();//清除原来的数据 以接收新的内容
            if(inputBuffer.capacity() < len){
                //此缓冲区一旦使用,只有在 dequeueInputBuffer 返回其索引位置才代表它可以再次使用。
                mMC.queueInputBuffer(index, 0, 0, timestamp, 0);
                return BUFFER_TOO_SMALL;
            }
            inputBuffer.put(data,0,len);
            mMC.queueInputBuffer(index,0,len,timestamp,0);
        }else{
            return index;
        }
        return BUFFER_OK;
    }

    /**
     * 输出编码后的数据
     * @param data 数据
     * @param len 有效数据长度
     * @param ts 时间戳
     * @return
     * 通过dequeueOutputBuffer 获取输出缓冲区索引 传入BufferInfo(通过BufferInfo获取ByteBuffer的信息，通过BufferInfo我们可以得到当前数据是否Codec-specific Data);
     *
     */
    public int output(byte[] data,int[] len,long[] ts){
        //获取可用的输出缓冲区
        // 1：bufferinfo类型的实例  2：超时时间,负数代表无限等待
        int i = mMC.dequeueOutputBuffer(mBI, BUFFER_TIMEOUT);
        if(i >= 0){
            if(mBI.size > data.length) return BUFFER_TOO_SMALL;
            outputBuffers[i].position(mBI.offset);//mBI.offset 不同设备部相同
            outputBuffers[i].limit(mBI.offset + mBI.size);//当前缓冲区的当前终点,不能对缓冲区超过极限的位置进行读写操作且极限是可以修改的；
            outputBuffers[i].get(data, 0, mBI.size);//获取输出数据到data中
            len[0] = mBI.size ;
            ts[0] = mBI.presentationTimeUs;
            mMC.releaseOutputBuffer(i, false);//释放缓冲区
        } else if (i == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
            outputBuffers = mMC.getOutputBuffers();
            return OUTPUT_UPDATE;
        } else if (i == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
            mMF = mMC.getOutputFormat();
            return OUTPUT_UPDATE;
        } else if (i == MediaCodec.INFO_TRY_AGAIN_LATER) {
            return TRY_AGAIN_LATER;
        }

        return BUFFER_OK;
    }

    /**
     * 停止并销毁编码器：
     *      先告知编码器我们要结束编码，Surface 输入时调用 mVideoCodec.signalEndOfInputStream，普通输入则可以为在 queueInputBuffer 时指定;
     *      MediaCodec.BUFFER_FLAG_END_OF_STREAM 这个 flag；告知编码器后我们就可以等到编码器输出的 buffer 带着 MediaCodec.BUFFER_FLAG_END_OF_STREAM 这个 flag 了，等到之后我们调用 mVideoEncoder.release 销毁编码器；
     */
    public void release(){
        mMC.stop();
        mMC.release();
        mMC = null;
        outputBuffers = null;
        inputBuffers = null;
    }

    public void flush() {
        mMC.flush();
    }

    private void initCallBck(MediaCodec mediaCodec) {
        //文档也没有明确说 setCallback 应该在 configure 之前，但既然示例是这样写的，我们还是保持这样
        //api>21:异步消费输出
//        mediaCodec.setCallback(new MediaCodec.Callback() {
//            @Override
//            public void onInputBufferAvailable(MediaCodec mediaCodec, int i) {
//
//            }
//
//            @Override
//            public void onOutputBufferAvailable(MediaCodec mediaCodec, int i, MediaCodec.BufferInfo bufferInfo) {
//                //消费编码器输出数据：异步模式下，我们在 onOutputBufferAvailable 中使用 buffer 内的数据，然后 releaseOutputBuffer 即可；同步模式下我们需要 dequeueOutputBuffer、使用 buffer 内的数据、releaseOutputBuffer；
//            }
//
//            @Override
//            public void onError(MediaCodec mediaCodec, MediaCodec.CodecException e) {
//
//            }
//
//            @Override
//            public void onOutputFormatChanged(MediaCodec mediaCodec, MediaFormat mediaFormat) {
//
//            }
//        });

        //api>19 surface输入
//        mediaCodec.createInputSurface();
    }
}
