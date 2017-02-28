package com.example.mobilesafe.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mobilesafe.engine.BlackNumber;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sing on 14-1-17.
 * desc:
 */
public class BlackNumberDao {
    private static final String TAG = "BlackNumberDao";

    public static final int STOP_SMS = 1;
    public static final int STOP_CALL = 2;
    public static final int STOP_SMSCALL = 4;

    private BlackNumberDBOpenHelper helper;

    public BlackNumberDao(Context context) {
        helper = new BlackNumberDBOpenHelper(context);
    }

    public boolean find(String number) {
        boolean result = false;

        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from blacknumber where number = ?", new String[]{number});
            if (cursor.moveToFirst()) {
                result = true;
            }
            cursor.close();
            db.close();
        }

        return result;
    }

    public int findNumberMode(String number) {
        int result = -1;

        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select mode from blacknumber where number = ?", new String[]{number});
            if (cursor.moveToFirst()) {
                result = cursor.getInt(0);
            }
            cursor.close();
            db.close();
        }

        return result;
    }

    public boolean add(String number, int mode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("insert into blacknumber (number,mode) values(?,?)", new Object[]{number, mode});
            db.close();
        }

        return find(number);
    }

    public void delete(String number) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("delete from blacknumber where number=?", new String[]{number});
            db.close();
        }
    }

    public void update(String oldnumber, String newnumber, int mode) {
        if (newnumber.isEmpty()) {
            newnumber = oldnumber;
        }

        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("update blacknumber set number=?,mode=? where number=?", new Object[]{newnumber, mode, oldnumber});
            db.close();
        }
    }

    public List<BlackNumber> findAll() {
        List<BlackNumber> numbers = new ArrayList<BlackNumber>();

        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select number,mode from blacknumber", null);
            while (cursor.moveToNext()) {
                BlackNumber blackNumber = new BlackNumber();
                blackNumber.setNumber(cursor.getString(0));
                blackNumber.setMode(cursor.getInt(1));
                numbers.add(blackNumber);
                blackNumber = null;
            }
            cursor.close();
            db.close();
        }

        return numbers;
    }
}
