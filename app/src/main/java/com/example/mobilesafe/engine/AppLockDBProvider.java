package com.example.mobilesafe.engine;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.example.mobilesafe.db.AppLockDao;

/**
 * Created by sing on 14-1-16.
 * desc:
 */
public class AppLockDBProvider extends ContentProvider {
    private static final String TAG = "AppLockDBProvider";
    public static final int ADD = 1;
    public static final int DELETE = 2;

    private AppLockDao dao;

    private static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        matcher.addURI("com.example.mobilesafe.applock", "ADD", ADD);
        matcher.addURI("com.example.mobilesafe.applock", "DELETE", DELETE);
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings2, String s2) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        int result = matcher.match(uri);
        if (result == DELETE) {
            dao.delete(strings[0]);
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return 0;
    }

    @Override
    public boolean onCreate() {
        dao = new AppLockDao(getContext());
        return false;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int result = matcher.match(uri);
        if (result == ADD) {
            String packname = contentValues.getAsString("packname");
            dao.add(packname);
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }
}
