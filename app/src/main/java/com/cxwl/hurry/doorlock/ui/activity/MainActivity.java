package com.cxwl.hurry.doorlock.ui.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.AudioManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidex.aexapplibs.appLibsService;
import com.cxwl.hurry.doorlock.MainApplication;
import com.cxwl.hurry.doorlock.R;
import com.cxwl.hurry.doorlock.config.DeviceConfig;
import com.cxwl.hurry.doorlock.interfac.TakePictureCallback;
import com.cxwl.hurry.doorlock.service.MainService;
import com.cxwl.hurry.doorlock.utils.Ajax;
import com.cxwl.hurry.doorlock.utils.HttpApi;
import com.cxwl.hurry.doorlock.utils.HttpUtils;
import com.cxwl.hurry.doorlock.utils.NetWorkUtils;
import com.cxwl.hurry.doorlock.utils.NfcReader;
import com.cxwl.hurry.doorlock.utils.UploadUtil;
import com.qiniu.android.common.FixedZone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import jni.util.Utils;

import static com.cxwl.hurry.doorlock.utils.Constant.CALLING_MODE;
import static com.cxwl.hurry.doorlock.utils.Constant.CALL_MODE;
import static com.cxwl.hurry.doorlock.utils.Constant.MSG_CALLMEMBER_ERROR;
import static com.cxwl.hurry.doorlock.utils.Constant.MSG_CALLMEMBER_NO_ONLINE;
import static com.cxwl.hurry.doorlock.utils.Constant.MSG_CALLMEMBER_SERVER_ERROR;
import static com.cxwl.hurry.doorlock.utils.Constant.MSG_CALLMEMBER_TIMEOUT;
import static com.cxwl.hurry.doorlock.utils.Constant.MSG_CANCEL_CALL;
import static com.cxwl.hurry.doorlock.utils.Constant.MSG_INPUT_CARDINFO;
import static com.cxwl.hurry.doorlock.utils.Constant.MSG_LOGIN_AFTER;
import static com.cxwl.hurry.doorlock.utils.Constant.MSG_RTC_DISCONNECT;
import static com.cxwl.hurry.doorlock.utils.Constant.MSG_RTC_NEWCALL;
import static com.cxwl.hurry.doorlock.utils.Constant.MSG_RTC_ONVIDEO;
import static com.cxwl.hurry.doorlock.utils.Constant.MSG_RTC_REGISTER;
import static com.cxwl.hurry.doorlock.utils.Constant.ONVIDEO_MODE;
import static com.cxwl.hurry.doorlock.utils.Constant.PASSWORD_CHECKING_MODE;
import static com.cxwl.hurry.doorlock.utils.NetWorkUtils.NETWOKR_TYPE_ETHERNET;
import static com.cxwl.hurry.doorlock.utils.NetWorkUtils.NETWOKR_TYPE_MOBILE;
import static com.cxwl.hurry.doorlock.utils.NetWorkUtils.NETWORK_TYPE_NONE;
import static com.cxwl.hurry.doorlock.utils.NetWorkUtils.NETWORK_TYPE_WIFI;
import static com.cxwl.hurry.doorlock.utils.NfcReader.ACTION_NFC_CARDINFO;

