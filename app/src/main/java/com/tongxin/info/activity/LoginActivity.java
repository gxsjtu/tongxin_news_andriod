package com.tongxin.info.activity;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
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
    boolean showLogin = true;
    PushManager pushManager;

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            //4.4以下
            if (Build.VERSION.SDK_INT < 16) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
                View decorView = getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
            }
        } else {
            //4.4及以上
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
        {
            //4.4以下

        }
        else
        {
            //4.4及以上
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        application = (MyApp) getApplication();
        application.setLoginActivity(this);
        pushManager = application.getPushManager();
        userUtils = new UserUtils(this);

        String name = SharedPreUtils.getString(this, "name", "");
        String pwd = SharedPreUtils.getString(this, "pwd", "");

        mustLogin = SharedPreUtils.getBoolean(this, "mustLogin", true);

        if (!mustLogin) {
            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(pwd)) {
                String token = SharedPreUtils.getString(this, "token", "");
                showLogin = false;
                login(name, pwd, token, 1);
            }
        }
        else {
            setContentView(R.layout.activity_login);


            initViews();
        }
    }

    private void initViews() {
        et_name = (EditText) findViewById(R.id.et_name);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        btn_login = (ActionProcessButton) findViewById(R.id.btn_login);
        btn_login.setMode(ActionProcessButton.Mode.ENDLESS);
    }

    public void login(View view) {
        showLogin = true;
        clientId = pushManager.getClientid(this);
        if(TextUtils.isEmpty(clientId))
        {
            Toast.makeText(this,"获取设备号失败，请稍后重新登录",Toast.LENGTH_SHORT).show();
            return;
        }

        userUtils = new UserUtils(this);
        userUtils.setClientId(clientId);
        SharedPreUtils.setString(this,"token",clientId);
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
            login(name, pwd, clientId, 0);
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

    private void login(final String name, final String pwd, String clientId, int type) {
        String Url = "";
        if (type == 0) {
            Url = GlobalContants.Login_URL + "?method=signin&mobile=" + name + "&password=" + pwd + "&token=" + clientId + "&phoneType=1";
        } else {
            Url = GlobalContants.Login_URL + "?method=checkuser&mobile=" + name + "&password=" + pwd + "&token=" + clientId;
        }
        KJHttp kjHttp = new KJHttp();
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.TIMEOUT = 3 * 60 * 1000;
        kjHttp.setConfig(httpConfig);
        kjHttp.get(Url, null, false, new HttpCallBack() {
            @Override
            public void onFailure(int errorNo, String strMsg) {
                btn_login.setProgress(-1);
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
                        if(showLogin)
                            btn_login.setProgress(100);
//                        Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
//                        startActivity(intent);
//                        finish();
                        application.startCheckUser();
                        nextPage();


                    } else {
                        if(showLogin)
                            btn_login.setProgress(0);
                        if(!showLogin)
                        {
                            showLogin = true;
                            setContentView(R.layout.activity_login);
                            initViews();
                        }
                        //Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPreStart() {
                if(showLogin) {
                    et_name.setEnabled(false);
                    et_pwd.setEnabled(false);
                    btn_login.setEnabled(false);
                    btn_login.setProgress(50);
                }
            }

            @Override
            public void onFinish() {
                if(showLogin) {
                    et_name.setEnabled(true);
                    et_pwd.setEnabled(true);
                    btn_login.setEnabled(true);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        application.setLoginActivity(null);
        super.onDestroy();
    }

    /*忘记密码*/
    public void forgetPwd(View view) {
        Intent intent = new Intent(this,TrialActivity.class);
        intent.putExtra("Type", "forgetPwd");
        startActivity(intent);
    }

    /*联系客服*/
    public void Contact(View view) {
        String tel = "4008720588";//客服电话
        Intent phoneIntent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + tel));
        startActivity(phoneIntent);
    }

    /*申请试用*/
    public void Require(View view) {
        Intent intent = new Intent(this,TrialActivity.class);
        intent.putExtra("Type","trial");
        startActivity(intent);
    }


    private void nextPage() {
        //判断是否进入过新手指引页面
        boolean userGuide = SharedPreUtils.getBoolean(this, "is_user_guide_showed", false);
        if (!userGuide) {
            //跳的新手指引页
            startActivity(new Intent(LoginActivity.this, GuideActivity.class));
        } else {
            //跳到主页
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
        finish();
    }
}
