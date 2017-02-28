package com.example.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mobilesafe.R;
import com.example.mobilesafe.db.NumberAddressDao;

/**
 * Created by sing on 14-1-13.
 * desc:监听电话呼入的服务
 */
public class ShowCallLocationService extends Service {

    //"半透明", "活力橙", "卫士蓝", "苹果绿", "金属灰"
    public static final int[] bg_styles = {R.drawable.call_locate_white,
            R.drawable.call_locate_orange, R.drawable.call_locate_blue,
            R.drawable.call_locate_green, R.drawable.call_locate_gray};

    //电话管理器
    private TelephonyManager tm;

    //窗体管理器
    private WindowManager windowManager;

    //电话状态改变的监听器
    private MyPhoneListener listener;

    //保存配置
    private SharedPreferences sp;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sp = getSharedPreferences("config", MODE_PRIVATE);
        listener = new MyPhoneListener();
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    private class MyPhoneListener extends PhoneStateListener {

        private View view;

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                //电话铃声正在响
                case TelephonyManager.CALL_STATE_RINGING:
                    //查询出电话号码的归属地
                    NumberAddressDao numberAddressDao = new NumberAddressDao(getApplicationContext());
                    String address = numberAddressDao.getAddress(incomingNumber);

                    //通过布局填充器将布局转换为view
                    view = View.inflate(getApplicationContext(), R.layout.show_address, null);
                    LinearLayout ll = (LinearLayout) view.findViewById(R.id.ll_show_address);
                    int style = sp.getInt("which", 0);
                    ll.setBackgroundResource(bg_styles[style]);
                    TextView tv = (TextView) view.findViewById(R.id.tv_show_address);
                    tv.setText(address);

                    final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                    params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    //显示在窗体上的style为半透明
                    params.format = PixelFormat.TRANSLUCENT;
                    //窗体view的类型为吐司
                    params.type = WindowManager.LayoutParams.TYPE_TOAST;
                    windowManager.addView(view, params);
                    break;
                //电话空闲状态
                case TelephonyManager.CALL_STATE_IDLE:
                    //将窗体上的吐司移除
                    if (view != null) {
                        windowManager.removeView(view);
                        view = null;
                    }
                    break;
                //电话接通状态
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //将窗体上的吐司移除
                    if (view != null) {
                        windowManager.removeView(view);
                        view = null;
                    }
                    break;
            }

            super.onCallStateChanged(state, incomingNumber);
        }

    }
}
