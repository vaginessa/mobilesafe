package com.example.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafe.db.BlackNumberDao;
import com.example.mobilesafe.engine.BlackNumber;

import java.util.List;

/**
 * Created by sing on 14-1-17.
 * desc:
 */
public class CallSafeActivity extends Activity {

    public static final String TAG = "CallSafeActivity";

    public static final int LOAD_DATA_SUCCESS = 1;

    public static final int STOP_SMS = 1;
    public static final int STOP_CALL = 2;
    public static final int STOP_SMSCALL = 4;

    private View ll_callsafe_loading;
    private ListView lv_blacknumbers;
    private Button bt_add_blacknumber;

    //显示黑名单号码的适配器对象
    private BlackNumberAdapter adapter;
    private BlackNumberDao dao;
    private List<BlackNumber> blackNumbers;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == LOAD_DATA_SUCCESS) {
                ll_callsafe_loading.setVisibility(View.INVISIBLE);
                adapter = new BlackNumberAdapter();
                lv_blacknumbers.setAdapter(adapter);
            }
            super.handleMessage(msg);
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.callsafe_layout);

        ll_callsafe_loading = findViewById(R.id.ll_callsafe_loading);
        ll_callsafe_loading.setVisibility(View.VISIBLE);
        lv_blacknumbers = (ListView) findViewById(R.id.lv_blacknumbers);
        bt_add_blacknumber = (Button) findViewById(R.id.bt_add_blacknumber);
        bt_add_blacknumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBlackNumberDialog(0, 0);
            }
        });

        dao = new BlackNumberDao(this);

        // 1.为lv_call_sms_safe注册一个上下文菜单
        registerForContextMenu(lv_blacknumbers);

        new Thread() {
            @Override
            public void run() {
                blackNumbers = dao.findAll();
                Message msg = Message.obtain();
                msg.what = LOAD_DATA_SUCCESS;
                handler.sendMessage(msg);
            }
        }.start();

    }

    private class BlackNumberAdapter extends BaseAdapter {
        private static final String TAG = "BlackNumberAdapter";

        @Override
        public int getCount() {
            return blackNumbers.size();
        }

        @Override
        public Object getItem(int i) {
            return blackNumbers.get(i);
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
                v = View.inflate(getApplicationContext(), R.layout.callsafe_item, null);
                holder = new ViewHolder();
                holder.tv_number = (TextView) v.findViewById(R.id.tv_callsafe_item_name);
                holder.tv_mode = (TextView) v.findViewById(R.id.tv_callsafe_item_mode);
                v.setTag(holder);
            } else {
                v = view;
                holder = (ViewHolder) view.getTag();
            }

            BlackNumber blackNumber = blackNumbers.get(i);
            holder.tv_number.setText(blackNumber.getNumber());
            int mode = blackNumber.getMode();
            if (mode == STOP_SMS) {
                holder.tv_mode.setText("拦截短信");
            } else if (mode == STOP_CALL) {
                holder.tv_mode.setText("拦截电话");
            } else {
                holder.tv_mode.setText("拦截全部");
            }

            return v;
        }
    }

    private static class ViewHolder {
        TextView tv_number;
        TextView tv_mode;
    }

    // 2.重写创建上下文菜单的方法
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //设置长按Item后要显示的布局
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.call_safe_menu, menu);
    }

    // 3.响应上下文菜单的点击事件
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //获取到Item对应的对象
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = (int) info.id; // 当前上下文菜单对应的listview里面的哪一个条目
        switch (item.getItemId()) {
            case R.id.item_delete:
                Log.i(TAG, "删除黑名单记录");
                deleteBlackNumber(position);
                return true;
            case R.id.item_update:
                Log.i(TAG, "更新黑名单记录");
                updateBlackNumber(position);

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * 更新黑名单号码
     *
     * @param position
     */
    private void updateBlackNumber(int position) {
        showBlackNumberDialog(1, position);
    }

    /**
     * 删除一条黑名单记录
     *
     * @param position
     */
    private void deleteBlackNumber(int position) {
        BlackNumber blackNumber = (BlackNumber) lv_blacknumbers.getItemAtPosition(position);
        String number = blackNumber.getNumber();
        dao.delete(number); // 删除了 数据库里面的记录
        blackNumbers.remove(blackNumber);// 删除当前listview里面的数据.
        adapter.notifyDataSetChanged();
    }

    /**
     * 显示添加黑名单时的添加对话框或者修改对话框（两者共用同一个对话框）
     *
     * @param flag     0 代表添加， 1 代表修改
     * @param position 被修改的Item在窗体中的位置。如果添加 数据，添加的数据可以为空
     */
    private void showBlackNumberDialog(final int flag, final int position) {

        //获得一个窗体构造器
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //将添加号码的布局文件转换成一个View
        View dialogview = View.inflate(this, R.layout.add_black_number, null);

        //获取输入黑名单号码的EditText
        final EditText et_number = (EditText) dialogview.findViewById(R.id.et_add_black_number);

        //获取到弹出的对话框中的各个组件
        final CheckBox cb_phone = (CheckBox) dialogview.findViewById(R.id.cb_block_phone);
        final CheckBox cb_sms = (CheckBox) dialogview.findViewById(R.id.cb_block_sms);
        TextView tv_title = (TextView) dialogview.findViewById(R.id.tv_black_number_title);

        if (flag == 1) {//修改黑名单数据
            tv_title.setText("修改");
            //将要修改的黑名单号码回显到号码输入框中
            BlackNumber blackNumber = (BlackNumber) lv_blacknumbers.getItemAtPosition(position);
            String oldnumber = blackNumber.getNumber();
            et_number.setText(oldnumber);
            int m = blackNumber.getMode();
            //通过拦截模式来指定Checkbox的勾选状态
            if (m == STOP_SMS) {            //短信拦截
                cb_sms.setChecked(true);
                cb_phone.setChecked(false);
            } else if (m == STOP_CALL) {    //电话拦截
                cb_phone.setChecked(true);
                cb_sms.setChecked(false);
            } else {//电话与短信拦截
                cb_phone.setChecked(true);
                cb_sms.setChecked(true);
            }
        }
        //将转换的布局文件添加到窗体上
        builder.setView(dialogview);
        //窗体对话框中的“确定”按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                //获取到输入的号码，并将号码前后的空格清除掉
                String number = et_number.getText().toString().trim();
                //flag=1代表的是修改，处理更改的时候 避免更改出来相同的电话号码.
                if (flag == 1 && dao.find(number)) {
                    Toast.makeText(getApplicationContext(), "要修改的电话号码已经存在", 0).show();
                    return;
                }
                //如果输入的是空，则直接结束当前方法
                if (number.isEmpty()) {
                    return;
                } else {//输入的号码不为空
                    // 添加结果。如果添加成功 ，需要通知界面更新黑名单数据。默认的是添加失败
                    boolean result = false;
                    BlackNumber blacknumber = new BlackNumber();
                    blacknumber.setNumber(number);
                    //电话拦截狂和短信拦截狂都被选中的话，拦截模式应该为2
                    if (cb_phone.isChecked() && cb_sms.isChecked()) {
                        if (flag == 0) {//flag=1表示是添加黑名单号码
                            result = dao.add(number, STOP_SMSCALL);
                            blacknumber.setMode(STOP_SMSCALL);
                        } else {//修改黑名单号码
                            //获取到要修改的Item对象
                            BlackNumber blackNumber = (BlackNumber) lv_blacknumbers.getItemAtPosition(position);
                            //更新数据库中要修改的那条数据
                            dao.update(blackNumber.getNumber(), number, STOP_SMSCALL);
                            blackNumber.setMode(STOP_SMSCALL);
                            blackNumber.setNumber(number);
                            //通知适配器重新显示数据（此时，界面上的数据被刷新）
                            adapter.notifyDataSetChanged();
                        }
                    } else if (cb_phone.isChecked()) {//电话拦截，拦截模式为0
                        if (flag == 0) {//添加黑名单数据
                            result = dao.add(number, STOP_CALL);
                            blacknumber.setMode(STOP_CALL);
                        } else {//修改黑名单数据
                            //获取到要修改的Item对象
                            BlackNumber blackNumber = (BlackNumber) lv_blacknumbers.getItemAtPosition(position);
                            //更新数据库中要修改的那条数据
                            dao.update(blackNumber.getNumber(), number, STOP_CALL);
                            blackNumber.setMode(STOP_CALL);
                            blackNumber.setNumber(number);
                            //通知适配器重新显示数据（此时，界面上的数据被刷新）
                            adapter.notifyDataSetChanged();

                        }
                    } else if (cb_sms.isChecked()) {//拦截模式为短信拦截（对应的数字为1）
                        if (flag == 0) {//添加黑名单数据
                            result = dao.add(number, STOP_SMS);
                            blacknumber.setMode(STOP_SMS);
                        } else {//修改黑名单数据
                            //获取到要修改的Item对象
                            BlackNumber blackNumber = (BlackNumber) lv_blacknumbers.getItemAtPosition(position);
                            //更新数据库中要修改的那条数据
                            dao.update(blackNumber.getNumber(), number, STOP_SMS);
                            blackNumber.setMode(STOP_SMS);
                            blackNumber.setNumber(number);
                            //通知适配器重新显示数据（此时，界面上的数据被刷新）
                            adapter.notifyDataSetChanged();
                        }
                    } else {//没有选择任何拦截模式
                        Toast.makeText(getApplicationContext(), "拦截模式不能为空", 0).show();
                        return;
                    }
                    if (result) {//添加或修改数据成功，此时需要更新界面列表中的数据
                        //将新添加的数据添加到集合中，因为适配器是从集合中取数据的
                        blackNumbers.add(blacknumber);
                        //通知适配器重新显示数据（此时，界面上的数据被刷新）
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
        //窗体对话框中的“取消按钮”对应的点击事件
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        //创建并显示出窗体对话框
        builder.create().show();
    }
}