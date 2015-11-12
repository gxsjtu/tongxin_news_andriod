package com.tongxin.info.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.tongxin.info.domain.ProductPrice;
import com.tongxin.info.global.GlobalContants;
import com.tongxin.info.utils.ColorsUtils;
import com.tongxin.info.utils.DensityUtils;
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

public class HqDetailActivity extends BaseActivity {

    private TextView tv_headerTitle;
    private SwipeMenuListView hq_detail_lv;
    private LinearLayout iv_return;
    private LinearLayout iv_ref;
    private ArrayList<ProductPrice> mProductPrices = new ArrayList<ProductPrice>();
    private String mMarketName;
    loadingUtils loadingUtils;
    AppAdapter adapter;
    private String tel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hq_detail);
        tel = new UserUtils(this).getTel();
        Intent intent = getIntent();
        final int id = intent.getIntExtra("marketId", 0);
        mMarketName = intent.getStringExtra("marketName");
        tv_headerTitle = (TextView) findViewById(R.id.tv_headerTitle);
        hq_detail_lv = (SwipeMenuListView) findViewById(R.id.hq_detail_lv);

        tv_headerTitle.setText(mMarketName);

        iv_return = (LinearLayout) findViewById(R.id.iv_return);
        iv_ref = (LinearLayout) findViewById(R.id.iv_ref);

        loadingUtils = new loadingUtils(this);
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
                initData(id);
            }
        });

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem watchItem = new SwipeMenuItem(HqDetailActivity.this);
                watchItem.setWidth(DensityUtils.dp2px(HqDetailActivity.this, 90));
                watchItem.setTitleSize(18);
                watchItem.setTitleColor(Color.WHITE);
                if (menu.getViewType() == 0) {
                    watchItem.setBackground(new ColorDrawable(Color.rgb(35, 124, 2)));
                    watchItem.setTitle("添加关注");
                } else if (menu.getViewType() == 1) {
                    watchItem.setBackground(new ColorDrawable(Color.rgb(0xff, 0x00, 0x00)));
                    watchItem.setTitle("取消关注");
                }
                menu.addMenuItem(watchItem);
            }
        };

        hq_detail_lv.setMenuCreator(creator);

        initData(id);
    }

    private void initData(int id) {
        KJHttp kjHttp = new KJHttp();
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.TIMEOUT = 3 * 60 * 1000;
        kjHttp.setConfig(httpConfig);
        kjHttp.get(GlobalContants.GETHQPRICES_URL + "&marketId=" + id + "&mobile="+tel, null, false, new HttpCallBack() {
            @Override
            public void onFailure(int errorNo, String strMsg) {
                Toast.makeText(HqDetailActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                loadingUtils.close();
            }

            @Override
            public void onPreStart() {
                loadingUtils.show();
            }

            @Override
            public void onSuccess(String t) {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<ProductPrice>>() {
                }.getType();
                mProductPrices = gson.fromJson(t, type);
                adapter = new AppAdapter();
                hq_detail_lv.setAdapter(adapter);

                hq_detail_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ProductPrice clickItem = mProductPrices.get(position);
                        Intent intent = new Intent(HqDetailActivity.this, HqHistoryActivity.class);
                        intent.putExtra("productId", clickItem.ProductId);
                        intent.putExtra("productName", mMarketName + " - " + clickItem.ProductName);
                        startActivity(intent);
                    }
                });

                hq_detail_lv.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                        if (index == 0) {
                            //关注
                            ProductPrice item = mProductPrices.get(position);
                            if (item.isOrder.equals("YES")) {
                                //取消关注
                                order(item.ProductId, false, position);
                            } else {
                                //添加关注
                                order(item.ProductId, true, position);
                            }
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
            return mProductPrices.size();
        }

        @Override
        public ProductPrice getItem(int position) {
            return mProductPrices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mProductPrices.get(position).ProductId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(HqDetailActivity.this, R.layout.hq_detail_lv_item, null);
                viewHolder.hq_detail_item_productName = (TextView) convertView.findViewById(R.id.hq_detail_item_productName);
                viewHolder.hq_detail_item_Date = (TextView) convertView.findViewById(R.id.hq_detail_item_Date);
                viewHolder.hq_detail_item_Min = (TextView) convertView.findViewById(R.id.hq_detail_item_Min);
                viewHolder.hq_detail_item_Max = (TextView) convertView.findViewById(R.id.hq_detail_item_Max);
                viewHolder.hq_detail_item_Change = (TextView) convertView.findViewById(R.id.hq_detail_item_Change);

                viewHolder.hq_detail_item_ChangeText = (TextView) convertView.findViewById(R.id.hq_detail_item_ChangeText);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            ProductPrice price = mProductPrices.get(position);
            if (price != null) {
                viewHolder.hq_detail_item_productName.setText(price.ProductName);
                viewHolder.hq_detail_item_Date.setText(price.Date);
                viewHolder.hq_detail_item_Min.setText(price.LPrice);
                viewHolder.hq_detail_item_Max.setText(price.HPrice);


                if (!TextUtils.isEmpty(price.Change)) {
                    double change = Double.parseDouble(price.Change);

                    if (change > 0) {
                        viewHolder.hq_detail_item_Change.setTextColor(ColorsUtils.HIGH);
                        viewHolder.hq_detail_item_ChangeText.setTextColor(ColorsUtils.HIGH);
                        viewHolder.hq_detail_item_Change.setText(String.format("%.2f", Math.abs(change))+"▲");
                        viewHolder.hq_detail_item_ChangeText.setText("涨");
                    } else if (change < 0) {
                        viewHolder.hq_detail_item_Change.setTextColor(ColorsUtils.LOW);
                        viewHolder.hq_detail_item_ChangeText.setTextColor(ColorsUtils.LOW);
                        viewHolder.hq_detail_item_Change.setText(String.format("%.2f", Math.abs(change)) + "▼");
                        viewHolder.hq_detail_item_ChangeText.setText("跌");
                    } else {
                        viewHolder.hq_detail_item_Change.setTextColor(ColorsUtils.NOCHANGE);
                        viewHolder.hq_detail_item_ChangeText.setTextColor(ColorsUtils.NOCHANGE);
                        viewHolder.hq_detail_item_Change.setText("——");
                        viewHolder.hq_detail_item_ChangeText.setText("平");
                    }

                } else {
                    viewHolder.hq_detail_item_Change.setText("");
                    viewHolder.hq_detail_item_ChangeText.setText("");
                    viewHolder.hq_detail_item_Change.setText("");
                }
            }

            return convertView;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            if (mProductPrices.get(position).isOrder.equals("YES")) {
                //已经关注
                return 1;
            } else {
                //没有关注
                return 0;
            }
        }
    }

    private void order(int id, final boolean isOrder, final int position)
    {
        KJHttp kjHttp = new KJHttp();
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.TIMEOUT = 3 * 60 * 1000;
        kjHttp.setConfig(httpConfig);
        HttpParams params = new HttpParams();
        params.put("method", "order");
        params.put("productId", id);
        params.put("mobile", tel);
        params.put("isOrder", isOrder?"YES":"NO");
        kjHttp.post(GlobalContants.ORDER_URL,params,false,new HttpCallBack(){
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
                Toast.makeText(HqDetailActivity.this, "访问网络失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String t) {
                try {
                    JSONObject jsonObject = new JSONObject(t);
                    String result = jsonObject.getString("result");
                    if(result.equals("ok"))
                    {
                        mProductPrices.get(position).isOrder = isOrder?"YES":"NO";
                        adapter.notifyDataSetChanged();
                    }
                    else
                    {
                        Toast.makeText(HqDetailActivity.this, (isOrder?"新增":"取消")+"关注失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static class ViewHolder {
        TextView hq_detail_item_productName;
        TextView hq_detail_item_Date;
        TextView hq_detail_item_Min;
        TextView hq_detail_item_Max;
        TextView hq_detail_item_Change;
        TextView hq_detail_item_ChangeText;
    }

}
