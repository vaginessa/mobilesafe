package com.example.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.mobilesafe.R;
import com.example.mobilesafe.db.BlackNumberDao;
import com.example.mobilesafe.engine.GPSInfoProvider;

/**
 * Created by sing on 14-1-3.
 * desc:
 */
public class SmsReceiver extends BroadcastReceiver {

    public static final String TAG = "SmsReceiver";

    public static final int STOP_SMS = 1;
    public static final int STOP_CALL = 2;
    public static final int STOP_SMSCALL = 4;

    private SharedPreferences sp;

    private BlackNumberDao dao;

    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "收到短信");
        dao = new BlackNumberDao(context);

        sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        String safenumber = sp.getString("safenumber", "");

        Object[] objs = (Object[]) intent.getExtras().get("pdus");
        DevicePolicyManager dm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

        //创建一个与MyAdmin相关的组件
        ComponentName componentName = new ComponentName(context, MyAdmin.class);

        for (Object obj : objs) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);

            //获取发件人的地址
            String sender = smsMessage.getOriginatingAddress();

            //检查是否是黑名单号码
            int mode = dao.findNumberMode(sender);
            if ((mode & STOP_SMS) != 0) {
                Log.i(TAG, "拦截黑名单短信");
                abortBroadcast();
            }
            ////////////////////////////////

            //获取短信内容
            String body = smsMessage.getMessageBody();

            if (body.equals("#*location*#")) {
                Log.i(TAG, "发送当前位置");
                String lastLocation = GPSInfoProvider.getInstance(context).getLocation();
                if (lastLocation.isEmpty() == false) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(safenumber, null, lastLocation, null, null);
                }
                abortBroadcast();
            } else if (body.equals("#*alarm*#")) {
                Log.i(TAG, "播放报警音乐");

                //得到音乐播放器
                MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);

                //设置音量，静音下也有声音
                player.setVolume(1.0f, 1.0f);

                //开始播放
                player.start();

                abortBroadcast();
            } else if (body.equals("#*wipedata*#")) {
                Log.i(TAG, "清除数据");

                //判断设备的管理员权限是否被激活，只有被激活才可以执行锁屏、清除数据、恢复出厂设置（模拟器不支持该操作）等操作
                if (dm.isAdminActive(componentName)) {

                    //清除设备中的数据，手机会自动重启
                    dm.wipeData(0);
                }
                abortBroadcast();
            } else if (body.equals("#*lockscreen*#")) {
                Log.i(TAG, "远程锁屏");

                if (dm.isAdminActive(componentName)) {
                    dm.resetPassword("123", 0);
                    dm.lockNow();
                }
                abortBroadcast();
            }
        }//endfor
    }
}
