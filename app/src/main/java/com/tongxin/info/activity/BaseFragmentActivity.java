package com.tongxin.info.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;

import com.tongxin.info.domain.MyApp;
import com.tongxin.info.utils.SharedPreUtils;

import java.util.List;

/**
 * Created by Administrator on 2015/11/5.
 */
public class BaseFragmentActivity extends FragmentActivity {
    protected MyApp myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myApp = (MyApp)getApplication();
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
//            //4.4以下
//            if (Build.VERSION.SDK_INT < 16) {
//                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            } else {
//                View decorView = getWindow().getDecorView();
//                int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//                decorView.setSystemUiVisibility(uiOptions);
//            }
//        } else {
//            //4.4及以上
//            //透明状态栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            //透明导航栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }
    }

    @Override
    protected void onResume() {
        //if (!myApp.isActive()) {
            //app 从后台唤醒，进入前台
            boolean mustLogin = SharedPreUtils.getBoolean(this, "mustLogin", true);
            if(mustLogin) {
                Intent intent = new Intent(this, LoginActivity.class);
                this.startActivity(intent);
            }
            //myApp.setIsActive(true);
        //}
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (isAppOnForeground()) {
//            myApp.setIsActive(false);
//        }
    }

    public boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }
}
