package com.cxwl.hurry.doorlock.http;

/**
 * Created by William on 2018/5/10.
 */

public interface API {
    String HTTP_HOST = "http://192.168.8.142:8084";

    String DEVICE_LOGIN = "/xdoor/device/deviceLogin";//登录

    String CONNECT_REPORT = "/xdoor/device/connectReport";//心跳

}