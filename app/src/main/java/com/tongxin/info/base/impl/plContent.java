package com.tongxin.info.base.impl;

import android.app.Activity;
import android.view.View;

import com.tongxin.info.R;
import com.tongxin.info.base.BaseContent;

/**
 * Created by Administrator on 2015/9/24.
 */
public class plContent extends BaseContent {
    public plContent(Activity activity) {
        super(activity);
    }

    public plContent(Activity activity, String title) {
        super(activity, title);
    }

    @Override
    public View initViews() {
        return View.inflate(mActivity, R.layout.plcontent,null);
    }

    @Override
    public void initData() {

    }
}
