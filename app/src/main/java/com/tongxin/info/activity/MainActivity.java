package com.tongxin.info.activity;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.readystatesoftware.viewbadger.BadgeView;
import com.tongxin.info.R;
import com.tongxin.info.domain.MyApp;
import com.tongxin.info.page.boxFragment;
import com.tongxin.info.page.hqFragment;
import com.tongxin.info.page.meFragment;
import com.tongxin.info.page.plFragment;
import com.tongxin.info.page.sqFragment;
import com.tongxin.info.utils.BadgeUtils;
import com.tongxin.info.utils.ColorsUtils;
import com.tongxin.info.utils.SharedPreUtils;

import org.w3c.dom.Text;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by Administrator on 2015/9/21.
 */
public class MainActivity extends BaseFragmentActivity {
    private FrameLayout main_fl_content;
    private LinearLayout ll_inbox;
    private LinearLayout ll_hq;
    private LinearLayout ll_pl;
    private LinearLayout ll_sq;
    private LinearLayout ll_qh;


    private ImageView iv_inbox;
    private ImageView iv_hq;
    private ImageView iv_pl;
    private ImageView iv_sq;
    private ImageView iv_qh;

    private TextView tv_inbox;
    private TextView tv_hq;
    private TextView tv_pl;
    private TextView tv_sq;
    private TextView tv_qh;
    private FragmentManager fragmentManager;
    FragmentTransaction tran;
    BadgeView badge;
    BadgeBroadcast badgeBroadcast = null;
    int select = Color.rgb(0x00,0x79,0xff);
    int unselect = Color.rgb(0x92,0x92,0x92);


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        main_fl_content = (FrameLayout) findViewById(R.id.main_fl_content);
        ll_inbox = (LinearLayout) findViewById(R.id.ll_inbox);
        ll_hq = (LinearLayout) findViewById(R.id.ll_hq);
        ll_pl = (LinearLayout) findViewById(R.id.ll_pl);
        ll_sq = (LinearLayout) findViewById(R.id.ll_sq);
        ll_qh = (LinearLayout) findViewById(R.id.ll_qh);

        iv_inbox = (ImageView) findViewById(R.id.iv_inbox);
        iv_hq = (ImageView) findViewById(R.id.iv_hq);
        iv_pl = (ImageView) findViewById(R.id.iv_pl);
        iv_sq = (ImageView) findViewById(R.id.iv_sq);
        iv_qh = (ImageView) findViewById(R.id.iv_qh);

        tv_inbox = (TextView) findViewById(R.id.tv_inbox);
        tv_hq = (TextView) findViewById(R.id.tv_hq);
        tv_pl = (TextView) findViewById(R.id.tv_pl);
        tv_sq = (TextView) findViewById(R.id.tv_sq);
        tv_qh = (TextView) findViewById(R.id.tv_qh);

        badgeBroadcast = new BadgeBroadcast();
        IntentFilter filter = new IntentFilter("com.tongxin.badge");
        registerReceiver(badgeBroadcast, filter);

        initViews();

