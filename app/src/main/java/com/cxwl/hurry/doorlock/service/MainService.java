package com.cxwl.hurry.doorlock.service;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.arcsoft.facedetection.AFD_FSDKEngine;
import com.arcsoft.facedetection.AFD_FSDKError;
import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facedetection.AFD_FSDKVersion;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.cxwl.hurry.doorlock.config.Constant;
import com.cxwl.hurry.doorlock.config.DeviceConfig;
import com.cxwl.hurry.doorlock.db.ImgFile;
import com.cxwl.hurry.doorlock.db.Ka;
import com.cxwl.hurry.doorlock.db.LogDoor;
import com.cxwl.hurry.doorlock.db.AdTongJiBean;
import com.cxwl.hurry.doorlock.entity.ConnectReportBean;
import com.cxwl.hurry.doorlock.entity.DoorBean;
import com.cxwl.hurry.doorlock.entity.FaceUrlBean;
import com.cxwl.hurry.doorlock.entity.GuangGaoBean;
import com.cxwl.hurry.doorlock.entity.LogListBean;
import com.cxwl.hurry.doorlock.entity.ResponseBean;
import com.cxwl.hurry.doorlock.entity.XdoorBean;
import com.cxwl.hurry.doorlock.entity.YeZhuBean;
import com.cxwl.hurry.doorlock.http.API;
import com.cxwl.hurry.doorlock.ui.activity.MainActivity;
import com.cxwl.hurry.doorlock.utils.AexUtil;
import com.cxwl.hurry.doorlock.utils.Ajax;
import com.cxwl.hurry.doorlock.utils.BitmapUtils;
import com.cxwl.hurry.doorlock.utils.CardRecord;
import com.cxwl.hurry.doorlock.utils.DbUtils;
import com.cxwl.hurry.doorlock.utils.HttpApi;
import com.cxwl.hurry.doorlock.utils.HttpUtils;
import com.cxwl.hurry.doorlock.utils.InstallUtil;
import com.cxwl.hurry.doorlock.utils.JsonUtil;
import com.cxwl.hurry.doorlock.utils.MacUtils;
import com.cxwl.hurry.doorlock.utils.SPUtil;
import com.cxwl.hurry.doorlock.utils.ShellUtils;
import com.cxwl.hurry.doorlock.utils.SoundPoolUtil;
import com.cxwl.hurry.doorlock.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import com.guo.android_extend.image.ImageConverter;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import jni.http.HttpManager;
import jni.http.HttpResult;
import jni.http.RtcHttpClient;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Response;
import rtc.sdk.clt.RtcClientImpl;
import rtc.sdk.common.RtcConst;
import rtc.sdk.common.SdkSettings;
import rtc.sdk.core.RtcRules;
import rtc.sdk.iface.ClientListener;
import rtc.sdk.iface.Connection;
import rtc.sdk.iface.ConnectionListener;
import rtc.sdk.iface.Device;
import rtc.sdk.iface.DeviceListener;
import rtc.sdk.iface.RtcClient;

import static com.cxwl.hurry.doorlock.config.Constant.CALL_VIDEO_CONNECTING;
import static com.cxwl.hurry.doorlock.config.Constant.CALL_WAITING;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_ADVERTISE_REFRESH;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_ADVERTISE_REFRESH_PIC;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_CALLMEMBER_ERROR;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_CALLMEMBER_NO_ONLINE;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_CALLMEMBER_SERVER_ERROR;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_CALLMEMBER_TIMEOUT;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_CANCEL_CALL;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_CARD_OPENLOCK;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_DISCONNECT_VIEDO;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_FACE_DOWNLOAD;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_FACE_INFO;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_FACE_OPENLOCK;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_GET_NOTICE;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_GUEST_PASSWORD_CHECK;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_INVALID_CARD;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_LIXIAN_PASSWORD_CHECK;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_LIXIAN_PASSWORD_CHECK_AFTER;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_LOADLOCAL_DATA;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_LOCK_OPENED;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_LOGIN;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_LOGIN_AFTER;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_PASSWORD_CHECK;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_RTC_DISCONNECT;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_RTC_NEWCALL;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_RTC_ONVIDEO;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_RTC_REGISTER;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_TONGJI_PIC;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_TONGJI_VEDIO;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_UPLOAD_LIXIAN_IMG;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_YIJIANKAIMEN_TAKEPIC;
import static com.cxwl.hurry.doorlock.config.Constant.RTC_APP_ID;
import static com.cxwl.hurry.doorlock.config.Constant.RTC_APP_KEY;
import static com.cxwl.hurry.doorlock.config.Constant.SP_LIXIAN_MIMA;
import static com.cxwl.hurry.doorlock.config.Constant.SP_VISION_GUANGGAO;
import static com.cxwl.hurry.doorlock.config.Constant.SP_VISION_KA;
import static com.cxwl.hurry.doorlock.config.Constant.SP_XINTIAO_TIME;
import static com.cxwl.hurry.doorlock.config.Constant.arc_appid;
import static com.cxwl.hurry.doorlock.config.Constant.fd_key;
import static com.cxwl.hurry.doorlock.config.Constant.fr_key;
import static com.cxwl.hurry.doorlock.config.DeviceConfig.LOCAL_APK_PATH;


/**
 * @author xlei
 * @Date 2018/4/24.
 */

public class MainService extends Service {
    private static final String TAG = "MainService";
    private static final String RTCTAG = "RTCTAG";
    public static final int MAIN_ACTIVITY_INIT = 0;
    public static final int REGISTER_ACTIVITY_DIAL = 3;
    public static final int MSG_CALLMEMBER = 20002;//呼叫成员

    public static final int MSG_START_DIAL = 20005;//开始呼叫
    public static final int MSG_CHECK_PASSWORD = 20006;//检查密码
    public static final int MSG_CARD_INCOME = 20008;//刷卡回调

    public static final int MSG_UPDATE_NETWORKSTATE = 20028;//网络状态改变

    public static final int MSG_START_DIAL_PICTURE = 21005;//开始呼叫的访客图片
    public static final int MSG_CHECK_PASSWORD_PICTURE = 21006;//密码访客图片

    public int callConnectState = CALL_WAITING;//视频通话链接状态  默认等待

    protected AexUtil aexUtil = null;
    protected CardRecord cardRecord = new CardRecord();
    private String mac;
    private String key;
    private Handler mHandler;
    private Messenger serviceMessage;
    private Messenger mainMessage;
    public static String httpServerToken = "";//服务器拿到的token
    RtcClient rtcClient;
    boolean isRtcInit = false; //RtcSDK初始化状态
    //天翼登陆参数
    private String token;//天翼登陆所需的token；
    private Device device;//天翼登陆连接成功 发消息的类
    private DbUtils mDbUtils;//数据库操作
    private Hashtable<String, String> currentAdvertisementFiles = new Hashtable<String, String>(); //广告数据地址
    private AudioManager audioManager;//音频管理器

    private ArrayList<YeZhuBean> allUserList = new ArrayList<>();
    private ArrayList<YeZhuBean> triedUserList = new ArrayList<>();
    private ArrayList<YeZhuBean> onlineUserList = new ArrayList<>();
    private ArrayList<YeZhuBean> offlineUserList = new ArrayList<>();
    private ArrayList<YeZhuBean> rejectUserList = new ArrayList<>();
    private ArrayList<FaceUrlBean> faceUrlList = new ArrayList<>();//人脸照片URL集合

    public String unitNo = "";//呼叫房号
    public static int communityId = 0;//社区ID
    public static int blockId = 0;//楼栋ID
    public static String communityName = "";//社区名字
    public static String lockName = "";//锁的名字
    public int inputBlockId = 0;//这个也是楼栋ID，好像可以用来代表社区大门
    public static int lockId = 0;//锁ID
    public String imageUrl = null;//对应呼叫访客图片地址
    public String imageUuid = null;//图片对应的uuid
    public String faceImageUrl = null;//人脸开门的图片地址

    private Thread timeoutCheckThread = null;//自动取消呼叫的定时器
    private Thread connectReportThread = null;//心跳包线程

    private boolean netWorkstate = false;//是否有网的标识
    public String tempKey = "";
    private Ka kaInfo = null;

    private AFD_FSDKEngine engine_afd = new AFD_FSDKEngine();//这个类实现了人脸检测的功能
    private AFD_FSDKVersion version_afd = new AFD_FSDKVersion();//这个类用来保存版本信息
    private List<AFD_FSDKFace> result_afd = new ArrayList<AFD_FSDKFace>();//检测到的人脸信息集合
    private AFD_FSDKError err_afd = new AFD_FSDKError();//这个类用来保存虹软错误

    private AFR_FSDKVersion version_afr = new AFR_FSDKVersion();//保存版本信息(人脸识别)
    private AFR_FSDKEngine engine_afr = new AFR_FSDKEngine();//这个类实现了人脸识别的功能
    private AFR_FSDKFace result_afr = new AFR_FSDKFace();//识别到的人脸信息
    private AFR_FSDKError err_afr = new AFR_FSDKError();//这个类用来保存虹软错误

    private AFR_FSDKFace mAFR_FSDKFace;//用于保存到数据库中的人脸特征信息