/**
 * MainActivity
 * Created by William on 2018/4/26
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        TakePictureCallback, NfcReader.AccountCallback, NfcAdapter.ReaderCallback {

    private static String TAG = "MainActivity";
    public static final int MSG_RTC_ONVIDEO_IN = 10011;//接收到视频呼叫
    public static final int MSG_ADVERTISE_IMAGE = 20001;//跟新背景图片
    public static int currentStatus = CALL_MODE;//当前状态

    private TextView version_text;//版本名显示
    private View container;//根View
    private LinearLayout videoLayout;
    private RelativeLayout rl_nfc, rl;//录卡布局和网络检测提示布局
    private GridView mGridView;
    private ImageView iv_setting, bluetooth_image, iv_bind, imageView, wifi_image;
    private TextView headPaneTextView, tv_message, tv_battery, showMacText;
    private EditText tv_input, et_blackno, et_unitno, tv_input_text;

    private Handler handler;
    private Messenger mainMessage;
    private Messenger serviceMessage;//Service端的Messenger
    private String mac;//Mac地址
    private boolean isFlag = true;//录卡时楼栋编号焦点监听的标识
    private NfcReader nfcReader;//用于nfc卡扫描

    private SurfaceView localView = null;//rtc本地摄像头view
    private SurfaceView remoteView = null;//rtc远端视频view
    private String blockNo = "";//输入的房号
    private HashMap<String, String> uuidMaps = new HashMap<String, String>();//储存uuid
    private String lastImageUuid = ""; //与拍照图片相对应

    private Camera camera = null;
    private SurfaceHolder autoCameraHolder = null;
    private SurfaceView autoCameraSurfaceView = null;
    private boolean mCamerarelease = true; //判断照相机是否释放

    public appLibsService hwservice;//hwservice为安卓工控appLibs的服务

    private String cardId;//卡ID
    private boolean nfcFlag = false;//录卡页面是否显示(即是否录卡)的标识,默认false
    private Receive receive; //本地广播
    private int netWorkFlag = -1;//当前网络是否可用标识 有网为1 无网为0
    private Timer netTimer = new Timer();//检测网络用定时器
    private boolean checkTime = false;//是否校时过的标识,没有重置为false操作

    private WifiInfo wifiInfo = null;//获得的Wifi信息
    private int level;//信号强度值
    private WifiManager wifiManager = null;//Wifi管理器

    private UploadManager uploadManager;//七牛上传

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: 2018/5/8  此处hwservice实例化没以MainActivity继承AndroidExActivityBase实现
        this.hwservice = new appLibsService(this);
        super.onCreate(savedInstanceState);

        //全屏设置，隐藏窗口所有装饰
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);//清除FLAG
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager
                .LayoutParams.FLAG_FULLSCREEN);

        {
            ActionBar ab = getActionBar();
            if (ab != null) ab.setDisplayHomeAsUpEnabled(true);//左上角显示应用程序图标
        }
        setContentView(R.layout.activity_main);
        hwservice.EnterFullScreen();//hwservice为appLibs的服务

        initView();//初始化View
        initQiniu();
        initScreen();
        initHandle();
        initAexNfcReader();//初始化nfc本地广播
        initMainService();
        initVoiceVolume();//初始化音量设置
        initAutoCamera();


        initNet();

    }

    private void initQiniu() {
        Configuration config = new Configuration.Builder().chunkSize(512 * 1024)        // 分片上传时，每片的大小。 默认256K
                .putThreshhold(1024 * 1024)   // 启用分片上传阀值。默认512K
                .connectTimeout(10)           // 链接超时。默认10秒
                .useHttps(true)               // 是否使用https上传域名
                .responseTimeout(60)          // 服务器响应超时。默认60秒
                //  .recorder(recorder)           // recorder分片上传时，已上传片记录器。默认null
                //   .recorder(recorder, keyGen)   // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
                .zone(FixedZone.zone2)        // 设置区域，指定不同区域的上传域名、备用域名、备用IP。
                .build();
        // 实例化一个上传的实例
        uploadManager = new UploadManager(config);
    }

    /**
     * 初始化音量设置
     */
    protected void initVoiceVolume() {
        AudioManager audioManager = (AudioManager) getSystemService(this.AUDIO_SERVICE);
        initVoiceVolume(audioManager, AudioManager.STREAM_MUSIC, DeviceConfig.VOLUME_STREAM_MUSIC);
        initVoiceVolume(audioManager, AudioManager.STREAM_RING, DeviceConfig.VOLUME_STREAM_RING);
        initVoiceVolume(audioManager, AudioManager.STREAM_SYSTEM, DeviceConfig
                .VOLUME_STREAM_SYSTEM);
        initVoiceVolume(audioManager, AudioManager.STREAM_VOICE_CALL, DeviceConfig
                .VOLUME_STREAM_VOICE_CALL);
    }

    /**
     * 设置具体音量
     *
     * @param audioManager
     * @param type
     * @param value
     */
    protected void initVoiceVolume(AudioManager audioManager, int type, int value) {
        int thisValue = audioManager.getStreamMaxVolume(type);//得到最大音量
        thisValue = thisValue * value / 10;//具体音量值
        audioManager.setStreamVolume(type, thisValue, AudioManager.FLAG_PLAY_SOUND);//调整音量时播放声音
    }

    /**
     * 初始化nfc阅读器
     */
    private void initAexNfcReader() {
        //nfc系统默认有效
        nfcReader = new NfcReader(this);
        //enableReaderMode(); //xiaozd add
        receive = new Receive();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_NFC_CARDINFO);//NFC读取到卡片信息
        registerReceiver(receive, intentFilter);
    }

    /**
     * 初始化视频通话布局(用于天翼rtc？)
     */
    protected void initScreen() {
        //callLayout=(LinearLayout) findViewById(R.id.call_pane);
        //guestLayout=(LinearLayout) findViewById(R.id.guest_pane);
        headPaneTextView = (TextView) findViewById(R.id.header_pane);//可视对讲设备状态
        videoLayout = (LinearLayout) findViewById(R.id.ll_video);//用于添加视频通话的根布局

//        videoPane = (LinearLayout) findViewById(R.id.video_pane);
//        imagePane = (LinearLayout) findViewById(R.id.image_pane);
//        remoteLayout = (LinearLayout) findViewById(R.id.ll_remote);

        setTextView(R.id.tv_community, MainService.communityName);
        setTextView(R.id.tv_lock, MainService.lockName);
    }

    /**
     * 初始化照相机的surefaceview
     */
    protected void initAutoCamera() {
        Log.v("MainActivity", "initAutoCamera-->");
        autoCameraSurfaceView = (SurfaceView) findViewById(R.id.autoCameraSurfaceview);
        autoCameraHolder = autoCameraSurfaceView.getHolder();
        autoCameraHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
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
        videoLayout = (LinearLayout) findViewById(R.id.ll_video);//用于添加视频通话的根布局
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

    /**
     * 初始化handler
     */
    private void initHandle() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_LOGIN_AFTER:
                        //登陆成功后 设置信息 初始化rtc
                        Log.i(TAG, "登陆成功后");
                        onLoginAfter(msg);
                        break;
                    case MSG_RTC_ONVIDEO:
                        //接通视频通话
                        Log.i(TAG, "接通视频通话");
                        onRtcVideoOn();
                        break;
                    case MSG_RTC_DISCONNECT:
                        //视频通话断开
                        Log.i(TAG, "视频通话断开");
                        onRtcDisconnect();
                        //启动人脸识别
//                        if (faceHandler != null) {
//                            faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_CONTRAST, 3000);
//                        }
                        break;
                    case MSG_RTC_NEWCALL:
                        //收到新的来电
                        Log.i(TAG, "收到新的来电");
                        onRtcConnected();
                        break;
                    case MSG_CALLMEMBER_TIMEOUT:
                        Log.e(TAG, "呼叫超时");
                        onCallMemberError(msg.what);
                        break;
                    case MSG_CALLMEMBER_NO_ONLINE:
                        Log.e(TAG, "呼叫用户不在线");
                        onCallMemberError(msg.what);
                        break;
                    case MSG_INPUT_CARDINFO:
                        Log.e(TAG, "重复录卡");
                        String obj = (String) msg.obj;
                        tv_message.setText(obj);
                        break;
                    default:
                        break;
                }

            }
        };
        mainMessage = new Messenger(handler);
    }

    /**
     * 登录成功后
     * @param msg
     */
    private void onLoginAfter(Message msg) {
        if (msg.obj != null) {
            JSONObject result = (JSONObject) msg.obj;
            try {
                int code = result.getInt("code");
                if (code == 0) {//登录成功
                    //初始化token
                    sendMainMessager(MSG_RTC_REGISTER, null);
                    //初始化社区信息
                    JSONObject user = result.getJSONObject("user");
                    setCommunityName(user.getString("communityName"));
                    setLockName(user.getString("lockName"));

                    enableReaderMode(); //登录成功后开启读卡
                    Log.e(TAG, "可以读卡");

                    // TODO: 2018/5/8 登录成功后人脸识别对比开启

                } else if (code == 1) { //登录失败,MAC地址不存在服务器
                    //显示MAC地址并提示添加
                    showMacaddress(result.getString("mac"));
                }
            } catch (Exception e) {

            }
        }
    }

    private void initMainService() {
        Intent intent = new Intent(this, MainService.class);
        bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);

        // TODO: 2018/5/8 开门服务类暂时注释
