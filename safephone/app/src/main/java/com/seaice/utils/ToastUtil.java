package com.seaice.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by seaice on 2016/3/3.
 */
public class ToastUtil {
    public static void showDialog(Context ctx, String text){
        Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
    }
}
