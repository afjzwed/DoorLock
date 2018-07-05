package com.cxwl.hurry.doorlock.entity;


import com.cxwl.hurry.doorlock.db.AdTongJiBean;

import java.util.List;

/**
 * Created by William on 2018/7/2.
 */

public class NewTongJiBean {

    private List<AdTongJiBean> tongjis;
    private List<XdoorNotesBean> xdoorNotes;

    public List<AdTongJiBean> getTongjis() {
        return tongjis;
    }

    public void setTongjis(List<AdTongJiBean> tongjis) {
        this.tongjis = tongjis;
    }

    public List<XdoorNotesBean> getXdoorNotes() {
        return xdoorNotes;
    }

    public void setXdoorNotes(List<XdoorNotesBean> xdoorNotes) {
        this.xdoorNotes = xdoorNotes;
    }

    public static class XdoorNotesBean {
        /**
         * chiyouren : string
         * danyuan_id : 0
         * danyuan_ming : string
         * endTime : string
         * gongsi_id : 0
         * gongsi_ming : string
         * id : 0
         * kaimenShijian : string
         * kaimen_chenggong : 0
         * kaimen_fangshi : 0
         * kaimen_jietu : string
         * kaimen_mima : string
         * kaimen_ren : string
         * kaimen_sfz : string
         * kaimen_shouji : string
         * loudong_id : 0
         * loudong_ming : string
         * mac : string
         * startTime : string
         * xiangmu_id : 0
         * xiangmu_ming : string
         * yaoqing_fanghao : string
         * yaoqing_id : 0
         * yaoqing_ren : string
         * yaoqing_shouji : string
         */

        private String chiyouren;
        private int danyuan_id;
        private String danyuan_ming;
        private String endTime;
        private int gongsi_id;
        private String gongsi_ming;
        private int id;
        private String kaimenShijian;
        private int kaimen_chenggong;
        private int kaimen_fangshi;//开门方式
        private String kaimen_jietu;
        private String kaimen_mima;
        private String kaimen_ren;
        private String kaimen_sfz;
        private String kaimen_shouji;
        private int loudong_id;
        private String loudong_ming;
        private String mac;
        private String startTime;
        private int xiangmu_id;
        private String xiangmu_ming;
        private String yaoqing_fanghao;
        private int yaoqing_id;
        private String yaoqing_ren;
        private String yaoqing_shouji;

        public String getChiyouren() {
            return chiyouren;
        }

        public void setChiyouren(String chiyouren) {
            this.chiyouren = chiyouren;
        }

        public int getDanyuan_id() {
            return danyuan_id;
        }

        public void setDanyuan_id(int danyuan_id) {
            this.danyuan_id = danyuan_id;
        }

        public String getDanyuan_ming() {
            return danyuan_ming;
        }

