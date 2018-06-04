package com.cxwl.hurry.doorlock.Bean;

/**
 * Created by William on 2018/6/3.
 */

public class BanbenBean {
    /**
     * id : 1
     * ka : 20180601103302438
     * ka_gx : 2018-06-01 10:33:02.0
     * lian : null
     * lianGx : null
     * mac_id : 1
     * tonggao : 20180601174947137
     * tonggaoGx : 2018-06-01 17:49:47.0
     * guanggao : 20180602212746092
     * guanggaoGx : 2018-06-02 21:27:46.0
     * tupian : 20180603002223940
     * tupianGx : 2018-06-03 00:22:23.0
     * xiangmu_id : null
     * xdoor : null
     * xintiao_time : null
     * beforeTime : null
     * fuwuqi_time : null
     * lixian_mima : null
     * version : null
     * token : null
     */


    private int id;
    private String ka;//卡
    private String ka_gx;
    private String lian;//脸
    private String lianGx;
    private int mac_id;
    private String tonggao;//通告
    private String tonggaoGx;
    private String guanggao;//广告
    private String guanggaoGx;
    private String tupian;//图片
    private String tupianGx;
    private Object xiangmu_id;
    private Object xdoor;
    private Object xintiao_time;
    private Object beforeTime;
    private Object fuwuqi_time;
    private Object lixian_mima;
    private Object version;
    private Object token;

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

    public String getLianGx() {
        return lianGx;
    }

    public void setLianGx(String lianGx) {
        this.lianGx = lianGx;
    }

    public int getMac_id() {
        return mac_id;
    }

    public void setMac_id(int mac_id) {
        this.mac_id = mac_id;
    }

    public String getTonggao() {
        return tonggao;
    }

    public void setTonggao(String tonggao) {
        this.tonggao = tonggao;
    }

    public String getTonggaoGx() {
        return tonggaoGx;
    }

    public void setTonggaoGx(String tonggaoGx) {
        this.tonggaoGx = tonggaoGx;
    }

    public String getGuanggao() {
        return guanggao;
    }

    public void setGuanggao(String guanggao) {
        this.guanggao = guanggao;
    }

    public String getGuanggaoGx() {
        return guanggaoGx;
    }

    public void setGuanggaoGx(String guanggaoGx) {
        this.guanggaoGx = guanggaoGx;
    }

    public String getTupian() {
        return tupian;
    }

    public void setTupian(String tupian) {
        this.tupian = tupian;
    }

    public String getTupianGx() {
        return tupianGx;
    }

    public void setTupianGx(String tupianGx) {
        this.tupianGx = tupianGx;
    }

    public Object getXiangmu_id() {
        return xiangmu_id;
    }

    public void setXiangmu_id(Object xiangmu_id) {
        this.xiangmu_id = xiangmu_id;
    }

    public Object getXdoor() {
        return xdoor;
    }

    public void setXdoor(Object xdoor) {
        this.xdoor = xdoor;
    }

    public Object getXintiao_time() {
        return xintiao_time;
    }

    public void setXintiao_time(Object xintiao_time) {
        this.xintiao_time = xintiao_time;
    }

    public Object getBeforeTime() {
        return beforeTime;
    }

    public void setBeforeTime(Object beforeTime) {
        this.beforeTime = beforeTime;
    }

    public Object getFuwuqi_time() {
        return fuwuqi_time;
    }

    public void setFuwuqi_time(Object fuwuqi_time) {
        this.fuwuqi_time = fuwuqi_time;
    }

    public Object getLixian_mima() {
        return lixian_mima;
    }

    public void setLixian_mima(Object lixian_mima) {
        this.lixian_mima = lixian_mima;
    }

    public Object getVersion() {
        return version;
    }

    public void setVersion(Object version) {
        this.version = version;
    }

    public Object getToken() {
        return token;
    }

    public void setToken(Object token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "BanbenBean{" +
                "id=" + id +
                ", ka='" + ka + '\'' +
                ", ka_gx='" + ka_gx + '\'' +
                ", lian='" + lian + '\'' +
                ", lianGx='" + lianGx + '\'' +
                ", mac_id=" + mac_id +
                ", tonggao='" + tonggao + '\'' +
                ", tonggaoGx='" + tonggaoGx + '\'' +
                ", guanggao='" + guanggao + '\'' +
                ", guanggaoGx='" + guanggaoGx + '\'' +
                ", tupian='" + tupian + '\'' +
                ", tupianGx='" + tupianGx + '\'' +
                ", xiangmu_id=" + xiangmu_id +
                ", xdoor=" + xdoor +
                ", xintiao_time=" + xintiao_time +
                ", beforeTime=" + beforeTime +
                ", fuwuqi_time=" + fuwuqi_time +
                ", lixian_mima=" + lixian_mima +
                ", version=" + version +
                ", token=" + token +
                '}';
    }
}
