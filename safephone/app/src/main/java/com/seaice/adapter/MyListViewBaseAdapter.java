package com.seaice.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

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
}
