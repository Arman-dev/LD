package demo.arman.com.mediacodece.test;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by Ethan on 2018/4/9.
 */

public class utils {

    /**
     * 检查相机在运行时是否可用
     * @param context
     * @return
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
}