//        Intent dlIntent = new Intent(MainActivity.this, DoorLock.class);
//        startService(dlIntent);//start方式启动锁Service
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //获取Service端的Messenger
            serviceMessage = new Messenger(service);
            Log.i(TAG, "连接MainService成功" + (serviceMessage != null));
            netWorkFlag = NetWorkUtils.isNetworkAvailable(MainActivity.this) ? 1 : 0;
            if (netWorkFlag == 0) {
                enableReaderMode();//无网时打开读卡
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "无网状态");
                        // TODO: 2018/5/8    rl.setVisibility(View.VISIBLE);//界面上显示无网提示
                    }
                });
            } else {
                Log.i(TAG, "有网");
                // TODO: 2018/5/8   setStatusBarIcon(true); initSystemtime();
            }
            sendMainMessager(MainService.MAIN_ACTIVITY_INIT, NetWorkUtils.isNetworkAvailable(MainActivity
                    .this));
            initNetListen();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /**
     * 每隔一秒检查一次网络是否可用
     */
    private void initNetListen() {
        netTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                int s = NetWorkUtils.isNetworkAvailable(MainActivity.this) ? 1 : 0;
                if (s != netWorkFlag) {//如果当前网络状态与之前不一致
                    if (s == 1) {//当前有网，之前没网
                        //关闭读卡
                        disableReaderMode();//没网时打开过一次
                        //时间更新
                        initSystemtime();
                    } else {//当前没网，之前有网
                        enableReaderMode(); //打开读卡
                    }
                    sendMainMessager(MainService.MSG_UPDATE_NETWORKSTATE, s == 1 ? true : false);
                    netWorkFlag = s;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            // TODO: 2018/5/8 下面暂时注释
//                            if (netWorkFlag == 1) {
//                                setStatusBarIcon(true);
//                                rl.setVisibility(View.GONE);
//                            } else {
//                                setStatusBarIcon(false);
//                                rl.setVisibility(View.VISIBLE);
//                            }
                        }
                    });
                }
            }
        }, 500, 1000);
    }

    /**
     * 校时
     */
    private void initSystemtime() {
        if (NetWorkUtils.isNetworkAvailable(this) && !checkTime) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Calendar c = HttpApi.getInstance().loadTime();
                    if (c != null) {
                        if (checkTime(c)) {
                            SimpleDateFormat d = new SimpleDateFormat("yyyyMMdd.HHmmss");
                            String cmd = "date -s '[_update_time]'";
                            String time = d.format(c.getTime());
                            cmd = cmd.replace("[_update_time]", time);
                            hwservice.execRootCommand(cmd);
                            checkTime = true;
                            HttpApi.e("时间更新：" + time);
                        } else {
                            HttpApi.e("系统与服务器时间差小，不更新");
                        }
                    } else {
                        HttpApi.i("获取服务器时间出错！");
                    }

                }
            }).start();
        }
    }

    private boolean checkTime(Calendar c) {
        Calendar c1 = Calendar.getInstance();
        long abs = Math.abs(c.getTimeInMillis() - c1.getTimeInMillis());
        if (abs > 1 * 60 * 1000) {
            return true;
        }
        return false;
    }

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
        unregisterReceiver(receive);
    }

    /**
     * 使用定时器,每隔5秒获得一次信号强度值
     */
    @SuppressLint("WifiManagerLeak")
    private void initNet() {
        wifiManager  =(WifiManager) MainApplication.getApplication().getSystemService(WIFI_SERVICE);//获得WifiManager
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                switch (NetWorkUtils.getCurrentNetType(MainActivity.this)) {
                    case NETWORK_TYPE_WIFI:
                        //  Log.i(TAG, "NETWORK_TYPE_WIFI");
                        wifiInfo = wifiManager.getConnectionInfo();
                        //获得信号强度值
                        level = wifiInfo.getRssi();
                        //根据获得的信号强度发送信息
                        if (level <= 0 && level >= -50) {
                            Message msg = new Message();
                            msg.what = 1;
                            mHandler.sendMessage(msg);
                        } else if (level < -50 && level >= -70) {
                            Message msg = new Message();
                            msg.what = 2;
                            mHandler.sendMessage(msg);
                        } else if (level < -70 && level >= -80) {
                            Message msg = new Message();
                            msg.what = 3;
                            mHandler.sendMessage(msg);
                        } else if (level < -80 && level >= -100) {
                            Message msg = new Message();
                            msg.what = 4;
                            mHandler.sendMessage(msg);
                        } else {
                            Message msg = new Message();
                            msg.what = 5;
                            mHandler.sendMessage(msg);
                        }
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
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            int keyCode = event.getKeyCode();
            onKeyDown(keyCode);
        }
        return false;
    }

    private void onKeyDown(int keyCode) {
        int key = convertKeyCode(keyCode);
        if (key >= 0 && key != 10 && key != 11) {
            //数字键
            if (currentStatus == CALL_MODE) {
                input(key);
            }
        } else if (key == 10) {
            //确定键
            if (blockNo.length() == 0) {
                //表示输入密码访问
                toast("输入密码");
            } else if (blockNo.length() == 4) {
                //长度等于4并且按下确定键 表示呼叫房号
                startDialing(blockNo);
                toast("开始进行呼叫房号");
            } else if (blockNo.length() == 11) {
                //长度等于11并且按下确定键 表示呼叫手机号
                toast("开始进行呼叫手机号");
            } else {
                toast("此房号或电话号码不存在");
            }
        } else if (key == 11) {
            //删除键
            if (currentStatus == CALLING_MODE) {
                startCancelCall();
            }
            if (blockNo.length() > 0) {
                //删除当前一个数字
                delInput();
            }
        }
    }

    /**
     * 呼叫出现错误
     *
     * @param reason
     */

    protected void onCallMemberError(int reason) {
        blockNo = "";
        setDialValue("");
        setCurrentStatus(CALL_MODE);
        if (reason == MSG_CALLMEMBER_ERROR) {
            Utils.DisplayToast(MainActivity.this, "您呼叫的房间号错误或者无注册用户");
            Log.v("MainActivity", "无用户取消呼叫");
            clearImageUuidAvaible(lastImageUuid);
        } else if (reason == MSG_CALLMEMBER_TIMEOUT) {
            Utils.DisplayToast(MainActivity.this, "您呼叫的房间号无人应答");
        } else if (reason == MSG_CALLMEMBER_SERVER_ERROR) {
            Utils.DisplayToast(MainActivity.this, "无法从服务器获取住户信息，请联系管理处");
        }
//        else if (reason == MSG_CALLMEMBER_DIRECT_TIMEOUT) {
//            Utils.DisplayToast(MainActivity.this, "您呼叫的房间直拨电话无人应答");
//        }else if (reason == MSG_CALLMEMBER_NO_ONLINE) {
//            Utils.DisplayToast(MainActivity.this, "您呼叫的房间号无人在线");
//        }
        //启动人脸识别
//        if (faceHandler != null) {
//            faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_CONTRAST, 1000);
//        }
    }

    protected void startCancelCall() {
        new Thread() {
            @Override
            public void run() {
                //  stopCallCamera();
                try {
                    sleep(1000);
                } catch (Exception e) {
                }
                sendMainMessager(MSG_CANCEL_CALL, "");
//                if (faceHandler != null) {
//                    faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_CONTRAST, 1000);
//                }
                try {
                    sleep(1000);
                } catch (Exception e) {
                }
                toast("您已经取消拨号");
                resetDial();
            }
        }.start();
    }

    private void startDialing(String blockNo) {
        //呼叫前，确认摄像头不被占用 红软


        Log.i(TAG, "拍摄访客照片 并进行呼叫" + blockNo);
        setCurrentStatus(CALLING_MODE);
        //拍摄访客照片 并进行呼叫
        takePicture(blockNo, true, MainActivity.this);
    }

    private void input(int key) {
        blockNo = blockNo + key;
        Log.i(TAG, "input  blockNo=" + blockNo);
        setDialValue(blockNo);
    }

    private void delInput() {
        Log.i(TAG, "delInput  blockNo=" + blockNo);
        blockNo = blockNo.substring(0, blockNo.length() - 1);
        setDialValue(blockNo);
    }

    private int convertKeyCode(int keyCode) {
        int value = -1;
        if ((keyCode == KeyEvent.KEYCODE_0)) {
            value = 0;
        } else if ((keyCode == KeyEvent.KEYCODE_1)) {
            value = 1;
        } else if ((keyCode == KeyEvent.KEYCODE_2)) {
            value = 2;
        } else if ((keyCode == KeyEvent.KEYCODE_3)) {
            value = 3;
        } else if ((keyCode == KeyEvent.KEYCODE_4)) {
            value = 4;
        } else if ((keyCode == KeyEvent.KEYCODE_5)) {
            value = 5;
        } else if ((keyCode == KeyEvent.KEYCODE_6)) {
            value = 6;
        } else if ((keyCode == KeyEvent.KEYCODE_7)) {
            value = 7;
        } else if ((keyCode == KeyEvent.KEYCODE_8)) {
            value = 8;
        } else if ((keyCode == KeyEvent.KEYCODE_9)) {
            value = 9;
        } else if (keyCode == 66) {
            value = 10;//确定键
        } else if (keyCode == 67) {
            value = 11;//删除键
        }
        return value;
    }

    //    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_0) {
