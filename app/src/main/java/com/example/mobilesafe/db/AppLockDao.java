package com.example.mobilesafe.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sing on 14-1-15.
 * desc:对于锁定的app包名存储在数据库中
 */
public class AppLockDao {
    private static final String TAG = "AppLockDao";

    private AppLockDBOpenHelper helper;

    public AppLockDao(Context context) {
        helper = new AppLockDBOpenHelper(context);
    }

    /**
     * 查找指定的包名程序是否被锁定
     *
     * @param packname
     * @return
     */
    public boolean find(String packname) {
        boolean result = false;

        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from applock where packname=?", new String[]{packname});
            if (cursor.moveToFirst()) {
                result = true;
            }
            cursor.close();
            db.close();
        }
        return result;
    }

    /**
     * 数据库中添加一个包名，也就是对这个包名程序进行锁定
     *
     * @param packname
     * @return
     */
    public boolean add(String packname) {
        if (find(packname)) {
            return false;
        }

        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("insert into applock (packname) values (?)", new String[]{packname});
            db.close();
        }

        return find(packname);
    }

    /**
     * 删除某包名，下次不再对其锁定
     *
     * @param packname
     */
    public void delete(String packname) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("delete from applock where packname=?", new String[]{packname});
            db.close();
        }
    }

    /**
     * 查询所有被锁定的应用程序包名
     *
     * @return
     */
    public List<String> findAll() {
        List<String> packnames = new ArrayList<String>();
        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select packname from applock", null);
            while (cursor.moveToNext()) {
                packnames.add(cursor.getString(0));
            }
            cursor.close();
            db.close();
        }

        return packnames;
    }
}
