package com.example.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.TelephonyManager;

import java.io.File;
import java.security.MessageDigest;


/**
 * Created by sing on 13-12-24.
 * desc:
 */
public class util {
    /**
     * 安装apk
     *
     * @param activity
     * @param file
     */
    public static void installApk(Context context, File file) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 计算一个String的md5值
     *
     * @param s
     * @return
     */
    public static String md5String(String s) {
        String smd5 = "";

        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] result = digest.digest(s.getBytes());
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < result.length; i++) {
                int number = result[i] & 0xff;
                String str = Integer.toHexString(number);
                if (str.length() == 1) {
                    stringBuilder.append("0");
                }
                stringBuilder.append(str);
            }
            smd5 = stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return smd5;
    }

    /**
     * 获取sim卡，需要权限：android.permission.READ_PHONE_STATE
     *
     * @return
     */
    public static String getSimSerial(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSimSerialNumber();
    }
}
