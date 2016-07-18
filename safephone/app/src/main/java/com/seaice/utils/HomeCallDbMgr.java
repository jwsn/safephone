package com.seaice.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.seaice.constant.GlobalConstant;
import com.seaice.db.SqliteDbHelper;
import com.seaice.safephone.HomeCall.BlackNumInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by seaice on 2016/3/31.
 */
public class HomeCallDbMgr {
    private static final String TAG = "HomeCallDbUtil";

    private static final String NUMBER = "number";
    private static final String MODE = "mode";

    private AtomicInteger mOpenCount = new AtomicInteger();

    private static HomeCallDbMgr instance = null;
    private static SqliteDbHelper dbHelper = null;

    private SQLiteDatabase database;

    //必须初始化数据库
    public static synchronized void initDataBase(Context ctx) {
        if (instance == null) {
            synchronized (HomeCallDbMgr.class) {
                if (instance == null) {
                    instance = new HomeCallDbMgr();
                    dbHelper = new SqliteDbHelper(ctx, GlobalConstant.DB_NAME);
                }
            }
        }
    }

    //获取管理实例
    public static synchronized HomeCallDbMgr getInstance() {
        if (instance == null) {
            throw new IllegalStateException(HomeCallDbMgr.class.getSimpleName() + "is not create," +
                    "call initDataBase() method first");
        }
        return instance;
    }

    //打开数据库连接
    private synchronized SQLiteDatabase openWritableDataBase() {
        if (mOpenCount.incrementAndGet() == 1) {
            database = dbHelper.getWritableDatabase();
        }
        return database;
    }

    //关闭数据库
    public synchronized void closeDataBase() {
        if (mOpenCount.decrementAndGet() == 0) {
            database.close();
        }
    }

    private HomeCallDbMgr() {
    }

    public void addNum(String num, String mode) {
        ContentValues cv = new ContentValues();
        cv.put(NUMBER, num);
        cv.put(MODE, mode);
        getInstance().openWritableDataBase().insert(GlobalConstant.DB_BLACKNUM_TABLE, null, cv);
    }

    public void delete(String num) {
        getInstance().openWritableDataBase().delete(GlobalConstant.DB_BLACKNUM_TABLE, NUMBER + "=?", new String[]{num});
    }

    public void update(String num, String mode) {
        ContentValues cv = new ContentValues();
        cv.put(MODE, mode);
        getInstance().openWritableDataBase().update(GlobalConstant.DB_BLACKNUM_TABLE, cv, NUMBER + "=?", new String[]{num});
    }

    public List<BlackNumInfo> findAll() {
        List<BlackNumInfo> lists = new ArrayList<BlackNumInfo>();
        String sql = "select * from " + GlobalConstant.DB_BLACKNUM_TABLE;
        Cursor cursor = getInstance().openWritableDataBase().rawQuery(sql, null);
        while (cursor.moveToNext()) {
            BlackNumInfo bi = new BlackNumInfo();
            bi.setMode(cursor.getString(cursor.getColumnIndex(MODE)));
            bi.setNum(cursor.getString(cursor.getColumnIndex(NUMBER)));
            lists.add(bi);
        }
        cursor.close();
        getInstance().openWritableDataBase().close();
        return lists;
    }

    public String findModeOfNum(String number) {
        Log.e(TAG, "FING MODE OF NUM");
        Log.e(TAG, number+"");
        String mode = "";
        Cursor cursor = getInstance().openWritableDataBase().query(GlobalConstant.DB_BLACKNUM_TABLE,
                new String[]{MODE}, NUMBER + "=?", new String[]{number}, null, null, null);

        if (cursor.moveToNext()) {
            Log.e(TAG, "ADD LOG");
            mode = cursor.getString(cursor.getColumnIndex(MODE));
        }
        Log.e(TAG, "ADD LOG1");
        cursor.close();
        Log.e(TAG, mode);
        return mode;
    }

    public synchronized int getTotalNumer() {
        Cursor cursor = getInstance().openWritableDataBase().rawQuery("select count(*) from " + GlobalConstant.DB_BLACKNUM_TABLE, null);
        cursor.moveToNext();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    /**
     * 数据库分页查询，从哪个Index开始查询20条数据
     *
     * @param startIndex
     * @return
     */
    public synchronized List<BlackNumInfo> findPart(int startIndex) {
        try {
            //假装查询很费时
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //查询20条数据
        String sql = "select " + NUMBER + " ," + MODE + " from " + GlobalConstant.DB_BLACKNUM_TABLE + " limit 20 offset ?";
        List<BlackNumInfo> infos = new ArrayList<BlackNumInfo>();
        Cursor cursor = getInstance().openWritableDataBase().rawQuery(sql, new String[]{startIndex + ""});
        while (cursor.moveToNext()) {
            BlackNumInfo info = new BlackNumInfo();
            info.setMode(cursor.getString(cursor.getColumnIndex(MODE)));
            info.setNum(cursor.getString(cursor.getColumnIndex(NUMBER)));
            infos.add(info);
        }
        cursor.close();
        return infos;
    }
}
