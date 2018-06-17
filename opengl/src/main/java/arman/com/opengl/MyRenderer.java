package arman.com.opengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @Author admin.
 * @Time 2018/1/20.
 * @Description:
 * 1:着色器shader就相当于画笔;
 * 一个用于绘制顶点的顶点着色器VerticesShader(顶点的位置已经确定好了为何还需要单独一个程序来绘制？
 * 在后面你看到摄像机和纹理部分就知道实际传入的顶点坐标并不是简单的对应到屏幕上的坐标，所以顶点着色器的存在有它的 合理性，存在即合理)；
 * 一个用于给顶点连线后所包围的区域填充颜色的片元着色器FragmentShader，你可以简单的理解成windows中画图的填充工具(类似油漆桶)
 *
 *
 */

public class MyRenderer implements GLSurfaceView.Renderer {

    private int program;
    private int vPosition;
    private int uColor;

    // 缓冲区
    private FloatBuffer mBuffer;

    //当Surface被创建的时候，GLSurfaceView会调用这个方法，这发生在应用程序创建的第一次，
    // 并且当设备被唤醒或者用户从其他activity切换回去时，也会被调用。
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // 初始化着色器
        // 基于顶点着色器与片元着色器创建程序
        program = createProgram(verticesShader, fragmentShader);
        // 获取着色器中指定为attribute类型变量的id
        vPosition = GLES20.glGetAttribLocation(program, "vPosition");
        uColor = GLES20.glGetUniformLocation(program, "uColor");

