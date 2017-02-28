package com.example.mobilesafe;

/**
 * Created by sing on 13-12-23.
 */
public class UpdateInfo {
    //服务器端版本号
    private String version;

    //服务器端升级提示
    private String desc;

    //服务器端apk下载地址
    private String apkurl;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getApkurl() {
        return apkurl;
    }

    public void setApkurl(String apkurl) {
        this.apkurl = apkurl;
    }

}
