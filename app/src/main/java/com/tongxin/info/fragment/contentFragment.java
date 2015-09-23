package com.tongxin.info.fragment;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tongxin.info.R;
import com.tongxin.info.base.BaseFragment;
import com.tongxin.info.base.BasePager;
import com.tongxin.info.base.impl.boxPager;
import com.tongxin.info.base.impl.commentPager;
import com.tongxin.info.base.impl.futurePager;
import com.tongxin.info.base.impl.sqPager;
import com.tongxin.info.base.impl.userPager;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/9/22.
 */
public class contentFragment extends BaseFragment {

    @ViewInject(R.id.rg_group)
    private RadioGroup rgGroup;

    @ViewInject(R.id.vp_content)
    private ViewPager vp_content;

    private ArrayList<BasePager> mPagerList;

    @Override
    public View initViews() {
        View view = View.inflate(mActivity, R.layout.fragment_content,null);
        ViewUtils.inject(this,view);
        return view;
    }

    @Override
    public void initData() {
        //初始化子页
        mPagerList = new ArrayList<BasePager>();
        mPagerList.add(new boxPager(mActivity));
        mPagerList.add(new futurePager(mActivity));
        mPagerList.add(new commentPager(mActivity));
        mPagerList.add(new sqPager(mActivity));
        mPagerList.add(new userPager(mActivity));

        vp_content.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return mPagerList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                BasePager pager = mPagerList.get(position);
                container.addView(pager.mRootView);
                return pager.mRootView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });

        rgGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_inbox:
                        vp_content.setCurrentItem(0, false);
                        break;
                    case R.id.rb_hq:
                        vp_content.setCurrentItem(1, false);
                        break;
                    case R.id.rb_pl:
                        vp_content.setCurrentItem(2, false);
                        break;
                    case R.id.rb_sq:
                        vp_content.setCurrentItem(3, false);
                        break;
                    case R.id.rb_me:
                        vp_content.setCurrentItem(4, false);
                        break;
                }
            }
        });

        vp_content.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPagerList.get(position).initData();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //TODO::设置选中的菜单，目前是行情。做完改成第一个。
        rgGroup.check(R.id.rb_hq);
        //TODO::初始化加载行情的数据,做完后修改成第一个
        mPagerList.get(1).initData();

    }
}
