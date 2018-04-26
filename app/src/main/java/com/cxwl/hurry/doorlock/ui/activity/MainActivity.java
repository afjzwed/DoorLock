package com.cxwl.hurry.doorlock.ui.activity;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.cxwl.hurry.doorlock.MainApplication;
import com.cxwl.hurry.doorlock.R;
import com.cxwl.hurry.doorlock.utils.NetWorkUtils;

import java.util.Timer;
import java.util.TimerTask;

import static com.cxwl.hurry.doorlock.utils.MacUtils.getWifiMac;
import static com.cxwl.hurry.doorlock.utils.NetWorkUtils.NETWOKR_TYPE_ETHERNET;
import static com.cxwl.hurry.doorlock.utils.NetWorkUtils.NETWOKR_TYPE_MOBILE;
import static com.cxwl.hurry.doorlock.utils.NetWorkUtils.NETWORK_TYPE_NONE;
import static com.cxwl.hurry.doorlock.utils.NetWorkUtils.NETWORK_TYPE_WIFI;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";

    private Handler mHandle;
    private Messenger mainMessage;
    private Messenger serviceMessage;
    private String mac;//Mac地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initHandle();
        initMainService();

    }

    private void initHandle() {
        mHandle = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    default:
                        break;
                }

            }
        };
        mainMessage = new Messenger(mHandle);
    }

    private void initMainService() {
        Intent intent = new Intent(this, MainService.class);
        bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceMessage = new Messenger(service);
            Log.i(TAG, "连接MainService成功" + (serviceMessage != null));
            if (!NetWorkUtils.isNetworkAvailable(MainActivity.this)) {
                mHandle.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "无网状态");
                    }
                });
            } else {
                Log.i(TAG, "有网");
            }
            sendMainMessager(MainService.MAIN_ACTIVITY_INIT, NetWorkUtils.isNetworkAvailable
                    (MainActivity
                    .this));
            initNet();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /**
     * 通过ServiceMessenger将注册消息发送到Service中的Handler
     */
    private void sendMainMessager(int what, Object o) {
        Message message = Message.obtain();
        message.what = what;
        message.replyTo = mainMessage;
        message.obj = o;
        try {
            serviceMessage.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    /**
     * 使用定时器,每隔5秒获得一次信号强度值
     */
    @SuppressLint("WifiManagerLeak")
    private void initNet() {
        final WifiManager wifiManager = (WifiManager) MainApplication.getApplication()
                .getSystemService(WIFI_SERVICE);//获得WifiManager
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                switch (NetWorkUtils.getCurrentNetType(MainActivity.this)) {
                    case NETWORK_TYPE_WIFI:
                        Log.i(TAG, "NETWORK_TYPE_WIFI");
                        break;
                    case NETWOKR_TYPE_ETHERNET:
                        Log.i(TAG, "NETWOKR_TYPE_ETHERNET");

                        break;
                    case NETWOKR_TYPE_MOBILE:
                        Log.i(TAG, "gprs");
                        break;
                    case NETWORK_TYPE_NONE:
                        Log.i(TAG, "断网");
                    default:
                        break;
                }

            }

        }, 1000, 5000);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_0) {

            Log.e(TAG, "keyCode" + "0");
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_1) {
            Log.e(TAG, "keyCode" + "1");

            return false;
        } else if (keyCode == KeyEvent.KEYCODE_2) {
            Log.e(TAG, "keyCode" + "2");
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_3) {
            Log.e(TAG, "keyCode" + "3");
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_4) {
            Log.e(TAG, "keyCode" + "4");
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_5) {
            Log.e(TAG, "keyCode" + "5");
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_6) {
            Log.e(TAG, "keyCode" + "6");
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_7) {
            Log.e(TAG, "keyCode" + "7");
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_8) {
            Log.e(TAG, "keyCode" + "8");
        } else if (keyCode == KeyEvent.KEYCODE_9) {
            Log.e(TAG, "keyCode" + "9");
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_A) {
            Log.e(TAG, "keyCode " + "A" + "管理处");
            startNewActivity(this, HurryDemoActivity.class, null);
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_B) {
            Log.e(TAG, "keyCode " + "B" + "拨号");
            String wifiMac = getWifiMac();
            Log.e(TAG, "mac " + wifiMac);
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_C) {
            Log.e(TAG, "keyCode " + "C" + "帮助");
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_D) {
            Log.e(TAG, "keyCode " + "D" + "返回");
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_DEL) {
            Log.e(TAG, "keyCode " + "*");
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_STAR) {
            Log.e(TAG, "keyCode" + "*");
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_POUND) {
            Log.e(TAG, "keyCode" + "");
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_F4) {
            Log.e(TAG, "keyCode" + "️➡️");
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_F3) {
            Log.e(TAG, "keyCode" + "⬅️️");
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_F2) {
            Log.e(TAG, "keyCode" + "管理处");
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_F1) {
            Log.e(TAG, "keyCode" + "帮助");
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 强制让控件获取焦点
     *
     * @param view
     */
    private void getFocus(View view) {
        view.setFocusable(true);//普通物理方式获取焦点
        view.setFocusableInTouchMode(true);//触摸模式获取焦点,不是触摸屏啊
        view.requestFocus();//要求获取焦点

        boolean focusable = view.isFocusable();
        Log.e(TAG, "获取焦点 " + focusable);
    }

    /**
     * 强制让控件失去焦点
     *
     * @param view
     */
    private void delFocus(View view) {
        view.setFocusable(false);//普通物理方式获取焦点
        view.setFocusableInTouchMode(false);//触摸模式获取焦点,不是触摸屏啊

        boolean focusable = view.isFocusable();
        Log.e(TAG, "失去焦点 " + focusable);
    }

    private void startNewActivity(Context context, Class<?> clz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(context, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }
}
