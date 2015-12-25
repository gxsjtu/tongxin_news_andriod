package com.tongxin.info.page;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseDrawer;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tongxin.info.R;
import com.tongxin.info.activity.ChannelActivity;
import com.tongxin.info.activity.SearchActivity;
import com.tongxin.info.activity.userActivity;
import com.tongxin.info.control.PagerSlidingTabStrip;
import com.tongxin.info.domain.ChannelItem;
import com.tongxin.info.domain.MarketGroup;
import com.tongxin.info.domain.ReOrderVM;
import com.tongxin.info.global.GlobalContants;
import com.tongxin.info.utils.DensityUtils;
import com.tongxin.info.utils.SharedPreUtils;
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
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

/**
 * Created by Administrator on 2015/9/24.
 */
public class hqFragment extends baseFragment implements Serializable {
    private FragmentActivity mActivity;
    private ViewPager hq_vp;
    private PagerSlidingTabStrip tabs;
    private LinearLayout hq_tab_btn;
    private TextView tv_headerTitle;
    private LinearLayout iv_user;
    private LinearLayout iv_ref;
    private EditText et_search;
    private ImageView iv_search;
    private List<hq_contentFragment> hq_frag = new ArrayList<hq_contentFragment>();
    private FragmentManager fm;
    private Button hqbtn_CancelSearch;
    //boolean showUserGuide = false;
    ProgressDialog dialog;
    public static ArrayList<MarketGroup> marketGroups = new ArrayList<MarketGroup>();
    public static ArrayList<MarketGroup> allMarketGroups = new ArrayList<MarketGroup>();
    MyPagerAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (FragmentActivity) getActivity();
        fm = mActivity.getSupportFragmentManager();
    }

    private void showLoading() {
        if (!dialog.isShowing()) {
            dialog.setCancelable(false);
            dialog.show();
            dialog.setContentView(R.layout.loading_layout);
        }
    }

    private void hideLoading() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
//        container.removeAllViews();
        View view = View.inflate(mActivity, R.layout.hqcontent, null);
        tv_headerTitle = (TextView) view.findViewById(R.id.tv_headerTitle);
        tv_headerTitle.setText("实时行情");
        dialog = new ProgressDialog(mActivity);
        iv_user = (LinearLayout) view.findViewById(R.id.iv_user);
        iv_ref = (LinearLayout) view.findViewById(R.id.iv_ref);

        iv_user.setVisibility(View.VISIBLE);
        iv_ref.setVisibility(View.VISIBLE);

        iv_ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                for (int i = 0; i < hq_frag.size(); i++) {
                    hq_frag.get(i).refLV();
                }
            }
        });

        iv_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, userActivity.class);
                startActivity(intent);
            }
        });

        hq_vp = (ViewPager) view.findViewById(R.id.hq_vp);
        tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        tabs.setHqFragment(this);
        tabs.setIndicatorColor(Color.rgb(255, 0, 0));

        hq_tab_btn = (LinearLayout) view.findViewById(R.id.hq_tab_btn);
        hq_tab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                int position = hq_vp.getCurrentItem();
//                hq_vp.setCurrentItem(++position);
                Intent intent = new Intent(mActivity,ChannelActivity.class);
                intent.putExtra(ChannelActivity.TYPETAG,"hqFragment");
                mActivity.startActivity(intent);
            }
        });

        et_search = (EditText) view.findViewById(R.id.et_search);

        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if ((actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) && event.getAction() == KeyEvent.ACTION_DOWN) {
                    search();
                    return true;
                } else {
                    return false;
                }
            }
        });
        et_search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hqbtn_CancelSearch.setTextColor(Color.parseColor("#23B1EF"));
                    hqbtn_CancelSearch.setEnabled(true);
                } else {
                    hqbtn_CancelSearch.setTextColor(Color.GRAY);
                    hqbtn_CancelSearch.setEnabled(false);
                }
            }
        });
        hqbtn_CancelSearch = (Button) view.findViewById(R.id.hqbtn_CancelSearch);
        hqbtn_CancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_search.setText("");
                et_search.clearFocus();
                hqbtn_CancelSearch.setTextColor(Color.GRAY);
                hqbtn_CancelSearch.setEnabled(false);
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_search.getWindowToken(), 0);
            }
        });
        initData();

        return view;
    }

    private void search() {
        String key = et_search.getText().toString().trim();
        if (TextUtils.isEmpty(key))
            return;
        Intent intent = new Intent(mActivity, SearchActivity.class);
        intent.putExtra("key", key);
        startActivity(intent);
    }

    @Override
    public void setBtn(Boolean flag) {
//        if (flag) {
//            hq_tab_btn.setVisibility(View.VISIBLE);
//        } else {
//            hq_tab_btn.setVisibility(View.INVISIBLE);
//        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initData() {
        String tel = UserUtils.Tel;
        KJHttp kjHttp = new KJHttp();
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.TIMEOUT = 3 * 60 * 1000;
        kjHttp.setConfig(httpConfig);
        kjHttp.get(GlobalContants.GETMARKETS_URL + "&mobile=" + tel, null, false, new HttpCallBack() {
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

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    public void resetPage()
    {
        adapter = new MyPagerAdapter(fm);

        initFragment();

        hq_vp.setAdapter(adapter);
        tabs.setViewPager(hq_vp);
    }

    private void initFragment() {
        hq_frag.clear();
        for (int i = 0; i < marketGroups.size(); i++) {
            hq_contentFragment hq_fragment = hq_contentFragment.newInstance(i);
            hq_frag.add(hq_fragment);
        }
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
            return hq_frag.get(position);
        }

        @Override
        public int getCount() {
            return marketGroups.size();
        }
    }

    public void onEventMainThread(ReOrderVM vm) {
        String tag = vm.Tag;
        if(!tag.equals("hqFragment"))
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
}
