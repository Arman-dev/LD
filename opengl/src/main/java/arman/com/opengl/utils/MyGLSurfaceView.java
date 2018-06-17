package arman.com.opengl.utils;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import arman.com.opengl.Open1Render;

/**
 * @Author admin.
 * @Time 2018/5/6.
 * @Description:
 */

public class MyGLSurfaceView extends GLSurfaceView{

    public MyGLSurfaceView(Context context) {
        super(context);
        // 设置OpenGL版本
        this.setEGLContextClientVersion(1);
        this.setRenderer(new Open1Render());
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 设置OpenGL版本
        this.setEGLContextClientVersion(1);
        this.setRenderer(new Open1Render());
    }
}
