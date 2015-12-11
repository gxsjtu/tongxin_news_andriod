package com.tongxin.info.page;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tongxin.info.R;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/9/24.
 */
public class meFragment extends Fragment implements Serializable {
    private Activity mActivity;
    private WebView wv;
    private TextView tv_headerTitle;
    private LinearLayout iv_ref;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        container.removeAllViews();
        View view =View.inflate(mActivity, R.layout.mecontent,null);
        wv = (WebView) view.findViewById(R.id.wv);
        tv_headerTitle = (TextView) view.findViewById(R.id.tv_headerTitle);
        iv_ref = (LinearLayout) view.findViewById(R.id.iv_ref);
        tv_headerTitle.setText("期货行情");
        iv_ref.setVisibility(View.VISIBLE);
        iv_ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wv.reload();
            }
        });
        WebSettings settings = wv.getSettings();
        settings.setJavaScriptEnabled(true);
        //wv.loadUrl("http://dzwx.cfkd.net/shtxh5/");
        wv.loadUrl("http://futures.shtx.com.cn");
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }



}
