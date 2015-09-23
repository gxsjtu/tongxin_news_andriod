package com.tongxin.info.com.tongxin.info.base;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.tongxin.info.R;
import com.tongxin.info.com.tongxin.info.activity.MainActivity;

/**
 * Created by Administrator on 2015/9/23.
 * 主页中的子页面的基类
 */
public class BasePager {
    public Activity mActivity;
    public View mRootView;
    public TextView basepager_tv_title;
    public FrameLayout basepager_fl_content;

    public BasePager(Activity activity) {
        mActivity = activity;
        initViews();
    }

    public void initViews()
    {
        mRootView = View.inflate(mActivity, R.layout.base_pager,null);
        basepager_tv_title = (TextView) mRootView.findViewById(R.id.basepager_tv_title);
        basepager_fl_content = (FrameLayout) mRootView.findViewById(R.id.basepager_fl_content);
    }

    public void initData()
    {

    }

    //设置侧边栏是否开启
    public void setSlidingMenuEnable(Boolean enable)
    {
        MainActivity mainActivity = (MainActivity) mActivity;
        SlidingMenu slidingMenu = mainActivity.getSlidingMenu();
        if(enable)
        {
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        }
        else
        {
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }
    }

    //切换侧边栏显示隐藏
    protected void toggleSlidingMenu()
    {
        MainActivity mainActivity = (MainActivity) mActivity;
        SlidingMenu slidingMenu = mainActivity.getSlidingMenu();
        slidingMenu.toggle();
    }

    protected void setTitle(String title)
    {
        basepager_tv_title.setText(title);
    }
}
