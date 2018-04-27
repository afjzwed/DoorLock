package com.cxwl.hurry.doorlock;

import android.app.Application;

import com.cxwl.hurry.doorlock.db.DaoMaster;
import com.cxwl.hurry.doorlock.db.DaoSession;

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
    static DaoSession mDaoSessin;
    public static DaoSession getGreenDaoSession() {
        if (mDaoSessin == null) {
            DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(application, "door-db", null);
            DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
            mDaoSessin = daoMaster.newSession();
        }
        return mDaoSessin;
    }
    public static MainApplication getApplication() {
        return application;
    }
}
