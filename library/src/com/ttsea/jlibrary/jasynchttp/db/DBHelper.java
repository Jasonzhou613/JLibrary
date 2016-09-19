package com.ttsea.jlibrary.jasynchttp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.ttsea.jlibrary.common.JLog;


class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = "DBHelper";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "jasync.db";
    private Context mContext;

    public DBHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.mContext = context;
    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    public static SQLiteDatabase getReadableDatabase(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        JLog.d(TAG, "getReadableDatabase");

        return db;
    }

    public static SQLiteDatabase getWritableDatabase(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        JLog.d(TAG, "getWritableDatabase");

        return db;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        DBConstants.createTables(db);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
