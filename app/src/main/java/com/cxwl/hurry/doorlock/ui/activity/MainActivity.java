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
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
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
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidex.aexapplibs.appLibsService;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKMatching;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.arcsoft.facetracking.AFT_FSDKEngine;
import com.arcsoft.facetracking.AFT_FSDKError;
import com.arcsoft.facetracking.AFT_FSDKFace;
import com.arcsoft.facetracking.AFT_FSDKVersion;
import com.cxwl.hurry.doorlock.MainApplication;
import com.cxwl.hurry.doorlock.R;
import com.cxwl.hurry.doorlock.callback.AdverErrorCallBack;
import com.cxwl.hurry.doorlock.config.Constant;
import com.cxwl.hurry.doorlock.config.DeviceConfig;
import com.cxwl.hurry.doorlock.db.Lian;
import com.cxwl.hurry.doorlock.entity.XdoorBean;
import com.cxwl.hurry.doorlock.interfac.TakePictureCallback;
import com.cxwl.hurry.doorlock.service.MainService;
import com.cxwl.hurry.doorlock.utils.AdvertiseHandler;
import com.cxwl.hurry.doorlock.utils.Ajax;
import com.cxwl.hurry.doorlock.utils.DbUtils;
import com.cxwl.hurry.doorlock.utils.HttpApi;
import com.cxwl.hurry.doorlock.utils.HttpUtils;
import com.cxwl.hurry.doorlock.utils.Intenet;
import com.cxwl.hurry.doorlock.utils.NetWorkUtils;
import com.cxwl.hurry.doorlock.utils.NfcReader;
import com.cxwl.hurry.doorlock.utils.UploadUtil;
import com.guo.android_extend.java.AbsLoop;
import com.guo.android_extend.widget.CameraFrameData;
import com.guo.android_extend.widget.CameraGLSurfaceView;
import com.guo.android_extend.widget.CameraSurfaceView;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import jni.util.Utils;

import static com.cxwl.hurry.doorlock.config.Constant.CALL_MODE;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_CALLMEMBER_ERROR;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_FACE_DETECT_CHECK;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_FACE_DETECT_CONTRAST;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_FACE_DETECT_INPUT;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_FACE_DETECT_PAUSE;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_FACE_DOWNLOAD;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_FACE_INFO;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_ID_CARD_DETECT_INPUT;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_ID_CARD_DETECT_PAUSE;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_ID_CARD_DETECT_RESTART;
import static com.cxwl.hurry.doorlock.config.Constant.arc_appid;
import static com.cxwl.hurry.doorlock.config.Constant.ft_key;
import static com.cxwl.hurry.doorlock.config.DeviceConfig.DEVICE_KEYCODE_POUND;
import static com.cxwl.hurry.doorlock.config.DeviceConfig.DEVICE_KEYCODE_STAR;
import static com.cxwl.hurry.doorlock.config.Constant.CALLING_MODE;

import static com.cxwl.hurry.doorlock.config.Constant.ERROR_MODE;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_CALLMEMBER_NO_ONLINE;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_CALLMEMBER_SERVER_ERROR;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_CALLMEMBER_TIMEOUT;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_CANCEL_CALL;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_DISCONNECT_VIEDO;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_INPUT_CARDINFO;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_LOGIN_AFTER;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_PASSWORD_CHECK;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_RTC_DISCONNECT;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_RTC_NEWCALL;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_RTC_ONVIDEO;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_RTC_REGISTER;
import static com.cxwl.hurry.doorlock.config.Constant.ONVIDEO_MODE;
import static com.cxwl.hurry.doorlock.config.Constant.PASSWORD_CHECKING_MODE;
import static com.cxwl.hurry.doorlock.config.Constant.PASSWORD_MODE;
import static com.cxwl.hurry.doorlock.utils.NetWorkUtils.NETWOKR_TYPE_ETHERNET;
import static com.cxwl.hurry.doorlock.utils.NetWorkUtils.NETWOKR_TYPE_MOBILE;
import static com.cxwl.hurry.doorlock.utils.NetWorkUtils.NETWORK_TYPE_NONE;
import static com.cxwl.hurry.doorlock.utils.NetWorkUtils.NETWORK_TYPE_WIFI;
import static com.cxwl.hurry.doorlock.utils.NfcReader.ACTION_NFC_CARDINFO;

