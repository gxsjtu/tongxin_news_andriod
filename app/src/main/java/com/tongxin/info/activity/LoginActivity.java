package com.tongxin.info.activity;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.igexin.sdk.PushManager;
import com.tongxin.info.R;
import com.tongxin.info.domain.MarketGroup;
import com.tongxin.info.domain.MyApp;
import com.tongxin.info.global.GlobalContants;
import com.tongxin.info.utils.SharedPreUtils;
import com.tongxin.info.utils.UserUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/11/5.
 */
public class LoginActivity extends Activity {
    private EditText et_name;
    private EditText et_pwd;
    private ActionProcessButton btn_login;
    private String clientId;
    UserUtils userUtils;
    boolean mustLogin = false;
    MyApp application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        application = (MyApp) getApplication();
        application.setLoginActivity(this);

        //初始化个推
        PushManager pushManager = PushManager.getInstance();
        pushManager.initialize(this.getApplicationContext());
        clientId = pushManager.getClientid(this);
        userUtils = new UserUtils(this);
        userUtils.setClientId(clientId);

        String name = SharedPreUtils.getString(this, "name", "");
        String pwd = SharedPreUtils.getString(this, "pwd", "");
        mustLogin = SharedPreUtils.getBoolean(this, "mustLogin", true);
        if (!mustLogin) {
            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(pwd)) {
                login(name, pwd, clientId);
            }
        }

        initViews();
    }

    private void initViews() {
        et_name = (EditText) findViewById(R.id.et_name);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        btn_login = (ActionProcessButton) findViewById(R.id.btn_login);
        btn_login.setMode(ActionProcessButton.Mode.ENDLESS);
    }

    public void login(View view) {
        boolean hasName = true;
        boolean hasPwd = true;
        final String name = et_name.getText().toString();
        final String pwd = et_pwd.getText().toString();
        if (TextUtils.isEmpty(name)) {
            hasName = false;
        }
        if (TextUtils.isEmpty(pwd)) {
            hasPwd = false;
        }
        if (hasName && hasPwd) {
            et_name.setEnabled(false);
            et_pwd.setEnabled(false);
            btn_login.setEnabled(false);
            login(name, pwd, clientId);
        } else {
            if (!hasName && !hasPwd) {
                Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            } else {
                if (!hasName) {
                    Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
                } else if (!hasPwd) {
                    Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void login(final String name, final String pwd, String clientId) {
        KJHttp kjHttp = new KJHttp();
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.TIMEOUT = 3 * 60 * 1000;
        kjHttp.setConfig(httpConfig);
        kjHttp.get(GlobalContants.Login_URL + "?method=signin&mobile=" + name + "&password=" + pwd + "&token=" + clientId + "&phoneType=1",
                new HttpCallBack() {
                    @Override
                    public void onFailure(int errorNo, String strMsg) {
                        Toast.makeText(LoginActivity.this, "访问网络失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String t) {
                        try {
                            JSONObject jsonObject = new JSONObject(t);
                            String result = jsonObject.getString("result");
                            if (result.equals("ok")) {
                                //登陆成功
                                userUtils.setTel(name);
                                userUtils.setPwd(pwd);
                                SharedPreUtils.setString(LoginActivity.this, "name", name);
                                SharedPreUtils.setString(LoginActivity.this, "pwd", pwd);
                                SharedPreUtils.setBoolean(LoginActivity.this, "mustLogin", false);
                                Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onPreStart() {
                        btn_login.setProgress(50);
                    }

                    @Override
                    public void onFinish() {
                        et_name.setEnabled(true);
                        et_pwd.setEnabled(true);
                        btn_login.setEnabled(true);
                        btn_login.setProgress(100);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        application.setLoginActivity(null);
        super.onDestroy();
    }
}
