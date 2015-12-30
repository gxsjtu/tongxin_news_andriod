package com.tongxin.info.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tongxin.info.R;
import com.tongxin.info.domain.ProductHistoryPrice;
import com.tongxin.info.global.GlobalContants;
import com.tongxin.info.utils.ColorsUtils;
import com.tongxin.info.utils.ToastUtils;
import com.tongxin.info.utils.loadingUtils;

import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HqHistoryActivity extends BaseActivity {

    Calendar calendar = Calendar.getInstance(Locale.CHINA);
    EditText startDate;
    EditText endDate;
    ListView hq_history_lv;
    TextView tv_totalAvg;
    RelativeLayout content;
    LinearLayout avgContent;
    private TextView tv_headerTitle;
    private LinearLayout iv_return;
    private LinearLayout tv_headerChart;
    private float totalAvg = 0;

    private String mProductName;
    private int mProductId;
    private ArrayList<ProductHistoryPrice> mHistoryPrices = new ArrayList<ProductHistoryPrice>();

    private String getDateStr(int year, int month, int day) {
        month++;
        StringBuffer sb = new StringBuffer();
        sb.append(year + "-");
        if (month < 10) {
            sb.append("0");
        }
        sb.append(month + "-");
        if (day < 10) {
            sb.append("0");
        }
        sb.append(day);
        return sb.toString();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("productId", mProductId);
        outState.putString("productName", mProductName);
        super.onSaveInstanceState(outState);
    }

//    private int getListViewHeight()
//    {
//        ListAdapter listAdapter = hq_history_lv.getAdapter();
//        if (listAdapter == null) {
//            return 0;
//        }
//
//        int totalHeight = 0;
//        for (int i = 0; i < listAdapter.getCount(); i++) {
//            View listItem = listAdapter.getView(i, null, hq_history_lv);
//            listItem.measure(0, 0);
//            totalHeight += listItem.getMeasuredHeight();
//        }
//
//        int height = totalHeight + (hq_history_lv.getDividerHeight() * (listAdapter.getCount() - 1));
//
//        return height;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hq_history);

        if (savedInstanceState != null) {
            mProductId = savedInstanceState.getInt("productId");
            mProductName = savedInstanceState.getString("productName");
        } else {
            Intent intent = getIntent();
            mProductId = intent.getIntExtra("productId", 0);
            mProductName = intent.getStringExtra("productName");
        }

        tv_headerTitle = (TextView) findViewById(R.id.tv_headerTitle);
        tv_headerTitle.setText(mProductName);

        startDate = (EditText) findViewById(R.id.startDate);
        endDate = (EditText) findViewById(R.id.endDate);
        hq_history_lv = (ListView) findViewById(R.id.hq_history_lv);
        avgContent = (LinearLayout) findViewById(R.id.avgContent);

        tv_totalAvg = (TextView) findViewById(R.id.tv_totalAvg);

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = startDate.getText().toString();
                String[] arrDate = date.split("-");
                int year = Integer.parseInt(arrDate[0]);
                int month = Integer.parseInt(arrDate[1]) - 1;
                int day = Integer.parseInt(arrDate[2]);
                DatePickerDialog dpd = new DatePickerDialog(HqHistoryActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        startDate.setText(getDateStr(year, monthOfYear, dayOfMonth));
                    }
                }, year, month, day);
                dpd.show();

            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = endDate.getText().toString();
                String[] arrDate = date.split("-");
                int year = Integer.parseInt(arrDate[0]);
                int month = Integer.parseInt(arrDate[1]) - 1;
                int day = Integer.parseInt(arrDate[2]);
                DatePickerDialog dpd = new DatePickerDialog(HqHistoryActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        endDate.setText(getDateStr(year, monthOfYear, dayOfMonth));
                    }
                }, year, month, day);
                dpd.show();

            }
        });

        iv_return = (LinearLayout) findViewById(R.id.iv_return);
        tv_headerChart = (LinearLayout) findViewById(R.id.tv_headerChart);

        iv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_headerChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //走势图
                if (mHistoryPrices.size() > 0) {
                    Intent chartIntent = new Intent(HqHistoryActivity.this, ChartActivity.class);
                    chartIntent.putExtra("data", (Serializable) mHistoryPrices);
                    chartIntent.putExtra("title", mProductName);
                    startActivity(chartIntent);
                }
            }
        });

        initData();


    }

    public void searchClick(View view) {
        search();
    }

    private String format(double value)
    {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        nf.setRoundingMode(RoundingMode.HALF_UP);
        nf.setGroupingUsed(false);
        return nf.format(value);
    }

    private void search() {
        String start = startDate.getText().toString();
        String end = endDate.getText().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d1 = sdf.parse(start);
            Date d2 = sdf.parse(end);

            if (d2.getTime() - d1.getTime() < 0) {
                ToastUtils.Show(this, "截止日期不能小于开始日期");
                return;
            }

            if ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24.0) > 90) {
                ToastUtils.Show(this, "查询时间跨度不能超过90天！");
                return;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        mHistoryPrices.clear();

        KJHttp kjHttp = new KJHttp();
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.TIMEOUT = 3 * 60 * 1000;
        kjHttp.setConfig(httpConfig);
        kjHttp.get(GlobalContants.GETHQHISTORYPRICES_URL + "&productId=" + mProductId + "&start=" + start + "&end=" + end, null, false, new HttpCallBack() {
            @Override
            public void onFailure(int errorNo, String strMsg) {
                ToastUtils.Show(HqHistoryActivity.this, "获取数据失败");
            }

            @Override
            public void onPreStart() {
                showLoading();
            }

            @Override
            public void onFinish() {
                hideLoading();
            }

            @Override
            public void onSuccess(String t) {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<ProductHistoryPrice>>() {
                }.getType();
                mHistoryPrices = gson.fromJson(t, type);

                if (mHistoryPrices.size() == 0) {
                    tv_headerChart.setVisibility(View.INVISIBLE);
                    avgContent.setVisibility(View.INVISIBLE);
                } else {
                    tv_headerChart.setVisibility(View.VISIBLE);
                    avgContent.setVisibility(View.VISIBLE);
                }

                totalAvg = 0;

                for (ProductHistoryPrice price : mHistoryPrices) {
                    float lPrice = 0;
                    float hPrice = 0;
                    float aPrice = 0;
                    boolean hasL = false;
                    boolean hasH = false;
                    if (!TextUtils.isEmpty(price.LPrice)) {
                        lPrice = Float.valueOf(price.LPrice);
                        hasL = true;
                    }

                    if (!TextUtils.isEmpty(price.HPrice)) {
                        hPrice = Float.valueOf(price.HPrice);
                        hasH = true;
                    }

                    if (hasL && hasH) {
                        aPrice = (lPrice + hPrice) / 2;
                    } else {
                        if (hasL) {
                            aPrice = lPrice;
                        } else if (hasH) {
                            aPrice = hPrice;
                        }
                    }
                    price.APrice = aPrice;
                    totalAvg += aPrice;
                }
                if(mHistoryPrices.size()>0) {
                    tv_totalAvg.setText("均价 : " + format(totalAvg/mHistoryPrices.size()));
                }
                hq_history_lv.setAdapter(new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return mHistoryPrices.size();
                    }

                    @Override
                    public ProductHistoryPrice getItem(int position) {
                        return mHistoryPrices.get(position);
                    }

                    @Override
                    public long getItemId(int position) {
                        return position;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        ViewHolder viewHolder = null;
                        if (convertView == null) {
                            viewHolder = new ViewHolder();
                            convertView = View.inflate(HqHistoryActivity.this, R.layout.hq_history_item, null);
                            viewHolder.tv_priceMin = (TextView) convertView.findViewById(R.id.tv_priceMin);
                            viewHolder.tv_priceMax = (TextView) convertView.findViewById(R.id.tv_priceMax);
                            viewHolder.tv_priceDate = (TextView) convertView.findViewById(R.id.tv_priceDate);
                            viewHolder.tv_priceChange = (TextView) convertView.findViewById(R.id.tv_priceChange);
                            viewHolder.tv_priceAvg = (TextView) convertView.findViewById(R.id.tv_priceAvg);
                            convertView.setTag(viewHolder);
                        } else {
                            viewHolder = (ViewHolder) convertView.getTag();
                        }

                        ProductHistoryPrice price = mHistoryPrices.get(position);

                        viewHolder.tv_priceDate.setText(price.Date);
                        viewHolder.tv_priceMin.setText(price.LPrice);
                        viewHolder.tv_priceMax.setText(price.HPrice);
                        viewHolder.tv_priceAvg.setText(format(price.APrice));
                        if (!TextUtils.isEmpty(price.Change)) {
                            Double change = Double.parseDouble(price.Change);
                            if (change > 0) {
                                viewHolder.tv_priceChange.setText("涨 " + format(change) + "▲");
                                viewHolder.tv_priceChange.setTextColor(ColorsUtils.HIGH);
                            } else if (change < 0) {
                                viewHolder.tv_priceChange.setText("跌 " + format(Math.abs(change))+ "▼");
                                viewHolder.tv_priceChange.setTextColor(ColorsUtils.LOW);

                            } else {
                                viewHolder.tv_priceChange.setText("平");
                                viewHolder.tv_priceChange.setTextColor(ColorsUtils.NOCHANGE);
                            }
                        } else {
                            viewHolder.tv_priceChange.setText("");
                        }
                        return convertView;
                    }
                });
            }
        });
    }

    private void initData() {
        Date myData = new Date();
        calendar.setTime(myData);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        endDate.setText(getDateStr(year, month, day));

        calendar.add(Calendar.DAY_OF_MONTH, -14);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        startDate.setText(getDateStr(year, month, day));

        search();
    }

    private static class ViewHolder {
        TextView tv_priceMin;
        TextView tv_priceMax;
        TextView tv_priceChange;
        TextView tv_priceDate;
        TextView tv_priceAvg;
    }

    @Override
    protected void onDestroy() {
        mHistoryPrices.clear();

        super.onDestroy();
    }
}
