package com.example.mobilesafe.engine;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sing on 13-12-30.
 * desc:
 */
public class ContactinfoProvider {
    private Context context;

    public ContactinfoProvider(Context context) {
        this.context = context;
    }

    public List<ContactInfo> getContactInfos() {
        List<ContactInfo> infos = new ArrayList<ContactInfo>();
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri datauri = Uri.parse("content://com.android.contacts/data");
        Cursor cursor = context.getContentResolver().query(uri, new String[]{"contact_id"}, null, null, null);
        while (cursor.moveToNext()) {
            String id = cursor.getString(0);
            ContactInfo info = new ContactInfo();
            Cursor dataCursor = context.getContentResolver().query(datauri, null, "raw_contact_id=?", new String[]{id}, null);
            while (dataCursor.moveToNext()) {
                if (dataCursor.getString(dataCursor.getColumnIndex("mimetype")).equals("vnd.android.cursor.item/name")) {
                    info.setName(dataCursor.getString(dataCursor.getColumnIndex("data1")));
                } else if (dataCursor.getString(dataCursor.getColumnIndex("mimetype")).equals("vnd.android.cursor.item/phone_v2")) {
                    info.setPhone(dataCursor.getString(dataCursor.getColumnIndex("data1")));
                }
            }

            infos.add(info);
            info = null;
            dataCursor.close();
        }
        cursor.close();
        return infos;
    }
}
