package com.seaice.safephone.HomeCall;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceActivity;

import com.seaice.constant.GlobalConstant;
import com.seaice.safephone.HomeCallActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seaice on 2016/3/31.
 */
public class HomeCallDbUtil {
    private SQLiteOpenHelper dbHelper;

    private static final String NUMBER = "number";
    private static final String MODE = "mode";

    public HomeCallDbUtil(Context ctx) {
        dbHelper = new HomeCallDbHelper(ctx, GlobalConstant.DB_NAME);
    }

    public void addNum(String num, String mode) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NUMBER, num);
        cv.put(MODE, mode);
        db.insert(GlobalConstant.DB_BLACKNUM_TABLE, null, cv);
        db.close();
    }

    public void delete(String num) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(GlobalConstant.DB_BLACKNUM_TABLE, NUMBER + "=?", new String[]{num});
        db.close();
    }

    public void update(String num, String mode) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MODE, mode);
        db.update(GlobalConstant.DB_BLACKNUM_TABLE, cv, NUMBER + "=?", new String[]{num});
        return;
    }

    public List<BlackNumInfo> findAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<BlackNumInfo> lists = new ArrayList<BlackNumInfo>();
        String sql="select * from "+GlobalConstant.DB_BLACKNUM_TABLE;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            BlackNumInfo bi = new BlackNumInfo();
            bi.setMode(cursor.getString(cursor.getColumnIndex(MODE)));
            bi.setNum(cursor.getString(cursor.getColumnIndex(NUMBER)));
            lists.add(bi);
        }
        cursor.close();
        db.close();
        return lists;
    }

    public String findModeOfNum(String number) {
        String mode = "";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(GlobalConstant.DB_BLACKNUM_TABLE, new String[]{MODE}, NUMBER+"=?", new String[]{number}, null, null, null);
        if(cursor.moveToNext()){
            mode = cursor.getString(cursor.getColumnIndex(MODE));
        }
        cursor.close();
        db.close();
        return mode;
    }

    public int getTotalNumer(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from " + GlobalConstant.DB_BLACKNUM_TABLE, null);
        cursor.moveToNext();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

    /**
     * 数据库分页查询，从哪个Index开始查询20条数据
     * @param startIndex
     * @return
     */
    public List<BlackNumInfo> findPart(int startIndex){
        try {
            //假装查询很费时
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //查询20条数据
        String sql = "select "+NUMBER+" ,"+MODE+" from "+ GlobalConstant.DB_BLACKNUM_TABLE+" limit 20 offset ?";
        List<BlackNumInfo> infos = new ArrayList<BlackNumInfo>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{startIndex+""});
        while(cursor.moveToNext()){
            BlackNumInfo info = new BlackNumInfo();
            info.setMode(cursor.getString(cursor.getColumnIndex(MODE)));
            info.setNum(cursor.getString(cursor.getColumnIndex(NUMBER)));
            infos.add(info);
        }
        cursor.close();
        db.close();
        return infos;
    }
}
