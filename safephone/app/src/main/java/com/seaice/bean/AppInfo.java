package com.seaice.bean;

import android.graphics.drawable.Drawable;

import com.seaice.safephone.R;

/**
 * Created by seaice on 2016/5/4.
 */
public class AppInfo {
    /**
     * 程序的图标
     */
    private Drawable icon;
    /**
     * 程序的包名
     */
    private String apkName;

    @Override
    public String toString() {
        return "AppInfo{" +
                "icon=" + icon +
                ", apkName='" + apkName + '\'' +
                ", apkSize=" + apkSize +
                ", userApp=" + userApp +
                ", isRom=" + isRom +
                ", apkPackageName='" + apkPackageName + '\'' +
                '}';
    }

    /**
     * 程序的大小
     */
    private long apkSize;
    /**
     * 是否是用户app
     */
    private boolean userApp;
    /**

     * 程序的位置
     */
    private boolean isRom;
    /**
     * 程序的包名
     */
    private String apkPackageName;
    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    public boolean isUserApp() {
        return userApp;
    }

    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }

    public boolean isRom() {
        return isRom;
    }

    public void setIsRom(boolean isRom) {
        this.isRom = isRom;
    }

    public String getApkPackageName() {
        return apkPackageName;
    }

    public void setApkPackageName(String apkPackageName) {
        this.apkPackageName = apkPackageName;
    }

    public long getApkSize() {
        return apkSize;
    }

    public void setApkSize(long apkSize) {
        this.apkSize = apkSize;
    }
}
