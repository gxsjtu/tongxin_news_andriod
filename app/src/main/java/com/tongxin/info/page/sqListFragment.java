package com.tongxin.info.page;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Layout;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.actionsheet.ActionSheet;
import com.github.amlcurran.showcaseview.ShowcaseDrawer;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tongxin.info.R;
import com.tongxin.info.activity.BaseFragmentActivity;
import com.tongxin.info.activity.SqCatalogActivity;
import com.tongxin.info.control.SegmentedGroup;
import com.tongxin.info.domain.SqListVM;
import com.tongxin.info.global.GlobalContants;
import com.tongxin.info.utils.SharedPreUtils;
import com.tongxin.info.utils.ToastUtils;
import com.tongxin.info.utils.UserUtils;
import com.tongxin.info.utils.loadingUtils;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;
import java.lang.reflect.Type;
import java.util.ArrayList;

import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

/**
 * Created by cc on 2015/11/3.
 */
public class sqListFragment extends BaseFragmentActivity {
    private String tv_Name;
    private int tv_ID;
    private ArrayList<SqListVM> sqList = new ArrayList<SqListVM>();
    private ArrayList<SqListVM> resList = new ArrayList<SqListVM>();
    private ArrayList<SqListVM> searchList = new ArrayList<SqListVM>();
    private ListView lv_sqList;
    private RadioButton btn_GY;
    private RadioButton btn_XQ;
    private LinearLayout iv_sqReturn;
    private LinearLayout iv_sqMenu;
    private Button btn_headerSure;
    private TextView tv_headerText;
    private String typeForRefresh;
    private EditText name_searchTxt;
    private ImageView sq_searchImg;
    AppAdapter adapter;
    private Button sqbtn_CancelSearch;
    public ViewHolder viewHolder = null;
    ProgressDialog dialog;
    String mSupplyType = "供    应：";
    //boolean showOptionGuide = false;


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("tv_ID",tv_ID);
        outState.putString("tv_Name",tv_Name);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sq_list);
        //showOptionGuide = SharedPreUtils.getBoolean(this, "optionGuide", false);
        typeForRefresh = "false";
        if(savedInstanceState!=null)
        {
            tv_Name = savedInstanceState.getString("tv_Name");
            tv_ID = savedInstanceState.getInt("tv_ID");
        }
        else {
            Intent intent = getIntent();
            tv_Name = intent.getStringExtra("CHANNEL_NAME");
            tv_ID = intent.getIntExtra("CHANNEL_ID", 0);
        }
        lv_sqList = (ListView) findViewById(R.id.sq_lvData);


        tv_headerText = (TextView)findViewById(R.id.sq_HeaderText);
        tv_headerText.setVisibility(View.GONE);
        btn_headerSure = (Button)findViewById(R.id.btn_spHeaderSure);
        btn_headerSure.setVisibility(View.GONE);
//        sq_searchImg = (ImageView)findViewById(R.id.ivMsg_search);
        name_searchTxt = (EditText)findViewById(R.id.msg_search);
        name_searchTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (((actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) && event.getAction() == KeyEvent.ACTION_DOWN) || actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                    InputMethodManager imm = (InputMethodManager) sqListFragment.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(name_searchTxt.getWindowToken(), 0);
                    return true;
                } else {
                    return false;
                }
            }
        });
        name_searchTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    sqbtn_CancelSearch.setTextColor(Color.parseColor("#23B1EF"));
                    sqbtn_CancelSearch.setEnabled(true);
                }
                else {
                    sqbtn_CancelSearch.setTextColor(Color.GRAY);
                    sqbtn_CancelSearch.setEnabled(false);
                }
            }
        });
        sqbtn_CancelSearch = (Button)findViewById(R.id.sqbtn_CancelSearch);
        sqbtn_CancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name_searchTxt.setText("");
                name_searchTxt.clearFocus();
                sqbtn_CancelSearch.setTextColor(Color.GRAY);
                sqbtn_CancelSearch.setEnabled(false);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(name_searchTxt.getWindowToken(), 0);
                adapter = new AppAdapter();
                lv_sqList.setAdapter(adapter);
