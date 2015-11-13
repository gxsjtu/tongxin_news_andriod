package com.tongxin.info.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tongxin.info.R;
import com.tongxin.info.domain.PlProductVM;
import com.tongxin.info.global.GlobalContants;
import com.tongxin.info.utils.DensityUtils;
import com.tongxin.info.utils.UserUtils;
import com.tongxin.info.utils.loadingUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;
import org.kymjs.kjframe.http.HttpParams;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class PingLunDetailActivity extends BaseActivity {
    private TextView tv_headerTitle;
    private LinearLayout iv_return;
    private LinearLayout iv_ref;
    private SwipeMenuListView pl_detail_lv;
    private int mMarketId;
    private String mMarketName;
    private String mGroupName;
    private KJBitmap kjb = new KJBitmap();
    private com.tongxin.info.utils.loadingUtils loadingUtils;
    private String tel;
    AppAdapter adapter;
    private int imgWeight;
    private int imgHeight;

    private ArrayList<PlProductVM> products = new ArrayList<PlProductVM>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pl_detail);
        Intent intent = getIntent();
        mMarketId = intent.getIntExtra("marketId", 0);
        mMarketName = intent.getStringExtra("marketName");
        mGroupName = intent.getStringExtra("groupName");
        UserUtils userUtils = new UserUtils(this);
        tel = userUtils.getTel();
        loadingUtils = new loadingUtils(this);
        imgWeight = DensityUtils.dp2px(this, 80);
        imgHeight = DensityUtils.dp2px(this, 80);
        initViews();
        initData();
    }

    private void initViews() {
        tv_headerTitle = (TextView) findViewById(R.id.tv_headerTitle);
        iv_return = (LinearLayout) findViewById(R.id.iv_return);
        iv_ref = (LinearLayout) findViewById(R.id.iv_ref);
        pl_detail_lv = (SwipeMenuListView) findViewById(R.id.pl_detail_lv);

        tv_headerTitle.setText(mGroupName + "-" + mMarketName);
        iv_return.setVisibility(View.VISIBLE);
        iv_ref.setVisibility(View.VISIBLE);
        iv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        iv_ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
            }
        });

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem watchItem = new SwipeMenuItem(PingLunDetailActivity.this);
                watchItem.setWidth(DensityUtils.dp2px(PingLunDetailActivity.this, 90));
                watchItem.setTitleSize(18);
                watchItem.setTitleColor(Color.WHITE);
                if (menu.getViewType() == 0) {
                    watchItem.setBackground(new ColorDrawable(Color.rgb(35, 124, 2)));
                    watchItem.setTitle("添加关注");
                } else if (menu.getViewType() == 1) {
                    watchItem.setBackground(new ColorDrawable(Color.rgb(0xff, 0x0, 0x0)));
                    watchItem.setTitle("取消关注");
                }
                menu.addMenuItem(watchItem);
            }
        };

        pl_detail_lv.setMenuCreator(creator);
    }

    private void initData() {
        KJHttp kjHttp = new KJHttp();
        kjHttp.get(GlobalContants.GETPLPRODUCTS_URL + "&marketId=" + mMarketId + "&mobile=" + tel, null, false, new HttpCallBack() {
            @Override
            public void onFailure(int errorNo, String strMsg) {
                Toast.makeText(PingLunDetailActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String t) {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<PlProductVM>>() {
                }.getType();
                products = gson.fromJson(t, type);
                adapter = new AppAdapter();
                pl_detail_lv.setAdapter(adapter);

                pl_detail_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        PlProductVM clickItem = products.get(position);
                        Intent intent = new Intent(PingLunDetailActivity.this, InboxDetailActivity.class);
                        intent.putExtra("inboxDetailUrl", clickItem.url);
                        intent.putExtra("title", mMarketName + "-" + clickItem.productname);
                        startActivity(intent);
                    }
                });

                pl_detail_lv.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                        if (index == 0) {
                            //关注
                            PlProductVM item = products.get(position);
                            int id = Integer.parseInt(item.id);
                            if (item.isOrder.equals("YES")) {
                                //取消关注
                                order(id, false, position);
                            } else {
                                //添加关注
                                order(id, true, position);
                            }
                        }
                        return false;
                    }
                });
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

    public class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return products.size();
        }

        @Override
        public PlProductVM getItem(int position) {
            return products.get(position);
        }

        @Override
        public long getItemId(int position) {
            return Long.parseLong(products.get(position).id);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(PingLunDetailActivity.this, R.layout.pl_detail_lv_item, null);
                viewHolder.iv_pl_detail_item = (ImageView) convertView.findViewById(R.id.iv_pl_detail_item);
                viewHolder.tv_pl_detail_item_header = (TextView) convertView.findViewById(R.id.tv_pl_detail_item_header);
                viewHolder.tv_pl_detail_item_name = (TextView) convertView.findViewById(R.id.tv_pl_detail_item_name);
                viewHolder.tv_pl_detail_item_date = (TextView) convertView.findViewById(R.id.tv_pl_detail_item_date);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            PlProductVM pl = products.get(position);
            if (pl != null) {
                viewHolder.tv_pl_detail_item_header.setText(pl.title);
                viewHolder.tv_pl_detail_item_name.setText(pl.productname);
                viewHolder.tv_pl_detail_item_date.setText(pl.date);
                kjb.display(viewHolder.iv_pl_detail_item, pl.avatar, imgWeight, imgHeight);
            }

            return convertView;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            if (products.get(position).isOrder.equals("YES")) {
                //已经关注
                return 1;
            } else {
                //没有关注
                return 0;
            }
        }
    }

    private void order(int id, final boolean isOrder, final int position) {
        KJHttp kjHttp = new KJHttp();
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.TIMEOUT = 3 * 60 * 1000;
        kjHttp.setConfig(httpConfig);
        HttpParams params = new HttpParams();
        params.put("method", "order");
        params.put("productId", id);
        params.put("mobile", tel);
        params.put("isOrder", isOrder ? "YES" : "NO");
        kjHttp.post(GlobalContants.ORDER_URL, params, false, new HttpCallBack() {
            @Override
            public void onPreStart() {
                loadingUtils.show();
            }

            @Override
            public void onFinish() {
                loadingUtils.close();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                Toast.makeText(PingLunDetailActivity.this, "访问网络失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String t) {
                try {
                    JSONObject jsonObject = new JSONObject(t);
                    String result = jsonObject.getString("result");
                    if (result.equals("ok")) {
                        products.get(position).isOrder = isOrder ? "YES" : "NO";
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(PingLunDetailActivity.this, (isOrder ? "新增" : "取消") + "关注失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static class ViewHolder {
        ImageView iv_pl_detail_item;
        TextView tv_pl_detail_item_header;
        TextView tv_pl_detail_item_name;
        TextView tv_pl_detail_item_date;
    }
}
