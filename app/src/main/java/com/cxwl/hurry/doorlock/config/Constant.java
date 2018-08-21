package com.cxwl.hurry.doorlock.config;

/**
 * 一些常量值
 * Created by William on 2018/5/11.
 */

public class Constant {
    /********************虹软相关*********************/
    public static String arc_appid = "F8FoZsZXFJTrbG1xtRY8ghqYi8UFqpAq9F7Nbo2v6jK8";//APPID
    public static String ft_key = "79kKgHjTgz27bkMMkKA8G6TjzSKnRN46cS7ad5JnQNdc";//人脸追踪(FT) Key :
    public static String fd_key = "79kKgHjTgz27bkMMkKA8G6Ts9qaxt7NsLEmKwBBH84TF";//人脸检测(FD) Key :
    public static String fr_key = "79kKgHjTgz27bkMMkKA8G6TzKEr6TYfhDa6UAuqxKK5r";//人脸识别(FR) Key :
    public static String age_key = "79kKgHjTgz27bkMMkKA8G6Uc8F9xjy9bcFCL3o9w8k6m";//年龄识别(Age) Key :
    public static String gender_key = "79kKgHjTgz27bkMMkKA8G6UjHeRAg5TZNJ4u8sPEKj9m";//性别识别
    // (Gender) Key :

    //    public static final String PIC_PREFIX = "arcsoft_";//虹软人脸照片文件夹名
    public static final int MSG_FACE_DETECT_CONTRAST = 11100;//人脸识别对比
    public static final int MSG_FACE_DETECT_INPUT = 11101;//人脸识别录入
    public static final int MSG_FACE_DETECT_PAUSE = 11102;//人脸识别暂停
    public static final int MSG_ID_CARD_DETECT_PAUSE = 11103;//身份证识别暂停
    public static final int MSG_ID_CARD_DETECT_RESTART = 11104;//身份证识别重新开始
    public static final int MSG_ID_CARD_DETECT_INPUT = 11105;//身份证识别重新开始

    public static final int MSG_FACE_DETECT_CHECK = -1;//人脸识别重置标志位

    public static final int MSG_FACE_INFO = 11106;//获取人脸照片URL
    public static final int MSG_FACE_DOWNLOAD = 11107;//下载照片并录入人脸
    public static final int MSG_FACE_INFO_FINISH = 11108;//获取人脸照片信息录入完成
    public static final int MSG_DELETE_FACE = 11109;//删除人脸信息

    public static final int MSG_FACE_OPENLOCK = 40000;//人脸开门

    public static final float FACE_MAX = 0.6f;
    /********************虹软相关*********************/

    /********************天翼RTC相关******************/
    public static final String RTC_APP_ID = "71012";
    public static final String RTC_APP_KEY = "71007b1c-6b75-4d6f-85aa-40c1f3b842ef";

    public static final int MSG_RTC_NEWCALL = 10000;//收到新的来电
    public static final int MSG_RTC_ONVIDEO = 10001;//视频通话连接
    public static final int MSG_RTC_DISCONNECT = 10002;//视频通话断开
    public static final int MSG_DISCONNECT_VIEDO = 200010;//

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
    public static final int MSG_LOGIN_FAILED = 2000101; //登录失败
    public static final int MSG_LOGIN_AFTER = 20002; //登录成功后,开始rtc登录,人脸识别,读卡开始,心跳包等
    public static final int MSG_RTC_REGISTER = 20003;//rtc登录
    public static final int MSG_CALLMEMBER = 20004;//呼叫成员
    public static final int MSG_START_DIAL = 20005;//开始呼叫
    public static final int MSG_CANCEL_CALL = 20010;//取消呼叫
    public static final int MSG_CALLMEMBER_TIMEOUT = 11005;//呼叫成员超时
    public static final int MSG_CALLMEMBER_NO_ONLINE = 12005;//呼叫成员不在线

    public static final int MSG_GUEST_PASSWORD_CHECK = 20007;//获取服务器返回的密码
    public static final int MSG_LIXIAN_PASSWORD_CHECK = 20009;//获取离线验证密码
    public static final int MSG_LIXIAN_PASSWORD_CHECK_AFTER = 200090;//获取离线验证密码
    public static final int MSG_PASSWORD_CHECK = 10003;//验证密码后
    public static final int MSG_TONGJI_VEDIO = 200091;//上传统计视频播放消息
    public static final int MSG_TONGJI_PIC = 200092;//上传统计广告图片播放消息

    public static final int MSG_START_DIAL_PICTURE = 21005;//开始呼叫的访客图片
    public static final int MSG_CHECK_PASSWORD_PICTURE = 21006;//密码访客图片

    public static final int MSG_YIJIANKAIMEN_OPENLOCK = 21007;//手机一键开门后截图和上传日志
    /********************天翼RTC相关******************/

    /********************卡相关*******************/
    public static final int MSG_LOCK_OPENED = 10004;//开锁
    public static final int MSG_INVALID_CARD = 10015;//无效房卡
    public static final int MSG_INPUT_CARDINFO = 0x04;//重复录入卡信息
    public static final int MSG_CARD_OPENLOCK = 10016;//卡开门后截图和上传日志
    public static final int MSG_CARD_INCOME = 20008;//刷卡回调
    /********************卡相关*******************/

    /*******************sp保存的心跳信息****************/
    public static final String SP_VISION_KA = "ka_vison";//卡版本
    public static final String SP_VISION_LIAN = "lian_vison";//脸版本
    public static final String SP_VISION_GUANGGAO = "guanggao_vison";//图片广告版本
    public static final String SP_VISION_GUANGGAO_VIDEO = "guanggao_vison_video";//视频广告版本
    public static final String SP_VISION_TONGGAO = "tonggao_vison";//通告版本
    public static final String SP_VISION_APP = "app_vison";//应用版本
    public static final String SP_LIXIAN_MIMA = "lixian_mima";//离线密码
    public static final String SP_XINTIAO_TIME = "xintiao_time";//心跳间隔时间
    /*******************sp保存的心跳信息****************/

    public static final int MSG_LOADLOCAL_DATA = 20030;//离线模式

    public static final int MSG_GET_NOTICE = 20100;//获取通告成功

    public static final int MSG_ADVERTISE_REFRESH = 10013;//刷新广告
    public static final int MSG_ADVERTISE_REFRESH_PIC = 10014;//刷新广告图片
    public static final int MSG_YIJIANKAIMEN_TAKEPIC = 10098;//手机一键开门拍照片
    public static final int MSG_YIJIANKAIMEN_TAKEPIC1 = 10099;//手机开门拍照片
    public static final int MSG_UPLOAD_LIXIAN_IMG = 100001;//上传离线img

    public static final int MSG_CHECK_PASSWORD = 20006;//检查密码
    public static final int MSG_UPDATE_NETWORKSTATE = 20028;//网络状态改变

    public static final int MSG_RESTART_VIDEO1 = 11110;//媒体流的重启
    public static final int MSG_RESTART_VIDEO = 100002;//媒体流的重启
    public static  boolean RESTART_PHONE = false;//手机重启
    public static boolean RESTART_AUDIO = false;//媒体流重启

}
