package com.tongxin.info.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tongxin.info.R;
import com.tongxin.info.domain.MarketGroup;
import com.tongxin.info.domain.ProductPrice;
import com.tongxin.info.global.GlobalContants;
import com.tongxin.info.page.hqFragment;
import com.tongxin.info.utils.ColorsUtils;
import com.tongxin.info.utils.loadingUtils;

import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class HqDetailActivity extends AppCompatActivity {

    private TextView tv_headerTitle;
    private ListView hq_detail_lv;
    private ImageView iv_return;
    private ImageView iv_ref;
    private ArrayList<ProductPrice> mProductPrices = new ArrayList<ProductPrice>();
    private String mMarketName;
    loadingUtils loadingUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hq_detail);
        Intent intent = getIntent();
        final int id = intent.getIntExtra("marketId", 0);
        mMarketName = intent.getStringExtra("marketName");
        tv_headerTitle = (TextView) findViewById(R.id.tv_headerTitle);
        hq_detail_lv = (ListView) findViewById(R.id.hq_detail_lv);

        tv_headerTitle.setText(mMarketName);

        iv_return = (ImageView) findViewById(R.id.iv_return);
        iv_ref = (ImageView) findViewById(R.id.iv_ref);

        loadingUtils = new loadingUtils(this);

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

        initData(id);
    }

    private void initData(int id) {
        KJHttp kjHttp = new KJHttp();
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.TIMEOUT = 3 * 60 * 1000;
        kjHttp.setConfig(httpConfig);
        kjHttp.get(GlobalContants.GETHQPRICES_URL + "&marketId=" + id + "&mobile=13764233669", null, false, new HttpCallBack() {
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

                hq_detail_lv.setAdapter(new BaseAdapter() {
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
                            viewHolder.hq_detail_item_Change.setText(price.Change);

                            if (!TextUtils.isEmpty(price.Change)) {
                                double change = Double.parseDouble(price.Change);
                                if (change > 0) {
                                    viewHolder.hq_detail_item_Change.setTextColor(ColorsUtils.DARKRED);
                                    viewHolder.hq_detail_item_ChangeText.setTextColor(ColorsUtils.DARKRED);
                                    viewHolder.hq_detail_item_ChangeText.setText("涨");
                                } else if (change < 0) {
                                    viewHolder.hq_detail_item_Change.setTextColor(ColorsUtils.DARKGREEN);
                                    viewHolder.hq_detail_item_ChangeText.setTextColor(ColorsUtils.DARKGREEN);
                                    viewHolder.hq_detail_item_ChangeText.setText("跌");
                                } else {
                                    viewHolder.hq_detail_item_Change.setTextColor(Color.BLACK);
                                    viewHolder.hq_detail_item_ChangeText.setTextColor(Color.BLACK);
                                    viewHolder.hq_detail_item_Change.setText("——");
                                    viewHolder.hq_detail_item_ChangeText.setText("平");
                                }
                            } else {
                                viewHolder.hq_detail_item_Change.setText("");
                                viewHolder.hq_detail_item_ChangeText.setText("");
                            }
                        }

                        return convertView;
                    }
                });

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
