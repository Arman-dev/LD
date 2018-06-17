package demo.arman.com.mediacodece.test;

import android.graphics.Camera;
import android.media.CamcorderProfile;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import demo.arman.com.mediacodece.R;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mediacodec_activity_main2);
        //保存音视频配置信息
//        CamcorderProfile
        initCamera();
    }

    private void initCamera() {
    }
}
