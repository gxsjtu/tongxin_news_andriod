package com.tongxin.info.base;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tongxin.info.R;

/**
 * Created by Administrator on 2015/9/24.
 */
public abstract class BaseContent {
    public Activity mActivity;
    public View mView;
    public TextView tv_title;
    public LinearLayout baseContent_content;

    public BaseContent(Activity activity) {
        mActivity = activity;
        mView = getView();
        baseContent_content.removeAllViews();
        baseContent_content.addView(initViews());
        initData();
    }

    public BaseContent(Activity activity,String title) {
        mActivity = activity;
        mView = getView();
        SetTitle(title);
        baseContent_content.removeAllViews();
        baseContent_content.addView(initViews());
        initData();
    }

    public View getView()
    {
        View view = android.view.View.inflate(mActivity, R.layout.basecontent,null);
        tv_title = (TextView) view.findViewById(R.id.baseContent_title);
        baseContent_content = (LinearLayout) view.findViewById(R.id.baseContent_content);
        return view;
    }

    public abstract View initViews();

    public void SetTitle(String title)
    {
        tv_title.setText(title);
    }

    public void initData()
    {

    }
}
