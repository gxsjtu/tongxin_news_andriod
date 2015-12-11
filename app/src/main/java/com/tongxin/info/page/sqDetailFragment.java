package com.tongxin.info.page;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tongxin.info.R;
import com.tongxin.info.activity.BaseActivity;
import com.tongxin.info.activity.DetailForShowImg;
import com.tongxin.info.domain.SQDetailVM;
import com.tongxin.info.global.GlobalContants;
import com.tongxin.info.utils.ToastUtils;
import com.tongxin.info.utils.loadingUtils;

import org.json.JSONObject;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;
import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Created by cc on 2015/11/5.
 */
public class sqDetailFragment extends BaseActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    private SliderLayout mDemoSlider;
    private TextView tv_headerTitle;
    private int sq_channelID;
    private TextView tv_sqDetailName;
    private TextView tv_sqDetailQty;
    private TextView tv_sqDetailPrice;
    private TextView tv_sqDetailContact;
    private TextView btn_sqDetailMobile;
    private TextView tv_sqDetailLocation;
    private TextView tv_sqDetailDeliver;
    private TextView tv_sqDetailDesc;
    private String mobile;
    private LinearLayout iv_return;
    private LinearLayout iv_ref;
    private String url;
    String title;
    HashMap<String, String> url_maps = new HashMap<String, String>();
    private boolean isClickedComplete = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sq_itemdetail);

        tv_sqDetailName = (TextView) findViewById(R.id.sq_detailName);
        tv_sqDetailQty = (TextView) findViewById(R.id.sq_detailQty);
        tv_sqDetailPrice = (TextView) findViewById(R.id.sq_detailPrice);
        tv_sqDetailContact = (TextView) findViewById(R.id.sq_detailContact);
        btn_sqDetailMobile = (TextView) findViewById(R.id.sq_detailMobile);
        iv_ref = (LinearLayout)findViewById(R.id.iv_ref);
        iv_ref.setVisibility(View.VISIBLE);
        iv_ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDemoSlider.removeAllSliders();
                initData();
            }
        });
        iv_return = (LinearLayout) findViewById(R.id.iv_return);
        iv_return.setVisibility(View.VISIBLE);
        iv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDemoSlider.removeAllSliders();
                finish();
            }
        });
        btn_sqDetailMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"".equals(mobile)) {
                    callMoile(mobile);
                }
            }
        });
        tv_sqDetailLocation = (TextView) findViewById(R.id.sq_detailLocation);
        tv_sqDetailDeliver = (TextView) findViewById(R.id.sq_detailDeliver);
        tv_sqDetailDesc = (TextView) findViewById(R.id.sq_detailDesc);
        mDemoSlider = (SliderLayout) findViewById(R.id.sq_imgSlider);
        tv_headerTitle = (TextView) findViewById(R.id.tv_headerTitle);
        if(savedInstanceState!=null) {
            sq_channelID = savedInstanceState.getInt("sq_channelID");
            title = savedInstanceState.getString("title");
        }
        else {
            Intent intent = getIntent();
            sq_channelID = intent.getIntExtra("SQDETAIL_CHANNELID", 0);
            title = intent.getStringExtra("SQDETAIL_CHANNELNAME");
        }
        if (!"".equals(title) && title != null) {
            tv_headerTitle.setText(title);
        } else {
            tv_headerTitle.setText("我的发布");
        }
        initData();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("sq_channelID",sq_channelID);
        outState.putString("title",title);
        super.onSaveInstanceState(outState);
    }

    private void initData() {
        showLoading();
        KJHttp kjHttp = new KJHttp();
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.TIMEOUT = 3 * 60 * 1000;
        kjHttp.setConfig(httpConfig);
        kjHttp.get(GlobalContants.GETSPLIST_URL + "?method=getitem&id=" + String.valueOf(sq_channelID), null, false, new HttpCallBack() {
            @Override
            public void onPreStart() {
                super.onPreStart();
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                hideLoading();
                ToastUtils.Show(getApplicationContext(), "获取数据失败");
            }

            @Override
            public void onSuccess(String t) {

                Gson gson = new Gson();
                Type type = new TypeToken<SQDetailVM>() {
                }.getType();

                SQDetailVM detail = new SQDetailVM();
                detail = gson.fromJson(t, type);

                if (detail.errorcode.equals("error")) {
                    ToastUtils.ShowLong(getApplicationContext(), "该供需已被删除，请点击右上角按钮刷新供需列表页面");
                    finish();
                    return;
                }

                tv_sqDetailName.setText(detail.name);
                tv_sqDetailQty.setText(detail.quantity);
                tv_sqDetailPrice.setText(detail.price);
                tv_sqDetailContact.setText(detail.contact);
                tv_sqDetailLocation.setText(detail.location);
                mobile = detail.mobile;
                btn_sqDetailMobile.setText(detail.mobile + " （点击拨打）");
                tv_sqDetailDesc.setText(detail.description);
                if ("true".equals(detail.deliver)) {
                    tv_sqDetailDeliver.setText("自提");
                } else {
                    tv_sqDetailDeliver.setText("发货");
                }

//                HashMap<String, String> url_maps = new HashMap<String, String>();
                for (int i = 0; i < detail.avatars.size(); i++) {
                    url_maps.put(String.valueOf(i + 1), detail.avatars.get(i).avatar);
                }

                for (String name : url_maps.keySet()) {
                    TextSliderView textSliderView = new TextSliderView(sqDetailFragment.this);

                    textSliderView.image(url_maps.get(name)).setOnSliderClickListener(sqDetailFragment.this);

                    //add your extra information
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle()
                            .putString("extra", name);

                    mDemoSlider.addSlider(textSliderView);
                }
                mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
                mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                mDemoSlider.setCustomAnimation(new DescriptionAnimation());
                mDemoSlider.setDuration(4000);
                mDemoSlider.addOnPageChangeListener(sqDetailFragment.this);


                if (url_maps.size() == 1) {
                    mDemoSlider.stopAutoCycle();

                }

                hideLoading();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == 20)
        {
            if (url_maps.size() == 1) {
                mDemoSlider.stopAutoCycle();
            }
            else {
                mDemoSlider.startAutoCycle();
            }
            isClickedComplete = true;
        }
    }

    private void callMoile(String tel) {
        if (tel != null && !"".equals(tel)) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel));
            startActivity(intent);
        }
    }

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        if(isClickedComplete) {
            url = slider.getUrl();
            Intent intent = new Intent(sqDetailFragment.this, DetailForShowImg.class);
            intent.putExtra("IMGURLFORSHOW", url);
//        startActivity(intent);
            startActivityForResult(intent, 20);
            isClickedComplete = false;
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        //Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

}
