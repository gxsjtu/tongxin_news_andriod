package com.tongxin.info.domain;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.igexin.sdk.PushManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.tongxin.info.activity.ChartActivity;
import com.tongxin.info.activity.GuideActivity;
import com.tongxin.info.activity.HqDetailActivity;
import com.tongxin.info.activity.HqHistoryActivity;
import com.tongxin.info.activity.InboxDetailActivity;
import com.tongxin.info.activity.LoginActivity;
import com.tongxin.info.activity.MainActivity;
import com.tongxin.info.activity.PingLunDetailActivity;
import com.tongxin.info.activity.SearchActivity;
import com.tongxin.info.activity.SplashActivity;
import com.tongxin.info.utils.SharedPreUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2015/10/30.
 */
public class MyApp extends Application {
    private String tel;
    private String clientId;
    private String pwd;
    private int startActivityCount = 0;
    private boolean isActive = false;

    private Timer checkUserTimer;
    private TimerTask checkUserTimerTask;
    private final long checkTime = 1000 * 20;
    private Context context;
    private PushManager pushManager;


    private LoginActivity loginActivity;


    public void setLoginActivity(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }

    public PushManager getPushManager() {
        return pushManager;
    }

    public void setPushManager(PushManager pushManager) {
        this.pushManager = pushManager;
    }

    public MyApp() {
        super();
    }

    List<Activity> activityList = new ArrayList<Activity>();

    public List<Activity> getActivityList() {
        return activityList;
    }

    public void setActivityList(List<Activity> activityList) {
        this.activityList = activityList;
    }

    public void startCheckUser() {
        this.checkUserTimer = new Timer();
        this.checkUserTimerTask = new TimerTask() {
            @Override
            public void run() {
                boolean mustLogin = SharedPreUtils.getBoolean(context, "mustLogin", true);
                if (mustLogin) {
                    int isBack = isBackGroundRunning();
                    if (isBack == 1) {
                        if (loginActivity == null) {
                            Intent intent = new Intent(context, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    }
                }
            }
        };
        checkUserTimer.schedule(checkUserTimerTask, 10000, checkTime);
    }


    public boolean isActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public int getStartActivityCount() {
        return startActivityCount;
    }

    public void setStartActivityCount(int startActivityCount) {
        this.startActivityCount = startActivityCount;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
//        setTel("13764233669");
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)  //1.8.6包使用时候，括号里面传入参数true
                .cacheOnDisc(true)    //同上
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getBaseContext()).defaultDisplayImageOptions(defaultOptions)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoader.getInstance().init(config);
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

    /*
    * -1后台运行
    * 1前台运行
    * 0没有运行
    * */
    public int isBackGroundRunning() {
        String packageName = context.getPackageName();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            String processName = appProcess.processName;
            if (processName.equals(packageName)) {
                //100   200
                boolean isBackground = appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                        && appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;
                boolean isLockedState = keyguardManager.inKeyguardRestrictedInputMode();
                if (isBackground || isLockedState)
                    return -1;
                else
                    return 1;
            }
        }
        return 0;

    }


}
