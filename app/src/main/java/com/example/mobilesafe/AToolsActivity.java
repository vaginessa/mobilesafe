package com.example.mobilesafe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utils.AssetUtil;

import java.io.File;

/**
 * Created by sing on 14-1-10.
 * desc:
 */
public class AToolsActivity extends Activity {

    public static final int COPY_SUCCESS = 100;
    public static final int COPY_COMMON_NUMBER_DB_FILE_SUCCESS = 101;
    public static final int COPY_FAILED = 102;

    private TextView tv_address_query;
    private TextView tv_common_address;
    private TextView tv_app_locker;

    private ProgressDialog pd;

    /**
     * 号码归属地数据库文件复制成功则显示“号码归属查询”页面，否则显示错误信息
     * 因为资源文件较大，复制需要使用线程，因此这里采用消息接受处理结果
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case COPY_SUCCESS:
                    //复制成功，进度条关闭，加载查询页面
                    pd.dismiss();
                    loadQueryNumberUI();
                    break;
                case COPY_COMMON_NUMBER_DB_FILE_SUCCESS:
                    //复制成功，进度条关闭，加载常用号码页面
                    pd.dismiss();
                    loadCommonNumberUI();
                    break;
                case COPY_FAILED:
                    //复制失败，进度条关闭，显示错误信息
                    pd.dismiss();
                    Toast.makeText(AToolsActivity.this, "复制数据失败", 0).show();
                    break;
            }
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atoolsactivity_layout);

        tv_address_query = (TextView) findViewById(R.id.tv_address_query);
        tv_common_address = (TextView) findViewById(R.id.tv_common_address);
        tv_app_locker = (TextView) findViewById(R.id.tv_app_locker);

        //号码归属地查询
        tv_address_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //将asset目录下的naddress.db复制到data/data/包名/files/address.db
                final File file = new File(getFilesDir(), "address.db");
                if (file.exists() && file.length() > 0) {
                    //简单判断文件合法性，如果存在且不为空则认为数据库存在，加载号码查询页面
                    loadQueryNumberUI();
                } else {
                    //文件不存在，复制过去
                    pd = new ProgressDialog(AToolsActivity.this);
                    //水平进度条
                    pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    pd.show();
                    new Thread() {
                        @Override
                        public void run() {
                            AssetUtil assetUtil = new AssetUtil(AToolsActivity.this);
                            if (assetUtil.copyFile("naddress.db", file, pd)) {
                                //复制成功
                                Message message = Message.obtain();
                                message.what = COPY_SUCCESS;
                                handler.sendMessage(message);
                            } else {
                                //复制失败
                                Message message = Message.obtain();
                                message.what = COPY_FAILED;
                                handler.sendMessage(message);
                            }
                        }

                        ;
                    }.start();
                }
            }
        });

        //常用号码查询
        tv_common_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //将asset目录下的commonnum.db复制到data/data/包名/files/commonnum.db
                final File file = new File(getFilesDir(), "commonnum.db");
                if (file.exists() && file.length() > 0) {
                    //简单判断文件合法性，如果存在且不为空则认为数据库存在，加载号码查询页面
                    loadCommonNumberUI();
                } else {
                    //文件不存在，复制过去
                    pd = new ProgressDialog(AToolsActivity.this);
                    //水平进度条
                    pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    pd.show();
                    new Thread() {
                        @Override
                        public void run() {
                            AssetUtil assetUtil = new AssetUtil(AToolsActivity.this);
                            if (assetUtil.copyFile("commonnum.db", file, pd)) {
                                //复制成功
                                Message message = Message.obtain();
                                message.what = COPY_COMMON_NUMBER_DB_FILE_SUCCESS;
                                handler.sendMessage(message);
                            } else {
                                //复制失败
                                Message message = Message.obtain();
                                message.what = COPY_FAILED;
                                handler.sendMessage(message);
                            }
                        }

                        ;
                    }.start();
                }
            }
        });

        //程序锁
        tv_app_locker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AToolsActivity.this, AppLockerActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadCommonNumberUI() {
        Intent intent = new Intent(AToolsActivity.this, CommonNumberActivity.class);
        startActivity(intent);
    }

    private void loadQueryNumberUI() {
        Intent intent = new Intent(AToolsActivity.this, NumberQueryActivity.class);
        startActivity(intent);
    }
}