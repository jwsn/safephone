package com.seaice.safephone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.seaice.adapter.BaseHolder;
import com.seaice.adapter.MyListViewBaseAdapter;
import com.seaice.bean.CacheInfo;
import com.seaice.bean.TrafficInfo;
import com.seaice.safephone.cache.CacheInfoProvider;
import com.seaice.utils.TextFormater;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class TrafficActivity extends Activity {
    private static final String TAG = "TrafficActivity";

    private TextView tv_2g3g4g_total;
    private TextView tv_wifi_total;
    private ListView listView;
    private List<TrafficInfo> trafficInfos;

    private PackageManager pm;

    private Timer timer;
    private TimerTask timerTask;

    private TrafficAdapter adapter;

    private static final int FINISH = 1;
    private static final int REFRESH = 2;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FINISH:
                    adapter = new TrafficAdapter(TrafficActivity.this, trafficInfos);
                    listView.setAdapter(adapter);
                    startTimerRefrsh();
                    break;
                case REFRESH:
                    if (adapter != null) {
                        Log.e(TAG, "REFRESH");
                        adapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic);
        initData();
    }

    private void startTimerRefrsh() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(REFRESH);
            }
        };
        timer.schedule(timerTask, 1000, 3000);
    }

    @Override
    protected void onStart() {

        super.onStart();
    }

    private void initViewItem() {
        tv_2g3g4g_total = (TextView) findViewById(R.id.tv_2g3g4g_total);
        tv_wifi_total = (TextView) findViewById(R.id.tv_wifi_total);
        listView = (ListView) findViewById(R.id.lv_traffic);
    }

    private void initData() {
        initViewItem();
        //拿到2G、3G的总共接收的数据大小
        long total_2g3g4g_rx = TrafficStats.getMobileRxBytes();
        long total_2g3g4g_tx = TrafficStats.getMobileTxBytes();
        long total_2g3g4g = total_2g3g4g_rx + total_2g3g4g_tx;
        tv_2g3g4g_total.setText("2g/3g/4g总流量：" + TextFormater.getDataSize(total_2g3g4g));

        //拿到总共接收到的数据大小
        long total_rx = TrafficStats.getTotalRxBytes();
        long total_tx = TrafficStats.getTotalTxBytes();
        long total = total_rx + total_tx;
        long total_wifi = total - total_2g3g4g;
        tv_wifi_total.setText("wifi总流量：" + TextFormater.getDataSize(total_wifi));

        pm = getPackageManager();

        trafficInfos = new ArrayList<>();
        initResolveInfos();
    }

    private void initResolveInfos() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                trafficInfos.clear();
                Intent intent = new Intent();
                intent.setAction("android.intent.action.MAIN");
                intent.addCategory("android.intent.category.LAUNCHER");
                //查询应用入口activity而且是桌面上有图标的activity
                List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
                for (ResolveInfo resolveInfo : resolveInfos) {
                    String appName = resolveInfo.loadLabel(pm).toString();
                    Drawable icon = resolveInfo.loadIcon(pm);
                    String pkgName = resolveInfo.activityInfo.packageName;
                    int uid = 0;
                    try {
                        PackageInfo packageInfo = pm.getPackageInfo(pkgName, 0);
                        uid = packageInfo.applicationInfo.uid;
                        long rx = TrafficStats.getUidRxBytes(uid);
                        long tx = TrafficStats.getUidTxBytes(uid);
                        if (tx == -1 && rx == -1) {
                            continue;
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    TrafficInfo trafficInfo = new TrafficInfo();
                    trafficInfo.setUid(uid);
                    trafficInfo.setIcon(icon);
                    trafficInfo.setAppName(appName);
                    trafficInfos.add(trafficInfo);
                }
                handler.sendEmptyMessage(FINISH);
            }
        }).start();
    }

    private class TrafficAdapter extends MyListViewBaseAdapter<TrafficInfo> {

        public TrafficAdapter(Context context, List<TrafficInfo> list) {
            super(context, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                holder = new Holder(lists);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.refreshView(position);
            return holder.getContentView();
        }
    }

    private class Holder extends BaseHolder<TrafficInfo> {
        public ImageView iv_icon;
        public TextView tv_name;
        public TextView tv_rx;
        public TextView tv_tx;

        protected Holder(List<TrafficInfo> l) {
            super(l);
            initView();
        }

        @Override
        protected void initView() {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_traffic_item, null, false);
            this.iv_icon = (ImageView) view.findViewById(R.id.iv_traffic_icon);
            this.tv_name = (TextView) view.findViewById(R.id.tv_traffic_app);
            this.tv_tx = (TextView) view.findViewById(R.id.tv_tx);
            this.tv_rx = (TextView) view.findViewById(R.id.tv_rx);
            view.setTag(this);
        }

        @Override
        protected void refreshView(int pos) {
            TrafficInfo trafficInfo = trafficInfos.get(pos);
            this.iv_icon.setImageDrawable(trafficInfo.getIcon());
            this.tv_name.setText(trafficInfo.getAppName());
            long rx = TrafficStats.getUidRxBytes(trafficInfo.getUid());
            long tx = TrafficStats.getUidTxBytes(trafficInfo.getUid());
            this.tv_tx.setText(TextFormater.getDataSize(tx));
            this.tv_rx.setText(TextFormater.getDataSize(rx));
            Log.e(TAG, "rx = " + TextFormater.getDataSize(rx));
            Log.e(TAG, "tx = " + TextFormater.getDataSize(tx));
            Log.e(TAG, "APP = " + trafficInfo.getAppName());
            Log.e(TAG, "====================================");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            timerTask = null;
        }
        super.onDestroy();
    }
}
