package com.example.mobilesafe;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafe.engine.IService;
import com.example.mobilesafe.service.WatchDogService;

/**
 * Created by sing on 14-1-16.
 * desc:
 */
public class EnterPswdActivity extends Activity {
    public static final String TAG = "EnterPswdActivity";

    private ImageView iv_icon;
    private TextView tv_name;
    private EditText et_pswd;
    private Button bt_enter;

    private Intent serviceIntent;

    private IService iService;

    private MyConn conn;

    private String packname;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enterpswd_layout);

        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        tv_name = (TextView) findViewById(R.id.tv_name);
        et_pswd = (EditText) findViewById(R.id.et_pswd);
        bt_enter = (Button) findViewById(R.id.bt_enter);
        bt_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pswd = et_pswd.getText().toString().trim();
                if (pswd.isEmpty()) {
                    Toast.makeText(EnterPswdActivity.this, "密码不能为空", 0).show();
                    return;
                }
                if (pswd.equals("123")) {
                    iService.callTempStopProtect(packname);
                    finish();
                } else {
                    Toast.makeText(EnterPswdActivity.this, "密码不正确", 0).show();
                    return;
                }
            }
        });

        //获取到激活当前activity的意图，也就是WatchDogService传递的pswdIntent
        Intent intent = getIntent();
        packname = intent.getStringExtra("packname");

        serviceIntent = new Intent(this, WatchDogService.class);
        conn = new MyConn();

        //绑定服务（非startService），执行WatchDogService中的onCreate-onBing方法
        //如果绑定成功，WatchDogService中onBing方法返回一个IBinder给conn.ServiceConnection
        bindService(serviceIntent, conn, BIND_AUTO_CREATE);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(packname, 0);
            iv_icon.setImageDrawable(info.applicationInfo.loadIcon(getPackageManager()));
            tv_name.setText(info.applicationInfo.loadLabel(getPackageManager()));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    private class MyConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            iService = (IService) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //接触绑定
        unbindService(conn);
    }

    /**
     * 不允许用户按back键后退
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}