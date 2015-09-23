package com.tongxin.info.com.tongxin.info.base.impl;

import android.app.Activity;

import com.tongxin.info.com.tongxin.info.base.BasePager;

/**
 * Created by Administrator on 2015/9/23.
 * 收件箱界面
 */
public class boxPager extends BasePager {
    public boxPager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        setTitle("收件箱");
        setSlidingMenuEnable(false);
    }
}
