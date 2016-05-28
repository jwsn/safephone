package com.seaice.safephone;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.seaice.adapter.MyListViewBaseAdapter;
import com.seaice.safephone.HomeCall.BlackNumInfo;
import com.seaice.safephone.HomeCall.HomeCallDbUtil;
import com.seaice.utils.ToastUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class HomeCallActivity extends Activity {
    private static final String TAG = "HomeSafeActivity";

    private HomeCallDbUtil hcDbUtil;
    private List<BlackNumInfo> biLists;
    private ListView lv_home_call;
    private Button btn_add_blacknum;
    private ListViewAdapter adapter;
    private View footerView;
    private LinearLayout ll_loading;

    private static final int FIND_PART_BLACK = 2;
    private static final int FIND_ALL_BLACK = 1;

    private int startIndex;
    private int mDbTotalCount;

    private EditText et_input_black_num;
    private Button popup_btn_ok;
    private Button popup_btn_cancel;
    private CheckBox cb_call;
    private CheckBox cb_sms;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FIND_ALL_BLACK:
                    ll_loading.setVisibility(View.INVISIBLE);
                    lv_home_call.setVisibility(View.VISIBLE);
                    if (adapter == null) {
                        adapter = new ListViewAdapter(HomeCallActivity.this, biLists);
                        lv_home_call.setAdapter(adapter);
                    } else {
                        lv_home_call.removeFooterView(footerView);
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case FIND_PART_BLACK:
                    ll_loading.setVisibility(View.INVISIBLE);
                    lv_home_call.setVisibility(View.VISIBLE);
                    if (adapter == null) {
                        adapter = new ListViewAdapter(HomeCallActivity.this, biLists);
                        lv_home_call.setAdapter(adapter);
                    } else {
                        lv_home_call.removeFooterView(footerView);
                        adapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    break;
            }
            //super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_call);
        //创建数据库
        hcDbUtil = new HomeCallDbUtil(this);
        startIndex = 0;
        biLists = new ArrayList<BlackNumInfo>();

        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);

        footerView = getLayoutInflater().inflate(R.layout.list_view_add_more_footer_view, null);

        lv_home_call = (ListView) findViewById(R.id.lv_home_call);
        lv_home_call.setVisibility(View.INVISIBLE);
        lv_home_call.setOnScrollListener(new MyListViewOnScrollListener());

        btn_add_blacknum = (Button) findViewById(R.id.btn_add_blacknum);
        startThreadQueryDb();
    }

    /**
     * listview的滑动事件
     */
    private class MyListViewOnScrollListener implements AbsListView.OnScrollListener {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                    final int pos = lv_home_call.getLastVisiblePosition();
                    Log.e("SCROLL_STATE_IDLE", "pos: " + pos);
                    int totalCount = biLists.size();
                    if (pos + 1 >= totalCount) {//大于或等于当前的列表的最后一个item
                        if (startIndex < mDbTotalCount) {//每次加载前的index必须必数据库的条目少
                            lv_home_call.addFooterView(footerView);
                            startThreadQueryDb();
                            ToastUtil.showDialog(HomeCallActivity.this, "加载数据中");
                        } else {
                            ToastUtil.showDialog(HomeCallActivity.this, "没有更多数据加载了");
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    }

    /**
     * 启动一个线程去获取数据,必须同步，应该lisview和添加操作的时候，会同时操作
     */
    private synchronized void startThreadQueryDb() {
        //开一个线程从数据库里面查找黑名单。耗时操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                //测试数据
                //biLists = hcDbUtil.findAll();
                //分页查询
                List<BlackNumInfo> list = hcDbUtil.findPart(startIndex);
                if (list != null) {
                    biLists.addAll(list);
                    startIndex += list.size();
                    Message msg = Message.obtain();
                    msg.what = FIND_PART_BLACK;
                    handler.sendMessage(msg);
                    mDbTotalCount = hcDbUtil.getTotalNumer();
                    if(mDbTotalCount < startIndex){
                        startIndex = mDbTotalCount;
                    }
                }
                Log.e("startThreadQueryDb", "startIndex: " + startIndex + ",mDbTotalCount: " + mDbTotalCount);
            }
        }).start();
    }

    /**
     * 添加btn的响应函数
     *
     * @param view
     */
    public void btnAddBlackNum(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflate = LayoutInflater.from(this);
        final AlertDialog dialog = builder.create();
        View popupView = inflate.inflate(R.layout.activity_home_call_add_black, null, false);
        et_input_black_num = (EditText) popupView.findViewById(R.id.et_input_black_num);
        popup_btn_ok = (Button) popupView.findViewById(R.id.popup_btn_ok);
        popup_btn_cancel = (Button) popupView.findViewById(R.id.popup_btn_cancel);
        cb_call = (CheckBox) popupView.findViewById(R.id.cb_call);
        cb_sms = (CheckBox) popupView.findViewById(R.id.cb_sms);
        dialog.setView(popupView);
        popup_btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mode = "";
                String num = et_input_black_num.getText().toString().trim();
                boolean isBlockCall = cb_call.isChecked();
                boolean isBlockSms = cb_sms.isChecked();
                if (!TextUtils.isEmpty(num)) {
                    if (isBlockCall && isBlockSms) {
                        mode = "拦截短信和拦截电话";
                        hcDbUtil.addNum(num, mode);
                    } else if (isBlockCall) {
                        mode = "拦截电话";
                        hcDbUtil.addNum(num, mode);
                    } else if (isBlockSms) {
                        mode = "拦截短信";
                        hcDbUtil.addNum(num, mode);
                    } else {
                        ToastUtil.showDialog(HomeCallActivity.this, "请选择模式");
                        return;
                    }
                } else {
                    ToastUtil.showDialog(HomeCallActivity.this, "请输入正确的号码");
                    return;
                }
                startThreadQueryDb();
                dialog.dismiss();
            }
        });
        popup_btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * LIST VIEW ADAPTER
     */
    private class ListViewAdapter extends MyListViewBaseAdapter<BlackNumInfo> {

        public ListViewAdapter(Context context, List list) {
            super(context, list);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder = new Holder();
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.home_call_listview_item, null, false);
                holder.tv_num = (TextView) convertView.findViewById(R.id.tv_number);
                holder.tv_mod = (TextView) convertView.findViewById(R.id.tv_mode);
                holder.iv_del = (ImageView) convertView.findViewById(R.id.iv_del);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            holder.tv_num.setText(lists.get(position).getNum());
            holder.tv_mod.setText(lists.get(position).getMode());
            holder.iv_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hcDbUtil.delete(lists.get(position).getNum());
                    lists.remove(position);
                    notifyDataSetChanged();
                    mDbTotalCount--;
                    if(mDbTotalCount < startIndex){
                        startIndex = mDbTotalCount;
                    }
                }
            });
            return convertView;
        }
    }

    private static class Holder {
        public TextView tv_num;
        public TextView tv_mod;
        public ImageView iv_del;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
