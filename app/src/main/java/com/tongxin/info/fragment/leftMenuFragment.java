package com.tongxin.info.fragment;

import android.view.View;

import com.tongxin.info.R;
import com.tongxin.info.base.BaseFragment;

/**
 * Created by Administrator on 2015/9/22.
 */
public class leftMenuFragment extends BaseFragment {
    @Override
    public View initViews() {
        View view = View.inflate(mActivity, R.layout.fragment_leftmenu,null);
        return view;
    }
}