        public void setDanyuan_ming(String danyuan_ming) {
            this.danyuan_ming = danyuan_ming;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public int getGongsi_id() {
            return gongsi_id;
        }

        public void setGongsi_id(int gongsi_id) {
            this.gongsi_id = gongsi_id;
        }

        public String getGongsi_ming() {
            return gongsi_ming;
        }

        public void setGongsi_ming(String gongsi_ming) {
            this.gongsi_ming = gongsi_ming;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getKaimenShijian() {
            return kaimenShijian;
        }

        public void setKaimenShijian(String kaimenShijian) {
            this.kaimenShijian = kaimenShijian;
        }

        public int getKaimen_chenggong() {
            return kaimen_chenggong;
        }

        public void setKaimen_chenggong(int kaimen_chenggong) {
            this.kaimen_chenggong = kaimen_chenggong;
        }

        public int getKaimen_fangshi() {
            return kaimen_fangshi;
        }

        public void setKaimen_fangshi(int kaimen_fangshi) {
            this.kaimen_fangshi = kaimen_fangshi;
        }

        public String getKaimen_jietu() {
            return kaimen_jietu;
        }

        public void setKaimen_jietu(String kaimen_jietu) {
            this.kaimen_jietu = kaimen_jietu;
        }

        public String getKaimen_mima() {
            return kaimen_mima;
        }

        public void setKaimen_mima(String kaimen_mima) {
            this.kaimen_mima = kaimen_mima;
        }

        public String getKaimen_ren() {
            return kaimen_ren;
        }

        public void setKaimen_ren(String kaimen_ren) {
            this.kaimen_ren = kaimen_ren;
        }

        public String getKaimen_sfz() {
            return kaimen_sfz;
        }

        public void setKaimen_sfz(String kaimen_sfz) {
            this.kaimen_sfz = kaimen_sfz;
        }

        public String getKaimen_shouji() {
            return kaimen_shouji;
        }

        public void setKaimen_shouji(String kaimen_shouji) {
            this.kaimen_shouji = kaimen_shouji;
        }

        public int getLoudong_id() {
            return loudong_id;
        }

        public void setLoudong_id(int loudong_id) {
            this.loudong_id = loudong_id;
        }

        public String getLoudong_ming() {
            return loudong_ming;
        }

        public void setLoudong_ming(String loudong_ming) {
            this.loudong_ming = loudong_ming;
        }

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public int getXiangmu_id() {
            return xiangmu_id;
        }

        public void setXiangmu_id(int xiangmu_id) {
            this.xiangmu_id = xiangmu_id;
        }

        public String getXiangmu_ming() {
            return xiangmu_ming;
        }

        public void setXiangmu_ming(String xiangmu_ming) {
            this.xiangmu_ming = xiangmu_ming;
        }

        public String getYaoqing_fanghao() {
            return yaoqing_fanghao;
        }

        public void setYaoqing_fanghao(String yaoqing_fanghao) {
            this.yaoqing_fanghao = yaoqing_fanghao;
        }

        public int getYaoqing_id() {
            return yaoqing_id;
        }

        public void setYaoqing_id(int yaoqing_id) {
            this.yaoqing_id = yaoqing_id;
        }

        public String getYaoqing_ren() {
            return yaoqing_ren;
        }

        public void setYaoqing_ren(String yaoqing_ren) {
            this.yaoqing_ren = yaoqing_ren;
        }

        public String getYaoqing_shouji() {
            return yaoqing_shouji;
        }

        public void setYaoqing_shouji(String yaoqing_shouji) {
            this.yaoqing_shouji = yaoqing_shouji;
        }

        @Override
        public String toString() {
            return "XdoorNotesBean{" +
                    "chiyouren='" + chiyouren + '\'' +
                    ", danyuan_id=" + danyuan_id +
                    ", danyuan_ming='" + danyuan_ming + '\'' +
                    ", endTime='" + endTime + '\'' +
                    ", gongsi_id=" + gongsi_id +
                    ", gongsi_ming='" + gongsi_ming + '\'' +
                    ", id=" + id +
                    ", kaimenShijian='" + kaimenShijian + '\'' +
                    ", kaimen_chenggong=" + kaimen_chenggong +
                    ", kaimen_fangshi=" + kaimen_fangshi +
                    ", kaimen_jietu='" + kaimen_jietu + '\'' +
                    ", kaimen_mima='" + kaimen_mima + '\'' +
                    ", kaimen_ren='" + kaimen_ren + '\'' +
                    ", kaimen_sfz='" + kaimen_sfz + '\'' +
                    ", kaimen_shouji='" + kaimen_shouji + '\'' +
                    ", loudong_id=" + loudong_id +
                    ", loudong_ming='" + loudong_ming + '\'' +
                    ", mac='" + mac + '\'' +
                    ", startTime='" + startTime + '\'' +
                    ", xiangmu_id=" + xiangmu_id +
                    ", xiangmu_ming='" + xiangmu_ming + '\'' +
                    ", yaoqing_fanghao='" + yaoqing_fanghao + '\'' +
                    ", yaoqing_id=" + yaoqing_id +
                    ", yaoqing_ren='" + yaoqing_ren + '\'' +
                    ", yaoqing_shouji='" + yaoqing_shouji + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "NewTongJiBean{" +
                "tongjis=" + tongjis +
                ", xdoorNotes=" + xdoorNotes +
                '}';
    }
}
