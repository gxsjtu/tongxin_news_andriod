package com.tongxin.info.domain;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

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
    private boolean isActive  = false;

    private Timer checkUserTimer;
    private TimerTask checkUserTimerTask;
    private final long checkTime = 1000 * 10;

    List<Activity> activityList = new ArrayList<Activity>();

    public List<Activity> getActivityList() {
        return activityList;
    }

    public void setActivityList(List<Activity> activityList) {
        this.activityList = activityList;
    }

    public void startCheckUser()
    {
        this.checkUserTimer = new Timer();
        this.checkUserTimerTask = new TimerTask() {
            @Override
            public void run() {
                startActivityCount++;
                if(startActivityCount==3)
                {
//                    Intent intent = new Intent(Intent.ACTION_MAIN);
//                    intent.addCategory(Intent.CATEGORY_HOME);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);
//                    android.os.Process.killProcess(android.os.Process.myPid());
                    //System.exit(0);


//                    activityList.get(0).finish();
//                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }
        };
        checkUserTimer.schedule(checkUserTimerTask,checkTime,checkTime);
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
        setTel("13764233669");
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


}
