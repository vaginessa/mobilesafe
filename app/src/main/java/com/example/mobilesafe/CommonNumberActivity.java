package com.example.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.mobilesafe.db.CommonNumberDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sing on 14-1-10.
 * desc:
 */
public class CommonNumberActivity extends Activity {

    public static final String TAG = "CommonNumberActivity";
    private ExpandableListView elv_common_number;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commonnumberactivity_layout);

        elv_common_number = (ExpandableListView) findViewById(R.id.elv_common_number);

        //设置适配器
        elv_common_number.setAdapter(new CommonNumberAdapter());

        //点击分组中的孩子view时的处理事件
        elv_common_number.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long l) {

                //取号码
                String number = ((TextView) view).getText().toString().split("\n")[1];

                //拨号
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + number));
                startActivity(intent);

                return false;
            }
        });
    }

    private class CommonNumberAdapter extends BaseExpandableListAdapter {

        private List<String> groupNames;

        private Map<Integer, List<String>> childrenCache;

        public CommonNumberAdapter() {
            childrenCache = new HashMap<Integer, List<String>>();
        }

        @Override
        public View getChildView(int i, int i2, boolean b, View view, ViewGroup viewGroup) {
            TextView tv;

            if (view == null) {
                tv = new TextView(getApplicationContext());
            } else {
                tv = (TextView) view;
            }

            tv.setTextSize(20);

            String name = null;
            if (childrenCache.containsKey(i)) {
                name = childrenCache.get(i).get(i2);
            } else {
                List<String> results = CommonNumberDao.getChildrenNamesByPosition(i);
                childrenCache.put(i, results);
                name = results.get(i2);
            }
            tv.setText(name);

            return tv;
        }

        /**
         * 返回true表示分组条目可以响应单击事件
         *
         * @param i
         * @param i2
         * @return
         */
        @Override
        public boolean isChildSelectable(int i, int i2) {
            return true;
        }

        /**
         * 返回组数
         *
         * @return
         */
        @Override
        public int getGroupCount() {
            return CommonNumberDao.getGroupCount();
        }

        /**
         * 一组有多少条目
         *
         * @param i
         * @return
         */
        @Override
        public int getChildrenCount(int i) {
            return CommonNumberDao.getChildrenCount(i);
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public Object getChild(int i, int i2) {
            return null;
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public Object getGroup(int i) {
            return null;
        }

        @Override
        public long getChildId(int i, int i2) {
            return i2;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            TextView tv;

            if (view == null) {
                tv = new TextView(getApplicationContext());
            } else {
                tv = (TextView) view;
            }
            tv.setTextSize(28);
            if (groupNames == null) {
                groupNames = CommonNumberDao.getGroupNames();
            }
            tv.setText("        " + groupNames.get(i));

            return tv;
        }
    }

}