package com.cxwl.hurry.doorlock.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.cxwl.hurry.doorlock.R;
import com.cxwl.hurry.doorlock.ui.activity.MainActivity;
import com.cxwl.hurry.doorlock.utils.MacUtils;

import org.json.JSONException;
import org.json.JSONObject;

import jni.http.HttpManager;
import jni.http.HttpResult;
import jni.http.RtcHttpClient;
import jni.util.Utils;
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

import static android.provider.UserDictionary.Words.APP_ID;


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
    RtcClient rtcClient;
    boolean isRtcInit = false; //RtcSDK初始化状态
    //天翼登陆参数
    public static final String APP_ID = "71012";
    public static final String APP_KEY = "71007b1c-6b75-4d6f-85aa-40c1f3b842ef";
    private String token;//天翼登陆所需的token；
    private Device device;//天翼登陆连接成功 发消息的类

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "service启动");
        initHandler();
        initMacKey();
        initTYSDK();
    }

    private void initMacKey() {
        mac = MacUtils.getMac();
        key = mac.replace(":", "");
        Log.i(TAG, "初始化mac=" + mac + "key=" + key);
    }

    /****************************初始化天翼操作********************************/
    public static Connection callConnection;

    /**
     * 初始化天翼sdk
     */
    private void initTYSDK() {
        if (!isRtcInit) {
            rtcClient = new RtcClientImpl();
            rtcClient.initialize(this.getApplicationContext(), new ClientListener() {
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
        HttpResult ret = HttpManager.getInstance().getCapabilityToken(jsonobj, APP_ID, APP_KEY);
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
                jargs.put(RtcConst.kAccAppID, APP_ID);//应用id
                jargs.put(RtcConst.kAccUser, key); //号码
                jargs.put(RtcConst.kAccType, RtcConst.UEType_Current);//终端类型
                jargs.put(RtcConst.kAccRetry, 5);//设置重连时间
                device = rtcClient.createDevice(jargs.toString(), deviceListener);
                //登陆
                Log.i(TAG, " 设置监听 deviceListener   ");
                calling("1003");
                calling("1002");
                calling("2008");
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

        }

        @Override
        public void onReceiveIm(String s, String s1, String s2) {

        }

        @Override
        public void onNewCall(Connection connection) {

        }

        @Override
        public void onQueryStatus(int i, String s) {

        }
    };

    private void calling(String callName) {
        try {
            String remoteuri = RtcRules.UserToRemoteUri_new(callName, RtcConst.UEType_Any);
            JSONObject jinfo = new JSONObject();
            jinfo.put(RtcConst.kCallRemoteUri, remoteuri);
            jinfo.put(RtcConst.kCallType, RtcConst.CallType_A_V);
            callConnection=device.connect(jinfo.toString(), connectionListener);
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
            callConnection=null;
        }

        @Override
        public void onVideo() {
            Log.i(TAG, "onVideo");
            sendMessageToMainAcitivity(MainActivity.MSG_RTC_ONVIDEO_IN, "");

        }

        @Override
        public void onNetStatus(int msg, String info) {

        }
    };

    /****************************初始化天翼操作********************************/
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
}