//
//            Log.e(TAG, "keyCode" + "0");
//            return false;
//        } else if (keyCode == KeyEvent.KEYCODE_1) {
//            Log.e(TAG, "keyCode" + "1");
//
//            return false;
//        } else if (keyCode == KeyEvent.KEYCODE_2) {
//            Log.e(TAG, "keyCode" + "2");
//            return false;
//        } else if (keyCode == KeyEvent.KEYCODE_3) {
//            Log.e(TAG, "keyCode" + "3");
//            return false;
//        } else if (keyCode == KeyEvent.KEYCODE_4) {
//            Log.e(TAG, "keyCode" + "4");
//            return false;
//        } else if (keyCode == KeyEvent.KEYCODE_5) {
//            Log.e(TAG, "keyCode" + "5");
//            return false;
//        } else if (keyCode == KeyEvent.KEYCODE_6) {
//            Log.e(TAG, "keyCode" + "6");
//            return false;
//        } else if (keyCode == KeyEvent.KEYCODE_7) {
//            Log.e(TAG, "keyCode" + "7");
//            return false;
//        } else if (keyCode == KeyEvent.KEYCODE_8) {
//            Log.e(TAG, "keyCode" + "8");
//        } else if (keyCode == KeyEvent.KEYCODE_9) {
//            Log.e(TAG, "keyCode" + "9");
//            return false;
//        } else if (keyCode == KeyEvent.KEYCODE_A) {
//            Log.e(TAG, "keyCode " + "A" + "管理处");
//            startNewActivity(this, HurryDemoActivity.class, null);
//            return false;
//        } else if (keyCode == KeyEvent.KEYCODE_B) {
//            Log.e(TAG, "keyCode " + "B" + "拨号");
//            return false;
//        } else if (keyCode == KeyEvent.KEYCODE_C) {
//            Log.e(TAG, "keyCode " + "C" + "帮助");
//            return false;
//        } else if (keyCode == KeyEvent.KEYCODE_D) {
//            Log.e(TAG, "keyCode " + "D" + "返回");
//            return false;
//        } else if (keyCode == KeyEvent.KEYCODE_DEL) {
//            Log.e(TAG, "keyCode " + "*");
//            return false;
//        } else if (keyCode == KeyEvent.KEYCODE_STAR) {
//            Log.e(TAG, "keyCode" + "*");
//            return false;
//        } else if (keyCode == KeyEvent.KEYCODE_POUND) {
//            Log.e(TAG, "keyCode" + "");
//            return false;
//        } else if (keyCode == KeyEvent.KEYCODE_F4) {
//            Log.e(TAG, "keyCode" + "️➡️");
//            return false;
//        } else if (keyCode == KeyEvent.KEYCODE_F3) {
//            Log.e(TAG, "keyCode" + "⬅️️");
//            return false;
//        } else if (keyCode == KeyEvent.KEYCODE_F2) {
//            Log.e(TAG, "keyCode" + "管理处");
//            return false;
//        } else if (keyCode == KeyEvent.KEYCODE_F1) {
//            Log.e(TAG, "keyCode" + "帮助");
//            return false;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

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

    /****************************天翼rtc********************/
    public void onRtcDisconnect() {
        Log.i(TAG, "重置房号为空 设置为 呼叫模式状态");
        blockNo = "";
        setDialValue(blockNo);
        setCurrentStatus(CALL_MODE);
        //启动广告
        //    advertiseHandler.start(adverErrorCallBack);

        videoLayout.setVisibility(View.INVISIBLE);
        setVideoSurfaceVisibility(View.INVISIBLE);
    }

    public void onRtcConnected() {
        setCurrentStatus(ONVIDEO_MODE);
        setDialValue("");
        //暂时停止广告播放
        // advertiseHandler.pause(adverErrorCallBack);
    }

    public void onRtcVideoOn() {
        setDialValue("正在和" + blockNo + "视频通话");
        initVideoViews();
        Log.e(TAG, "开始创建remoteView");
        MainService.callConnection.buildVideo(remoteView);
        setVideoSurfaceVisibility(View.VISIBLE);
    }

    /**
     * 创建本地view和远端
     */
    void initVideoViews() {
        //创建本地view
        if (localView != null) {
            return;
        }
        if (MainService.callConnection != null) {
            localView = (SurfaceView) MainService.callConnection.createVideoView(true, this, true);
        }
        if (localView != null) {
            localView.setVisibility(View.INVISIBLE);
            videoLayout.addView(localView);
            localView.setKeepScreenOn(true);
            localView.setZOrderMediaOverlay(true);
            localView.setZOrderOnTop(true);
        }
        //创建远端view
        if (MainService.callConnection != null) {
            remoteView = (SurfaceView) MainService.callConnection.createVideoView(false, this,
                    true);
        }
        if (remoteView != null) {
            remoteView.setVisibility(View.INVISIBLE);
            remoteView.setKeepScreenOn(true);
            remoteView.setZOrderMediaOverlay(true);
            remoteView.setZOrderOnTop(true);
        }
    }

    /**
     * 显示本地view和远端view
     *
     * @param visible
     */
    private void setVideoSurfaceVisibility(int visible) {
        if (localView != null) {
            localView.setVisibility(visible);
        }
        if (remoteView != null) {
            remoteView.setVisibility(visible);
        }
    }

    /****************************天翼rtc********************/

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
/****************************拍照相关************************/
    /**
     * 开始启动拍照
     */
    protected void takePicture(final String thisValue, final boolean isCall, final
    TakePictureCallback callback) {
        if (currentStatus == CALLING_MODE || currentStatus == PASSWORD_CHECKING_MODE) {
            final String uuid = getUUID(); //随机生成UUID
            lastImageUuid = uuid;
            setImageUuidAvaibale(uuid);
            callback.beforeTakePickture(thisValue, isCall, uuid);
            Log.v("MainActivity", "开始启动拍照");
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    final String thisUuid = uuid;
                    if (checkTakePictureAvailable(thisUuid)) {
                        doTakePicture(thisValue, isCall, uuid, callback);
                    } else {
                        Log.v("MainActivity", "取消拍照");
                    }
                }
            }.start();
        }
    }

    private synchronized void doTakePicture(final String thisValue, final boolean isCall, final String uuid, final
    TakePictureCallback callback) {
        mCamerarelease = false;
        try {
            camera = Camera.open();

        } catch (Exception e) {
        }
        Log.v("MainActivity", "打开相机");
        if (camera == null) {
            try {
                camera = Camera.open(0);
            } catch (Exception e) {
            }
        }
        if (camera != null) {
            try {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setPreviewSize(320, 240);
                try {
                    camera.setParameters(parameters);
                } catch (Exception err) {
                    err.printStackTrace();
                }
                camera.setPreviewDisplay(autoCameraHolder);
                camera.startPreview();
                camera.autoFocus(null);
                Log.v("MainActivity", "开始拍照");
                camera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        try {
                            Log.v("MainActivity", "拍照成功");
                            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                            final File file = new File(Environment.getExternalStorageDirectory(), System
                                    .currentTimeMillis() + ".jpg");
                            FileOutputStream outputStream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                            outputStream.close();
                            final String url = DeviceConfig.SERVER_URL + "/app/upload/image";
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                            String date = sdf.format(new java.util.Date());
                            final String curUrl = "upload/menjin/img/" + "android_" + date ;
                            if (checkTakePictureAvailable(uuid)) {
                                new Thread() {
                                    @Override
                                    public void run() {
                                        Log.i(TAG, "开始上传照片");
                                        String s = HttpApi.getInstance().loadHttpforGet(DeviceConfig.GET_QINIUTOKEN,
                                                "");
                                        JSONObject jsonObject = Ajax.getJSONObject(s);
                                        String token = "";
                                        try {
                                            token = (String) jsonObject.get("uptoken");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        Log.e(TAG, "Token==" + token);
                                        Log.e(TAG, "file七牛储存地址："+curUrl);
                                        Log.e(TAG, "file本地地址："+file.getPath()+"file大小"+file.length());
                                        uploadManager.put(file.getPath(), curUrl, token, new UpCompletionHandler() {
                                            @Override
                                            public void complete(String key, ResponseInfo info, JSONObject response) {
                                                //   Log.i(TAG,"qiniu"+key + ",\r\n " + info.toString()+ ",\r\n " +
                                                // response.toString());
                                                if (info.isOK()) {
                                                    Log.e(TAG, "七牛上传图片成功");

                                                } else {
                                                    Log.e(TAG, "七牛上传图片失败");
                                                }
                                                if (checkTakePictureAvailable(uuid)) {
                                                    Log.i(TAG, "开始发送图片");
                                                    callback.afterTakePickture(thisValue, curUrl, isCall, uuid);
                                                } else {
                                                    Log.v("MainActivity", "上传照片成功,但已取消");
                                                }
                                                clearImageUuidAvaible(uuid);
                                                Log.v(TAG, "正常清除" + uuid);
                                                try {
                                                    if (file != null) {
                                                        file.deleteOnExit();
                                                    }
                                                } catch (Exception e) {
                                                }
                                            }
                                        }, null);

                                    }
                                }.start();
                            } else {
                                Log.v("MainActivity", "拍照成功，但已取消");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            camera.setPreviewCallback(null);
                            camera.stopPreview();
                            camera.release();
                            camera = null;
                            mCamerarelease = true;
                            Log.v("MainActivity", "释放照相机资源");
                        }
                    }
                });
            } catch (Exception e) {
                try {
                    camera.stopPreview();
                } catch (Exception err) {
                }
                try {
                    camera.release();
                    camera = null;
                    mCamerarelease = true;
                } catch (Exception err) {
                }
                callback.afterTakePickture(thisValue, null, isCall, uuid);
                Log.v("MainActivity", "照相出异常清除UUID");
                clearImageUuidAvaible(uuid);
            }
        }
    }

    @Override
    public void beforeTakePickture(String thisValue, boolean isCall, String uuid) {
        startDialorPasswordDirectly(thisValue, null, isCall, uuid);
    }

    @Override
    public void afterTakePickture(String thisValue, String fileUrl, boolean isCall, String uuid) {
        startSendPictureDirectly(thisValue, fileUrl, isCall, uuid);
    }

    private boolean checkTakePictureAvailable(String uuid) {
        String thisValue = uuidMaps.get(uuid);
        boolean result = false;
        if (thisValue != null && thisValue.equals("Y")) {
            result = true;
        }
        Log.v(TAG, "检查UUID" + uuid + result);
        return result;
    }

    private String getUUID() {
        UUID uuid = UUID.randomUUID();
        String result = UUID.randomUUID().toString();
        return result;
    }

    private void setImageUuidAvaibale(String uuid) {
        Log.v("MainActivity", "加入UUID" + uuid);
        uuidMaps.put(uuid, "Y");
    }

    private void clearImageUuidAvaible(String uuid) {
        Log.v("MainActivity", "清除UUID" + uuid);
        uuidMaps.remove(uuid);
    }
