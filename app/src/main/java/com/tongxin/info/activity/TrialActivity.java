package com.tongxin.info.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.tongxin.info.R;
import com.tongxin.info.control.MobileEditTextWithDel;
import com.tongxin.info.global.GlobalContants;
import com.tongxin.info.utils.SharedPreUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;

/**
 * Created by Administrator on 2015/11/9.
 */
public class TrialActivity extends Activity {
    private LinearLayout iv_return;
    private TextView tv_headerTitle;
    private MobileEditTextWithDel et_mobile;
    private ActionProcessButton btn_submit;
    private String view_type;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
//            //4.4及以下
//            if (Build.VERSION.SDK_INT < 16) {
//                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            } else {
//                View decorView = getWindow().getDecorView();
//                int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//                decorView.setSystemUiVisibility(uiOptions);
//            }
//        } else {
//            //4.4以上
//            //透明状态栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            //透明导航栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }
        Intent intent = getIntent();
        view_type = intent.getStringExtra("Type");
        if (view_type.equals("trial")) {
            //申请试用
            setContentView(R.layout.activity_trial);
            title = "申请试用";
        } else {
            //忘记密码
            setContentView(R.layout.activity_forgetpwd);
            title = "发送密码";
        }
        initViews();

    }

    private void initViews() {
        iv_return = (LinearLayout) findViewById(R.id.iv_return);
        tv_headerTitle = (TextView) findViewById(R.id.tv_headerTitle);
        et_mobile = (MobileEditTextWithDel) findViewById(R.id.et_mobile);
        btn_submit = (ActionProcessButton) findViewById(R.id.btn_submit);
        et_mobile.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        tv_headerTitle.setText(title);
        iv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void submit(View view) {
        String mobile = et_mobile.getText().toString().trim();

        if (TextUtils.isEmpty(mobile)) {
            Toast.makeText(this, "请输入手机号码", Toast.LENGTH_SHORT).show();
        } else {
            if(mobile.length() < 11)
            {
                Toast.makeText(this, "手机号码不合法，请重新输入!", Toast.LENGTH_SHORT).show();
                return;
            }
            String url = GlobalContants.Trial_URL + "?method=";
            if (title.equals("申请试用")) {
                url += "trial";
            } else if (title.equals("发送密码")) {
                url += "send";
            }
            url+="&mobile="+mobile;

            KJHttp kjHttp = new KJHttp();
            HttpConfig httpConfig = new HttpConfig();
            httpConfig.TIMEOUT = 3 * 60 * 1000;
            kjHttp.setConfig(httpConfig);
            kjHttp.get(url, null, false, new HttpCallBack() {
                @Override
                public void onFailure(int errorNo, String strMsg) {
                    btn_submit.setProgress(-1);
                    Toast.makeText(TrialActivity.this, "访问网络失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(String t) {
                    try {
                        JSONObject jsonObject = new JSONObject(t);
                        String result = jsonObject.getString("result");
                        if (result.equals("ok")) {
                            //登陆成功
                            btn_submit.setProgress(100);
                            finish();

                        } else {
                            btn_submit.setProgress(0);
                            if(title.equals("申请试用"))
                            {
                                Toast.makeText(TrialActivity.this, result, Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(TrialActivity.this, "该手机号码尚未注册,请使用有效手机号码", Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onPreStart() {
                    btn_submit.setEnabled(false);
                    btn_submit.setProgress(50);
                }

                @Override
                public void onFinish() {
                    btn_submit.setEnabled(true);
                }
            });
        }
    }
}
