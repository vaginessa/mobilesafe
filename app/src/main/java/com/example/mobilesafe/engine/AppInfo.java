package com.example.mobilesafe.engine;

import android.graphics.drawable.Drawable;

/**
 * Created by sing on 14-1-15.
 * desc:
 */
public class AppInfo {

    private static final String TAG = "AppInfo";

    //包名
    private String packname;

    //应用程序版本号
    private String version;

    //应用程序名
    private String appname;

    //应用程序图标
    private Drawable appicon;

    //标识是否是用户层应用
    private boolean userpp;

    public String getPackname() {
        return packname;
    }

    public void setPackname(String packname) {
        this.packname = packname;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public Drawable getAppicon() {
        return appicon;
    }

    public void setAppicon(Drawable appicon) {
        this.appicon = appicon;
    }

    public boolean isUserpp() {
        return userpp;
    }

    public void setUserpp(boolean userpp) {
        this.userpp = userpp;
    }
}