/****************************拍照相关************************/
/****************************呼叫相关************************/
    /**
     * 开始呼叫
     */
    protected void startDialorPasswordDirectly(final String thisValue, final String fileUrl, final boolean isCall,
                                               String uuid) {
        if (currentStatus == CALLING_MODE || currentStatus == PASSWORD_CHECKING_MODE) {
            Message message = Message.obtain();
            String[] parameters = new String[3];
            if (isCall) {
                setDialValue("呼叫" + thisValue + "，取消请按删除键");
                message.what = MainService.MSG_START_DIAL;
                if (DeviceConfig.DEVICE_TYPE.equals("C")) {
                    parameters[0] = thisValue.substring(2);
                } else {
                    parameters[0] = thisValue;
                }
            } else {
                setTempkeyValue("准备验证密码" + thisValue + "...");
                message.what = MainService.MSG_CHECK_PASSWORD;
                parameters[0] = thisValue;
            }
            parameters[1] = fileUrl;
            parameters[2] = uuid;
            message.obj = parameters;
            try {
                serviceMessage.send(message);
            } catch (RemoteException er) {
                er.printStackTrace();
            }
        }
    }

    protected void startSendPictureDirectly(final String thisValue, final String fileUrl, final boolean isCall,
                                            String uuid) {
        if (fileUrl == null || fileUrl.length() == 0) {
            return;
        }
        Message message = Message.obtain();
        if (isCall) {
            message.what = MainService.MSG_START_DIAL_PICTURE;
        } else {
            message.what = MainService.MSG_CHECK_PASSWORD_PICTURE;
        }
        String[] parameters = new String[3];
        parameters[0] = thisValue;
        parameters[1] = fileUrl;
        parameters[2] = uuid;
        message.obj = parameters;
        try {
            serviceMessage.send(message);
        } catch (RemoteException er) {
            er.printStackTrace();
        }
    }
