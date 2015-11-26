package com.tongxin.info.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.tongxin.info.R;
import com.tongxin.info.domain.MyApp;
import com.tongxin.info.global.GlobalContants;
import com.tongxin.info.utils.SharedPreUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import com.igexin.sdk.PushManager;
import com.tongxin.info.utils.ToastUtils;

//闪屏页,可以用来检测app的合法性和新版本的验证，以及预加载一些数据
public class SplashActivity extends Activity {

    private RelativeLayout splash_rl;

    protected static final int UPDATE_DIALOG = 0;
    protected static final int UPDATE_ERROR = 1;
    protected static final int UPDATE_GOHOME = 2;// 进入主页面

    private String mVersionName;
    private int mVersionCode;
    private String mDesc;
    private String mDownloadUrl;
    private String err;
    private static PushManager pushManager;
    MyApp myApp;
    private String token="";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_DIALOG:
                    //showUpdateDailog();
                    break;
                case UPDATE_ERROR:
                    ToastUtils.Show(SplashActivity.this, "检查版本更新失败" + err);
                    //nextPage();
                    break;
                case UPDATE_GOHOME:
                    //nextPage();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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


        setContentView(R.layout.activity_splash);
        myApp = ((MyApp)getApplication());
//        pushManager = myApp.getPushManager();
        pushManager = PushManager.getInstance();
        pushManager.initialize(this.getApplicationContext());
        myApp.setPushManager(pushManager);
        String clientId = pushManager.getClientid(this);
        token = SharedPreUtils.getString(this,"token","");
        if(TextUtils.isEmpty(token))
        {
            //登录

        }
        else
        {
            //跳过登录

        }
        if(!TextUtils.isEmpty(clientId))
        {
            SharedPreUtils.setString(this,"token",clientId);
        }

        splash_rl = (RelativeLayout) findViewById(R.id.splash_rl);

        startAnim();//开始动画
        //checkVersion();
    }

    private void startAnim() {
        AnimationSet set = new AnimationSet(false);
        //缩放动画
        ScaleAnimation scale = new ScaleAnimation(2, 1, 2, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(1000);
        scale.setFillAfter(true);

        //渐变动画
        AlphaAnimation alpha = new AlphaAnimation(0, 1);
        alpha.setDuration(2000);
        alpha.setFillAfter(true);

        set.addAnimation(scale);
        set.addAnimation(alpha);

        //监听动画
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            //动画结束
            @Override
            public void onAnimationEnd(Animation animation) {
                //checkVersion();
                //nextPage();
                Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        splash_rl.startAnimation(set);
    }

    @Override
    protected void onDestroy() {
        splash_rl.setBackgroundResource(0);

        super.onDestroy();
    }
}
