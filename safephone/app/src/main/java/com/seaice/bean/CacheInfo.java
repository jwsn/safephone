package com.seaice.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by seaice on 2016/6/6.
 */
public class CacheInfo {
    private String appName;
    private String pkgName;
    private Drawable icon;

    //应用大小
    private String codeSize;
    //数据大小
    private String dataSize;
    //缓存大小
    private String cacheSize;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getCodeSize() {
        return codeSize;
    }

    public void setCodeSize(String codeSize) {
        this.codeSize = codeSize;
    }

    public String getDataSize() {
        return dataSize;
    }

    public void setDataSize(String dataSize) {
        this.dataSize = dataSize;
    }

    public String getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(String cacheSize) {
        this.cacheSize = cacheSize;
    }

    @Override
    public String toString() {
        return "CacheInfo{" +
                "appName='" + appName + '\'' +
                ", pkgName='" + pkgName + '\'' +
                ", icon=" + icon +
                ", codeSize='" + codeSize + '\'' +
                ", dataSize='" + dataSize + '\'' +
                ", cacheSize='" + cacheSize + '\'' +
                '}';
    }
}
