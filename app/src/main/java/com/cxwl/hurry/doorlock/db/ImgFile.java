package com.cxwl.hurry.doorlock.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author xlei
 * @Date 2018/6/2.
 */
@Entity
public class ImgFile {
    @Id
    private Long id;
    @Property(nameInDb = "img_localurl")
    private String img_localurl;
    @Property(nameInDb = "img_uploadurl")
    private String img_uploadurl;
    public String getImg_uploadurl() {
        return this.img_uploadurl;
    }
    public void setImg_uploadurl(String img_uploadurl) {
        this.img_uploadurl = img_uploadurl;
    }
    public String getImg_localurl() {
        return this.img_localurl;
    }
    public void setImg_localurl(String img_localurl) {
        this.img_localurl = img_localurl;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @Generated(hash = 263411280)
    public ImgFile(Long id, String img_localurl, String img_uploadurl) {
        this.id = id;
        this.img_localurl = img_localurl;
        this.img_uploadurl = img_uploadurl;
    }
    @Generated(hash = 137030121)
    public ImgFile() {
    }
}
