package com.example.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mobilesafe.engine.ContactInfo;
import com.example.mobilesafe.engine.ContactinfoProvider;

import java.util.List;

/**
 * Created by sing on 13-12-30.
 * desc:
 */
public class SelectContactActivity extends Activity {

    private ListView lv_select_contact;

    private ContactinfoProvider provider;

    private List<ContactInfo> infos;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectcontactactivity_layout);

        lv_select_contact = (ListView) findViewById(R.id.lv_select_contact);
        provider = new ContactinfoProvider(this);
        infos = provider.getContactInfos();
        lv_select_contact.setAdapter(new ContactAdapter());
        lv_select_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ContactInfo info = (ContactInfo) lv_select_contact.getItemAtPosition(i);
                String number = info.getPhone();
                Intent data = new Intent();
                data.putExtra("number", number);
                setResult(0, data);
                finish();
            }
        });
    }

    private class ContactAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return infos.size();
        }

        @Override
        public Object getItem(int i) {
            return infos.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ContactInfo info = infos.get(i);
            TextView tv = new TextView(getApplicationContext());
            tv.setTextSize(24);
            tv.setTextColor(Color.WHITE);
            tv.setText(info.getName() + "\n" + info.getPhone());
            return tv;
        }
    }
}