package com.tongxin.info.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tongxin.info.R;
import com.tongxin.info.domain.SearchItem;
import com.tongxin.info.domain.SearchVM;
import com.tongxin.info.global.GlobalContants;

import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/10/21.
 */
public class SearchActivity extends Activity {
    String str="";
    private ArrayList<SearchVM> searchVMs = new ArrayList<SearchVM>();
    private ArrayList<SearchItem> searchItems = new ArrayList<SearchItem>();

    private TextView tv_headerTitle;
    private ListView lv_search;
    private ImageView iv_return;
    private ImageView iv_ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent intent = getIntent();
        String key = intent.getStringExtra("key");

        try {
            str = URLEncoder.encode(key,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        initViews();
        initData();
    }

    private void initViews()
    {
        tv_headerTitle = (TextView) findViewById(R.id.tv_headerTitle);
        lv_search = (ListView) findViewById(R.id.lv_search);
        iv_return = (ImageView) findViewById(R.id.iv_return);
        iv_ref = (ImageView) findViewById(R.id.iv_ref);

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
    }

    private void initData()
    {
        KJHttp kjHttp = new KJHttp();
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.TIMEOUT = 3 * 60 * 1000;
        kjHttp.setConfig(httpConfig);
        kjHttp.get(GlobalContants.SEARCH_URL + "&searchKey=" + str + "&mobile=13764233669", null,false,new HttpCallBack() {
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
                Toast.makeText(SearchActivity.this, "获取数据失败" + strMsg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String t) {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<SearchVM>>() {
                }.getType();

                searchVMs = gson.fromJson(t, type);
                convertVm2Item();

                lv_search.setAdapter(new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return searchItems.size();
                    }

                    @Override
                    public SearchItem getItem(int position) {
                        return searchItems.get(position);
                    }

                    @Override
                    public long getItemId(int position) {
                        return getItem(position).ProductId;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        ViewHolder viewHolder = null;
                        SearchItem item = getItem(position);
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
                            convertView.setTag(viewHolder);

                        } else {
                            viewHolder = (ViewHolder) convertView.getTag();
                        }
                        if (item.IsGroupHeader) {
                            viewHolder.search_MarketName.setText(item.MarketName);
                            viewHolder.search_MarketName.setVisibility(View.VISIBLE);
                        } else {
                            viewHolder.search_MarketName.setVisibility(View.GONE);
                        }
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
                                viewHolder.search_Change.setText(item.Change);
                                viewHolder.search_Change.setTextColor(Color.BLACK);
                                viewHolder.search_ChangeText.setTextColor(Color.BLACK);
                                viewHolder.search_ChangeText.setText("涨");
                                viewHolder.iv_Change.setVisibility(View.VISIBLE);
                                viewHolder.iv_Change.setImageResource(R.drawable.red);
                            } else if (change < 0) {
                                //跌
                                viewHolder.search_Change.setText(String.valueOf(Math.abs(change)));
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

                        return convertView;
                    }
                });
            }
        });
    }

    public class ViewHolder
    {
        public TextView search_MarketName;
        public TextView search_ProductName;
        public TextView search_Date;
        public TextView search_Min;
        public TextView search_Max;
        public TextView search_Change;
        public ImageView iv_Change;
        public TextView search_ChangeText;
    }

    private void convertVm2Item()
    {
        searchItems.clear();
        if(searchVMs.size()>0)
        {
            for (int i = 0;i<searchVMs.size();i++)
            {
                SearchVM vm = searchVMs.get(i);
                if(vm.products.size()>0)
                {
                    for (int j = 0;j<vm.products.size();j++)
                    {
                        SearchVM.SearchPrice price = vm.products.get(j);
                        SearchItem item = new SearchItem();
                        if(j==0)
                        {
                            item.IsGroupHeader = true;
                        }
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
