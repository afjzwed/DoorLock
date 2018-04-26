package com.cxwl.hurry.doorlock.ui.activity;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cxwl.hurry.doorlock.MainApplication;
import com.cxwl.hurry.doorlock.R;
import com.cxwl.hurry.doorlock.service.MainService;
import com.cxwl.hurry.doorlock.utils.Intenet;
import com.cxwl.hurry.doorlock.utils.MacUtils;
import com.cxwl.hurry.doorlock.utils.NetWorkUtils;

import java.util.Timer;
import java.util.TimerTask;

import static com.cxwl.hurry.doorlock.utils.NetWorkUtils.NETWOKR_TYPE_ETHERNET;
import static com.cxwl.hurry.doorlock.utils.NetWorkUtils.NETWOKR_TYPE_MOBILE;
import static com.cxwl.hurry.doorlock.utils.NetWorkUtils.NETWORK_TYPE_NONE;
import static com.cxwl.hurry.doorlock.utils.NetWorkUtils.NETWORK_TYPE_WIFI;

/**
 * MainActivity
 * Created by William on 2018/4/26
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = "MainActivity";

    private TextView version_text;//版本名显示
    private View container;//根View
    private LinearLayout videoLayout;
    private RelativeLayout rl_nfc, rl;//录卡布局和网络检测提示布局
    private GridView mGridView;
    private ImageView iv_setting, bluetooth_image, iv_bind, imageView, wifi_image;
    private TextView headPaneTextView, tv_message, tv_battery, showMacText;
    private EditText tv_input, et_blackno, et_unitno, tv_input_text;

    private Handler mHandle;
    private Messenger mainMessage;
    private Messenger serviceMessage;
    private String mac;//Mac地址
    private boolean isFlag = true;//录卡时楼栋编号焦点监听的标识


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();//初始化View

        initHandle();
        initMainService();

    }

    /**
     * 初始化view
     */
    public void initView() {
        version_text = (TextView) findViewById(R.id.version_text);
        version_text.setText(getVersionName());
        container = findViewById(R.id.container);
        rl_nfc = (RelativeLayout) findViewById(R.id.rl_nfc);//录卡布局
        et_blackno = (EditText) findViewById(R.id.et_blockno);//录卡时楼栋编号
        et_unitno = (EditText) findViewById(R.id.et_unitno);//录卡时房屋编号
        iv_bind = (ImageView) findViewById(R.id.user_bind);//QQ物联标志
        imageView = (ImageView) findViewById(R.id.iv_erweima);//二维码
        wifi_image = (ImageView) findViewById(R.id.wifi_image); //wifi图标控件初始化
        iv_setting = (ImageView) findViewById(R.id.iv_setting);//左上角弹出菜单按钮
        bluetooth_image = (ImageView) findViewById(R.id.bluetooth_image);//蓝牙按钮
        tv_message = (TextView) findViewById(R.id.tv_message);//录入卡提示信息
        tv_input_text = (EditText) findViewById(R.id.tv_input_text);//桌面会话状态的提示信息
        tv_battery = (TextView) findViewById(R.id.tv_battery);//显示蓝牙锁的电量
        mGridView = (GridView) findViewById(R.id.gridView_binderlist);//QQ物联相关（应该用于显示绑定用户
        rl = (RelativeLayout) findViewById(R.id.net_view_rl);//网络检测提示布局
        showMacText = (TextView) findViewById(R.id.show_mac);//mac地址
        tv_input = (EditText) findViewById(R.id.tv_input);//完全不知道干嘛用

        //getBgBanners();// 网络获得轮播背景图片数据

        rl.setOnClickListener(this);
        iv_setting.setOnClickListener(this);

        //QQ物联相关，注释
//        mAdapter = new BinderListAdapter(this);
//        mGridView.setAdapter(mAdapter);

        sendBroadcast(new Intent("com.android.action.hide_navigationbar"));//隱藏底部導航
        container.setSystemUiVisibility(13063);//设置状态栏显示与否,禁止頂部下拉

//        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/GBK.TTF");//字体设置
//        tv_input.setTypeface(typeFace);// com_log.setTypeface(typeFace);
        et_blackno.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    isFlag = true;
                } else {
                    isFlag = false;
                }
            }
        });

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

    /**
     * 获取版本名
     *
     * @return
     */
    private String getVersionName() {
        String verName = "";
        try {
            verName = this.getPackageManager().
                    getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return verName;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {//跳转网络或网络设置
            case R.id.net_view_rl: {

                break;
            }
            case R.id.iv_setting: {
//                initMenu();//初始化左上角弹出框
                break;
            }
        }
    }
}
