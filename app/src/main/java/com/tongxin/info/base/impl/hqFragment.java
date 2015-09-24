package com.tongxin.info.base.impl;

import android.view.View;

import com.tongxin.info.base.BaseFragment;

/**
 * Created by Administrator on 2015/9/24.
 */
public class hqFragment extends BaseFragment {
    @Override
    public View initViews() {
        hqContent content = new hqContent(mActivity,"行情");
        return content.mView;
    }
}
