package com.tongxin.info.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tongxin.info.R;
import com.tongxin.info.adapter.DragAdapter;
import com.tongxin.info.adapter.OtherAdapter;
import com.tongxin.info.control.DragGrid;
import com.tongxin.info.control.OtherGridView;
import com.tongxin.info.domain.ChannelItem;
import com.tongxin.info.domain.ChannelManage;
import com.tongxin.info.domain.ReOrderVM;
import com.tongxin.info.global.GlobalContants;
import com.tongxin.info.utils.UserUtils;

import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;
import org.kymjs.kjframe.http.HttpParams;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 频道管理
 *
 * @Author RA
 * @Blog http://blog.csdn.net/vipzjyno1
 */
public class ChannelActivity extends BaseActivity implements OnItemClickListener {
    /**
     * 用户栏目的GRIDVIEW
     */
    private DragGrid userGridView;
    /**
     * 其它栏目的GRIDVIEW
     */
    private OtherGridView otherGridView;
    private LinearLayout iv_return;
    private TextView tv_headerTitle;
    /**
     * 用户栏目对应的适配器，可以拖动
     */
    DragAdapter userAdapter;
    /**
     * 其它栏目对应的适配器
     */
    OtherAdapter otherAdapter;
    /**
     * 其它栏目列表
     */
    ArrayList<ChannelItem> otherChannelList = new ArrayList<ChannelItem>();
    /**
     * 用户栏目列表
     */
    ArrayList<ChannelItem> userChannelList = new ArrayList<ChannelItem>();
    /**
     * 是否在移动，由于这边是动画结束后才进行的数据更替，设置这个限制为了避免操作太频繁造成的数据错乱。
     */
    boolean isMove = false;

