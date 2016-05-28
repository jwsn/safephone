package com.seaice.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seaice.safephone.R;

/**
 * 自定义组合控件
 * Created by seaice on 2016/3/3.
 */
public class AddressStyleView extends RelativeLayout {

    public static final String NAMESPACE = "http://schemas.android.com/apk/res-auto";
    private TextView tvTitle;
    private TextView tvDesc;
    private ImageView ivArrow;

    private String mTitle;
    private String mDescOn;
    //private String mDescOff;

    public AddressStyleView(Context context) {
        super(context);
        initView(context);
    }

    public AddressStyleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        mTitle = attrs.getAttributeValue(NAMESPACE, "address_title");
        mDescOn = attrs.getAttributeValue(NAMESPACE, "address_desc_on");
        //mDescOff = attrs.getAttributeValue(NAMESPACE, "desc_off");
        tvTitle.setText(mTitle);
    }

    public AddressStyleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView(context);
    }

    /**
     * 初始化布局
     * @param ctx
     */
    private void initView(Context ctx) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.homesafe_address_style_item, this);
        tvTitle = (TextView) view.findViewById(R.id.tv);
        tvDesc = (TextView) view.findViewById(R.id.tv_cb);
        ivArrow = (ImageView) view.findViewById(R.id.iv_arrow);
    }

    /**
     * 设置描述信息
     * @param text
     */
    public void setDesc(String text){
        tvDesc.setText(text);
    }
}
