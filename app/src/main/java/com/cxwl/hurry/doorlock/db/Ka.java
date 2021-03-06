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
    @Generated(hash = 472692778)
    public Ka(Long id, String ka_id) {
        this.id = id;
        this.ka_id = ka_id;
    }
    @Generated(hash = 1202595018)
    public Ka() {
    }
}
