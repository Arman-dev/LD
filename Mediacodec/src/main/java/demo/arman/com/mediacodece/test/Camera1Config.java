package demo.arman.com.mediacodece.test;

import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

/**
 * Created by Ethan on 2018/4/10.
 */

public class Camera1Config {

    private int previewWidth, previewHeight;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;

    public void setSurfaceViewConfig(SurfaceView view){
        this.mSurfaceView=view;
        mSurfaceHolder=mSurfaceView.getHolder();
        mSurfaceHolder.setFixedSize(previewWidth,previewHeight);
        /**
         * 为了充分利用不同平台的资源，发挥平台的最优效果可以通过SurfaceHolder的setType函数来设置绘制的类型
         * SURFACE_TYPE_NORMAL:用RAM缓存原生数据的普通Surface
         * SURFACE_TYPE_HARDWARE:适用于DMA(Direct memory access )引擎和硬件加速的Surface
         * SURFACE_TYPE_GPU:适用于GPU加速的Surface
         * SURFACE_TYPE_PUSH_BUFFERS:表明该Surface不包含原生数据，Surface用到的数据由其他对象提供，
         * 在Camera图像预览中就使用该类型的Surface，有Camera负责提供给预览Surface数据，这样图像预览会比较流畅。如果设置这种类型则就不能调用lockCanvas来获取Canvas对象了
         */
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //接收surface变化的消息
        mSurfaceHolder.addCallback(holderChangeCallback);
    }

    private void startPreview(SurfaceHolder holder){
        try {
            mCamera= Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(mCamera!=null){
            try {
                mCamera.setDisplayOrientation(90);
                mCamera.setPreviewDisplay(mSurfaceHolder);
                Camera.Parameters parameters=mCamera.getParameters();
                //需要通过getSupportedPreviewSizes设置与SurfaceView宽高比例相近，并且大小相近的尺寸,否则预览会失真变形
                List<Camera.Size> mSupportedPreviewSizes = parameters.getSupportedPreviewSizes();
                List<Camera.Size> mSupportedVideoSizes = parameters.getSupportedVideoSizes();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private SurfaceHolder.Callback holderChangeCallback=new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };



    /**
     * 通过对比得到与宽高比最接近的预览尺寸（如果有相同尺寸，优先选择）
     *
     * @param isPortrait 是否竖屏
     * @param surfaceWidth 需要被进行对比的原宽
     * @param surfaceHeight 需要被进行对比的原高
     * @param preSizeList 需要对比的预览尺寸列表
     * @return 得到与原宽高比例最接近的尺寸
     */
    public static  Camera.Size getCloselyPreSize(boolean isPortrait, int surfaceWidth, int surfaceHeight, List<Camera.Size> preSizeList) {
        int reqTmpWidth;
        int reqTmpHeight;
        // 当屏幕为垂直的时候需要把宽高值进行调换，保证宽大于高
        if (isPortrait) {
            reqTmpWidth = surfaceHeight;
            reqTmpHeight = surfaceWidth;
        } else {
            reqTmpWidth = surfaceWidth;
            reqTmpHeight = surfaceHeight;
        }
        //先查找preview中是否存在与surfaceview相同宽高的尺寸
        for(Camera.Size size : preSizeList){
            if((size.width == reqTmpWidth) && (size.height == reqTmpHeight)){
                return size;
            }
        }

        // 得到与传入的宽高比最接近的size
        float reqRatio = ((float) reqTmpWidth) / reqTmpHeight;
        float curRatio, deltaRatio;
        float deltaRatioMin = Float.MAX_VALUE;
        Camera.Size retSize = null;
        for (Camera.Size size : preSizeList) {
            curRatio = ((float) size.width) / size.height;
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin) {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
        }

        return retSize;
    }
}
