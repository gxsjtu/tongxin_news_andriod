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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tongxin.info.R;
import com.tongxin.info.domain.SqListVM;
import com.tongxin.info.global.GlobalContants;
import com.tongxin.info.utils.UserUtils;
import com.tongxin.info.utils.loadingUtils;

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
public class sqListFragment extends Activity {
    private String tv_Name;
    private int tv_ID;
    loadingUtils loadingUtils;
    private String tel;
    private ArrayList<SqListVM> sqList = new ArrayList<SqListVM>();
    private ArrayList<SqListVM> resList = new ArrayList<SqListVM>();
    private ListView lv_sqList;
    private Button btn_GY;
    private Button btn_XQ;
    private ImageView iv_sqReturn;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sq_list);
        Intent intent = getIntent();
        tv_Name = intent.getStringExtra("CHANNEL_NAME");
        tv_ID = intent.getIntExtra("CHANNEL_ID", 0);
        UserUtils userUtils = new UserUtils(this);
        tel = userUtils.getTel();
        lv_sqList = (ListView) findViewById(R.id.sq_lvData);
        final GradientDrawable drawable = new GradientDrawable();
        drawable.setStroke(1, Color.WHITE); // 边框粗细及颜色
        drawable.setColor(0x23B1EF); // 边框内部颜色
        iv_sqReturn = (ImageView)findViewById(R.id.sq_ivReturn);
        iv_sqReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final GradientDrawable drawableClick = new GradientDrawable();
        drawableClick.setColor(Color.WHITE); // 边框内部颜色

        btn_GY = (Button)findViewById(R.id.bt_SqTabGY);
        btn_GY.setBackgroundDrawable(drawableClick);
        btn_GY.setTextColor(Color.parseColor("#23B1EF"));
        btn_XQ = (Button)findViewById(R.id.bt_SqlTabXQ);
        btn_XQ.setBackgroundDrawable(drawable);
        btn_GY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqList.clear();
                resList.clear();
                btn_GY.setBackgroundDrawable(drawableClick);
                btn_GY.setTextColor(Color.parseColor("#23B1EF"));
                btn_XQ.setBackgroundDrawable(drawable);
                btn_XQ.setTextColor(Color.WHITE);
                initData("true");
            }
        });
        btn_XQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqList.clear();
                resList.clear();
                btn_XQ.setBackgroundDrawable(drawableClick);
                btn_XQ.setTextColor(Color.parseColor("#23B1EF"));
                btn_GY.setBackgroundDrawable(drawable);
                btn_GY.setTextColor(Color.WHITE);
                initData("false");
            }
        });
//        loadingUtils = new loadingUtils(this);
        initData("true");
    }

    private void initData(final String dataType) {
        KJHttp kjHttp = new KJHttp();
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.TIMEOUT = 3 * 60 * 1000;
        kjHttp.setConfig(httpConfig);
//        loadingUtils.show();
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
//                loadingUtils.close();
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
                        }
                        else
                        {
                            viewHolder = (ViewHolder) convertView.getTag();
                        }

                       // Bitmap bitmap = getHttpBitmap(item.avatar);

                        //显示
//                        viewHolder.imgSqList.setImageBitmap(bitmap);
                        ImageLoader.getInstance().displayImage(item.avatar,viewHolder.imgSqList);
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
