package com.cxwl.hurry.doorlock.entity;

import com.cxwl.hurry.doorlock.db.LogDoor;

import java.util.List;

/**
 * @author xlei
 * @Date 2018/5/18.
 */

public class LogListBean {
    private String mac;
    private List<LogDoor> xdoorOneOpenDtos;

    public List<LogDoor> getXdoorOneOpenDtos() {
        return xdoorOneOpenDtos;
    }

    public void setXdoorOneOpenDtos(List<LogDoor> xdoorOneOpenDtos) {
        this.xdoorOneOpenDtos = xdoorOneOpenDtos;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
