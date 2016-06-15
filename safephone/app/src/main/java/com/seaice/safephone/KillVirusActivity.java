package com.seaice.safephone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.seaice.adapter.MyListViewBaseAdapter;
import com.seaice.bean.AppInfo;
import com.seaice.bean.ProInfo;
import com.seaice.constant.GlobalConstant;
import com.seaice.safephone.TaskSetting.TaskSettingActivity;
import com.seaice.utils.AppInfosUtils;
import com.seaice.utils.DbUtil;
import com.seaice.utils.Md5Util;
import com.seaice.utils.PrefUtil;
import com.seaice.utils.ToastUtil;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class KillVirusActivity extends Activity {
    private static final String TAG = "KillVirusActivity";

    private final static int START_KILL = 0;
    private final static int FINISH_KILL = 1;

    private ImageView iv_kill;
    private ImageView iv_kill_scan;
    private TextView tv_kill;
    private ProgressBar pb_kill;
    private TextView tv_scanning;
    private LinearLayout ll_content;
    private Button btn_start;
    private Button btn_stop;
    private RotateAnimation rotateAnimation;
    private Thread scanThread;
    private boolean isStopTheThread;

    private int countVirus;
    private int countScan;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START_KILL: {
                    ScanInfo scanInfo = (ScanInfo) msg.obj;

                    TextView view = new TextView(KillVirusActivity.this);
                    view.setText(scanInfo.dec);
                    if (scanInfo.flag == true) {
                        countVirus++;
                        view.setTextColor(Color.RED);
                    } else {
                        view.setTextColor(Color.BLACK);
                    }
                    countScan++;
                    tv_scanning.setText("已扫描"+ countScan+"个软件，"+"发现病毒"+countVirus+"个。");
                    ll_content.addView(view, 0);
                    break;
                }
                case FINISH_KILL: {
                    pb_kill.setProgress(0);
                    countScan = 0;
                    iv_kill_scan.clearAnimation();
                    if(countVirus == 0){
                        ToastUtil.showDialog(KillVirusActivity.this, "手机十分安全，没有发现病毒");
                    }else{
                        ToastUtil.showDialog(KillVirusActivity.this, "手机总共扫描到"+countVirus+"个病毒");
                    }
                    break;
                }
                default:
                    break;
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kill_virus);
        initUI();
    }

    private void initUI() {
        iv_kill = (ImageView) findViewById(R.id.iv_kill);
        iv_kill_scan = (ImageView) findViewById(R.id.iv_kill_scan);
        tv_kill = (TextView) findViewById(R.id.tv_kill);
        pb_kill = (ProgressBar) findViewById(R.id.pb_kill);
        btn_start = (Button) findViewById(R.id.btn_kill_start);
        btn_stop = (Button) findViewById(R.id.btn_kill_stop);
        ll_content = (LinearLayout) findViewById(R.id.ll_content);
        tv_scanning = (TextView) findViewById(R.id.tv_scanning);
    }

    /**
     * 开始杀毒啦
     *
     * @param view
     */
    public void btn_kill_start(View view) {
        isStopTheThread = false;
        countVirus = 0;
        countScan = 0;
        rotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(3000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        iv_kill_scan.startAnimation(rotateAnimation);
        ll_content.removeAllViews();
        scanThread = new Thread(new Runnable() {
            @Override
            public void run() {
                PackageManager packageManager = getPackageManager();
                List<PackageInfo> list = packageManager.getInstalledPackages(0);
                int size = list.size();
                pb_kill.setMax(size);
                int count = 0;
                for (PackageInfo p : list) {
                    try {
                        if(isStopTheThread){
                            break;
                        }

                        Thread.sleep(100);

                        ScanInfo scanInfo = new ScanInfo();
                        //String appName = p.applicationInfo.loadLabel(packageManager).toString();
                        String appName = p.packageName;
                        //scanInfo.appName = appName;
                        //scanInfo.pkgName = pkgName;

                        String sourceDir = p.applicationInfo.sourceDir;
                        String md5 = Md5Util.getMd5(sourceDir);
                        String dec = DbUtil.checkFileVirus(md5);
                        Log.e(TAG, md5);
                        Log.e(TAG, appName);
                        Log.e(TAG, "--------------");

                        if (dec == null) {
                            scanInfo.dec = appName + "扫描安全";
                            scanInfo.flag = false;
                        } else {
                            scanInfo.dec = appName + " 有病毒，" + dec;
                            scanInfo.flag = true;
                        }

                        count++;
                        pb_kill.setProgress(count);
                        Message msg = handler.obtainMessage();
                        msg.obj = scanInfo;
                        msg.what = START_KILL;
                        handler.sendMessage(msg);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                }
                handler.sendEmptyMessage(FINISH_KILL);
            }
        });
        scanThread.start();
    }

    /**
     * 停止杀毒啦
     *
     * @param view
     */
    public void btn_kill_stop(View view) {
        isStopTheThread = true;
        iv_kill_scan.clearAnimation();
    }

    static class ScanInfo {
        //public String appName;
        //public String pkgName;
        public String dec;
        public boolean flag;
    }
}
