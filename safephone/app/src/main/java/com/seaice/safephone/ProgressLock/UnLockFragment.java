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
import com.seaice.utils.AppInfosUtils;
import com.seaice.utils.LockDbUtil;
import com.seaice.utils.ToastUtil;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link UnLockFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UnLockFragment extends Fragment {

    private static final String TAG = "UnLockFragment";

    private TextView tv_unlock;
    private ListView lv_unlock;

    private List<AppInfo> lockList;
    private List<AppInfo> unLockList;

    private LockDbUtil lockDbUtil;

    public UnLockFragment() {
        // Required empty public constructor
    }

    public static UnLockFragment newInstance(List<AppInfo> lockList, List<AppInfo> unLockList) {
        UnLockFragment fragment = new UnLockFragment();
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
        View view = inflater.inflate(R.layout.fragment_unlock, container, false);
        initViewItem(view);
        return view;
    }

    private void initViewItem(View view) {
        tv_unlock = (TextView) view.findViewById(R.id.tv_unlock_number);
        lv_unlock = (ListView) view.findViewById(R.id.lv_unlock);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        lv_unlock.setAdapter(new LockListViewAdapter());
    }

    private class LockListViewAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            int count = unLockList.size();
            tv_unlock.setText("未锁软件总共有："+ count +"个");
            return count;
        }

        @Override
        public Object getItem(int position) {
            return unLockList.get(position);
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
                convertView = View.inflate(getActivity(), R.layout.fragemnt_lv_unlock_item, null);
                holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_unlock_icon);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_unlock_name);
                holder.iv_unlock = (ImageView) convertView.findViewById(R.id.iv_unlock_mark);
                convertView.setTag(holder);
            }

            final AppInfo appInfo = unLockList.get(position);
            holder.iv_icon.setImageDrawable(appInfo.getIcon());
            holder.tv_name.setText(appInfo.getApkName());
            holder.iv_unlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtil.showDialog(getActivity(), "点击");
                    unLockList.remove(appInfo);
                    lockList.add(appInfo);
                    lockDbUtil.addLock(appInfo.getApkPackageName(), 1);
                    notifyDataSetChanged();
                    ((LockActivity)getActivity()).CheckLockList();
                    ((LockActivity)getActivity()).showSetPasswordDialog();
                }
            });
            return convertView;
        }
    }

    private static class Holder {
        public TextView tv_name;
        public ImageView iv_icon;
        public ImageView iv_unlock;
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
