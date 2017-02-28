package com.example.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utils.AssetUtil;
import com.example.utils.DownLoadUtil;
import com.example.utils.util;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class SplashActivity extends Activity {
    //常量定义
    public static final int UNKNOWN_ERROR = 99;
    public static final int GET_INFO_SUCCESS = 100;
    public static final int SERVER_ERROR = 101;
    public static final int SERVER_URL_ERROR = 102;
    public static final int PROTOCOL_ERROR = 103;
    public static final int IO_ERROR = 104;
    public static final int XML_PARSER_ERROR = 105;
    public static final int DOWNLOAD_SUCCESS = 106;
    public static final int DOWNLOAD_ERROR = 107;
    public static final String TAG = "SplashActivity";

    //获取的服务器端的更新信息
    UpdateInfo updateinfo;

    //显示版本号的tv控件
    private TextView tv_splash_version;

    //布局控件
    private RelativeLayout r1_splash;

    //升级下载进度条
    ProgressDialog pd;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置无标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //设置全屏模式
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //设置局部，显示版本号
        setContentView(R.layout.activity_splash);
        r1_splash = (RelativeLayout) findViewById(R.id.r1_splash);
        tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
        tv_splash_version.setText("版本号：" + getVersion());

        //显示渐进动画
        AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
        aa.setDuration(2000);
        r1_splash.startAnimation(aa);

        //检查更新
        new Thread(new checkUpdate()) {
        }.start();

        //拷贝病毒库的数据库文件
        new Thread() {
            public void run() {
                File file = new File(getFilesDir(), "antivirus.db");
                if (file.exists() && file.length() > 0) {//数据库文件已经拷贝成功

                } else {
                    AssetUtil.copy1(getApplicationContext(), "antivirus.db", file.getAbsolutePath(), null);
                }
            }

            ;

        }.start();
    }

    //获取当前应用程序的版本号
    private String getVersion() {
        String version = "";

        //获取系统包管理器
        PackageManager pm = this.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
            version = info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 消息处理器
     */
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UNKNOWN_ERROR:
                    Toast.makeText(getApplicationContext(), "未知错误", 1).show();
                    loadMainUI();
                    break;
                case GET_INFO_SUCCESS:
                    String serverVersion = updateinfo.getVersion();
                    String currentVersion = getVersion();
                    if (currentVersion.equals(serverVersion)) {
                        Log.i(TAG, "版本号相同无需升级，直接进入主界面");
                        loadMainUI();
                    } else {
                        Log.i(TAG, "版本号不同需升级，显示升级对话框");
                        showUpdateDialog();
                    }
                    break;
                case SERVER_ERROR:
                    Toast.makeText(getApplicationContext(), "服务器内部异常", 1).show();
                    loadMainUI();
                    break;
                case SERVER_URL_ERROR:
                    Toast.makeText(getApplicationContext(), "服务器路径错误", 1).show();
                    loadMainUI();
                    break;
                case PROTOCOL_ERROR:
                    Toast.makeText(getApplicationContext(), "协议错误", 1).show();
                    loadMainUI();
                    break;
                case XML_PARSER_ERROR:
                    Toast.makeText(getApplicationContext(), "XML解析错误", 1).show();
                    loadMainUI();
                    break;
                case IO_ERROR:
                    Toast.makeText(getApplicationContext(), "I/O错误", 1).show();
                    loadMainUI();
                    break;
                case DOWNLOAD_SUCCESS:
                    Log.i(TAG, "文件下载成功");
                    //得到消息中的文件对象，并安装
                    File file = (File) msg.obj;
                    util.installApk(SplashActivity.this, file);
                    break;
                case DOWNLOAD_ERROR:
                    Toast.makeText(getApplicationContext(), "下载文件错误", 1).show();
                    loadMainUI();
                    break;
            }
        }
    };

    /**
     * 加载主界面
     */
    private void loadMainUI() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();// 把当前的Activity从任务栈里面移除
    }

    /**
     * 检查更新
     */
    private class checkUpdate implements Runnable {
        public void run() {
            ////////////////////////////////////////////////////////
            SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
            boolean autoupdate = sp.getBoolean("autoupdate", true);
            // 自动更新没有开启
            if (!autoupdate) {
                try {
                    //睡眠2秒钟的是为了播放动画
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 睡眠2秒钟播放动画完毕后进入程序主界面
                loadMainUI();
                return;
            }
            ////////////////////////////////////////////////////////

            long startTime = System.currentTimeMillis();
            long endTime = startTime;
            Message msg = Message.obtain();
            try {
                //获取服务器更新地址
                String serverurl = getResources().getString(R.string.serverurl);
                URL url = new URL(serverurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                int code = conn.getResponseCode();
                if (code == 200) {
                    //success
                    InputStream is = conn.getInputStream();
                    //解析出更新信息
                    updateinfo = UpdateInfoParser.getUpdateInfo(is);
                    endTime = System.currentTimeMillis();
                    long time = endTime - startTime;
                    if (time < 2000) {
                        try {
                            Thread.sleep(2000 - time);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    msg.what = GET_INFO_SUCCESS;
                    handler.sendMessage(msg);
                } else {
                    //服务器错误
                    msg.what = SERVER_ERROR;
                    handler.sendMessage(msg);
                    endTime = System.currentTimeMillis();
                    long time = endTime - startTime;
                    if (time < 2000) {
                        try {
                            Thread.sleep(2000 - time);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            } catch (MalformedURLException e) {
                msg.what = SERVER_URL_ERROR;
                handler.sendMessage(msg);
                e.printStackTrace();
            } catch (ProtocolException e) {
                msg.what = PROTOCOL_ERROR;
                handler.sendMessage(msg);
                e.printStackTrace();
            } catch (IOException e) {
                msg.what = IO_ERROR;
                handler.sendMessage(msg);
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                msg.what = XML_PARSER_ERROR;
                handler.sendMessage(msg);
                e.printStackTrace();
            } catch (Exception e) {
                msg.what = UNKNOWN_ERROR;
                handler.sendMessage(msg);
                e.printStackTrace();
            }

        }
    }

    /**
     * 显示升级提示对话框
     */
    protected void showUpdateDialog() {

        //创建对话框构造器
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //设置对话框图标
        builder.setIcon(getResources().getDrawable(R.drawable.notification));
        //设置对话框标题
        builder.setTitle("升级提示");
        //设置更新信息为对话框提示内容
        builder.setMessage(updateinfo.getDesc());

        //创建下载进度条
        pd = new ProgressDialog(SplashActivity.this);
        //设置进度条在显示时的提示消息
        pd.setMessage("正在下载");
        //设置下载进度条为水平形状
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        //设置升级按钮及响应事件
        builder.setPositiveButton("升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "升级，下载：" + updateinfo.getApkurl());
                //判断sdcard是否存在
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    //显示下载进度条
                    pd.show();
                    //开启线程下载apk文件
                    new Thread() {
                        public void run() {
                            File file = new File(Environment.getExternalStorageDirectory(), DownLoadUtil.getFileName(updateinfo.getApkurl()));
                            file = DownLoadUtil.getFile(updateinfo.getApkurl(), file.getAbsolutePath(), pd);
                            if (file != null) {
                                //下载成功
                                Message msg = Message.obtain();
                                msg.what = DOWNLOAD_SUCCESS;
                                msg.obj = file;
                                handler.sendMessage(msg);
                            } else {
                                //下载失败
                                Message msg = Message.obtain();
                                msg.what = DOWNLOAD_ERROR;
                                handler.sendMessage(msg);
                            }

                            //下载完毕，关闭进度条
                            pd.dismiss();
                        }

                        ;
                    }.start();
                } else {
                    Toast.makeText(getApplicationContext(), "sd卡不可用", 1).show();
                    //进入主界面
                    loadMainUI();
                }
            }
        });
        //设置取消按钮及响应事件
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //进入主界面
                loadMainUI();
            }
        });

        //创建并显示对话框
        builder.create().show();
    }
}