//                if(sqList != null && resList != null)
//                {
//                    sqList.clear();
//                    resList.clear();
//                }
//                initData(typeForRefresh);

            }
        });
        iv_sqReturn = (LinearLayout) findViewById(R.id.sq_ivReturn);
        iv_sqReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iv_sqMenu = (LinearLayout) findViewById(R.id.iv_sqMenu);


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

                            }

                            @Override
                            public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                            if(index == 0) {
                                if(sqList != null && resList != null)
                                {
                                    sqList.clear();
                                    resList.clear();
                                }
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
                mSupplyType="供    应：";
                initData(typeForRefresh);
            }
        });
        btn_XQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqList.clear();
                resList.clear();
                typeForRefresh = "true";
                mSupplyType="采    购：";
                initData(typeForRefresh);
            }
        });

        initData(typeForRefresh);
    }

    private void search()
    {
        showLoading();
        searchList.clear();

        String searchKey = name_searchTxt.getText().toString();
        String res = "";
        if(searchKey != null) {
//            res = searchKey.replace(" ", "");
            res = searchKey.trim();
        }
        if("".equals(res))
        {
            //searchList.addAll(resList);
            sqList.clear();
            resList.clear();
            initData(typeForRefresh);
        }
        else
        {
            for(int i = 0; i< resList.size();i++)
            {
                if(resList.get(i).name.toLowerCase().contains(res.toLowerCase()))
                {
                    searchList.add(resList.get(i));
                }
            }
        }
//        resList.clear();
//        resList.addAll(searchList);
        lv_sqList.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return searchList.size();
            }

            @Override
            public SqListVM getItem(int position) {
                return searchList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
//                ViewHolder viewHolder = null;
                viewHolder = null;
                SqListVM item = getItem(position);

                if (convertView == null) {
                    viewHolder = new ViewHolder();
                    convertView = View.inflate(sqListFragment.this, R.layout.sq_listcontent, null);
                    viewHolder.imgSqList = (ImageView) convertView.findViewById(R.id.img_sqList);
                    viewHolder.sqName = (TextView) convertView.findViewById(R.id.tv_ChannelName);
                    viewHolder.txt_sqContact = (TextView) convertView.findViewById(R.id.tv_sqContact);
                    viewHolder.txt_sqLocation = (TextView) convertView.findViewById(R.id.tv_sqLocation);
                    viewHolder.txt_sqDate = (TextView) convertView.findViewById(R.id.tv_sqDate);
                    viewHolder.txt_sqIsChecked = (TextView) convertView.findViewById(R.id.tv_sqIsChecked);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

//                ImageLoader.getInstance().displayImage(item.avatar, viewHolder.imgSqList);
                ImageLoader.getInstance().displayImage(item.avatar, viewHolder.imgSqList, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {
                        viewHolder.imgSqList.setImageResource(R.drawable.loading_img);
                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {

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

                if ("".equals(item.ischecked)) {
                    viewHolder.txt_sqIsChecked.setText("待审核");
                    viewHolder.txt_sqIsChecked.setTextColor(Color.BLACK);
                } else if ("true".equals(item.ischecked)) {
                    viewHolder.txt_sqIsChecked.setText("已审核");
                    viewHolder.txt_sqIsChecked.setTextColor(Color.parseColor("#006400"));
                } else {
                    viewHolder.txt_sqIsChecked.setText("已拒绝");
                    viewHolder.txt_sqIsChecked.setTextColor(Color.RED);
                }
                return convertView;
            }

        });
        hideLoading();
        lv_sqList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SqListVM item = searchList.get(position);
                Intent intent = new Intent(sqListFragment.this, sqDetailFragment.class);
                intent.putExtra("SQDETAIL_CHANNELNAME", "商圈 - " + tv_Name);
                intent.putExtra("SQDETAIL_CHANNELID", item.id);
                startActivity(intent);
            }
        });
    }

    private void initData(final String dataType) {
        if(UserUtils.Tel == null)
        {
            UserUtils.Tel = SharedPreUtils.getString(this,"name","");
        }
        showLoading();
        KJHttp kjHttp = new KJHttp();
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.TIMEOUT = 3 * 60 * 1000;
        kjHttp.setConfig(httpConfig);
        kjHttp.get(GlobalContants.GETSPLIST_URL + "?method=getsupply&createBy=" + UserUtils.Tel + "&channel=" + tv_ID, null, false, new HttpCallBack() {
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
                ToastUtils.Show(sqListFragment.this, "获取数据失败");
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
                adapter = new AppAdapter();
                lv_sqList.setAdapter(adapter);
                hideLoading();
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

    public class AppAdapter extends BaseAdapter {
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
                viewHolder = null;
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
                    viewHolder.tv_sqGQ = (TextView) convertView.findViewById(R.id.tv_sqGQ);
                    convertView.setTag(viewHolder);
                }
                else
                {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

//                ImageLoader.getInstance().displayImage(item.avatar,viewHolder.imgSqList);

                ImageLoader.getInstance().displayImage(item.avatar, viewHolder.imgSqList, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {
                        viewHolder.imgSqList.setImageResource(R.drawable.loading_img);
                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {

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
                viewHolder.tv_sqGQ.setText(mSupplyType);

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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(requestCode == 3)
//        {
//            tv_Name = data.getStringExtra("CHANNEL_NAME");
//            tv_ID = data.getIntExtra("CHANNEL_ID", 0);
//            initData(typeForRefresh);
//        }
    }

    public class ViewHolder
    {
        public ImageView imgSqList;
        public TextView sqName;
        public TextView txt_sqLocation;
        public TextView txt_sqContact;
        public TextView txt_sqDate;
        public TextView txt_sqIsChecked;
        public TextView tv_sqGQ;
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        Toast.makeText(this,"onNewIntent",Toast.LENGTH_SHORT).show();
//        super.onNewIntent(intent);
//    }
}
