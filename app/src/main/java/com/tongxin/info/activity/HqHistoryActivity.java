package com.tongxin.info.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tongxin.info.R;
import com.tongxin.info.domain.MyApp;
import com.tongxin.info.domain.ProductHistoryPrice;
import com.tongxin.info.global.GlobalContants;
import com.tongxin.info.utils.ColorsUtils;
import com.tongxin.info.utils.loadingUtils;

import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;

import java.io.Serializable;
import java.lang.reflect.Type;
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
    private TextView tv_headerTitle;
    private ImageView iv_return;
    private TextView tv_headerChart;
    loadingUtils loadingUtils;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hq_history);

        Intent intent = getIntent();
        mProductId = intent.getIntExtra("productId", 0);
        mProductName = intent.getStringExtra("productName");

        tv_headerTitle = (TextView) findViewById(R.id.tv_headerTitle);
        tv_headerTitle.setText(mProductName);

        startDate = (EditText) findViewById(R.id.startDate);
        endDate = (EditText) findViewById(R.id.endDate);
        hq_history_lv = (ListView) findViewById(R.id.hq_history_lv);

        loadingUtils = new loadingUtils(this);

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

        iv_return = (ImageView) findViewById(R.id.iv_return);
        tv_headerChart = (TextView) findViewById(R.id.tv_headerChart);

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
                    chartIntent.putExtra("title",mProductName);
                    startActivity(chartIntent);
                }
            }
        });

        initData();
    }

    public void searchClick(View view) {
        search();
    }

    private void search() {
        String start = startDate.getText().toString();
        String end = endDate.getText().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d1 = sdf.parse(start);
            Date d2 = sdf.parse(end);

//            Calendar temp = Calendar.getInstance(Locale.CHINA);
//            temp.setTime(d2);
//            temp.add(Calendar.DAY_OF_YEAR,1);
//            d2 = temp.getTime();
//
//
//            int year = temp.get(Calendar.YEAR);
//            int month = temp.get(Calendar.MONTH);
//            int day = temp.get(Calendar.DAY_OF_MONTH);
//            end = getDateStr(year, month, day);

//            Calendar now = Calendar.getInstance();
//            now.add(Calendar.DAY_OF_YEAR, value);
//            return now.getTime();

            if (d2.getTime() - d1.getTime() < 0) {
                Toast.makeText(this, "截止日期不能小于开始日期", Toast.LENGTH_LONG).show();
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
        kjHttp.get(GlobalContants.GETHQHISTORYPRICES_URL + "&productId=" + mProductId + "&start=" + start + "&end=" + end,null,false, new HttpCallBack() {
            @Override
            public void onFailure(int errorNo, String strMsg) {
                Toast.makeText(HqHistoryActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPreStart() {
                loadingUtils.show();
            }

            @Override
            public void onFinish() {
                loadingUtils.close();
            }

            @Override
            public void onSuccess(String t) {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<ProductHistoryPrice>>() {
                }.getType();
                mHistoryPrices = gson.fromJson(t, type);

                if(mHistoryPrices.size() == 0)
                {
                    tv_headerChart.setVisibility(View.INVISIBLE);
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
                            convertView.setTag(viewHolder);
                        } else {
                            viewHolder = (ViewHolder) convertView.getTag();
                        }

                        ProductHistoryPrice price = mHistoryPrices.get(position);

                        viewHolder.tv_priceDate.setText(price.Date);
                        viewHolder.tv_priceMin.setText(price.LPrice);
                        viewHolder.tv_priceMax.setText(price.HPrice);
                        if (!TextUtils.isEmpty(price.Change)) {
                            Double change = Double.parseDouble(price.Change);
                            if (change > 0) {
                                viewHolder.tv_priceChange.setText("涨 " + String.format("%.2f", change)+"▲");
                                viewHolder.tv_priceChange.setTextColor(ColorsUtils.HIGH);
                            } else if (change < 0) {
                                viewHolder.tv_priceChange.setText("跌 " + String.format("%.2f", Math.abs(change))+"▼");
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

        calendar.add(Calendar.DAY_OF_MONTH, -15);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH) + 1;
        startDate.setText(getDateStr(year, month, day));

        search();
    }

    private static class ViewHolder {
        TextView tv_priceMin;
        TextView tv_priceMax;
        TextView tv_priceChange;
        TextView tv_priceDate;
    }

}
