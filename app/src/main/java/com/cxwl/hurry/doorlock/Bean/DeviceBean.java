package com.cxwl.hurry.doorlock.Bean;

/**
 * Created by William on 2018/6/3.
 */

public class DeviceBean {
    /**
     * banben : {"id":1,"ka":"20180601103302438","ka_gx":"2018-06-01 10:33:02.0","lian":null,"lianGx":null,
     * "mac_id":1,"tonggao":"20180601174947137","tonggaoGx":"2018-06-01 17:49:47.0","guanggao":"20180602212746092",
     * "guanggaoGx":"2018-06-02 21:27:46.0","tupian":"20180603002223940","tupianGx":"2018-06-03 00:22:23.0",
     * "xiangmu_id":null,"xdoor":null,"xintiao_time":null,"beforeTime":null,"fuwuqi_time":null,"lixian_mima":null,
     * "version":null,"token":null}
     * door : {"id":1,"danyuan_id":null,"gongsi_id":23,"key":"442c05e69cc5","loudong_id":null,
     * "mac":"44:2c:05:e6:9c:c5","name":"十三区大门门禁","type":"0","xiangmu_id":443,"shanchu":1,"sheng_id":null,
     * "shi_id":null,"xian_id":null,"jiedao_id":null,"shequ":null,"lixian_mima":null,"version":null,
     * "danyuan_name":null,"loudong_name":null,"xiangmu_name":"13区","gongsi_name":null,"tempPassword":null,
     * "xinhao":2,"online":false}
     * token : 64e73426-0e62-494f-b6d8-8029efae4145
     * version : 101
     * fuwuqi_shijian : 2018-06-03 16:47:48
     * lixian_mima : 841014
     * xintiao_shijian : null
     */

    private BanbenBean banben;
    private NewDoorBean door;
    private String token;
    private String version;//APP版本
    private String fuwuqi_shijian;//服务器时间
    private String lixian_mima;//离线密码
    private int xintiao_shijian;//心跳间隔

    public BanbenBean getBanben() {
        return banben;
    }

    public void setBanben(BanbenBean banben) {
        this.banben = banben;
    }

    public NewDoorBean getDoor() {
        return door;
    }

    public void setDoor(NewDoorBean door) {
        this.door = door;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFuwuqi_shijian() {
        return fuwuqi_shijian;
    }

    public void setFuwuqi_shijian(String fuwuqi_shijian) {
        this.fuwuqi_shijian = fuwuqi_shijian;
    }

    public String getLixian_mima() {
        return lixian_mima;
    }

    public void setLixian_mima(String lixian_mima) {
        this.lixian_mima = lixian_mima;
    }

    public int getXintiao_shijian() {
        return xintiao_shijian;
    }

    public void setXintiao_shijian(int xintiao_shijian) {
        this.xintiao_shijian = xintiao_shijian;
    }

    @Override
    public String toString() {
        return "DeviceBean{" +
                "banben=" + banben +
                ", door=" + door +
                ", token='" + token + '\'' +
                ", version='" + version + '\'' +
                ", fuwuqi_shijian='" + fuwuqi_shijian + '\'' +
                ", lixian_mima='" + lixian_mima + '\'' +
                ", xintiao_shijian=" + xintiao_shijian +
                '}';
    }
}
