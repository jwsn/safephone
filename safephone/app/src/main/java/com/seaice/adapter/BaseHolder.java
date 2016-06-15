package com.seaice.adapter;

import android.view.View;

import java.util.List;

/**
 * Created by seaice on 2016/6/15.
 */
abstract public class BaseHolder<T> {
    public View view;
    protected List<T> lists;

    //构造函数，初始化list
    protected BaseHolder(List<T> l){
        lists = l;
    }
    //加载界面，并初始化
    protected abstract void initView();
    //刷新界面，设置holder内容
    protected abstract void refreshView(int pos);

    public View getContentView(){
        return view;
    }
}
