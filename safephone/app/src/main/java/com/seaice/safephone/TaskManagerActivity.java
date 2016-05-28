package com.seaice.safephone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seaice.adapter.MyListViewBaseAdapter;
import com.seaice.bean.AppInfo;
import com.seaice.bean.ProInfo;
import com.seaice.constant.GlobalConstant;
import com.seaice.safephone.TaskSetting.TaskSettingActivity;
import com.seaice.utils.AppInfosUtils;
import com.seaice.utils.PrefUtil;
import com.seaice.utils.ToastUtil;

import java.io.File;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class TaskManagerActivity extends Activity {
    private static final String TAG = "TaskManagerActivity";

    private TextView tv_running_process_count;
    private TextView tv_memory;
    private ListView lv_process;
    private TextView tv_show;

    private static final int GET_APP_INFO = 0;
    private List<ProInfo> listProInfos;
    private List<ProInfo> listUserProcess;
    private List<ProInfo> listSysProcess;
    private LinearLayout ll_loading;
    private LinearLayout ll_btn;

    private MyListViewBaseAdapter<ProInfo> adapter;

    private ActivityManager mAm = null;
    private int countPro = 0;

    public android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_APP_INFO:
                    ll_loading.setVisibility(View.INVISIBLE);
                    if (adapter == null) {
                        adapter = new ListViewAdapter(TaskManagerActivity.this, listProInfos);
                        lv_process.setAdapter(adapter);
                        lv_process.setVisibility(View.VISIBLE);
                        lv_process.setOnScrollListener(new ListViewScrollerListener());
                        lv_process.setOnItemClickListener(new ListViewItemClickListener());
                        ll_btn.setVisibility(View.VISIBLE);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);
        mAm = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> listRapi = mAm.getRunningAppProcesses();
        countPro = listRapi.size();
        tv_running_process_count = (TextView) findViewById(R.id.tv_running_process_count);
        tv_running_process_count.setText("正在运行的进程数：" + countPro);

        tv_memory = (TextView) findViewById(R.id.tv_memory);
        tv_memory.setText("剩余/总内存：" + getSystemAvailMemory());

        lv_process = (ListView) findViewById(R.id.lv_process);

        tv_show = (TextView) findViewById(R.id.tv_pro_show);
        tv_show.setVisibility(View.INVISIBLE);

        ll_loading = (LinearLayout) findViewById(R.id.pro_loading);
        ll_btn = (LinearLayout) findViewById(R.id.ll_btn);
        initData();
    }

    /**
     * 获取数据
     */
    private void initData() {
        new Thread() {
            @Override
            public void run() {
                listProInfos = AppInfosUtils.getProInfos(TaskManagerActivity.this);
                Log.e(TAG, listProInfos.toString());
                listUserProcess = new ArrayList<ProInfo>();
                listSysProcess = new ArrayList<ProInfo>();
                for (ProInfo a : listProInfos) {
                    if (a != null) {
                        if (a.getIsSysPro() == false) {
                            listUserProcess.add(a);
                        } else {
                            if (!PrefUtil.getBooleanPref(TaskManagerActivity.this, GlobalConstant.PREF_DISPLAY_SYSTEM_PROCESS)) {
                                listSysProcess.add(a);
                            }
                        }
                    }
                }
                handler.sendEmptyMessage(GET_APP_INFO);
            }
        }.start();
    }

    /**
     * listview adapter
     */
    private class ListViewAdapter extends MyListViewBaseAdapter<ProInfo> {

        public ListViewAdapter(Context context, List<ProInfo> list) {
            super(context, list);
        }

        @Override
        public int getCount() {
            int count = 0;
            if (!PrefUtil.getBooleanPref(TaskManagerActivity.this, GlobalConstant.PREF_DISPLAY_SYSTEM_PROCESS)) {
                count = listSysProcess.size() + 1 + listUserProcess.size() + 1;
            }else{
                count = listUserProcess.size() + 1;
            }
            return count;
        }

        @Override
        public Object getItem(int position) {
            ProInfo proInfo = null;
            if (position == 0) {
                return null;
            } else if (position == listUserProcess.size() + 1) {
                return null;
            } else {
                if (position < listUserProcess.size() + 1) {
                    //把多出来的特殊条目减去
                    proInfo = listUserProcess.get(position - 1);
                } else if (position > listUserProcess.size() + 1) {
                    proInfo = listSysProcess.get(position - listUserProcess.size() - 2);
                }
            }
            return proInfo;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            /**
             * 特殊条目
             */
            if (position == 0) {
                TextView tv = new TextView(TaskManagerActivity.this);
                tv.setText("用户程序: " + (listUserProcess.size()));
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.GRAY);
                return tv;
            } else if (position == listUserProcess.size() + 1) {
                if (!PrefUtil.getBooleanPref(TaskManagerActivity.this, GlobalConstant.PREF_DISPLAY_SYSTEM_PROCESS)) {
                    TextView tv = new TextView(TaskManagerActivity.this);
                    tv.setText("系统程序：" + (listSysProcess.size()));
                    tv.setTextColor(Color.WHITE);
                    tv.setBackgroundColor(Color.GRAY);
                    return tv;
                }
            }

            //获取条目的位置
            ProInfo appInfo = null;
            if (position < listUserProcess.size() + 1) {
                //把多出来的特殊条目减去
                appInfo = listUserProcess.get(position - 1);
            } else if (position > listUserProcess.size() + 1) {
                appInfo = listSysProcess.get(position - listUserProcess.size() - 2);
            }

            Holder holder = new Holder();
            if (convertView != null && convertView instanceof RelativeLayout) {
                holder = (Holder) convertView.getTag();
            } else {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.task_manager_listview_item, null, false);
                holder.iv_pro_icon = (ImageView) convertView.findViewById(R.id.iv_pro_icon);
                holder.tv_pro_name = (TextView) convertView.findViewById(R.id.tv_pro_name);
                holder.tv_pro_rom = (TextView) convertView.findViewById(R.id.tv_pro_rom);
                holder.cb_pro = (CheckBox) convertView.findViewById(R.id.cb_pro);
                if (appInfo.getProName().equals(getPackageName())) {
                    holder.cb_pro.setVisibility(View.INVISIBLE);
                }
                convertView.setTag(holder);
            }

            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            //holder.iv_pro_icon.setBackground(appInfo.getIcon());
            holder.iv_pro_icon.setImageDrawable(appInfo.getIcon());
            //} else {
            //holder.iv_pro_icon.setBackgroundResource(R.drawable.ic_launcher);
            //}
            holder.tv_pro_name.setText(appInfo.getProName());
            holder.tv_pro_rom.setText(android.text.format.Formatter.formatFileSize(TaskManagerActivity.this, (appInfo.getProSize() * 1024)));
            holder.cb_pro.setChecked(appInfo.getIsChecked());
            return convertView;
        }
    }

    private static class Holder {
        public ImageView iv_pro_icon;
        public TextView tv_pro_name;
        public TextView tv_pro_rom;
        public CheckBox cb_pro;
    }

    /**
     * 实现ListView的滑动监听
     */
    private class ListViewScrollerListener implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (listSysProcess == null && listUserProcess == null) {
                return;
            }

            tv_show.setVisibility(View.VISIBLE);
            if (firstVisibleItem < listUserProcess.size() + 1) {
                tv_show.setText("" + "用户进程: " + (listUserProcess.size()));
            } else {
                if (!PrefUtil.getBooleanPref(TaskManagerActivity.this, GlobalConstant.PREF_DISPLAY_SYSTEM_PROCESS)) {
                    tv_show.setText("" + "系统进程：" + (listSysProcess.size()));
                }
            }
        }
    }

    private void notifyListviewDataChange() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * listview 按键信息
     */
    private class ListViewItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Object obj = lv_process.getItemAtPosition(position);
            Holder holder = (Holder) view.getTag();
            if (obj != null && obj instanceof ProInfo) {
                ProInfo proInfo = (ProInfo) obj;
                if (proInfo.getProName().equals(getPackageName())) {
                    return;
                }
                if (proInfo.getIsChecked()) {
                    proInfo.setIsChecked(false);
                    holder.cb_pro.setChecked(false);
                } else {
                    proInfo.setIsChecked(true);
                    holder.cb_pro.setChecked(true);
                }
            }
        }
    }

    /**
     * 获取系统可用内存
     *
     * @return
     */
    private String getSystemAvailMemory() {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        mAm.getMemoryInfo(memoryInfo);
        long memSize = memoryInfo.availMem;
        @SuppressLint("NewApi") long allSize = memoryInfo.totalMem;
        String availMemoryStr = Formatter.formatFileSize(this, memSize);
        String totalMemStr = Formatter.formatFileSize(this, allSize);
        return availMemoryStr + "/" + totalMemStr;
    }

    /**
     * 全选按钮
     */
    public void btn_all(View view) {

        for (ProInfo p : listUserProcess) {
            p.setIsChecked(true);
        }

        for (ProInfo p : listSysProcess) {
            p.setIsChecked(true);
        }

        notifyListviewDataChange();
    }

    /**
     * 反选
     */
    public void btn_no(View view) {
        for (ProInfo p : listUserProcess) {
            p.setIsChecked(!p.getIsChecked());
        }

        for (ProInfo p : listSysProcess) {
            p.setIsChecked(!p.getIsChecked());
        }
        notifyListviewDataChange();
    }

    /**
     * 清除用户进程，用进程管理器进行杀
     */
    public void btn_clear(View view) {

        int count = 0;
        int mem = 0;
        List<ProInfo> list = new ArrayList<ProInfo>();
        List<ProInfo> listSys = new ArrayList<ProInfo>();
        list.addAll(listUserProcess);
        listSys.addAll(listSysProcess);

        for (ProInfo p : list) {//遍历中的list不能删除元素
            if (!p.getProName().equals("com.seaice.safephone")) {
                if (p.getIsChecked()) {
                    mAm.killBackgroundProcesses(p.getProName());
                    listUserProcess.remove(p);
                    mem += p.getProSize();
                }
            }
        }

        for (ProInfo p : listSys) {
            if (p.getIsChecked()) {
                mAm.killBackgroundProcesses(p.getProName());
                listSysProcess.remove(p);
                mem += p.getProSize();
            }
        }
        notifyListviewDataChange();
        count = list.size() + listSys.size() - listUserProcess.size() - listSysProcess.size();
        ToastUtil.showDialog(this, "共清理 " + count + "个进程" + "释放 " + Formatter.formatFileSize(this, mem * 1024) + "内存");

        tv_running_process_count.setText("正在运行的进程数：" + (listUserProcess.size() + listSysProcess.size()));
        tv_memory.setText("剩余/总内存：" + getSystemAvailMemory());
    }

    /**
     * 设置
     */
    public void btn_setting(View view) {
        Intent intent = new Intent();
        intent.setClass(this, TaskSettingActivity.class);
        startActivity(intent);
        finish();
    }
}
