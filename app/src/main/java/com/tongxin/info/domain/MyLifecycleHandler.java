package com.tongxin.info.domain;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import com.igexin.sdk.PushManager;
import com.tongxin.info.activity.LoginActivity;
import com.tongxin.info.global.GlobalContants;
import com.tongxin.info.utils.SharedPreUtils;
import com.tongxin.info.utils.ToastUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/11/19.
 */
public class MyLifecycleHandler implements Application.ActivityLifecycleCallbacks {
    private static int resumed;
    private static int paused;
    private static int started;
    private static int stopped;
    private static int destroyed;
    static ArrayList<String> list = new ArrayList<String>() {{
        add("LoginActivity");
        add("TrialActivity");
        add("SplashActivity");
    }};

    public static boolean startCount(Context context) {

        String contextString = context.toString();
        String name = contextString.substring(contextString.lastIndexOf(".") + 1, contextString.indexOf("@"));
        if (list.contains(name)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        ++started;
    }

    @Override
    public void onActivityResumed(final Activity activity) {
        ++resumed;
        boolean back = SharedPreUtils.getBoolean(activity, "back", false);
        if (back && startCount(activity)) {
            SharedPreUtils.setBoolean(activity, "back", false);
            //从后台到前台
            MyApp application = (MyApp) activity.getApplication();
            PushManager pushManager = application.getPushManager();

            String name = SharedPreUtils.getString(activity, "name", "");
            String pwd = SharedPreUtils.getString(activity, "pwd", "");

            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(pwd)) {
                String clientId = pushManager.getClientid(activity);
                if(clientId == null)
                {
                    clientId = SharedPreUtils.getString(activity, "token", "");
                }
                if(clientId == "")
                {
                    ToastUtils.Show(activity,"获取设备号失败，请重新登录");
                    return;
                }
                String Url = GlobalContants.Login_URL + "?method=checkuser&mobile=" + name + "&password=" + pwd + "&token=" + clientId;

                KJHttp kjHttp = new KJHttp();
                HttpConfig httpConfig = new HttpConfig();
                httpConfig.TIMEOUT = 3 * 60 * 1000;
                kjHttp.setConfig(httpConfig);
                kjHttp.get(Url, null, false, new HttpCallBack() {
                    @Override
                    public void onFailure(int errorNo, String strMsg) {

                    }

                    @Override
                    public void onSuccess(String t) {
                        try {
                            JSONObject jsonObject = new JSONObject(t);
                            String result = jsonObject.getString("result");
                            if (result.equals("ok")) {
                                //check成功
                                Intent intentCount = new Intent("com.tongxin.badge");
                                intentCount.putExtra("count", -1);
                                activity.sendBroadcast(intentCount);
                                boolean mustLogin = SharedPreUtils.getBoolean(activity, "mustLogin", true);
                                if (mustLogin) {
                                    ToastUtils.Show(activity,"您已被强制退出");
                                    Intent intent = new Intent(activity, LoginActivity.class);
                                    activity.startActivity(intent);
                                    activity.finish();
                                }


                            } else {
                                Intent intentCount = new Intent("com.tongxin.badge");
                                intentCount.putExtra("count", -1);
                                activity.sendBroadcast(intentCount);
                                ToastUtils.Show(activity, "账号密码错误，请重新输入！");
                                Intent intent = new Intent(activity, LoginActivity.class);
                                SharedPreUtils.setBoolean(activity,"mustLogin",true);
                                activity.startActivity(intent);
                                activity.finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onPreStart() {

                    }

                    @Override
                    public void onFinish() {

                    }
                });

            }
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        ++paused;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        ++stopped;
        if (started == stopped) {
            //在后台,checkuser
            SharedPreUtils.setBoolean(activity, "back", true);
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        ++destroyed;
    }

    public static boolean isApplicationVisible() {
        return started > stopped;
    }

    public static boolean isApplicationInForeground() {
        return resumed > paused;
    }
}
