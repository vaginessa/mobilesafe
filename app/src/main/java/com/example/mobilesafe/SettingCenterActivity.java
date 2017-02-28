package com.example.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.mobilesafe.service.CallFirewallService;
import com.example.mobilesafe.service.ShowCallLocationService;
import com.example.mobilesafe.service.WatchDogService;
import com.example.utils.ServiceStatusUtil;

/**
 * Created by sing on 13-12-24.
 * desc:
 */
public class SettingCenterActivity extends Activity {

    //背景风格
    private static final String[] bg_styles = {"半透明", "活力橙", "卫士蓝", "苹果绿", "金属灰"};

    //设置自动更新的checkbox
    private View rl_setting_autoupdate;
    private CheckBox cb_setting_autoupdate;

    //显示自动更新开启状态的文本框
    private TextView tv_setting_autoupdate_status;

    private View rl_setting_show_location;
    private CheckBox cb_setting_showlocation;
    private TextView tv_setting_show_location_status;

    //来电归属地显示风格
    private View rl_setting_showlocation_style;
    private TextView tv_setting_showlocation_style;

    //归属地提示框位置设置
    private View rl_setting_show_location_pos;

    //来电黑名单设置
    private View rl_setting_black_call;
    private CheckBox cb_setting_blackcall;
    private TextView tv_setting_blackcall_status;

    //程序锁设置
    private View rl_setting_applocker;
    private TextView tv_setting_applocker_status;
    private CheckBox cb_setting_applocker;

    //开启来电归属地信息显示的意图
    private Intent showLocationIntent;

