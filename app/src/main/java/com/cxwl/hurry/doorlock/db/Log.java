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
public class Log {
    @Id
    private Long id;
    @Property(nameInDb = "mac")
    private String mac;
    @Property(nameInDb = "kaimenshijian")
    private String kaimenshijian;
    @Property(nameInDb = "kaimenfangshi")
    private String kaimenfangshi;
    @Property(nameInDb = "kaimenjietu")
    private String kaimenjietu;
    @Generated(hash = 1623580919)
    public Log(Long id, String mac, String kaimenshijian, String kaimenfangshi,
            String kaimenjietu) {
        this.id = id;
        this.mac = mac;
        this.kaimenshijian = kaimenshijian;
        this.kaimenfangshi = kaimenfangshi;
        this.kaimenjietu = kaimenjietu;
    }
    @Generated(hash = 1364647056)
    public Log() {
    }
    public String getKaimenjietu() {
        return this.kaimenjietu;
    }
    public void setKaimenjietu(String kaimenjietu) {
        this.kaimenjietu = kaimenjietu;
    }
    public String getKaimenfangshi() {
        return this.kaimenfangshi;
    }
    public void setKaimenfangshi(String kaimenfangshi) {
        this.kaimenfangshi = kaimenfangshi;
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
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
 
}
