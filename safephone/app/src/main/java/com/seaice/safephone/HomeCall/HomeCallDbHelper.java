package com.seaice.safephone.HomeCall;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.seaice.constant.GlobalConstant;

/**
 * Created by seaice on 2016/3/31.
 */
public class HomeCallDbHelper extends SQLiteOpenHelper {

    private static final String sql = "create table "+ GlobalConstant.DB_BLACKNUM_TABLE + " (_id integer primary key autoincrement,number varchar(20),mode varchar(2))";
    public HomeCallDbHelper(Context context, String db_name){
        super(context, db_name, null, 1);
    }
    public HomeCallDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
