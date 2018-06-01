package com.cxwl.hurry.doorlock.http;

/**
 * Created by William on 2018/5/10.
 */

public interface API {
    String HTTP_HOST = "http://192.168.8.132:80";
//    String HTTP_HOST = "http://120.79.212.90:80";

    String DEVICE_LOGIN = HTTP_HOST + "/xdoor/device/deviceLogin";//登录

    String CONNECT_REPORT = HTTP_HOST + "/xdoor/device/connectReport";//心跳

    String CALLALL_MEMBERS = HTTP_HOST + "/xdoor/device/callAllMembers";//获取成员

    String OPENDOOR_BYTEMPKEY = HTTP_HOST + "/xdoor/device/openDoorByTempKey";//密码验证

    String LOG = HTTP_HOST + "/xdoor/device/createAccessLog";//日志提交 开门方式:1卡2手机3人脸4邀请码5离线密码6临时密码'

    String CALLALL_CARDS = HTTP_HOST + "/xdoor/device/callAllCards";//获取门禁卡信息

    String CALLALL_NOTICES = HTTP_HOST + "/xdoor/device/callAllNotices";//获取通告信息

    String SYNC_CALLBACK = HTTP_HOST + "/xdoor/device/syncCallBack";//同步完成通知

    String CALLALL_FACES = HTTP_HOST + "/xdoor/device/callAllFaces";//获取人脸信息

    String CALLALL_ADS = HTTP_HOST + "/xdoor/device/callAllAds";//获取广告信息

    String VERSION_ADDRESS = HTTP_HOST + "/xdoor/device/version";//获取版本更新路径

    String ADV_TONGJI = HTTP_HOST + "/xdoor/device/tongji";//获取版本更新路径

    String QINIU_IMG = HTTP_HOST + "/qiniu/getQiniuToken";//获取七牛图片路径

    String PIC = "http://img0.hnchxwl.com/";//七牛图片访问地址
}