/**
 * MainActivity
 * Created by William on 2018/4/26
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, TakePictureCallback, NfcReader
        .AccountCallback, NfcAdapter.ReaderCallback, CameraSurfaceView.OnCameraListener {

    private static String TAG = "MainActivity";
    public static final int MSG_RTC_ONVIDEO_IN = 10011;//接收到视频呼叫
    public static final int MSG_ADVERTISE_IMAGE = 20001;//跟新背景图片
    public static int currentStatus = CALL_MODE;//当前状态
    public static final int INPUT_SYSTEMSET_REQUESTCODE = 0X03;

    private TextView version_text;//版本名显示
    private View container;//根View
    private LinearLayout videoLayout;
    private RelativeLayout rl_nfc, rl;//录卡布局和网络检测提示布局
    private ImageView iv_setting, imageView, wifi_image;
    private TextView headPaneTextView, tv_message, tv_battery, showMacText;
    private EditText et_blackno, et_unitno, tv_input_text;

    private Handler handler;
    private Messenger mainMessage;
    private Messenger serviceMessage;//Service端的Messenger
    private String mac;//Mac地址
    private boolean isFlag = true;//录卡时楼栋编号焦点监听的标识
    private NfcReader nfcReader;//用于nfc卡扫描

    private SurfaceView localView = null;//rtc本地摄像头view
    private SurfaceView remoteView = null;//rtc远端视频view
    private String blockNo = "";//输入的房号
    private int blockId;//输入的房号
    private HashMap<String, String> uuidMaps = new HashMap<String, String>();//储存uuid
    private String lastImageUuid = ""; //与拍照图片相对应

    private Camera camera = null;
    private SurfaceHolder autoCameraHolder = null;
    private SurfaceView autoCameraSurfaceView = null;
    private boolean mCamerarelease = true; //判断照相机是否释放
    private AdvertiseHandler advertiseHandler = null;//广告播放类
    public appLibsService hwservice;//hwservice为安卓工控appLibs的服务

    private String cardId;//卡ID
    private boolean nfcFlag = false;//录卡页面是否显示(即是否录卡)的标识,默认false
    private Receive receive; //本地广播
    private int netWorkFlag = -1;//当前网络是否可用标识 有网为1 无网为0
    private Timer netTimer = new Timer();//检测网络用定时器

    private WifiInfo wifiInfo = null;//获得的Wifi信息
    private int level;//信号强度值
    private WifiManager wifiManager = null;//Wifi管理器

    private UploadManager uploadManager;//七牛上传

    private Thread clockRefreshThread = null;

    private Thread passwordTimeoutThread = null;//访客密码线程
    private String guestPassword = "";//访客输入的密码值

    private DbUtils mDbUtils;//数据库操作

    private int mWidth, mHeight;//屏幕宽高
    private CameraSurfaceView mSurfaceView;//用于人脸识别（单独使用则渲染直接走系统流程，配合CameraGLSurfaceView
    // 这个渲染显示的控件来用的的话，。CameraGLSurfaceView 这个类里使用了GLRender
    // 这个GL渲染类，把Camera的数据放进去就可以等比例显示，当然也可以设置不等比例的显示，支持旋转，支持输出渲染帧率）
    private CameraGLSurfaceView mGLSurfaceView;//用于人脸识别
    private Camera mCamera;//虹软要使用的摄像头对象
    private AFT_FSDKVersion version = new AFT_FSDKVersion();//这个类用来保存版本信息
    private AFT_FSDKEngine engine = new AFT_FSDKEngine();//这个类具体实现了人脸跟踪的功能
    private List<AFT_FSDKFace> result = new ArrayList<>();//摄像头检测到的人脸信息集合
    private byte[] mImageNV21 = null;//图像数据
    private AFT_FSDKFace mAFT_FSDKFace = null;//这个类用来保存检测到的人脸信息
    private Handler faceHandler;//人脸识别handler
    private boolean identification = false;//人脸识别可以开始对比的标识
    private FRAbsLoop mFRAbsLoop = null;//人脸对比线程

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: 2018/5/8  此处hwservice实例化没以MainActivity继承AndroidExActivityBase实现
        this.hwservice = new appLibsService(this);
        super.onCreate(savedInstanceState);

        //全屏设置，隐藏窗口所有装饰
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);//清除FLAG
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //禁止软键盘弹出

        {
            ActionBar ab = getActionBar();
            if (ab != null) {
                ab.setDisplayHomeAsUpEnabled(true);//左上角显示应用程序图标
            }
        }
//        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_new_main);
        hwservice.EnterFullScreen();//hwservice为appLibs的服务

        initView();//初始化View
        initDB();//初始化数据库
        initQiniu();//初始化七牛
        initScreen();
        initHandle();
        initAexNfcReader();//初始化nfc本地广播
        initMainService();
        initVoiceVolume();//初始化音量设置
        initAutoCamera();
        startClockRefresh();//时间更新线程,在有心跳包后用心跳包代替
        initNet();
        initAdvertiseHandler();

        if ("C".equals(DeviceConfig.DEVICE_TYPE)) {//判断是否社区大门
            setDialStatus("请输入楼栋编号");
        }
        // TODO: 2018/5/10 这个谷歌api不知道有啥用 client = new GoogleApiClient.Builder(this).addApi
        // (AppIndex.API).build();

        //初始化人脸相关与身份证识别
        initFaceDetectAndIDCard();

    }
/*************************************************初始化一些基本东西start
 * ********************************************/
    /**
     * 开启界面时间(本地)更新线程 之后放到MainService中
     */
    private void startClockRefresh() {
        clockRefreshThread = new Thread() {
            public void run() {
                try {
                    setNewTime();
                    while (true) {
                        sleep(1000 * 60); //等待指定的一个时间(一分钟显示一次)
                        if (!isInterrupted()) { //检查线程没有被停止
                            setNewTime();
                        }
                    }
                } catch (InterruptedException e) {
                }
                clockRefreshThread = null;
            }
        };
        clockRefreshThread.start();
    }

    /**
     * 设置界面时间(本地)
     */
    private void setNewTime() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Date now = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("E");//星期
                String dayStr = dateFormat.format(now);
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String dateStr = dateFormat.format(now);
                dateFormat = new SimpleDateFormat("HH:mm");
                String timeStr = dateFormat.format(now);

                setTextView(R.id.tv_day, dayStr);
                setTextView(R.id.tv_date, dateStr);
                setTextView(R.id.tv_time, timeStr);
            }
        });
    }

    private TextView textViewGongGao;
    private SurfaceView videoView;
    private AdverErrorCallBack adverErrorCallBack;

    protected void initAdvertiseHandler() {
        if (advertiseHandler == null) {
            advertiseHandler = new AdvertiseHandler();
        }
        videoView = (SurfaceView) findViewById(R.id.surface_view);
        imageView = (ImageView) findViewById(R.id.image_view);
        textViewGongGao = (TextView) findViewById(R.id.gonggao);
        Log.v("UpdateAdvertise", "------>start Update Advertise<------");
        advertiseHandler.init(textViewGongGao, videoView, imageView);
        adverErrorCallBack = new AdverErrorCallBack() {
            @Override
            public void ErrorAdver() {
                imageView.setVisibility(View.VISIBLE);
            }
        };
//        advertiseHandler.initData(rows, dialMessenger, (currentStatus == ONVIDEO_MODE),
//                adverErrorCallBack);
        // TODO: 2018/4/27 随便找个位置增加通告接口，之后要改

    }

    /**
     * 初始化七牛
     */

    private void initQiniu() {
        Configuration config = new Configuration.Builder().chunkSize(512 * 1024)        //
                // 分片上传时，每片的大小。 默认256K
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
        initVoiceVolume(audioManager, AudioManager.STREAM_SYSTEM, DeviceConfig.VOLUME_STREAM_SYSTEM);
        initVoiceVolume(audioManager, AudioManager.STREAM_VOICE_CALL, DeviceConfig.VOLUME_STREAM_VOICE_CALL);
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
        headPaneTextView = (TextView) findViewById(R.id.header_pane);//可视对讲设备状态
        videoLayout = (LinearLayout) findViewById(R.id.ll_video);//用于添加视频通话的根布局
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
     * 初始化数据库
     */
    private void initDB() {
        mDbUtils = DbUtils.getInstans();
    }

    /**
     * 初始化view
     */
    public void initView() {
        version_text = (TextView) findViewById(R.id.version_text);
        version_text.setText(getVersionName());
        container = findViewById(R.id.container);

        imageView = (ImageView) findViewById(R.id.iv_erweima);//二维码
        wifi_image = (ImageView) findViewById(R.id.wifi_image); //wifi图标控件初始化
        iv_setting = (ImageView) findViewById(R.id.iv_setting);//左上角弹出菜单按钮
        tv_message = (TextView) findViewById(R.id.tv_message);//录入卡提示信息
        tv_input_text = (EditText) findViewById(R.id.tv_input_text);//桌面会话状态的提示信息
        tv_battery = (TextView) findViewById(R.id.tv_battery);//显示蓝牙锁的电量
        rl = (RelativeLayout) findViewById(R.id.net_view_rl);//网络检测提示布局
        showMacText = (TextView) findViewById(R.id.show_mac);//mac地址
        videoLayout = (LinearLayout) findViewById(R.id.ll_video);//用于添加视频通话的根布局
        //getBgBanners();// 网络获得轮播背景图片数据

        rl.setOnClickListener(this);
        iv_setting.setOnClickListener(this);

        sendBroadcast(new Intent("com.android.action.hide_navigationbar"));//隱藏底部導航
        container.setSystemUiVisibility(13063);//设置状态栏显示与否,禁止頂部下拉

        //录卡相关注释
//        rl_nfc = (RelativeLayout) findViewById(R.id.rl_nfc);//录卡布局
//        et_blackno = (EditText) findViewById(R.id.et_blockno);//录卡时楼栋编号
//        et_unitno = (EditText) findViewById(R.id.et_unitno);//录卡时房屋编号
//        et_blackno.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    isFlag = true;
//                } else {
//                    isFlag = false;
//                }
//            }
//        });

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
                        //登陆成功后 设置信息 初始化rtc等
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
                        if (faceHandler != null) {
                            faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_CONTRAST, 3000);
                        }
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
                    case MSG_CALLMEMBER_SERVER_ERROR:
                        Log.e(TAG, "服务器错误");
                        onCallMemberError(msg.what);
                        break;
                    case MSG_INPUT_CARDINFO:
                        Log.e(TAG, "重复录卡");
                        String obj = (String) msg.obj;
                        tv_message.setText(obj);
                        break;
                    case MSG_PASSWORD_CHECK:
                        Log.i(TAG, "服务器验证密码后的返回");
                        onPasswordCheck((Integer) msg.obj);
                        break;
                    case MSG_FACE_INFO:
                        //人脸识别录入
                        faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_PAUSE, 100);
                        faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_INPUT, 100);
                        break;
                    default:
                        break;
                }

            }
        };
        mainMessage = new Messenger(handler);

    }

    /**
     * 初始化服务
     */
    private void initMainService() {
        Intent intent = new Intent(this, MainService.class);
        bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);

        // TODO: 2018/5/8 开门服务类暂时注释
