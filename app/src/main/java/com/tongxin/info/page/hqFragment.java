package com.tongxin.info.page;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tongxin.info.R;
import com.tongxin.info.activity.MainActivity;
import com.tongxin.info.domain.MarketGroup;
import com.tongxin.info.global.GlobalContants;

import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/9/24.
 */
public class hqFragment extends Fragment {
    private FragmentActivity mActivity;
    private ViewPager hq_vp;
    private PagerSlidingTabStrip tabs;
    private ImageButton hq_tab_btn;
    public static ArrayList<MarketGroup> marketGroups = new ArrayList<MarketGroup>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (FragmentActivity)getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        container.removeAllViews();
        View view = View.inflate(mActivity, R.layout.hqcontent,null);

        hq_vp = (ViewPager) view.findViewById(R.id.hq_vp);
        tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        hq_tab_btn = (ImageButton) view.findViewById(R.id.hq_tab_btn);
        hq_tab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = hq_vp.getCurrentItem();
                hq_vp.setCurrentItem(++position);
            }
        });
        initData();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initData()
    {
        KJHttp kjHttp = new KJHttp();
        kjHttp.get(GlobalContants.GETMARKETS_URL, new HttpCallBack() {
            @Override
            public void onFailure(int errorNo, String strMsg) {
                Toast.makeText(mActivity, "获取数据失败" + strMsg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String t) {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<MarketGroup>>() {
                }.getType();
                marketGroups = gson.fromJson(t, type);

                hq_vp.setAdapter(new MyPagerAdapter(mActivity.getSupportFragmentManager()));
                tabs.setViewPager(hq_vp);
            }
        });


    }

    public class MyPagerAdapter extends FragmentPagerAdapter{

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return marketGroups.get(position).name;
        }

        @Override
        public Fragment getItem(int position) {
            hq_contentFragment hq_contentFragment = new hq_contentFragment();
            //((MainActivity)mActivity).data=marketGroups.get(position);
            hq_contentFragment.setMarketGroup(marketGroups.get(position));
//            hq_contentFragment.initData();
            return hq_contentFragment;
        }

        @Override
        public int getCount() {
            return marketGroups.size();
        }
    }
}
