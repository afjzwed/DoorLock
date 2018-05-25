package com.cxwl.hurry.doorlock.entity;

/**
 * @author xlei
 * @Date 2018/5/17.
 */

public class GuangGaoBean {

    /**
     * biaoti : 酸酸甜甜就是我
     * neirong : https://ss1.bdstatic.com/kvoZeXSm1A5BphGlnYG/skin/400.jpg
     * kaishi_shijian : 1525933059455
     * shixiao_shijian : 1525933059455
     * celve : 1
     * leixing : 2
     */
    private int id;
    private String biaoti;
    private String neirong;
    private String kaishi_shijian;
    private String shixiao_shijian;
    private String celve;
    private String leixing;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
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
        return "GuangGaoBean{" + "id=" + id + ", biaoti='" + biaoti + '\'' + ", neirong='" + neirong + '\'' + ", " +
                "kaishi_shijian='" + kaishi_shijian + '\'' + ", shixiao_shijian='" + shixiao_shijian + '\'' + ", " +
                "celve='" + celve + '\'' + ", leixing='" + leixing + '\'' + '}';
    }
}
