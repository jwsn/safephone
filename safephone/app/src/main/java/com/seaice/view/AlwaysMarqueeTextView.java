package com.seaice.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 自定义TextView，让字滚动起来
 * Created by seaice on 2016/3/3.
 */
public class AlwaysMarqueeTextView extends TextView {

    public AlwaysMarqueeTextView(Context context) {
        super(context);
    }

    public AlwaysMarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlwaysMarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused(){
        return true;
    }
}
