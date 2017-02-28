package com.example.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

/**
 * Created by sing on 13-12-24.
 * desc:
 */
public class MainActivity extends Activity {
    //activity_main中的gridview控件
    private GridView gv_main;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gv_main = (GridView) findViewById(R.id.gv_main);
        //为gv_main对象设置适配器，该适配器为每个item填充对应的数据
        gv_main.setAdapter(new MainAdapter(this));

        //为gv_main设置点击item的处理事件
        gv_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * item点击事件
             * @param adapterView
             * @param view
             * @param i
             * @param l
             */
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = null;
                switch (i) {
                    case 0: //手机防盗
                        intent = new Intent(MainActivity.this, LostProtectedActivity.class);
                        startActivity(intent);
                        break;
                    case 1: //通信卫士
                        intent = new Intent(MainActivity.this, CallSafeActivity.class);
                        startActivity(intent);
                        break;
                    case 2: //软件管理
                        intent = new Intent(MainActivity.this, AppManagerActivity.class);
                        startActivity(intent);
                        break;
                    case 3: //进程管理
                        intent = new Intent(MainActivity.this, TaskManagerActivity.class);
                        startActivity(intent);
                        break;
                    case 4: //流量统计
                        intent = new Intent(MainActivity.this, TrafficInfoActivity.class);
                        startActivity(intent);
                        break;
                    case 5: //手机杀毒
                        intent = new Intent(MainActivity.this, AntiVirusActivity.class);
                        startActivity(intent);
                        break;
                    case 6: //系统优化
                        intent = new Intent(MainActivity.this, CleanCacheActivity.class);
                        startActivity(intent);
                        break;
                    case 7: //高级工具
                        intent = new Intent(MainActivity.this, AToolsActivity.class);
                        startActivity(intent);
                        break;
                    case 8: //设置中心
                        intent = new Intent(MainActivity.this, SettingCenterActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }
}