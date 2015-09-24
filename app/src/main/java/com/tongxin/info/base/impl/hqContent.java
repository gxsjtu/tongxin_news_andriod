package com.tongxin.info.base.impl;

import android.app.Activity;
import android.view.View;

import com.tongxin.info.R;
import com.tongxin.info.base.BaseContent;

/**
 * Created by Administrator on 2015/9/24.
 */
public class hqContent extends BaseContent {
    public hqContent(Activity activity) {
        super(activity);
    }

    public hqContent(Activity activity, String title) {
        super(activity, title);
    }

    @Override
    public View initViews() {
        return View.inflate(mActivity, R.layout.hqcontent,null);
    }

    @Override
    public void initData() {

    }
}
