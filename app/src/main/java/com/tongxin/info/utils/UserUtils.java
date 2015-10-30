package com.tongxin.info.utils;

import android.app.Activity;

import com.tongxin.info.domain.MyApp;

/**
 * Created by Administrator on 2015/10/30.
 */
public class UserUtils {
    private Activity mActivity;
    public UserUtils(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void setTel(String tel)
    {
        MyApp myApp = (MyApp) mActivity.getApplication();
        myApp.setTel(tel);
    }

    public String getTel()
    {
        MyApp myApp = (MyApp) mActivity.getApplication();
        return myApp.getTel();
    }
}
