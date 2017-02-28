package com.example.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sing on 14-1-17.
 * desc:
 */
public class BlackNumberDBOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = "BlackNumberDBOpenHelper";

    public BlackNumberDBOpenHelper(Context context) {
        super(context, "blacknumber.db", null, 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }

    /**
     * 黑名单号码表的结构：_id，number（号码），mode（拦截号码）
     *
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table blacknumber (_id integer primary key autoincrement, number varchar(20), mode integer)");
    }
}
