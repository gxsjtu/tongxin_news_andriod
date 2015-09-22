package com.tongxin.info.com.tongxin.info.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tongxin.info.R;
import com.tongxin.info.com.tongxin.info.utils.DensityUtils;
import com.tongxin.info.com.tongxin.info.utils.SharedPreUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/9/21.
 */
public class GuideActivity extends Activity {

    private static final int[] mImageIds = new int[]{R.mipmap.guide1, R.mipmap.guide2};
    private ArrayList<ImageView> mImageViewList;
    private int mPointWidth;// 圆点间的距离

    @ViewInject(R.id.guide_vp)
    private ViewPager guide_vp;
    @ViewInject(R.id.guide_btn_start)
    private Button guide_btn_start;
    @ViewInject(R.id.guide_ll_pointGroup)
    private LinearLayout guide_ll_pointGroup;
    @ViewInject(R.id.guide_view_point)
    private View guide_view_point;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题
        setContentView(R.layout.activity_guide);

        ViewUtils.inject(this);

        guide_btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreUtils.setBoolean(GuideActivity.this, "is_user_guide_showed", true);
                // 跳转主页面
                startActivity(new Intent(GuideActivity.this, MainActivity.class));
                finish();
            }
        });

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

        //ViewPager页面更改监听
        guide_vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //滑动事件
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int len = (int) (mPointWidth * positionOffset) + position
                        * mPointWidth;
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) guide_view_point
                        .getLayoutParams();// 获取当前红点的布局参数
                params.leftMargin = len;// 设置左边距

                guide_view_point.setLayoutParams(params);// 重新给小红点设置布局参数
            }

            //某个页面选中
            @Override
            public void onPageSelected(int i) {
                if (i == mImageIds.length - 1) {
                    guide_btn_start.setVisibility(View.VISIBLE);
                } else {
                    guide_btn_start.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }

    private void initViews() {
        mImageViewList = new ArrayList<ImageView>();
        // 初始化引导页的页面
        for (int i = 0; i < mImageIds.length; i++) {
            ImageView image = new ImageView(this);
            image.setBackgroundResource(mImageIds[i]);// 设置引导页背景
            mImageViewList.add(image);
        }
        // 初始化引导页的小圆点
        for (int i = 0; i < mImageIds.length; i++) {
            View point = new View(this);
            point.setBackgroundResource(R.drawable.guide_gray_point);// 设置引导页默认圆点

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    DensityUtils.dp2px(this, 10), DensityUtils.dp2px(this, 10));
            if (i > 0) {
                params.leftMargin = DensityUtils.dp2px(this, 10);// 设置圆点间隔
            }

            point.setLayoutParams(params);// 设置圆点的大小

            guide_ll_pointGroup.addView(point);// 将圆点添加给线性布局
        }

        // 获取视图树, 对layout结束事件进行监听
        guide_ll_pointGroup.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    // 当layout执行结束后回调此方法
                    @Override
                    public void onGlobalLayout() {
                        guide_ll_pointGroup.getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                        mPointWidth = guide_ll_pointGroup.getChildAt(1).getLeft()
                                - guide_ll_pointGroup.getChildAt(0).getLeft();
                    }
                });
    }
}
