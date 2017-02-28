package com.example.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.mobilesafe.LostProtectedActivity;

/**
 * Created by sing on 13-12-26.
 * desc:
 */
public class CallOutReceiver extends BroadcastReceiver {

    //设定进入手机防盗的号码
    private static final String enterLostProtectedPhoneNumber = "110";

    public void onReceive(Context context, Intent intent) {

        //获取广播发送来的数据
        String number = getResultData();
        if (number.equals(enterLostProtectedPhoneNumber)) {
            Intent lostProtectedIntent = new Intent(context, LostProtectedActivity.class);

            //为lostProtectedIntent设置新的任务栈
            lostProtectedIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(lostProtectedIntent);

            //拦截该外拨号码，拨号记录中不会显示此次拨号
            setResultData(null);
        }

    }
}
