package com.example.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by sing on 13-12-31.
 * desc:
 */
public class BootCompleteReceiver extends BroadcastReceiver {

    public static final String TAG = "BootCompleteReceiver";

    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "手机重启了");
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        boolean protecting = sp.getBoolean("protecting", false);
        if (protecting) {
            String safenumber = sp.getString("safenumber", "");
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String nowsim = tm.getSimSerialNumber();
            String savedsim = sp.getString("sim", "");
            if (nowsim.equals(savedsim) == true) {
                Log.i(TAG, "sim卡变更，发送通知短信");
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(savedsim, nowsim, "sim card changed", null, null);
            }
        }
    }
}
