package com.seaice.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by seaice on 2016/5/4.
 */
public class SmsItem{
    private String date;
    private String body;
    private String type;
    private String address;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "SmsItem{" +
                "date='" + date + '\'' +
                ", body='" + body + '\'' +
                ", type='" + type + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}

