package com.tongxin.info.base.impl;

import android.view.View;

import com.tongxin.info.base.BaseFragment;

/**
 * Created by Administrator on 2015/9/24.
 */
public class boxFragment extends BaseFragment {
    @Override
    public View initViews() {
        boxContent content = new boxContent(mActivity,"收件箱");
        return content.mView;
    }
}
