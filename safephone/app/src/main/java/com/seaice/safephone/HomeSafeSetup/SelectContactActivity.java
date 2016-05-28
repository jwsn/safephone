package com.seaice.safephone.HomeSafeSetup;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.seaice.safephone.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by seaice on 2016/3/4.
 */
public class SelectContactActivity extends Activity{
    private static final String TAG = "HomeSafeSetup1";

    private ListView lv;
    private ArrayList<HashMap<String, String>> readContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);
        lv = (ListView) findViewById(R.id.lv_contact);
        readContact = readContact();
        if(readContact.size() <= 0){
            return;
        }

        lv.setAdapter(new SimpleAdapter(this, readContact, R.layout.contact_listview_item, new String[] {"name","phone"}, new int[] {R.id.tv_name, R.id.tv_phone}));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

    }

    /**
     * 读取联系人列表
     * @return
     */
    private ArrayList<HashMap<String, String>> readContact(){
        //首先，从raw_contacts表中读取联系人的id("contact_id");
        //其次，根据contact_id从data表中查询出相应的电话号码和联系人
        //然后，根据mimetype表来区分哪个是联系人，哪个是电话号码，并存入map
        Uri rawContactUri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri dataUri = Uri.parse("content://com.android.contacts/data");

        ArrayList<HashMap<String, String>> contactList = new ArrayList<HashMap<String, String>>();
        Cursor rawContactsCursor = getContentResolver().query(rawContactUri, new String[] {"contact_id"},null, null, null);
        if(rawContactsCursor != null){
            while(rawContactsCursor.moveToNext()){
                String contactId = rawContactsCursor.getString(0);
                Cursor dataCursor= getContentResolver().query(dataUri, new String[] {"data1", "mimetype"}, "contact_id=?",new String[] {contactId}, null);
                if(dataCursor != null){
                    HashMap<String, String> map = new HashMap<String, String>();
                    while(dataCursor.moveToNext()){
                        String data1 = dataCursor.getString(0);
                        String mimetype = dataCursor.getString(1);
                        if("vnd.android.cursor.item/phone_v2".equals(mimetype)){
                            map.put("phone", data1);
                        }else if ("vnd.android.cursor.item/name".equals(mimetype)){
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
