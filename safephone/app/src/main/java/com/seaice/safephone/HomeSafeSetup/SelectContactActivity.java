package com.seaice.safephone.HomeSafeSetup;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.seaice.safephone.R;
import com.seaice.utils.ThreadManager;
import com.seaice.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by seaice on 2016/3/4.
 */
public class SelectContactActivity extends Activity {
    private static final String TAG = "SelectContactActivity";

    private static final int READ_CONTACT_SUCCESS = 1;
    private static final int READ_CONTACT_FAILED = 2;
    private static final String RAW_CONTACT_STR = "content://com.android.contacts/raw_contacts";
    private static final String DATA_URI_STR = "content://com.android.contacts/data";

    private ListView listView;
    private ArrayList<HashMap<String, String>> readContact;
    private Runnable readContactRunnable;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case READ_CONTACT_SUCCESS: {
                    listView.setAdapter(new SimpleAdapter(SelectContactActivity.this, readContact, R.layout.contact_listview_item,
                            new String[]{"name", "phone"}, new int[]{R.id.tv_name, R.id.tv_phone}));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            HashMap<String, String> data = readContact.get(position);
                            String number = data.get("phone");
                            Intent intent = new Intent();
                            intent.putExtra("phone", number);
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        }
                    });
                    break;
                }
                case READ_CONTACT_FAILED: {
                    ToastUtil.showDialog(SelectContactActivity.this, "读取联系人失败");
                    break;
                }
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    private void initView() {
        setContentView(R.layout.activity_select_contact);
        listView = (ListView) findViewById(R.id.lv_contact);
    }

    private void initData() {
        readContactRunnable = new Runnable() {
            @Override
            public void run() {
                readContact = readContact();
                if (readContact.size() > 0) {
                    handler.sendEmptyMessage(READ_CONTACT_SUCCESS);
                }else{
                    handler.sendEmptyMessage(READ_CONTACT_FAILED);
                }
            }
        };

        ThreadManager.getThreadPool().execute(readContactRunnable);
    }

    /**
     * 读取联系人列表
     * 首先，从raw_contacts表中读取联系人的id("contact_id");
     * 其次，根据contact_id从data表中查询出相应的电话号码和联系人
     * 然后，根据mimetype表来区分哪个是联系人，哪个是电话号码，并存入map
     * @return
     */
    private ArrayList<HashMap<String, String>> readContact() {

        Uri rawContactUri = Uri.parse(RAW_CONTACT_STR);
        Uri dataUri = Uri.parse(DATA_URI_STR);

        ArrayList<HashMap<String, String>> contactList = new ArrayList<HashMap<String, String>>();
        Cursor rawContactsCursor = getContentResolver().query(rawContactUri, new String[]{"contact_id"}, null, null, null);
        if (rawContactsCursor != null) {
            while (rawContactsCursor.moveToNext()) {
                String contactId = rawContactsCursor.getString(0);
                Cursor dataCursor = getContentResolver().query(dataUri, new String[]{"data1", "mimetype"}, "contact_id=?", new String[]{contactId}, null);
                if (dataCursor != null) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    while (dataCursor.moveToNext()) {
                        String data1 = dataCursor.getString(0);
                        String mimetype = dataCursor.getString(1);
                        if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
                            map.put("phone", data1);
                        } else if ("vnd.android.cursor.item/name".equals(mimetype)) {
                            map.put("name", data1);
                        }
                    }
                    contactList.add(map);
                    dataCursor.close();
                }
            }
            rawContactsCursor.close();
        }
        return contactList;
    }
}
