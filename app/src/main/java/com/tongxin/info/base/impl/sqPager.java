package com.tongxin.info.base.impl;

import android.app.Activity;

import com.tongxin.info.base.BasePager;

/**
 * Created by Administrator on 2015/9/23.
 * 商圈界面
 */
public class sqPager extends BasePager {
    public sqPager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        setTitle("金属商圈");
        setSlidingMenuEnable(false);
    }
}
