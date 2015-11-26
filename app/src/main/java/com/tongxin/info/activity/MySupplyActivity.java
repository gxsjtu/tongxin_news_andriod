package com.tongxin.info.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tongxin.info.R;
import com.tongxin.info.domain.SqListVM;
import com.tongxin.info.global.GlobalContants;
import com.tongxin.info.page.sqDetailFragment;
import com.tongxin.info.utils.ToastUtils;
import com.tongxin.info.utils.UserUtils;
import com.tongxin.info.utils.loadingUtils;

import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by cc on 2015/11/11.
 */
public class MySupplyActivity extends BaseActivity {
    private String tel;
    private ListView lv_mySupply;
    private ArrayList<SqListVM> mySupplyList = new ArrayList<SqListVM>();
    private TextView tv_headerTitle;
    private LinearLayout iv_return;
    private LinearLayout iv_ref;
    loadingUtils loadingUtils;
    private ViewHolder viewHolder = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mysupply);

        loadingUtils = new loadingUtils(this);
        lv_mySupply = (ListView)findViewById(R.id.lv_mySupply);
        UserUtils userUtils = new UserUtils(this);
        tel = userUtils.getTel();
        tv_headerTitle = (TextView) findViewById(R.id.tv_headerTitle);
        tv_headerTitle.setText("我的发布");
        iv_return = (LinearLayout) findViewById(R.id.iv_return);
        iv_return.setVisibility(View.VISIBLE);
        iv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iv_ref =(LinearLayout)findViewById(R.id.iv_ref);
        iv_ref.setVisibility(View.VISIBLE);
        iv_ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
            }
        });
        initData();
    }

    private void initData()
    {
        loadingUtils.show();
        KJHttp kjHttp = new KJHttp();
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.TIMEOUT = 3 * 60 * 1000;
        kjHttp.setConfig(httpConfig);
        kjHttp.get(GlobalContants.GETSPLIST_URL + "?method=mysupply&mobile=" + tel, null, false, new HttpCallBack() {
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

                loadingUtils.close();
                ToastUtils.Show(MySupplyActivity.this, "获取数据失败" + strMsg);
            }

            @Override
            public void onSuccess(String t) {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<SqListVM>>()
                {
                }.getType();

                mySupplyList = gson.fromJson(t,type);
                lv_mySupply.setAdapter(new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return mySupplyList.size();
                    }

                    @Override
                    public SqListVM getItem(int position) {
                        return mySupplyList.get(position);
                    }

                    @Override
                    public long getItemId(int position) {
                        return position;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        viewHolder = null;
                        SqListVM item = getItem(position);
                        if(convertView == null)
                        {
                            viewHolder = new ViewHolder();
                            convertView = View.inflate(MySupplyActivity.this,R.layout.sq_listcontent,null);
                            viewHolder.imgSqList = (ImageView) convertView.findViewById(R.id.img_sqList);
                            viewHolder.sqName = (TextView) convertView.findViewById(R.id.tv_ChannelName);
                            viewHolder.txt_sqContact = (TextView)convertView.findViewById(R.id.tv_sqContact);
                            viewHolder.txt_sqLocation = (TextView)convertView.findViewById(R.id.tv_sqLocation);
                            viewHolder.txt_sqDate = (TextView) convertView.findViewById(R.id.tv_sqDate);
                            viewHolder.txt_sqIsChecked = (TextView) convertView.findViewById(R.id.tv_sqIsChecked);
                            viewHolder.tv_Type = (TextView)convertView.findViewById(R.id.tv_sqGQ);
                            convertView.setTag(viewHolder);
                        }
                        else
                        {
                            viewHolder = (ViewHolder)convertView.getTag();
                        }

//                        ImageLoader.getInstance().displayImage(item.avatar,viewHolder.imgSqList);
                        ImageLoader.getInstance().displayImage(item.avatar, viewHolder.imgSqList, new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String s, View view) {
                                viewHolder.imgSqList.setImageResource(R.drawable.loading_img);
                            }

                            @Override
                            public void onLoadingFailed(String s, View view, FailReason failReason) {
//                                viewHolder.imgSqList.setImageResource(R.drawable.loading_img);
                            }

                            @Override
                            public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                            }

                            @Override
                            public void onLoadingCancelled(String s, View view) {

                            }
                        });

                        viewHolder.sqName.setText(item.name);
                        viewHolder.txt_sqDate.setText(item.date);
                        viewHolder.txt_sqContact.setText(item.contact);
                        viewHolder.txt_sqIsChecked.setText(item.ischecked);
                        viewHolder.txt_sqLocation.setText(item.location);

                        if("false".equals(item.type))
                        {
                            viewHolder.tv_Type.setText("销售");
                            viewHolder.tv_Type.setTextColor(Color.RED);
                        }
                        else
                        {
                            viewHolder.tv_Type.setText("采购");
                            viewHolder.tv_Type.setTextColor(Color.parseColor("#006400"));
                        }

                        if("".equals(item.ischecked))
                        {
                            viewHolder.txt_sqIsChecked.setText("待审核");
                            viewHolder.txt_sqIsChecked.setTextColor(Color.BLACK);
                        }
                        else if("true".equals(item.ischecked))
                        {
                            viewHolder.txt_sqIsChecked.setText("已审核");
                            viewHolder.txt_sqIsChecked.setTextColor(Color.parseColor("#006400"));
                        }
                        else
                        {
                            viewHolder.txt_sqIsChecked.setText("已拒绝");
                            viewHolder.txt_sqIsChecked.setTextColor(Color.RED);
                        }

                        return convertView;
                    }
                });
                loadingUtils.close();
                lv_mySupply.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        SqListVM item = mySupplyList.get(position);
                        Intent intent = new Intent(MySupplyActivity.this, sqDetailFragment.class);
//                        intent.putExtra("SQDETAIL_CHANNELNAME", "商圈 - " + tv_Name);
                        intent.putExtra("SQDETAIL_CHANNELID", item.id);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    public class ViewHolder
    {
        public ImageView imgSqList;
        public TextView sqName;
        public TextView txt_sqLocation;
        public TextView txt_sqContact;
        public TextView txt_sqDate;
        public TextView txt_sqIsChecked;
        public TextView tv_Type;
    }

    @Override
    protected void onDestroy() {
        mySupplyList.clear();
        mySupplyList = null;
        lv_mySupply = null;
        tv_headerTitle = null;
        iv_return = null;
        iv_ref = null;
        loadingUtils = null;

        super.onDestroy();
    }
}
