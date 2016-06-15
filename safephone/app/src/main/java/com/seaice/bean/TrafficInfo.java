package com.seaice.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by seaice on 2016/6/8.
 */
public class TrafficInfo {
    private String appName;
    private Drawable icon;
    private int uid;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "TrafficInfo{" +
                "appName='" + appName + '\'' +
                ", icon=" + icon +
                ", uid=" + uid +
                '}';
    }
}
