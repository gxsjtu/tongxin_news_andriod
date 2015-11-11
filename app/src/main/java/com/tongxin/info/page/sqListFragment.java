package com.tongxin.info.page;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.actionsheet.ActionSheet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tongxin.info.R;
import com.tongxin.info.activity.InboxDetailActivity;
import com.tongxin.info.activity.SqCatalogActivity;
import com.tongxin.info.control.SegmentedGroup;
import com.tongxin.info.domain.SqListVM;
import com.tongxin.info.global.GlobalContants;
import com.tongxin.info.utils.UserUtils;
import com.tongxin.info.utils.loadingUtils;

import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLClientInfoException;
import java.util.ArrayList;

/**
 * Created by cc on 2015/11/3.
 */
public class sqListFragment extends FragmentActivity {
    private String tv_Name;
    private int tv_ID;
    loadingUtils loadingUtils;
    private String tel;
    private ArrayList<SqListVM> sqList = new ArrayList<SqListVM>();
    private ArrayList<SqListVM> resList = new ArrayList<SqListVM>();
    private ListView lv_sqList;
    private RadioButton btn_GY;
    private RadioButton btn_XQ;
    private ImageView iv_sqReturn;
    private ImageView iv_sqMenu;
    private Button btn_headerSure;
    private TextView tv_headerText;
    private String typeForRefresh;
    private FragmentManager fragmentManager;
    FragmentTransaction tran;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sq_list);
        typeForRefresh = "false";
        Intent intent = getIntent();
        tv_Name = intent.getStringExtra("CHANNEL_NAME");
        tv_ID = intent.getIntExtra("CHANNEL_ID", 0);
        UserUtils userUtils = new UserUtils(this);
        tel = userUtils.getTel();
        lv_sqList = (ListView) findViewById(R.id.sq_lvData);


        tv_headerText = (TextView)findViewById(R.id.sq_HeaderText);
        tv_headerText.setVisibility(View.GONE);
        btn_headerSure = (Button)findViewById(R.id.btn_spHeaderSure);
        btn_headerSure.setVisibility(View.GONE);
        fragmentManager = getSupportFragmentManager();
        iv_sqReturn = (ImageView)findViewById(R.id.sq_ivReturn);
        iv_sqReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                tran = fragmentManager.beginTransaction();
//                tran.replace(R.id.main_fl_content,new sqFragment());
//                tran.commit();
            }
        });
        iv_sqMenu = (ImageView)findViewById(R.id.iv_sqMenu);
        iv_sqMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionSheet.createBuilder(sqListFragment.this, getSupportFragmentManager())
                        .setCancelButtonTitle("取消")
                        .setOtherButtonTitles("刷新供需列表", "发布供需")
                        .setCancelableOnTouchOutside(true)
                        .setListener(new ActionSheet.ActionSheetListener() {
                            @Override
                            public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
                               // Toast.makeText(sqListFragment.this, "取消", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                            if(index == 0) {
                                initData(typeForRefresh);
                            }
                                else if(index == 1) {
                                Intent intent = new Intent(sqListFragment.this,SqCatalogActivity.class);
                                intent.putExtra("CATALOGCHANNEL_ID",tv_ID);
                                intent.putExtra("CATALOGCAHNNEL_NAME", tv_Name);
                                startActivity(intent);
                            }
                            }
                        }).show();
            }
        });

        SegmentedGroup segmented2 = (SegmentedGroup) findViewById(R.id.sq_segmented1);
        segmented2.setTintColor(Color.WHITE,Color.parseColor("#23B1EF"));

        btn_GY = (RadioButton)findViewById(R.id.bt_SqTabGY);
        btn_XQ = (RadioButton)findViewById(R.id.bt_SqlTabXQ);
        btn_GY.setChecked(true);

        btn_GY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqList.clear();
                resList.clear();
                typeForRefresh ="false";
                initData(typeForRefresh);
            }
        });
        btn_XQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqList.clear();
                resList.clear();
                typeForRefresh = "true";
                initData(typeForRefresh);
            }
        });

        initData(typeForRefresh);
    }

    private void initData(final String dataType) {
        KJHttp kjHttp = new KJHttp();
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.TIMEOUT = 3 * 60 * 1000;
        kjHttp.setConfig(httpConfig);
        kjHttp.get(GlobalContants.GETSPLIST_URL + "?method=getsupply&createBy=" + tel + "&channel=" + tv_ID, null, false, new HttpCallBack() {
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
                Toast.makeText(sqListFragment.this, "获取数据失败" + strMsg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String t) {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<SqListVM>>() {
                }.getType();

                sqList = gson.fromJson(t, type);
                if(sqList != null && sqList.size() > 0) {
                    for (int i = 0;i<sqList.size();i++) {
                        if(dataType.equals(sqList.get(i).type))
                        {
                            resList.add(sqList.get(i));
                        }
                    }
                }
                lv_sqList.setAdapter(new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return resList.size();
                    }

                    @Override
                    public SqListVM getItem(int position) {
                        return resList.get(position);
                    }

                    @Override
                    public long getItemId(int position) {
                        return position;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        ViewHolder viewHolder = null;
                        SqListVM item = getItem(position);

                        if(convertView == null)
                        {
                            viewHolder = new ViewHolder();
                            convertView = View.inflate(sqListFragment.this,R.layout.sq_listcontent,null);
                            viewHolder.imgSqList = (ImageView) convertView.findViewById(R.id.img_sqList);
                            viewHolder.sqName = (TextView) convertView.findViewById(R.id.tv_ChannelName);
                            viewHolder.txt_sqContact = (TextView)convertView.findViewById(R.id.tv_sqContact);
                            viewHolder.txt_sqLocation = (TextView)convertView.findViewById(R.id.tv_sqLocation);
                            viewHolder.txt_sqDate = (TextView) convertView.findViewById(R.id.tv_sqDate);
                            viewHolder.txt_sqIsChecked = (TextView) convertView.findViewById(R.id.tv_sqIsChecked);
                            convertView.setTag(viewHolder);
                        }
                        else
                        {
                            viewHolder = (ViewHolder) convertView.getTag();
                        }

                        ImageLoader.getInstance().displayImage(item.avatar,viewHolder.imgSqList);
//                        kjb.display(viewHolder.imgSqList, item.avatar);
                        viewHolder.sqName.setText(item.name);
                        viewHolder.txt_sqDate.setText(item.date);
                        viewHolder.txt_sqContact.setText(item.contact);
                        viewHolder.txt_sqIsChecked.setText(item.ischecked);
                        viewHolder.txt_sqLocation.setText(item.location);

                        if("".equals(item.ischecked))
                        {
                            viewHolder.txt_sqIsChecked.setText("待审核");
                        }
                        else if("true".equals(item.ischecked))
                        {
                            viewHolder.txt_sqIsChecked.setText("已审核");
                        }
                        else
                        {
                            viewHolder.txt_sqIsChecked.setText("已拒绝");
                        }

                        return convertView;
                    }
                });

                lv_sqList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        SqListVM item = resList.get(position);
                        Intent intent = new Intent(sqListFragment.this,sqDetailFragment.class);
                        intent.putExtra("SQDETAIL_CHANNELNAME", "商圈 - " + tv_Name);
                        intent.putExtra("SQDETAIL_CHANNELID",item.id);
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
    }
}