    public static String TYPETAG = "FromPage";
    private String type;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscribe_activity);

        initView();
        initData(savedInstanceState);
    }

    /**
     * 初始化数据
     */
    private void initData(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            type = savedInstanceState.getString("type");
            userChannelList = (ArrayList<ChannelItem>) savedInstanceState.getSerializable("user");
            otherChannelList = (ArrayList<ChannelItem>) savedInstanceState.getSerializable("other");
            UserUtils.Tel = savedInstanceState.getString("tel");
        } else {
            Intent intent = getIntent();
            type = intent.getStringExtra(TYPETAG);
            ChannelManage channelManage = new ChannelManage();
            channelManage.initChannel(type);
            userChannelList = channelManage.getUserChannel();
            otherChannelList = channelManage.getOtherChannel();

        }
        if(type.equals("hqFragment")) {
            tv_headerTitle.setText("行情频道管理");
        }
        else
        {
            tv_headerTitle.setText("评论频道管理");
        }
        userAdapter = new DragAdapter(this, userChannelList);
        userGridView.setAdapter(userAdapter);
        otherAdapter = new OtherAdapter(this, otherChannelList);
        otherGridView.setAdapter(this.otherAdapter);
        //设置GRIDVIEW的ITEM的点击监听
        otherGridView.setOnItemClickListener(this);
        userGridView.setOnItemClickListener(this);
    }

    /**
     * 初始化布局
     */
    private void initView() {
        userGridView = (DragGrid) findViewById(R.id.userGridView);
        otherGridView = (OtherGridView) findViewById(R.id.otherGridView);
        iv_return = (LinearLayout) findViewById(R.id.iv_return);
        tv_headerTitle = (TextView) findViewById(R.id.tv_headerTitle);

        iv_return.setVisibility(View.VISIBLE);
        iv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChannel();
                finish();
            }
        });
    }

    /**
     * GRIDVIEW对应的ITEM点击监听接口
     */
    @Override
    public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
        //如果点击的时候，之前动画还没结束，那么就让点击事件无效
        if (isMove) {
            return;
        }
        switch (parent.getId()) {
            case R.id.userGridView:
                //position为 0，1 的不可以进行任何操作
                if (position != 0) {
                    final ImageView moveImageView = getView(view);
                    if (moveImageView != null) {
                        TextView newTextView = (TextView) view.findViewById(R.id.text_item);
                        final int[] startLocation = new int[2];
                        newTextView.getLocationInWindow(startLocation);
                        final ChannelItem channel = ((DragAdapter) parent.getAdapter()).getItem(position);//获取点击的频道内容
                        otherAdapter.setVisible(false);
                        //添加到最后一个
                        otherAdapter.addItem(channel);
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                try {
                                    int[] endLocation = new int[2];
                                    //获取终点的坐标
                                    otherGridView.getChildAt(otherGridView.getLastVisiblePosition()).getLocationInWindow(endLocation);
                                    MoveAnim(moveImageView, startLocation, endLocation, channel, userGridView);
                                    userAdapter.setRemove(position);
                                } catch (Exception localException) {
                                }
                            }
                        }, 50L);
                    }
                }
                break;
            case R.id.otherGridView:
                final ImageView moveImageView1 = getView(view);
                if (moveImageView1 != null) {
                    TextView newTextView = (TextView) view.findViewById(R.id.text_item);
                    final int[] startLocation = new int[2];
                    newTextView.getLocationInWindow(startLocation);
                    final ChannelItem channel = ((OtherAdapter) parent.getAdapter()).getItem(position);
                    userAdapter.setVisible(false);
                    //添加到最后一个
                    userAdapter.addItem(channel);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            try {
                                int[] endLocation = new int[2];
                                //获取终点的坐标
                                userGridView.getChildAt(userGridView.getLastVisiblePosition()).getLocationInWindow(endLocation);
                                MoveAnim(moveImageView1, startLocation, endLocation, channel, otherGridView);
                                otherAdapter.setRemove(position);
                            } catch (Exception localException) {
                            }
                        }
                    }, 50L);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 点击ITEM移动动画
     *
     * @param moveView
     * @param startLocation
     * @param endLocation
     * @param moveChannel
     * @param clickGridView
     */
    private void MoveAnim(final View moveView, int[] startLocation, int[] endLocation, final ChannelItem moveChannel,
                          final GridView clickGridView) {
        int[] initLocation = new int[2];
        //获取传递过来的VIEW的坐标
        moveView.getLocationInWindow(initLocation);
        //得到要移动的VIEW,并放入对应的容器中
        final ViewGroup moveViewGroup = getMoveViewGroup();
        final View mMoveView = getMoveView(moveViewGroup, moveView, initLocation);
        //创建移动动画
        TranslateAnimation moveAnimation = new TranslateAnimation(
                startLocation[0], endLocation[0], startLocation[1],
                endLocation[1]);
        moveAnimation.setDuration(300L);//动画时间
        //动画配置
        AnimationSet moveAnimationSet = new AnimationSet(true);
        moveAnimationSet.setFillAfter(false);//动画效果执行完毕后，View对象不保留在终止的位置
        moveAnimationSet.addAnimation(moveAnimation);
        mMoveView.startAnimation(moveAnimationSet);

        moveAnimationSet.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                isMove = true;

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                moveViewGroup.removeView(mMoveView);
                // instanceof 方法判断2边实例是不是一样，判断点击的是DragGrid还是OtherGridView
                if (clickGridView instanceof DragGrid) {
                    otherAdapter.setVisible(true);
                    otherAdapter.notifyDataSetChanged();
                    userAdapter.remove();
                } else {
                    userAdapter.setVisible(true);
                    userAdapter.notifyDataSetChanged();
                    otherAdapter.remove();
                }
                isMove = false;
            }
        });
    }

    /**
     * 获取移动的VIEW，放入对应ViewGroup布局容器
     *
     * @param viewGroup
     * @param view
     * @param initLocation
     * @return
     */
    private View getMoveView(ViewGroup viewGroup, View view, int[] initLocation) {
        int x = initLocation[0];
        int y = initLocation[1];
        viewGroup.addView(view);
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mLayoutParams.leftMargin = x;
        mLayoutParams.topMargin = y;
        view.setLayoutParams(mLayoutParams);
        return view;
    }

    /**
     * 创建移动的ITEM对应的ViewGroup布局容器
     */
    private ViewGroup getMoveViewGroup() {
        ViewGroup moveViewGroup = (ViewGroup) getWindow().getDecorView();
        LinearLayout moveLinearLayout = new LinearLayout(this);
        moveLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        moveViewGroup.addView(moveLinearLayout);
        return moveLinearLayout;
    }

    /**
     * 获取点击的Item的对应View，
     *
     * @param view
     * @return
     */
    private ImageView getView(View view) {
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        Bitmap cache = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        ImageView iv = new ImageView(this);
        iv.setImageBitmap(cache);
        return iv;
    }

    /**
     * 退出时候保存
     */
    private void saveChannel() {
        List<ChannelItem> list = userAdapter.getChannnelLst();
        ReOrderVM vm = new ReOrderVM();
        vm.Tag = type;
        vm.list = list;
        EventBus.getDefault().post(vm);
        String strGroups = "";
        for (ChannelItem item : list) {
            strGroups += String.valueOf(item.getId() + "|");
        }
        String tel = UserUtils.Tel;
        KJHttp kjHttp = new KJHttp();
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.TIMEOUT = 3 * 60 * 1000;
        kjHttp.setConfig(httpConfig);
        HttpParams params = new HttpParams();
        params.put("method", "groupChannel");
        params.put("groups", strGroups);
        params.put("mobile", tel);
        String url = GlobalContants.SERVER_URL + "/Handlers/XHMarketHandler.ashx";
        if(type.equals("plFragment"))
        {
            url=GlobalContants.SERVER_URL + "/Handlers/PLHandler.ashx";
        }

        kjHttp.post(url, params, false, new HttpCallBack() {

        });
    }

    @Override
    public void onBackPressed() {
        saveChannel();
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("type",type);
        outState.putSerializable("user", userChannelList);
        outState.putSerializable("other", otherChannelList);
        outState.putString("tel", UserUtils.Tel);
        super.onSaveInstanceState(outState);
    }
}
