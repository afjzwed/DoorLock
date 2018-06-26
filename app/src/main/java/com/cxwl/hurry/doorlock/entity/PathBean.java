package com.cxwl.hurry.doorlock.entity;

/**
 * Created by William on 2018/6/21.
 */

public class PathBean {
    private String path;
    private String name;

    public PathBean(String path, String name) {
        this.path = path;
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "PathBean{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
