package com.tongxin.info.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tongxin.info.R;
import com.tongxin.info.control.SegmentedGroup;
import com.tongxin.info.domain.SqCatalogVM;
import com.tongxin.info.global.GlobalContants;
import com.tongxin.info.page.sqListFragment;
import com.tongxin.info.utils.loadingUtils;

import org.kymjs.kjframe.*;
import org.kymjs.kjframe.http.*;
import org.kymjs.kjframe.http.*;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by cc on 2015/11/6.
 */
public class SqCatalogActivity extends BaseActivity {
    private ListView lv_catalog;
    private int channelID;
    private String channelName;
    private ArrayList<SqCatalogVM> sqCataLogList = new ArrayList<SqCatalogVM>();
    private ImageView iv_sqReturn;
    private ImageView iv_sqMenu;
    private Button btn_headerSure;
    private TextView tv_headerText;
    loadingUtils loadingUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sq_cataloglist);

        loadingUtils = new loadingUtils(this);
        Intent intent = getIntent();
        channelID = intent.getIntExtra("CATALOGCHANNEL_ID",0);
        channelName = intent.getStringExtra("CATALOGCAHNNEL_NAME");
        tv_headerText = (TextView)findViewById(R.id.sq_HeaderText);
        btn_headerSure = (Button)findViewById(R.id.btn_spHeaderSure);
        btn_headerSure.setVisibility(View.GONE);
        iv_sqReturn = (ImageView)findViewById(R.id.sq_ivReturn);
        iv_sqReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SqCatalogActivity.this, sqListFragment.class);
                intent.putExtra("CHANNEL_ID",channelID);
                intent.putExtra("CHANNEL_NAME", channelName);
//                startActivity(intent);
                setResult(3,intent);
                finish();

            }
        });
        iv_sqMenu = (ImageView)findViewById(R.id.iv_sqMenu);
        iv_sqMenu.setVisibility(View.GONE);
        SegmentedGroup segmented2 = (SegmentedGroup) findViewById(R.id.sq_segmented1);
        segmented2.setVisibility(View.GONE);

        lv_catalog = (ListView)findViewById(R.id.sq_lvCatalog);
        tv_headerText.setText("商圈 - " + channelName);

        initData();
    }

    private void initData() {
        loadingUtils.show();
        KJHttp kjHttp = new KJHttp();
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.TIMEOUT = 3 * 60 * 1000;
        kjHttp.setConfig(httpConfig);
        kjHttp.get(GlobalContants.GETCHANNEL_URL + "?method=getcatalog&channelId=" + channelID, null, false, new HttpCallBack() {
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
                Toast.makeText(SqCatalogActivity.this, "获取数据失败" + strMsg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String t) {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<SqCatalogVM>>() {
                }.getType();

                sqCataLogList = gson.fromJson(t, type);

                lv_catalog.setAdapter(new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return sqCataLogList.size();
                    }

                    @Override
                    public SqCatalogVM getItem(int position) {
                        return sqCataLogList.get(position);
                    }

                    @Override
                    public long getItemId(int position) {
                        return position;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        ViewHolder viewHolder = null;
                        SqCatalogVM item = getItem(position);

                        if (convertView == null) {
                            viewHolder = new ViewHolder();
                            convertView = View.inflate(SqCatalogActivity.this, R.layout.sq_catalogcell, null);
                            viewHolder.tv_catalogName = (TextView) convertView.findViewById(R.id.tv_sqCatalogName);
                            viewHolder.tv_catalogDesc = (TextView) convertView.findViewById(R.id.tv_sqCatalogDesc);
                            convertView.setTag(viewHolder);
                        } else {
                            viewHolder = (ViewHolder) convertView.getTag();
                        }

                        viewHolder.tv_catalogName.setText(item.Name);
                        viewHolder.tv_catalogDesc.setText(item.Desc);
                        return convertView;
                    }
                });
                loadingUtils.close();
                lv_catalog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        SqCatalogVM item = sqCataLogList.get(position);
                        if (item.id > 0) {
                            Intent intent = new Intent(SqCatalogActivity.this, SqCatalogItemAdd.class);
                            intent.putExtra("CATALOGCHANNEL_ID", channelID);
                            intent.putExtra("CATALOGCHANNEL_NAME", channelName);
                            intent.putExtra("PRODUCT_NAME", item.Name);
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 4)
        {
            channelID = data.getIntExtra("CATALOGCHANNEL_ID",0);
            channelName = data.getStringExtra("CATALOGCAHNNEL_NAME");
            initData();
        }
    }

    public class ViewHolder
    {
        public TextView tv_catalogName;
        public TextView tv_catalogDesc;
    }
    }
