package com.example.mobilesafe.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

/**
 * Created by sing on 14-1-10.
 * desc:号码归属地查询类
 */
public class NumberAddressDao {

    public static final String SQL_SELECT_MOBILEPREFIX = "select city from address_tb where _id=(select outkey from numinfo where mobileprefix=?)";
    public static final String SQL_SELECT_AREA = "select city from address_tb where _id=(select outkey from numinfo where mobileprefix=?)";

    private Context context;

    public NumberAddressDao(Context context) {
        this.context = context;
    }

    public String getAddress(String number) {
        String address = number;

        File file = new File(context.getFilesDir(), "address.db");
        String path = file.getAbsolutePath();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        if (db.isOpen()) {
            if (number.matches("^1[3458]\\d{9}$")) {
                //匹配手机的前7位
                Cursor cursor = db.rawQuery(SQL_SELECT_MOBILEPREFIX, new String[]{number.substring(0, 7)});
                if (cursor.moveToFirst()) {
                    address = cursor.getString(0);
                }
                cursor.close();
            } else {
                Cursor cursor;
                switch (number.length()) {
                    case 4:
                        address = "模拟器";
                        break;
                    case 7:
                    case 8:
                        //一般是不带区号的本地号码
                        address = "本地号码";
                        break;
                    case 10:
                        //带有三位区号的号码
                        cursor = db.rawQuery(SQL_SELECT_AREA, new String[]{number.substring(0, 3)});
                        if (cursor.moveToFirst()) {
                            address = cursor.getString(0);
                        }
                        cursor.close();
                        break;
                    case 12:
                        //带有四位区号的号码
                        cursor = db.rawQuery(SQL_SELECT_AREA, new String[]{number.substring(0, 4)});
                        if (cursor.moveToFirst()) {
                            address = cursor.getString(0);
                        }
                        cursor.close();
                        break;
                    case 11:
                        //三位区号号码+8位号码或者是四位区号+7位号码
                        cursor = db.rawQuery(SQL_SELECT_AREA, new String[]{number.substring(0, 3)});
                        if (cursor.moveToFirst()) {
                            address = cursor.getString(0);
                        }
                        cursor.close();
                        cursor = db.rawQuery(SQL_SELECT_AREA, new String[]{number.substring(0, 4)});
                        if (cursor.moveToFirst()) {
                            address = cursor.getString(0);
                        }
                        cursor.close();
                        break;
                }
            }
        }


        return address;
    }
}
