package com.cxwl.hurry.doorlock.entity;

/**
 * Created by William on 2018/5/10.
 */

public class XdoorBean {
    /**
     * id : 1
     * name : 1
     * key : 442c05e69cc5
     * ip : 1
     * mac : 44:2c:05:e6:9c:c5
     * type : 1
     * danyuan_id : 1
     * loudong_id : 1
     * xiangmu_id : 1
     * gongsi_id : 1
     * lixian_mima : 123456
     * version : null
     */

    private int id;
    private String name;
    private String key;
    private String ip;
    private String mac;
    private String type;
    private int danyuan_id;
    private int loudong_id;
    private int xiangmu_id;
    private int gongsi_id;
    private String lixian_mima;
    private Object version;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDanyuan_id() {
        return danyuan_id;
    }

    public void setDanyuan_id(int danyuan_id) {
        this.danyuan_id = danyuan_id;
    }

    public int getLoudong_id() {
        return loudong_id;
    }

    public void setLoudong_id(int loudong_id) {
        this.loudong_id = loudong_id;
    }

    public int getXiangmu_id() {
        return xiangmu_id;
    }

    public void setXiangmu_id(int xiangmu_id) {
        this.xiangmu_id = xiangmu_id;
    }

    public int getGongsi_id() {
        return gongsi_id;
    }

    public void setGongsi_id(int gongsi_id) {
        this.gongsi_id = gongsi_id;
    }

    public String getLixian_mima() {
        return lixian_mima;
    }

    public void setLixian_mima(String lixian_mima) {
        this.lixian_mima = lixian_mima;
    }

    public Object getVersion() {
        return version;
    }

    public void setVersion(Object version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "XdoorBean{" + "id=" + id + ", name='" + name + '\'' + ", key='" + key + '\'' + "," +
                " ip='" + ip + '\'' + ", mac='" + mac + '\'' + ", type='" + type + '\'' + ", " +
                "danyuan_id=" + danyuan_id + ", loudong_id=" + loudong_id + ", xiangmu_id=" +
                xiangmu_id + ", gongsi_id=" + gongsi_id + ", lixian_mima='" + lixian_mima + '\''
                + ", version=" + version + '}';
    }
}
