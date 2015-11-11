package com.tongxin.info.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.tongxin.info.R;
import com.tongxin.info.global.GlobalContants;
import com.tongxin.info.utils.DensityUtils;
import com.tongxin.info.utils.SharedPreUtils;
import com.tongxin.info.utils.UserUtils;
import com.tongxin.info.utils.loadingUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;
import org.kymjs.kjframe.http.HttpParams;

import java.awt.font.TextAttribute;

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

    loadingUtils loadingUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initViews();
        initData();
    }

    public void loginout(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        SharedPreUtils.setBoolean(this, "mustLogin", true);
        startActivity(intent);
        finish();
    }

    private void initViews() {
        iv_return = (LinearLayout) findViewById(R.id.iv_return);
        tv_userMobile = (TextView) findViewById(R.id.tv_userMobile);
        tv_userEndDate = (TextView) findViewById(R.id.tv_userEndDate);
        tv_headerTitle = (TextView) findViewById(R.id.tv_headerTitle);
        sw = (Switch) findViewById(R.id.sw);
        loadingUtils = new loadingUtils(this);
        tv_headerTitle.setText("用户设置");
        tv_headerTitle.setPadding(0,0,DensityUtils.dp2px(this,60),0);
        iv_return.setVisibility(View.VISIBLE);
        iv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        UserUtils userUtils = new UserUtils(this);
        mobile = userUtils.getTel();
        tv_userMobile.setText(mobile);

    }

    private void initData() {
        KJHttp kjHttp = new KJHttp();
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.TIMEOUT = 3 * 60 * 1000;
        kjHttp.setConfig(httpConfig);
        kjHttp.get(GlobalContants.UserInfo_URL + "?method=getUserInfo&mobile=" + mobile, null, false, new HttpCallBack() {
            @Override
            public void onFailure(int errorNo, String strMsg) {
                Toast.makeText(userActivity.this, "获取用户信息失败，请稍后重试", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFinish() {
                loadingUtils.close();
            }

            @Override
            public void onPreStart() {
                loadingUtils.show();
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
                                    Toast.makeText(userActivity.this, "信息免打扰设置失败，请稍后重试", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFinish() {
                                    loadingUtils.close();
                                }

                                @Override
                                public void onPreStart() {
                                    loadingUtils.show();
                                }

                                @Override
                                public void onSuccess(String t) {
                                    try {
                                        JSONObject json = new JSONObject(t);
                                        String result = json.getString("result");
                                        if (result.equals("ok")) {

                                        } else {
                                            Toast.makeText(userActivity.this, "信息免打扰设置失败，请稍后重试", Toast.LENGTH_SHORT).show();
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
            }
        });
    }

    public void myOrder(View view) {
        //我的关注
    }

    public void mySupply(View view) {
        //我的供求
        Intent intent = new Intent(userActivity.this, MySupplyActivity.class);
        startActivity(intent);
    }
}
