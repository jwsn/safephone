package com.seaice.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seaice.safephone.R;

/**
 * 自定义组合控件
 * Created by seaice on 2016/3/3.
 */
public class SetUpItemView extends RelativeLayout {

    public static final String NAMESPACE = "http://schemas.android.com/apk/res-auto";
    private TextView tvTitle;
    private TextView tvDesc;
    private CheckBox checkBox;

    private String mTitle;
    private String mDescOn;
    private String mDescOff;

    public SetUpItemView(Context context) {
        super(context);
        initView(context);
    }

    public SetUpItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        mTitle = attrs.getAttributeValue(NAMESPACE, "item_title");
        mDescOn = attrs.getAttributeValue(NAMESPACE, "desc_on");
        mDescOff = attrs.getAttributeValue(NAMESPACE, "desc_off");
        tvTitle.setText(mTitle);
    }

    public SetUpItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView(context);
    }

    /**
     * 初始化布局
     * @param ctx
     */
    private void initView(Context ctx) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.homesafe_setup_item, this);
        tvTitle = (TextView) view.findViewById(R.id.tv);
        tvDesc = (TextView) view.findViewById(R.id.tv_cb);
        checkBox = (CheckBox) view.findViewById(R.id.checkbox);
    }

    /**
     * 检查是否选中checkbox
     * @return boolean
     */
    public boolean isChecked(){
        return checkBox.isChecked();
    }

    /**
     * 设置check box 是否选中
     * @param checked
     */
    public void setCheckBox(boolean checked){
        if(checked){
            setDesc(mDescOn);
        }else{
            setDesc(mDescOff);
        }
        checkBox.setChecked(checked);
    }

    /**
     * 设置描述信息
     * @param text
     */
    public void setDesc(String text){
        tvDesc.setText(text);
    }
}
