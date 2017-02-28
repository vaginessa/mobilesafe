package com.example.mobilesafe;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by sing on 13-12-24.
 * desc:
 */
public class MainAdapter extends BaseAdapter {

    //布局填充器
    private LayoutInflater inflater;

    //保存mainactivity传递来的上下文对象
    private Context context;

    //将9个item的图片id存入数组
    public static final int[] icons = {
            R.drawable.widget01, R.drawable.widget02, R.drawable.widget03,
            R.drawable.widget04, R.drawable.widget05, R.drawable.widget06,
            R.drawable.widget07, R.drawable.widget08, R.drawable.widget09
    };

    //将9个item的标题存入数据
    public static final String[] names = {
            "手机防盗", "通信卫士", "软件管理",
            "进程管理", "流量统计", "手机杀毒",
            "系统优化", "高级工具", "设置中心"
    };

    /**
     * 构造函数
     *
     * @param context
     */
    public MainAdapter(Context context) {
        this.context = context;
        //获取系统中的布局填充器
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //读取用户自定义标题
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        String newtitle = sp.getString("newtitle", "");
        if (newtitle.isEmpty() == false) {
            names[0] = newtitle;
        }
    }

    /**
     * 返回gridview有多少个item
     *
     * @return
     */
    @Override
    public int getCount() {
        return names.length;
    }

    /**
     * 返回item对象
     *
     * @param position
     * @return
     */
    @Override
    public Object getItem(int position) {
        return position;
    }

    /**
     * 返回gridview中的item的view对象
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.main_item, null);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_main_item_name);
        ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_main_item_icon);
        tv_name.setText(names[position]);
        iv_icon.setImageResource(icons[position]);
        return view;
    }

    /**
     * 返回item id
     *
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return position;
    }


}
