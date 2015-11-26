package com.tongxin.info.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tongxin.info.R;
import com.tongxin.info.domain.MyOrderVM;
import com.tongxin.info.global.GlobalContants;
import com.tongxin.info.utils.DensityUtils;
import com.tongxin.info.utils.SharedPreUtils;
import com.tongxin.info.utils.ToastUtils;
import com.tongxin.info.utils.UserUtils;
import com.tongxin.info.utils.loadingUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;
import org.kymjs.kjframe.http.HttpParams;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/11/11.
 */
public class MyOrderActivity extends BaseActivity {
    private ArrayList<MyOrderVM> orderList = new ArrayList<MyOrderVM>();
    private SwipeMenuListView lv_MyOrder;
    private TextView tv_headerTitle;
    private LinearLayout iv_return;
    private LinearLayout iv_ref;
    AppAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myorder);
        lv_MyOrder = (SwipeMenuListView) findViewById(R.id.lv_MyOrder);
        UserUtils userUtils = new UserUtils(this);
        tv_headerTitle = (TextView) findViewById(R.id.tv_headerTitle);
        tv_headerTitle.setText("我的关注");
        iv_return = (LinearLayout) findViewById(R.id.iv_return);
        iv_return.setVisibility(View.VISIBLE);
        iv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iv_ref = (LinearLayout) findViewById(R.id.iv_ref);
        iv_ref.setVisibility(View.VISIBLE);
        iv_ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
            }
        });
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem watchItem = new SwipeMenuItem(MyOrderActivity.this);
                watchItem.setWidth(DensityUtils.dp2px(MyOrderActivity.this, 90));
                watchItem.setTitleSize(18);
                watchItem.setTitleColor(Color.WHITE);
                watchItem.setBackground(new ColorDrawable(Color.rgb(0xff, 0x0, 0x0)));
                watchItem.setTitle("取消关注");
                menu.addMenuItem(watchItem);
            }
        };
        lv_MyOrder.setMenuCreator(creator);
        initData();
    }

    private void initData() {
        if (UserUtils.Tel == null) {
            UserUtils.Tel = SharedPreUtils.getString(this, "name", "");
        }
        showLoading();
        KJHttp kjHttp = new KJHttp();
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.TIMEOUT = 3 * 60 * 1000;
        kjHttp.setConfig(httpConfig);
        kjHttp.get(GlobalContants.ORDER_URL + "?method=myorder&mobile=" + UserUtils.Tel, null, false, new HttpCallBack() {
            @Override
            public void onPreStart() {
                super.onPreStart();
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                hideLoading();
                ToastUtils.Show(MyOrderActivity.this, "获取数据失败" + strMsg);
            }

            @Override
            public void onSuccess(String t) {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<MyOrderVM>>() {
                }.getType();

                orderList = gson.fromJson(t, type);
                adapter = new AppAdapter();
                lv_MyOrder.setAdapter(adapter);
                hideLoading();
                lv_MyOrder.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                        if (index == 0) {
                            //关注
                            MyOrderVM item = orderList.get(position);
                            //取消关注
                            order(Integer.valueOf(item.productid), position);
                        }
                        return false;
                    }
                });
            }
        });
    }

    public class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return orderList.size();
        }

        @Override
        public MyOrderVM getItem(int position) {
            return orderList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyOrderVM item = getItem(position);
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(MyOrderActivity.this, R.layout.myordercell, null);
                viewHolder.tv_ProductName = (TextView) convertView.findViewById(R.id.tv_MyOrderName);
                viewHolder.tv_MarketName = (TextView) convertView.findViewById(R.id.tv_MyOrderMarket);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.tv_ProductName.setText(item.productname);
            viewHolder.tv_MarketName.setText(item.marketname);
            return convertView;
        }

    }

    private void order(int id, final int position) {
        if (UserUtils.Tel == null) {
            UserUtils.Tel = SharedPreUtils.getString(this, "name", "");
        }
        KJHttp kjHttp = new KJHttp();
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.TIMEOUT = 3 * 60 * 1000;
        kjHttp.setConfig(httpConfig);
        HttpParams params = new HttpParams();
        params.put("method", "order");
        params.put("productId", id);
        params.put("mobile", UserUtils.Tel);
        params.put("isOrder", "NO");
        kjHttp.post(GlobalContants.ORDER_URL, params, false, new HttpCallBack() {
            @Override
            public void onPreStart() {
                showLoading();
            }

            @Override
            public void onFinish() {
                hideLoading();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                ToastUtils.Show(MyOrderActivity.this, "访问网络失败");
            }

            @Override
            public void onSuccess(String t) {
                try {
                    JSONObject jsonObject = new JSONObject(t);
                    String result = jsonObject.getString("result");
                    if (result.equals("ok")) {
                        orderList.remove(position);
                        adapter.notifyDataSetChanged();
                    } else {
                        ToastUtils.Show(MyOrderActivity.this, "取消关注失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public class ViewHolder {
        public TextView tv_ProductName;
        public TextView tv_MarketName;
    }

    @Override
    protected void onDestroy() {
        orderList.clear();

        super.onDestroy();
    }
}
