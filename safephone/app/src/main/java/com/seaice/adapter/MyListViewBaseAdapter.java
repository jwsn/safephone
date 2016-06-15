package com.seaice.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.seaice.safephone.HomeCall.BlackNumInfo;

import java.util.List;

/**
 * Created by seaice on 2016/3/31.
 */
public abstract class MyListViewBaseAdapter<T> extends BaseAdapter {

    protected List<T> lists;
    private Context ctx;

    public MyListViewBaseAdapter(Context context, List<T> list) {
        lists = list;
        ctx = context;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        return convertView;
    }

//    public static abstract class BaseHolder<T> {
//
//        public View view;
//        protected List<T> lists;
//
//        //构造函数，初始化list
//        protected BaseHolder(List<T> l){
//            lists = l;
//        }
//        //加载界面，并初始化
//        protected abstract void initView();
//        //刷新界面，设置holder内容
//        protected abstract void refreshView(int pos);
//
//        public View getContentView(){
//            return view;
//        }
//    }
}
