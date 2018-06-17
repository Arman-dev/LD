package arman.com.opengl;

import android.opengl.GLSurfaceView;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import arman.com.opengl.utils.ToolUtils;

/**
 * @Author admin.
 * @Time 2018/5/5.
 * @Description:
 */

public class Open1Render implements GLSurfaceView.Renderer{


    private float rotate=0;

    /**
     * 索引数组
     * 索引就是让不同的顶点按照规定的顺序来绘制，这样就不会导致各种三角形的错乱。

     首先定义一个索引数组,这里的0,1,2等指的是顶点顺序，比如0,1,2就是按照0,1,2的顺序绘制三角形
     ,0,2,3就是按照02,3的顺序绘制三角形，就不会出现0,1,2 和1,2,3 以及 023都绘制的情况。
     */
    private short [] indices={
            0,1,2,
            0,2,3,

            4,5,6,
            4,6,7,

            8,9,10,
            8,10,11,

            12,13,14,
            12,14,15,

            16,17,18,
            16,18,19,

            20,21,22,
            20,22,23,
    };

    //顶点数组
    private float[] mArrayVertex = {
            -0.6f , 0.6f , 0f,

            -0.2f , 0f , 0f ,

            0.2f , 0.6f , 0f};

    private int count=24;
            //mArrayVertex.length/3;count


    /**立方体:立方体有多个面：顺序为：左右 前后 上下**/
    // 定义立方体的8个顶点
    float[] cubeVertices = {
            //左面
            -0.5f,0.5f,0.5f,
            -0.5f,-0.5f,0.5f,
            -0.5f,0.5f,-0.5f,
            -0.5f,-0.5f,-0.5f,

            //右面
            0.5f, 0.5f,0.5f,
            0.5f,-0.5f,0.5f,
            0.5f,-0.5f,-0.5f,
            0.5f,0.5f,-0.5f ,

            //前面
            -0.5f,0.5f,0.5f,
            -0.5f,-0.5f,0.5f,
            0.5f,-0.5f,0.5f,
            0.5f, 0.5f,0.5f,

            //后面
            0.5f,-0.5f,-0.5f,
            0.5f,0.5f,-0.5f,
            -0.5f,0.5f,-0.5f,
            -0.5f,-0.5f,-0.5f,

            //上面
            -0.5f,0.5f,0.5f,
            0.5f, 0.5f,0.5f,
            0.5f,0.5f,-0.5f,
            -0.5f,0.5f,-0.5f,

            //下面
            -0.5f,-0.5f,0.5f,
            0.5f,-0.5f,0.5f,
            0.5f,-0.5f,-0.5f,
            -0.5f,-0.5f,-0.5f
    };

    //  颜色数组
    float []  cubeColors = {
            1f,0f,0f,1f ,
            0f,1f,0f,1f,
            0f,0f,1f,1f,
            1f,0f,0f,1f,

            1f,0f,0f,1f ,
            0f,1f,0f,1f,
            0f,0f,1f,1f,
            1f,0f,0f,1f,

            1f,0f,0f,1f ,
            0f,1f,0f,1f,
            0f,0f,1f,1f,
            1f,0f,0f,1f,

            1f,0f,0f,1f ,
            0f,1f,0f,1f,
            0f,0f,1f,1f,
            1f,0f,0f,1f,


            1f,0f,0f,1f ,
            0f,1f,0f,1f,
            0f,0f,1f,1f,
            1f,0f,0f,1f,

            1f,0f,0f,1f ,
            0f,1f,0f,1f,
            0f,0f,1f,1f,
            1f,0f,0f,1f,
    };

    // 缓冲区
    private FloatBuffer mCubeColorsBuffer,cubeVerticesBuffer,mBuffer;
    private ShortBuffer indexbuffer;

    public Open1Render(){
        // 获取浮点形缓冲数据
        mBuffer = ToolUtils.getFloatBuffer(mArrayVertex);
        cubeVerticesBuffer=ToolUtils.getFloatBuffer(cubeVertices);
        mCubeColorsBuffer=ToolUtils.getFloatBuffer(cubeColors);
        //获取浮点型索引数据
        indexbuffer= ToolUtils.getShortBuffer(indices);
    }


