package com.tongxin.info.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
import android.widget.Toast;

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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import com.igexin.sdk.PushManager;
import com.tongxin.info.utils.UserUtils;

//闪屏页,可以用来检测app的合法性和新版本的验证，以及预加载一些数据
public class SplashActivity extends Activity {

    private RelativeLayout splash_rl;
    private ProgressBar progress;

    protected static final int UPDATE_DIALOG = 0;
    protected static final int UPDATE_ERROR = 1;
    protected static final int UPDATE_GOHOME = 2;// 进入主页面

    private String mVersionName;
    private int mVersionCode;
    private String mDesc;
    private String mDownloadUrl;
    private String err;
    private PushManager pushManager;
    MyApp myApp;
    private String token="";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_DIALOG:
                    showUpdateDailog();
                    break;
                case UPDATE_ERROR:
                    Toast.makeText(SplashActivity.this, "检查版本更新失败"+err, Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_splash);
        myApp = ((MyApp)getApplication());
        pushManager = myApp.getPushManager();
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
        progress = (ProgressBar) findViewById(R.id.progress);
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


    /**
     * 获取本地app的版本号
     */
    private int getVersionCode() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            int versionCode = packageInfo.versionCode;
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void checkVersion() {

        final long startTime = System.currentTimeMillis();
        KJHttp kjHttp = new KJHttp();
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.TIMEOUT = 3 * 60 * 1000;
        kjHttp.setConfig(httpConfig);

        final Message msg = Message.obtain();

        kjHttp.get(GlobalContants.CHECKVERSION_URL, null, false, new HttpCallBack() {
            @Override
            public void onFailure(int errorNo, String strMsg) {
                msg.what = UPDATE_ERROR;
                err = strMsg;
            }

            @Override
            public void onSuccess(String t) {
                try {
                    JSONObject json = new JSONObject(t);
                    mVersionCode = json.getInt("versioncode");
                    if (mVersionCode > getVersionCode()) {
                        mVersionName = json.getString("versionname");
                        mDesc  = json.getString("description");
                        mDownloadUrl = json.getString("download");
                        msg.what = UPDATE_DIALOG;
                    } else {
                        msg.what = UPDATE_GOHOME;
                    }
                } catch (JSONException e) {
                    err = "";
                    msg.what = UPDATE_ERROR;
                }
            }

            @Override
            public void onFinish() {

                long endTime = System.currentTimeMillis();
                long timeUsed = endTime - startTime;
                if (timeUsed < 2500) {
                    try {
                        Thread.sleep(2500 - timeUsed);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mHandler.sendMessage(msg);
            }
        });
    }

    private void showUpdateDailog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("最新版本" + mVersionName);
        builder.setMessage(mDesc);
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                download();
            }
        });
//        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                nextPage();
//            }
//        });
//        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                nextPage();
//            }
//        });
        builder.show();
    }

    private void download() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            progress.setVisibility(View.VISIBLE);
            final String target = Environment.getExternalStorageDirectory() + "/tongxin_update.apk";
            KJHttp kjHttp = new KJHttp();
            HttpConfig httpConfig = new HttpConfig();
            httpConfig.TIMEOUT = 5 * 60 * 1000;
            kjHttp.setConfig(httpConfig);
            kjHttp.get("http://"+mDownloadUrl, null, false, new HttpCallBack() {
                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                }

                @Override
                public void onLoading(long count, long current) {
//                    super.onLoading(count, current);
                    progress.setProgress((int) (current * 100.0 / count));
                    if (count == current)
                        progress.setProgress(100);
                }

                @Override
                public void onSuccess(byte[] t) {

                    BufferedOutputStream bos = null;
                    FileOutputStream fos = null;
                    File file = null;

                    file = new File(target);
                    try {
                        fos = new FileOutputStream(file);
                        bos = new BufferedOutputStream(fos);
                        bos.write(t);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (bos != null) {
                            try {
                                bos.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                        if (fos != null) {
                            try {
                                fos.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }


                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setDataAndType(Uri.fromFile(new File(target)), "application/vnd.android.package-archive");
                    startActivityForResult(intent, 0);

                }

            });

        } else {
            Toast.makeText(SplashActivity.this, "没有找到sdcard!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //nextPage();
        super.onActivityResult(requestCode, resultCode, data);
    }
}
