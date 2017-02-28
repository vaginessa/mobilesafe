package com.example.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sing on 14-1-15.
 * desc:
 */
public class AppInfoProvider {
    private static final String TAG = "AppInfoProvider";

    private PackageManager pm;

    public AppInfoProvider(Context context) {
        pm = context.getPackageManager();
    }

    public List<AppInfo> getInstalledApps() {
        List<PackageInfo> packageInfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        List<AppInfo> appInfos = new ArrayList<AppInfo>();

        for (PackageInfo info : packageInfos) {
            AppInfo appinfo = new AppInfo();
            appinfo.setPackname(info.packageName);
            appinfo.setVersion(info.versionName);
            appinfo.setAppname(info.applicationInfo.loadLabel(pm).toString());
            appinfo.setAppicon(info.applicationInfo.loadIcon(pm));
            appinfo.setUserpp(filterApp(info.applicationInfo));
            appInfos.add(appinfo);
            appinfo = null;
        }

        return appInfos;
    }

    public boolean filterApp(ApplicationInfo info) {
        if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            return false;
        } else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            return true;
        }

        return false;
    }
}