        // 设置clear color颜色RGBA(这里仅仅是设置清屏时GLES20.glClear()用的颜色值而不是执行清屏)
        GLES20.glClearColor(0f, 0, 0, 0f);
        //类似clearcolor
//        gl.glClearDepthf(float);//指明深度缓冲区的清理值,区间是0~1,初始值为1
//        gl.glClearStencil(int);//指明模板缓冲区的清理值,指明模板缓冲区的清理值
    }

    //在Surface创建以后，每次Surface尺寸变化后，这个方法都会调用,记录了横向 竖向模式下的显示屏幕的最新宽高；
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 设置绘图的窗口(可以理解成在画布上划出一块区域来画图)，默认是整个屏幕
        // x.y表示现实屏幕的位置，并从左下角开始计算0,0，为了确保视图可见,必须显示在屏幕左下角和右上角x=w,y=h范围内
        GLES20.glViewport(0,0,width,height);//这个窗口坐标原点是位于屏幕左下角,与android中的屏幕坐标系不一样
    }

    //当绘制每一帧的时候会被调用。
    @Override
    public void onDrawFrame(GL10 gl) {
        //GLSurfaceView.RENDERMODE_CONTINUOUSLY : 固定一秒回调60次(60fps)
        // GLSurfaceView.RENDERMODE_WHEN_DIRTY   : 当调用GLSurfaceView.requestRender()之后回调一次

        // 获取图形的顶点坐标
        FloatBuffer vertices = getVertices();

        // 清屏 清除完毕 显示glClearColor 设置的颜色值
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        //mask:GL_COLOR_BUFFER_BIT——表明颜色缓冲区;GL_DEPTH_BUFFER_BIT——表明深度缓冲;GL_STENCIL_BUFFER_BIT——表明模型缓冲区


        /**
         *
         * 调用这个方法来启用客户端的某项功能。对应的是glDisableClientState(int array) 禁用客户端的某项功能。默认情况下,所有功能是被禁止的。
         * 允许设置顶点 GL10.GL_VERTEX_ARRAY顶点数组\
         *
         * GL_COLOR_ARRAY —— 如果启用，颜色矩阵可以用来写入以及调用glDrawArrays方法或者glDrawElements方法时进行渲染。详见glColorPointer。
         *
         GL_NORMAL_ARRAY —— 如果启用，法线(和光线有关)矩阵可以用来写入以及调用glDrawArrays方法或者glDrawElements方法时进行渲染。详见glNormalPointer。

         GL_TEXTURE_COORD_ARRAY —— 如果启用，纹理（材质）坐标矩阵可以用来写入以及调用glDrawArrays方法或者glDrawElements方法时进行渲染。详见glTexCoordPointer。

         GL_VERTEX_ARRAY —— 如果启用，顶点矩阵可以用来写入以及调用glDrawArrays方法或者glDrawElements方法时进行渲染。详见glVertexPointer。

         GL_POINT_SIZE_ARRAY_OES(OES_point_size_arrayextension)——如果启用，点大小矩阵控制大小以渲染点和点sprites。这时由glPointSize定义的点大小将被忽略，由点大小矩阵 提供的大小将被用来渲染点和点sprites。详见glPointSize。
         */
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        // 设置顶点 size=每个顶点的坐标维数，必须是2, 3 或者4，初始值是4
        //type:指明每个顶点坐标的数据类型，允许的符号常量GL_BYTE, GL_SHORT,GL_FIXED 和GL_FLOAT，初始值为GL_FLOAT
        //stride:指明连续顶点间的位偏移，如果为0，顶点被认为是紧密压入矩阵，初始值为0。
        //pointer 指明顶点坐标的缓冲区，如果为null，则没有设置缓冲区。
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mBuffer);

        // 使用某套shader程序
        GLES20.glUseProgram(program);
        // 为画笔指定顶点位置数据(vPosition)  属性索引,单顶点大小，数据类型，归一化，顶点间偏移量，顶点buffer
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, vertices);
        // 允许顶点位置数据数组，将激活队友的属性数组，并将顶点数据传递至open gl中；
        GLES20.glEnableVertexAttribArray(vPosition);
        // 设置属性uColor(颜色 索引,R,G,B,A)
        GLES20.glUniform4f(uColor, 0.0f, 1.0f, 0.0f, 1.0f);
        // 绘制 绘制方式，起始偏移量，顶点数量
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 3);
        //glDrawArrays=可告知顶点着色器当前操作将在open gl表面上绘制3个点，第二个参数是0，即pointVFA(0.1f,0.1f,0.1f)方法中从首个顶点开始渲染


        // 禁止顶点设置
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }

    /**
     * 加载指定shader的方法
     * @param shaderType shader的类型  GLES20.GL_VERTEX_SHADER   GLES20.GL_FRAGMENT_SHADER
     * @param sourceCode shader的脚本
     * @return shader索引
     */
    private int loadShader(int shaderType,String sourceCode) {
        // 根据类型创建一个新shader  应该是创建成功返回一个着色器的id 创建失败为0；
        int shader = GLES20.glCreateShader(shaderType);
        // 若创建成功则加载shader
        if (shader != 0) {
            // 加载shader的源代码
            GLES20.glShaderSource(shader, sourceCode);
            // 编译shader
            GLES20.glCompileShader(shader);
            // 存放编译成功shader数量的数组
            int[] compiled = new int[1];
            // 在编译阶段 获取Shader着色器 的编译情况，，在连接阶段使用glGetProgramiv获取连接情况
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {//若编译失败则显示错误日志并删除此shader
                Log.e("ES20_ERROR", "Could not compile shader " + shaderType + ":");
                Log.e("ES20_ERROR", GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    /**
     * 创建shader程序的方法
     */
    private int createProgram(String vertexSource, String fragmentSource) {
        //加载顶点着色器
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }

        // 加载片元着色器
        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }

        // 创建es2.0程序，在连接shader之前，首先要创建一个容纳程序的容器，称为着色器程序容器。可以通过glCreateProgram函数来创建一个程序容器。
        int program = GLES20.glCreateProgram();
        // 若程序创建成功(返回正整数作为着色器的id)则向程序中加入顶点着色器与片元着色器
        if (program != 0) {
            // 将顶点着色器加入到程序id=program中
            GLES20.glAttachShader(program, vertexShader);
            // 向程序中加入片元着色器
            GLES20.glAttachShader(program, pixelShader);
            //连接程序 在链接操作执行以后，可以任意修改shader的源代码，对shader重新编译不会影响整个程序，除非重新链接程序
            GLES20.glLinkProgram(program);
            // 存放链接成功program数量的数组
            int[] linkStatus = new int[1];
            // 获取program的链接情况
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            // 若链接失败则报错并删除程序
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e("ES20_ERROR", "Could not link program: ");
                Log.e("ES20_ERROR", GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    /**
     * 获取图形的顶点
     * 特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
     * 转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
     *
     * @return 顶点Buffer
     */
    private FloatBuffer getVertices() {
        float vertices[] = {
                0.0f,   0.5f,
                -0.5f, -0.5f,
                0.5f,  -0.5f,
        };

        // 创建顶点坐标数据缓冲
        // vertices.length*4是因为一个float占四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());             //设置字节顺序
        FloatBuffer vertexBuf = vbb.asFloatBuffer();    //转换为Float型缓冲
        vertexBuf.put(vertices);                        //向缓冲区中放入顶点坐标数据
        vertexBuf.position(0);                          //设置缓冲区起始位置

        return vertexBuf;
    }

    // 顶点着色器的脚本
    private static final String verticesShader
            = "attribute vec2 vPosition;            \n" // 顶点位置属性vPosition
            + "void main(){                         \n"
            + "   gl_Position = vec4(vPosition,0,1);\n" // 确定顶点位置
            + "}";

    // 片元着色器的脚本
    private static final String fragmentShader
            = "precision mediump float;         \n" // 声明float类型的精度为中等(精度越高越耗资源)
            + "uniform vec4 uColor;             \n" // uniform的属性uColor
            + "void main(){                     \n"
            + "   gl_FragColor = uColor;        \n" // 给此片元的填充色
            + "}";
}
