package com.tongxin.info.base.impl;

import android.view.View;

import com.tongxin.info.base.BaseFragment;

/**
 * Created by Administrator on 2015/9/24.
 */
public class plFragment extends BaseFragment {
    @Override
    public View initViews() {

        plContent content = new plContent(mActivity,"评论");
        return content.mView;
    }
}
