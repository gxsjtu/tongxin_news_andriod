package com.tongxin.info.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.tongxin.info.R;
import com.tongxin.info.global.GlobalContants;
import com.tongxin.info.utils.UserUtils;

/**
 * Created by cc on 2015/10/22.
 */
public class InboxDetailActivity extends BaseActivity {
    String url;
    String sharedUrl;
    String title;
    String descript;
    String sharedicon;
    Toolbar toolbar;
    private LinearLayout iv_return;
    private LinearLayout iv_more;
    private LinearLayout popup_menu_wx;
    private LinearLayout popup_menu_qq;
    private LinearLayout popup_menu_friend;
    private LinearLayout popup_menu_qqzone;
    private TextView tv_headerTitle;
    private WebView webView;
    private IWXAPI api;
    private PopupWindow window;
    Tencent mTencent;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("inboxDetailUrl", url);
        outState.putString("title", title);
        outState.putString("descript", descript);
        outState.putString("sharedicon", sharedicon);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_detail);
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle("");
//        setSupportActionBar(toolbar);
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }

        //tv_headerTitle = (TextView) findViewById(R.id.toolbar_header);
        api = WXAPIFactory.createWXAPI(this, GlobalContants.APP_ID);
        mTencent = Tencent.createInstance(GlobalContants.QQAPP_ID, this.getApplicationContext());

        initPopwindow();

        iv_return = (LinearLayout) findViewById(R.id.iv_return);
        iv_more = (LinearLayout) findViewById(R.id.iv_more);
        tv_headerTitle = (TextView) findViewById(R.id.tv_headerTitle);
        iv_more.setVisibility(View.VISIBLE);
        iv_return.setVisibility(View.VISIBLE);
        iv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopwindow();
            }
        });
        if (savedInstanceState != null) {
            url = savedInstanceState.getString("inboxDetailUrl");
            title = savedInstanceState.getString("title");
            descript = savedInstanceState.getString("descript");
            sharedicon = savedInstanceState.getString("sharedicon");
            sharedUrl = url.replace(UserUtils.Tel, "");
        } else {
            Intent intent = getIntent();
            url = intent.getStringExtra("inboxDetailUrl");
            title = intent.getStringExtra("title");
            descript = intent.getStringExtra("descript");
            sharedicon = intent.getStringExtra("sharedicon");
            sharedUrl = url.replace(UserUtils.Tel, "");
        }
        if (TextUtils.isEmpty(title)) {
            tv_headerTitle.setText("");
        } else {
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
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//                super.onReceivedError(view, request, error);
                view.loadUrl("http://api.shtx.com.cn/upload/404.html");
            }
        });
    }

    private void shared2Wx(final int scene) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = sharedUrl;

        final WXMediaMessage msg = new WXMediaMessage(webpage);
        //msg.title = "同鑫资讯";
        msg.title = descript;
        //msg.description = descript;
        if(TextUtils.isEmpty(sharedicon)) {
            Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.shareddefault);
            msg.thumbData = UserUtils.bmpToByteArray(thumb, true);
            thumb.recycle();
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = buildTransaction("webpage");
            req.message = msg;
            req.scene = scene;
            api.sendReq(req);
        }
        else
        {
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    //msg.setThumbImage(bitmap);
                    msg.thumbData = UserUtils.bmpToByteArray(bitmap, false);
                    //bitmap.recycle();
                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                    req.transaction = buildTransaction("webpage");
                    req.message = msg;
                    req.scene = scene;
                    api.sendReq(req);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            Picasso.with(this).load(sharedicon).into(target);
        }

//        SendMessageToWX.Req req = new SendMessageToWX.Req();
//        req.transaction = buildTransaction("webpage");
//        req.message = msg;
//        req.scene = scene;
//        api.sendReq(req);
    }

    private void shared2Qq(boolean shared2Zone)
    {
        ShareListener myListener = new ShareListener();
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, descript);
//        params.putString(QQShare.SHARE_TO_QQ_TITLE, "同鑫资讯");
//        params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  descript);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  sharedUrl);
        if(!TextUtils.isEmpty(sharedicon)) {
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, sharedicon);
        }
        else
        {
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://api.shtx.com.cn/upload/default.png");
        }
        if(shared2Zone)
        {
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT,  QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        }
        mTencent.shareToQQ(InboxDetailActivity.this, params, myListener);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ShareListener myListener = new ShareListener();
        Tencent.onActivityResultData(requestCode, resultCode, data, myListener);
    }

    private class ShareListener implements IUiListener {
        @Override
        public void onCancel() {
            //Toast.makeText(getBaseContext(),"分享取消",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onComplete(Object arg0) {
            Toast.makeText(getBaseContext(),"分享成功",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(UiError arg0) {
            Toast.makeText(getBaseContext(),"分享出错",Toast.LENGTH_SHORT).show();
        }
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    private void initPopwindow()
    {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_pl, null);
        window = new PopupWindow(view,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

        window.setFocusable(true);

//        ColorDrawable dw = new ColorDrawable(0xff23B1EF);
        window.setBackgroundDrawable(new BitmapDrawable());
        view.setBackgroundResource(R.drawable.popupwindow_back);

        popup_menu_wx = (LinearLayout) view.findViewById(R.id.popup_menu_wx);
        popup_menu_qq = (LinearLayout) view.findViewById(R.id.popup_menu_qq);
        popup_menu_friend = (LinearLayout) view.findViewById(R.id.popup_menu_friend);
        //popup_menu_qqzone = (LinearLayout) view.findViewById(R.id.popup_menu_qqzone);

        popup_menu_wx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shared2Wx(SendMessageToWX.Req.WXSceneSession);
                hidePopwindow();
            }
        });

        popup_menu_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shared2Qq(false);
                hidePopwindow();
            }
        });

        popup_menu_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shared2Wx(SendMessageToWX.Req.WXSceneTimeline);
                hidePopwindow();
            }
        });

//        popup_menu_qqzone.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                shared2Qq(true);
//                hidePopwindow();
//            }
//        });

    }

    private void showPopwindow()
    {



//        window.showAtLocation(this.findViewById(R.id.iv_more),
//                Gravity.BOTTOM, 0, 0);

//        int width = window.getWidth();

        window.showAsDropDown(iv_more);


    }

    private void hidePopwindow()
    {
        if(window!=null && window.isShowing())
        {
            window.dismiss();
        }
    }

}