//        Intent dlIntent = new Intent(MainActivity.this, DoorLock.class);
//        startService(dlIntent);//start方式启动锁Service
    }

    /**
     * 服务连接监听
     */

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
                setStatusBarIcon(true);
                initSystemtime();
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
/*************************************************初始化一些基本东西end
 * ********************************************/
    /**
     * 登录成功后
     *
     * @param msg
     */
    private void onLoginAfter(Message msg) {
        if (msg.obj != null) {
            XdoorBean result = (XdoorBean) msg.obj;
            sendMainMessager(MSG_RTC_REGISTER, null);
            //初始化社区信息
            setCommunityName(result.getXiangmu_name() == null ? "欣社区" : result.getXiangmu_name());
            setLockName(result.getDanyuan_name() == null ? "1单元" : result.getDanyuan_name());

            Log.e(TAG, "可以读卡");
            enableReaderMode();//登录成功后开启读卡

            if (faceHandler != null) {
                faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_CONTRAST, 1000);
            }

            sendMainMessager(MainService.REGISTER_ACTIVITY_DIAL, null);//开始心跳包

        }
//                else if (code == 1) { //登录失败,MAC地址不存在服务器
//                    //显示MAC地址并提示添加
//                    showMacaddress(result.getString("mac"));
//                }

    }

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
                            //主界面显示相关状态
                            if (netWorkFlag == 1) {
                                setStatusBarIcon(true);
                                rl.setVisibility(View.GONE);
                            } else {
                                setStatusBarIcon(false);
                                rl.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }
        }, 500, 1000 * 10);
    }

    /**
     * 校时(校正本地时间)  之后放在心跳包接口里做
     */
    private void initSystemtime() {
        if (NetWorkUtils.isNetworkAvailable(this)) {
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
                            // TODO: 2018/5/9 这里的校时要用到工控相关hwservice,暂时不注释,之后解决
                            hwservice.execRootCommand(cmd);
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
        disableReaderMode();
        if (netTimer != null) {
            netTimer.cancel();
            netTimer = null;
        }
    }

    /**
     * 使用定时器,每隔5秒获得一次信号强度值
     */
    @SuppressLint("WifiManagerLeak")
    private void initNet() {
        wifiManager = (WifiManager) MainApplication.getApplication().getSystemService(WIFI_SERVICE);//获得WifiManager
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
                        Message msg = new Message();
                        msg.what = 11;
                        mHandler.sendMessage(msg);
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

    /**********************************************按键相关start***************************/
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            int keyCode = event.getKeyCode();
            onKeyDown(keyCode);
        }
        return false;
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
        }
        return value;
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

    private String str;//输入框内容

    private void onKeyDown(int keyCode) {
        Log.i(TAG, "默认按键key=" + keyCode);
        if (nfcFlag) {
            //  inputCardInfo(keyCode);//录入卡片信息
        } else {
            int key = convertKeyCode(keyCode);
            Log.i(TAG, "按键key=" + key + "模式currentStatus" + currentStatus);
            if (currentStatus == CALL_MODE || currentStatus == PASSWORD_MODE) {//处于呼叫模式或密码模式
                // TODO: 2018/5/4 这里的判断得改成输入框是否有值,有值确认键走呼叫或密码,没值走切换模式
                str = tv_input_text.getText().toString();
                if (keyCode == DEVICE_KEYCODE_POUND) {//确认键
                    if ("".equals(str)) {//输入框没值走切换模式
                        if (currentStatus == CALL_MODE) {//呼叫模式下，按确认键切换成密码模式
                            initPasswordStatus();
                        } else {
                            initDialStatus();
                        }
                    } else {//输入框有值走呼叫或密码
                        if (currentStatus == CALL_MODE) {//呼叫
                            if ("C".equals(DeviceConfig.DEVICE_TYPE)) {
                                if (blockNo.length() == DeviceConfig.BLOCK_LENGTH) {
                                    startDialing(blockNo);
                                } else if (blockNo.length() == DeviceConfig.MOBILE_NO_LENGTH) {//手机号
                                    if (true) {//正则判断
                                        startDialing(blockNo);
                                    }
                                }
                            } else {
//                                if (blockNo.length() == DeviceConfig.UNIT_NO_LENGTH) {
//                                    startDialing(blockNo);
//                                } else if (blockNo.length() == DeviceConfig.MOBILE_NO_LENGTH) {//手机号
//                                    if (true) {//正则判断
//                                        startDialing(blockNo);
//                                    }
//                                }
                                startDialing(blockNo);
                            }
                        } else {//密码开门
                            if (guestPassword.length() == 6) {
                                checkPassword();
                            }
                        }
                    }
                } else if (keyCode == DEVICE_KEYCODE_STAR) {//删除键
                    if (currentStatus == CALL_MODE) {
                        callInput();//房号或手机号删除一位
                    } else {
                        passwordInput();//密码删除一位
                    }
                    if (str == null || str.equals("")) {
                        //跳转到登录界面
//                        Intent intent = new Intent(this, InputCardInfoActivity.class);
//                        startActivityForResult(intent, INPUT_CARDINFO_REQUESTCODE);
                    }
                } else if (key >= 0) {//数字键
                    if (currentStatus == CALL_MODE) {
                        callInput(key);
                    } else {
                        passwordInput(key);//密码开门
                    }
                }
            } else if (currentStatus == ERROR_MODE) {
                Utils.DisplayToast(MainActivity.this, "当前网络异常");
            } else if (currentStatus == CALLING_MODE) {//处于正在呼叫模式
                Log.v(TAG, "onKeyDown-->111");
                if (keyCode == KeyEvent.KEYCODE_STAR || keyCode == DEVICE_KEYCODE_STAR) {
                    Log.v(TAG, "onKeyDown-->222");
                    startCancelCall();//取消呼叫
                }
            } else if (currentStatus == ONVIDEO_MODE) {
                if (keyCode == KeyEvent.KEYCODE_STAR || keyCode == DEVICE_KEYCODE_STAR) {
                    sendMainMessager(MSG_DISCONNECT_VIEDO, "");
                }
            }
        }
    }

    /**********************************************按键相关end***************************/

    /*********************************密码房号等输入状态相关start*******************************************/
    private void callInput(int key) {
        if ("C".equals(DeviceConfig.DEVICE_TYPE)) {
            if (blockId == 0) {
                if (blockNo.length() < DeviceConfig.BLOCK_LENGTH) {
                    blockNo = blockNo + key;
                    setDialValue(blockNo);
                    Log.i(TAG, "输入的楼栋编号长度不为6 blockNo" + blockNo);
                    Log.e("blockNo", "1===" + blockNo);
                }
                if (blockNo.length() == DeviceConfig.BLOCK_NO_LENGTH) {
                    // TODO: 2018/5/9  检查楼栋
//                    setDialValue(blockNo);
//                    Message message = Message.obtain();
//                    message.what = MainService.MSG_CHECK_BLOCKNO;
//                    message.obj = blockNo;
//                    Log.i(TAG, "输入的楼栋编号满足长度为6 blockNo=" + blockNo);
//                    Log.e("blockNo", "2===" + blockNo);
//                    try {
//                        serviceMessenger.send(message);
//                    } catch (RemoteException e) {
//                        e.printStackTrace();
//                    }
                }
            } else {
                unitNoInput(key);
            }
        } else {
            unitNoInput(key);
        }
    }

    /**
     * 输入的房号或手机号的输入  1
     *
     * @param key
     */
    private void unitNoInput(int key) {
        blockNo = blockNo + key;
        setDialValue(blockNo);
        // TODO: 2018/5/4 这个判断交给确认键去做,暂时注释
//        if (DeviceConfig.DEVICE_TYPE.equals("C")) {
//            if (blockNo.length() == DeviceConfig.BLOCK_LENGTH) {
//                startDialing(blockNo);
//            }
//        } else {
//            if (blockNo.length() == DeviceConfig.UNIT_NO_LENGTH) {
//                startDialing(blockNo);
//            }
//        }


//         TODO: 2018/5/3 暂时注释 测试呼叫手机号
//        if (DeviceConfig.DEVICE_TYPE.equals("C")) {
//            if (blockNo.length() == DeviceConfig.BLOCK_LENGTH) {
//                startDialing(blockNo);
//            }
//        } else {
//            if (blockNo.equals("0101") || blockNo.equals("9999")) {
//                startDialing(blockNo);
//            } else if (blockNo.length() == 11) {
//                startDialing(blockNo);
//            }
//        }

    }

    /**
     * 房号或手机号删除最后一位
     */
    private void callInput() {
        if (DeviceConfig.DEVICE_TYPE.equals("C")) {
            if (blockId > 0) {
                if (blockNo.equals("")) {
                    blockId = 0;
                    blockNo = backKey(blockNo);
                    setDialStatus("请输入楼栋编号");
                    setDialValue(blockNo);
                } else {
                    blockNo = backKey(blockNo);
                    setDialValue(blockNo);
                }
            } else {
                blockNo = backKey(blockNo);
                setDialValue(blockNo);
            }
        } else {
            blockNo = backKey(blockNo);
            setDialValue(blockNo);
        }
    }

    /**
     * 密码输入
     *
     * @param key
     */
    private void passwordInput(int key) {
        guestPassword = guestPassword + key;
        setTempkeyValue(guestPassword);
        // TODO: 2018/5/4 这个判断交给确认键去做,暂时注释
//        if (guestPassword.length() == 6) {
//            checkPassword();
//        }
    }

    /**
     * 密码 删除最后一位
     */
    private void passwordInput() {
        guestPassword = backKey(guestPassword);
        setTempkeyValue(guestPassword);
    }

    /**
     * 删除最后一位
     *
     * @param code
     * @return
     */
    private String backKey(String code) {
        if (code != null && code != "") {
            int length = code.length();
            if (length == 1) {
                code = "";
            } else {
                code = code.substring(0, (length - 1));
            }
        }
        return code;
    }

    private void checkPassword() {
        setCurrentStatus(PASSWORD_CHECKING_MODE);
        String thisPassword = guestPassword;
        guestPassword = "";
        takePicture(thisPassword, false, this);
    }

    /**
     * 桌面显示呼叫模式
     */
    private void initDialStatus() {
        videoLayout.setVisibility(View.INVISIBLE);
        setCurrentStatus(CALL_MODE);
        blockNo = "";
        blockId = 0;
        if (DeviceConfig.DEVICE_TYPE.equals("C")) {
            setDialStatus("请输入楼栋编号");
        } else {
            setDialStatus("请输入房屋编号");
        }
        setDialValue(blockNo);
    }

    /**
     * 界面显示输入密码模式
     */
    private void initPasswordStatus() {
        stopPasswordTimeoutChecking();
        setDialStatus("请输入访客密码");
        videoLayout.setVisibility(View.INVISIBLE);
        setCurrentStatus(PASSWORD_MODE);
        guestPassword = "";
        setTempkeyValue(guestPassword);
        startTimeoutChecking();
    }

    /**
     * 密码验证后 是否成功等
     *
     * @param code
     */
    private void onPasswordCheck(int code) {
        setCurrentStatus(PASSWORD_MODE);
        setTempkeyValue("");
        if (code == 0) {
            Utils.DisplayToast(MainActivity.this, "您输入的密码验证成功");
        } else {
            if (code == 1) {
                Utils.DisplayToast(MainActivity.this, "您输入的密码不存在");
            } else if (code == 2) {
                Utils.DisplayToast(MainActivity.this, "您输入的密码已经过期");
            } else if (code < 0) {
                Utils.DisplayToast(MainActivity.this, "密码验证不成功，请联系管理员");
            }
        }
    }

    /**
     * 输入密码 无操作 启动自动跳转到输入房号的状态的线程
     */
    private void startTimeoutChecking() {
        passwordTimeoutThread = new Thread() {
            public void run() {
                try {
                    sleep(DeviceConfig.PASSWORD_WAIT_TIME); //等待指定的一个等待时间
                    if (!isInterrupted()) { //检查线程没有被停止
                        if (currentStatus == PASSWORD_MODE) { //如果现在是密码输入状态
                            if (TextUtils.isEmpty(guestPassword)) { //如果密码一直是空白的
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        initDialStatus();
                                    }
                                });
                                stopPasswordTimeoutChecking();
                            }
                        }
                    }
                } catch (InterruptedException e) {
                }
                passwordTimeoutThread = null;
            }
        };
        passwordTimeoutThread.start();
    }

    /**
     * 输入密码 无操作 停止自动跳转到输入房号的状态的线程
     */
    protected void stopPasswordTimeoutChecking() {
        if (passwordTimeoutThread != null) {
            passwordTimeoutThread.interrupt();
            passwordTimeoutThread = null;
        }
    }