    //创建的时候调用 当activiey被唤醒调用 切换回来的时候会被调用
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        //设置清屏时屏幕用的颜色
        //r g b a  取值范围为0-1 并不是0-255
        gl.glClearColor(0f,0f,0f,0f);
    }

    //activity大小改变  横竖屏幕的时候 会在created之后调用
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //设置视口（viewport）尺寸，告诉OpenGl可以用来渲染的surface的大小
        gl.glViewport(width/4, width/2, width/2, height/2);

//        设置观察的位置和角度
//        GLU.gluLookAt(gl, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
//
//                - eye理解为照相机或者你的眼睛，默认位置在原点(0,0,0)
//                - center是观察物体的坐标,从eye到center的向量就是观察的方向
//                - up默认为Y轴正方向，可以理解为眼睛到头顶的方向，比如改成(0,-1,0)那就是倒立着观察了
    }

    //渲染绘制帧  如果什么都不绘制也要调用清空屏幕  不然会闪动 渲染缓冲区会被交换并显示在在屏幕上
    @Override
    public void onDrawFrame(GL10 gl) {

        try {
            //清空屏幕（清理缓冲区），并调用之前glClearColor的颜色填充整个surface,
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
            //GL_COLOR_BUFFER_BIT —— 表明颜色缓冲区
            //GL_DEPTH_BUFFER_BIT —— 表明深度缓冲
            //GL_STENCIL_BUFFER_BIT —— 表明模型缓冲区

            /**
             * 允许设置顶点 GL10.GL_VERTEX_ARRAY顶点数组\
             *
             * GL_COLOR_ARRAY —— 如果启用，颜色矩阵可以用来写入以及调用glDrawArrays方法或者glDrawElements方法时进行渲染。详见glColorPointer。
             *
             GL_NORMAL_ARRAY —— 如果启用，法线矩阵可以用来写入以及调用glDrawArrays方法或者glDrawElements方法时进行渲染。详见glNormalPointer。

             GL_TEXTURE_COORD_ARRAY —— 如果启用，纹理坐标矩阵可以用来写入以及调用glDrawArrays方法或者glDrawElements方法时进行渲染。详见glTexCoordPointer。

             GL_VERTEX_ARRAY —— 如果启用，顶点矩阵可以用来写入以及调用glDrawArrays方法或者glDrawElements方法时进行渲染。详见glVertexPointer。

             GL_POINT_SIZE_ARRAY_OES(OES_point_size_arrayextension)——如果启用，点大小矩阵控制大小以渲染点和点sprites。这时由glPointSize定义的点大小将被忽略，由点大小矩阵 提供的大小将被用来渲染点和点sprites。详见glPointSize。
             */
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

            //size=每个顶点的坐标维数，必须是2, 3 或者4，初始值是4
            //type:指明每个顶点坐标的数据类型，允许的符号常量GL_BYTE, GL_SHORT,GL_FIXED 和GL_FLOAT，初始值为GL_FLOAT
            //stride:指明连续顶点间的位偏移，如果为0，顶点被认为是紧密压入矩阵，初始值为0。
            //pointer 指明顶点坐标的缓冲区，如果为null，则没有设置缓冲区。
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, cubeVerticesBuffer);//设置顶点

            // 设置点的颜色为绿色
            gl.glColor4f(0f, 1f, 0f, 0f);

            // 设置点的大小
            gl.glPointSize(10f);


            // 开启颜色渲染功能.
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

            /**
             *  size ———— 指明每个颜色的元素数量，必须为4
             - type————— 指明每个矩阵中颜色元素的数据类型
             - stride————指明连续的点之间的位偏移，如果stride 为0 时，颜色被紧密挤入矩阵，初始值为0
             - pointer———指明包含颜色的缓冲区
             */
            gl.glColorPointer(4, GL10.GL_FIXED, 0, mCubeColorsBuffer);// 设置三角形顶点的颜色


            //----OpenGL ES提供了两类方法来绘制一个空间几何图形------//
            /**
             * 绘制数组里面所有点构成的各个三角片
             *
             * mode:
             * GL_POINTS   只绘制独立的点；
             * GL_LINE_STRIP    将点按照顺序连接起来,不封闭(首尾不连接)
             * GL_LINE_LOOP     将点按照顺序连接起来,封闭（首尾连接）
             * GL_LINES         将点两两连接起来
             * GL_TRIANGLE_STRIP 每相邻三个顶点组成一个三角形，4个点 2个三角
             * GL_TRIANGLE_FAN   以一个点为三角形公共顶点，组成一系列相邻的三角形
             * GL_TRIANGLES      每隔三个顶点构成一个三角形，当顶点个数不足以绘制第二个三角形时，就忽略
             *
             * first：从数组缓存中的哪一位开始绘制，一般都定义为0
             * count：顶点的数量
             */
//        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, count);//使用VetexBuffer 来绘制，顶点的顺序由vertexBuffer中的顺序指定。

            /**
             *可以重新定义顶点的顺序，顶点的顺序由indices Buffer 指定。
             * - mode——指明被渲染的是哪种图元，被允许的符号常量有GL_POINTS,GL_LINE_STRIP,GL_LINE_LOOP,GL_LINES,GL_TRIANGLE_STRIP, GL_TRIANGLE_FAN 和GL_TRIANGLES
             - count——指明被渲染的元素个数。
             - type——指明索引指的类型，不是GL_UNSIGNED_BYTE 就是GL_UNSIGNED_SHORT。
             - indices——指明存储索引的位置指针。
             */
            gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_SHORT, indexbuffer);


            // 禁止顶点设置
            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
            //关闭颜色渲染功能.
            gl.glDisableClientState(GL10.GL_COLOR_ARRAY);


            //---在平移变换之前，需调用以下代码才能进行变换----//
            gl.glLoadIdentity();//重置矩阵为没有任何变换之前
            //向右移动0.6f个x坐标