/****************************呼叫相关************************/


    /****************************设置一些状态************************/
    synchronized void setCurrentStatus(int status) {
        currentStatus = status;
    }

    private void setDialStatus(String value) {
        final String thisValue = value;
        handler.post(new Runnable() {
            @Override
            public void run() {
                setTextView(R.id.tv_input_label, thisValue);
            }
        });
    }

    //设置桌面会话的状态
    private void setDialValue(String value) {
        final String thisValue = value;
        handler.post(new Runnable() {
            @Override
            public void run() {
                setTextView(R.id.tv_input_text, thisValue);
            }
        });
    }

    private void toast(final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Utils.DisplayToast(MainActivity.this, message);
            }
        });
    }

    private void setTempkeyValue(String value) {
        final String thisValue = value;
        handler.post(new Runnable() {
            @Override
            public void run() {
                setTextView(R.id.tv_input_text, thisValue);
            }
        });
    }

    private void setTextView(int id, String txt) {
        ((TextView) findViewById(id)).setText(txt);
    }

    /**
     * 设置社区名字
     *
     * @param value
     */
    private void setCommunityName(String value) {
        final String thisValue = value;
        handler.post(new Runnable() {
            @Override
            public void run() {
                setTextView(R.id.tv_community, thisValue);
            }
        });
    }

    /**
     * 设置锁的名字
     *
     * @param value
     */
    private void setLockName(String value) {
        final String thisValue = value;
        handler.post(new Runnable() {
            @Override
            public void run() {
                setTextView(R.id.tv_lock, thisValue);
            }
        });
    }

    /**
     * 设置mac地址显示等
     *
     * @param mac
     */
    private void showMacaddress(String mac) {
        if (showMacText != null && mac != null && mac.length() > 0) {
            showMacText.setVisibility(View.VISIBLE);
            showMacText.setText("MAC地址未注册，请添加\nMac地址：" + mac);
        }
    }

    /**
     * 取消呼叫设置状态
     */
    protected void resetDial() {
        blockNo = "";
        setDialValue(blockNo);
        setCurrentStatus(CALL_MODE);
    }

    @Override
    public void onAccountReceived(String account) {
        //这里接收到刷卡后获得的卡ID
        cardId = account;
        Log.e(TAG, "onAccountReceived 卡信息 account " + account + " cardId " + cardId);
        if (!nfcFlag) {//非录卡状态（卡信息用于开门）
            Message message = Message.obtain();
            message.what = MainService.MSG_CARD_INCOME;
            message.obj = account;
            try {
                serviceMessage.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {//正在录卡状态（卡信息用于录入）
            Message message = Message.obtain();
            message.what = MSG_INPUT_CARDINFO;
            message.obj = account;
            handler.sendMessage(message);
        }
    }

    /****************************设置一些状态************************/

    /**
     * 开启nfc读卡模式
     */
    private void enableReaderMode() {
        Log.i(TAG, "开启读卡模式");
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(this);
        if (nfc != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (this instanceof NfcAdapter.ReaderCallback) {
                    if (!this.isDestroyed()) {
                        nfc.enableReaderMode(this, this, NfcReader.READER_FLAGS, null);
                    }
                }
            }
        }
    }

    /**
     * 禁用读卡
     */
    private void disableReaderMode() {
        Log.i(TAG, "禁用读卡模式");
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(this);
        if (nfc != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (!this.isDestroyed()) {
                    nfc.disableReaderMode(this);
                }
            }
        }
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if ((nfcReader != null) && (nfcReader instanceof NfcAdapter.ReaderCallback)) {
                NfcAdapter.ReaderCallback nfcReader = (NfcAdapter.ReaderCallback) this.nfcReader;
                nfcReader.onTagDiscovered(tag);
            }
        }
    }

    /**
     * 使用Handler实现UI线程与Timer线程之间的信息传递,每5秒告诉UI线程获得wifi Info
     */
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 如果收到正确的消息就获取WifiInfo，改变图片并显示信号强度
                // TODO: 2018/5/8 以下QQ物联相关暂时注释
