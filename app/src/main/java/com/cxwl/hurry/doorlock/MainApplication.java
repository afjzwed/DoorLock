package com.cxwl.hurry.doorlock;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.cxwl.hurry.doorlock.db.DaoMaster;
import com.cxwl.hurry.doorlock.db.DaoSession;
import com.cxwl.hurry.doorlock.face.ArcsoftManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Application
 * Created by William on 2018/4/26.
 */

public class MainApplication  extends Application implements Thread.UncaughtExceptionHandler{

    private static MainApplication application;

    @Override
    public void onCreate() {
        application = this;

        //人脸识别的数据存本地
         ArcsoftManager.getInstance().initArcsoft(this);//虹软人脸识别初始化

        super.onCreate();

        //okhttp初始化
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor("okhttp",true))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);
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

    // 捕获系统运行停止错误
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        // System.exit(0);

        Intent intent = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());

        PendingIntent restartIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);

        // 退出程序
        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500, restartIntent); // 1秒钟后重启应用

        System.exit(0);

    }
}
