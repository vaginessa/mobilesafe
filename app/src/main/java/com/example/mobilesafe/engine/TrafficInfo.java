package com.example.mobilesafe.engine;

import android.graphics.drawable.Drawable;

/**
 * Created by sing on 14-1-26.
 * desc:
 */
public class TrafficInfo {
    private static final String TAG = "TrafficInfo";

    //应用的包名
    private String packname;
    //应用的名称
    private String appname;
    //上传的数据
    private long tx;
    //下载的数据
    private long rx;
    //应用图标
    private Drawable icon;

    public String getPackname() {
        return packname;
    }

    public void setPackname(String packname) {
        this.packname = packname;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public long getTx() {
        return tx;
    }

    public void setTx(long tx) {
        this.tx = tx;
    }

    public long getRx() {
        return rx;
    }

    public void setRx(long rx) {
        this.rx = rx;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}
