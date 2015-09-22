package com.tongxin.info.com.tongxin.info.fragment;

import android.view.View;
import android.widget.RadioGroup;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tongxin.info.R;
import com.tongxin.info.com.tongxin.info.base.BaseFragment;

/**
 * Created by Administrator on 2015/9/22.
 */
public class contentFragment extends BaseFragment {

    @ViewInject(R.id.rg_group)
    private RadioGroup rgGroup;

    @Override
    public View initViews() {
        View view = View.inflate(mActivity, R.layout.fragment_content,null);
        ViewUtils.inject(this,view);
        return view;
    }

    @Override
    public void initData() {
        rgGroup.check(R.id.rb_inbox);
    }
}
