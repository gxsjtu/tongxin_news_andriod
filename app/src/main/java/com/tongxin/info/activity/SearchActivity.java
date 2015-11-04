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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tongxin.info.R;
import com.tongxin.info.domain.SearchItem;
import com.tongxin.info.domain.SearchVM;
import com.tongxin.info.global.GlobalContants;
import com.tongxin.info.utils.DensityUtils;
import com.tongxin.info.utils.UserUtils;
import com.tongxin.info.utils.loadingUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;
import org.kymjs.kjframe.http.HttpParams;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/10/21.
 */
public class SearchActivity extends Activity {
    String str = "";
    private ArrayList<SearchVM> searchVMs = new ArrayList<SearchVM>();
    private ArrayList<SearchItem> searchItems = new ArrayList<SearchItem>();

    private TextView tv_headerTitle;
    private SwipeMenuListView lv_search;
    private LinearLayout iv_return;
    private LinearLayout iv_ref;
    loadingUtils loadingUtils;
    AppAdapter adapter;
    private String tel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        tel = new UserUtils(this).getTel();
        Intent intent = getIntent();
        String key = intent.getStringExtra("key");

        try {
            str = URLEncoder.encode(key, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        loadingUtils = new loadingUtils(this);
        initViews();
        initData();
    }

    private void initViews() {
        tv_headerTitle = (TextView) findViewById(R.id.tv_headerTitle);
        lv_search = (SwipeMenuListView) findViewById(R.id.lv_search);
        iv_return = (LinearLayout) findViewById(R.id.iv_return);
        iv_ref = (LinearLayout) findViewById(R.id.iv_ref);
        tv_headerTitle.setText("搜索结果");
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
                SwipeMenuItem watchItem = new SwipeMenuItem(SearchActivity.this);
                watchItem.setWidth(DensityUtils.dp2px(SearchActivity.this, 90));
                watchItem.setTitleSize(18);
                watchItem.setTitleColor(Color.WHITE);
                if (menu.getViewType() == 0) {
                    watchItem.setBackground(new ColorDrawable(Color.rgb(0x51, 0x95, 0x3e)));
                    watchItem.setTitle("添加关注");
                } else if (menu.getViewType() == 1) {
                    watchItem.setBackground(new ColorDrawable(Color.rgb(0xd9, 0x2b, 0x19)));
                    watchItem.setTitle("取消关注");
                }
                menu.addMenuItem(watchItem);
                if(menu.getViewType() == 2)
                {
                    menu.removeMenuItem(watchItem);
                }
            }
        };

