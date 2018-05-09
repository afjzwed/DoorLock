package com.cxwl.hurry.doorlock.utils;

/**
 * @author xlei
 * @Date 2018/5/3.
 */

public class Constant {

    public static final int MSG_RTC_NEWCALL = 10000;//收到新的来电
    public static final int MSG_RTC_ONVIDEO = 10001;//视频通话连接
    public static final int MSG_RTC_DISCONNECT = 10002;//视频通话断开
    public static final int MSG_DISCONNECT_VIEDO = 20009;//

    public static final int MSG_CALLMEMBER_SERVER_ERROR = 12105; //呼叫服务器没返回值错误
    public static final int MSG_CALLMEMBER_ERROR = 10005;//呼叫错误

    public static final int CALL_MODE = 1;    //呼叫模式
    public static final int PASSWORD_MODE = 2;//密码验证模式
    public static final int CALLING_MODE = 3; //正在呼叫模式
    public static final int ONVIDEO_MODE = 4; //正在视频
    public static final int ERROR_MODE = 6;   //网络出错
    public static final int PASSWORD_CHECKING_MODE = 9;//正在验证密码

    //视频链接状态
    public static final int CALL_WAITING = 20;  //等待链接中
    public static final int CALL_VIDEO_CONNECTING = 21; //链接中

    public static final int MSG_LOGIN = 20001; //登录成功
    public static final int MSG_LOGIN_AFTER = 20002; //登录成功后,开始rtc登录,人脸识别,读卡开始,心跳包等
    public static final int MSG_RTC_REGISTER = 20003;//rtc登录
    public static final int MSG_CANCEL_CALL = 20010;//取消呼叫
    public static final int MSG_CALLMEMBER_TIMEOUT = 11005;//呼叫成员超时
    public static final int MSG_CALLMEMBER_NO_ONLINE = 12005;//呼叫成员不在线

    public static final int MSG_INPUT_CARDINFO = 0x04;//重复录入卡信息


    public static final int MSG_GUEST_PASSWORD_CHECK = 20007;//获取服务器返回的密码
    public static final int MSG_PASSWORD_CHECK = 10003;//验证密码后

}
