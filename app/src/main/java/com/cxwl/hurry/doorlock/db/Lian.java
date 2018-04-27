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
public class Lian {
    @Id
    private Long id;
    @Property(nameInDb = "lian_id")
    private String lian_id;
    @Property(nameInDb = "lian_info")
    private String lian_info;
    public String getLian_info() {
        return this.lian_info;
    }
    public void setLian_info(String lian_info) {
        this.lian_info = lian_info;
    }
    public String getLian_id() {
        return this.lian_id;
    }
    public void setLian_id(String lian_id) {
        this.lian_id = lian_id;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @Generated(hash = 1582017075)
    public Lian(Long id, String lian_id, String lian_info) {
        this.id = id;
        this.lian_id = lian_id;
        this.lian_info = lian_info;
    }
    @Generated(hash = 1412347224)
    public Lian() {
    }
}