//        gl.glTranslatef(0.6f, 0f, 0f);

            /**
             *调用glRotatef(float angle, float x, float y, float z)传入4个参数，
             * 角度和x、y、z坐标。此时一定要注意旋转的角度,角度为正表示逆时针。
             * 可以用安培定则 来判断旋转方向,大拇指正对向量方向,卷曲手握着向量,手指弯曲的方向即为要旋转的方向
             */
//        gl.glRotatef(60, 1f, 0f, 0f);//以(1f,0f,0f)空间向量旋转60度
            //以(1f,0f,0f)空间向量旋转180度
            //gl.glRotatef(180, 1f, 0f, 0f);

//        // 沿着Y轴旋转
            gl.glRotatef(rotate, 0f, 1f, 0f);
            // 旋转角度增加1
            rotate-=1;

            /**
             * 进行缩放,三个参数为缩放比例,
             * 缩放之前的坐标乘以缩放比例即可
             */
//        gl.glScalef(0.1f,0.1f, 0.1f);//缩小为原来0.1倍

            //保存当前矩阵
//        gl.glPushMatrix();
//        //重置矩阵
//        gl.glLoadIdentity();
//        //恢复矩阵
//        gl.glPopMatrix();

            //------OpenGL ES可以使用两种不同的投影变换：透视投影（Perspective Projection）和正侧投影（Orthographic Projection----------//


            //--openGl 添加颜色分为2种：---//
            //Flat coloring             单色
            //Smooth coloring           平滑着色(渐变)
            // 颜色设置必须在绘制之前，并且需要开启和关闭颜色渲染功能



            getTest5(gl);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 材质和光照
     * @param gl
     */
    private void getTest5(GL10 gl) {
        /**
         * 光照效果由4部分组成:
         * - Emitted(光源）：物体本身所发射出的光，有的物体不发射光，那就没有这个属性
         - diffuse(漫反射光）：投射在粗糙表面上的光向各个方向反射的现象
         - specular（镜面反射光）：反射面比较光滑，当平行入射的光线射到这个反射面时，仍会平行地向一个方向反射出来
         - ambient(环境光）：环境中进行了多次散射的光，而最终无法分辨其方向的光
         */
        /**
         * 材质 ：物体的材质属性通过反射不同方向的环境光，漫反射光，镜面光的RGB颜色来表示的。分为四种:
         * - 泛射材质
         - 漫反射材质
         - 镜面反射材质
         - 发射材质
         */
        /**
         * OpenGL的光照模型需要以下4步:
         */
        //1:打开光源总开关,,OpenGL ES中，仅仅支持有限数量的光源。使用GL_LIGHT0表示第0号光源，GL_LIGHT1表示第1号光源，依次类推，OpenGL至少会支持8个光源 。GL_LIGHT0到GL_LIGHT7
        gl.glEnable(GL10.GL_LIGHTING);
        //2:设置光源的种类、位置和方向(对于平行光源)

//        gl.glLightfv(int light, int pname, float[] params, int offset)
    }
}