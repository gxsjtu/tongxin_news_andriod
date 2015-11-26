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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tongxin.info.R;
import com.tongxin.info.domain.ProductPrice;
import com.tongxin.info.global.GlobalContants;
import com.tongxin.info.utils.ColorsUtils;
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

import pl.droidsonroids.gif.GifImageView;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

public class HqDetailActivity extends BaseActivity {

    private TextView tv_headerTitle;
    private SwipeMenuListView hq_detail_lv;
    private LinearLayout iv_return;
    private LinearLayout iv_ref;
    private ArrayList<ProductPrice> mProductPrices = new ArrayList<ProductPrice>();
    private String mMarketName;
    private String mGroupName;
    loadingUtils loadingUtils;
    AppAdapter adapter;
    //boolean showlistGuide = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hq_detail);
        Intent intent = getIntent();
        final int id = intent.getIntExtra("marketId", 0);
        mMarketName = intent.getStringExtra("marketName");
        mGroupName = intent.getStringExtra("groupName");
        tv_headerTitle = (TextView) findViewById(R.id.tv_headerTitle);
        hq_detail_lv = (SwipeMenuListView) findViewById(R.id.hq_detail_lv);
        //showlistGuide = SharedPreUtils.getBoolean(this, "listGuide", false);

        tv_headerTitle.setText(mGroupName + "-" + mMarketName);

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
        if(UserUtils.Tel == null) {
            UserUtils.Tel = SharedPreUtils.getString(this,"name","");
        }
        KJHttp kjHttp = new KJHttp();
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.TIMEOUT = 3 * 60 * 1000;
        kjHttp.setConfig(httpConfig);
        kjHttp.get(GlobalContants.GETHQPRICES_URL + "&marketId=" + id + "&mobile=" + UserUtils.Tel, null, false, new HttpCallBack() {
            @Override
            public void onFailure(int errorNo, String strMsg) {
                ToastUtils.Show(HqDetailActivity.this, "获取数据失败");
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


                //if (!showlistGuide && mProductPrices.size()>0) {
//                    ShowcaseView showcaseView = new ShowcaseView.Builder(HqDetailActivity.this)
//                            .setStyle(R.style.Custom_semi_transparent_demo)
//                            .setContentText("左滑关注产品")
//                            .build();
//                    showcaseView.hideButton();
//                    showcaseView.setHideOnTouchOutside(true);
//                    //showcaseView.setBackground(getResources().getDrawable(R.drawable.swipe_back_en));//minAPI=16
//                    showcaseView.setBackgroundDrawable(getResources().getDrawable(R.drawable.guideback));//deprecated.
////                    SharedPreUtils.setBoolean(HqDetailActivity.this, "listGuide", true);
//                    View view = adapter.getView(0, null, hq_detail_lv);
//                    TextView show = (TextView) view.findViewById(R.id.hq_detail_item_Max);
//                    ToolTip toolTip = new ToolTip().
//                            setTitle("用户设置").
//                            setDescription("点击此处可以进入用户设置页面");
//
//                    Pointer pointer = new Pointer();
//
//                    pointer.setColor(Color.RED);
//
//                    Overlay overlay = new Overlay();
//                    overlay.setBackgroundColor(Color.parseColor("#66000000"));
//                    final TourGuide mTutorialHandler = TourGuide.init(HqDetailActivity.this).with(TourGuide.Technique.Click)
//                            .setPointer(pointer)
//                            .setToolTip(toolTip)
//                            .setOverlay(overlay)
//                            .playOn(hq_detail_lv);
//
//                    overlay.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            mTutorialHandler.cleanUp();
//                        }
//                    });
//                }
//
//                //guide_listview.setVisibility(View.VISIBLE);
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
                viewHolder.hq_detail_item_productName.setText(price.ProductName.trim());
                viewHolder.hq_detail_item_Date.setText(price.Date.trim());
                viewHolder.hq_detail_item_Min.setText(price.LPrice.trim());
                viewHolder.hq_detail_item_Max.setText(price.HPrice.trim());


                if (!TextUtils.isEmpty(price.Change)) {
                    double change = Double.parseDouble(price.Change);

                    if (change > 0) {
                        viewHolder.hq_detail_item_Change.setTextColor(ColorsUtils.HIGH);
                        viewHolder.hq_detail_item_ChangeText.setTextColor(ColorsUtils.HIGH);
                        viewHolder.hq_detail_item_Change.setText(String.format("%.2f", Math.abs(change)) + "▲");
                        viewHolder.hq_detail_item_ChangeText.setText("涨");
                    } else if (change < 0) {
                        viewHolder.hq_detail_item_Change.setTextColor(ColorsUtils.LOW);
                        viewHolder.hq_detail_item_ChangeText.setTextColor(ColorsUtils.LOW);
                        viewHolder.hq_detail_item_Change.setText(String.format("%.2f", Math.abs(change)) + "▼");
                        viewHolder.hq_detail_item_ChangeText.setText("跌");
                    } else {
                        viewHolder.hq_detail_item_Change.setTextColor(ColorsUtils.NOCHANGE);
                        viewHolder.hq_detail_item_ChangeText.setTextColor(ColorsUtils.NOCHANGE);
                        viewHolder.hq_detail_item_Change.setText("一");
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

    private void order(int id, final boolean isOrder, final int position) {
        if(UserUtils.Tel == null) {
            UserUtils.Tel = SharedPreUtils.getString(this,"name","");
        }
        KJHttp kjHttp = new KJHttp();
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.TIMEOUT = 3 * 60 * 1000;
        kjHttp.setConfig(httpConfig);
        HttpParams params = new HttpParams();
        params.put("method", "order");
        params.put("productId", id);
        params.put("mobile", UserUtils.Tel);
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
                ToastUtils.Show(HqDetailActivity.this, "访问网络失败");
            }

            @Override
            public void onSuccess(String t) {
                try {
                    JSONObject jsonObject = new JSONObject(t);
                    String result = jsonObject.getString("result");
                    if (result.equals("ok")) {
                        mProductPrices.get(position).isOrder = isOrder ? "YES" : "NO";
                        adapter.notifyDataSetChanged();
                    } else {
                        ToastUtils.Show(HqDetailActivity.this, (isOrder ? "新增" : "取消") + "关注失败");
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

    @Override
    protected void onDestroy() {
        mProductPrices.clear();
        super.onDestroy();
    }
}
