package com.arman.demo;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;

/**
 * @Author Ethan.
 * @Time 2018/2/27.
 * @Description:
 */
public class AApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        ARouter.init(this);
    }
}
