package com.cxwl.hurry.doorlock.entity;

/**
 * Created by William on 2018/5/10.
 */

public class DoorBean {

    /**
     * id : 1
     * ka : 2018-05-09 17:20:55.0
     * ka_gx : 2018-05-09 17:20:50.0
     * lian : 1
     * lian_gx : 2018-05-09 17:20:42.0
     * guanggao : 1
     * guanggao_gx : 2018-05-09 17:56:49.0
     * tonggao : 1
     * tonggao_gx : 2018-05-09 17:20:46.0
     * mac : 44:2c:05:e6:9c:c5
     * xiangmu_id : 1
     * xdoor : {"id":1,"name":"1","key":"442c05e69cc5","ip":"1","mac":"44:2c:05:e6:9c:c5",
     * "type":"1","danyuan_id":1,"loudong_id":1,"xiangmu_id":1,"gongsi_id":1,
     * "lixian_mima":"123456","version":null}
     * xintiao_time : 300
     * fuwuqi_time : 1525935607487
     * lixian_mima : 123456
     * version : null
     */

    private int id;
    private String ka;
    private String ka_gx;
    private String lian;
    private String lian_gx;
    private String guanggao;
    private String guanggao_gx;
    private String tonggao;
    private String tonggao_gx;
    private String mac;
    private int xiangmu_id;
    private XdoorBean xdoor;
    private int xintiao_time;
    private String fuwuqi_time;
    private String lixian_mima;
    private Object version;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKa() {
        return ka;
    }

    public void setKa(String ka) {
        this.ka = ka;
    }

    public String getKa_gx() {
        return ka_gx;
    }

    public void setKa_gx(String ka_gx) {
        this.ka_gx = ka_gx;
    }

    public String getLian() {
        return lian;
    }

    public void setLian(String lian) {
        this.lian = lian;
    }

    public String getLian_gx() {
        return lian_gx;
    }

    public void setLian_gx(String lian_gx) {
        this.lian_gx = lian_gx;
    }

    public String getGuanggao() {
        return guanggao;
    }

    public void setGuanggao(String guanggao) {
        this.guanggao = guanggao;
    }

    public String getGuanggao_gx() {
        return guanggao_gx;
    }

    public void setGuanggao_gx(String guanggao_gx) {
        this.guanggao_gx = guanggao_gx;
    }

    public String getTonggao() {
        return tonggao;
    }

    public void setTonggao(String tonggao) {
        this.tonggao = tonggao;
    }

    public String getTonggao_gx() {
        return tonggao_gx;
    }

    public void setTonggao_gx(String tonggao_gx) {
        this.tonggao_gx = tonggao_gx;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getXiangmu_id() {
        return xiangmu_id;
    }

    public void setXiangmu_id(int xiangmu_id) {
        this.xiangmu_id = xiangmu_id;
    }

    public XdoorBean getXdoor() {
        return xdoor;
    }

    public void setXdoor(XdoorBean xdoor) {
        this.xdoor = xdoor;
    }

    public int getXintiao_time() {
        return xintiao_time;
    }

    public void setXintiao_time(int xintiao_time) {
        this.xintiao_time = xintiao_time;
    }

    public String getFuwuqi_time() {
        return fuwuqi_time;
    }

    public void setFuwuqi_time(String fuwuqi_time) {
        this.fuwuqi_time = fuwuqi_time;
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
        return "DoorBean{" + "id=" + id + ", ka='" + ka + '\'' + ", ka_gx='" + ka_gx + '\'' + ", " +
                "lian='" + lian + '\'' + ", lian_gx='" + lian_gx + '\'' + ", guanggao='" +
                guanggao + '\'' + ", guanggao_gx='" + guanggao_gx + '\'' + ", tonggao='" +
                tonggao + '\'' + ", tonggao_gx='" + tonggao_gx + '\'' + ", mac='" + mac + '\'' +
                ", xiangmu_id=" + xiangmu_id + ", xdoor=" + xdoor + ", xintiao_time=" +
                xintiao_time + ", fuwuqi_time='" + fuwuqi_time + '\'' + ", lixian_mima='" +
                lixian_mima + '\'' + ", version=" + version + '}';
    }
}
