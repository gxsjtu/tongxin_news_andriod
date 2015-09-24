package com.tongxin.info.base.impl;

import android.app.Activity;
import android.view.View;

import com.tongxin.info.R;
import com.tongxin.info.base.BaseContent;

/**
 * Created by Administrator on 2015/9/24.
 */
public class meContent extends BaseContent {
    public meContent(Activity activity) {
        super(activity);
    }

    public meContent(Activity activity, String title) {
        super(activity, title);
    }

    @Override
    public View initViews() {
        return View.inflate(mActivity, R.layout.mecontent,null);
    }

    @Override
    public void initData() {

    }
}
