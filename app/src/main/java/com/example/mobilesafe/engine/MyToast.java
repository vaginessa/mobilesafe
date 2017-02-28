package com.example.mobilesafe.engine;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafe.R;

/**
 * Created by sing on 14-1-26.
 * desc:
 */
public class MyToast {
    private static final String TAG = "MyToast";

    /**
     * 显示自定义的土司
     *
     * @param text 显示的内容
     */
    public static void showToast(Context context, String text) {
        Toast toast = new Toast(context);
        View view = View.inflate(context, R.layout.mytoast, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_toast);
        //设置显示内容
        tv.setText(text);
        toast.setView(view);
        //设置Toast显示的时长。0表示短，1表示常
        toast.setDuration(1);
        //设置Toast显示在窗体中的位置（这里是显示在窗体中央）
        toast.setGravity(Gravity.CENTER, 0, 0);
        //将Toast显示出来
        toast.show();
    }
}
