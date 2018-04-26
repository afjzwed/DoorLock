package com.cxwl.hurry.doorlock.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.cxwl.hurry.doorlock.R;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";


    private String mac;//Mac地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
            startNewActivity(this, HurryDemoActivity.class,null);
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
     * @param view
     */
    private void delFocus(View view) {
        view.setFocusable(false);//普通物理方式获取焦点
        view.setFocusableInTouchMode(false);//触摸模式获取焦点,不是触摸屏啊

        boolean focusable = view.isFocusable();
        Log.e(TAG, "失去焦点 " + focusable);
    }

    @SuppressLint("WifiManagerLeak")
    protected String getWifiMac() {
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        mac = info.getMacAddress();
        if (mac != null) {
            return mac;
        } else {
            return "";
        }
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
