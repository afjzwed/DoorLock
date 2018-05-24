package com.cxwl.hurry.doorlock.entity;

/**
 * Created by William on 2018/5/10.
 */

public class XdoorBean {
    /**
     * id : 1
     * name : 大门
     * key : 442c05e69cc5
     * ip : 123456
     * mac : 44:2c:05:e6:9c:c5
     * type : 0
     * danyuan_id : 1
     * loudong_id : 1
     * xiangmu_id : 346
     * gongsi_id : 1
     * lixian_mima : 123456
     * version : null
     */
//"danyuan_name":null,"loudong_name":null,"xiangmu_name":null
    private int id;
    private String name;
    private String key;
    private String ip;
    private String mac;
    private String type;//0 为大门 1 为 单元门
    private String danyuan_id;
    private String loudong_id;
    private int xiangmu_id;
    private String gongsi_id;
    private String lixian_mima;
    private Object version;
    private String danyuan_name;
    private String loudong_name;
    private String xiangmu_name;

    public String getDanyuan_name() {
        return danyuan_name;
    }

    public void setDanyuan_name(String danyuan_name) {
        this.danyuan_name = danyuan_name;
    }

    public String getLoudong_name() {
        return loudong_name;
    }

    public void setLoudong_name(String loudong_name) {
        this.loudong_name = loudong_name;
    }

    public String getXiangmu_name() {
        return xiangmu_name;
    }

    public void setXiangmu_name(String xiangmu_name) {
        this.xiangmu_name = xiangmu_name;
    }

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

    public String getDanyuan_id() {
        return danyuan_id;
    }

    public void setDanyuan_id(String danyuan_id) {
        this.danyuan_id = danyuan_id;
    }

    public String getLoudong_id() {
        return loudong_id;
    }

    public void setLoudong_id(String loudong_id) {
        this.loudong_id = loudong_id;
    }

    public int getXiangmu_id() {
        return xiangmu_id;
    }

    public void setXiangmu_id(int xiangmu_id) {
        this.xiangmu_id = xiangmu_id;
    }

    public String getGongsi_id() {
        return gongsi_id;
    }

    public void setGongsi_id(String gongsi_id) {
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
                "danyuan_id='" + danyuan_id + '\'' + ", loudong_id='" + loudong_id + '\'' + ", " +
                "xiangmu_id=" + xiangmu_id + ", gongsi_id='" + gongsi_id + '\'' + ", " +
                "lixian_mima='" + lixian_mima + '\'' + ", version=" + version + '}';
    }
}
