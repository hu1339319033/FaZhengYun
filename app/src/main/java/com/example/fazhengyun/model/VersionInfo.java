package com.example.fazhengyun.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by fscm-qt on 2017/12/15.
 */

public class VersionInfo extends BaseModel {
    private int id;
    @SerializedName("ver_name")
    private String verName;
    @SerializedName("ver_url")
    private String verUrl;
    private int count;
    @SerializedName("isNeed")
    private int isneed;
    @SerializedName("app_name")
    private String appName;
    @SerializedName("ver_code")
    private int verCode;
    @SerializedName("ver_desc")
    private String verDesc;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVerName() {
        return verName;
    }

    public void setVerName(String verName) {
        this.verName = verName;
    }

    public String getVerUrl() {
        return verUrl;
    }

    public void setVerUrl(String verUrl) {
        this.verUrl = verUrl;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getIsneed() {
        return isneed;
    }

    public void setIsneed(int isneed) {
        this.isneed = isneed;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getVerCode() {
        return verCode;
    }

    public void setVerCode(int verCode) {
        this.verCode = verCode;
    }

    public String getVerDesc() {
        return verDesc;
    }

    public void setVerDesc(String verDesc) {
        this.verDesc = verDesc;
    }
}
