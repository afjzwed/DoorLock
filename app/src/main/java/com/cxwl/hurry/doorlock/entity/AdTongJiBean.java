package com.cxwl.hurry.doorlock.entity;

/**
 * @author xlei
 * @Date 2018/5/23.
 */

public class AdTongJiBean {
    private int add_id;
    private String start_time;
    private String end_time;
    private String mac;

    public int getAdd_id() {
        return add_id;
    }

    public void setAdd_id(int add_id) {
        this.add_id = add_id;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    @Override
    public String toString() {
        return "AdTongJiBean{" + "add_id='" + add_id + '\'' + ", start_time='" + start_time + '\'' + ", end_time='" +
                end_time + '\'' + ", mac='" + mac + '\'' + '}';
    }

}
