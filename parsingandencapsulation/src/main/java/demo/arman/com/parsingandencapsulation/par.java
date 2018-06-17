package demo.arman.com.parsingandencapsulation;

import android.os.Environment;

import java.io.File;

/**
 * @Author admin.
 * @Time 2018/2/20.
 * @Description:
 */

public class par {

    File file;

    public void Ge(){
        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/我是渣渣辉.mp4");
//        VideoPlayer player=new VideoPlayer();
    }
}
