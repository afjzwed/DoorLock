package com.cxwl.hurry.doorlock.config;


/**
 * Created by simon on 2016/7/23.
 */
public class DeviceConfig {
    /********residential*****/

    public static final String LOCAL_FILE_PATH = "adv";//广告储存位置
    public static final String LOCAL_ADV_PATH = "adv";//广告视频储存位置
    public static final String LOCAL_ADP_PATH = "adp";//广告图片储存位置
    public static final String LOCAL_FACE_PATH = "myface";//人脸图片(bin文件)储存位置(下载)
    public static final String LOCAL_APK_PATH = "myapk";//apk文件储存位置
    public static final String LOCAL_FACEINFO_PATH = "myfaceinfo";//人脸数据库储存位置
    public static final String LOCAL_FACE_PATH_TEMP = "tempface";//人脸图片(bin文件)临时储存位置(上传)
    public static final String LOCAL_IMG_PATH = "img_local";//所有照片



    public static String DEVICE_TYPE = "B"; //C：社区大门门禁 B:楼栋单元门禁

    public static String RFID_PORT = "/dev/ttyS1";//卡阅读器使用

    // TODO: 2018/7/11 后期可能会控制，先注释  public static int OPENDOOR_TIME = 1000 * 5;//开门持续时间
    // TODO: 2018/7/11 先注释 public static int OPENDOOR_STATE = 0;//开门状态 0为关闭门锁 1为打开门锁 默认关闭
    public static int CANCEL_CALL_WAIT_TIME = 1000 * 30;//自动取消呼叫等待时间
    public static int PASSWORD_WAIT_TIME = 1000 * 20;//密码验证线程等待时间

    public static int VOLUME_STREAM_MUSIC = 5;//音乐音量
    public static int VOLUME_STREAM_VOICE_CALL = 5;//通话音量
    public static int VOLUME_STREAM_RING = 5;//铃声音量
    public static int VOLUME_STREAM_SYSTEM = 5;//系统音量

    public static int MOBILE_NO_LENGTH = 11;//手机号长度
    public static int UNIT_NO_LENGTH = 4;//房屋号长度
    public static int BLOCK_LENGTH = 8;//楼栋房屋号长度

    public static int PRINTSCREEN_STATE = 0;//各种方式(人脸/卡)是否开始处理图片并上传日志的状态  0:未开始 1:人脸 2:卡成功 3:一键开门

    public static final int DEVICE_KEYCODE_POUND = 66;//确认键
    public static final int DEVICE_KEYCODE_STAR = 67;//删除键
//    public static final int DEVICE_KEYCODE_POUND = 30;//确认键
//    public static final int DEVICE_KEYCODE_STAR = 32;//删除键

    public static final String Lockaxial_Monitor_PackageName = "com.cxwl.hurry.monitor";
    public static final String Lockaxial_Monitor_SERVICE = "com.cxwl.hurry.monitor.MonitorService";

    public static final String SD_PATH = "CXWL/Monitor";

    /*******************************/

}
