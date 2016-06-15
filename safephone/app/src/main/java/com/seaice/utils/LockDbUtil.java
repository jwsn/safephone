package com.seaice.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.seaice.constant.GlobalConstant;
import com.seaice.db.SqliteDbHelper;

/**
 * Created by seaice on 2016/3/31.
 */
public class LockDbUtil {
    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;
    private static final String APP_NAME = "app_name";
    private static final String IS_LOCK = "is_lock";

    //锁
    private static final byte[] lock = new byte[0];

    public LockDbUtil(Context ctx) {
        dbHelper = new SqliteDbHelper(ctx, GlobalConstant.DB_NAME);
        db = dbHelper.getWritableDatabase();
    }

    public void closeDb() {
        synchronized (lock) {
            if (db.isOpen()) {
                db.close();
            }
        }
    }

    /**
     * 把app加锁标志存入数据库
     * @param app_name
     * @param is_lock
     */
    public void addLock(String app_name, int is_lock) {
        synchronized (lock) {
            if (db.isOpen()) {
                if (isLock(app_name) == false) {
                    //SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues cv = new ContentValues();
                    cv.put(APP_NAME, app_name);
                    cv.put(IS_LOCK, is_lock);
                    db.insert(GlobalConstant.DB_LOCK_TABLE, null, cv);
                    //db.close();
                }
            }
        }
    }

    /**
     * 删除这个app的加锁
     *
     * @param app_name
     */
    public void delete(String app_name) {
        synchronized (lock) {
            //SQLiteDatabase db = dbHelper.getWritableDatabase();
            if (db.isOpen()) {
                db.delete(GlobalConstant.DB_LOCK_TABLE, APP_NAME + "=?", new String[]{app_name});
            }//db.close();
        }
    }

    public void update(String app_name, int is_lock) {
        //SQLiteDatabase db = dbHelper.getWritableDatabase();
        synchronized (lock) {
            if (db.isOpen()) {
                ContentValues cv = new ContentValues();
                cv.put(IS_LOCK, is_lock);
                db.update(GlobalConstant.DB_LOCK_TABLE, cv, app_name + "=?", new String[]{APP_NAME});
                return;
            }
        }
    }

    /**
     * 判断这个app是否已经加锁
     *
     * @param app_name app的名字
     * @return
     */
    public boolean isLock(String app_name) {
        boolean ret = false;
        synchronized (lock) {
            if (db.isOpen()) {
                //SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.query(GlobalConstant.DB_LOCK_TABLE, new String[]{IS_LOCK}, APP_NAME + "=?", new String[]{app_name}, null, null, null);
                if (!cursor.isClosed() && cursor.moveToNext()) {
                    ret = true;
                }
                cursor.close();
            }
        }
        //db.close();
        return ret;
    }

    /**
     * 找出所有的加锁程序
     *
     * @return
     */
    public int findLockNum() {
        int ret = 0;
        synchronized (lock) {
            if (db.isOpen()) {
                //SQLiteDatabase db = dbHelper.getReadableDatabase();
                String sql = "select * from " + GlobalConstant.DB_LOCK_TABLE;
                Cursor cursor = db.rawQuery(sql, null);
                ret = cursor.getCount();
//        while (cursor.moveToNext()) {
//            ret++;
//        }
                cursor.close();
            }
        }
        //db.close();
        return ret;
    }
}
