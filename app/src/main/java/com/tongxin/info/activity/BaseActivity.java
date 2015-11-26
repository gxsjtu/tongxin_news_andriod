package com.tongxin.info.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.tongxin.info.R;
import com.tongxin.info.domain.MyApp;
import com.tongxin.info.utils.SharedPreUtils;
import com.tongxin.info.utils.ToastUtils;

import java.util.List;

/**
 * Created by Administrator on 2015/11/5.
 */
public class BaseActivity extends Activity {
    protected MyApp myApp;
    protected ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myApp = (MyApp) getApplication();
        dialog = new ProgressDialog(this);
    }

    @Override
    protected void onResume() {
        //app 从后台唤醒，进入前台
//        boolean mustLogin = SharedPreUtils.getBoolean(this, "mustLogin", true);
//        if (mustLogin) {
//            ToastUtils.Show(this,"您已被强制退出");
//            Intent intent = new Intent(this, LoginActivity.class);
//            this.startActivity(intent);
//        }
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    protected void showLoading()
    {
        if(!dialog.isShowing())
        {
            dialog.setCancelable(false);
            dialog.show();
            dialog.setContentView(R.layout.loading_layout);
        }

    }

    protected  void hideLoading()
    {
        if(dialog!=null && dialog.isShowing())
            dialog.dismiss();
    }

}
