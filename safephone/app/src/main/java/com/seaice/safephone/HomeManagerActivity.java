package com.seaice.safephone;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seaice.adapter.MyListViewBaseAdapter;
import com.seaice.bean.AppInfo;
import com.seaice.utils.AppInfosUtils;
import com.seaice.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeManagerActivity extends Activity {
    private static final String TAG = "HomeManagerActivity";

    private TextView tv_avail_memory;
    private TextView tv_sdcard_memory;
    private ListView lv_package;
    private TextView tv_show;
    private PopupWindow popupWindow;

    private static final int GET_APP_INFO = 0;

    private List<AppInfo> listAppInfos;
    private List<AppInfo> listUserApp;
    private List<AppInfo> listSysApp;
    private MyListViewBaseAdapter<AppInfo> adapter;
    private AppInfo clickAppInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_manager);

        tv_avail_memory = (TextView) findViewById(R.id.tv_avail_memory);
        tv_sdcard_memory = (TextView) findViewById(R.id.tv_sdcard_memory);
        lv_package = (ListView) findViewById(R.id.lv_package);
        tv_show = (TextView) findViewById(R.id.tv_show);

        tv_show.setVisibility(View.INVISIBLE);

        long rom_free_space = Environment.getDataDirectory().getFreeSpace();
        //long sd_free_space = Environment.getExternalStorageDirectory().getFreeSpace();
        String sd_free_space = getExternalTotalSpace();

        tv_avail_memory.setText("内存可用：" + android.text.format.Formatter.formatFileSize(this, rom_free_space));
        //tv_sdcard_memory.setText("sd卡可用：" + android.text.format.Formatter.formatFileSize(this, sd_free_space));
        tv_sdcard_memory.setText("sd卡可用" + sd_free_space);

        initData();

        //UnstalledReceiver receiver = new UnstalledReceiver();
        //IntentFilter intentFilter = new IntentFilter(Intent.ACTION_PACKAGE_FULLY_REMOVED);
        //registerReceiver(receiver, intentFilter);
    }

    public android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_APP_INFO:
                    if (adapter == null) {
                        adapter = new ListViewAdapter(HomeManagerActivity.this, listAppInfos);
                        lv_package.setVisibility(View.VISIBLE);
                        lv_package.setAdapter(adapter);
                        lv_package.setOnScrollListener(new ListViewScrollerListener());
                        lv_package.setOnItemClickListener(new ListViewItemClickListener());
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    };

    private class ListViewItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Object obj = lv_package.getItemAtPosition(position);

            if (obj != null && obj instanceof AppInfo) {

                clickAppInfo = (AppInfo) obj;

                dimissPopupWindow();

                View contentView = View.inflate(HomeManagerActivity.this, R.layout.item_popup_window, null);

                ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                sa.setDuration(300);
                popupWindow = new PopupWindow(contentView, -2, -2);
                //获取view展示到窗体上
                int[] location = new int[2];
                view.getLocationInWindow(location);
                popupWindow.showAtLocation(parent, Gravity.LEFT + Gravity.TOP, 70, location[1]);
                //必须设置背景，不然没有动画
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                contentView.startAnimation(sa);
            }
        }
    }

    /**
     * 卸载软件
     * @param view
     */
    public void tv_unstalled(View view) {

        if(!clickAppInfo.isUserApp()){
            ToastUtil.showDialog(this, "系统应用需要Root权限后才能卸载");
            return;
        }else{
            listAppInfos.remove(clickAppInfo);
            listUserApp.remove(clickAppInfo);
        }

        Intent intent = new Intent("android.intent.action.DELETE", Uri.parse("package:"+clickAppInfo.getApkPackageName()));
        this.startActivityForResult(intent, 0);
        //Intent intent = new Intent();
        //intent.setAction("android.intent.action.VIEW");
        //intent.addCategory("android.intent.category.DEFAULT");
        //intent.setData(Uri.parse("package:"+clickAppInfo.getApkPackageName()));
        //startActivity(intent);
        dimissPopupWindow();
    }

    /**
     * 运行软件
     *
     * @param view
     */
    public void tv_run(View view) {
        Intent intent = this.getPackageManager().getLaunchIntentForPackage(clickAppInfo.getApkPackageName());
        this.startActivity(intent);
        dimissPopupWindow();
    }

    /**
     * 分享按钮
     *
     * @param view
     */
    public void tv_share(View view) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.putExtra("android.intent.extra.SUBJECT", "分享");
        intent.putExtra("android.intent.extra.TEXT", "hi, 推荐您使用软件：" + clickAppInfo.getApkName());
        this.startActivity(Intent.createChooser(intent, "分享"));
        dimissPopupWindow();
    }

    /**
     * 程序细节
     *
     * @param view
     */
    public void tv_detail(View view) {
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:"+clickAppInfo.getApkPackageName()));
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case 0:
                Log.e("HomeAppManager", "onActivityResult");
                adapter.notifyDataSetChanged();
                break;

            default:
            break;
        }
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
            if (listSysApp == null && listUserApp == null) {
                return;
            }

            dimissPopupWindow();

            tv_show.setVisibility(View.VISIBLE);
            if (firstVisibleItem < listUserApp.size() + 1) {
                tv_show.setText("" + "用户程序: " + (listUserApp.size() + 1));
            } else {
                tv_show.setText("" + "系统程序：" + (listSysApp.size() + 1));
            }
        }
    }

    /**
     * 获取数据
     */
    private void initData() {
        new Thread() {
            @Override
            public void run() {
                listAppInfos = AppInfosUtils.getAppInfos(HomeManagerActivity.this);

                listUserApp = new ArrayList<AppInfo>();
                listSysApp = new ArrayList<AppInfo>();
                for (AppInfo a : listAppInfos) {
                    if (a.isUserApp()) {
                        listUserApp.add(a);
                    } else {
                        listSysApp.add(a);
                    }
                }
                handler.sendEmptyMessage(GET_APP_INFO);
            }
        }.start();
    }

    /**
     * 返回指定路径的空间大小
     *
     * @param path
     * @return
     */
    public String getTotalSpace(String path) {
        StatFs statFs = new StatFs(path);
        long blockSize = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = statFs.getBlockSizeLong();
        } else {
            blockSize = statFs.getBlockSize();
        }
        long totalBlocks = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            totalBlocks = statFs.getBlockCountLong();
        } else {
            totalBlocks = statFs.getBlockCount();
        }
        return android.text.format.Formatter.formatFileSize(this, blockSize * totalBlocks);
    }

    /**
     * listview adapter
     */
    private class ListViewAdapter extends MyListViewBaseAdapter<AppInfo> {

        public ListViewAdapter(Context context, List<AppInfo> list) {
            super(context, list);
        }

        @Override
        public int getCount() {
            return listSysApp.size() + 1 + listUserApp.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            AppInfo appInfo = null;
            if (position == 0) {
                return null;
            } else if (position == listUserApp.size() + 1) {
                return null;
            } else {
                if (position < listUserApp.size() + 1) {
                    //把多出来的特殊条目减去
                    appInfo = listUserApp.get(position - 1);
                } else if (position > listUserApp.size() + 1) {
                    appInfo = listSysApp.get(position - listUserApp.size() - 2);
                }
            }
            return appInfo;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            /**
             * 特殊条目
             */
            if (position == 0) {
                TextView tv = new TextView(HomeManagerActivity.this);
                tv.setText("用户程序: " + (listUserApp.size() + 1));
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.GRAY);
                return tv;
            } else if (position == listUserApp.size() + 1) {
                TextView tv = new TextView(HomeManagerActivity.this);
                tv.setText("系统程序：" + (listSysApp.size() + 1));
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.GRAY);
                return tv;
            }

            //获取条目的位置
            AppInfo appInfo = null;
            if (position < listUserApp.size() + 1) {
                //把多出来的特殊条目减去
                appInfo = listUserApp.get(position - 1);
            } else if (position > listUserApp.size() + 1) {
                appInfo = listSysApp.get(position - listUserApp.size() - 2);
            }

            Holder holder;
            if (convertView != null && convertView instanceof RelativeLayout) {
                holder = (Holder) convertView.getTag();
            } else {
                holder = new Holder();
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.app_manager_listview_item, null, false);
                holder.iv = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.tv_apk_name = (TextView) convertView.findViewById(R.id.tv_apk_name);
                holder.tv_rom = (TextView) convertView.findViewById(R.id.tv_rom);
                holder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
                convertView.setTag(holder);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.iv.setBackground(appInfo.getIcon());
            } else {
                holder.iv.setBackgroundResource(R.drawable.ic_launcher);
            }
            holder.tv_apk_name.setText(appInfo.getApkName());
            holder.tv_size.setText(android.text.format.Formatter.formatFileSize(HomeManagerActivity.this, appInfo.getApkSize()));
            if (appInfo.isRom()) {
                holder.tv_rom.setText("手机内存");
            } else {
                holder.tv_rom.setText("SD卡内存");
            }
            return convertView;
        }
    }

    private static class Holder {
        public ImageView iv;
        public TextView tv_apk_name;
        public TextView tv_rom;
        public TextView tv_size;
    }

    /**
     * 获取SD卡的存储空间
     *
     * @return
     */
    public String getExternalTotalSpace() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.e(TAG, "MOUNTED");
            File pathFile = Environment.getExternalStorageDirectory();
            return getTotalSpace(pathFile.getPath());
        } else {
            return "";
        }
    }

    @Override
    protected void onDestroy() {
        dimissPopupWindow();
        super.onDestroy();
    }

    /**
     * 销毁popupwindow
     */
    private void dimissPopupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    /*
    private class UnstalledReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("HomeAppManager", "onReceive");
            adapter.notifyDataSetChanged();
        }
    }*/
}
