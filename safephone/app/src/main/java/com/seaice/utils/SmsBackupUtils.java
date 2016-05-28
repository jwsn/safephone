package com.seaice.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.util.Xml;
import com.seaice.bean.SmsItem;
import com.seaice.service.CallSmsSafeService;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.security.auth.callback.Callback;

/**
 * Created by seaice on 2016/5/6.
 */
public class SmsBackupUtils {

    private static final String TAG = "SmsBackupUtils";

    private static final String SMS = "sms";

    private static final String SMSS = "smss";

    private static final String ADDRESS = "address";

    private static final String DATE = "date";

    private static final String TYPE = "type";

    private static final String BODY = "body";

    private static final String SMS_URI = "content://sms";

    private static final File SMS_PATH = Environment.getExternalStorageDirectory();

    private static final String SMS_BACK_UP_XML = "backupsms.xml";

    public interface SmsBackupCallback {

        /**
         * 短信前设置进度条总数
         *
         * @param total
         */
        public void beforeSmsBackup(int total);

        /**
         * 短信进度过程
         *
         * @param progress
         */
        public void afterSmsBackup(int progress);
    }

    /**
     * 短信备份到xml文件中，耗时操作，应该放在子线程中
     *
     * @param ctx
     */
    public static void smsBackup(Context ctx, SmsBackupCallback callback) {
        /**
         * 1.判断手机上是否有sd卡
         * 2.权限
         * 3.写短信
         */
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            XmlSerializer serializer = Xml.newSerializer();
            File file = new File(SMS_PATH, SMS_BACK_UP_XML);
            Log.e("SmsBackupUtils", Environment.getDataDirectory().toString());
            Log.e("SmsBackupUtils", Environment.getExternalStorageDirectory().toString());
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                serializer.setOutput(fos, "utf-8");
                serializer.startDocument("utf-8", true);

                ContentResolver resolver = ctx.getContentResolver();
                Uri uri = Uri.parse(SMS_URI);
                Cursor cursor = resolver.query(uri, new String[]{ADDRESS, DATE, TYPE, BODY}, null, null, null);
                callback.beforeSmsBackup(cursor.getCount());
                int progress = 0;
                while (cursor.moveToNext()) {
                    serializer.startTag(null, SMS);

                    String address = cursor.getString(cursor.getColumnIndex(ADDRESS));
                    serializer.attribute(null, ADDRESS, address);

                    String date = cursor.getString(cursor.getColumnIndex(DATE));
                    serializer.attribute(null, DATE, date);

                    String type = cursor.getString(cursor.getColumnIndex(TYPE));
                    serializer.attribute(null, TYPE, type);

                    String body = cursor.getString(cursor.getColumnIndex(BODY));
                    serializer.attribute(null, BODY, body);

                    //String body = cursor.getString(cursor.getColumnIndex(BODY));
//                    try {
//                        serializer.attribute(null, BODY, (CryptoUtils.encrypt("123", body)));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }

                    serializer.endTag(null, SMS);
                    progress++;
                    callback.afterSmsBackup(progress);
                    SystemClock.sleep(100);
                }
                cursor.close();
                serializer.endDocument();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            ToastUtil.showDialog(ctx, "短信备份成功");
        } else {
 //           ToastUtil.showDialog(ctx, "没有sd卡");
        }
    }

    /**
     * 恢复短信
     * @param ctx
     */
    public static void rollbackSms(Context ctx, SmsBackupCallback callback) throws Exception {
        ContentResolver cr = ctx.getContentResolver();
        Uri uri = Uri.parse(SMS_URI);
        List<SmsItem> listSms = getSmsItemFromXml(ctx);
        Log.e(TAG, listSms.toString());
        callback.beforeSmsBackup(listSms.size());
        int progress = 0;
        for(SmsItem si : listSms){
            //找到手机上是否有该条短信，没有则恢复
            Cursor cursor = cr.query(uri, new String[]{DATE}, DATE+"=?", new String[]{si.getDate()}, null);
            if(!cursor.moveToNext()){//没有该条短信
                ContentValues values = new ContentValues();
                values.put(ADDRESS, si.getAddress());
                values.put(TYPE, si.getType());
                values.put(DATE, si.getDate());
                values.put(BODY, si.getBody());
                Uri inser = Uri.parse(SMS_URI + "/");
                cr.insert(inser, values);
            }
            progress++;
            Log.e(TAG, ""+progress);
            callback.afterSmsBackup(progress);
            SystemClock.sleep(100);
            cursor.close();
        }
    }

    /**
     * 解析备份的短信到smsList
     * @param ctx
     * @return
     */
    private static List<SmsItem> getSmsItemFromXml(Context ctx){
        List<SmsItem> listSmsItem;
        listSmsItem = null;// new ArrayList<>();
        XmlPullParser parser = Xml.newPullParser();
        SmsItem smsItem = null;

        File file = new File(SMS_PATH, SMS_BACK_UP_XML);
        if(!file.exists()){
            //ToastUtil.sho wDialog(ctx, "没有短信可恢复");
        }
        try {
            FileInputStream fis = new FileInputStream(file);
            parser.setInput(fis, "utf-8");
            int event = parser.getEventType();
            while(event != XmlPullParser.END_DOCUMENT){
                switch (event){
                    case XmlPullParser.START_DOCUMENT:
                        listSmsItem = new ArrayList<>();
                        break;
                    case XmlPullParser.START_TAG:
                        String name = parser.getName();
                        if(SMS.equals(parser.getName())){
                            Log.e(TAG, "start tag");
                            smsItem = new SmsItem();
                            smsItem.setAddress(parser.getAttributeValue(0));
                            smsItem.setDate(parser.getAttributeValue(1));
                            smsItem.setType(parser.getAttributeValue(2));
                            smsItem.setBody(parser.getAttributeValue(3));
                            Log.e(TAG, smsItem.toString());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        String name1 = parser.getName();
                        if(SMS.equals(parser.getName())){
                            Log.e(TAG, "end Tag");
                            listSmsItem.add(smsItem);
                        }
                        break;
                }
                event = parser.next();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e(TAG, listSmsItem.toString());
        return listSmsItem;
    }
}
