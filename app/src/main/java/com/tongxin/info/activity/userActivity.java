package com.tongxin.info.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.tongxin.info.R;
import com.tongxin.info.global.GlobalContants;
import com.tongxin.info.utils.SharedPreUtils;
import com.tongxin.info.utils.ToastUtils;
import com.tongxin.info.utils.UserUtils;
import com.tongxin.info.utils.loadingUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;
import org.kymjs.kjframe.http.HttpParams;

/**
 * Created by Administrator on 2015/11/10.
 */
public class userActivity extends BaseActivity {
    private LinearLayout iv_return;
    private TextView tv_userMobile;
    private TextView tv_userEndDate;
    private TextView tv_headerTitle;
    private String mobile;
    private Switch sw;
    private int mVersionCode;
    private TextView tv_CheckVersion;
    private ImageView img_CheckVersion;
    private int currentVersion;
    private boolean isCanUpdate;
    private String versionName;
    private ImageView img_ForNewVersion;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("mobile", mobile);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        if(savedInstanceState!=null)
        {
            mobile = savedInstanceState.getString("mobile");
        }
        else {
            mobile = UserUtils.Tel;
        }
        initViews();
        //checkVersion();
        initData();
    }

    public void loginout(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        SharedPreUtils.setBoolean(this, "mustLogin", true);
        startActivity(intent);
        Intent intentCount = new Intent("com.tongxin.badge");
        intentCount.putExtra("count", -1);
        this.sendBroadcast(intentCount);
        finish();
    }

    private void initViews() {
        iv_return = (LinearLayout) findViewById(R.id.iv_return);
        tv_userMobile = (TextView) findViewById(R.id.tv_userMobile);
        tv_userEndDate = (TextView) findViewById(R.id.tv_userEndDate);
        tv_headerTitle = (TextView) findViewById(R.id.tv_headerTitle);
        tv_CheckVersion = (TextView)findViewById(R.id.tv_CheckVersion);
        img_CheckVersion = (ImageView)findViewById(R.id.img_CheckVersion);
        img_ForNewVersion = (ImageView)findViewById(R.id.img_ForNewVersion);
        sw = (Switch) findViewById(R.id.sw);
        tv_headerTitle.setText("用户设置");
        iv_return.setVisibility(View.VISIBLE);
        iv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if(UserUtils.Tel == null) {
            UserUtils.Tel = SharedPreUtils.getString(this,"name","");
        }
        tv_userMobile.setText(UserUtils.Tel);

    }

    private void initData() {
        if(UserUtils.Tel == null) {
            UserUtils.Tel = SharedPreUtils.getString(this,"name","");
        }
        KJHttp kjHttp = new KJHttp();
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.TIMEOUT = 3 * 60 * 1000;
        kjHttp.setConfig(httpConfig);
        kjHttp.get(GlobalContants.UserInfo_URL + "?method=getUserInfo&mobile=" + UserUtils.Tel, null, false, new HttpCallBack() {
            @Override
            public void onFailure(int errorNo, String strMsg) {
                ToastUtils.Show(userActivity.this, "获取用户信息失败，请稍后重试");
                finish();
            }

            @Override
            public void onFinish() {
                hideLoading();
            }

            @Override
            public void onPreStart() {
                showLoading();
            }

            @Override
            public void onSuccess(String t) {
                try {
                    JSONObject json = new JSONObject(t);
                    String endDate = json.getString("endDate");
                    String isSound = json.getString("isSound");
                    tv_userEndDate.setText(endDate);
                    sw.setChecked(isSound.equals("true"));

                    sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            KJHttp kjHttp = new KJHttp();
                            HttpConfig httpConfig = new HttpConfig();
                            httpConfig.TIMEOUT = 3 * 60 * 1000;
                            kjHttp.setConfig(httpConfig);
                            HttpParams params = new HttpParams();
                            params.put("method", "setUserInfo");
                            params.put("mobile", mobile);
                            params.put("isSound", isChecked ? "1" : "0");
                            kjHttp.post(GlobalContants.UserInfo_URL, params, false, new HttpCallBack() {
                                @Override
                                public void onFailure(int errorNo, String strMsg) {
                                    ToastUtils.Show(userActivity.this, "信息免打扰设置失败，请稍后重试");
                                }

                                @Override
                                public void onFinish() {
                                    hideLoading();
                                }

                                @Override
                                public void onPreStart() {
                                    showLoading();
                                }

                                @Override
                                public void onSuccess(String t) {
                                    try {
                                        JSONObject json = new JSONObject(t);
                                        String result = json.getString("result");
                                        if (result.equals("ok")) {

                                        } else {
                                            ToastUtils.Show(userActivity.this, "信息免打扰设置失败，请稍后重试");
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                checkVersion();
            }
        });
    }

    public void myOrder(View view) {
        //我的关注
        Intent intent = new Intent(this, MyOrderActivity.class);
//        intent.putExtra("mobile",mobile);
        startActivity(intent);
    }

    public void mySupply(View view) {
        //我的供求
        Intent intent = new Intent(userActivity.this, MySupplyActivity.class);
        startActivity(intent);
    }

    public void myUpdate(View view) {
        //版本更新
        if(isCanUpdate)//有可更新的版本
        {
//            Uri uri = Uri.parse("http://api.shtx.com.cn/index.html");
            Uri uri = Uri.parse("http://a.app.qq.com/o/simple.jsp?pkgname=com.tongxin.info");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
        else//没有版本可更新 不做跳转更新操作
        {

        }
    }

    private int getVersionCode() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            int versionCode = packageInfo.versionCode;
            versionName = packageInfo.versionName;
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
                ToastUtils.Show(userActivity.this, "获取更新版本失败！");
            }

            @Override
            public void onSuccess(String t) {
                try {
                    JSONObject json = new JSONObject(t);
                    mVersionCode = json.getInt("versioncode");
                    currentVersion = getVersionCode();
                    if (mVersionCode > currentVersion) {
                        isCanUpdate = true;
                        tv_CheckVersion.setText("有新的版本");
                        img_ForNewVersion.setVisibility(View.VISIBLE);
                        tv_CheckVersion.setTextColor(Color.RED);
                        img_CheckVersion.setVisibility(View.VISIBLE);
                    } else {
                        isCanUpdate = false;
                        tv_CheckVersion.setText("当前是最新版本（v" + versionName + "）");
                        img_ForNewVersion.setVisibility(View.GONE);
                        tv_CheckVersion.setTextColor(Color.GRAY);
                        img_CheckVersion.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
//                    err = "";
//                    msg.what = UPDATE_ERROR;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        img_CheckVersion.setBackgroundResource(0);
        img_ForNewVersion.setBackgroundResource(0);
        super.onDestroy();
    }
}
