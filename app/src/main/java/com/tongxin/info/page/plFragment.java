package com.tongxin.info.page;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tongxin.info.R;
import com.tongxin.info.control.PagerSlidingTabStrip;
import com.tongxin.info.domain.MarketGroup;
import com.tongxin.info.domain.SearchItem;
import com.tongxin.info.domain.SearchVM;
import com.tongxin.info.global.GlobalContants;
import com.tongxin.info.utils.loadingUtils;

import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/9/24.
 */
public class plFragment extends baseFragment {
    private FragmentActivity mActivity;
    private ViewPager pl_vp;
    private PagerSlidingTabStrip pl_tabs;
    private ImageButton pl_tab_btn;
    private TextView tv_headerTitle;
    private ImageView iv_return;
    private ImageView iv_ref;
    private List<pl_contentFragment> pl_frag = new ArrayList<pl_contentFragment>();
    com.tongxin.info.utils.loadingUtils loadingUtils;
    private FragmentManager fm;
    public static ArrayList<MarketGroup> marketGroups = new ArrayList<MarketGroup>();
    MyPagerAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        fm = mActivity.getSupportFragmentManager();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        container.removeAllViews();
        View view =View.inflate(mActivity, R.layout.plcontent,null);
        tv_headerTitle = (TextView) view.findViewById(R.id.tv_headerTitle);
        tv_headerTitle.setText("同鑫评论");
        loadingUtils = new loadingUtils(mActivity);
        iv_return = (ImageView) view.findViewById(R.id.iv_return);
        iv_ref = (ImageView) view.findViewById(R.id.iv_ref);

        iv_return.setVisibility(View.INVISIBLE);

        iv_ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
                adapter.notifyDataSetChanged();
                for (int i = 0; i < pl_frag.size(); i++) {
                    pl_frag.get(i).refLV();
                }
            }
        });

        pl_vp = (ViewPager) view.findViewById(R.id.pl_vp);
        pl_tabs = (PagerSlidingTabStrip) view.findViewById(R.id.pl_tabs);
        pl_tabs.setHqFragment(this);
        pl_tabs.setIndicatorColor(Color.rgb(255, 0, 0));

        pl_tab_btn = (ImageButton) view.findViewById(R.id.pl_tab_btn);
        pl_tab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = pl_vp.getCurrentItem();
                pl_vp.setCurrentItem(++position);
            }
        });
        initData();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return marketGroups.get(position).name;
        }

        @Override
        public Fragment getItem(int position) {
            return pl_frag.get(position);
        }

        @Override
        public int getCount() {
            return marketGroups.size();
        }
    }

    @Override
    public void setBtn(Boolean flag)
    {
        if(flag)
        {
            pl_tab_btn.setVisibility(View.VISIBLE);
        }
        else
        {
            pl_tab_btn.setVisibility(View.INVISIBLE);
        }
    }

    private void initData()
    {
        KJHttp kjHttp = new KJHttp();
        kjHttp.get(GlobalContants.GETPLMARKETS_URL, new HttpCallBack() {
            @Override
            public void onFailure(int errorNo, String strMsg) {
                Toast.makeText(mActivity, "获取数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String t) {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<MarketGroup>>() {
                }.getType();
                marketGroups = gson.fromJson(t, type);
                adapter = new MyPagerAdapter(fm);

                initFragment();

                pl_vp.setAdapter(adapter);
                pl_tabs.setViewPager(pl_vp);

            }

            @Override
            public void onPreStart() {
                loadingUtils.show();
            }

            @Override
            public void onFinish() {
                loadingUtils.close();
            }
        });
    }

    private void initFragment()
    {
        pl_frag.clear();
        for (int i = 0; i<marketGroups.size();i++)
        {
            pl_contentFragment pl_fragment = pl_contentFragment.newInstance(i);
            pl_frag.add(pl_fragment);
        }
    }
}
