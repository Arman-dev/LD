package arman.com.opengl;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;

/**
 *
 1、建模：OpenGL图形库除了提供基本的点、线、多边形的绘制函数外，还提供了复杂的三维物体（球、锥、多面体、茶壶等）以及复杂曲线和曲面绘制函数。
 2、变换：OpenGL图形库的变换包括基本变换和投影变换。基本变换有平移、旋转、缩放、镜像四种变换，投影变换有平行投影（又称正射投影）和透视投 影两种变换。其变换方法有利于减少算法的运行时间，提高三维图形的显示速度。
 3、颜色模式设置：OpenGL颜色模式有两种，即RGBA模式和颜色索引（Color Index）。
 4、光照和材质设置：OpenGL光有自发光（Emitted Light）、环境光（Ambient Light）、漫反射光（Diffuse Light）和高光（Specular Light）。材质是用光反射率来表示。场景（Scene）中物体最终反映到人眼的颜色是光的红绿蓝分量与材质红绿蓝分量的反射率相乘后形成的颜色。
 5、纹理映射（Texture Mapping）。利用OpenGL纹理映射功能可以十分逼真地表达物体表面细节。
 6、位图显示和图象增强图象功能除了基本的拷贝和像素读写外，还提供融合（Blending）、抗锯齿（反走样）（Antialiasing）和雾（fog）的特殊图象效果处理。以上三条可使被仿真物更具真实感，增强图形显示的效果。
 7、双缓存动画（Double Buffering）双缓存即前台缓存和后台缓存，简言之，后台缓存计算场景、生成画面，前台缓存显示后台缓存已画好的画面。
 */


/**
 * 将一个3D模型显示到2D屏幕中有以下四个过程：
 * 1. 视角（Viewing）变换：一台照相机移动，从不同的位置来观察一个人
 * 2. 模型（Modeling）变换：此时相机不动，人做移动
 * 3. 投影（Projection）变换：此时相机可以调整远近距离，人不动,这样观察到的也会不一样
 * 4. 视窗（Viewport）变换：按下快门之后，需要把像素按照比例转化后显示到屏幕上,这就是视窗
 *
 *
 * 视角和模型变换是一样的：具有一下三种变换
 *  - Translate    平移变换
 *  - Rotate       旋转变换
 *  - Scale        缩放变换
 */
@Route(path = "/opengl/OpenGLTestActivity")
public class OpenGLTestActivity extends AppCompatActivity {
    /**
     * OpenGL的好处：目前为止最高效的方法是有效地使用图形处理单元，或者叫 GPU；GPU 可以集中来处理好一件事情，就是并行地做浮点运算。
     * 事实上，图像处理和渲染就是在将要渲染到窗口上的像素上做许许多多的浮点运算。也就是说用GPU来分担CPU的工作，提高工作效率;
     * OpenGL ES：openGL的子集；Open GL ES2.0源自OPen gl 2.0 api(桌面级硬件的可编程图像渲染管线)
     * EGL:OpenGL负责绘制 EGL负责和设备交互；
     * GLSurfaceView会处理OpenGL初始化过程中比较基础的操作，可以配置显示设备以及在后台线程中渲染。渲染是在surface的特定区域中完成的，也称为视口；
     */
    private boolean rendererSet=false;
    private GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_gltest);
        //GLSurfaceView 将opengl绑定在了一起,GLSurfaceView销毁 gl一起销毁,
        glSurfaceView = (GLSurfaceView) findViewById(R.id.glsurfaceView);
        //--called unimplemented OpenGL ES API; 解决这个方法需要在清单文件中声明gl版本 还有就是要和设置的版本号对应--//

        setglClientVersion();
        /**
         * 设置渲染模式为连续模式(会以60fps的速度刷新)
         * GLSurfaceView会在一个单独的线程中调用渲染器的方法
         * RENDERMODE_WHEN_DIRTY 表示被动渲染，只有在调用requestRender或者onResume等方法时才会进行渲染。
         * RENDERMODE_CONTINUOUSLY 表示持续渲染
         */
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        /*
        * EGL创建EGLSurface有三个方法：
        * WindowSurface：顾名思义WindowSurface是和窗口相关的，也就是在屏幕上的一块显示区的封装，渲染后即显示在界面上；
        * PbufferSurface：在显存中开辟一个空间，将渲染后的数据(帧)存放在这里
        * PixmapSurface：以位图的形式存放在内存中，据说各平台的支持不是很好；
        * */
    }

    /**
     * 检查手机是够支持opengl 2.0
     */
    private void setglClientVersion(){
        ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager
                .getDeviceConfigurationInfo();

        final boolean supportsEs2 =
                configurationInfo.reqGlEsVersion >= 0x20000
                        || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                        && (Build.FINGERPRINT.startsWith("generic")
                        || Build.FINGERPRINT.startsWith("unknown")
                        || Build.MODEL.contains("google_sdk")
                        || Build.MODEL.contains("Emulator")
                        || Build.MODEL.contains("Android SDK built for x86")));

        if (supportsEs2) {
//            // 设置OpenGL版本
            glSurfaceView.setEGLContextClientVersion(1);
            // 设置自定义的渲染器
            glSurfaceView.setRenderer(new Open1Render());
            rendererSet=true;
        } else {
            Toast.makeText(this, "This device does not support OpenGL ES 2.0.",
                    Toast.LENGTH_LONG).show();
            return;
        }
    }

    private boolean IsSupported() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x2000;

        return supportsEs2;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(rendererSet){
            glSurfaceView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(rendererSet){
            glSurfaceView.onPause();
        }
    }

    /**
     * 切换成surfaceView
     */
    private void change(){
        /**
         * <SurfaceView
         android:id="@+id/sv_main_demo"
         android:layout_width="match_parent"
         android:layout_height="match_parent" />

         SurfaceView sv = (SurfaceView)findViewById(R.id.sv_main_demo);
         glRenderer = new GLRenderer();
         glRenderer.start();

         sv.getHolder().addCallback(new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {

        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        glRenderer.render(surfaceHolder.getSurface(),width,height);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        }
        });
         @Override
         protected void onDestroy() {
         glRenderer.release();
         glRenderer = null;
         super.onDestroy();
         }
         */
    }



}
