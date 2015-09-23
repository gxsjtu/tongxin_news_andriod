package com.tongxin.info.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.tongxin.info.R;
import com.tongxin.info.fragment.contentFragment;
import com.tongxin.info.fragment.leftMenuFragment;

/**
 * Created by Administrator on 2015/9/21.
 */
public class MainActivity extends SlidingFragmentActivity {
    private static final String FRAGMENT_LEFT_MENU = "fragment_left_menu";
    private static final String FRAGMENT_CONTENT = "fragment_content";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        setBehindContentView(R.layout.left_menu);
        SlidingMenu slidingMenu = getSlidingMenu();
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        int width  =getWindowManager().getDefaultDisplay().getWidth();
        slidingMenu.setBehindOffset(width * 200 / 320);

        initFragment();
    }

    private void initFragment()
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fl_left_menu,new leftMenuFragment(),FRAGMENT_LEFT_MENU);
        transaction.replace(R.id.fl_content, new contentFragment(), FRAGMENT_CONTENT);
        transaction.commit();
    }

    // 获取侧边栏fragment
    public leftMenuFragment getLeftMenuFragment()
    {
        FragmentManager fm = getSupportFragmentManager();
        leftMenuFragment fragment = (leftMenuFragment) fm.findFragmentByTag(FRAGMENT_LEFT_MENU);
        return fragment;
    }

    // 获取主页面fragment
    public contentFragment getContentMenuFragment()
    {
        FragmentManager fm = getSupportFragmentManager();
        contentFragment fragment = (contentFragment) fm.findFragmentByTag(FRAGMENT_CONTENT);
        return fragment;
    }
}
