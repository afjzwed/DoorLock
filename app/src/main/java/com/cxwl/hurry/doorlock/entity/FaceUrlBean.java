package com.cxwl.hurry.doorlock.entity;

/**
 * Created by William on 2018/5/17.
 */

public class FaceUrlBean {
    /**
     * xdoorId : 2
     * chiyouren : 王祖贤
     * xdoorLianId : 2
     * yezhuId : 0
     * gongsiId : 23
     * xiangmuId : 443
     * yezhuPhone : 15347345968
     * shanchu : 1
     * bangdingTime : 2018-06-22 11:57:59
     * xiangmuName : null
     * gongsiName : null
     * lianName : 王祖贤
     * lianUrl : uploadan/1529637685929.bin
     */

    private int xdoorId;
    private String chiyouren;//持有人
    private int xdoorLianId;
    private int yezhuId;//业主ID
    private int gongsiId;
    private int xiangmuId;
    private String yezhuPhone;//电话
    private int shanchu;//1新增-1删除2修改
    private String bangdingTime;
    private String xiangmuName;
    private String gongsiName;
    private String lianName;
    private String lianUrl;//脸地址

    private String fileName;//文件名
    private String path;//本地路径

    public int getXdoorId() {
        return xdoorId;
    }

    public void setXdoorId(int xdoorId) {
        this.xdoorId = xdoorId;
    }

    public String getChiyouren() {
        return chiyouren;
    }

    public void setChiyouren(String chiyouren) {
        this.chiyouren = chiyouren;
    }

    public int getXdoorLianId() {
        return xdoorLianId;
    }

    public void setXdoorLianId(int xdoorLianId) {
        this.xdoorLianId = xdoorLianId;
    }

    public int getYezhuId() {
        return yezhuId;
    }

    public void setYezhuId(int yezhuId) {
        this.yezhuId = yezhuId;
    }

    public int getGongsiId() {
        return gongsiId;
    }

    public void setGongsiId(int gongsiId) {
        this.gongsiId = gongsiId;
    }

    public int getXiangmuId() {
        return xiangmuId;
    }

    public void setXiangmuId(int xiangmuId) {
        this.xiangmuId = xiangmuId;
    }

    public String getYezhuPhone() {
        return yezhuPhone;
    }

    public void setYezhuPhone(String yezhuPhone) {
        this.yezhuPhone = yezhuPhone;
    }

    public int getShanchu() {
        return shanchu;
    }

    public void setShanchu(int shanchu) {
        this.shanchu = shanchu;
    }

    public String getBangdingTime() {
        return bangdingTime;
    }

    public void setBangdingTime(String bangdingTime) {
        this.bangdingTime = bangdingTime;
    }

    public String getXiangmuName() {
        return xiangmuName;
    }

    public void setXiangmuName(String xiangmuName) {
        this.xiangmuName = xiangmuName;
    }

    public String getGongsiName() {
        return gongsiName;
    }

    public void setGongsiName(String gongsiName) {
        this.gongsiName = gongsiName;
    }

    public String getLianName() {
        return lianName;
    }

    public void setLianName(String lianName) {
        this.lianName = lianName;
    }

    public String getLianUrl() {
        return lianUrl;
    }

    public void setLianUrl(String lianUrl) {
        this.lianUrl = lianUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "FaceUrlBean{" +
                "xdoorId=" + xdoorId +
                ", chiyouren='" + chiyouren + '\'' +
                ", xdoorLianId=" + xdoorLianId +
                ", yezhuId=" + yezhuId +
                ", gongsiId=" + gongsiId +
                ", xiangmuId=" + xiangmuId +
                ", yezhuPhone='" + yezhuPhone + '\'' +
                ", shanchu=" + shanchu +
                ", bangdingTime='" + bangdingTime + '\'' +
                ", xiangmuName='" + xiangmuName + '\'' +
                ", gongsiName='" + gongsiName + '\'' +
                ", lianName='" + lianName + '\'' +
                ", lianUrl='" + lianUrl + '\'' +
                ", fileName='" + fileName + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
