package com.cxwl.hurry.doorlock.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.cxwl.hurry.doorlock.config.DeviceConfig;
import com.cxwl.hurry.doorlock.db.Ka;
import com.cxwl.hurry.doorlock.entity.DoorBean;
import com.cxwl.hurry.doorlock.entity.XdoorBean;
import com.cxwl.hurry.doorlock.http.API;
import com.cxwl.hurry.doorlock.utils.Ajax;
import com.cxwl.hurry.doorlock.utils.DbUtils;
import com.cxwl.hurry.doorlock.utils.HttpApi;
import com.cxwl.hurry.doorlock.utils.HttpUtils;
import com.cxwl.hurry.doorlock.utils.JsonUtil;
import com.cxwl.hurry.doorlock.utils.MacUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jni.http.HttpManager;
import jni.http.HttpResult;
import jni.http.RtcHttpClient;
import okhttp3.Call;
import okhttp3.MediaType;
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
import static com.cxwl.hurry.doorlock.config.Constant.MSG_CALLMEMBER_ERROR;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_CALLMEMBER_NO_ONLINE;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_CALLMEMBER_SERVER_ERROR;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_CALLMEMBER_TIMEOUT;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_CANCEL_CALL;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_GUEST_PASSWORD_CHECK;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_LOGIN;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_PASSWORD_CHECK;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_RTC_DISCONNECT;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_RTC_NEWCALL;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_RTC_ONVIDEO;
import static com.cxwl.hurry.doorlock.config.Constant.MSG_RTC_REGISTER;
import static com.cxwl.hurry.doorlock.config.Constant.RTC_APP_ID;
import static com.cxwl.hurry.doorlock.config.Constant.RTC_APP_KEY;
import static com.cxwl.hurry.doorlock.config.DeviceConfig.XINTIAO_URL;



/**
 * @author xlei
 * @Date 2018/4/24.
 */

public class MainService extends Service {
    private static final String TAG = "MainService";
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

    private String mac;
    private String key;
    private Handler mHandler;
    private Messenger serviceMessage;
    private Messenger mainMessage;
    public static String httpServerToken = null;//服务器拿到的token
    RtcClient rtcClient;
    boolean isRtcInit = false; //RtcSDK初始化状态
    //天翼登陆参数
    private String token;//天翼登陆所需的token；
    private Device device;//天翼登陆连接成功 发消息的类
    private DbUtils mDbUtils;//数据库操作
    private Hashtable<String, String> currentAdvertisementFiles = new Hashtable<String, String>()
            ; //广告数据地址
    private AudioManager audioManager;//音频管理器

    private ArrayList allUserList = new ArrayList();
    private ArrayList triedUserList = new ArrayList();
    private ArrayList onlineUserList = new ArrayList();
    private ArrayList offlineUserList = new ArrayList();
    private ArrayList rejectUserList = new ArrayList();

    public String unitNo = "";//呼叫房号
    public static int communityId = 0;//社区ID
    public int blockId = 0;//楼栋ID
    public static String communityName = "";//社区名字
    public static String lockName = "";//锁的名字
    public int inputBlockId = 0;//这个也是楼栋ID，好像可以用来代表社区大门
    public static int lockId = 0;//锁ID
    public String imageUrl = null;//对应呼叫访客图片地址
    public String imageUuid = null;//图片对应的uuid

    Thread timeoutCheckThread = null;//自动取消呼叫的定时器

    Thread connectReportThread = null;//心跳包线程

