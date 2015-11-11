package com.tongxin.info.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tongxin.info.R;
import com.tongxin.info.domain.ProductHistoryPrice;
import com.tongxin.info.utils.ColorsUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by Administrator on 2015/10/23.
 */
public class ChartActivity extends BaseActivity {
    LineChartView chart;
    private TextView tv_headerTitle;
    private LinearLayout iv_return;
    private String mTitle;
    private TextView tv_priceMin;
    private TextView tv_priceMax;
    private TextView tv_priceDate;
    private ArrayList<ProductHistoryPrice> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        Intent intent = getIntent();
        mTitle = intent.getStringExtra("title");
        data = (ArrayList<ProductHistoryPrice>) intent.getSerializableExtra("data");
        Collections.reverse(data);
        initViews();

        initData();
    }

    private void initViews() {
        chart = (LineChartView) findViewById(R.id.chart);
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

        if (data.size() > 0) {
            List<PointValue> maxPointValues = new ArrayList<PointValue>();
            List<PointValue> minPointValues = new ArrayList<PointValue>();

            for (int i = 0; i <data.size(); i++) {
                ProductHistoryPrice price = data.get(i);
                maxPointValues.add(new PointValue(i, Float.parseFloat(price.HPrice)));
                minPointValues.add(new PointValue(i, Float.parseFloat(price.LPrice)));
            }

            Line maxLine = new Line(maxPointValues);
            maxLine.setColor(ColorsUtils.DARKRED);

            Line minLine = new Line(minPointValues);
            minLine.setColor(ColorsUtils.DARKGREEN);

            List<Line> lines = new ArrayList<Line>();
            lines.add(maxLine);
            lines.add(minLine);

            LineChartData chartData = new LineChartData();
            chartData.setLines(lines);

            Axis axisY = new Axis();  //Y轴
            axisY.setMaxLabelChars(7);
            chartData.setAxisYLeft(axisY);

            chart.setInteractive(true);
            chart.setZoomType(ZoomType.HORIZONTAL);
            chart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
            chart.setLineChartData(chartData);

            chart.setOnValueTouchListener(new LineChartOnValueSelectListener() {
                @Override
                public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
                    ProductHistoryPrice price = data.get(pointIndex);
                    tv_priceDate.setText(price.Date);
                    tv_priceMax.setText("最高价" + price.HPrice);
                    tv_priceMin.setText("最低价" + price.LPrice);
                }

                @Override
                public void onValueDeselected() {

                }
            });
        }
    }
}
