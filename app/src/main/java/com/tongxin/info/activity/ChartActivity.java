package com.tongxin.info.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.tongxin.info.R;
import com.tongxin.info.domain.ProductHistoryPrice;
import com.tongxin.info.utils.ColorsUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2015/10/23.
 */
public class ChartActivity extends BaseActivity {
    LineChart chart;
    private TextView tv_headerTitle;
    private LinearLayout iv_return;
    private String mTitle;
    private TextView tv_priceMin;
    private TextView tv_priceMax;
    private TextView tv_priceDate;
    private ArrayList<ProductHistoryPrice> historyPrices;
    ArrayList<String> xVals = new ArrayList<String>();
    ArrayList<Entry> yHigh = new ArrayList<Entry>();
    LineDataSet high = new LineDataSet(yHigh, "最高价");
    ArrayList<Entry> yLow = new ArrayList<Entry>();
    LineDataSet low = new LineDataSet(yLow, "最低价");
    ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
    float min = 0;
    float max = 0;

    private void getMaxAndMin() {
        try {
            for (int i = 0; i < historyPrices.size(); i++) {
                ProductHistoryPrice price = historyPrices.get(i);
                float low = Float.parseFloat(price.LPrice);
                float high = Float.parseFloat(price.HPrice);
                if (i == 0) {
                    min = low;
                    max = high;
                } else {
                    if (min > high) {
                        min = high;
                    }
                    if (min > low) {
                        min = low;
                    }
                    if (max < low) {
                        max = low;
                    }
                    if (max < high) {
                        max = high;
                    }
                }
            }
        }
        catch (NumberFormatException exception)
        {
            historyPrices.clear();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        Intent intent = getIntent();
        mTitle = intent.getStringExtra("title");
        historyPrices = (ArrayList<ProductHistoryPrice>) intent.getSerializableExtra("data");
        Collections.reverse(historyPrices);
        getMaxAndMin();

        float range = (max-min)/20;

        initViews();

        initData();

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        YAxis leftAxis = chart.getAxisLeft();
        YAxis rightAxis = chart.getAxisRight();

        // Y坐标轴最大值
        leftAxis.setAxisMaxValue(max+range);
        rightAxis.setAxisMaxValue(max+range);
        leftAxis.setLabelCount(10, true);
        rightAxis.setLabelCount(10, true);

        // Y坐标轴最小值
        leftAxis.setAxisMinValue(min-range);
        rightAxis.setAxisMinValue(min-range);

        leftAxis.setStartAtZero(false);
        rightAxis.setStartAtZero(false);
//        Paint paint = new Paint();
//        paint.setColor(Color.BLACK);
//        paint.setStrokeWidth(3);
//        chart.setPaint(paint,2);
//        chart.setBorderColor(Color.BLACK);
//        chart.setBorderWidth(3);
        chart.setBorderColor(Color.rgb(213, 216, 214));
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                int index = entry.getXIndex();
                ProductHistoryPrice historyPrice = historyPrices.get(index);
                tv_priceDate.setText("日期" + historyPrice.Date);
                tv_priceMax.setText("最高价" + historyPrice.HPrice);
                tv_priceMin.setText("最低价" + historyPrice.LPrice);
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private void initViews() {
        chart = (LineChart) findViewById(R.id.chart);

        tv_headerTitle = (TextView) findViewById(R.id.tv_headerTitle);
        iv_return = (LinearLayout) findViewById(R.id.iv_return);
        tv_priceMin = (TextView) findViewById(R.id.tv_priceMin);
        tv_priceMax = (TextView) findViewById(R.id.tv_priceMax);
        tv_priceDate = (TextView) findViewById(R.id.tv_priceDate);

        tv_headerTitle.setText(mTitle);

        iv_return.setVisibility(View.VISIBLE);
        iv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void initData() {

        chart.setDescription("");
        chart.setNoDataTextDescription("没有可显示的数据");
        chart.setNoDataText("");
        chart.animateX(1000);

        if (historyPrices.size() > 0) {

            for (int i = 0; i < historyPrices.size(); i++) {
                ProductHistoryPrice price = historyPrices.get(i);
                xVals.add(price.Date);
                yHigh.add(new Entry(Float.parseFloat(price.HPrice), i));
                yLow.add(new Entry(Float.parseFloat(price.LPrice), i));
            }
            high.setAxisDependency(YAxis.AxisDependency.LEFT);
            low.setAxisDependency(YAxis.AxisDependency.LEFT);
            high.setColor(Color.RED);
            high.setLineWidth(3f);
            high.setValueTextColor(Color.TRANSPARENT);
            low.setColor(Color.GREEN);
            low.setLineWidth(3f);
            low.setValueTextColor(Color.TRANSPARENT);
//            high.setHighLightColor(Color.BLACK);
//            low.setHighLightColor(Color.BLACK);
            high.setHighlightLineWidth(2);
            low.setHighlightLineWidth(2);
            dataSets.add(high);
            dataSets.add(low);
            LineData data = new LineData(xVals, dataSets);
            chart.setData(data);
        }
    }

    @Override
    protected void onDestroy() {
        historyPrices.clear();
        historyPrices = null;
        high.clear();
        high = null;
        low.clear();
        low = null;
        xVals.clear();;
        xVals = null;
        yHigh.clear();
        yHigh = null;
        yLow.clear();
        yLow = null;
        dataSets.clear();
        dataSets = null;

        mTitle = null;

        chart = null;
        tv_headerTitle = null;
        iv_return = null;
        tv_priceMin = null;
        tv_priceMax = null;
        tv_priceDate = null;

        super.onDestroy();
    }
}