    private int noticesStatus = 0;//门禁卡信息版本状态(默认为0)// 0:不一致（等待下载数据）1:不一致（正在下载数据）
    private int faceStatus = 0;//人脸信息版本状态(默认为0)// 0:不一致（等待下载数据）1:不一致（正在下载数据）
    private int cardInfoStatus = 0;//门禁卡信息版本状态(默认为0)// 0:不一致（等待下载数据）1:不一致（正在下载数据）
    private String lastVersionStatus = "L"; //L: last version N: find new version D：downloading
    // P: pending to install I: installing  版本更新状态
    private int downloadingFlag = 0; //0：not downloading 1:downloading 2:stop download  apk下载状态
    private String mac_id; //心跳接口传
    private ActivityManager activityManager;//Activity管理类
    private boolean isPullTime = false;
    private Timer activityTimer = null;
    private Runnable startMain = new Runnable() {
        @Override
        public void run() {
            Intent i = new Intent(MainService.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MainService.this.startActivity(i);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "service启动");
        audioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        initHandler();
        // TODO: 2018/5/14 放在MainActivity中  initDB();
        initMacKey();
        //testTJ();
    }

    /**
     * 统计广告视频信息接口
     */
    private void tongjiVedio(Object obj) {
        Log.e(TAG, "开始上传统计视频信息");
        final List<AdTongJiBean> list = (List<AdTongJiBean>) obj;
        String json = JsonUtil.parseListToJson(list);
        Log.e(TAG, "广告视频统计请求 json " + json);
        OkHttpUtils.postString().url(API.ADV_TONGJI).content(json).mediaType(MediaType.parse("application/json; " +
                "charset=utf-8")).tag(this).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.i(TAG, "onError 上传广告视频统计信息失败  保存信息到数据库 " + e.toString());
                DbUtils.getInstans().addAllTongji(list);
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e(TAG, "onResponse" + response);
                if ("0".equals(JsonUtil.getFieldValue(response, "code"))) {
                    Log.i(TAG, "onResponse统计广告视频信息统计接口 上传统计信息成功");
                    List<AdTongJiBean> adTongJiBeen = DbUtils.getInstans().quaryTongji();
                    if (adTongJiBeen.size() > 0) {
                        Log.i(TAG, "本地数据库中--存在--视频的统计信息 开始上传离线");
                        lixianTongji(adTongJiBeen);
                    }
                } else {
                    Log.i(TAG, "上传广告视频统计信息失败  保存信息到数据库");
                    DbUtils.getInstans().addAllTongji(list);
                }
            }
        });
    }

    /**
     * 统计广告图片信息统计接口
     */
    private void tongjiPic(Object obj) {

        final List<AdTongJiBean> list = (List<AdTongJiBean>) obj;
        String json = JsonUtil.parseListToJson(list);
        Log.e(TAG, "广告图片统计请求 json " + json);
        OkHttpUtils.postString().url(API.ADV_TONGJI).content(json).mediaType(MediaType.parse("application/json; " +
                "charset=utf-8")).tag(this).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, "er");
                Log.i(TAG, "onError 上传广告图片统计信息失败  保存信息到数据库e " + e.toString());
                DbUtils.getInstans().addAllTongji(list);
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e(TAG, "onResponse 广告图片" + response);
                if ("0".equals(JsonUtil.getFieldValue(response, "code"))) {
                    Log.i(TAG, "onResponse上传广告图片统计信息成功 检查是否存在离线信息");
                    List<AdTongJiBean> adTongJiBeen = DbUtils.getInstans().quaryTongji();
                    if (adTongJiBeen.size() > 0) {
                        Log.i(TAG, "本地数据库中--存在--图片的统计信息 开始上传离线");
                        lixianTongji(adTongJiBeen);
                    }
                } else {
                    Log.i(TAG, "onResponse上传广告图片统计信息失败  保存信息到数据库");
                    DbUtils.getInstans().addAllTongji(list);
                }
            }
        });
    }

    /**
     * 离线统计信息上传
     *
     * @param list
     */
    private void lixianTongji(List<AdTongJiBean> list) {
        String json = JsonUtil.parseListToJson(list);
        Log.e(TAG, "上传离线视频图片统计请求 json " + json);
        OkHttpUtils.postString().url(API.ADV_TONGJI).content(json).mediaType(MediaType.parse("application/json; " +
                "charset=utf-8")).tag(this).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, "er");
                Log.e(TAG, "onError统计广告图片信息统计接口 上传离线统计信息失败 " + e.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e(TAG, "onResponse" + response);
                if ("0".equals(JsonUtil.getFieldValue(response, "code"))) {
                    Log.i(TAG, "onResponse上传离线视频广告统计信息成功 删除保存本地的信息");
                    DbUtils.getInstans().deleteAllTongji();

                } else {
                    Log.i(TAG, "onResponse 上传离线统计信息失败  保存信息到数据库");
                }
            }
        });

    }

    /**
     * 初始化handle
     */
    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MAIN_ACTIVITY_INIT:
                        mainMessage = msg.replyTo;
                        netWorkstate = (Boolean) msg.obj;
                        Log.i(TAG, "MainActivity初始化完成  MainServic开始初始化" + (netWorkstate ? "有网" : "没网"));
                        init();
                        break;
                    case MSG_RTC_REGISTER:
                        //登陆成功后
                        Log.i(TAG, "登陆成功后 rtc注册");
                        initTYSDK();// rtc注册
                        break;
                    case MSG_CALLMEMBER:
                        //呼叫成员
                        Log.i(TAG, "呼叫成员");
                        onCallMember(msg);
                        break;
                    case MSG_LOGIN:
                        Log.i(TAG, "登陆成功");
                        onLogin(msg);
                        break;
                    case MSG_CANCEL_CALL:
                        //取消呼叫
                        Log.i(TAG, "取消呼叫");
                        cancelCurrentCall();
                        break;
                    case MSG_DISCONNECT_VIEDO:
                        Log.i(TAG, "挂断正在通话的视频");
                        disconnectCallingConnection();
                        break;
                    case MSG_TONGJI_VEDIO:
                        Log.i(TAG, "统计广告视频消息");
                        tongjiVedio(msg.obj);
                        break;
                    case MSG_TONGJI_PIC:
                        Log.i(TAG, "统计广告图片消息");
                        tongjiPic(msg.obj);
                        break;
                    case MSG_START_DIAL:
                        Log.i(TAG, "开始呼叫");
                        String[] parameters = (String[]) msg.obj;
                        unitNo = parameters[0]; //号码
                        imageUrl = parameters[1]; //拍照路径
                        imageUuid = parameters[2]; //uuid
                        startCallMember();
                        break;
                    case MSG_START_DIAL_PICTURE:
                        Log.i(TAG, "开始发送呼叫访客呼叫图片地址");
                        String[] parameters1 = (String[]) msg.obj;
                        if (parameters1[2].equals(imageUuid)) {
                            imageUrl = parameters1[1];
                            Log.i(TAG, "访客图片地址" + imageUrl);
                            sendCallAppendImage();
                        }
                        break;
                    case MSG_CHECK_PASSWORD:
                        Log.i(TAG, "开始检查密码");
                        String[] parameters2 = (String[]) msg.obj;
                        tempKey = parameters2[0];
                        imageUrl = parameters2[1];
                        imageUuid = parameters2[2];
                        checkGuestPassword();
                        break;
                    case MSG_GUEST_PASSWORD_CHECK:
                        Log.i(TAG, "获取获取到服务器返回的密码");
                        onCheckGuestPassword((ResponseBean) msg.obj);
                        break;
                    case MSG_LIXIAN_PASSWORD_CHECK:
                        Log.i(TAG, "获取获取到离线验证密码");
                        onCheckLixianPassword(msg.obj == null ? null : (boolean) msg.obj);
                        break;
                    case MSG_CHECK_PASSWORD_PICTURE:
                        Log.i(TAG, "开始发送访客密码图片地址到服务器");
                        String[] parameters3 = (String[]) msg.obj;
                        tempKey = parameters3[0];
                        imageUrl = parameters3[1];
                        imageUuid = parameters3[2];
                        //   startCheckGuestPasswordAppendImage();
                        break;
                    case MSG_CARD_INCOME: {
                        //进行卡信息处理（判定及开门等）
                        String obj1 = (String) msg.obj;
                        onCardIncome(obj1);
                        Log.e(TAG, "onCardIncome obj1" + obj1);
                        break;
                    }
                    case MSG_UPDATE_NETWORKSTATE: {
                        netWorkstate = (boolean) msg.obj;
                        Log.e(TAG, "initWhenConnected obj1" + netWorkstate);
                        if (netWorkstate) {
                            initWhenConnected(); //开始在线版本
                        } else {
                            initWhenOffline(); //开始离线版本
                        }
                        break;
                    }
                    case REGISTER_ACTIVITY_DIAL:
                        initConnectReport();//心跳开始,根据心跳返回结果开启各更新程序
                        break;
                    case MSG_FACE_DOWNLOAD: {
                        // TODO: 2018/5/15 接收人脸URL,循环下载并录入
                        if (null != faceUrlList && faceUrlList.size() > 0) {
                            initFaceEngine();
                        }
                        break;
                    }
                    case MSG_FACE_OPENLOCK: { //脸开门
                        String[] parame = (String[]) msg.obj;
                        String phoneNum = parame[0];//手机号码
                        String picUrl = parame[1];//图片URL
                        if (!cardRecord.checkLastCard(phoneNum)) {//判断距离上次刷脸时间是否超过2秒
                            LogDoor data = new LogDoor();
                            data.setMac(mac);
                            data.setKaimenfangshi(3);
                            if (TextUtils.isEmpty(phoneNum)) {
                                data.setPhone("");
                            } else {
                                data.setPhone(phoneNum);
                            }
                            if (TextUtils.isEmpty(picUrl)) {
                                data.setKaimenjietu("");
                            } else {
                                data.setKaimenjietu(picUrl);
                            }
                            data.setKa_id("");
                            data.setState(1);
                            data.setKaimenshijian(StringUtils.transferLongToDate("yyyy-MM-dd HH:mm:ss",System.currentTimeMillis()));
                            data.setUuid("");
                            List<LogDoor> list = new ArrayList<>();
                            list.add(data);
                            createAccessLog(list);
                            openLock(3);
                        }
                        break;
                    }
                    case MSG_CARD_OPENLOCK: {
                        String pic_url = (String) msg.obj;
                        LogDoor data = new LogDoor();
                        data.setMac(mac);
                        data.setPhone(kaInfo.getYezhu_dianhua());
                        data.setKa_id(kaInfo.getKa_id());
                        data.setKaimenfangshi(1);
                        data.setState(1);
                        if (TextUtils.isEmpty(pic_url)) {
                            data.setKaimenjietu("");
                        } else {
                            data.setKaimenjietu(pic_url);
                        }
                        data.setKaimenshijian(StringUtils.transferLongToDate("yyyy-MM-dd HH:mm:ss",System.currentTimeMillis()));
                        data.setUuid("");
                        List<LogDoor> list = new ArrayList<>();
                        list.add(data);
                        createAccessLog(list);
                        break;
                    }
                    default:
                        break;
                }

            }
        };
        serviceMessage = new Messenger(mHandler);
    }

    /**
     * 开启心跳线程
     */
    private void initConnectReport() {
        //xiaozd add
        if (connectReportThread != null) {
            connectReportThread.interrupt();
            connectReportThread = null;
        }
        connectReportThread = new Thread() {
            @Override
            public void run() {
                try {
                    connectReport();//首次执行
                    while (!isInterrupted()) {//检测线程是否已经中断
                        sleep((Long) SPUtil.get(MainService.this, SP_XINTIAO_TIME, 60000L));
                        //心跳间隔时间
                        connectReport();
                    }
                } catch (InterruptedException e) {

                }
            }
        };
        connectReportThread.start();
    }

    private void startCheckGuestPasswordAppendImage() {
        new Thread() {
            @Override
            public void run() {
                checkGuestPasswordAppendImage();
            }
        }.start();
    }

    /**
     * 验证密码是上传图片
     */
    private void checkGuestPasswordAppendImage() {
        try {
            String url = DeviceConfig.SERVER_URL + "/app/device/appendImage?";
            if (imageUuid != null) {
                url = url + "imageUuid=" + URLEncoder.encode(this.imageUuid, "UTF-8");
            } else {
                return;
            }
            if (imageUrl != null) {
                url = url + "&imageUrl=" + URLEncoder.encode(this.imageUrl, "UTF-8");
            } else {
                return;
            }
            try {
                String result = HttpApi.getInstance().loadHttpforGet(url, httpServerToken);
                if (result != null) {
                    HttpApi.i("checkGuestPasswordAppendImage()->" + result);
                } else {
                    HttpApi.i("checkGuestPasswordAppendImage()->服务器异常");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
        }
    }

    /**
     * 验证密码
     */
    private void checkGuestPassword() {
        /**
         * 访客使用临时密码开门验证：服务器检查锁门禁发来的临时密码，是否与之前颁发的密码一致，一致开门，不一致呵呵，并记录本次日志。

         请求路径：xdoor/device/openDoorByTempKey
         请求参数：String mac 地址，Integer xiangmu_id 项目ID，loudong_id楼栋ID，
         String temp_key 临时密码
         返回示例：
         {
         code:”0”,
         msg:””,
         data:”{
         …
         }”
         }

         */
        if (!netWorkstate) {
            //断网 离线密码验证
            Message message = mHandler.obtainMessage();
            message.what = MSG_LIXIAN_PASSWORD_CHECK;

            String o = (String) SPUtil.get(MainService.this, SP_LIXIAN_MIMA, "123456");
            Log.i(TAG, "验证离线密码 本地储存的离线密码是：" + o + "  用户输入的密码是" + tempKey);
            if (o.equals(tempKey)) {
                Log.i(TAG, "离线密码验证成功");
                message.obj = true;
            } else {
                message.obj = false;
                Log.i(TAG, "离线密码验证失败");
            }
            mHandler.sendMessage(message);
            return;
        }

        try {
            String url = API.OPENDOOR_BYTEMPKEY;
            JSONObject data = new JSONObject();
            data.put("mac", mac);
            data.put("xiangmu_id", communityId + "");
            data.put("loudong_id", blockId + "");
            data.put("temp_key", this.tempKey);
            if (imageUrl != null) {
                data.put("kaimenjietu", this.imageUrl);
            }
            OkHttpUtils.postString().url(url).content(data.toString()).mediaType(MediaType.parse("application/json; "
                    + "charset=utf-8")).addHeader("Authorization", httpServerToken).tag(this).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    HttpApi.e("验证密码接口->服务器异常或没有网络" + e.toString());
                    Message message = mHandler.obtainMessage();
                    message.what = MSG_GUEST_PASSWORD_CHECK;
                    mHandler.sendMessage(message);
                }

                @Override
                public void onResponse(String response, int id) {
                    Message message = mHandler.obtainMessage();
                    message.what = MSG_GUEST_PASSWORD_CHECK;
                    HttpApi.e("验证密码接口->成功" + response);
                    if (null != response) {
                        ResponseBean responseBean = JsonUtil.parseJsonToBean(response, ResponseBean.class);
                        message.obj = responseBean;
                    }
                    mHandler.sendMessage(message);
                }
            });


        } catch (Exception e) {
            Message message = mHandler.obtainMessage();
            message.what = MSG_GUEST_PASSWORD_CHECK;
            mHandler.sendMessage(message);
            HttpApi.e("验证密码接口 catch->" + e.toString());
            e.printStackTrace();
        }
    }

    private void onCheckGuestPassword(ResponseBean result) {

        if (result != null && "0".equals(result.getCode())) {
            Log.e(TAG, "-----------------临时密码开门成功  开门开门------------------");
            openLock(6);
            List<LogDoor> list = new ArrayList<>();
            LogDoor logDoor = new LogDoor();
            logDoor.setMac(mac);
            logDoor.setKa_id("");
            logDoor.setUuid("");
            logDoor.setKaimenjietu(imageUrl == null ? "" : imageUrl);
            logDoor.setPhone("");
            logDoor.setState(1);
            logDoor.setMima(tempKey);
            logDoor.setKaimenshijian(StringUtils.transferLongToDate("yyyy-MM-dd HH:mm:ss",System.currentTimeMillis()));
            logDoor.setKaimenfangshi(6);
            Log.i(TAG, "上传临时密码开门日志" + "---logDoor=" + logDoor.toString());
            list.add(logDoor);
            createAccessLog(list);
        } else {
            Log.e(TAG, "--------------------临时密码开门失败 上传开门日志  --------------------");
//            List<LogDoor> list = new ArrayList<>();
//            LogDoor logDoor = new LogDoor();
//            logDoor.setMac(mac);
//            logDoor.setKa_id("-1");
//            logDoor.setUuid("");
//            logDoor.setKaimenjietu(imageUrl == null ? "" : imageUrl);
//            logDoor.setPhone("");
//            logDoor.setKaimenshijian(System.currentTimeMillis() + "");
//            logDoor.setKaimenfangshi("6");
//            Log.i(TAG, "上传临时密码开门日志" + "---logDoor=" + logDoor.toString());
//            list.add(logDoor);
//            createAccessLog(list);
        }

        sendMessageToMainAcitivity(MSG_PASSWORD_CHECK, result);
    }

    private void onCheckLixianPassword(Boolean result) {
        if (result != null) {
            if (result) {
                Log.e(TAG, "-----------------离线密码开门成功   上传开门日志 开门开门------------------");
                openLock(5);//调用开门接口
//                sendMessageToMainAcitivity(MSG_LOCK_OPENED, "");
                List<LogDoor> list = new ArrayList<>();
                LogDoor logDoor = new LogDoor();
                logDoor.setMac(mac);
                logDoor.setKa_id(""); //// TODO: 2018/6/2 离线密码1表示成功-1表示失败
                logDoor.setUuid("");
                logDoor.setState(1);
                logDoor.setMima(tempKey);
                logDoor.setKaimenjietu(imageUrl == null ? "" : imageUrl);
                logDoor.setPhone("");
                logDoor.setKaimenshijian(StringUtils.transferLongToDate("yyyy-MM-dd HH:mm:ss",System.currentTimeMillis()));
                logDoor.setKaimenfangshi(5);
                Log.i(TAG, "上传离线密码开门成功日志" + "---logDoor=" + logDoor.toString());
                list.add(logDoor);
                createAccessLog(list);
            } else {
                Log.e(TAG, "--------------------离线密码开门失败   上传开门日志--------------------");
                List<LogDoor> list = new ArrayList<>();
                LogDoor logDoor = new LogDoor();
                logDoor.setMac(mac);
                logDoor.setKa_id(""); //离线密码1表示成功-1表示失败
                logDoor.setUuid("");
                logDoor.setState(-1);
                logDoor.setMima(tempKey);
                logDoor.setKaimenjietu(imageUrl == null ? "" : imageUrl);
                logDoor.setPhone("");
                logDoor.setKaimenshijian(StringUtils.transferLongToDate("yyyy-MM-dd HH:mm:ss",System.currentTimeMillis()));
                logDoor.setKaimenfangshi(5);
                Log.i(TAG, "上传离线密码开门失败日志" + "---logDoor=" + logDoor.toString());
                list.add(logDoor);
                createAccessLog(list);
            }
        }
        sendMessageToMainAcitivity(MSG_LIXIAN_PASSWORD_CHECK_AFTER, result);
    }

    /**
     * 心跳接口
     */
    private void connectReport() {
        /**
         * 锁门禁向服务器发送的心跳包：每间隔一段时间发送一次（间隔时间由服务器返回,默认1分钟），服务器接到心跳包后，返回心跳发送间隔(s) ,服务器时间()
         * ，卡最后更新时间（版本），广告最后更新时间（版本），人脸最后更新时间（版本），通告最后更新时间（版本）,离线密码，版本号，服务器记录本次心跳时间，用于后续判断锁门禁是否在线
         *请求路径：xdoor/device/connectReport
         *请求参数：String mac 地址，Integer xiangmu_id 项目ID，String date锁门禁时间(毫秒值)，String version版本号（APP版本)
         */
        String url = API.CONNECT_REPORT;
        try {
            JSONObject data = new JSONObject();
            data.put("mac", mac);
            data.put("mac_id", mac_id);
            OkHttpUtils.postString().url(url).content(data.toString()).mediaType(MediaType.parse("application/json; "
                    + "charset=utf-8")).addHeader("Authorization", httpServerToken).tag(this).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    Log.e(TAG, "onError 心跳接口 connectReport " + e.toString());
                }

                @Override
                public void onResponse(String response, int id) {
                    Log.e(TAG, "onResponse 心跳接口 connectReport " + response);

                    if (null != response) {
                        String code = JsonUtil.getFieldValue(response, "code");
                        if (code.equals("0")) {

                            /**
                             * {"id":1,"ka":"1","ka_gx":"2018-05-09 17:20:50","lian":"1",
                             * "lian_gx":"2018-05-09
                             * 17:20:42","guanggaopic":"1","guanggaovideo":"",
                             * "guanggao_gx":"2018-05-09 17:56:49",
                             * "tonggao":"1","tonggao_gx":"2018-05-09 17:20:46",
                             * "mac":"44:2c:05:e6:9c:c5",
                             * "xiangmu_id":346,"xdoor":null,"xintiao_time":300,
                             * "fuwuqi_time":"1526610665487",
                             * "lixian_mima":"123465","version":"1.00","token":null}
                             */
                            String result = JsonUtil.getResult(response);
                            ConnectReportBean connectReportBean = JsonUtil.parseJsonToBean(result, ConnectReportBean
                                    .class);
                            //测试上传日志
                            //test();

                            if (StringUtils.isNoEmpty(connectReportBean.getLixian_mima())) {
                                Log.i(TAG, "心跳--服务器返回的离线密码" + connectReportBean.getLixian_mima());
                                SPUtil.put(MainService.this, Constant.SP_LIXIAN_MIMA, connectReportBean
                                        .getLixian_mima());
                            }
                            Log.i(TAG, "心跳--服务器返回的心跳时间（秒）" + (long) (connectReportBean.getXintiao_time() * 1000));
                            if (0L != ((long) (connectReportBean.getXintiao_time()))) {
                                SPUtil.put(MainService.this, Constant.SP_XINTIAO_TIME, (long) (connectReportBean
                                        .getXintiao_time() * 1000));
                            } else {
                                SPUtil.put(MainService.this, Constant.SP_XINTIAO_TIME, (long) (60000));
                            }
                            if (StringUtils.isNoEmpty(connectReportBean.getKa())) {
                                long kaVision = (long) SPUtil.get(MainService.this, Constant.SP_VISION_KA, 0L);
                                Log.i(TAG, "心跳--当前卡版本：" + kaVision + "   服务器卡版本：" + Long.parseLong(connectReportBean
                                        .getKa()));
                                if (Long.parseLong(connectReportBean.getKa()) > kaVision) {
                                    Log.i(TAG, "心跳中有卡信息更新");
                                    getCardInfo(Long.parseLong(connectReportBean.getKa()));
                                }
                            }
                            if (StringUtils.isNoEmpty(connectReportBean.getLian())) {
                                long lianVision = (long) SPUtil.get(MainService.this, Constant.SP_VISION_LIAN, 0L);
                                if (Long.parseLong(connectReportBean.getLian()) > lianVision) {
                                    Log.i(TAG, "心跳中有脸信息更新");
                                    if (faceStatus == 0) {
                                        //判断是否正在下载
//                                    getFaceUrlInfo();
                                    }
                                }
                            }
                            if (StringUtils.isNoEmpty(connectReportBean.getTupian())) {
                                long guanggaoVision = (long) SPUtil.get(MainService.this, Constant
                                        .SP_VISION_GUANGGAO, 0L);
                                Log.i(TAG, "心跳--当前广告图片版本：" + guanggaoVision + "   服务器广告图片版本：" + Long.parseLong
                                        (connectReportBean.getTupian()));
                                if (Long.parseLong(connectReportBean.getTupian()) > guanggaoVision) {
                                    Log.i(TAG, "心跳中有广告图片信息更新");
                                    getTupian(Long.parseLong(connectReportBean.getTupian()));
                                }
                            }
                            if (StringUtils.isNoEmpty(connectReportBean.getGuanggao())) {
                                long guanggaoVadioVision = (long) SPUtil.get(MainService.this, Constant
                                        .SP_VISION_GUANGGAO_VIDEO, 0L);
                                Log.i(TAG, "心跳--当前广告视频版本：" + guanggaoVadioVision + "   " + "服务器广告视频版本：" + Long
                                        .parseLong(connectReportBean.getGuanggao()));
                                if (Long.parseLong(connectReportBean.getGuanggao()) > guanggaoVadioVision) {
                                    Log.i(TAG, "心跳中有广告视频信息更新");
                                    getGuanggao(Long.parseLong(connectReportBean.getGuanggao()));
                                }
                            }
                            //// TODO: 2018/5/17 拿app版本信息 去掉点
                            if (StringUtils.isNoEmpty(connectReportBean.getVersion())) {
                                String appVision = (String) SPUtil.get(MainService.this, Constant.SP_VISION_APP,
                                        getVersionName());
                                Log.i(TAG, "心跳--当前app版本：" + appVision + "   服务器app版本：" + (connectReportBean
                                        .getVersion()));
                                if (Integer.parseInt(connectReportBean.getVersion().replace(".", "")) > Integer
                                        .parseInt(appVision.replace(".", ""))) {
                                    Log.i(TAG, "心跳中有APP信息更新");
                                    if (lastVersionStatus.equals("D")) {//正在下载最新包
                                        //不获取地址
                                        Log.e(TAG, "正在下载最新包,不获取地址");
//                                    setDownloadingFlag(2);
                                    } else if (lastVersionStatus.equals("P")) {//已下载,未安装
                                        // TODO: 2018/4/28 这里的数据fileName会做成成员变量，以供使用
//                                    lastVersionStatus = "I";//版本状态设为正在安装
//                                    updateApp(fileName);
                                        //不获取地址
                                        Log.e(TAG, "已下载等待未安装,不获取地址");
                                    } else if (lastVersionStatus.equals("I")) {//已下载,安装中
                                        //不获取地址
                                        Log.e(TAG, "已下载,安装中,不获取地址");
                                    } else if (lastVersionStatus.equals("L")) {//未下载状态
                                        getVersionInfo();
                                    } else if (lastVersionStatus.equals("N")) {//正在获取下载地址
                                        //不获取地址
                                        Log.e(TAG, "未下载,正在获取下载地址,不获取地址");
                                    }
                                }
                            }
                            if (StringUtils.isNoEmpty(connectReportBean.getTonggao())) {
                                long tonggaoVision = (long) SPUtil.get(MainService.this, Constant.SP_VISION_TONGGAO,
                                        0L);
                                if (Long.parseLong(connectReportBean.getTonggao()) > tonggaoVision) {
                                    Log.i(TAG, "心跳中有通告信息更新");
                                    if (noticesStatus == 0) {//判断是否正在下载
                                        getTongGaoInfo(Long.parseLong(connectReportBean.getTonggao()));
                                    }
                                }
                            }
                            //查询数据库中是否有离线照片
                            List<ImgFile> imgFiles = DbUtils.getInstans().quaryImg();
                            if (imgFiles!=null&&imgFiles.size()>0){
                                Log.i(TAG, "存在"+imgFiles.size()+"张离线照片");
                                sendMessageToMainAcitivity(MSG_UPLOAD_LIXIAN_IMG,imgFiles);
                            }
                        } else {
                            //服务器异常或没有网络
                            HttpApi.e("getClientInfo()->服务器无响应");
                            Log.e(TAG, "onResponse 心跳接口 connectReport " + response);
                        }
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取广告图片接口
     *
     * @param v
     */

    private int adpicInfoStatus = 0;

    private void getTupian(final long v) {
        if (adpicInfoStatus == 0) {
            adpicInfoStatus = 1;
            String url = API.CALLALL_ADS;
            Log.e(TAG, "获取广告url= " + url);
            Map<String, String> map = new HashMap<>();
            map.put("mac", mac);
            map.put("leixing", "2");
            String json = JsonUtil.parseMapToJson(map);
            Log.i(TAG, "getTupian请求图片接口 json=" + json);
            OkHttpUtils.postString().url(url).content(json).mediaType(MediaType.parse("application/json; " +
                    "charset=utf-8")).addHeader("Authorization", httpServerToken).tag(this).build().execute(new StringCallback() {

                @Override
                public void onError(Call call, Exception e, int id) {
                    Log.e(TAG, "onError 获取广告图片接口 getTupian  Exception=" + e.toString());
                    adpicInfoStatus = 0;
                }

                @Override
                public void onResponse(String response, int id) {
                    Log.i(TAG, "onResponse 获取广告图片接口 getTupian  response=" + response);
                    if (response == null || "".equals(response)) {
                        adpicInfoStatus = 0;
                        return;
                    }
                    String code = JsonUtil.getFieldValue(response, "code");
                    if ("0".equals(code)) {
                        String result = JsonUtil.getResult(response);
                        String guanggao = JsonUtil.getFieldValue(result, "guanggao");
                        try {
                            List<GuangGaoBean> guangGaoBeen = JsonUtil.fromJsonArray(guanggao, GuangGaoBean.class);
                            Log.i(TAG, "获取广告图片接口 guangGaoBeen " + guangGaoBeen);
                            sendMessageToMainAcitivity(MSG_ADVERTISE_REFRESH_PIC, guangGaoBeen);
                            adpicInfoStatus = 0;
                            syncCallBack("3", v);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        adpicInfoStatus = 0;
                    }
                }
            });

        }
    }

    /**
     * 下载app文件
     *
     * @param address
     * @param fileName
     */
    private void downloadApp(String address, String fileName) {
        Log.v(TAG, "UpdateService " + "开始下载APK");
        lastVersionStatus = "D";//正在下载最新包

        String lastFile = downloadFile(address, fileName);
        Log.v(TAG, "UpdateService " + "  " + "fileName:" + " " + fileName);

        if (lastFile != null) {//下载完成
            if (lastVersionStatus.equals("D")) {
                Log.v(TAG, "UpdateService " + "  " + "lastFile:" + " " + lastFile.length());
                Log.v("UpdateService", "change status to P");
                lastVersionStatus = "P";//已下载,未安装
                if (lastVersionStatus.equals("P")) {//版本状态为待安装
                    //开始安装
                    // TODO: 2018/4/28 这里要把数据fileName做成成员变量，以供使用
                    updateApp(fileName);
                }
            } else {
                Log.v("UpdateService", "不会出现这种情况 status is " + lastVersionStatus);
//                lastVersionStatus = "L";
            }
        } else {
            // TODO: 2018/5/18  下载失败，整理.temp文件  absolutePath为apk存储路径的文件夹
            String absolutePath = Environment.getExternalStorageDirectory() + "/" + LOCAL_APK_PATH + "/" + fileName +
                    ".apk";
            File file = new File(absolutePath);
            if (file.exists()) {
//                Log.e("下载", "删除");
                file.delete();
            }
            lastVersionStatus = "L";//等待下次心跳重新获取URL下载apk文件
        }
    }

    /**
     * 获取APP更新路径接口
     */
    private void getVersionInfo() {
        lastVersionStatus = "N";
        try {
            String url = API.VERSION_ADDRESS;
            Map<String, String> map = new HashMap<>();
            map.put("mac", mac);
            map.put("version", getVersionName());
            map.put("mac_id", mac_id);
            OkHttpUtils.postString().url(url).content(JsonUtil.parseMapToJson(map)).mediaType(MediaType.parse
                    ("application/json; " + "charset=utf-8")).addHeader("Authorization", httpServerToken).tag(this)
                    .build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    Log.i(TAG, "onError 获取app下载地址" + e.toString());
                    lastVersionStatus = "L";//等待下次心跳重新获取URL
                }

                @Override
                public void onResponse(String response, int id) {
                    Log.i(TAG, "onResponse 获取app下载地址" + response);
                    if ("0".equals(JsonUtil.getFieldValue(response, "code"))) {
                        String result = JsonUtil.getResult(response);
                        final String address = JsonUtil.getFieldValue(result, "version");
                        final String fileName = "door";
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                downloadApp(address, fileName);
                            }
                        }).start();
                    } else {
                        lastVersionStatus = "L";//等待下次心跳重新获取URL

                    }

                }
            });

        } catch (Exception e) {
            HttpApi.e("connectReportInfo()->服务器数据解析异常");
            e.printStackTrace();
            lastVersionStatus = "L";//等待下次心跳重新获取URL
        }
    }

    /**
     * 更新应用(保险起见，开子线程前先判断一下)
     *
     * @param lastVersionFile 文件名
     */
    protected void updateApp(String lastVersionFile) {
        String SDCard = Environment.getExternalStorageDirectory() + "";
        String fileName = SDCard + "/" + LOCAL_APK_PATH + "/" + lastVersionFile + ".apk";//文件存储路径;
        File file = new File(fileName);
        Log.v(TAG, "UpdateService " + "------>start Update App<------");
        if (true) {//如果本地版本低于网络版本
            if (file.exists()) {
                lastVersionStatus = "I";//版本状态设为正在安装
                Log.v(TAG, "UpdateService:" + "check update file OK");
                startInstallApp(fileName);
                Log.i(TAG, "UpdateService:" + fileName);
            }
        } else {
//            ToastUtil.showToast("版本已是最新");
        }
    }

    /**
     * 安装apk
     *
     * @param fileName
     */
    protected void startInstallApp(final String fileName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String cmd = "pm install -r " + fileName;
                HttpApi.i("UpdateService安装命令：" + cmd);
                //以下两个方法应该等效
                ShellUtils.CommandResult result = InstallUtil.executeCmd(cmd);
//                ShellUtils.CommandResult commandResult = ShellUtils.execCommand(cmd, false);
                HttpApi.i("UpdateService安装结果：" + result.toString());
            }
        }).start();
    }

    /**
     * 下载新apk
     *
     * @param url      下载地址
     * @param fileName 文件名(带后缀.temp)
     * @return
     */
    public String downloadFile(String url, final String fileName) {
        OutputStream output = null;
        String localFile = null;
        String result = null;
        setDownloadingFlag(1);
        try {
            URL urlObject = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlObject.openConnection();
            String SDCard = Environment.getExternalStorageDirectory() + "";
            localFile = SDCard + "/" + LOCAL_APK_PATH + "/" + fileName + ".apk";//文件存储路径
            Log.v(TAG, "downloadFile " + "File path: " + localFile);
            File file = new File(localFile);
            InputStream input = conn.getInputStream();
            File fileParent = file.getParentFile();
            if (!fileParent.exists()) {
                fileParent.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();//新建文件
            } else {
                Log.v(TAG, "downloadFile " + "File is exists ");
                file.delete();
            }
            output = new FileOutputStream(file);
            //读取大文件
            byte[] buffer = new byte[1024 * 8];
            BufferedInputStream in = new BufferedInputStream(input, 1024 * 8);
            BufferedOutputStream out = new BufferedOutputStream(output, 1024 * 8);
            int count = 0, n = 0;
            try {
                while ((n = in.read(buffer, 0, 1024 * 8)) != -1 && getDownloadingFlag() == 1) {
                    out.write(buffer, 0, n);
                    count += n;
                }
                out.flush();
                if (getDownloadingFlag() == 1) {
                    result = localFile;
                    Log.v(TAG, "downloadFile " + "result:  " + result + "  getDownloadingFlag=" + 1);
                }
                Log.v(TAG, "downloadFile " + "result:  " + result + "  getDownloadingFlag=" + getDownloadingFlag());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    result = null;
                }
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    result = null;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (Exception e) {
                result = null;
            }
        }
        setDownloadingFlag(0);
        Log.v(TAG, "UpdateService " + "download file end=" + result);
        //下载完毕
        return result;
    }

    protected synchronized void setDownloadingFlag(int flag) {
        downloadingFlag = flag;
    }

    protected synchronized int getDownloadingFlag() {
        return downloadingFlag;
    }

    /**
     * 获取人脸URL接口
     */

    private void getFaceUrlInfo() {
        faceStatus = 1;//正在下载
        try {
            //开始获取人脸信息
            String url = API.CALLALL_FACES;

            JSONObject data = new JSONObject();
            data.put("mac", mac);
            Log.e(TAG, "人脸URL mac " + mac + " url " + url);

            OkHttpUtils.postString().url(url).content(data.toString()).mediaType(MediaType.parse("application/json; "
                    + "charset=utf-8")).addHeader("Authorization", httpServerToken).tag(this).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    Log.e(TAG, "人脸URL 服务器异常或没有网络 " + e.toString());
                    faceStatus = 0;//等待下载数据
                }

                @Override
                public void onResponse(String response, int id) {
                    Log.e("wh response 人脸URL", response);
                    if (null != response) {
                        String code = JsonUtil.getFieldValue(response, "code");
                        if ("0".equals(code)) {
                            String result = JsonUtil.getResult(response);
                            String list = JsonUtil.getFieldValue(result, "lian");//服务器字段命名错误
                            faceUrlList = (ArrayList<FaceUrlBean>) JsonUtil.parseJsonToList(list, new
                                    TypeToken<List<FaceUrlBean>>() {
                            }.getType());

                            //通知MainActivity开始人脸录入流程
                            sendMessageToMainAcitivity(MSG_FACE_INFO, null);
                            // TODO: 2018/5/17 完全录入完成才改状态 faceStatus = 0;//修改状态，等待下次（新）数据
                        } else {
                            faceStatus = 0;//等待下载数据
                        }
                    } else {
                        //服务器异常或没有网络
                        HttpApi.e("人脸URL getClientInfo()->服务器无响应");
                        faceStatus = 0;//等待下载数据
                    }
                }
            });
        } catch (Exception e) {
            HttpApi.e("人脸URL getClientInfo()->服务器数据解析异常");
            e.printStackTrace();
            faceStatus = 0;//等待下载数据
        }
    }

    /**
     * 获取广告视频接口
     *
     * @param v
     */
    private int adInfoStatus = 0;//广告信息状态(默认为0) 0:不一致（等待下载数据）1:不一致（正在下载数据）

    private void getGuanggao(final long v) {
        if (adInfoStatus == 0) {
            adInfoStatus = 1;
            String url = API.CALLALL_ADS;
            try {
                Log.e(TAG, "获取广告url= " + url);
                JSONObject data = new JSONObject();
                data.put("mac", mac);
                data.put("leixing", "1");
                OkHttpUtils.postString().url(url).content(data.toString()).mediaType(MediaType.parse
                        ("application/json; " + "charset=utf-8")).addHeader("Authorization", httpServerToken).tag
                        (this).build().execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(TAG, "onError 获取广告接口 getGuangGao  Exception=" + e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e(TAG, "onResponse 获取广告接口 getGuangGao  response=" + response);
                        if (null != response) {
                            String code = JsonUtil.getFieldValue(response, "code");
                            if ("0".equals(code)) {
                                try {
                                    String result = JsonUtil.getResult(response);
                                    String guanggao = JsonUtil.getFieldValue(result, "guanggao");
                                    final List<GuangGaoBean> guangGaoBeen = JsonUtil.fromJsonArray(guanggao,
                                            GuangGaoBean.class);
//                                GuangGaoBean guangGaoBeen1 = new GuangGaoBean();
//                                guangGaoBeen1.setLeixing("2");
//                                guangGaoBeen1.setNeirong("http://img.taopic" + "" + "" + "" +
// "" + "" + "" + "" + ""
//                                        + ".com/uploads/allimg/120727/201995-120HG1030762.jpg");
//                                guangGaoBeen.add(guangGaoBeen1);
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                if (guangGaoBeen != null && guangGaoBeen.size() > 0) {
                                                    downloadAdvertisement(guangGaoBeen);
                                                    adjustAdvertiseFiles();
                                                    restartAdvertise(guangGaoBeen);
                                                    removeAdvertiseFiles();
                                                }
                                                syncCallBack("5", v);//同步视频

                                                adInfoStatus = 0;//重置广告视频下载状态
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    adInfoStatus = 0;
                                    Log.e(TAG, "视频广告下载出错" + adInfoStatus);
                                }
                            } else {
                                adInfoStatus = 0;//等待下载数据
//                                sendMessageToMainAcitivity(MSG_ADVERTISE_REFRESH, null);
//                                syncCallBack("5", v);//同步视频
                            }
                        } else {
                            //服务器异常或没有网络
                            HttpApi.e("getClientInfo()->服务器无响应");
                            adInfoStatus = 0;//等待下载数据
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private List<GuangGaoBean> currentAdvertisementList = new ArrayList<>();

    protected void restartAdvertise(List<GuangGaoBean> rows) {
        if (!isAdvertisementListSame(rows)) {
            //比较当前广告信息列表和服务器的数据是否一致，不一致则通过下面的方法重新播放
            sendMessageToMainAcitivity(MSG_ADVERTISE_REFRESH, rows);
        }
        //更新当前广告信息列表
        currentAdvertisementList = rows;
    }

    protected boolean isAdvertisementListSame(List<GuangGaoBean> rows) {
        String thisValue = currentAdvertisementList.toString();
        String thisRow = rows.toString();
        Log.d(TAG, "UpdateAdvertise: thisValue" + thisValue);
        Log.d(TAG, "UpdateAdvertise: thisRow" + thisRow);
        return thisRow.equals(thisValue);
    }

    protected void removeAdvertiseFiles() {
        File[] files = HttpUtils.getAllLocalFiles();
        //遍历本地文件,如果有临时文件或多余文件(即与服务器数据不一致的),删除
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            String fileName = file.getAbsolutePath();
            if (fileName.endsWith(".temp")) {
                file.delete();
            } else if (!currentAdvertisementFiles.containsValue(fileName)) {
                file.delete();
            }
        }
    }

    /**
     * 整理广告文件(这里的用处就是把.temp后缀去掉)
     */
    protected void adjustAdvertiseFiles() {
        Log.i(TAG, "adjustAdvertiseFiles");
        String SDCard = Environment.getExternalStorageDirectory() + "";
        String localFilePath = SDCard + "/" + DeviceConfig.LOCAL_FILE_PATH + "/";//文件存储路径
        Enumeration<String> keys = currentAdvertisementFiles.keys();
        //遍历本地广告文件名集合,如果有新的.temp文件，重命名
        while (keys.hasMoreElements()) {
            String fileName = keys.nextElement();
            String filePath = currentAdvertisementFiles.get(fileName);
            File file = new File(filePath + ".temp");
            if (file.exists()) {
                file.renameTo(new File(filePath));
            }
        }
    }

    protected void downloadAdvertisement(List<GuangGaoBean> rows) {
        //遍历外层广告列表
        for (int i = 0; i < rows.size(); i++) {
            GuangGaoBean row = rows.get(i);
            HttpApi.i("downloadAdvertisement->" + row.toString());
            //传入内层广告列表信息
            downloadAdvertisementItemFiles(row);

        }
    }


    protected void downloadAdvertisementItemFiles(GuangGaoBean item) {

        String type = item.getLeixing();
        if ("1".equals(type)) {//判断类别为视频
            Log.i(TAG, "广告为 视频");
            try {
                downloadAdvertisementFile(item.getNeirong());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("2".equals(type)) {//判断类别为图片
            Log.i(TAG, "广告为 图片");
            try {
                downloadAdvertisementFile(item.getNeirong());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 下载广告
     *
     * @param file
     * @throws Exception
     */
    protected void downloadAdvertisementFile(String file) throws Exception {
        int lastIndex = file.lastIndexOf("/");
        String fileName = file.substring(lastIndex + 1);
        Log.e("filename", fileName);
        //包含.mp4去掉
        if (fileName.contains(".")) {
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
            Log.e("filename .", fileName);
        }
        //根据文件名返回本地路径
        String localFile = HttpUtils.getLocalFile(fileName);
        if (localFile == null) {
            Log.i(TAG, "准备下载广告");
            //如果本地没有对应文件,则下载文件至本地
            localFile = HttpUtils.downloadFile(file);
            Log.i(TAG, "广告----" + localFile);
            if (localFile != null) {
                Log.i(TAG, "加载本地广告");
                if (localFile.endsWith(".temp")) {
                    localFile = localFile.substring(0, localFile.length() - 5);
                }
                //将文件名和本地路径塞入集合
                currentAdvertisementFiles.put(fileName, localFile);
            }
        } else {
            //本地已有文件（之前已下载成功）,将文件名和本地路径塞入集合
            currentAdvertisementFiles.put(fileName, localFile);
        }
    }

    /**
     * 获取通告信息接口
     *
     * @param version
     */
    private void getTongGaoInfo(final long version) {
        noticesStatus = 1;//正在下载，避免重复下载
        try {
            String url = API.CALLALL_NOTICES;
            JSONObject data = new JSONObject();
            data.put("mac", mac);

            OkHttpUtils.postString().url(url).content(data.toString()).mediaType(MediaType.parse("application/json; "
                    + "charset=utf-8")).addHeader("Authorization", httpServerToken).tag(this).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    Log.e(TAG, "通告信息 服务器异常或没有网络 " + e.toString());
                    noticesStatus = 0;//等待下载数据
                }

                @Override
                public void onResponse(String response, int id) {
                    Log.e("wh response 通告信息", response);

                    if (null != response) {
                        String code = JsonUtil.getFieldValue(response, "code");
                        if ("0".equals(code)) {
                            String result = JsonUtil.getResult(response);
                            String tonggao = JsonUtil.getFieldValue(result, "guanggao");//服务器字段命名错误

                            Log.e(TAG, "设置通告notice" + tonggao);
                            //通知主线程，显示通知
                            sendMessageToMainAcitivity(MSG_GET_NOTICE, tonggao);

                            //调用更新通知接口
                            syncCallBack("4", version);
                            noticesStatus = 0;//修改状态，等待下次（新）数据
                        } else {
                            noticesStatus = 0;//等待下载数据
                        }
                    } else {
                        //服务器异常或没有网络
                        HttpApi.e("通告信息 getClientInfo()->服务器无响应");
                        noticesStatus = 0;//等待下载数据
                    }
                }
            });

        } catch (Exception e) {
            noticesStatus = 0;//等待下载数据
            HttpApi.e("通告信息 getClientInfo()->服务器数据解析异常");
            e.printStackTrace();
        }
    }


    /**
     * 获取门禁卡信息接口
     */
    private void getCardInfo(final long kaVison) {
        if (cardInfoStatus == 0) {
            Log.i(TAG, "getCardInfo正在进行卡信息下载");
            cardInfoStatus = 1;//正在下载
            try {
                //开始获取门禁卡信息
                String url = API.CALLALL_CARDS;
                JSONObject data = new JSONObject();
                data.put("mac", mac);
                OkHttpUtils.postString().url(url).content(data.toString()).mediaType(MediaType.parse
                        ("application/json; " + "charset=utf-8")).addHeader("Authorization", httpServerToken).tag
                        (this).build().execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(TAG, "onError 卡信息接口getCardInfo" + e.toString());
                        cardInfoStatus = 0;//修改状态，等待下次（新）数据
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, "onResponse 卡信息接口getCardInfo  onResponse" + response);
                        if (null != response) {
                            String code = JsonUtil.getFieldValue(response, "code");
                            if ("0".equals(code)) {
                                try {
                                    String result = JsonUtil.getResult(response);
                                    List<Ka> kas = JsonUtil.fromJsonArray(result, Ka.class);
                                    //保存卡信息成功
                                    DbUtils.getInstans().addAllKa(kas);
                                    //查询卡信息成功
                                    //DbUtils.getInstans().quaryAllKa();

                                    syncCallBack("1", kaVison);
                                    cardInfoStatus = 0;//修改状态，等待下次（新）数据
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.i(TAG, "onResponse 卡信息接口getCardInfo  catch" + e.toString());
                                    cardInfoStatus = 0;//修改状态，等待下次（新）数据
                                }

                            } else {
                                Log.i(TAG, "onResponse 卡信息接口getCardInfo  code" + code);
                                cardInfoStatus = 0;//修改状态，等待下次（新）数据
                            }
                        } else {
                            //服务器异常或没有网络
                            Log.i(TAG, "onResponse 卡信息接口getCardInfo  服务器异常或没有网络");
                            cardInfoStatus = 0;//等待下载数据
                        }
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "catch 卡信息接口getCardInfo  Exception" + e.toString());
                e.printStackTrace();
                cardInfoStatus = 0;//等待下载数据
            }
        }
    }

    /**
     * 同步完成更新的信息信息
     *
     * @param type   1 卡，2 人脸，3 图片广告，4 通告 ，5.视频广告
     * @param vision 版本
     */
    private void syncCallBack(final String type, final long vision) {
        try {
            //开始获取门禁卡信息
            String url = API.SYNC_CALLBACK;
            JSONObject data = new JSONObject();
            data.put("mac", mac);
            data.put("type", type);
            data.put("version", vision + "");
            data.put("time", System.currentTimeMillis() + "");
            OkHttpUtils.postString().url(url).content(data.toString()).mediaType(MediaType.parse("application/json; "
                    + "charset=utf-8")).addHeader("Authorization", httpServerToken).tag(this).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    Log.e(TAG, "onError同步完成更新信息接口   syncCallBack() type+" + type + "   Exception" + e.toString());
                }

                @Override
                public void onResponse(String response, int id) {
                    Log.i(TAG, "onResponse同步完成更新信息接口   syncCallBack() type+" + type + "   " + "response" + response);
                    if (null != response) {
                        String code = JsonUtil.getFieldValue(response, "code");
                        if ("0".equals(code)) {
                            switch (type) {
                                case "1":
                                    //保存卡信息的版本
                                    SPUtil.put(MainService.this, SP_VISION_KA, vision);
                                    break;
                                case "2":
                                    break;
                                case "3":
                                    //保存图片广告信息的版本
                                    SPUtil.put(MainService.this, SP_VISION_GUANGGAO, vision);
                                    break;
                                case "4":
                                    // 保存通告告信息的版本
                                    SPUtil.put(MainService.this, Constant.SP_VISION_TONGGAO, vision);
                                    break;
                                case "5":
                                    //保存最新广告视频版本
                                    SPUtil.put(MainService.this, Constant.SP_VISION_GUANGGAO_VIDEO, vision);
                                    break;
                                default:
                                    break;
                            }
                        } else {
                            Log.e(TAG, "同步信息失败+ type " + type);
                        }
                    } else {
                        //服务器异常或没有网络
                        Log.e(TAG, "同步信息失败+ type " + type);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 保存心跳接口返回信息
     *
     * @param connectReportBean
     */
    private void saveVisionInfo(ConnectReportBean connectReportBean) {
        SPUtil.put(MainService.this, Constant.SP_VISION_APP, connectReportBean.getVersion());
        SPUtil.put(MainService.this, Constant.SP_LIXIAN_MIMA, connectReportBean.getLixian_mima());
        SPUtil.put(MainService.this, Constant.SP_VISION_KA, connectReportBean.getKa());
        SPUtil.put(MainService.this, Constant.SP_VISION_GUANGGAO, connectReportBean.getTupian());
        SPUtil.put(MainService.this, Constant.SP_VISION_GUANGGAO_VIDEO, connectReportBean.getTupian());
        SPUtil.put(MainService.this, Constant.SP_VISION_LIAN, connectReportBean.getLian());
        SPUtil.put(MainService.this, Constant.SP_VISION_TONGGAO, connectReportBean.getTonggao());
    }

    /**
     * 重置广告，图片，通知版本为0，下次登录时重新加载
     */
    private void saveVisionInfo() {
//// TODO: 2018/5/28 清除版本 暂时先把卡版本也清除
        SPUtil.put(MainService.this, Constant.SP_VISION_KA, 0L);
        SPUtil.put(MainService.this, Constant.SP_VISION_GUANGGAO, 0L);
        SPUtil.put(MainService.this, Constant.SP_VISION_GUANGGAO_VIDEO, 0L);
        SPUtil.put(MainService.this, Constant.SP_VISION_TONGGAO, 0L);

//        Log.e(TAG, "广告，图片，通知版本" + SPUtil.get(MainService.this, Constant.SP_VISION_GUANGGAO, 0f));
    }

    /**
     * 比对心跳接口当前版本信息
     *
     * @param connectReportBean
     */
    private void comparisonVisionInfo(ConnectReportBean connectReportBean) {
        SPUtil.put(MainService.this, Constant.SP_VISION_APP, connectReportBean.getVersion());
        SPUtil.put(MainService.this, Constant.SP_LIXIAN_MIMA, connectReportBean.getLixian_mima());
        SPUtil.put(MainService.this, Constant.SP_VISION_KA, connectReportBean.getKa());
        SPUtil.put(MainService.this, Constant.SP_VISION_GUANGGAO, connectReportBean.getTupian());
        SPUtil.put(MainService.this, Constant.SP_VISION_GUANGGAO_VIDEO, connectReportBean.getGuanggao());
        SPUtil.put(MainService.this, Constant.SP_VISION_LIAN, connectReportBean.getLian());
        SPUtil.put(MainService.this, Constant.SP_VISION_TONGGAO, connectReportBean.getTonggao());
    }

    protected void init() {

        initAexUtil(); //安卓工控设备控制器初始化
        Log.i("MainService", "init AEX");
        // TODO: 2018/5/8  initSqlUtil();  初始化卡相关数据库工具类
        Log.i("MainService", "init SQL");
        initCheckTopActivity();//检查最上层界面

        //xiaozd add
        if (netWorkstate) {
            initWhenConnected(); //开始在线版本
        } else {
            initWhenOffline(); //开始离线版本
        }
    }

    /**
     * 检查最上层界面
     */
    private void initCheckTopActivity() {
        if (activityTimer != null) {
            activityTimer.cancel();
            activityTimer = null;
        }
        if (activityTimer == null) {
            activityTimer = new Timer();
        }
        activityTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (activityManager != null) {
                    ComponentName cn = activityManager.getRunningTasks(1).get(0).topActivity;
                    if (!cn.getPackageName().equals(MainService.this.getPackageName())) {
                        HttpApi.i("TopActivity_", "不在当前程序");
                        if (!isPullTime) {
                            HttpApi.i("TopActivity_", "倒计时进入MainActivity");
                            mHandler.postDelayed(startMain, 30 * 1000);
                            isPullTime = true;
                        }
                    } else {
                        HttpApi.i("TopActivity_", "处于当前程序");
                        mHandler.removeCallbacks(startMain);
                        isPullTime = false;
                    }
                }
            }
        }, 500, 3000);
    }

    /**
     * 初始化安卓工控设备控制器
     */
    protected void initAexUtil() {

        aexUtil = new AexUtil(mHandler);
        try {
            aexUtil.open();
        } catch (Exception e) {
        }
        Log.e("wh", "初始化控制设备");
//            sendInitMessenger(InitActivity.MSG_INIT_AEX);

    }

    /**
     * 进入在线版本
     */
    protected void initWhenConnected() {
        if (initMacKey()) {
            Log.i("MainService", "INIT MAC Address");
            try {
                initClientInfo();
            } catch (Exception e) {
                Log.v("MainService", "onDeviceStateChanged,result=" + e.getMessage());
            }
        }
    }

    /**
     * 进入离线版本
     */
    protected void initWhenOffline() {
        HttpApi.i("进入离线模式");
        if (initMacKey()) {
            HttpApi.i("通过MAC地址验证");
            try {
                loadInfoFromLocal(); //获取本地sp文件中的数据
                sendMessageToMainAcitivity(MSG_LOADLOCAL_DATA, "");//在MainActivity中展示
                //startDialActivity(false);  //xiaozd add
                //rtcConnectTimeout();
            } catch (Exception e) {
                e.printStackTrace();
                Log.v("MainService", "onDeviceStateChanged,result=" + e.getMessage());
            }
        }
    }

    private void initDB() {
        mDbUtils = DbUtils.getInstans();
    }

    /**
     * 获取WIFI mac地址和密码
     */
    private boolean initMacKey() {
        mac = MacUtils.getMac();
        if (mac == null || mac.length() == 0) {
            //无法获取设备编号 用mainMessage发送信息给MainActivity显示
            return false;
        } else {
            key = mac.replace(":", "");
            Log.i(TAG, "初始化mac=" + mac + "key=" + key);
            //// TODO: 2018/5/16 mac测试写死
//            mac = "44:2c:05:e6:9c:c5";
//            key = "442c05e69cc5";
            //获取设备编号 用mainMessage发送信息给MainActivity显示
//            Message message = Message.obtain();
//            message.what = MainActivity.MSG_GET_MAC_ADDRESS;
//            message.obj = mac;
//            try {
//                initMessenger.send(message);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
            return true;
        }
    }

    /**
     * 开启登录线程
     */
    protected void initClientInfo() {
        new Thread() {
            public void run() {
                boolean result = false;
                try {
                    do {
                        result = deviceLogin();
                        if (!result) {
                            sleep(1000 * 10);//重新登录间隔10秒
                        }
                    } while (!result);
                } catch (Exception e) {
                }
            }
        }.start();

//        getClientInfo();
    }

    /**
     * 登录接口
     *
     * @return
     */
    private boolean deviceLogin() {
        boolean resultValue = false;
        try {
            String url = API.DEVICE_LOGIN;

            JSONObject data = new JSONObject();
            data.put("mac", mac);
            data.put("key", key);
            data.put("version", getVersionName());

            Response execute = OkHttpUtils.postString().url(url).content(data.toString()).mediaType(MediaType.parse
                    ("application/json; charset=utf-8")).tag(this).build().execute();
            if (null != execute) {
                String response = execute.body().string();
                if (null != response) {
                    String code = JsonUtil.getFieldValue(response, "code");
                    if ("0".equals(code)) {
                        resultValue = true;
                        String result = JsonUtil.getResult(response);
                        DoorBean doorBean = JsonUtil.parseJsonToBean(result, DoorBean.class);
                        httpServerToken = doorBean.getToken();
                        mac_id = doorBean.getMac_id() + "";
                        Log.e(TAG, doorBean.toString());
                        //重置广告，图片，通知版本为0，登录时重新加载
                        saveVisionInfo();
                        //保存返回数据，通知主线程继续下一步逻辑
                        Message message = mHandler.obtainMessage();
                        message.what = MSG_LOGIN;
                        message.obj = doorBean.getXdoor();
                        mHandler.sendMessage(message);
                    }
                } else {
//                    服务器异常或没有网络
                    HttpApi.e("getClientInfo()->服务器无响应");
                }
            }
        } catch (Exception e) {
            HttpApi.e("getClientInfo()->服务器数据解析异常");
            e.printStackTrace();
        }
        Log.e("这里", "" + resultValue);
        return resultValue;
    }

    private void textDB() {
        final List<Ka> list = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, System.currentTimeMillis() + "开始前");
                for (int i = 0; i < 10000; i++) {
                    Ka ka = new Ka();
                    ka.setKa_id("" + i);
                    list.add(ka);
                }
                Log.i(TAG, System.currentTimeMillis() + "开始后");
                mDbUtils.addAllKa(list);
                Log.i(TAG, System.currentTimeMillis() + "添加后");
//                boolean hasKa = mDbUtils.isHasKa("1052");
//                LogDoor.i(TAG, System.currentTimeMillis() + "查询后" + hasKa);
            }
        }).start();
    }

    /**
     * 登录接口
     *
     * @return
     * @throws JSONException
     */
    protected void getClientInfo() {
        try {
            String url = API.DEVICE_LOGIN;
            JSONObject data = new JSONObject();
            // TODO: 2018/5/16 参数之后要改
            data.put("mac", mac);
            data.put("key", key);
            data.put("version", "1.0");

            OkHttpUtils.postString().url(url).content(data.toString()).mediaType(MediaType.parse("application/json; "
                    + "charset=utf-8")).tag(url).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    Log.e(TAG, "e " + e.toString());
                    getClientInfo();
                }

                @Override
                public void onResponse(String response, int id) {
                    Log.e("wh response", response);
                    Log.i(TAG, response);
                    if (null != response) {
                        String code = JsonUtil.getFieldValue(response, "code");
                        if ("0".equals(code)) {
                            String result1 = JsonUtil.getResult(response);
                            DoorBean doorBean = JsonUtil.parseJsonToBean(result1, DoorBean.class);
                            httpServerToken = doorBean.getToken();
                            Log.e(TAG, doorBean.toString());
                            //保存返回数据，通知主线程继续下一步逻辑
                            Message message = mHandler.obtainMessage();
                            message.what = MSG_LOGIN;
                            message.obj = doorBean.getXdoor();
                            mHandler.sendMessage(message);
                        } else {
                            getClientInfo();
                        }
                    } else {
                        //服务器异常或没有网络
                        HttpApi.e("getClientInfo()->服务器无响应");
                        getClientInfo();
                    }

                }
            });
        } catch (Exception e) {
            HttpApi.e("登录接口返回参数getClientInfo()->服务器数据解析异常");
        }
    }

    /**
     * 登录成功后
     *
     * @param msg
     */
    protected void onLogin(Message msg) {
        //"id":1,"name":"大门","key":"442c05e69cc5","ip":"123456","mac":"44:2c:05:e6:9c:c5",
        // "type":"0",
        // "danyuan_id":"1","loudong_id":"1","xiangmu_id":346,"gongsi_id":"1",
        // "lixian_mima":"123456","version":null,
        // "xintiao_time":null
        XdoorBean result = (XdoorBean) msg.obj;
        if ("0".equals(result.getType())) {//大门
            DeviceConfig.DEVICE_TYPE = "C";
            lockName = "大门";
        } else if ("1".equals(result.getType())) {//单元门
            DeviceConfig.DEVICE_TYPE = "B";
            blockId = Integer.parseInt(result.getLoudong_id());
            lockId = Integer.parseInt(result.getDanyuan_id());
            lockName = blockId + "栋" + lockId + "单元";
        }
        communityId = result.getXiangmu_id();
        //目前服务器返回为空
        communityName = result.getXiangmu_name() == null ? "欣社区" : result.getXiangmu_name();

        // 保存消息  需要操作
        saveInfoIntoLocal(communityId, blockId, lockId, communityName, lockName);
        Message message = Message.obtain();
        message.what = MSG_LOGIN_AFTER;
        message.obj = result;
        try {
            mainMessage.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    protected void loadInfoFromLocal() {
        SharedPreferences sharedPreferences = getSharedPreferences("residential", Activity.MODE_PRIVATE);
        communityId = sharedPreferences.getInt("communityId", 0);
        blockId = sharedPreferences.getInt("blockId", 0);
        lockId = sharedPreferences.getInt("lockId", 0);
        communityName = sharedPreferences.getString("communityName", "");
        lockName = sharedPreferences.getString("lockName", "");
    }

    protected void saveInfoIntoLocal(int communityId, int blockId, int lockId, String communityName, String lockName) {
        SharedPreferences sharedPreferences = getSharedPreferences("residential", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //SPUtil.put(getApplicationContext(),);
        editor.putInt("communityId", communityId);
        editor.putInt("blockId", blockId);
        editor.putInt("lockId", lockId);
        editor.putString("communityName", communityName);
        editor.putString("lockName", lockName);
        editor.commit();
    }

    /****************************初始化天翼操作********************************/
    public static Connection callConnection;

    /**
     * 初始化天翼sdk
     */
    private void initTYSDK() {
        if (!isRtcInit) {
            rtcClient = new RtcClientImpl();
            Log.i(TAG, getApplicationContext() == null ? "yes" : "no");
            rtcClient.initialize(getApplicationContext(), new ClientListener() {
                @Override   //初始化结果回调
                public void onInit(int result) {
                    Log.v("MainService", "onInit,result=" + result);//常见错误9003:网络异常或系统时间差的太多
                    if (result == 0) {
                        Log.i(TAG, "----------------天翼rtc初始化成功---------------");
                        rtcClient.setAudioCodec(RtcConst.ACodec_OPUS);
                        rtcClient.setVideoCodec(RtcConst.VCodec_VP8);
                        rtcClient.setVideoAttr(RtcConst.Video_SD);
                        rtcClient.setVideoAdapt(1);
                        isRtcInit = true;
                        startGetToken();
                    } else {
                        isRtcInit = false;
                        initTYSDK();
                    }
                }
            });
        }
    }

    /**
     * 开启线程获取token
     */
    private void startGetToken() {
        Log.i(TAG, "开始获取Token ");
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                getTokenFromServer();
                Looper.loop();
            }
        }.start();
    }

    /**
     * 终端直接从rtc平台获取token，应用产品需要通过自己的服务器来获取，rtc平台接口请参考开发文档<2.5>章节.
     */
    private void getTokenFromServer() {
        Log.i(TAG, "rtc平台获取token");
        RtcConst.UEAPPID_Current = RtcConst.UEAPPID_Self;//账号体系，包括私有、微博、QQ等，必须在获取token之前确定。
        JSONObject jsonobj = HttpManager.getInstance().CreateTokenJson(0, key, RtcHttpClient.grantedCapabiltyID, "");
        HttpResult ret = HttpManager.getInstance().getCapabilityToken(jsonobj, RTC_APP_ID, RTC_APP_KEY);
        onResponseGetToken(ret);
    }

    /**
     * 获取TOKEN
     */
    private void onResponseGetToken(HttpResult ret) {
        Log.i(TAG, "rtc平台获取token 的状态  status=" + ret.getStatus());
        JSONObject jsonrsp = (JSONObject) ret.getObject();
        if (jsonrsp != null && jsonrsp.isNull("code") == false) {
            try {
                String code = jsonrsp.getString(RtcConst.kcode);
                String reason = jsonrsp.getString(RtcConst.kreason);
                Log.v("MainService", "Response getCapabilityToken code:" + code + " reason:" + reason);
                if (code.equals("0")) {
                    token = jsonrsp.getString(RtcConst.kcapabilityToken);
                    Log.i(TAG, "获取token成功 token=" + token);
                    rtcRegister();
                } else {
                    Log.e(TAG, "获取token失败 [status:" + ret.getStatus() + "]" + ret.getErrorMsg());
                    getTokenFromServer();
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e(TAG, "获取token失败 [status:" + e.getMessage() + "]");
            }
        } else {
        }
    }

    private void rtcRegister() {
        Log.i(TAG, "开始登陆rtc  mac:" + key + "token:" + token);
        if (token != null) {
            try {
                JSONObject jargs = SdkSettings.defaultDeviceSetting();
                jargs.put(RtcConst.kAccPwd, token);
                //账号格式形如“账号体系-号码~应用id~终端类型”，以下主要设置账号内各部分内容，其中账号体系的值要在获取token之前确定，默认为私有账号
                jargs.put(RtcConst.kAccAppID, RTC_APP_ID);//应用id
                jargs.put(RtcConst.kAccUser, key); //号码
                jargs.put(RtcConst.kAccType, RtcConst.UEType_Current);//终端类型
                jargs.put(RtcConst.kAccRetry, 5);//设置重连时间
                device = rtcClient.createDevice(jargs.toString(), deviceListener);
                //登陆
                Log.i(TAG, " 设置监听 deviceListener   ");
            } catch (JSONException e) {
                Log.i(TAG, "登陆rtc失败   e:" + e.toString());
                e.printStackTrace();
            }
        }
    }

    /**
     * rtc收发消息监听
     */
    DeviceListener deviceListener = new DeviceListener() {
        @Override
        public void onDeviceStateChanged(int result) {
            Log.i(TAG, "登陆状态 ,result=" + result);
            if (result == RtcConst.CallCode_Success) { //注销也存在此处
                Log.e(TAG, "-----------登陆成功-------------key=" + key + "------------");
            } else if (result == RtcConst.NoNetwork) {
                Log.i(TAG, "断网销毁，自动重连接");
            } else if (result == RtcConst.ChangeNetwork) {
                Log.i(TAG, "网络状态改变，自动重连接");
            } else if (result == RtcConst.PoorNetwork) {
                Log.i(TAG, "网络差，自动重连接");
            } else if (result == RtcConst.ReLoginNetwork) {
                Log.i(TAG, " 网络原因导致多次登陆不成功，由用户选择是否继续，如想继续尝试，可以重建device");
            } else if (result == RtcConst.DeviceEvt_KickedOff) {
                Log.i(TAG, "被另外一个终端踢下线，由用户选择是否继续，如果再次登录，需要重新获取token，重建device");
            } else if (result == RtcConst.DeviceEvt_MultiLogin) {
            } else if (result == RtcConst.CallCode_Forbidden) {
                Log.i(TAG, "密码错误 重新登陆啦 result=" + result);
            } else if (result == RtcConst.CallCode_NotFound) {
                Log.i(TAG, "被叫号码从未获取token登录过 result=" + result);
            } else {
                Log.i(TAG, "登陆失败 result=" + result);
            }
        }

        @Override
        public void onSendIm(int i) {
            Log.e(RTCTAG, "onSendIm()" + (i == 0 ? "成功" : "失败"));
            if (callConnectState == CALL_VIDEO_CONNECTING) {
                checkSendCallMessageParall(i);
            }
        }

        @Override
        public void onReceiveIm(String s, String s1, String s2) {
            onMessage(s, s1, s2);
        }

        @Override
        public void onNewCall(Connection connection) {
            //   LogDoor.i(TAG,"收到来电");
            JSONObject callInfo = null;
            String acceptMember = null;
            try {
                callInfo = new JSONObject(connection.info());
                acceptMember = callInfo.getString("uri");
            } catch (JSONException e) {
            }
            Log.i(TAG, "收到来电 call=" + connection.info());
            if (callConnection != null) {
                //表示已经在通话 拒绝这个链接
                connection.reject();
                Log.i(TAG, "已经在通话 拒绝这个连接");
                return;
            }
            //收到呼叫
            callConnection = connection;
            connection.setIncomingListener(connectionListener);
            connection.accept(RtcConst.CallType_A_V);
            Log.i(TAG, "正在与" + acceptMember + "进行连接，取消其他用户");
            Log.i(TAG, "接通" + acceptMember);
            cancelOtherMembers(acceptMember);  //挂断其他电话
            resetCallMode();
            stopTimeoutCheckThread();
            try {
                Message message = Message.obtain();
                message.what = MSG_RTC_NEWCALL;
                mainMessage.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }


        }

        @Override
        public void onQueryStatus(int i, String s) {

        }
    };

    /**
     * 收到消息
     *
     * @param from
     * @param mime
     * @param content
     */
    protected void onMessage(String from, String mime, String content) {
        HttpApi.i("from = " + from + "    mime = " + mime + "     content = " + content);
        // sendMessageToMainAcitivity(MSG_RTC_MESSAGE, null);
        if (content.equals("refresh card info")) {
        } else if (content.equals("refresh finger info")) {
        } else if (content.equals("refresh all info")) {
        } else if (content.startsWith("reject call")) { //挂断
        } else if (content.startsWith("{")) {
            //手机开门
            LogDoor logDoor = JsonUtil.parseJsonToBean(content, LogDoor.class);
            cancelOtherMembers(from);
            Log.v("MainService", "用户手机一键开门，取消其他呼叫");
            resetCallMode();
            stopTimeoutCheckThread();
            //开门操作
            Log.e(TAG, "进行开门操作 开门开门");
            openLock(2);
            //分为手机开门和视屏开门 1和2 进行区分 上传日志统一传2；
            if (logDoor.getKaimenfangshi()==1) {
                logDoor.setKaimenfangshi(2);
                //一键开门拍照
                if (StringUtils.isFastClick()) {
                    String imgurl = "door/img/" + System.currentTimeMillis() + ".jpg";
                    sendMessageToMainAcitivity(MSG_YIJIANKAIMEN_TAKEPIC, imgurl);
                    logDoor.setKaimenjietu(imgurl);
                }
            }
            logDoor.setState(1);
            List<LogDoor> list = new ArrayList<>();
            //拼接图片地址
            logDoor.setKaimenjietu(logDoor.getKaimenjietu());
            logDoor.setKaimenshijian(StringUtils.transferLongToDate("yyyy-MM-dd HH:mm:ss",System.currentTimeMillis()));
            Log.e(TAG, "图片imageUrl" + logDoor.getKaimenjietu());
            list.add(logDoor);
            //上传日志
            createAccessLog(list);
        } else if (content.startsWith("refuse call")) { //拒绝接听
//            if (!rejectUserList.contains(from)) {
//                rejectUserList.add(from);
//            }
            Log.d(TAG, "onMessage: ++++++++++++" + from);
            cancelOtherMembers(from);
            Log.v("MainService", "用户没有接听，取消其他呼叫");
            resetCallMode();
            sendMessageToMainAcitivity(MSG_CALLMEMBER_TIMEOUT, ""); //通知界面目前已经超时，并进入初始状态
            stopTimeoutCheckThread();
        }
    }

//    private void test() {
//        String url = API.LOG;
//        JSONObject data = new JSONObject();
//        try {
//            data.put("mac", "44:2c:05:e6:9c:c5");
//            data.put("phone", "454");
//            data.put("ka_id", "");
//            data.put("kaimenfangshi", "1");
//            data.put("kaimenjietu", "");
//            data.put("kaimenshijian", System.currentTimeMillis());
//            data.put("uuid", "");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        LogDoor logDoor = JsonUtil.parseJsonToBean(data.toString(), LogDoor.class);
//        List<LogDoor> door = new ArrayList<>();
//        door.add(logDoor);
//        LogListBean logListBean = new LogListBean();
//        logListBean.setMac("44:2c:05:e6:9c:c5");
//        logListBean.setXdoorOneOpenDtos(door);
//        String s = JsonUtil.parseBeanToJson(logListBean);
//        Log.e(TAG, "test" + JsonUtil.parseBeanToJson(logListBean));
//        OkHttpUtils.postString().url(url).content(s).mediaType(MediaType.parse
// ("application/json;" + " " +
//                "charset=utf-8")).addHeader("Authorization", httpServerToken).tag(this).build()
// .execute(new
// StringCallback() {
//
//
//            @Override
//            public void onError(Call call, Exception e, int id) {
//                Log.e(TAG, e.toString());
//            }
//
//            @Override
//            public void onResponse(String response, int id) {
//                Log.e(TAG, response);
//            }
//        });
//    }

    /**
     * 上传开门日志
     * 开门方式:1卡2手机3人脸4邀请码5离线密码6临时密码
     */
    protected void createAccessLog(final List<LogDoor> data) {
        Log.e(TAG, "开门日志上传" + data.toString());
        String url = API.LOG;

        LogListBean logListBean = new LogListBean();
        logListBean.setMac(mac);
        logListBean.setXdoorOneOpenDtos(data);
        String json = JsonUtil.parseBeanToJson(logListBean);
        Log.e(TAG, "开门日志上传 参数" + json);

        OkHttpUtils.postString().url(url).content(json).mediaType(MediaType.parse("application/json;" + "" + "" + " "
                + "charset=utf-8")).addHeader("Authorization", httpServerToken).tag(this).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, "onError rtc上传日志接口createAccessLog oner " + e.toString());
                Log.e(TAG, "上传日志失败 保存日志信息到数据库");
                DbUtils.getInstans().addAllLog(data);
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e("wh response", response);
                Log.e(TAG, "onResponse rtc上传日志接口createAccessLog response" + response);
                if (null != response) {
                    String code = JsonUtil.getFieldValue(response, "code");
                    if ("0".equals(code)) {
                        Log.i(TAG, "日志上传成功 查询离线日志");
                        List<LogDoor> doors = DbUtils.getInstans().quaryLog();

                        if (doors.size() > 0) {
                            //有离线日志 上传离线日志
                            Log.i(TAG, "有离线日志" + doors.size() + "条" + " 开始上传 " + doors.toString());
                            createAccessLogLixian(doors);
                        }
                    } else {
                        Log.e(TAG, "上传日志失败 保存日志信息到数据库");
                        DbUtils.getInstans().addAllLog(data);
                    }
                } else {
                    //服务器异常或没有网络
                    Log.e(TAG, "上传日志失败 保存日志信息到数据库");
                    DbUtils.getInstans().addAllLog(data);
                }
            }
        });
    }

    /**
     * 离线日志上传
     * 开门方式:1卡2手机3人脸4邀请码5离线密码6临时密码
     *
     * @param data
     */
    protected void createAccessLogLixian(List<LogDoor> data) {
        Log.e(TAG, "开门离线日志上传" + data.toString());
        String url = API.LOG;
        LogListBean logListBean = new LogListBean();
        logListBean.setMac(mac);
        logListBean.setXdoorOneOpenDtos(data);
        String json = JsonUtil.parseBeanToJson(logListBean);
        Log.e(TAG, "开门离线日志上传 参数" + json);
        OkHttpUtils.postString().url(url).content(json).mediaType(MediaType.parse("application/json;" + "" + "" + " "
                + "charset=utf-8")).addHeader("Authorization", httpServerToken).tag(this).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, "onError rtc上传日志接口createAccessLog oner " + e.toString());
                Log.e(TAG, "上传离线日志失败 不做保存操作");

            }

            @Override
            public void onResponse(String response, int id) {
                Log.e("wh response", response);
                Log.e(TAG, "onResponse rtc上传日志接口createAccessLog response" + response);
                if (null != response) {
                    String code = JsonUtil.getFieldValue(response, "code");
                    if ("0".equals(code)) {
                        Log.i(TAG, "离线日志上传成功 删除离线日志");
                        DbUtils.getInstans().deleteAllLog();
                    } else {
                        Log.e(TAG, "上传离线日志失败不保存信息到数据库");
                    }
                } else {
                    //服务器异常或没有网络
                    Log.e(TAG, "上传离线日志失败 不保存信息到数据库");
                }
            }
        });
    }

    /**
     * 主动呼叫暂时不用
     *
     * @param callName
     */
    private void calling(String callName) {
        try {
            String remoteuri = RtcRules.UserToRemoteUri_new(callName, RtcConst.UEType_Any);
            JSONObject jinfo = new JSONObject();
            jinfo.put(RtcConst.kCallRemoteUri, remoteuri);
            jinfo.put(RtcConst.kCallType, RtcConst.CallType_A_V);
            callConnection = device.connect(jinfo.toString(), connectionListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 视频进行连接的回调
     */
    ConnectionListener connectionListener = new ConnectionListener() {
        @Override
        public void onConnecting() {
            Log.i(TAG, "onConnecting正在进行视频或音频的连接....");
        }

        @Override
        public void onConnected() {
            Log.i(TAG, "onConnected");
        }

        @Override
        public void onDisconnected(int code) {
            Log.i(TAG, "onDisconnected" + code);
            callConnection = null;
            //发送结束消息
            sendMessageToMainAcitivity(MSG_RTC_DISCONNECT, "");
        }

        @Override
        public void onVideo() {
            rtcClient.enableSpeaker(audioManager, true);
            Log.i(TAG, "onVideo 接通视频通话,并默认为免提");
            sendMessageToMainAcitivity(MSG_RTC_ONVIDEO, "");

        }

        @Override
        public void onNetStatus(int msg, String info) {

        }
    };

    /****************************初始化天翼操作********************************/


    /****************************呼叫相关start********************************/
    /**
     * 取消呼叫
     */
    protected void cancelCurrentCall() {
        cancelOtherMembers(null);
        HttpApi.i("用户取消呼叫");
        resetCallMode();
        stopTimeoutCheckThread();
    }

    /**
     * 挂断正在进行的呼叫
     */
    protected void disconnectCallingConnection() {
        if (callConnection != null) {
            callConnection.disconnect();
            callConnection = null;
            sendMessageToMainAcitivity(MSG_RTC_DISCONNECT, "");
        }
    }

    /**
     * 呼叫超时停止线程
     */
    private void stopTimeoutCheckThread() {
        if (timeoutCheckThread != null) {
            Log.v("MainService", "停止定时任务");
            timeoutCheckThread.interrupt();
            timeoutCheckThread = null;
        }
    }

    /**
     * 启动是否超时线程
     */
    private void startTimeoutChecking() {
        stopTimeoutCheckThread();
        timeoutCheckThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(DeviceConfig.CANCEL_CALL_WAIT_TIME); //等待指定的一个并行时间
                    if (!isInterrupted()) { //检查线程没有被停止
                        if (callConnectState == CALL_VIDEO_CONNECTING) { //如果现在是尝试连接状态
                            Log.v("MainService", "超时检查，取消当前呼叫");
                            resetCallMode();
                            sendMessageToMainAcitivity(MSG_CALLMEMBER_TIMEOUT, "");
                            //通知界面目前已经超时，并进入初始状态
                        }
                    }
                } catch (InterruptedException e) {
                }
                timeoutCheckThread = null;
            }
        };
        timeoutCheckThread.start();
    }

    /**
     * 拒绝其他成员  发送cancelCall消息
     *
     * @param acceptMember
     */
    private void cancelOtherMembers(String acceptMember) {
        try {
            if (acceptMember != null) {
                Log.i(TAG, "进入取消--" + acceptMember);
            }
            JSONObject command = new JSONObject();
            command.put("command", "cancelCall");
            command.put("from", this.key);
            if (triedUserList != null && triedUserList.size() > 0) {
                for (int i = 0; i < triedUserList.size(); i++) {
                    YeZhuBean userObject = triedUserList.get(i);
                    String username = userObject.getYezhu_dianhua();
                    Log.v("MainService", "检查在线设备并且进行取消" + username);
                    if (username.length() == 17) {
                        username = username.replaceAll(":", "");
                    }
                    if (!username.equals(acceptMember)) {
                        Log.v("MainService", "--->取消" + username);
                        String userUrl = RtcRules.UserToRemoteUri_new(username, RtcConst.UEType_Any);
                        Log.e(TAG, "发送取消呼叫的消息");
                        device.sendIm(userUrl, "cmd/json", command.toString());
                    }
                }
            } else {
                Log.v("MainService", "无其他在线设备" + acceptMember);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("MainService", "取消失败--" + acceptMember);
        }
    }

    /**
     * 发送图片消息  appendImage
     */
    protected void sendCallAppendImage() {
        try {
            JSONObject data = new JSONObject();
            data.put("command", "appendImage");
            data.put("from", this.key);
            data.put("imageUrl", this.imageUrl);
            data.put("imageUuid", this.imageUuid);
            Log.v("MainService", "开始发送访客图片");
            if (triedUserList.size() > 0) {
                Iterator iterator = triedUserList.iterator();
                while (iterator.hasNext()) {
                    YeZhuBean userObject = (YeZhuBean) iterator.next();
                    String username = userObject.getYezhu_dianhua();
                    if (username.length() == 17) {
                        username = username.replaceAll(":", "");
                    }
                    String userUrl = RtcRules.UserToRemoteUri_new(username, RtcConst.UEType_Any);
                    int sendResult = device.sendIm(userUrl, "cmd/json", data.toString());
                    Log.v("MainService", "发送访客图片-->" + username);
                    Log.v("MainService", "sendIm(): " + sendResult);
                }
            }
        } catch (JSONException e) {
        }
    }

    /**
     * 重置呼叫状态，将所有设置恢复至初始状态
     */
    private void resetCallMode() {
        Log.v("MainService", "清除呼叫数据");
        callConnectState = CALL_WAITING;
        allUserList.clear();
        triedUserList.clear();
        onlineUserList.clear();
        offlineUserList.clear();
        rejectUserList.clear();
    }

    /**
     * 开始发消息呼叫业主
     */
    private void startCallMember() {
        String callUuid = this.imageUuid;
        callMember(callUuid);

    }

    /**
     * 获取需要呼叫成员
     *
     * @param callUuid
     */
    private void callMember(final String callUuid) {
        try {
            String url = API.CALLALL_MEMBERS;
            JSONObject data = new JSONObject();
            data.put("mac", mac);
            data.put("hujiaohao", this.unitNo);

            OkHttpUtils.postString().url(url).content(data.toString()).mediaType(MediaType.parse("application/json; "
                    + "charset=utf-8")).addHeader("Authorization", httpServerToken).tag(this).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    Log.e(TAG, "服务器异常或没有网络 " + e.toString());
                    Message message = mHandler.obtainMessage();
                    message.what = MSG_CALLMEMBER;
                    mHandler.sendMessage(message);
                }

                @Override
                public void onResponse(String response, int id) {
                    Log.e("wh response", response);

                    if (null != response) {
                        String code = JsonUtil.getFieldValue(response, "code");
                        if ("0".equals(code) && isCurrentCallWorking(callUuid)) {
                            //发到主线程给天翼RTC使用
                            String result1 = JsonUtil.getResult(response);
                            HttpApi.i("获取成员接口请求成功 callMember()->" + response);
                            Message message = mHandler.obtainMessage();
                            message.what = MSG_CALLMEMBER;
                            Object[] objects = new Object[2];
                            objects[0] = callUuid;
                            objects[1] = result1;
                            message.obj = objects;
                            mHandler.sendMessage(message);
                        } else {
                            Message message = mHandler.obtainMessage();
                            message.what = MSG_CALLMEMBER;
                            mHandler.sendMessage(message);
                        }
                    } else {
                        Message message = mHandler.obtainMessage();
                        message.what = MSG_CALLMEMBER;
                        mHandler.sendMessage(message);
                        //服务器异常或没有网络
                        HttpApi.e("getClientInfo()->服务器无响应");
                    }
                }
            });
        } catch (Exception e) {
            HttpApi.e("getClientInfo()->服务器数据解析异常");
            Message message = mHandler.obtainMessage();
            message.what = MSG_CALLMEMBER;
            mHandler.sendMessage(message);
            e.printStackTrace();
        }

    }

    /**
     * 判断是否正在被呼叫
     *
     * @param uuid
     * @return
     */
    private boolean isCurrentCallWorking(String uuid) {
        return uuid.equals(this.imageUuid);
    }

    protected synchronized void onCallMember(Message msg) {
        try {
            if (msg.obj == null) {
                Log.e(TAG, "呼叫错误");
                Message message = Message.obtain();
                message.what = MSG_CALLMEMBER_SERVER_ERROR;
                try {
                    mainMessage.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return;
            }
            Object[] objects = (Object[]) msg.obj;
            final String callUuid = (String) objects[0];
            String result = (String) objects[1];
            HttpApi.i("拨号中->网络请求在线列表" + (result != null ? result.toString() : ""));
            String yezhu = JsonUtil.getFieldValue(result, "yezhu");
            Log.e(TAG, "yezhu" + yezhu);

            List<YeZhuBean> userList = JsonUtil.fromJsonArray(yezhu, YeZhuBean.class);
            if ((userList != null && userList.size() > 0)) {
                Log.v("MainService", "收到新的呼叫，清除呼叫数据，UUID=" + callUuid);
                HttpApi.i("拨号中->清除呼叫数据");
                allUserList.clear();
                triedUserList.clear();
                onlineUserList.clear();
                offlineUserList.clear();
                rejectUserList.clear();
                callConnectState = CALL_VIDEO_CONNECTING;
                allUserList.addAll(userList);
                //呼叫模式并行
                HttpApi.i("拨号中->准备拨号Parall");
                Log.i(TAG, "allUserList=" + allUserList.toString());
                sendCallMessageParall();
            } else {
                Message message = Message.obtain();
                message.what = MSG_CALLMEMBER_ERROR;
                try {
                    mainMessage.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkSendCallMessageParall(int status) {
        Log.i(TAG, "triedUserList=" + triedUserList.toString());
        YeZhuBean object = triedUserList.get(triedUserList.size() - 1);
        if (status == RtcConst.CallCode_Success) {
            onlineUserList.add(object);
        } else {
            offlineUserList.add(object);
        }
        sendCallMessageParall();
    }

    protected void sendCallMessageParall() {
        if (callConnectState == CALL_VIDEO_CONNECTING) {
            try {
                JSONObject data = new JSONObject();
                data.put("command", "call");
                data.put("from", this.key);
                data.put("imageUrl", this.imageUrl);
                data.put("imageUuid", this.imageUuid);
                data.put("communityName", communityName);
                data.put("lockName", lockName);
                if (allUserList.size() > 0) {
                    YeZhuBean userObject = allUserList.remove(0);
                    String username = userObject.getYezhu_dianhua();
//                    if (username.length() == 17) {
//                        username = username.replaceAll(":", "");
//                    }
                    String userUrl = RtcRules.UserToRemoteUri_new(username, RtcConst.UEType_Any);
                    HttpApi.i("拨号中->准备拨号userUrl = " + userUrl);
                    HttpApi.i("拨号中->准备拨号data = " + data.toString());
                    int sendResult = device.sendIm(userUrl, "cmd/json", data.toString());
                    Log.v("MainService", "sendIm(): " + sendResult);
                    HttpApi.i("拨号中->sendIm()" + sendResult);
                    triedUserList.add(userObject);

                } else {
                    HttpApi.i("拨号中->没有人在线");
                    afterTryAllMembers();
                }
            } catch (JSONException e) {
            }
        }
    }

    //全部人员尝试并行呼叫后，检查在线的用户，如果有在线用户则等待，否则立即启动直拨
    protected void afterTryAllMembers() {
        boolean needWait = false;
        needWait = triedUserList.size() > 0;
        //5 pushCallMessage();
        if (needWait) { //检查在线人数,大于0则等待一段时间
            startTimeoutChecking();
        } else {
            sendMessageToMainAcitivity(MSG_CALLMEMBER_NO_ONLINE, "");//告诉用户无人在线
        }
    }

    protected void pushCallMessage() {
        String pushList = null;
        for (int j = 0; j < offlineUserList.size(); j++) {
            YeZhuBean userObject = offlineUserList.get(j);
            String username = userObject.getYezhu_dianhua();
            if (username.length() != 17) {
                if (pushList == null) {
                    pushList = username;
                } else {
                    pushList = pushList + "," + username;
                }
            }
        }
        if (pushList != null) {
            startPushCallMessage(pushList);
        }
    }

    protected void startPushCallMessage(final String pushList) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    onPushCallMessage(pushList);
                } catch (Exception e) {

                }
            }
        };
        thread.start();
    }

    /**
     * 推送消息
     *
     * @param pushList
     * @throws JSONException
     * @throws IOException
     */
    protected void onPushCallMessage(String pushList) throws Exception {
        JSONObject data = new JSONObject();
        data.put("pushList", pushList);
        data.put("from", key);
        data.put("unitName", communityName + unitNo);
        String dataStr = data.toString();
        String url = DeviceConfig.SERVER_URL + "/app/device/pushCallMessage";
        String result = HttpApi.getInstance().loadHttpforPost(url, data, httpServerToken);
        if (result != null) {
            HttpApi.i("onPushCallMessage()->" + result);
            JSONObject resultObj = Ajax.getJSONObject(result);
            int code = resultObj.getInt("code");
            if (code == 0) {

            } else {

            }
        } else {
            HttpApi.e("onPushCallMessage()->服务器异常");
        }
    }

    /****************************呼叫相关********************************/

    /**
     * 发送消息到mainactivity
     *
     * @param what
     * @param o
     */
    private void sendMessageToMainAcitivity(int what, Object o) {
        if (mainMessage != null) {
            Message message = Message.obtain();
            message.what = what;
            message.obj = o;
            try {
                mainMessage.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /****************************虹软相关*********************************************/

    /**
     * 人脸录入前初始化虹软相关类
     */
    private void initFaceEngine() {
        //在这里初始化人脸检测和识别相关类，之后抽取方法
        //人脸检测初始化引擎，设置检测角度、范围，数量。创建对象后，必须先于其他成员函数调用
        err_afd = engine_afd.AFD_FSDK_InitialFaceEngine(arc_appid, fd_key, AFD_FSDKEngine.AFD_OPF_0_HIGHER_EXT, 16, 5);

        //人脸识别初始化引擎，设置检测角度、范围，数量。创建对象后，必须先于其他成员函数调用
        err_afr = engine_afr.AFR_FSDK_InitialEngine(arc_appid, fr_key);

        if (err_afd.getCode() != AFD_FSDKError.MOK) {//FD初始化失败
            Log.e(TAG, "FD初始化失败，错误码：" + err_afd.getCode());
        } else if (err_afr.getCode() != AFD_FSDKError.MOK) {
            Log.e(TAG, "FR初始化失败，错误码：" + err_afr.getCode());
        } else {
            err_afd = engine_afd.AFD_FSDK_GetVersion(version_afd);
            err_afr = engine_afr.AFR_FSDK_GetVersion(version_afr);
            Log.d(TAG, "AFD_FSDK_GetVersion =" + version_afd.toString() + ", " + err_afd.getCode());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (FaceUrlBean urlBean : faceUrlList) {
                        downLoadFace(urlBean);
                    }
                }
            }).start();
        }
    }

    /**
     * 下载人脸照片
     *
     * @param urlBean
     * @return
     */
    private boolean downLoadFace(FaceUrlBean urlBean) {
        Log.e("wh", "开始下载照片" + urlBean.toString());
        boolean result = false;//此张照片是否录入的标识
        try {
//            String file = "/app/download/face/20180421121538";
            String file = urlBean.getZhaopian();
            int lastIndex = file.lastIndexOf("/");
            String fileName = file.substring(lastIndex + 1);
            Log.e("wh", "fileName " + fileName);
            //根据文件名返回本地路径
            String localFile = HttpUtils.getLocalFile(fileName);
            if (localFile == null) {
                localFile = HttpUtils.downloadFile(file);//如果本地没有对应文件,则下载文件至本地
                if (localFile != null) {
                    if (localFile.endsWith(".temp")) {
                        localFile = localFile.substring(0, localFile.length() - 5);
                    }
                    Log.e("wh", "fileName " + fileName + " localFile " + localFile);
                    File file1 = new File(localFile + ".temp");
                    if (file1.exists()) {
                        File file2 = new File(localFile + ".jpg");
                        Log.e("wh", "file2 " + file2.getPath());
                        file1.renameTo(file2);//重命名,去掉.temp
                        String path = file2.getPath();
                        Log.e("wh", "图片路径" + path);
                        if (file1 != null) {
                            // TODO: 2018/5/15 这里捕捉人脸信息并录入
                            result = getFaceInfo(file2.getPath());
                            Log.e("wh", "录入结果" + result);
                            //无论录入是否成功，删除照片
                            if (file1.exists()) {
                                file1.delete();
                                Log.e("wh", "file1存在");
                            }
                            if (file2.exists()) {
                                file2.delete();
                                Log.e("wh", "file2存在");
                            }
                        }
                    }
                } else {
                    File file1 = new File(localFile + ".temp");
                    File file2 = new File(localFile + ".jpg");
                    if (file1.exists()) {
                        file1.delete();
                        Log.e("wh", "file1存在1");
                    }
                    if (file2.exists()) {
                        file2.delete();
                        Log.e("wh", "file2存在1");
                    }
                }
            } else {
                //文件已存在，不重复下载
            }
        } catch (Exception e) {
        }
        return result;
    }

    private boolean getFaceInfo(String mFilePath) {

        Bitmap mBitmap = BitmapUtils.decodeImage(mFilePath);

        byte[] data = new byte[mBitmap.getWidth() * mBitmap.getHeight() * 3 / 2];
        ImageConverter convert = new ImageConverter();
        convert.initial(mBitmap.getWidth(), mBitmap.getHeight(), ImageConverter.CP_PAF_NV21);
        if (convert.convert(mBitmap, data)) {
            Log.d(TAG, "convert ok!");
        }
        convert.destroy();

        //这个函数功能为检测输入的图像中存在的人脸,data 输入的图像数据,width 图像宽度,height 图像高度,format 图像格式,List<AFD_FSDKFace>
        // list 检测到的人脸会放到到该列表里
        err_afd = engine_afd.AFD_FSDK_StillImageFaceDetection(data, mBitmap.getWidth(), mBitmap.getHeight(),
                AFD_FSDKEngine.CP_PAF_NV21, result_afd);
        Log.d(TAG, "AFD_FSDK_StillImageFaceDetection =" + err_afd.getCode() + "<" + result_afd.size());

        if (!result_afd.isEmpty() && result_afd.size() != 0) {//人脸数据结果不为空

            //检测输入图像中的人脸特征信息，输出结果保存在 AFR_FSDKFace feature
            err_afr = engine_afr.AFR_FSDK_ExtractFRFeature(data, mBitmap.getWidth(), mBitmap.getHeight(),
                    AFR_FSDKEngine.CP_PAF_NV21, new Rect(result_afd.get(0).getRect()), result_afd.get(0).getDegree(),
                    result_afr);
            Log.d("com.arcsoft", "Face=" + result_afr.getFeatureData()[0] + "," + result_afr.getFeatureData()[1] + "," +
                    "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + ""
                    + "result_afr" + result_afr.toString() + "  " + "" + result_afr.getFeatureData()[2] + "," +
                    err_afr.getCode());
            if (err_afr.getCode() == err_afr.MOK) {//人脸特征检测成功
                mAFR_FSDKFace = result_afr.clone();
                // TODO: 2018/5/15 保存mAFR_FSDKFace人脸信息，操作数据库
                if (true) {//保存成功
                    return true;
                } else {
                    return false;
                }
            } else {
                //人脸特征无法检测
                Log.e(TAG, "人脸特征无法检测");
                return true;
            }
        } else {
            //没有人脸数据
            Log.e(TAG, "没有人脸数据");
            return true;
        }
    }

    /****************************虹软相关end*********************************************/

    /****************************生命周期start*********************************************/

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("MainService", "onDestroy()");

        saveVisionInfo();
        // TODO: 2018/5/15 还有资源未释放

        if (activityTimer != null) {
            activityTimer.cancel();
            activityTimer = null;
        }

        if (callConnection != null) {
            callConnection.disconnect();
            callConnection = null;
            Message message = Message.obtain();
            message.what = MSG_RTC_DISCONNECT;
            try {
                sendMessageToMainAcitivity(MSG_RTC_DISCONNECT, "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (device != null) {
            device.release();
            device = null;
        }
        if (rtcClient != null) {
            rtcClient.release();
            rtcClient = null;
        }
        if (aexUtil != null) {
            aexUtil.close();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return serviceMessage.getBinder();
    }


    /****************************生命周期end*********************************************/

    /****************************卡相关start************************/

    /**
     * 检测卡信息是否合法
     *
     * @param card
     */
    private void onCardIncome(String card) {
        if (!this.cardRecord.checkLastCard(card)) {//判断距离上次刷卡时间是否超过2秒
            Log.v("MainService", "onCard====卡信息：" + card);
            DbUtils.getInstans().quaryAllKa();
            kaInfo = DbUtils.getInstans().getKaInfo(card);
            if (kaInfo != null && System.currentTimeMillis() < Long.parseLong(kaInfo.getGuoqi_time())) {//判断数据库中是否有卡
                Log.v("MainService", "onCard====当前时间：" + System.currentTimeMillis() + "卡过期时间：" + kaInfo.getGuoqi_time
                        () + "是否失效  》0表示失效" + (System.currentTimeMillis() - Long.parseLong(kaInfo.getGuoqi_time())));
                Log.i(TAG, "刷卡开门成功" + card);
                //开始截图
//                if (DeviceConfig.OPEN_CARD_STATE == 0) {
                if (DeviceConfig.PRINTSCREEN_STATE == 0) {
                    Log.e(TAG, "刷卡开门，开始截图");
//                    DeviceConfig.OPEN_CARD_STATE = 1;
                    DeviceConfig.PRINTSCREEN_STATE = 2;
                    openLock(1);
                }
                Log.e(TAG, "onCard====:" + card);
            } else {
                Log.e(TAG, "数据库中不存在这个卡 刷卡开门失败" + card);
                sendMessageToMainAcitivity(MSG_INVALID_CARD, null);//无效房卡
            }
        }
    }

    /****************************卡相关end************************/
    protected void openLock(int type) {

        openAexLock(type);

        int status = 2;
        Intent ds_intent = new Intent();
        ds_intent.setAction(DoorLock.DoorLockOpenDoor);
        ds_intent.putExtra("index", 0);
        ds_intent.putExtra("status", status);
        sendBroadcast(ds_intent);

        Intent intent = new Intent();
        intent.setAction(DoorLock.DoorLockOpenDoor_BLE);
        sendBroadcast(intent);
    }

    private void openAexLock(int type) {
        int result = aexUtil.openLock();
        if (result > 0) {
            sendMessageToMainAcitivity(MSG_LOCK_OPENED, type);//开锁
            SoundPoolUtil.getSoundPoolUtil().loadVoice(getBaseContext(), 011111);
        }
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
}
