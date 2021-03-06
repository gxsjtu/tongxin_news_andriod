package com.tongxin.info.page;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tongxin.info.R;
import com.tongxin.info.activity.ChannelActivity;
import com.tongxin.info.control.PagerSlidingTabStrip;
import com.tongxin.info.domain.ChannelItem;
import com.tongxin.info.domain.MarketGroup;
import com.tongxin.info.domain.ReOrderVM;
import com.tongxin.info.global.GlobalContants;
import com.tongxin.info.utils.ToastUtils;
import com.tongxin.info.utils.UserUtils;
import com.tongxin.info.utils.loadingUtils;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/9/24.
 */
public class plFragment extends baseFragment implements Serializable {
    private FragmentActivity mActivity;
    private ViewPager pl_vp;
    private PagerSlidingTabStrip pl_tabs;
    private LinearLayout pl_tab_btn;
    private TextView tv_headerTitle;
    private LinearLayout iv_ref;
    private List<pl_contentFragment> pl_frag = new ArrayList<pl_contentFragment>();
    private FragmentManager fm;
    public static ArrayList<MarketGroup> marketGroups = new ArrayList<MarketGroup>();
    public static ArrayList<MarketGroup> allMarketGroups = new ArrayList<MarketGroup>();
    MyPagerAdapter adapter;
    ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        fm = mActivity.getSupportFragmentManager();
    }

    private void showLoading()
    {
        if(!dialog.isShowing()) {
            dialog.setCancelable(false);
            dialog.show();
            dialog.setContentView(R.layout.loading_layout);
        }
    }

    private void hideLoading()
    {
        if(dialog!=null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
//        container.removeAllViews();
        View view =View.inflate(mActivity, R.layout.plcontent,null);
        tv_headerTitle = (TextView) view.findViewById(R.id.tv_headerTitle);
        tv_headerTitle.setText("同鑫评论");
        dialog = new ProgressDialog(mActivity);
        iv_ref = (LinearLayout) view.findViewById(R.id.iv_ref);

        iv_ref.setVisibility(View.VISIBLE);

        iv_ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
                if (adapter != null)
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

        pl_tab_btn = (LinearLayout) view.findViewById(R.id.pl_tab_btn);
        pl_tab_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    Intent intent = new Intent(mActivity,ChannelActivity.class);
                    intent.putExtra(ChannelActivity.TYPETAG,"plFragment");
                    mActivity.startActivity(intent);
                }
                return true;
            }
        });
//        pl_tab_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                int position = pl_vp.getCurrentItem();
////                pl_vp.setCurrentItem(++position);
//                Intent intent = new Intent(mActivity,ChannelActivity.class);
//                intent.putExtra(ChannelActivity.TYPETAG,"plFragment");
//                mActivity.startActivity(intent);
//            }
//        });
        initData();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
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

    }

    private void initData()
    {
        KJHttp kjHttp = new KJHttp();
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.TIMEOUT = 3 * 60 * 1000;
        kjHttp.setConfig(httpConfig);
        kjHttp.get(GlobalContants.GETPLMARKETS_URL+"&mobile="+ UserUtils.Tel,null,false, new HttpCallBack() {
            @Override
            public void onFailure(int errorNo, String strMsg) {
                ToastUtils.Show(mActivity, "获取数据失败");
            }

            @Override
            public void onSuccess(String t) {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<MarketGroup>>() {
                }.getType();
                allMarketGroups = gson.fromJson(t, type);

                marketGroups.clear();
                for (MarketGroup group : allMarketGroups) {
                    if (group.inBucket.equals("true")) {
                        marketGroups.add(group);
                    }
                }

                resetPage();

            }

            @Override
            public void onPreStart() {
                showLoading();
            }

            @Override
            public void onFinish() {
                hideLoading();
            }
        });
    }

    private void resetPage()
    {
        adapter = new MyPagerAdapter(fm);

        initFragment();

        pl_vp.setAdapter(adapter);
        pl_tabs.setViewPager(pl_vp);
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

    public void onEventMainThread(ReOrderVM vm) {
        String tag = vm.Tag;
        if(!tag.equals("plFragment"))
            return;
        List<ChannelItem> list = vm.list;
        for (MarketGroup group : allMarketGroups)
        {
            group.inBucket  = "false";
        }
        marketGroups.clear();

        ArrayList<Integer> groupIds = new ArrayList<Integer>();

        for (ChannelItem item : list) {
            MarketGroup selectGroup = allMarketGroups.get(item.index);
            selectGroup.inBucket = "true";
            marketGroups.add(selectGroup);
            groupIds.add(selectGroup.id);
        }
        resetPage();
        for (Integer id : groupIds)
        {
            for (MarketGroup group : allMarketGroups)
            {
                if(group.id == id)
                {
                    allMarketGroups.remove(group);
                    break;
                }
            }
        }

        for (int i = 0;i<marketGroups.size();i++)
        {
            allMarketGroups.add(i,marketGroups.get(i));
        }
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }
}
