package com.tongxin.info.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.tongxin.info.R;

public class HqDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hq_detail);
        Intent intent = getIntent();
        int id = intent.getIntExtra("marketId", 0);
        String name = intent.getStringExtra("marketName");
        TextView hqdetail_tv= (TextView) findViewById(R.id.hqdetail_tv);
        hqdetail_tv.setText(name);
    }


}
