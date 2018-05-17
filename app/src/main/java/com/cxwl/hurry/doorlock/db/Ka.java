package com.cxwl.hurry.doorlock.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author xlei
 * @Date 2018/4/27.
 */
@Entity
public class Ka {
    @Id
    private Long id;
    @Property(nameInDb = "ka_id")
    private String ka_id;
    @Property(nameInDb = "yezhu_dianhua")
    private String yezhu_dianhua;
    @Property(nameInDb = "guoqi_time")
    private String guoqi_time;
    public String getGuoqi_time() {
        return this.guoqi_time;
    }
    public void setGuoqi_time(String guoqi_time) {
        this.guoqi_time = guoqi_time;
    }
    public String getYezhu_dianhua() {
        return this.yezhu_dianhua;
    }
    public void setYezhu_dianhua(String yezhu_dianhua) {
        this.yezhu_dianhua = yezhu_dianhua;
    }
    public String getKa_id() {
        return this.ka_id;
    }
    public void setKa_id(String ka_id) {
        this.ka_id = ka_id;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @Generated(hash = 468668514)
    public Ka(Long id, String ka_id, String yezhu_dianhua, String guoqi_time) {
        this.id = id;
        this.ka_id = ka_id;
        this.yezhu_dianhua = yezhu_dianhua;
        this.guoqi_time = guoqi_time;
    }
    @Generated(hash = 1202595018)
    public Ka() {
    }

    @Override
    public String toString() {
        return "Ka{" + "id=" + id + ", ka_id='" + ka_id + '\'' + ", yezhu_dianhua='" + yezhu_dianhua + '\'' + ", " +
                "guoqi_time='" + guoqi_time + '\'' + '}';
    }
}
