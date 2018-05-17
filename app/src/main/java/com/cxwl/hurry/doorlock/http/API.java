package com.cxwl.hurry.doorlock.http;

/**
 * Created by William on 2018/5/10.
 */

public interface API {
    String HTTP_HOST = "http://192.168.8.142:8084";

    String DEVICE_LOGIN = HTTP_HOST + "/xdoor/device/deviceLogin";//登录

    String CONNECT_REPORT = HTTP_HOST + "/xdoor/device/connectReport";//心跳

    String CALLALL_MEMBERS = HTTP_HOST+"/xdoor/device/callAllMembers";//获取成员

    String OPENDOOR_BYTEMPKEY = HTTP_HOST+"/xdoor/device/openDoorByTempKey";//密码验证

    String LOG = HTTP_HOST+"/xdoor/device/createAccessLog";//日志提交

    String CALLALL_CARDS = HTTP_HOST + "/xdoor/device/callAllCards";//获取门禁卡信息

    String SYNC_CALLBACK = HTTP_HOST + "xdoor/device/syncCallBack";//同步完成通知

}