/*********************************密码房号等输入状态相关end*******************************************/


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
            remoteView = (SurfaceView) MainService.callConnection.createVideoView(false, this, true);
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

/****************************拍照相关start************************/
    /**
     * 开始启动拍照
     */
    protected void takePicture(final String thisValue, final boolean isCall, final TakePictureCallback callback) {
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
        Log.v(TAG, "打开相机");
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
                camera.setParameters(parameters);
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
                            String date = sdf.format(new Date(System.currentTimeMillis()));
                            final String curUrl = "upload/menjin/img/" + "android_" + date;
                            if (checkTakePictureAvailable(uuid)) {
                                new Thread() {
                                    @Override
                                    public void run() {
                                        Log.i(TAG, "开始上传照片");
                                        String s = HttpApi.getInstance().loadHttpforGet(DeviceConfig.GET_QINIUTOKEN,
                                                "");
                                        String token = "";
                                        if (s != null && !"".equals(s)) {
                                            JSONObject jsonObject = Ajax.getJSONObject(s);

                                            try {
                                                token = (String) jsonObject.get("uptoken");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        Log.e(TAG, "Token==" + token);
                                        Log.e(TAG, "file七牛储存地址：" + curUrl);
                                        Log.e(TAG, "file本地地址：" + file.getPath() + "file大小" + file.length());
                                        uploadManager.put(file.getPath(), curUrl, "", new UpCompletionHandler() {
                                            @Override
                                            public void complete(String key, ResponseInfo info, JSONObject response) {
                                                if (info.isOK()) {
                                                    Log.e(TAG, "七牛上传图片成功");

                                                } else {
                                                    Log.e(TAG, "七牛上传图片失败");
                                                }
                                                if (checkTakePictureAvailable(uuid) && info.isOK()) {
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
                    camera.release();
                    camera = null;
                    mCamerarelease = true;
                    Log.v("MainActivity", "照相出异常清除UUID");
                    clearImageUuidAvaible(uuid);
                } catch (Exception err) {
                }

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
/****************************拍照相关end************************/
/****************************呼叫相关start************************/
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
//        }
        else if (reason == MSG_CALLMEMBER_NO_ONLINE) {
            Utils.DisplayToast(MainActivity.this, "您呼叫的房间号无人在线");
        }
        //启动人脸识别
        if (faceHandler != null) {
            faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_CONTRAST, 3000);
        }
    }

    /**
     * 删除键取消拨号
     */
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

    /**
     * 开始呼叫
     *
     * @param blockNo
     */
    private void startDialing(String blockNo) {
        //呼叫前，确认摄像头不被占用 红软
//呼叫前，确认摄像头不被占用
        if (faceHandler != null) {
            faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_PAUSE, 0);
        }

        Log.i(TAG, "拍摄访客照片 并进行呼叫" + blockNo);
        setCurrentStatus(CALLING_MODE);
        //拍摄访客照片 并进行呼叫
        takePicture(blockNo, true, MainActivity.this);
    }

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

    /**
     * 发送访客图片地址
     *
     * @param thisValue
     * @param fileUrl
     * @param isCall
     * @param uuid
     */
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
/****************************呼叫相关end************************/

    /****************************设置一些状态start************************/
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

    /**
     * 设置自定义状态栏的状态（WiFi标志）
     *
     * @param state true 显示  false 隐藏
     */
    private void setStatusBarIcon(boolean state) {
        if (state) {
            //显示
            if (wifi_image.getVisibility() == View.INVISIBLE) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        wifi_image.setVisibility(View.VISIBLE);
                    }
                });
            }
        } else {
            //隐藏
            if (wifi_image.getVisibility() == View.VISIBLE) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        wifi_image.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }
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
                Log.e(TAG, "设置社区" + thisValue);
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
                Log.e(TAG, "设置单元" + thisValue);
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

    /****************************设置一些状态end************************/

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
                case 11:
                    wifi_image.setImageResource(R.mipmap.ethernet);
//                    if (listTemp1 != null && listTemp1.length > 0) {
//                        iv_bind.setImageDrawable(getResources().getDrawable(R.mipmap
//                                .binder_default_head));
//                    } else {
//                        iv_bind.setImageDrawable(getResources().getDrawable(R.mipmap
// .bind_offline));
//                    }
                    break;
                case 1:
                    wifi_image.setImageResource(R.mipmap.wifi02);
                    break;
                case 2:
                    wifi_image.setImageResource(R.mipmap.wifi02);
                    break;
                case 3:
                    wifi_image.setImageResource(R.mipmap.wifi03);
                    break;
                case 4:
                    wifi_image.setImageResource(R.mipmap.wifi04);
                    break;
                case 5:
                    wifi_image.setImageResource(R.mipmap.wifi05);
                    break;
                case 6://无网络连接
                    rl.setVisibility(View.VISIBLE);
                    break;
                case 7:
                    //提示用户无网络连接
                    rl.setVisibility(View.GONE);
                    break;
                default:
                    //以防万一
                    wifi_image.setImageResource(R.mipmap.wifi_05);
                    rl.setVisibility(View.VISIBLE);
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
    /****************************点击事件相关start**************************************/
    /****************************点击事件相关start**************************************/

    @Override
    public void onClick(View view) {
        switch (view.getId()) {//跳转网络或网络设置
            case R.id.net_view_rl: {
                Intenet.system_set(MainActivity.this, INPUT_SYSTEMSET_REQUESTCODE);
                break;
            }
            case R.id.iv_setting: {
                initMenu();//初始化左上角弹出框
                break;
            }
        }
    }

    /**
     * 初始化左上角弹出框
     */
    private void initMenu() {
        PopupMenu popup = new PopupMenu(MainActivity.this, iv_setting);
        popup.getMenuInflater().inflate(R.menu.poupup_menu_home, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_settings1:
//                        Log.e(TAG,"menu 系統設置");

                        Intent intent = new Intent(Settings.ACTION_SETTINGS);//跳轉到系統設置
                        intent.putExtra("back", true);
                        startActivityForResult(intent, INPUT_SYSTEMSET_REQUESTCODE);
                        break;
                    case R.id.action_catIP:
                        Log.e(TAG, "menu 本机的IP");
//                        Toast.makeText(MainActivity.this, "本机的IP：" + Intenet.getHostIP(), Toast
//                                .LENGTH_LONG).show();
                        break;
                    case R.id.action_catVersion:
                        Log.e(TAG, "menu 本机的固件版本");
//                        Toast.makeText(MainActivity.this, "本机的固件版本：" + hwservice.getSdkVersion(),
//                                Toast.LENGTH_LONG).show();
                        break;
                    case R.id.action_updateVersion:
                        Log.e(TAG, "menu 更新");
                        //点击，手动更新
//                        Message message = Message.obtain();
//                        message.what = MSG_UPDATE_VERSION;
//                        try {
//                            serviceMessenger.send(message);
//                        } catch (RemoteException e) {
//                            e.printStackTrace();
//                        }
                        break;
                    case R.id.action_settings3://上传日志
                        Log.e(TAG, "menu 上传日志");
                        break;
                    case R.id.action_settings7://重启
                        Log.e(TAG, "menu 重启");
//                        DoorLock.getInstance().runReboot();
                        break;
                    case R.id.action_settings10://退出
//                        Log.e(TAG,"menu 退出");
                        setResult(RESULT_OK);
                        MainActivity.this.stopService(new Intent(MainActivity.this, MainService.class));
                        finish();
                        sendBroadcast(new Intent("com.android.action.display_navigationbar"));
                        break;
                    default:
                        break;

                }
                return true;
            }
        });
        popup.show();
    }
    /****************************点击事件相关end**************************************/

    /****************************虹软相关start*********************************************/

    /**
     * 初始化人脸相关与身份证识别
     */
    private void initFaceDetectAndIDCard() {
        //获取屏幕大小
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        float density = dm.density;
        mWidth = dm.widthPixels;
        mHeight = dm.heightPixels;
        Log.v(TAG, "initFaceDetect-->" + mWidth + "/" + mHeight + "/" + density);

        mGLSurfaceView = (CameraGLSurfaceView) findViewById(R.id.glsurfaceView);
        mSurfaceView = (CameraSurfaceView) findViewById(R.id.surfaceView);
        mSurfaceView.setOnCameraListener(this);
        mSurfaceView.setupGLSurafceView(mGLSurfaceView, true, true, 0);
        mSurfaceView.debug_print_fps(true, false);

        //人脸跟踪初始化引擎，设置检测角度、范围，数量。创建对象后，必须先于其他成员函数调用，否则其他成员函数会返回 MERR_BAD_STATE
        //orientsPriority 指定检测的角度 scale 指定支持检测的最小人脸尺寸(16) maxFaceNum 最多能检测到的人脸个数(5)
        AFT_FSDKError err = engine.AFT_FSDK_InitialFaceEngine(arc_appid, ft_key, AFT_FSDKEngine.AFT_OPF_0_HIGHER_EXT,
                16, 5);
        Log.d(TAG, "AFT_FSDK_InitialFaceEngine =" + err.getCode());
        err = engine.AFT_FSDK_GetVersion(version);//获取版本信息
        Log.d(TAG, "AFT_FSDK_GetVersion:" + version.toString() + "," + err.getCode());

        // TODO: 2018/5/11 身份证识别暂时不要  initIDCard();

        faceHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Log.v(TAG, "face" + "handleMessage-->" + msg.what + "/" + Thread.currentThread().getName());
                switch (msg.what) {
                    case MSG_FACE_DETECT_CHECK:
                        identification = true;
//                        idOperation =true;
                        break;
                    case MSG_FACE_DETECT_INPUT://人脸识别录入(拿到网络图片后发出人脸识别暂停，然后发出录入消息)
                        Log.e(TAG, "人脸" + "识别录入");
                        // TODO: 2018/5/11  这里要传入整个网络图片的所有地址过来给faceDetectInput方法使用
                        faceDetectInput();
                        break;
                    case MSG_FACE_DETECT_CONTRAST://人脸识别对比
                        Log.e(TAG, "人脸" + "识别对比");
                        identification = true;
                        if (mFRAbsLoop != null) {
                            mFRAbsLoop.resumeThread();
                        }
                        if (mSurfaceView.getVisibility() != View.VISIBLE) {
                            mGLSurfaceView.setVisibility(View.VISIBLE);
                            mSurfaceView.setVisibility(View.VISIBLE);
                        }
                        break;
                    case MSG_FACE_DETECT_PAUSE://人脸识别暂停
                        Log.e(TAG, "人脸" + "识别暂停");
                        identification = false;
                        if (mFRAbsLoop != null) {
                            mFRAbsLoop.pauseThread();
                        }
                        if (mSurfaceView.getVisibility() != View.GONE) {
                            mGLSurfaceView.setVisibility(View.GONE);
                            mSurfaceView.setVisibility(View.GONE);
                        }
                        break;
                    //身份证的都注释
                    case MSG_ID_CARD_DETECT_RESTART:
//                        idOperation = true;
//                        if (mIdCardUtil != null) {
//                            mIdCardUtil.setReading(true);
//                        }
                        break;
                    case MSG_ID_CARD_DETECT_PAUSE:
//                        idOperation = false;
//                        if (mIdCardUtil != null) {
//                            mIdCardUtil.setReading(false);
//                        }
                        break;
                    case MSG_ID_CARD_DETECT_INPUT:
//                        inputIDCard((IDCard) msg.obj);
                        break;
                }
                return false;
            }
        });

        mFRAbsLoop = new FRAbsLoop();
        mFRAbsLoop.start();

        // TODO: 2018/5/14 获取人脸信息本地数据库,判断是否为空
        if (true) {//人脸信息本地数据库不为空
            identification = true;//可以人脸对比
        } else {//人脸信息本地数据库为空
            identification = false;//不可以人脸对比
        }
    }

    /**
     * 执行顺序setupCamera-startPreviewLater-setupChanged-onPreview-onBeforeRender-onPreview
     * -onAfterRender
     * -onBeforeRender-onPreview-onAfterRender...
     */

    /**
     * SurfaceView被创建起来的时候，会被触发，需要在这个监听接口中，把相机打开并且设置参数，但不用启动预览
     *
     * @return
     */
    @Override
    public Camera setupCamera() {
//        Log.e(TAG, "相机" + "setupCamera");

        try {//这里其实不用捕捉错误
            mCamera = Camera.open();

            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(800, 600);//设置尺寸
            parameters.setPreviewFormat(ImageFormat.NV21);//指定图像的格式
            // (NV21：是一种YUV420SP格式，紧跟Y平面的是VU交替的平面)

//            for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
//                Log.v(TAG, "SIZE:" + size.width + "x" + size.height);
//            }
//            for (Integer format : parameters.getSupportedPreviewFormats()) {
//                Log.v(TAG, "FORMAT:" + format);
//            }
//
//            List<int[]> fps = parameters.getSupportedPreviewFpsRange();
//            for (int[] count : fps) {
//                Log.d(TAG, "T:");
//                for (int data : count) {
//                    Log.d(TAG, "V=" + data);
//                }
//            }
            //parameters.setPreviewFpsRange(15000, 30000);
            //parameters.setExposureCompensation(parameters.getMaxExposureCompensation());
            //parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
            //parameters.setAntibanding(Camera.Parameters.ANTIBANDING_AUTO);
            //parmeters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            //parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
            //parameters.setColorEffect(Camera.Parameters.EFFECT_NONE);
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
//            Log.v(TAG, "setupCamera-->" + e.getMessage());
        }

        if (mCamera != null) {
            mWidth = mCamera.getParameters().getPreviewSize().width;
            mHeight = mCamera.getParameters().getPreviewSize().height;
            mCamera.autoFocus(null);
            Log.v(TAG, "SIZE:" + mWidth + "x" + mHeight);//800x600 与设置值一样
        }
        return mCamera;
    }

    /**
     * SurfaceView 有变化时被触发，一般情况这个SurfaceView 只用来获取数据不会触发
     *
     * @param format
     * @param width
     * @param height
     */
    @Override
    public void setupChanged(int format, int width, int height) {
//        Log.e(TAG, "相机" + "setupChanged");
    }

    /**
     * 需要启动Preview的时机会被调用，返回false则在UI创建完之后立即启动,否则不启动预览，由使用者自己控制启动时机
     *
     * @return
     */
    @Override
    public boolean startPreviewLater() {
//        Log.e(TAG, "相机" + "startPreviewLater");
        return false;
    }

    /**
     * 启动Preview之后会被调用，返回相应的图像数据和时间戳，注意这里的时间戳不是底层驱动的时间戳
     *
     * @param data      输入的图像数据
     * @param width     图像宽度
     * @param height    图像宽度
     * @param format    图像格式
     * @param timestamp
     * @return
     */
    @Override
    public Object onPreview(byte[] data, int width, int height, int format, long timestamp) {
//        Log.e(TAG, "相机" + "onPreview");
        //检测输入的图像中存在的人脸，输出结果和初始化时设置的参数有密切关系,检测到的人脸会add到此result
        AFT_FSDKError err = engine.AFT_FSDK_FaceFeatureDetect(data, width, height, AFT_FSDKEngine.CP_PAF_NV21, result);
//        Log.d(TAG, "AFT_FSDK_FaceFeatureDetect =" + err.getCode());
//        Log.d(TAG, "Face=" + result.size());
//        for (AFT_FSDKFace face : result) {
//            Log.d(TAG, "虹软:" + face.toString());
//        }
        if (mImageNV21 == null) {
            if (!result.isEmpty()) {
                mAFT_FSDKFace = result.get(0).clone();//保存集合中第一个人脸信息
                mImageNV21 = data.clone();//保存图像数据
            } else {
//                mHandler.postDelayed(hide, 3000);
            }
        }

        //保存人脸框数组
        Rect[] rects = new Rect[result.size()];
        for (int i = 0; i < result.size(); i++) {
            rects[i] = new Rect(result.get(i).getRect());
        }
        //清空人脸信息集合
        result.clear();
        //返回人脸框数据用于渲染
        return rects;
    }

    /**
     * 渲染图像数据传递到 GLRender 之前会触发，这里可以对图像数据再次修改，例如在上面画框之类的操作都可以
     *
     * @param data
     */
    @Override
    public void onBeforeRender(CameraFrameData data) {
//        Log.e(TAG, "相机" + "onBeforeRender");
    }

    /**
     * 渲染完成之后被触发，这里可以做一些资源释放之类的操作，也可以什么都不做
     *
     * @param data
     */
    @Override
    public void onAfterRender(CameraFrameData data) {
//        Log.e(TAG, "相机" + "onAfterRender");
        if (null == data.getParams()) {
            Log.e(TAG, "相机" + "getParams" + "为空");
        }
        mGLSurfaceView.getGLES2Render().draw_rect((Rect[]) data.getParams(), Color.GREEN, 2);
    }

    /**
     * 开始人脸录入
     */
    private void faceDetectInput() {
        //// TODO: 2018/5/11 要发送消息给MainService，交由MainService去处理下载流程和录入流程
        // TODO: 2018/5/11 拿到所有网络图片路径后开始，一张张录入,开子线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO: 2018/5/11  下载人脸识别用图片并录入

            }
        }).start();

        sendMainMessager(MSG_FACE_DOWNLOAD, null);
    }

    class FRAbsLoop extends AbsLoop {

        AFR_FSDKVersion version = new AFR_FSDKVersion();
        AFR_FSDKEngine engine = new AFR_FSDKEngine();
        AFR_FSDKFace result = new AFR_FSDKFace();

        // TODO: 2018/5/14 这里拿到本地数据库脸信息表
//        List<FaceDB.FaceRegist> mResgist = ArcsoftManager.getInstance().mFaceDB.mRegister;
        List<Lian> mFaceList = new ArrayList<>();
//        List<ASAE_FSDKFace> face1 = new ArrayList<>();
//        List<ASGE_FSDKFace> face2 = new ArrayList<>();


        private final Object lock = new Object();
        private boolean pause = false;

        /**
         * 调用这个方法实现暂停线程
         */
        void pauseThread() {
            pause = true;
        }

        /**
         * 调用这个方法实现恢复线程的运行
         */
        void resumeThread() {
            pause = false;
            synchronized (lock) {
                lock.notifyAll();
            }
        }

        /**
         * 注意：这个方法只能在run方法里调用，不然会阻塞主线程，导致页面无响应
         */
        void onPause() {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void setup() {
            AFR_FSDKError error = engine.AFR_FSDK_InitialEngine(arc_appid, ft_key);
            //Log.v(FACE_TAG, "AFR_FSDK_InitialEngine = " + error.getCode());
            error = engine.AFR_FSDK_GetVersion(version);
            //Log.v(FACE_TAG, "FR=" + version.toString() + "," + error.getCode()); //(210, 178 -
            // 478, 446), degree =
            // 1　780, 2208 - 1942, 3370
        }

        @Override
        public void loop() {
//            Log.v(FACE_TAG, "loop1:" + mImageNV21 + "/" + identification);
            while (pause) {
                onPause();
            }
            try {
                Thread.sleep(1 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (mImageNV21 != null && identification) {//摄像头检测到人脸信息且处于人脸识别状态
                long time = System.currentTimeMillis();
                //检测输入图像中的人脸特征信息，输出结果保存在 AFR_FSDKFace feature
                //data 输入的图像数据,width 图像宽度,height 图像高度,format 图像格式,face 已检测到的脸框,ori 已检测到的脸角度,
                // feature 检测到的人脸特征信息
                AFR_FSDKError error = engine.AFR_FSDK_ExtractFRFeature(mImageNV21, mWidth, mHeight, AFR_FSDKEngine
                        .CP_PAF_NV21, mAFT_FSDKFace.getRect(), mAFT_FSDKFace.getDegree(), result);
                //Log.d(TAG, "AFR_FSDK_ExtractFRFeature cost :" + (System.currentTimeMillis() -
                // time) + "ms");
                //Log.d(TAG, "Face=" + result.getFeatureData()[0] + "," + result.getFeatureData()
                // [1] + "," + result
                // .getFeatureData()[2] + "," + error.getCode());
                AFR_FSDKMatching score = new AFR_FSDKMatching();//这个类用来保存特征信息匹配度
                float max = 0.0f;//匹配度的值
                String name = null;
                //下面暂时注释
                //遍历本地信息表
                /*for (FaceDB.FaceRegist fr : mResgist) {
                    Log.v(FACE_TAG, "loop:" + mResgist.size() + "/" + fr.mFaceList.size());
                    if (fr.mName.length() > 11) {
                        continue;
                    }
                    for (AFR_FSDKFace face : fr.mFaceList) {
                        //比较两份人脸特征信息的匹配度(result 脸部特征信息对象,face 脸部特征信息对象,score 匹配度对象)
                        error = engine.AFR_FSDK_FacePairMatching(result, face, score);
                        Log.d(TAG, "Score:" + score.getScore());
                        // + error.getCode());
                        if (max < score.getScore()) {
                            max = score.getScore();//匹配度赋值
                            name = fr.mName;
                            if (max > 0.618f) {//匹配度的值高于设定值,退出循环
                                break;
                            }
                        }
                    }
                }


                //Log.v(FACE_TAG, "fit Score:" + max + ", NAME:" + name);
                if (max > 0.618f) {//匹配度的值高于设定值,发出消息,开门
                    //fr success.
                    final float max_score = max;
                    //Log.v(FACE_TAG, "置信度：" + (float) ((int) (max_score * 1000)) / 1000.0);
                    Message message = Message.obtain();
                    message.what = MainService.MSG_FACE_OPENLOCK;
                    try {
                        serviceMessenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }*/
                mImageNV21 = null;
            }
        }

        @Override
        public void over() {
            AFR_FSDKError error = engine.AFR_FSDK_UninitialEngine();//销毁引擎，释放内存资源
            //Log.v(FACE_TAG, "AFR_FSDK_UninitialEngine : " + error.getCode());
        }
    }
    /****************************虹软相关end*********************************************/

}
