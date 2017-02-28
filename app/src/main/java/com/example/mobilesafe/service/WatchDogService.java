package com.example.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.example.mobilesafe.EnterPswdActivity;
import com.example.mobilesafe.db.AppLockDao;
import com.example.mobilesafe.engine.IService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sing on 14-1-16.
 * desc:
 */
public class WatchDogService extends Service {

    public static final String TAG = "WatchDogService";

    boolean flag;

    private Intent pswdIntent;

    private List<String> lockPacknames;
    private List<String> tempStopProtectPacknames;

    private AppLockDao dao;
    private MyObserver observer;

    private MyBinder binder;

    private LockScreenReceiver receiver;

    public IBinder onBind(Intent intent) {
        binder = new MyBinder();
        return binder;
    }

    private class MyBinder extends Binder implements IService {
        @Override
        public void callTempStopProtect(String packname) {
            tempStopProtect(packname);
        }
    }

    @Override
    public void onDestroy() {
        flag = false;
        getContentResolver().unregisterContentObserver(observer);
        observer = null;
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        //注册内容观察者
        Uri uri = Uri.parse("content://com.example.mobilesafe.applock/");
        observer = new MyObserver(new Handler());
        getContentResolver().registerContentObserver(uri, true, observer);

        //动态注册锁屏的广播接受者
        IntentFilter filter = new IntentFilter();
        filter.setPriority(1000);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        receiver = new LockScreenReceiver();
        registerReceiver(receiver, filter);

        super.onCreate();
        dao = new AppLockDao(this);
        flag = true;
        lockPacknames = dao.findAll();
        tempStopProtectPacknames = new ArrayList<String>();
        pswdIntent = new Intent(this, EnterPswdActivity.class);
        //服务没有任务栈，如果要开启一个在任务栈中运行的activity，需要为其创建一个任务栈
        pswdIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        new Thread() {
            @Override
            public void run() {
                while (flag) {
                    ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                    //获取当前正在栈顶运行的activity
                    ActivityManager.RunningTaskInfo taskInfo = am.getRunningTasks(1).get(0);
                    String packname = taskInfo.topActivity.getPackageName();
                    Log.i(TAG, packname);
                    if (tempStopProtectPacknames.contains(packname)) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }

                    pswdIntent.putExtra("packname", packname);
                    if (lockPacknames.contains(packname)) {
                        startActivity(pswdIntent);
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }.start();
    }

    public void tempStopProtect(String packname) {
        tempStopProtectPacknames.add(packname);
    }

    private class MyObserver extends ContentObserver {
        public MyObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            lockPacknames = dao.findAll();
            super.onChange(selfChange);
        }
    }

    /**
     * 锁屏清空“临时停止保护列表”
     */
    public class LockScreenReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            tempStopProtectPacknames.clear();
        }
    }
}
