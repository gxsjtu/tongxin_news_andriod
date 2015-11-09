package com.tongxin.info.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tongxin.info.R;

/**
 * Created by cc on 2015/10/22.
 */
public class InboxDetailActivity extends BaseActivity {
    private WebView webView;
    private LinearLayout iv_return;
    private TextView tv_headerTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_detail);
        iv_return = (LinearLayout) findViewById(R.id.iv_return);
        tv_headerTitle = (TextView) findViewById(R.id.tv_headerTitle);
        iv_return.setVisibility(View.VISIBLE);
        iv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        String url = intent.getStringExtra("inboxDetailUrl");
        String title = intent.getStringExtra("title");
        if(TextUtils.isEmpty(title))
        {
            tv_headerTitle.setText("");
        }
        else
        {
            tv_headerTitle.setText(title);
        }
        loadUrl(url);
    }

    private void loadUrl(String url) {
        webView = (WebView) findViewById(R.id.inboxDetailView);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
    }
}