    //保存配置
    private SharedPreferences sp;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settingcenter);

        tv_setting_autoupdate_status = (TextView) findViewById(R.id.tv_setting_autoupdate_status);
        cb_setting_autoupdate = (CheckBox) findViewById(R.id.cb_setting_autoupdate);

        //加载上次设置的状态
        sp = getSharedPreferences("config", MODE_PRIVATE);
        boolean autoupdate = sp.getBoolean("autoupdate", true);
        cb_setting_autoupdate.setChecked(autoupdate);
        tv_setting_autoupdate_status.setText(autoupdate ? "自动更新已经开启" : "自动更新已经关闭");
        tv_setting_autoupdate_status.setTextColor(autoupdate ? Color.WHITE : Color.GRAY);

        //为checkbox编写响应事件
        rl_setting_autoupdate = findViewById(R.id.rl_setting_autoupdate);
        rl_setting_autoupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean b = !cb_setting_autoupdate.isChecked();
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("autoupdate", b);
                editor.commit();
                cb_setting_autoupdate.setChecked(b);
                tv_setting_autoupdate_status.setText(b ? "自动更新已经开启" : "自动更新已经关闭");
                tv_setting_autoupdate_status.setTextColor(b ? Color.WHITE : Color.GRAY);
            }
        });

        //来电归属地设置
        showLocationIntent = new Intent(this, ShowCallLocationService.class);
        tv_setting_show_location_status = (TextView) findViewById(R.id.tv_setting_show_location_status);
        cb_setting_showlocation = (CheckBox) findViewById(R.id.cb_setting_showlocation);
        rl_setting_show_location = findViewById(R.id.rl_setting_show_location);
        rl_setting_show_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cb_setting_showlocation.isChecked()) {
                    cb_setting_showlocation.setChecked(false);
                    tv_setting_show_location_status.setText("来电归属地显示没有开启");
                    tv_setting_show_location_status.setTextColor(Color.GRAY);
                    //开启来电归属地显示
                    stopService(showLocationIntent);
                } else {
                    cb_setting_showlocation.setChecked(true);
                    tv_setting_show_location_status.setText("来电归属地显示已经开启");
                    tv_setting_show_location_status.setTextColor(Color.WHITE);
                    //关闭来电归属地显示
                    startService(showLocationIntent);
                }
            }
        });

        //来电归属地风格设置
        tv_setting_showlocation_style = (TextView) findViewById(R.id.tv_setting_showlocation_style);
        int style = sp.getInt("which", 0);
        tv_setting_showlocation_style.setText(bg_styles[style]);
        rl_setting_showlocation_style = findViewById(R.id.rl_setting_showlocation_style);
        rl_setting_showlocation_style.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseBgDlg();
            }
        });

        //归属地提示框位置设置
        rl_setting_show_location_pos = findViewById(R.id.rl_setting_show_location_pos);
        rl_setting_show_location_pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingCenterActivity.this, DragViewActivity.class);
                startActivity(intent);
            }
        });

        //来电黑名单设置
        rl_setting_black_call = findViewById(R.id.rl_setting_black_call);
        cb_setting_blackcall = (CheckBox) findViewById(R.id.cb_setting_blackcall);
        tv_setting_blackcall_status = (TextView) findViewById(R.id.tv_setting_blackcall_status);
        final Intent callServiceIntent = new Intent(this, CallFirewallService.class);
        final boolean stopblackcall = sp.getBoolean("stopblackcall", false);
        cb_setting_blackcall.setChecked(stopblackcall);
        if (stopblackcall) {
            tv_setting_blackcall_status.setText("来电黑名单拦截已经开启");
            tv_setting_blackcall_status.setTextColor(Color.WHITE);
            startService(callServiceIntent);
        } else {
            tv_setting_blackcall_status.setText("来电黑名单拦截没有开启");
            tv_setting_blackcall_status.setTextColor(Color.GRAY);
            stopService(callServiceIntent);
        }
        rl_setting_black_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean b = !cb_setting_blackcall.isChecked();
                cb_setting_blackcall.setChecked(b);
                if (b) {
                    tv_setting_blackcall_status.setText("来电黑名单拦截已经开启");
                    tv_setting_blackcall_status.setTextColor(Color.WHITE);
                    startService(callServiceIntent);
                } else {
                    tv_setting_blackcall_status.setText("来电黑名单拦截没有开启");
                    tv_setting_blackcall_status.setTextColor(Color.GRAY);
                    stopService(callServiceIntent);
                }
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("stopblackcall", b);
                editor.commit();
            }
        });

        //程序锁设置
        rl_setting_applocker = findViewById(R.id.rl_setting_applocker);
        tv_setting_applocker_status = (TextView) findViewById(R.id.tv_setting_applocker_status);
        cb_setting_applocker = (CheckBox) findViewById(R.id.cb_setting_applocker);
        boolean applock = sp.getBoolean("applock", false);
        cb_setting_applocker.setChecked(applock);
        tv_setting_applocker_status.setText(applock ? "程序锁服务已经开启" : "程序锁服务没有开启");
        tv_setting_applocker_status.setTextColor(applock ? Color.WHITE : Color.GRAY);
        rl_setting_applocker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingCenterActivity.this, WatchDogService.class);
                boolean b = !cb_setting_applocker.isChecked();
                if (b) {
                    startService(intent);
                } else {
                    stopService(intent);
                }
                cb_setting_applocker.setChecked(b);
                tv_setting_applocker_status.setText(b ? "程序锁服务已经开启" : "程序锁服务没有开启");
                tv_setting_applocker_status.setTextColor(b ? Color.WHITE : Color.GRAY);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("applock", b);
                editor.commit();
            }
        });
    }

    @Override
    protected void onResume() {
        boolean serviceRunning = ServiceStatusUtil.isServiceRunning(this, "com.example.mobilesafe.service.ShowCallLocationService");
        cb_setting_showlocation.setChecked(serviceRunning);
        tv_setting_show_location_status.setText(serviceRunning ? "来电归属地显示已经开启" : "来电归属地显示没有开启");
        tv_setting_show_location_status.setTextColor(serviceRunning ? Color.WHITE : Color.GRAY);

        super.onResume();
    }

    //选择背景颜色对话框
    private void showChooseBgDlg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.notification);
        builder.setTitle("归属地提示框风格");
        int which = sp.getInt("which", 0);
        builder.setSingleChoiceItems(bg_styles, which, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("which", i);
                editor.commit();
                tv_setting_showlocation_style.setText(bg_styles[i]);
                dialogInterface.dismiss();
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        builder.create().show();
    }
}