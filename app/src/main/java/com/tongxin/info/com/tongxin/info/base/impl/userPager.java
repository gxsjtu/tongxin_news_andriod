package com.tongxin.info.com.tongxin.info.base.impl;

import android.app.Activity;

import com.tongxin.info.com.tongxin.info.base.BasePager;

/**
 * Created by Administrator on 2015/9/23.
 * 用户界面
 */
public class userPager extends BasePager {
    public userPager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        setTitle("用户中心");
        setSlidingMenuEnable(false);
    }
}
