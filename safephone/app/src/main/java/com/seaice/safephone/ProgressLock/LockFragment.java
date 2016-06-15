package com.seaice.safephone.ProgressLock;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.seaice.bean.AppInfo;
import com.seaice.safephone.R;
import com.seaice.utils.LockDbUtil;
import com.seaice.utils.ToastUtil;
import java.util.List;

/**
 * app包中的Fragment和V4包中的Fragment的使用区别
 * 1.尽量不要用app包中的fragment,因为这个是3.0之后才有的，低版本用不了
 * 2.android.support.v4.app.Fragment可以兼容到1.6版本
 * 3.V4包，Activiy必须继承自FragemtnAcitviy
 */
public class LockFragment extends Fragment {

    private static final String TAG = "LockFragment";

    private TextView tv_lock;
    private ListView lv_lock;

    private List<AppInfo> lockList;
    private List<AppInfo> unLockList;

    private LockDbUtil lockDbUtil;

    public LockFragment() {
        // Required empty public constructor
    }

    public static LockFragment newInstance(List<AppInfo> lockList, List<AppInfo> unLockList) {
        LockFragment fragment = new LockFragment();
        fragment.setListData(lockList, unLockList);
        return fragment;
    }

    /**
     * 初始化fragment的数据
     * @param lockList
     * @param unLockList
     */
    private void setListData(List<AppInfo> lockList, List<AppInfo> unLockList){
        this.lockList = lockList;
        this.unLockList = unLockList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        lockDbUtil = new LockDbUtil(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_lock, container, false);
        initViewItem(view);
        return view;
    }

    private void initViewItem(View view) {
        tv_lock = (TextView) view.findViewById(R.id.tv_lock_number);
        lv_lock = (ListView) view.findViewById(R.id.lv_lock);
    }

    @Override
    public void onStart() {
        Log.e(TAG, "onStart");
        super.onStart();
        lv_lock.setAdapter(new LockListViewAdapter());
    }

    private class LockListViewAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            int count = lockList.size();
            tv_lock.setText("已锁软件总共有："+ count +"个");
            return count;
        }

        @Override
        public Object getItem(int position) {
            return lockList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            Holder holder;
            if (convertView != null && convertView instanceof RelativeLayout) {
                holder = (Holder) convertView.getTag();
            } else {
                holder = new Holder();
                convertView = View.inflate(getActivity(), R.layout.fragemnt_lv_lock_item, null);
                holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_lock_icon);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_lock_name);
                holder.iv_lock = (ImageView) convertView.findViewById(R.id.iv_lock_mark);
                convertView.setTag(holder);
            }

            final AppInfo appInfo = lockList.get(position);
            holder.iv_icon.setImageDrawable(appInfo.getIcon());
            holder.tv_name.setText(appInfo.getApkName());
            holder.iv_lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtil.showDialog(getActivity(), "点击");
                    lockList.remove(appInfo);
                    unLockList.add(appInfo);
                    lockDbUtil.delete(appInfo.getApkPackageName());
                    notifyDataSetChanged();
                    ((LockActivity)getActivity()).CheckLockList();
                }
            });
            return convertView;
        }
    }

    private static class Holder {
        public TextView tv_name;
        public ImageView iv_icon;
        public ImageView iv_lock;
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach");
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        Log.e(TAG, "onDestroyView");
        super.onDestroyView();
    }
}
