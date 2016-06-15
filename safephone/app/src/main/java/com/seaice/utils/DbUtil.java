package com.seaice.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 数据库工具类，归属地查询的数据库
 * Created by seaice on 2016/3/4.
 */
public class DbUtil {

    //注意，必须是这个路径
    private static final String dbPath = "data/data/com.seaice.safephone/files/address.db";
    private static final String sql = "select location from data2 where id=(select outkey from data1 where id=?)";

    private static final String virDbPath = "data/data/com.seaice.safephone/files/antivirus.db";

    public static String getAddress(String num) {
        String address = "未知号码";
        //获取数据库对象
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        //^1[3-8]\d{9}$
        if (num.matches("^1[3-8]\\d{9}$")) {

            Cursor cursor = db.rawQuery(sql, new String[]{num.substring(0, 7)});
            if (cursor.moveToNext()) {
                address = cursor.getString(0);

            }
            cursor.close();
        } else if (num.matches("^\\d+$")) {
            switch (num.length()) {
                case 3:
                    address = "报警电话";
                    break;
                case 4:
                    address = "模拟器";
                    break;
                case 5:
                    address = "客服电话";
                    break;
                case 6:
                case 7:
                case 8:
                    address = "本地电话";
                    break;
                default:
                    if (num.startsWith("0") && num.length() > 10) {
                        Cursor cursor = db.rawQuery("select location from data2 where area=?",
                                new String[]{num.substring(1, 4)});
                        if (cursor.moveToNext()) {
                            address = cursor.getString(0);
                        } else {
                            cursor.close();

                            cursor = db.rawQuery("select location from data2 where area=?",
                                    new String[]{num.substring(1, 3)});
                            if (cursor.moveToNext()) {
                                address = cursor.getString(0);
                            }
                            cursor.close();
                        }
                    }
                    break;
            }
        }
        db.close();
        return address;
    }

    /**
     * 检查是否是病毒
     *
     * @param md5 apk的md5码
     * @return
     */
    public static String checkFileVirus(String md5) {
        String ret = null;
        //获取数据库对象
        SQLiteDatabase db = SQLiteDatabase.openDatabase(virDbPath, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.rawQuery("select desc from datable where md5=?", new String[]{md5});
        if (cursor.moveToNext()) {
            ret = cursor.getString(cursor.getColumnIndex("desc"));
        }
        cursor.close();
        db.close();
        return ret;
    }

    public static void addVirus(String md5, String desc) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(virDbPath, null, SQLiteDatabase.OPEN_READWRITE);
        Cursor cursor = db.rawQuery("select desc from datable where md5=?", new String[]{md5});
        if (cursor.moveToNext() == false) {
            ContentValues c = new ContentValues();
            c.put("md5", md5);
            c.put("desc", desc);
            c.put("name", "aloseljlk.cajlf.cjlaj");
            c.put("type", 6);
            db.insert("datable", null, c);
        }
        db.close();
    }
}