        badge = new BadgeView(this, ll_inbox);
        badge.setTextSize(10);
        setMessageBadge(0);
    }

    private void initViews() {

        fragmentManager = getSupportFragmentManager();

        //收件箱
        ll_inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                iv_inbox.setImageResource(R.mipmap.box);
                tv_inbox.setTextColor(select);

                iv_hq.setImageResource(R.mipmap.future_gray);
                tv_hq.setTextColor(unselect);

                iv_pl.setImageResource(R.mipmap.comment_gray);
                tv_pl.setTextColor(unselect);

                iv_sq.setImageResource(R.mipmap.sq_gray);
                tv_sq.setTextColor(unselect);

                iv_qh.setImageResource(R.mipmap.user_gray);
                tv_qh.setTextColor(unselect);


                showPage(new boxFragment());

            }
        });

        //行情
        ll_hq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                iv_inbox.setImageResource(R.mipmap.box_gray);
                tv_inbox.setTextColor(unselect);

                iv_hq.setImageResource(R.mipmap.future);
                tv_hq.setTextColor(select);

                iv_pl.setImageResource(R.mipmap.comment_gray);
                tv_pl.setTextColor(unselect);

                iv_sq.setImageResource(R.mipmap.sq_gray);
                tv_sq.setTextColor(unselect);

                iv_qh.setImageResource(R.mipmap.user_gray);
                tv_qh.setTextColor(unselect);

                showPage(new hqFragment());
            }
        });

        //评论
        ll_pl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                iv_inbox.setImageResource(R.mipmap.box_gray);
                tv_inbox.setTextColor(unselect);

                iv_hq.setImageResource(R.mipmap.future_gray);
                tv_hq.setTextColor(unselect);

                iv_pl.setImageResource(R.mipmap.comment);
                tv_pl.setTextColor(select);

                iv_sq.setImageResource(R.mipmap.sq_gray);
                tv_sq.setTextColor(unselect);

                iv_qh.setImageResource(R.mipmap.user_gray);
                tv_qh.setTextColor(unselect);

                showPage(new plFragment());
            }
        });

        //商圈
        ll_sq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                iv_inbox.setImageResource(R.mipmap.box_gray);
                tv_inbox.setTextColor(unselect);

                iv_hq.setImageResource(R.mipmap.future_gray);
                tv_hq.setTextColor(unselect);

                iv_pl.setImageResource(R.mipmap.comment_gray);
                tv_pl.setTextColor(unselect);

                iv_sq.setImageResource(R.mipmap.sq);
                tv_sq.setTextColor(select);

                iv_qh.setImageResource(R.mipmap.user_gray);
                tv_qh.setTextColor(unselect);

                showPage(new sqFragment());
            }
        });

        //期货
        ll_qh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_inbox.setImageResource(R.mipmap.box_gray);
                tv_inbox.setTextColor(unselect);

                iv_hq.setImageResource(R.mipmap.future_gray);
                tv_hq.setTextColor(unselect);

                iv_pl.setImageResource(R.mipmap.comment_gray);
                tv_pl.setTextColor(unselect);

                iv_sq.setImageResource(R.mipmap.sq_gray);
                tv_sq.setTextColor(unselect);

                iv_qh.setImageResource(R.mipmap.user);
                tv_qh.setTextColor(select);

                showPage(new meFragment());
            }
        });

        iv_inbox.setImageResource(R.mipmap.box);
        tv_inbox.setTextColor(select);

        iv_hq.setImageResource(R.mipmap.future_gray);
        tv_hq.setTextColor(unselect);

        iv_pl.setImageResource(R.mipmap.comment_gray);
        tv_pl.setTextColor(unselect);

        iv_sq.setImageResource(R.mipmap.sq_gray);
        tv_sq.setTextColor(unselect);

        iv_qh.setImageResource(R.mipmap.user_gray);
        tv_qh.setTextColor(unselect);


        showPage(new boxFragment());
        setMessageBadge(0);

    }

    private void showPage(Fragment fragment) {
        tran = fragmentManager.beginTransaction();
        tran.replace(R.id.main_fl_content, fragment);
        tran.commit();
    }

    public void setMessageBadge(int count) {

        if (count > 0) {
            badge.setText(String.valueOf(count));
            badge.setVisibility(View.VISIBLE);
            badge.show();
        } else {
            SharedPreUtils.setString(this, "badgecount", "0");
            NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(909);
            badge.setVisibility(View.GONE);
            badge.setText("");
            BadgeUtils.resetBadgeCount(this);
        }
    }

    public class BadgeBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int count = intent.getIntExtra("count",0);
            setMessageBadge(count);
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(badgeBroadcast);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        String count = SharedPreUtils.getString(this, "badgecount", "0");
        int badgeCount = Integer.parseInt(count);
        setMessageBadge(badgeCount);
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        iv_inbox.setImageResource(R.mipmap.box);
        tv_inbox.setTextColor(select);

        iv_hq.setImageResource(R.mipmap.future_gray);
        tv_hq.setTextColor(unselect);

        iv_pl.setImageResource(R.mipmap.comment_gray);
        tv_pl.setTextColor(unselect);

        iv_sq.setImageResource(R.mipmap.sq_gray);
        tv_sq.setTextColor(unselect);

        iv_qh.setImageResource(R.mipmap.user_gray);
        tv_qh.setTextColor(unselect);

        tran = fragmentManager.beginTransaction();
        tran.replace(R.id.main_fl_content, new boxFragment());
        tran.commitAllowingStateLoss();

        setMessageBadge(0);

        super.onNewIntent(intent);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
