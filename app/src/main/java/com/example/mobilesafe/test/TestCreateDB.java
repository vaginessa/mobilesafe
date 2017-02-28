package com.example.mobilesafe.test;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.mobilesafe.db.BlackNumberDBOpenHelper;

/**
 * Created by sing on 14-1-17.
 * desc:
 */
public class TestCreateDB extends AndroidTestCase {
    private static final String TAG = "TestCreateDB";

    public void test() throws Exception {
        BlackNumberDBOpenHelper helper = new BlackNumberDBOpenHelper(getContext());
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            Log.i(TAG, "ok");
            db.close();
        }
    }
}
