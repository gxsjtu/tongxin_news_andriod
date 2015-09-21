package com.tongxin.info.com.tongxin.info.utils;

import android.view.WindowManager;
import android.app.Activity;
import android.content.Context;

/**
 * Created by Administrator on 2015/9/21.
 * 获取屏幕的信息
 */
public class ScreenUtils {
    public ScreenUtils(Activity mActivity) {
        this.mActivity = mActivity;
        wm = mActivity.getWindowManager();
    }

    private Activity mActivity;
    private WindowManager wm;

    //获取屏幕的宽度
    public int GetWindowWidth() {
        return wm.getDefaultDisplay().getWidth();
    }

    //获取屏幕的高度
    public int GetWindowHeight() {
        return wm.getDefaultDisplay().getHeight();
    }
}
