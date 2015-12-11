package com.tongxin.info.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tongxin.info.R;
import com.tongxin.info.page.sqListFragment;

/**
 * Created by Administrator on 2015/12/11.
 */
public class NotFoundActivity extends BaseActivity {
    private LinearLayout iv_return;
    private TextView tv_headerTitle;
    public static String CHANNEL_NAME = "CHANNELNAME";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notfound);
        Intent intent = getIntent();
        String channelName = intent.getStringExtra(CHANNEL_NAME);
        iv_return = (LinearLayout) findViewById(R.id.iv_return);
        tv_headerTitle = (TextView) findViewById(R.id.tv_headerTitle);
        tv_headerTitle.setText(channelName);
        iv_return.setVisibility(View.VISIBLE);
        iv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
//                Intent intent1 = new Intent(NotFoundActivity.this,sqListFragment.class);
//                startActivity(intent1);
//                finish();
            }
        });
    }
}
