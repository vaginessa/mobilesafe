package com.example.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafe.engine.AppInfo;
import com.example.mobilesafe.engine.AppInfoProvider;
import com.example.utils.DensityUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sing on 14-1-21.
 * desc:程序管理器
 */
public class AppManagerActivity extends Activity implements View.OnClickListener {

    public static final String TAG = "AppManagerActivity";
    public static final int LOAD_FINISHED = 1;

    private TextView tv_mem_avail;
    private TextView tv_sdcard_avail;
    private View ll_appmanager_loading;
    private ListView lv_apps;

    //PopupWindow中contentView对应的三个控件
    private View ll_uninstall;  //卸载
    private View ll_start;      //启动
    private View ll_share;      //分享

    private List<AppInfo> appInfos;
    private List<AppInfo> userappInfos;
    private List<AppInfo> systemappInfos;

    private PopupWindow popupWindow;
    private String clickedpackname;

    // 相当于windows系统下面的程序管理器（可以获取手机中所有的应用程序）
    private PackageManager pm;

    private BaseAdapter adapter = new AppManagerAdapter();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == LOAD_FINISHED) {
                ll_appmanager_loading.setVisibility(View.INVISIBLE);
                lv_apps.setAdapter(adapter);
            }
        }
    };

    private class AppManagerAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            //每个分组多一个标题头
            return userappInfos.size() + 1 + systemappInfos.size() + 1;
        }

        /**
         * 屏蔽掉两个TextView（用户程序和系统程序）被点击时的焦点
         */
        @Override
        public boolean isEnabled(int position) {
            if (position == 0 || position == (userappInfos.size() + 1)) {
                return false;
            }
            return super.isEnabled(position);
        }

        @Override
        public Object getItem(int i) {
            if (i == 0) {
                return i;
            } else if (i <= userappInfos.size()) {
                return userappInfos.get(i - 1);
            } else if (i == userappInfos.size() + 1) {
                return i;
            } else {
                return systemappInfos.get(i - userappInfos.size() - 2);
            }
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v;
            ViewHolder holder;

            if (i == 0) {
                TextView tv = new TextView(getApplicationContext());
                tv.setTextSize(20);
                tv.setText("用户程序（" + userappInfos.size() + "）");
                return tv;
            } else if (i <= userappInfos.size()) {
                if (view == null || view instanceof TextView) {
                    v = View.inflate(getApplicationContext(), R.layout.app_manager_item, null);
                    holder = new ViewHolder();
                    holder.iv_icon = (ImageView) v.findViewById(R.id.iv_appmanager_icon);
                    holder.tv_name = (TextView) v.findViewById(R.id.tv_appmanager_name);
                    holder.tv_version = (TextView) v.findViewById(R.id.tv_appmanager_version);
                    v.setTag(holder);
                } else {
                    v = view;
                    holder = (ViewHolder) view.getTag();
                }

                AppInfo appInfo = userappInfos.get(i - 1);
                holder.iv_icon.setImageDrawable(appInfo.getAppicon());
                holder.tv_name.setText(appInfo.getAppname());
                holder.tv_version.setText("版本号：" + appInfo.getVersion());
                return v;
            } else if (i == userappInfos.size() + 1) {
                TextView tv = new TextView(getApplicationContext());
                tv.setTextSize(20);
                tv.setText("系统程序（" + systemappInfos.size() + "）");
                return tv;
            } else {
                if (view == null || view instanceof TextView) {
                    v = View.inflate(getApplicationContext(), R.layout.app_manager_item, null);
                    holder = new ViewHolder();
                    holder.iv_icon = (ImageView) v.findViewById(R.id.iv_appmanager_icon);
                    holder.tv_name = (TextView) v.findViewById(R.id.tv_appmanager_name);
                    holder.tv_version = (TextView) v.findViewById(R.id.tv_appmanager_version);
                    v.setTag(holder);
                } else {
                    v = view;
                    holder = (ViewHolder) view.getTag();
                }

                AppInfo appInfo = systemappInfos.get(i - userappInfos.size() - 2);
                holder.iv_icon.setImageDrawable(appInfo.getAppicon());
                holder.tv_name.setText(appInfo.getAppname());
                holder.tv_version.setText("版本号：" + appInfo.getVersion());
                return v;
            }
        }

        private class ViewHolder {
            ImageView iv_icon;
            TextView tv_name;
            TextView tv_version;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appmanager_layout);

        pm = getPackageManager();

        tv_mem_avail = (TextView) findViewById(R.id.tv_mem_avail);
        tv_sdcard_avail = (TextView) findViewById(R.id.tv_sdcard_avail);
        ll_appmanager_loading = findViewById(R.id.ll_appmanager_loading);
        lv_apps = (ListView) findViewById(R.id.lv_apps);
        lv_apps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //当用户点击下一个Item时，需要关闭已经存在的PopupWindow
                dismissPopupWindow();
                //将布局文件转成view，该view用于显示PopupWindow中的内容
                View contentView = View.inflate(getApplicationContext(), R.layout.popup_item, null);
                //分别获取到PopupWindow窗体中的"卸载、启动、分享"控件所对应的父控件
                ll_uninstall = (LinearLayout) contentView.findViewById(R.id.ll_popup_uninstall);
                ll_start = (LinearLayout) contentView.findViewById(R.id.ll_popup_start);
                ll_share = (LinearLayout) contentView.findViewById(R.id.ll_popup_share);
                //获取用于显示PopupWindow内容的View的根布局，这里是要为该布局设置动画（相当于为PopupWindow设置动画）
                View ll_popup_container = contentView.findViewById(R.id.ll_popup_container);

                //为"卸载、启动、分享"设置点击事件
                ll_share.setOnClickListener(AppManagerActivity.this);
                ll_start.setOnClickListener(AppManagerActivity.this);
                ll_uninstall.setOnClickListener(AppManagerActivity.this);

                //获取到当前Item的对象
                Object obj = lv_apps.getItemAtPosition(i);
                //当Item为系统应用时，此时为PopupWindow中的"卸载"设置一个标记，在卸载时判断该标记，禁止卸载系统应用
                if (obj instanceof AppInfo) {
                    AppInfo appinfo = (AppInfo) obj;
                    clickedpackname = appinfo.getPackname();
                    if (appinfo.isUserpp()) {
                        ll_uninstall.setTag(true);
                    } else {
                        ll_uninstall.setTag(false);
                    }
                } else {
                    return;
                }
                //获取到当前Item离顶部、底部的距离
                int top = view.getTop();
                int bottom = view.getBottom();
                //创建PopupWindow窗体时必须要指定窗体的大小，否则不会显示在界面上。参数一：窗体中用于显示内容的viewContent，参数二、三：表示PopupWindow窗体的宽和高
                popupWindow = new PopupWindow(contentView, DensityUtil.dip2px(getApplicationContext(), 200), bottom - top
                        + DensityUtil.dip2px(getApplicationContext(), 30));
                // 注意:一定要给popwindow设置背景图片或背景资源,如果不设置背景资源 , 动画、 焦点的处理 都会产生问题。
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                //获取到Item在窗体中显示的位置
                int[] location = new int[2];
                view.getLocationInWindow(location);
                //参数一：PopupWindow挂载在那个View上，参数二：设置PopupWindow显示的重心位置
                //参数三：PopupWindow在View上X轴的偏移量，参数四：PopupWindow在View上Y轴的偏移量。X、Y轴的偏移量是相对于当前Activity所在的窗体，参照点为（0，0）
                popupWindow.showAtLocation(view, Gravity.TOP | Gravity.LEFT, location[0] + 20, location[1]);

                //设置一个缩放的动画效果
                ScaleAnimation sa = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f);
                //设置动画执行的时间
                sa.setDuration(300);
                // 播放一个缩放的动画.
                ll_popup_container.startAnimation(sa);
            }
        });

        /**
         * 当用户滑动窗体的时候,需要关闭已经存在的PopupWindow
         */
        lv_apps.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                dismissPopupWindow();
            }
        });

        tv_mem_avail.setText("内存可用" + getAvailROMSize());
        tv_sdcard_avail.setText("SD卡可用" + getAvailSDSize());

        fillData();
    }

    /**
     * 获取手机可用内存
     *
     * @return
     */
    private String getAvailROMSize() {
        File path = Environment.getDataDirectory();
        StatFs statFs = new StatFs(path.getPath());
        long blockSize = statFs.getBlockSize();
        long availableBlocks = statFs.getAvailableBlocks();
        return Formatter.formatFileSize(this, availableBlocks * blockSize);
    }

    /**
     * 获取SD卡可用内存
     *
     * @return
     */
    private String getAvailSDSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(path.getPath());
        long blockSize = statFs.getBlockSize();
        long availableBlocks = statFs.getAvailableBlocks();
        return Formatter.formatFileSize(this, availableBlocks * blockSize);
    }

    private void fillData() {
        ll_appmanager_loading.setVisibility(View.VISIBLE);
        new Thread() {
            @Override
            public void run() {
                AppInfoProvider provider = new AppInfoProvider(AppManagerActivity.this);
                appInfos = provider.getInstalledApps();
                initAppInfo();
                Message msg = Message.obtain();
                msg.what = LOAD_FINISHED;
                handler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 区分出用户程序和系统程序
     */
    private void initAppInfo() {
        userappInfos = new ArrayList<AppInfo>();
        systemappInfos = new ArrayList<AppInfo>();

        for (AppInfo appinfo : appInfos) {
            if (appinfo.isUserpp()) {
                userappInfos.add(appinfo);
            } else {
                systemappInfos.add(appinfo);
            }
        }
    }

    @Override
    protected void onDestroy() {
        dismissPopupWindow();
        super.onDestroy();
    }

    /**
     * 当用户在界面上点击下一个Item时，要关闭上一个PopupWindow
     */
    private void dismissPopupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    /**
     * PopupWindow中的点击事件
     */
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_popup_share:
                Log.i(TAG, "分享");
                shareApplication();
                break;

            case R.id.ll_popup_start:
                Log.i(TAG, "开启");
                startAppliction();

                break;
            case R.id.ll_popup_uninstall:
                //获取到Item为“ll_popup_uninstall”设置的标记
                boolean result = (Boolean) v.getTag();
                //禁止卸载系统应用
                if (result) {
                    Log.i(TAG, "卸载" + clickedpackname);
                    uninstallApplication();
                } else {
                    Toast.makeText(this, "系统应用不能被卸载", 1).show();
                }
                break;
        }

    }

    /**
     * 分享一个应用程序
     */
    private void shareApplication() {
        /*<intent-filter>
        <action android:name="android.intent.action.SEND" />
        <category android:name="android.intent.category.DEFAULT" />
        <data android:mimeType="text/plain" />
        </intent-filter>*/
        Intent intent = new Intent();
        //通过意图的的动作、类型来激活手机中具有分享功能的应用（短信，互联网...），这写具有分享功能的应用会以列表的格式展现出来
        intent.setAction("android.intent.action.SEND");
        intent.addCategory("android.intent.category.DEFAULT");
        //输入的内容为文本类型
        intent.setType("text/plain");
        //设置分享的标题
        intent.putExtra("subject", "分享的标题");
        //设置分享的默认内容
        intent.putExtra("sms_body", "推荐你使用一款软件" + clickedpackname);
        intent.putExtra(Intent.EXTRA_TEXT, "extra_text");
        startActivity(intent);
    }

    /**
     * 卸载一个应用程序
     */
    private void uninstallApplication() {

		/* * <intent-filter> <action android:name="android.intent.action.VIEW" />
         * <action android:name="android.intent.action.DELETE" /> <category
		 * android:name="android.intent.category.DEFAULT" /> <data
		 * android:scheme="package" /> </intent-filter>*/

        dismissPopupWindow();
        Intent intent = new Intent();
        intent.setAction("android.intent.action.DELETE");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:" + clickedpackname));
        //卸载一个应用程序后，对应的Sdcard或内存会发生变化，此时我们应当更新该信息。并且需要将卸载的应用从列表中移除
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            // 通知界面更新数据.
            fillData();
            tv_mem_avail.setText("内存可用" + getAvailROMSize());
            tv_sdcard_avail.setText("SD卡可用" + getAvailSDSize());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 开启一个应用程序
     */
    private void startAppliction() {
        dismissPopupWindow();
        Intent intent = new Intent();
        PackageInfo packinfo;
        try {
            //PackageManager.GET_ACTIVITIES告诉包管理者，在解析清单文件时，只解析Activity对应的节点
            packinfo = pm.getPackageInfo(clickedpackname, PackageManager.GET_ACTIVITIES);

            ActivityInfo[] activityinfos = packinfo.activities;
            //判断清单文件中是否存在Activity对应的节点
            if (activityinfos != null && activityinfos.length > 0) {
                //启动清单文件中的第一个Activity节点
                String className = activityinfos[0].name;
                intent.setClassName(clickedpackname, className);
                startActivity(intent);
            } else {
                Toast.makeText(this, "不能启动当前应用", 0).show();
            }
        } catch (PackageManager.NameNotFoundException e) {//使用C语言实现的应用程序，在DDMS中没有对应的包名
            e.printStackTrace();
            Toast.makeText(this, "不能启动当前应用", 0).show();
        }
    }

    /**
     * 获取具有启动属性的intent 系统桌面应用(luncher)
     */
    public Intent getIntent() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        List<ResolveInfo> resoveInfo = pm.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS | PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo info : resoveInfo) {
            // info.activityInfo.packageName;
        }
        return null;
    }
}