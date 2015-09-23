package com.tongxin.info.utils;

import android.content.Context;

/**
 * Created by Administrator on 2015/9/21.
 * 用来屏幕适配。dp与px相互转换
 */
public class DensityUtils {
    //dp转px
    public static int dp2px(Context ctx, float dp) {
        float density = ctx.getResources().getDisplayMetrics().density;//获取屏幕密度
        int px = (int) (dp * density + 0.5f);// 四舍五入
        return px;
    }

    // px转dp
    public static float px2dp(Context ctx, int px) {
        float density = ctx.getResources().getDisplayMetrics().density;
        float dp = px / density;
        return dp;
    }
}
