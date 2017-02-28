package com.example.mobilesafe;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mobilesafe.db.AppLockDao;
import com.example.mobilesafe.engine.AppInfo;
import com.example.mobilesafe.engine.AppInfoProvider;

import java.util.List;

/**
 * Created by sing on 14-1-10.
 * desc:
 */
public class AppLockerActivity extends Activity {

    public static final String TAG = "AppLockerActivity";

    private ListView lv_applock;
    private View ll_loading;

    private AppInfoProvider provider;
    private List<AppInfo> appInfos;
    private AppLockDao dao;
    private List<String> lockedPacknames;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ll_loading.setVisibility(View.INVISIBLE);
            lv_applock.setAdapter(new AppLockAdapter());
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applockeractivity_layout);

        lv_applock = (ListView) findViewById(R.id.lv_applock);
        ll_loading = findViewById(R.id.ll_applock_loading);

        provider = new AppInfoProvider(this);
        dao = new AppLockDao(this);
        lockedPacknames = dao.findAll();

        ll_loading.setVisibility(View.VISIBLE);
        new Thread() {
            public void run() {
                appInfos = provider.getInstalledApps();
                handler.sendEmptyMessage(0);
            }
        }.start();

        lv_applock.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AppInfo appInfo = (AppInfo) lv_applock.getItemAtPosition(i);
                String packname = appInfo.getPackname();
                ImageView iv = (ImageView) view.findViewById(R.id.iv_applock_status);
                TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, 0.2f,
                        Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, 0);
                ta.setDuration(200);
                if (lockedPacknames.contains(packname)) {
                    //dao.delete(packname);
                    Uri uri = Uri.parse("content://com.example.mobilesafe.applock/DELETE");
                    getContentResolver().delete(uri, null, new String[]{packname});
                    iv.setImageResource(R.drawable.unlock);
                    lockedPacknames.remove(packname);
                } else {
                    //dao.add(packname);
                    Uri uri = Uri.parse("content://com.example.mobilesafe.applock/ADD");
                    ContentValues values = new ContentValues();
                    values.put("packname", packname);
                    getContentResolver().insert(uri, values);
                    iv.setImageResource(R.drawable.lock);
                    lockedPacknames.add(packname);
                }
                view.startAnimation(ta);
            }
        });
    }

    private class AppLockAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return appInfos.size();
        }

        @Override
        public Object getItem(int i) {
            return appInfos.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v;
            ViewHolder holder;

            if (view == null) {
                v = View.inflate(getApplicationContext(), R.layout.app_lock_item, null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) v.findViewById(R.id.iv_applock_icon);
                holder.iv_status = (ImageView) v.findViewById(R.id.iv_applock_status);
                holder.tv_name = (TextView) v.findViewById(R.id.iv_applock_name);
                v.setTag(holder);
            } else {
                v = view;
                holder = (ViewHolder) view.getTag();
            }

            AppInfo appInfo = appInfos.get(i);
            holder.iv_icon.setImageDrawable(appInfo.getAppicon());
            holder.tv_name.setText(appInfo.getAppname());
            if (lockedPacknames.contains(appInfo.getPackname())) {
                holder.iv_status.setImageResource(R.drawable.lock);
            } else {
                holder.iv_status.setImageResource(R.drawable.unlock);
            }

            return v;
        }
    }

    public static class ViewHolder {
        ImageView iv_icon;
        ImageView iv_status;
        TextView tv_name;
    }
}