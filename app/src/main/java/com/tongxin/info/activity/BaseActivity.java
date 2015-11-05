package com.tongxin.info.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import com.tongxin.info.domain.MyApp;

import java.util.List;

/**
 * Created by Administrator on 2015/11/5.
 */
public class BaseActivity extends Activity {
    @Override
    protected void onResume() {
        if (!((MyApp) getApplication()).isActive()) {
            //app 从后台唤醒，进入前台

            ((MyApp) getApplication()).setIsActive(true);
        }
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isAppOnForeground()) {
            ((MyApp) getApplication()).setIsActive(false);
        }
    }

    public boolean isAppOnForeground() {
        // Returns a list of application processes that are running on the
        // device

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
