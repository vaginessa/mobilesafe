package com.example.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by sing on 14-1-13.
 * desc:判断某个服务是否处于运行状态
 */
public class ServiceStatusUtil {

    public static boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> infos = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo info : infos) {
            if (info.service.getClassName().equals(serviceName)) {
                return true;
            }
        }

        return false;
    }
}
