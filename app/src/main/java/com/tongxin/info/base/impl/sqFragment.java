package com.tongxin.info.base.impl;

import android.view.View;

import com.tongxin.info.base.BaseFragment;

/**
 * Created by Administrator on 2015/9/24.
 */
public class sqFragment extends BaseFragment {
    @Override
    public View initViews() {
        sqContent content = new sqContent(mActivity,"商圈");
        return content.mView;
    }
}
