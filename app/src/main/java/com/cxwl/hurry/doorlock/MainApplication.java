package com.cxwl.hurry.doorlock;

import android.app.Application;

/**
 * Application
 * Created by William on 2018/4/26.
 */

public class MainApplication  extends Application {

    private static MainApplication application;

    @Override
    public void onCreate() {
        application = this;

//        ArcsoftManager.getInstance().initArcsoft(this);//虹软人脸识别初始化

        super.onCreate();
    }

    public static MainApplication getApplication() {
        return application;
    }
}
