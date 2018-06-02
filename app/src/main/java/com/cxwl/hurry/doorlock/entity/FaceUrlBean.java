package com.cxwl.hurry.doorlock.entity;

/**
 * Created by William on 2018/5/17.
 */

public class FaceUrlBean {
    /**
     * chiyouren : 车文兵
     * dianhua : 18774062967
     * zhaopian : https://ss1.bdstatic.com/kvoZeXSm1A5BphGlnYG/skin/400.jpg
     * guoqi_shijian : 1525933059455
     * yezhu_id : 1
     */

    private String chiyouren;//持有人
    private String dianhua;//电话
    private String zhaopian;//照片地址
    private String guoqi_shijian;//过期时间
    private String yezhu_id;//业主ID

    public String getChiyouren() {
        return chiyouren;
    }

    public void setChiyouren(String chiyouren) {
        this.chiyouren = chiyouren;
    }

    public String getDianhua() {
        return dianhua;
    }

    public void setDianhua(String dianhua) {
        this.dianhua = dianhua;
    }

    public String getZhaopian() {
        return zhaopian;
    }

    public void setZhaopian(String zhaopian) {
        this.zhaopian = zhaopian;
    }

    public String getGuoqi_shijian() {
        return guoqi_shijian;
    }

    public void setGuoqi_shijian(String guoqi_shijian) {
        this.guoqi_shijian = guoqi_shijian;
    }

    public String getYezhu_id() {
        return yezhu_id;
    }

    public void setYezhu_id(String yezhu_id) {
        this.yezhu_id = yezhu_id;
    }

    @Override
    public String toString() {
        return "FaceUrlBean{" + "chiyouren='" + chiyouren + '\'' + ", dianhua='" + dianhua + '\''
                + ", zhaopian='" + zhaopian + '\'' + ", guoqi_shijian='" + guoqi_shijian + '\'' +
                ", yezhu_id='" + yezhu_id + '\'' + '}';
    }
}
