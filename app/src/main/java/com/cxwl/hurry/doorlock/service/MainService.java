package com.cxwl.hurry.doorlock.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.util.Log;

import com.cxwl.hurry.doorlock.utils.MacUtils;


/**
 * @author xlei
 * @Date 2018/4/24.
 */

public class MainService extends Service {
    private static final String TAG = "MainService";
    public static final int MAIN_ACTIVITY_INIT = 0;
    private String mac;
    private String key;
    private Handler mHandler;
    private Messenger serviceMessage;
    private Messenger mainMessage;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "service启动");
        initHandler();
        initMacKey();
    }

    private void initMacKey() {
        mac = MacUtils.getMac();
        key = mac.replace(":", "");
        Log.i(TAG, "初始化mac=" + mac + "key=" + key);
    }


    private void openCamera() {
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
    }

    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MAIN_ACTIVITY_INIT:
                        mainMessage = msg.replyTo;
                        Boolean obj = (Boolean) msg.obj;
                        Log.i(TAG, "MainActivity初始化完成" + (obj ? "有网" : "没网"));
                        break;
                    default:
                        break;
                }

            }
        };
        serviceMessage = new Messenger(mHandler);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return serviceMessage.getBinder();
    }
}
