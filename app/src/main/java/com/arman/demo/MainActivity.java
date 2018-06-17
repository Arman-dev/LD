package com.arman.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

/**
 *
 */

@Route(path = "/app/MainActivity")
public class MainActivity extends AppCompatActivity {

    private String mkey="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void click(View view){
        try {
            switch (view.getId()){
                case R.id.btn_audio:
                    mkey="/audiolibrary/AudioActivity";
                    break;
                case R.id.btn_codec:
                    mkey="/MediaCodec/MediaCodecActivity";
                    break;
                case R.id.btn_opengl:
                    mkey="/opengl/OpenGLTestActivity";
                    break;
            }
            go();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this,"ERROR!"+e.getMessage(),Toast.LENGTH_LONG);
        }
    }

    private void go(){//
        if(TextUtils.isEmpty(mkey)){
            Toast.makeText(MainActivity.this,"ERROR!",Toast.LENGTH_LONG);
            return;
        }
        ARouter.getInstance().build(mkey)
//                .withLong("key1", 666L)
//                .withString("key3", "888")
//                        .withObject("key4", new Test("Jack", "Rose"))
                .navigation();
    }
}
