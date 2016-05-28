package com.seaice.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by seaice on 2016/5/24.
 */
public class ProInfo {

    private Drawable icon;
    private String proName;
    private int proSize;
    private Boolean isSysPro;

    @Override
    public String toString() {
        return "ProInfo{" +
                "icon=" + icon +
                ", proName='" + proName + '\'' +
                ", proSize=" + proSize +
                ", isSysPro=" + isSysPro +
                ", isChecked=" + isChecked +
                ", PackageName='" + PackageName + '\'' +
                '}';
    }

    public Boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(Boolean isChecked) {
        this.isChecked = isChecked;
    }

    private Boolean isChecked;

    public String getPackageName() {
        return PackageName;
    }

    public void setPackageName(String packageName) {
        PackageName = packageName;
    }

    private String PackageName;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public int getProSize() {
        return proSize;
    }

    public void setProSize(int proSize) {
        this.proSize = proSize;
    }

    public Boolean getIsSysPro() {
        return isSysPro;
    }

    public void setIsSysPro(Boolean isSysPro) {
        this.isSysPro = isSysPro;
    }

}
