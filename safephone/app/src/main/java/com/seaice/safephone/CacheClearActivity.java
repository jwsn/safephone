package com.seaice.safephone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.seaice.adapter.BaseHolder;
import com.seaice.adapter.MyListViewBaseAdapter;
import com.seaice.bean.CacheInfo;
import com.seaice.safephone.cache.CacheInfoProvider;
import java.util.List;


public class CacheClearActivity extends Activity {
    private static final String TAG = "CacheClearActivity";

    private static final int LOADING = 0;
    private static final int FINISH = 1;

    private ListView lv;
    private LinearLayout ll_load;
    private List<CacheInfo> cacheInfoVector;
    private CacheInfoProvider provider;

    private android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    break;
                case FINISH:
                    cacheInfoVector = provider.getCacheInfos();
                    lv.setAdapter(new CacheAdapter(CacheClearActivity.this, cacheInfoVector));
                    hideLoadingPage();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_clear);
        initData();
        initViewItem();
    }

    private void initViewItem() {
        ll_load = (LinearLayout) findViewById(R.id.ll_loading);
        lv = (ListView) findViewById(R.id.lv_cache);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**
                 * Android2.3打开settings里面的那个应用的详细界面
                 * 后来我又查了一个Android4.1的，也是这样写的，所有应该是2.3之后，都是这样写的了，
                 * 但是这只是猜测，各位有空的可以去下载Android Settings的代码看一下
                 * 这样就可以做成多个版本的适配了
                 * <intent-filter>
                 * <action android:name="android.settings.APPLICATION_DETAILS_SETTINGS" />
                 * <category android:name="android.intent.category.DEFAULT" />
                 * <data android:scheme="package" />
                 * </intent-filter>
                 */
                Intent intent = new Intent();
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse("package:" + cacheInfoVector.get(position).getPkgName()));
                startActivity(intent);
            }
        });
        showLoadingPage();
    }

    private void initData() {
        provider = new CacheInfoProvider(handler, this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "initData");
                provider.initCacheInfos();
            }
        }).start();
    }

    private void showLoadingPage() {
        ll_load.setVisibility(View.VISIBLE);
    }

    private void hideLoadingPage() {
        ll_load.setVisibility(View.GONE);
    }

    private class CacheAdapter extends MyListViewBaseAdapter<CacheInfo> {

        public CacheAdapter(Context context, List<CacheInfo> list) {
            super(context, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            CacheInfo info = cacheInfoVector.get(position);

            Holder holder;
            if (convertView == null) {
                holder = new Holder(cacheInfoVector);
//                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//                convertView = inflater.inflate(R.layout.cache_listview_item, null, false);
//                holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_cache_icon);
//                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_cache_name);
//                holder.tv_cache = (TextView) convertView.findViewById(R.id.tv_cache_size);
//                holder.tv_code = (TextView) convertView.findViewById(R.id.tv_code_size);
//                holder.tv_data = (TextView) convertView.findViewById(R.id.tv_data_size);
//                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
//            Log.e(TAG, info.toString());
//            if(info.getIcon() == null){
//                Log.e(TAG, "HAIBING");
//            }
//
//            if(holder == null){
//                Log.e(TAG, "holer");
//            }
//
//            if(holder.iv_icon == null){
//                Log.e(TAG, "KONG");
//            }
//            holder.iv_icon.setImageDrawable(info.getIcon());
//            holder.tv_name.setText(info.getAppName());
//            holder.tv_cache.setText("缓存大小："+info.getCacheSize());
//            holder.tv_data.setText("数据大小："+info.getDataSize());
//            holder.tv_code.setText("应用大小："+info.getCodeSize());
            holder.refreshView(position);
            return holder.getContentView();
        }
    }

    private class Holder extends BaseHolder<CacheInfo> {
        public ImageView iv_icon;
        public TextView tv_name;
        public TextView tv_data;
        public TextView tv_code;
        public TextView tv_cache;

        protected Holder(List l) {
            super(l);
        }

        @Override
        protected void initView() {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.cache_listview_item, null, false);
            this.iv_icon = (ImageView) view.findViewById(R.id.iv_cache_icon);
            this.tv_name = (TextView) view.findViewById(R.id.tv_cache_name);
            this.tv_cache = (TextView) view.findViewById(R.id.tv_cache_size);
            this.tv_code = (TextView) view.findViewById(R.id.tv_code_size);
            this.tv_data = (TextView) view.findViewById(R.id.tv_data_size);
            view.setTag(this);
        }

        @Override
        protected void refreshView(int pos) {
            CacheInfo info = lists.get(pos);
            this.iv_icon.setImageDrawable(info.getIcon());
            this.tv_name.setText(info.getAppName());
            this.tv_cache.setText("缓存大小：" + info.getCacheSize());
            this.tv_data.setText("数据大小：" + info.getDataSize());
            this.tv_code.setText("应用大小：" + info.getCodeSize());
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
