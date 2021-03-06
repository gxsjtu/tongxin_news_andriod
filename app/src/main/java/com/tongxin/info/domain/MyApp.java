package com.tongxin.info.domain;

import android.app.ActivityManager;
import android.app.Application;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.igexin.sdk.PushManager;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.tongxin.info.activity.LoginActivity;
import com.tongxin.info.utils.SharedPreUtils;
import com.tongxin.info.utils.ToastUtils;

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

    private Timer checkUserTimer;
    private TimerTask checkUserTimerTask;
    private final long checkTime = 1000 * 20;
    private Context context;
    private PushManager pushManager;
    private boolean showLogin = false;
    private int badgeCount = 0;
    MyLifecycleHandler myLifecycleHandler = new MyLifecycleHandler();

    public int getBadgeCount() {
        return badgeCount;
    }

    public void setBadgeCount(int badgeCount) {
        this.badgeCount = badgeCount;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 999:
                    ToastUtils.Show(getApplicationContext(), "您已被强制退出");
                    break;
            }
        }
    };

    public void setShowLogin(boolean showLogin) {
        this.showLogin = showLogin;
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

    public void startCheckUser() {
        this.checkUserTimer = new Timer();
        this.checkUserTimerTask = new TimerTask() {
            @Override
            public void run() {
                boolean mustLogin = SharedPreUtils.getBoolean(context, "mustLogin", true);
                if (mustLogin && !showLogin) {
                    boolean isBack = isBackGroundRunning();
                    if (!isBack && MyLifecycleHandler.startCount(context)) {
                        final Message msg = Message.obtain();
                        msg.what = 999;
                        mHandler.sendMessage(msg);
                        Intent intentCount = new Intent("com.tongxin.badge");
                        intentCount.putExtra("count", -1);
                        context.sendBroadcast(intentCount);
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
            }
        };
        checkUserTimer.schedule(checkUserTimerTask, 10000, checkTime);
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

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        registerActivityLifecycleCallbacks(myLifecycleHandler);
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(false)  //1.8.6包使用时候，括号里面传入参数true
                .cacheOnDisc(false)    //同上
                .build();
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getBaseContext()).defaultDisplayImageOptions(defaultOptions)
//                .threadPriority(Thread.NORM_PRIORITY - 2)
//                .denyCacheImageMultipleSizesInMemory()
//                .tasksProcessingOrder(QueueProcessingType.LIFO)
//                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context)
                .threadPoolSize(3)
// default
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .denyCacheImageMultipleSizesInMemory()
// .memoryCache(new LruMemoryCache((int) (6 * 1024 * 1024)))
                .memoryCache(new WeakMemoryCache())
                .memoryCacheSize((int) (2 * 1024 * 1024))
                .memoryCacheSizePercentage(13)
// default
               // .diskCache(new UnlimitedDiscCache(cacheDir))
// default
                .diskCacheSize(50 * 1024 * 1024).diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .defaultDisplayImageOptions(defaultOptions).writeDebugLogs() // Remove
                .build();
        ImageLoader.getInstance().init(config);
    }

    public boolean isBackGroundRunning() {
        return !MyLifecycleHandler.isApplicationInForeground();
    }
}