    private boolean netWorkstate = false;
    public String tempKey = "";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "service启动");
        audioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        initHandler();
        // TODO: 2018/5/14 放在MainActivity中  initDB();
        initMacKey();

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
                        Log.i(TAG, "MainActivity初始化完成  MainServic开始初始化" + (netWorkstate ? "有网" :
                                "没网"));
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
                        //呼叫成员
                        Log.i(TAG, "登陆成功");
                        onLogin(msg);
                        break;
                    case MSG_CANCEL_CALL:
                        //取消呼叫
                        Log.i(TAG, "取消呼叫");
                        cancelCurrentCall();
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
                        startCheckGuestPassword();
                        break;
                    case MSG_GUEST_PASSWORD_CHECK:
                        Log.i(TAG, "获取获取到服务器返回的密码");
                        onCheckGuestPassword(msg.obj == null ? null : (JSONObject) msg.obj);
                        break;
                    case MSG_CHECK_PASSWORD_PICTURE:
                        Log.i(TAG, "开始发送访客密码图片地址到服务器");
                        String[] parameters3 = (String[]) msg.obj;
                        tempKey = parameters3[0];
                        imageUrl = parameters3[1];
                        imageUuid = parameters3[2];
                        startCheckGuestPasswordAppendImage();
                        break;
                    case MSG_CARD_INCOME: {
                        // TODO: 2018/5/8 下面的方法中进行卡信息处理（判定及开门等）  onCardIncome((String) msg.obj);
                        String obj1 = (String) msg.obj;
                        Log.e(TAG, "onCardIncome obj1" + obj1);
                        break;
                    }
                    case MSG_UPDATE_NETWORKSTATE: {
                        boolean obj1 = (boolean) msg.obj;
                        Log.e(TAG, "initWhenConnected obj1" + obj1);
                        if (obj1) {
                            initWhenConnected(); //开始在线版本
                        } else {
                            // TODO: 2018/5/8   initWhenOffline(); //开始离线版本
                        }
                        break;
                    }
                    case REGISTER_ACTIVITY_DIAL:
                        initConnectReport();//心跳开始,根据心跳返回结果开启各更新程序
                        break;
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
            public void run() {
                try {
                    connectReport();//首次执行
                    while (!isInterrupted()) {//检测线程是否已经中断
                        sleep(DeviceConfig.CONNECT_REPORT_WAIT_TIME); //心跳间隔时间
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
     * 开启检查密码线程
     */
    private void startCheckGuestPassword() {
        new Thread() {
            public void run() {
                checkGuestPassword();
            }
        }.start();
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
        try {
            String url = DeviceConfig.SERVER_URL + "/app/device/openDoorByTempKey?from=";
            url = url + this.key;
            url = url + "&communityId=" + communityId;
            url = url + "&blockId=" + this.blockId;
            url = url + "&tempKey=" + this.tempKey;
            if (imageUuid != null) {
                url = url + "&imageUuid=" + URLEncoder.encode(this.imageUuid, "UTF-8");
            }
            if (imageUrl != null) {
                url = url + "&imageUrl=" + URLEncoder.encode(this.imageUrl, "UTF-8");
            }
            try {
                String result = HttpApi.getInstance().loadHttpforGet(url, httpServerToken);
                if (result != null) {
                    HttpApi.i("checkGuestPassword()->" + result);
                    Message message = mHandler.obtainMessage();
                    message.what = MSG_GUEST_PASSWORD_CHECK;
                    message.obj = Ajax.getJSONObject(result);
                    mHandler.sendMessage(message);
                } else {
                    HttpApi.i("checkGuestPassword()->服务器异常");
                }
            } catch (Exception e) {
                Message message = mHandler.obtainMessage();
                message.what = MSG_GUEST_PASSWORD_CHECK;
                mHandler.sendMessage(message);
                e.printStackTrace();
            }
        } catch (Exception e) {
        }
    }

    private void onCheckGuestPassword(JSONObject result) {
        try {
            int code = 0;
            if (result != null) {
                code = result.getInt("code");
                if (code == 0) {
                    Log.e(TAG, "-----------------密码开门成功  开门开门------------------");
                }
            } else {
                code = -1;
                Log.e(TAG, "--------------------密码开门失败  --------------------");
            }
            sendMessageToMainAcitivity(MSG_PASSWORD_CHECK, code);

        } catch (JSONException e) {
        }
    }

    int i = 0;

    /**
     * 心跳接口
     */
    private void connectReport() {
//        Log.e(TAG, "心跳执行" + i + "次");
        /**
         * 锁门禁向服务器发送的心跳包：每间隔一段时间发送一次（间隔时间由服务器返回,默认1分钟），服务器接到心跳包后，返回心跳发送间隔(s) ,服务器时间()
         * ，卡最后更新时间（版本），广告最后更新时间（版本），人脸最后更新时间（版本），通告最后更新时间（版本）,离线密码，版本号，服务器记录本次心跳时间，用于后续判断锁门禁是否在线

         请求路径：xdoor/device/connectReport
         请求参数：String mac 地址，Integer xiangmu_id 项目ID，String date锁门禁时间(毫秒值)，String version版本号（APP版本)

         */
        try {
            String url = XINTIAO_URL;
            JSONObject data = new JSONObject();
//            data.put("username", mac);
//            data.put("password", key);
            data.put("mac", mac);
            data.put("xiangmu_id", lockId);
            String result = HttpApi.getInstance().loadHttpforPost(url, data, "");
            i++;
            Log.e(TAG, "心跳执行" + i + "次");
            if (result != null) {
                HttpApi.e("connectReportInfo()->" + result);
                JSONObject resultObj = Ajax.getJSONObject(result);
                int code = resultObj.getInt("code");
                if (code == 0) {
                    //比较返回数据与本地数据是否一致,并设置更新状态
                    if (false) {
                        //如果不一致，通知主线程
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                //在主线程调用方法更新对应的数据
//                            }
//                        });
                    }
                }
            } else {
                //服务器异常或没有网络
                HttpApi.e("connectReportInfo()->服务器无响应");
            }
        } catch (Exception e) {
            HttpApi.e("connectReportInfo()->服务器数据解析异常");
            e.printStackTrace();
        }

    }

    protected void init() {

        // TODO: 2018/5/8    initAexUtil(); //安卓工控设备控制器初始化
        Log.i("MainService", "init AEX");
        // TODO: 2018/5/8  initSqlUtil();  初始化卡相关数据库工具类
        Log.i("MainService", "init SQL");
        // TODO: 2018/5/8   initCheckTopActivity();检查最上层界面

        //xiaozd add
        if (netWorkstate) {
            initWhenConnected(); //开始在线版本
        } else {
            // TODO: 2018/5/8   initWhenOffline(); //开始离线版本
        }
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
                // TODO: 2018/5/8 loadInfoFromLocal(); //获取本地sp文件中的数据
                // TODO: 2018/5/8 sendInitMessenger(MSG_LOADLOCAL_DATA);//在MainActivity中展示
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
                        result = getClientInfo();
                        if (!result) {
                            sleep(1000 * 10);//间隔10秒登录一次
                        }
                    } while (!result);
                } catch (Exception e) {
                }
            }
        }.start();
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
                boolean hasKa = mDbUtils.isHasKa("1052");
                Log.i(TAG, System.currentTimeMillis() + "查询后" + hasKa);
            }
        }).start();
    }

    /**
     * 登录接口
     *
     * @return
     * @throws JSONException
     */
    protected boolean getClientInfo() throws JSONException {
        final boolean[] resultValue = {false};
        try {
            String url = API.DEVICE_LOGIN;
            JSONObject data = new JSONObject();
            data.put("mac", "44:2c:05:e6:9c:c5");
            data.put("key", "442c05e69cc5");
            data.put("version", "1.0");
            OkHttpUtils.postString().url(url).content(data.toString()).mediaType(MediaType.parse
                    ("application/json; charset=utf-8")).tag(this).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    Log.e(TAG, "e " + e.toString());
                }

                @Override
                public void onResponse(String response, int id) {
                    Log.e("wh response", response);
                    if (null != response) {
                        String code = JsonUtil.getFieldValue(response, "code");
                        if ("0".equals(code)) {
                            resultValue[0] = true;
                            String result1 = JsonUtil.getResult(response);
                            DoorBean doorBean = JsonUtil.parseJsonToBean(result1, DoorBean.class);
                            httpServerToken = doorBean.getToken();
                            Log.e(TAG, doorBean.toString());
                            //保存返回数据，通知主线程继续下一步逻辑
                            Message message = mHandler.obtainMessage();
                            message.what = MSG_LOGIN;
                            message.obj = doorBean.getXdoor();
                            mHandler.sendMessage(message);
                        }
                    } else {
                        //服务器异常或没有网络
                        HttpApi.e("getClientInfo()->服务器无响应");
                    }

                }
            });
        } catch (Exception e) {
            HttpApi.e("登录接口返回参数getClientInfo()->服务器数据解析异常");
        }
        return resultValue[0];
    }

    /**
     * 登录成功后
     *
     * @param msg
     */
    protected void onLogin(Message msg) {
//        XdoorBean result = (XdoorBean) msg.obj;
//        this.blockId = result.getLixian_mima();
//        communityId = result.getXiangmu_id();
//        lockId = result.getDanyuan_id();
//        lockName = result.getName();
//        communityName = result.getName();
//        if (this.blockId == 0) {
//            DeviceConfig.DEVICE_TYPE = "C";
//        }
//        // 保存消息
//        //  saveInfoIntoLocal(communityId, blockId, lockId, communityName, lockName);
//    }
//
//    Message message = Message.obtain();
//    message.what =MSG_LOGIN_AFTER;
//    message.obj =result;
//
//        mainMessage.send(message);


//        lockName = result.ge();
//        communityName = user.getString("communityName");
//        Log.i(TAG, "登录成功后保存一些相关变量到本地");
//        XdoorBean result = (XdoorBean) msg.obj;
//
//                this.blockId = result.getLoudong_id();
//                communityId = result.getXiangmu_id();
//                lockId = result.getDanyuan_id();
//                lockName = result.getd();
//                communityName = user.getString("communityName");
//                if (this.blockId == 0) {
//                    DeviceConfig.DEVICE_TYPE = "C";
//                }
//                Log.i(TAG, "user=" + user.toString());
//                // 保存消息
//                //  saveInfoIntoLocal(communityId, blockId, lockId, communityName, lockName);
//            }
//            Message message = Message.obtain();
//        message.what = MSG_LOGIN_AFTER;
//        message.obj = result;
//            try {
//                mainMessage.send(message);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        } catch (JSONException e) {
//        }
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
//                calling("1003");
//                calling("1002");
//                calling("2008");
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
            //   Log.i(TAG,"收到来电");
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
//            sendDialMessenger(MSG_REFRESH_DATA, "card");
//            retrieveCardList();//获取已注册卡信息
        } else if (content.equals("refresh finger info")) {
//            sendDialMessenger(MSG_REFRESH_DATA, "finger");
        } else if (content.equals("refresh all info")) {
        } else if (content.startsWith("reject call")) { //挂断
            if (!rejectUserList.contains(from)) {
                rejectUserList.add(from);
            }
        } else if (content.startsWith("open the door")) {
            String imageUrl = null;
            int thisIndex = content.indexOf("-");
            if (thisIndex > 0) {
                imageUrl = content.substring(thisIndex + 1);
            } else {
                imageUrl = null;
            }
            //日志
//            startCreateAccessLog(from, imageUrl);
            cancelOtherMembers(from);
            Log.v("MainService", "用户直接开门，取消其他呼叫");
            resetCallMode();
            stopTimeoutCheckThread();
            //开门操作
            Log.e(TAG, "进行开门操作 开门开门");
        } else if (content.startsWith("refuse call")) { //拒绝接听
            if (!rejectUserList.contains(from)) {
                rejectUserList.add(from);
            }
            Log.d(TAG, "onMessage: ++++++++++++" + from);
            cancelOtherMembers(from);
            Log.v("MainService", "用户没有接听，取消其他呼叫");
            resetCallMode();
            sendMessageToMainAcitivity(MSG_CALLMEMBER_TIMEOUT, ""); //通知界面目前已经超时，并进入初始状态
            stopTimeoutCheckThread();
        }
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


    /****************************呼叫相关********************************/
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
                    JSONObject userObject = (JSONObject) triedUserList.get(i);
                    String username = (String) userObject.get("username");
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
                    JSONObject userObject = (JSONObject) iterator.next();
                    String username = (String) userObject.get("username");
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
        final String callUuid = this.imageUuid;
        new Thread() {
            public void run() {
                callMember(callUuid);
            }
        }.start();
    }

    /**
     * 获取需要呼叫成员
     *
     * @param callUuid
     */
    private void callMember(String callUuid) {
        try {
            String url = DeviceConfig.SERVER_URL + "/app/device/callAllMembers?from=";
            url = url + this.key;
            url = url + "&communityId=" + communityId;
            if (DeviceConfig.DEVICE_TYPE.equals("C")) {
                url = url + "&blockId=" + this.inputBlockId;
            } else {
                url = url + "&blockId=" + this.blockId;
            }
            url = url + "&unitNo=" + this.unitNo;
            try {
                String result = HttpApi.getInstance().loadHttpforGet(url, httpServerToken);
                if (result != null && isCurrentCallWorking(callUuid)) {
                    HttpApi.i("callMember()->" + result);
                    Message message = mHandler.obtainMessage();
                    message.what = MSG_CALLMEMBER;
                    Object[] objects = new Object[2];
                    objects[0] = callUuid;
                    objects[1] = Ajax.getJSONObject(result);
                    message.obj = objects;
                    mHandler.sendMessage(message);
                } else {
                    HttpApi.e("callMember->服务器异常");
                }
            } catch (Exception e) {
                Message message = mHandler.obtainMessage();
                message.what = MSG_CALLMEMBER;
                mHandler.sendMessage(message);
                e.printStackTrace();
            }
        } catch (Exception e) {
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
            JSONObject result = (JSONObject) objects[1];
            HttpApi.i("拨号中->网络请求在线列表");
            JSONArray userList = (JSONArray) result.get("userList");
            JSONArray unitDeviceList = (JSONArray) result.get("unitDeviceList");
            HttpApi.i("拨号中->网络请求在线列表" + (result != null ? result.toString() : ""));
            if ((userList != null && userList.length() > 0) || (unitDeviceList != null && unitDeviceList.length() >
                    0)) {
                Log.v("MainService", "收到新的呼叫，清除呼叫数据，UUID=" + callUuid);
                HttpApi.i("拨号中->清除呼叫数据");
                allUserList.clear();
                triedUserList.clear();
                onlineUserList.clear();
                offlineUserList.clear();
                rejectUserList.clear();
                callConnectState = CALL_VIDEO_CONNECTING;
                if (unitDeviceList != null) {
                    for (int i = 0; i < unitDeviceList.length(); i++) {
                        allUserList.add(unitDeviceList.get(i));
                    }
                }
                if (userList != null) {
                    for (int i = 0; i < userList.length(); i++) {
                        allUserList.add(userList.get(i));
                    }
                }
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
        Object object = triedUserList.get(triedUserList.size() - 1);
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
                    JSONObject userObject = (JSONObject) allUserList.remove(0);
                    String username = (String) userObject.get("username");
                    if (username.length() == 17) {
                        username = username.replaceAll(":", "");
                    }
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
        pushCallMessage();
        if (needWait) { //检查在线人数,大于0则等待一段时间
            startTimeoutChecking();
        } else {
            sendMessageToMainAcitivity(MSG_CALLMEMBER_NO_ONLINE, "");//告诉用户无人在线
        }
    }

    protected void pushCallMessage() {
        String pushList = null;
        for (int j = 0; j < offlineUserList.size(); j++) {
            JSONObject userObject = (JSONObject) offlineUserList.get(j);
            String username = null;
            try {
                username = (String) userObject.get("username");
            } catch (JSONException e) {
            }
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return serviceMessage.getBinder();
    }

    /**
     * 发送消息到mainactivity
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
}
