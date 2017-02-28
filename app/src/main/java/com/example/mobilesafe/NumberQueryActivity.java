package com.example.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafe.db.NumberAddressDao;

/**
 * Created by sing on 14-1-10.
 * desc:
 */
public class NumberQueryActivity extends Activity {

    private EditText et_number;
    private Button bt_query;
    private TextView tv_area;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.numberqueryactivity_layout);

        et_number = (EditText) findViewById(R.id.et_number);
        bt_query = (Button) findViewById(R.id.bt_query);
        tv_area = (TextView) findViewById(R.id.tv_area);

        //点击查询
        bt_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = et_number.getText().toString().trim();
                if (number.isEmpty()) {
                    Toast.makeText(NumberQueryActivity.this, "号码不能为空", 1).show();
                    //震动输入框
                    Animation shake = AnimationUtils.loadAnimation(NumberQueryActivity.this, R.anim.shake);
                    et_number.startAnimation(shake);
                } else {
                    NumberAddressDao numberAddressDao = new NumberAddressDao(NumberQueryActivity.this);
                    String address = numberAddressDao.getAddress(number);
                    tv_area.setText(address);
                }
            }
        });
    }
}