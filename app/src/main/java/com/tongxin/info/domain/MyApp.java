package com.tongxin.info.domain;

import android.app.Application;

/**
 * Created by Administrator on 2015/10/30.
 */
public class MyApp extends Application {
    private String tel;

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setTel("13764233669");
    }
}
