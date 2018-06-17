package arman.com.opengl.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * @Author admin.
 * @Time 2018/3/4.
 * @Description:
 */

public class ToolUtils {

    /**
     *
     * 为了提高性能，会把点和颜色的信息放在java.nio包下的buffer中，类似于IO流中的缓冲
     * @param vertexes float 数组
     * @return 获取浮点形缓冲数据
     */
    public static FloatBuffer getFloatBuffer(float[] vertexes) {
        FloatBuffer buffer;
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertexes.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        buffer = vbb.asFloatBuffer();
        //写入数组
        buffer.put(vertexes);
        //设置默认的读取位置
        buffer.position(0);
        return buffer;
    }


    /**
     * @param vertexs
     *            int数组
     * @return 获取整形缓冲数据
     */
    public static IntBuffer getIntBuffer(int[] vertexs) {
        IntBuffer buffer;
        ByteBuffer qbb = ByteBuffer.allocateDirect(vertexs.length * 4);
        qbb.order(ByteOrder.nativeOrder());
        buffer = qbb.asIntBuffer();
        buffer.put(vertexs);
        buffer.position(0);
        return buffer;
    }

    /**
     * @param vertexs
     *            Byte 数组
     * @return 获取字节型缓冲数据
     */
    public static ByteBuffer getByteBuffer(byte[] vertexs) {
        ByteBuffer buffer = null;
        buffer = ByteBuffer.allocateDirect(vertexs.length);
        buffer.put(vertexs);
        buffer.position(0);
        return buffer;
    }

    /**
     * @param vertexs
     *            Byte 数组
     * @return 获取字节型缓冲数据
     */
    public static ShortBuffer getShortBuffer(short[] vertexs) {
        ShortBuffer buffer;
        ByteBuffer qbb = ByteBuffer.allocateDirect(vertexs.length * 2);
        qbb.order(ByteOrder.nativeOrder());
        buffer = qbb.asShortBuffer();
        buffer.put(vertexs);
        buffer.position(0);

        return buffer;
    }
}
