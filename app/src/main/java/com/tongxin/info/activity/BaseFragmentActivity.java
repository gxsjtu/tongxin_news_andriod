package com.tongxin.info.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

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
