package com.tongxin.info.page;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tongxin.info.R;
import com.tongxin.info.activity.MainActivity;
import com.tongxin.info.activity.SearchActivity;
import com.tongxin.info.domain.MarketGroup;
import com.tongxin.info.domain.SearchItem;
import com.tongxin.info.domain.SearchVM;
import com.tongxin.info.global.GlobalContants;
import com.tongxin.info.utils.loadingUtils;

import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/9/24.
 */
public class hqFragment extends Fragment {
    private FragmentActivity mActivity;
    private ViewPager hq_vp;
    private PagerSlidingTabStrip tabs;
    private ImageButton hq_tab_btn;
    private TextView tv_headerTitle;
    private ImageView iv_return;
    private ImageView iv_ref;
    private EditText et_search;
    private ListView lv_search;
    private ImageView iv_search;
    loadingUtils loadingUtils;

    public static ArrayList<MarketGroup> marketGroups = new ArrayList<MarketGroup>();
    private ArrayList<SearchVM> searchVMs = new ArrayList<SearchVM>();
    private ArrayList<SearchItem> searchItems = new ArrayList<SearchItem>();
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
        tv_headerTitle = (TextView) view.findViewById(R.id.tv_headerTitle);
        tv_headerTitle.setText("实时行情");
        loadingUtils = new loadingUtils(mActivity);
        iv_return = (ImageView) view.findViewById(R.id.iv_return);
        iv_ref = (ImageView) view.findViewById(R.id.iv_ref);

        iv_return.setVisibility(View.INVISIBLE);

        iv_ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //initData(id);
            }
        });

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

        et_search = (EditText) view.findViewById(R.id.et_search);
//        et_search.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Drawable drawable = et_search.getCompoundDrawables()[2];
//                if (drawable == null)
//                    return false;
//                if (drawable == null)
//                    return false;
//                if (event.getX() > et_search.getWidth() - et_search.getPaddingRight() - drawable.getIntrinsicWidth()) {
//                    search();
//                }
//                return false;
//            }
//        });

        iv_search = (ImageView) view.findViewById(R.id.iv_search);
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
        initData();

        lv_search = (ListView) view.findViewById(R.id.lv_search);

        return view;
    }

    private void search()
    {
        String key = et_search.getText().toString().trim();
        if(TextUtils.isEmpty(key))
            return;
        Intent intent = new Intent(mActivity, SearchActivity.class);
        intent.putExtra("key",key);
        startActivity(intent);
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
                Toast.makeText(mActivity, "获取数据失败", Toast.LENGTH_SHORT).show();
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
