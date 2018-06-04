package com.cxwl.hurry.doorlock.Bean;

/**
 * Created by William on 2018/6/3.
 */

public class NewDoorBean {
    /**
     * id : 1
     * danyuan_id : null
     * gongsi_id : 23
     * key : 442c05e69cc5
     * loudong_id : null
     * mac : 44:2c:05:e6:9c:c5
     * name : 十三区大门门禁
     * type : 0
     * xiangmu_id : 443
     * shanchu : 1
     * sheng_id : null
     * shi_id : null
     * xian_id : null
     * jiedao_id : null
     * shequ : null
     * lixian_mima : null
     * version : null
     * danyuan_name : null
     * loudong_name : null
     * xiangmu_name : 13区
     * gongsi_name : null
     * tempPassword : null
     * xinhao : 2
     * online : false
     */

    private int id;//macID
    private String danyuan_id;//单元ID
    private int gongsi_id;//公司ID
    private String key;
    private String loudong_id;//楼栋ID
    private String mac;
    private String name;
    private String type;//0 为大门 1 为 单元门
    private int xiangmu_id;//项目ID
    private int shanchu;
    private Object sheng_id;
    private Object shi_id;
    private Object xian_id;
    private Object jiedao_id;
    private Object shequ;
    private Object lixian_mima;
    private Object version;
    private Object danyuan_name;
    private Object loudong_name;
    private String xiangmu_name;//项目名
    private Object gongsi_name;
    private Object tempPassword;
    private int xinhao;//型号
    private boolean online;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDanyuan_id() {
        return danyuan_id;
    }

    public void setDanyuan_id(String danyuan_id) {
        this.danyuan_id = danyuan_id;
    }

    public int getGongsi_id() {
        return gongsi_id;
    }

    public void setGongsi_id(int gongsi_id) {
        this.gongsi_id = gongsi_id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLoudong_id() {
        return loudong_id;
    }

    public void setLoudong_id(String loudong_id) {
        this.loudong_id = loudong_id;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getXiangmu_id() {
        return xiangmu_id;
    }

    public void setXiangmu_id(int xiangmu_id) {
        this.xiangmu_id = xiangmu_id;
    }

    public int getShanchu() {
        return shanchu;
    }

    public void setShanchu(int shanchu) {
        this.shanchu = shanchu;
    }

    public Object getSheng_id() {
        return sheng_id;
    }

    public void setSheng_id(Object sheng_id) {
        this.sheng_id = sheng_id;
    }

    public Object getShi_id() {
        return shi_id;
    }

    public void setShi_id(Object shi_id) {
        this.shi_id = shi_id;
    }

    public Object getXian_id() {
        return xian_id;
    }

    public void setXian_id(Object xian_id) {
        this.xian_id = xian_id;
    }

    public Object getJiedao_id() {
        return jiedao_id;
    }

    public void setJiedao_id(Object jiedao_id) {
        this.jiedao_id = jiedao_id;
    }

    public Object getShequ() {
        return shequ;
    }

    public void setShequ(Object shequ) {
        this.shequ = shequ;
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

    public Object getDanyuan_name() {
        return danyuan_name;
    }

    public void setDanyuan_name(Object danyuan_name) {
        this.danyuan_name = danyuan_name;
    }

    public Object getLoudong_name() {
        return loudong_name;
    }

    public void setLoudong_name(Object loudong_name) {
        this.loudong_name = loudong_name;
    }

    public String getXiangmu_name() {
        return xiangmu_name;
    }

    public void setXiangmu_name(String xiangmu_name) {
        this.xiangmu_name = xiangmu_name;
    }

    public Object getGongsi_name() {
        return gongsi_name;
    }

    public void setGongsi_name(Object gongsi_name) {
        this.gongsi_name = gongsi_name;
    }

    public Object getTempPassword() {
        return tempPassword;
    }

    public void setTempPassword(Object tempPassword) {
        this.tempPassword = tempPassword;
    }

    public int getXinhao() {
        return xinhao;
    }

    public void setXinhao(int xinhao) {
        this.xinhao = xinhao;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    @Override
    public String toString() {
        return "NewDoorBean{" +
                "id=" + id +
                ", danyuan_id='" + danyuan_id + '\'' +
                ", gongsi_id=" + gongsi_id +
                ", key='" + key + '\'' +
                ", loudong_id='" + loudong_id + '\'' +
                ", mac='" + mac + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", xiangmu_id=" + xiangmu_id +
                ", shanchu=" + shanchu +
                ", sheng_id=" + sheng_id +
                ", shi_id=" + shi_id +
                ", xian_id=" + xian_id +
                ", jiedao_id=" + jiedao_id +
                ", shequ=" + shequ +
                ", lixian_mima=" + lixian_mima +
                ", version=" + version +
                ", danyuan_name=" + danyuan_name +
                ", loudong_name=" + loudong_name +
                ", xiangmu_name='" + xiangmu_name + '\'' +
                ", gongsi_name=" + gongsi_name +
                ", tempPassword=" + tempPassword +
                ", xinhao=" + xinhao +
                ", online=" + online +
                '}';
    }
}
