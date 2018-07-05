package com.cxwl.hurry.doorlock.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author xlei
 * @Date 2018/5/23.
 */

@Entity
public class AdTongJiBean {
    @Id
    private Long id;
    @Property(nameInDb = "ad_id")
    private int ad_id;
    @Property(nameInDb = "start_time")
    private String start_time;
    @Property(nameInDb = "end_time")
    private String end_time;
    @Property(nameInDb = "mac")
    private String mac;
    @Generated(hash = 1076116915)
    public AdTongJiBean(Long id, int ad_id, String start_time, String end_time,
            String mac) {
        this.id = id;
        this.ad_id = ad_id;
        this.start_time = start_time;
        this.end_time = end_time;
        this.mac = mac;
    }
    @Generated(hash = 1025819527)
    public AdTongJiBean() {
    }
    public String getMac() {
        return this.mac;
    }
    public void setMac(String mac) {
        this.mac = mac;
    }
    public String getEnd_time() {
        return this.end_time;
    }
    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }
    public String getStart_time() {
        return this.start_time;
    }
    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }
    public int getAd_id() {
        return this.ad_id;
    }
    public void setAd_id(int add_id) {
        this.ad_id = add_id;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "AdTongJiBean{" +
                "id=" + id +
                ", ad_id=" + ad_id +
                ", start_time='" + start_time + '\'' +
                ", end_time='" + end_time + '\'' +
                ", mac='" + mac + '\'' +
                '}';
    }
}
