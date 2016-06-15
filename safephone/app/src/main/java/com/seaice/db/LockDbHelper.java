//package com.seaice.db;
//
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//
//import com.seaice.constant.GlobalConstant;
//
///**
// * Created by seaice on 2016/3/31.
// */
//public class LockDbHelper extends SQLiteOpenHelper {
//
//    private static final String sql = "create table "+ GlobalConstant.DB_LOCK_TABLE + " (_id integer primary key autoincrement,app_name varchar(20),is_lock integer)";
//    public LockDbHelper(Context context, String db_name){
//        super(context, db_name, null, 1);
//    }
//    public LockDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
//        super(context, name, factory, version);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(sql);
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//    }
//}
