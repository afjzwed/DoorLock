package com.cxwl.hurry.doorlock.config;

/**
 * Created by simon on 2016/7/23.
 */
public class DeviceConfig {
    /********residential*****/
    public static final String SERVER_URL = "http://192.168.8.146:80";
    //   public static final String SERVER_URL = "http://www.lockaxial.com";

    public static final String LOCAL_FILE_PATH = "adv";//广告储存位置
    public static String DEVICE_TYPE = "B"; //C：社区大门门禁 B:楼栋单元门禁


    public static int CANCEL_CALL_WAIT_TIME = 1000 * 30;//自动取消呼叫等待时间
    /*******************************/
}
