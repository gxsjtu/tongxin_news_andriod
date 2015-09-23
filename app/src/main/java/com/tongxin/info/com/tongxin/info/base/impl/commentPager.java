package com.tongxin.info.com.tongxin.info.base.impl;

import android.app.Activity;

import com.tongxin.info.com.tongxin.info.base.BasePager;

/**
 * Created by Administrator on 2015/9/23.
 * 评论界面
 */
public class commentPager extends BasePager {
    public commentPager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        setTitle("同鑫评论");
        setSlidingMenuEnable(false);
    }
}
