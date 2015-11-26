package com.tongxin.info.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.tongxin.info.R;
import com.tongxin.info.control.MobileEditTextWithDel;
import com.tongxin.info.global.GlobalContants;
import com.tongxin.info.utils.SharedPreUtils;
import com.tongxin.info.utils.ToastUtils;

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
    private Button btn_submit;
    private String view_type;
    RelativeLayout trialBack;
    String title;

    @Override
    protected void onDestroy() {
        time.cancel();
        time = null;
        iv_return = null;
        tv_headerTitle = null;
        et_mobile = null;
        btn_submit = null;
        trialBack.setBackgroundResource(0);
        trialBack = null;

        super.onDestroy();
    }

    TimeCount time = new TimeCount(60000, 1000);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        btn_submit = (Button) findViewById(R.id.btn_submit);
        et_mobile.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        tv_headerTitle.setText(title);
        trialBack = (RelativeLayout) findViewById(R.id.trialBack);
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
            ToastUtils.Show(this, "请输入手机号码");
        } else {
            if (mobile.length() < 11) {
                ToastUtils.Show(this, "手机号码不合法，请重新输入!");
                return;
            }


            String url = GlobalContants.Trial_URL + "?method=";
            if (title.equals("申请试用")) {
                url += "trial";
            } else if (title.equals("发送密码")) {
                url += "send";
            }
            url += "&mobile=" + mobile;

            KJHttp kjHttp = new KJHttp();
            HttpConfig httpConfig = new HttpConfig();
            httpConfig.TIMEOUT = 3 * 60 * 1000;
            kjHttp.setConfig(httpConfig);
            kjHttp.get(url, null, false, new HttpCallBack() {
                @Override
                public void onFailure(int errorNo, String strMsg) {
                    ToastUtils.Show(TrialActivity.this, "访问网络失败");
                    setBtnStatus(btn_submit, true);
                }

                @Override
                public void onSuccess(String t) {
                    try {
                        JSONObject jsonObject = new JSONObject(t);
                        String result = jsonObject.getString("result");
                        if (result.equals("ok")) {
                            ToastUtils.Show(TrialActivity.this, "密码已发送到该手机，请查收！");
                            if (title.equals("申请试用")) {
                                setBtnStatus(btn_submit, true);
                                finish();
                            } else if (title.equals("发送密码")) {
                                time = new TimeCount(60000, 1000);
                                time.start();
                            }
                        } else {
                            if (title.equals("申请试用")) {
                                ToastUtils.Show(TrialActivity.this, result);
                            } else {
                                ToastUtils.Show(TrialActivity.this, "该手机号码尚未注册,请使用有效手机号码");
                                time.cancel();
                            }
                            setBtnStatus(btn_submit, true);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onPreStart() {
                    setBtnStatus(btn_submit, false);
                }

                @Override
                public void onFinish() {

                }
            });
        }
    }

    private void setBtnStatus(Button button, boolean flag) {
        if (!flag) {
            button.setEnabled(false);
            button.setClickable(false);
            button.setBackgroundColor(Color.rgb(204, 206, 208));
            button.setTextColor(Color.BLACK);
            button.setText("确定");
        } else {
            button.setEnabled(true);
            button.setClickable(true);//ff33b5e5
            button.setBackgroundColor(Color.argb(0xff, 0x33, 0xb5, 0xe5));
            button.setTextColor(Color.WHITE);
        }
    }

    class TimeCount extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            btn_submit.setText(millisUntilFinished / 1000 + "秒");
        }

        @Override
        public void onFinish() {
            btn_submit.setText("确定");
            setBtnStatus(btn_submit, true);
        }
    }

}
