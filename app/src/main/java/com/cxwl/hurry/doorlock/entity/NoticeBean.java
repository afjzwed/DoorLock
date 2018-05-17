package com.cxwl.hurry.doorlock.entity;

/**
 * Created by William on 2018/5/17.
 */

public class NoticeBean {

    /**
     * biaoti : 重要通知
     * neirong : 停水
     * kaishi_shijian : 2018-05-16 00:00:00
     * shixiao_shijian : 2018-05-17 00:00:00
     * celve : 循环
     * leixing : null
     */

    private String biaoti;
    private String neirong;
    private String kaishi_shijian;
    private String shixiao_shijian;
    private String celve;
    private String leixing;

    public String getBiaoti() {
        return biaoti;
    }

    public void setBiaoti(String biaoti) {
        this.biaoti = biaoti;
    }

    public String getNeirong() {
        return neirong;
    }

    public void setNeirong(String neirong) {
        this.neirong = neirong;
    }

    public String getKaishi_shijian() {
        return kaishi_shijian;
    }

    public void setKaishi_shijian(String kaishi_shijian) {
        this.kaishi_shijian = kaishi_shijian;
    }

    public String getShixiao_shijian() {
        return shixiao_shijian;
    }

    public void setShixiao_shijian(String shixiao_shijian) {
        this.shixiao_shijian = shixiao_shijian;
    }

    public String getCelve() {
        return celve;
    }

    public void setCelve(String celve) {
        this.celve = celve;
    }

    public String getLeixing() {
        return leixing;
    }

    public void setLeixing(String leixing) {
        this.leixing = leixing;
    }

    @Override
    public String toString() {
        return "NoticeBean{" + "biaoti='" + biaoti + '\'' + ", neirong='" + neirong + '\'' + ", " +
                "kaishi_shijian='" + kaishi_shijian + '\'' + ", shixiao_shijian='" +
                shixiao_shijian + '\'' + ", celve='" + celve + '\'' + ", leixing='" + leixing +
                '\'' + '}';
    }
}
