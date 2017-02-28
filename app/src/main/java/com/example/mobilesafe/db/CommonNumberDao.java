package com.example.mobilesafe.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sing on 14-1-15.
 * desc:
 */
public class CommonNumberDao {
    private static final String TAG = "CommonNumberDao";
    public static final String FILE_DIR = "/data/data/com.example.mobilesafe/files/";

    /**
     * 获取常用号码分组数
     *
     * @return
     */
    public static int getGroupCount() {
        int count = 0;

        String path = FILE_DIR + "commonnum.db";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from classlist", null);
            count = cursor.getCount();
            cursor.close();
            db.close();
        }

        return count;
    }

    /**
     * 获取常用号码分组名
     *
     * @return
     */
    public static List<String> getGroupNames() {
        List<String> groupNames = new ArrayList<String>();
        String path = FILE_DIR + "commonnum.db";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select name from classlist", null);
            while (cursor.moveToNext()) {
                groupNames.add(cursor.getString(0));
            }
            cursor.close();
            db.close();
        }

        return groupNames;
    }

    /**
     * 获取指定分组的名称
     *
     * @param groupPosition
     * @return
     */
    public static String getGroupNameByPosition(int groupPosition) {
        String name = null;

        String path = FILE_DIR + "commonnum.db";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select name from classlist where idx=?", new String[]{(groupPosition + 1) + ""});
            cursor.moveToFirst();
            name = cursor.getString(0);
            cursor.close();
            db.close();
        }

        return name;
    }

    /**
     * 获取指定分组的条目个数
     *
     * @param groupPosition
     * @return
     */
    public static int getChildrenCount(int groupPosition) {
        int count = 0;

        String path = FILE_DIR + "commonnum.db";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from table" + (groupPosition + 1), null);
            count = cursor.getCount();
            cursor.close();
            db.close();
        }

        return count;
    }

    /**
     * 获取指定分组指定条目的名称和电话号码
     *
     * @param groupPosition
     * @param childrenPosition
     * @return
     */
    public static String getChildNameByPosition(int groupPosition, int childrenPosition) {
        String result = null;
        String path = FILE_DIR + "commonnum.db";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select name,number from table" + (groupPosition + 1) + " where _id=?", new String[]{(childrenPosition + 1) + ""});
            if (cursor.moveToFirst()) {
                result = cursor.getString(0) + "\n" + cursor.getString(1);
            }

            cursor.close();
            db.close();
        }

        return result;
    }

    /**
     * 获取某分组下所有的项目名称和电话号码
     *
     * @param groupPosition
     * @return
     */
    public static List<String> getChildrenNamesByPosition(int groupPosition) {
        List<String> results = new ArrayList<String>();
        String path = FILE_DIR + "commonnum.db";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select name,number from table" + (groupPosition + 1), null);
            while (cursor.moveToNext()) {
                results.add(cursor.getString(0) + "\n" + cursor.getString(1));
            }
            cursor.close();
            db.close();
        }

        return results;
    }
}
