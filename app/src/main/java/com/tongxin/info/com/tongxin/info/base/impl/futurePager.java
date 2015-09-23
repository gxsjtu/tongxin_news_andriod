package com.tongxin.info.com.tongxin.info.base.impl;

import android.app.Activity;

import com.tongxin.info.com.tongxin.info.base.BasePager;


/**
 * Created by Administrator on 2015/9/23.
 * 行情页面
 */
public class futurePager extends BasePager {
    public futurePager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        setTitle("实时行情");//设置标题
        setSlidingMenuEnable(true);//启用侧边栏
    }
}
