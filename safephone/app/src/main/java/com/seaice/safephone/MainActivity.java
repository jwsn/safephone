package com.seaice.safephone;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.seaice.constant.GlobalConstant;
import com.seaice.reveiver.LockReceiver;
import com.seaice.safephone.HomeCall.HomeCallDbUtil;
import com.seaice.safephone.HomeSafeSetup.HomeSafeSetup1;
import com.seaice.utils.Md5Util;
import com.seaice.utils.PrefUtil;
import com.seaice.utils.StreamUtil;
import com.seaice.utils.ToastUtil;
import com.seaice.view.AlwaysMarqueeTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    private GridView gdView;
    private TextView mainTitleTv;
    private AlwaysMarqueeTextView mainMsgTv;
    private EditText et_password;
    private EditText et_comfirm_password;
    private EditText et_input_pwd;

    private String[] nameIds = {"手机防盗", "通讯卫士", "软件管理", "进程管理", "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心"};
    private int[] imageIds = {R.drawable.home_safe, R.drawable.home_callmsgsafe, R.drawable.home_apps
            , R.drawable.home_taskmanager, R.drawable.home_netmanager, R.drawable.home_trojan,
            R.drawable.home_sysoptimize, R.drawable.home_tools, R.drawable.home_settings};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainTitleTv = (TextView) findViewById(R.id.main_title);
        mainMsgTv = (AlwaysMarqueeTextView) findViewById(R.id.main_tv);
        gdView = (GridView) findViewById(R.id.gdView);
        gdView.setAdapter(new gdAdapter(this));
        gdView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: {
                        showPasswordDialog();
                        break;
                    }
                    case 1: {
                        Intent intent = new Intent(MainActivity.this, HomeCallActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case 2: {
                        Intent intent = new Intent(MainActivity.this, HomeManagerActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case 3: {
                        Intent intent = new Intent(MainActivity.this, TaskManagerActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case 7: {
                        Intent intent = new Intent(MainActivity.this, HomeToolsActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case 8: {
                        Intent intent = new Intent(MainActivity.this, HomeSettingActivity.class);
                        startActivity(intent);
                    }
//                    case 1:
//
//                        ComponentName cn = new ComponentName(MainActivity.this, LockReceiver.class);
//                        Intent intent = new Intent();
//                        intent.setAction(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
//                        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cn);
//                        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "一键锁屏");
//                        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
//                        break;
                    default:
                        break;
                }
            }
        });


        //测试黑名单
        //testBlackNumDb();
    }


    /**
     * GridView 适配器
     */
    public class gdAdapter extends BaseAdapter {

        private Context ctx;

        public gdAdapter(Context context) {
            ctx = context;
        }

        @Override
        public int getCount() {
            return nameIds.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = new Holder();
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.home_gridview_item, null, false);
                holder.gd_iv = (ImageView) convertView.findViewById(R.id.gdview_image);
                holder.gd_tv = (TextView) convertView.findViewById(R.id.gdview_tv);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            holder.gd_tv.setText(nameIds[position]);
            holder.gd_iv.setImageResource(imageIds[position]);

            return convertView;
        }
    }

    /**
     * 弹出密码输入框
     *
     * @return
     */
    private void showPasswordDialog() {
        //弹出密码输入框
        boolean haveSetPassword = PrefUtil.getBooleanPref(this, "IsSetPassword");

        if (haveSetPassword == false) {
            showSetPasswordDialog();
        } else {
            showInputPasswordDialog();
        }
    }

    /**
     * 展示输入密码界面
     */
    private void showInputPasswordDialog() {
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final AlertDialog dialog = builder.create();
        View inputpwdView = inflater.inflate(R.layout.homesafe_input_password_dialog, null, false);
        et_input_pwd = (EditText) inputpwdView.findViewById(R.id.et_input_pwd);
        //final String et_pwd = et_input_pwd.getText().toString();
        final String password = PrefUtil.getStringPref(this, "password");
        Button btn_ok = (Button) inputpwdView.findViewById(R.id.btn_ok);
        Button btn_cancel = (Button) inputpwdView.findViewById(R.id.btn_cancel);
        dialog.setView(inputpwdView);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    String et_pwd = et_input_pwd.getText().toString();
                    if (!TextUtils.isEmpty(et_pwd)) {
                        if (password != null && Md5Util.getMd5(et_pwd).equals(password)) {
                            dialog.dismiss();
                            ToastUtil.showDialog(MainActivity.this, "登陆成功");
                            boolean isSafePhoneHaveSet = PrefUtil.getBooleanPref(MainActivity.this, GlobalConstant.PREF_SAFE_PHONE_FINISH);
                            if (isSafePhoneHaveSet == false) {
                                Intent intent = new Intent(MainActivity.this, HomeSafeSetup1.class);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(MainActivity.this, HomeSafeActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            ToastUtil.showDialog(MainActivity.this, "密码错误");
                        }
                    } else {
                        ToastUtil.showDialog(MainActivity.this, "输入不能为空");
                    }
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 展示设置密码界面
     */
    private void showSetPasswordDialog() {
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final AlertDialog dialog = builder.create();
        View dialogView = inflater.inflate(R.layout.homesafe_password_dialog, null, false);
        et_password = (EditText) dialogView.findViewById(R.id.et_password);
        et_comfirm_password = (EditText) dialogView.findViewById(R.id.et_password_comfirm);
        Button btn_ok = (Button) dialogView.findViewById(R.id.btn_ok);
        Button btn_cancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        dialog.setView(dialogView);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    comfirm_set_password();
                    dialog.dismiss();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 保存设置密码
     *
     * @return
     */
    private void comfirm_set_password() throws NoSuchAlgorithmException {
        String password = et_password.getText().toString();
        String password_confirm = et_comfirm_password.getText().toString();

        if (!TextUtils.isEmpty(password) || password.trim().length() == 0) {
            if (password.equals(password_confirm)) {
                String md5Pwd = Md5Util.getMd5(password);
                PrefUtil.setStringPref(MainActivity.this, "password", md5Pwd);
                PrefUtil.setBooleanPref(this, "IsSetPassword", true);
                ToastUtil.showDialog(MainActivity.this, "密码已经设置！");
            } else {
                ToastUtil.showDialog(MainActivity.this, "输入的密码不匹配");
            }
        } else {
            ToastUtil.showDialog(MainActivity.this, "输入的密码不能为空");
        }
    }

    private static class Holder {
        public TextView gd_tv;
        public ImageView gd_iv;
    }

    /**
     * 用来测试数据库
     */
    private void testBlackNumDb() {
        Log.e(TAG, "testBlackNumDb");
        HomeCallDbUtil hcDbUtil = new HomeCallDbUtil(this);
        for (int i = 0; i < 200; i++) {
            String num = "1890221204" + i;
            String mode = "拦截短信";
            hcDbUtil.addNum(num, mode);
        }
    }

    /**
     * 启动设置里面的各种服务
     */
    private void startSettingService() {

    }
}
