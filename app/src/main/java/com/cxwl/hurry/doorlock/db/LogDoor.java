package com.cxwl.hurry.doorlock.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author xlei
 * @Date 2018/4/27.
 */
@Entity
public class LogDoor {
    @Id
    private Long id;


    @Property(nameInDb = "ka_id")
    private String ka_id;

    @Property(nameInDb = "kaimenfangshi")
    private int kaimenfangshi;

    @Property(nameInDb = "mac")
    private String mac;

    @Property(nameInDb = "kaimenshijian")
    private String kaimenshijian;

    @Property(nameInDb = "phone")
    private String phone;

    @Property(nameInDb = "uuid")
    private String uuid;

    @Property(nameInDb = "kaimenjietu")
    private String kaimenjietu;

    @Property(nameInDb = "mima")
    private String mima;

    @Property(nameInDb = "state")
    private int state;

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getMima() {
        return this.mima;
    }

    public void setMima(String mima) {
        this.mima = mima;
    }

    public String getKaimenjietu() {
        return this.kaimenjietu;
    }

    public void setKaimenjietu(String kaimenjietu) {
        this.kaimenjietu = kaimenjietu;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getKaimenshijian() {
        return this.kaimenshijian;
    }

    public void setKaimenshijian(String kaimenshijian) {
        this.kaimenshijian = kaimenshijian;
    }

    public String getMac() {
        return this.mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getKaimenfangshi() {
        return this.kaimenfangshi;
    }

    public void setKaimenfangshi(int kaimenfangshi) {
        this.kaimenfangshi = kaimenfangshi;
    }

    public String getKa_id() {
        return this.ka_id;
    }

    public void setKa_id(String ka_id) {
        this.ka_id = ka_id;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Generated(hash = 487498739)
    public LogDoor(Long id, String ka_id, int kaimenfangshi, String mac,
            String kaimenshijian, String phone, String uuid, String kaimenjietu,
            String mima, int state) {
        this.id = id;
        this.ka_id = ka_id;
        this.kaimenfangshi = kaimenfangshi;
        this.mac = mac;
        this.kaimenshijian = kaimenshijian;
        this.phone = phone;
        this.uuid = uuid;
        this.kaimenjietu = kaimenjietu;
        this.mima = mima;
        this.state = state;
    }

    @Generated(hash = 1490727955)
    public LogDoor() {
    }




}