        lv_search.setMenuCreator(creator);
    }

    private void initData() {
        KJHttp kjHttp = new KJHttp();
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.TIMEOUT = 3 * 60 * 1000;
        kjHttp.setConfig(httpConfig);
        kjHttp.get(GlobalContants.SEARCH_URL + "&searchKey=" + str + "&mobile="+tel, null, false, new HttpCallBack() {
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
                Toast.makeText(SearchActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String t) {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<SearchVM>>() {
                }.getType();

                searchVMs = gson.fromJson(t, type);
                convertVm2Item();

                adapter = new AppAdapter();

                lv_search.setAdapter(adapter);

                lv_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        SearchItem item = searchItems.get(position);
                        Intent intent = new Intent(SearchActivity.this, HqHistoryActivity.class);
                        intent.putExtra("productId", item.ProductId);
                        intent.putExtra("productName", item.MarketName + "-" + item.ProductName);
                        startActivity(intent);
                    }
                });

                lv_search.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                        if (index == 0) {
                            //关注
                            SearchItem item = searchItems.get(position);
                            if (item.IsOrder.equals("YES")) {
                                //取消关注
                                order(item.ProductId,false,position);
                            } else {
                                //添加关注
                                order(item.ProductId,true,position);
                            }
                        }
                        return false;
                    }
                });
            }
        });
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
                Toast.makeText(SearchActivity.this, "访问网络失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String t) {
                try {
                    JSONObject jsonObject = new JSONObject(t);
                    String result = jsonObject.getString("result");
                    if(result.equals("ok"))
                    {
                        searchItems.get(position).IsOrder = isOrder?"YES":"NO";
                        adapter.notifyDataSetChanged();
                    }
                    else
                    {
                        Toast.makeText(SearchActivity.this, (isOrder?"新增":"取消")+"关注失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public class ViewHolder {
        public TextView search_MarketName;
        public TextView search_ProductName;
        public TextView search_Date;
        public TextView search_Min;
        public TextView search_Max;
        public TextView search_Change;
        public ImageView iv_Change;
        public TextView search_ChangeText;
        public LinearLayout search_ProductItem;
    }

    public class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return searchItems.size();
        }

        @Override
        public Object getItem(int position) {
            return searchItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return searchItems.get(position).ProductId;
        }

        @Override
        public boolean isEnabled(int position) {
            //设置标题栏不能点击
            SearchItem item = searchItems.get(position);
            if (item.IsGroupHeader)
                return false;
            else
                return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            SearchItem item = searchItems.get(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(SearchActivity.this, R.layout.search_header_item, null);
                viewHolder.search_MarketName = (TextView) convertView.findViewById(R.id.search_MarketName);
                viewHolder.search_ProductName = (TextView) convertView.findViewById(R.id.search_ProductName);
                viewHolder.search_Date = (TextView) convertView.findViewById(R.id.search_Date);
                viewHolder.search_Min = (TextView) convertView.findViewById(R.id.search_Min);
                viewHolder.search_Max = (TextView) convertView.findViewById(R.id.search_Max);
                viewHolder.search_Change = (TextView) convertView.findViewById(R.id.search_Change);
                viewHolder.search_ChangeText = (TextView) convertView.findViewById(R.id.search_ChangeText);
                viewHolder.iv_Change = (ImageView) convertView.findViewById(R.id.iv_Change);
                viewHolder.search_ProductItem = (LinearLayout) convertView.findViewById(R.id.search_ProductItem);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (item.IsGroupHeader) {
                viewHolder.search_MarketName.setText(item.MarketName);
                viewHolder.search_MarketName.setVisibility(View.VISIBLE);
                viewHolder.search_ProductItem.setVisibility(View.GONE);
            } else {
                viewHolder.search_MarketName.setVisibility(View.GONE);
                viewHolder.search_ProductItem.setVisibility(View.VISIBLE);

                viewHolder.search_ProductName.setText(item.ProductName);
                viewHolder.search_Date.setText(item.Date);
                viewHolder.search_Min.setText(item.LPrice);
                viewHolder.search_Max.setText(item.HPrice);

                if (TextUtils.isEmpty(item.Change)) {
                    viewHolder.search_Change.setText("");
                    viewHolder.search_Change.setTextColor(Color.BLACK);
                    viewHolder.search_ChangeText.setTextColor(Color.BLACK);
                    viewHolder.search_ChangeText.setText("");
                    viewHolder.iv_Change.setVisibility(View.INVISIBLE);
                } else {
                    double change = Double.parseDouble(item.Change);
                    if (change > 0) {
                        //涨
                        viewHolder.search_Change.setText(String.format("%.1f",change));
                        viewHolder.search_Change.setTextColor(Color.BLACK);
                        viewHolder.search_ChangeText.setTextColor(Color.BLACK);
                        viewHolder.search_ChangeText.setText("涨");
                        viewHolder.iv_Change.setVisibility(View.VISIBLE);
                        viewHolder.iv_Change.setImageResource(R.drawable.red);
                    } else if (change < 0) {
                        //跌
                        viewHolder.search_Change.setText(String.format("%.1f",Math.abs(change)));
                        viewHolder.search_Change.setTextColor(Color.BLACK);
                        viewHolder.search_ChangeText.setTextColor(Color.BLACK);
                        viewHolder.search_ChangeText.setText("跌");
                        viewHolder.iv_Change.setVisibility(View.VISIBLE);
                        viewHolder.iv_Change.setImageResource(R.drawable.green);
                    } else {
                        //平
                        viewHolder.search_Change.setText("——");
                        viewHolder.search_Change.setTextColor(Color.BLACK);
                        viewHolder.search_ChangeText.setTextColor(Color.BLACK);
                        viewHolder.search_ChangeText.setText("平");
                        viewHolder.iv_Change.setVisibility(View.INVISIBLE);
                    }
                }
            }

            return convertView;
        }

        @Override
        public int getViewTypeCount() {
            return 3;
        }

        @Override
        public int getItemViewType(int position) {
            if(!searchItems.get(position).IsGroupHeader) {
                if (searchItems.get(position).IsOrder.equals("YES")) {
                    //已经关注
                    return 1;
                } else {
                    //没有关注
                    return 0;
                }
            }
            else
            {
                return 2;
            }
        }
    }

    private void convertVm2Item() {
        searchItems.clear();
        if (searchVMs.size() > 0) {
            for (int i = 0; i < searchVMs.size(); i++) {
                SearchVM vm = searchVMs.get(i);
                if (vm.products.size() > 0) {
                    SearchItem header = new SearchItem();
                    header.MarketId = vm.id;
                    header.MarketName = vm.name;
                    header.IsGroupHeader = true;
                    searchItems.add(header);
                    for (int j = 0; j < vm.products.size(); j++) {
                        SearchVM.SearchPrice price = vm.products.get(j);
                        SearchItem item = new SearchItem();
                        item.IsGroupHeader = false;
                        item.MarketId = vm.id;
                        item.MarketName = vm.name;
                        item.ProductId = price.ProductId;
                        item.ProductName = price.ProductName;
                        item.LPrice = price.LPrice;
                        item.HPrice = price.HPrice;
                        item.Change = price.Change;
                        item.Date = price.Date;
                        item.IsOrder = price.isOrder;
                        searchItems.add(item);
                    }
                }
            }
        }
    }
}
