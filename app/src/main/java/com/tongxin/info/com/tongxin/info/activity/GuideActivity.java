package com.tongxin.info.com.tongxin.info.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import com.tongxin.info.R;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/9/21.
 */
public class GuideActivity extends Activity {

    private static final int[] mImageIds = new int[]{R.mipmap.page1, R.mipmap.page2, R.mipmap.page3};
    private ArrayList<ImageView> mImageViewList;
    private ViewPager guide_vp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题
        setContentView(R.layout.activity_guide);

        guide_vp = (ViewPager) findViewById(R.id.guide_vp);

        initViews();
        guide_vp.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return mImageIds.length;
            }
            @Override
            public boolean isViewFromObject(View view, Object o) {
                return view == o;
            }
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(mImageViewList.get(position));
                return mImageViewList.get(position);
            }
            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });
    }

    private void initViews()
    {
        mImageViewList = new ArrayList<ImageView>();
        // 初始化引导页的页面
        for (int i = 0; i < mImageIds.length; i++) {
            ImageView image = new ImageView(this);
            image.setBackgroundResource(mImageIds[i]);// 设置引导页背景
            mImageViewList.add(image);
        }
    }
}