//                case 11:
//                    wifi_image.setImageResource(R.mipmap.ethernet);
////                    if (listTemp1 != null && listTemp1.length > 0) {
////                        iv_bind.setImageDrawable(getResources().getDrawable(R.mipmap
////                                .binder_default_head));
////                    } else {
////                        iv_bind.setImageDrawable(getResources().getDrawable(R.mipmap
//// .bind_offline));
////                    }
//                    break;
//                case 1:
//                    wifi_image.setImageResource(R.mipmap.wifi02);
//                    break;
//                case 2:
//                    wifi_image.setImageResource(R.mipmap.wifi02);
//                    break;
//                case 3:
//                    wifi_image.setImageResource(R.mipmap.wifi03);
//                    break;
//                case 4:
//                    wifi_image.setImageResource(R.mipmap.wifi04);
//                    break;
//                case 5:
//                    wifi_image.setImageResource(R.mipmap.wifi05);
//                    break;
//                case 6://无网络连接
//                    rl.setVisibility(View.VISIBLE);
//                    break;
//                case 7:
//                    //提示用户无网络连接
//                    rl.setVisibility(View.GONE);
//                    break;
//                default:
//                    //以防万一
//                    wifi_image.setImageResource(R.mipmap.wifi_05);
//                    rl.setVisibility(View.VISIBLE);
            }
        }

    };


    public class Receive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String actionName = intent.getAction();
            switch (actionName) {
                case ACTION_NFC_CARDINFO:
                    String cardInfo = intent.getStringExtra("cardinfo");
                    Log.i(TAG, "onReceive: cardinfo=" + cardInfo);
                    break;
            }
        }
    }

}
