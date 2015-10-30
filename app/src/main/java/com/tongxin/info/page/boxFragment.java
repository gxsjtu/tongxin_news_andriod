package com.tongxin.info.page;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.costum.android.widget.LoadMoreListView;
import com.costum.android.widget.PullAndLoadListView;
import com.costum.android.widget.PullToRefreshListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tongxin.info.R;
import com.tongxin.info.activity.InboxDetailActivity;
import com.tongxin.info.activity.MainActivity;
import com.tongxin.info.domain.InboxMsgVM;
import com.tongxin.info.domain.SearchItem;
import com.tongxin.info.domain.SearchVM;
import com.tongxin.info.global.GlobalContants;
import com.tongxin.info.utils.loadingUtils;

import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;
import org.kymjs.kjframe.http.HttpParams;
import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/9/24.
 */
public class boxFragment extends Fragment {
    private Activity mActivity;
    private PullToRefreshListView lv_msg;
    private ListView lv_searchRes;
    private ArrayList<InboxMsgVM> msgList = new ArrayList<InboxMsgVM>();
    private ArrayList<InboxMsgVM> loadList = new ArrayList<InboxMsgVM>();
    private String minDateForPullUp;
    private String maxDateForPullDown;
    private BaseAdapter adapterForData;
    private EditText msg_searchTxt;
    private ImageView msg_searchImg;
    private Button loadMoreBtn;
    private View footerView;
    loadingUtils loadingUtils;
    private String msgForImgHere;//上拉标志已读的图标
    private String msgForPullUpHere;//下拉标志已读的图标
    private int hereIndex = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        loadingUtils = new loadingUtils(mActivity);
        initData();
        //loadMore();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        container.removeAllViews();
        View view = View.inflate(mActivity, R.layout.inboxmsg, null);
        footerView = View.inflate(mActivity, R.layout.inboxmsgfooter, null);
        lv_msg = (PullToRefreshListView) view.findViewById(R.id.lvMsg);
        lv_msg.addFooterView(footerView);
        lv_searchRes = (ListView)view.findViewById(R.id.lv_searchRes);
        lv_searchRes.setVisibility(View.GONE);
        msg_searchImg = (ImageView)view.findViewById(R.id.ivMsg_search);
        msg_searchTxt = (EditText)view.findViewById(R.id.msg_search);
        loadMoreBtn = (Button)footerView.findViewById(R.id.msg_loadMoreBtn);
        loadMoreBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                loadMore();
            }
        });
//        lv_msg.setOnLoadMoreListener(new PullAndLoadListView.OnLoadMoreListener() {
//
//            @Override
//            public void onLoadMore() {
////                new LoadMoreDataTask().execute();
//                loadMore();
//            }
//
//        });

        lv_msg.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {

            @Override
            public void onRefresh() {
                pullRefresh();
            }
        });
        msg_searchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
        return view;
    }

    private void search()
    {
        loadingUtils.show();
        lv_msg.setVisibility(View.GONE);
        String searchKey = msg_searchTxt.getText().toString();
        loadList.clear();
        String res = "";
        if(searchKey != null) {
            res = searchKey.replace(" ", "");
        }
        if("".equals(res))
        {
            loadList.addAll(msgList);
        }
        else
        {
            for(int i = 0; i < msgList.size();i++) {
                if (msgList.get(i).msg.contains(searchKey)) {
                    loadList.add(msgList.get(i));
                }
            }
        }

//        if(loadList == null || loadList.size() <= 0)
//        {
//            lv_msg.removeFooterView(footerView);
//        }
//        else
//        {
//            lv_msg.addFooterView(footerView);
//        }

        adapterForData = new BaseAdapter() {
            @Override
            public int getCount() {
                return loadList.size();
            }

            @Override
            public InboxMsgVM getItem(int position) {
                return loadList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder viewHolder = null;
                InboxMsgVM item = getItem(position);

                if (convertView == null) {
                    viewHolder = new ViewHolder();
                    convertView = View.inflate(mActivity, R.layout.boxcontent, null);
                    viewHolder.txt_msg = (TextView) convertView.findViewById(R.id.txtMsg);
                    viewHolder.txt_date = (TextView) convertView.findViewById(R.id.txtDate);
                    viewHolder.img_Msg = (ImageView) convertView.findViewById(R.id.imgMsg);
//                                    viewHolder.msg_img = (TextView) convertView.findViewById(R.id.)
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                viewHolder.txt_msg.setText(item.msg);
                viewHolder.txt_date.setText(item.date);
                if (item.url == null || item.url == "") {
                    viewHolder.img_Msg.setVisibility(View.GONE);
                } else {
                    viewHolder.img_Msg.setVisibility(View.VISIBLE);
                }

                return convertView;
            }
        };
        if("".equals(res)) {
            lv_searchRes.setVisibility(View.GONE);
            lv_msg.setVisibility(View.VISIBLE);
            lv_msg.setAdapter(adapterForData);
        }
        else {
            lv_searchRes.setVisibility(View.VISIBLE);
            lv_msg.setVisibility(View.GONE);
            lv_searchRes.setAdapter(adapterForData);
            lv_searchRes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    InboxMsgVM item = loadList.get(position);
                    if (item.url != null && item.url != "") {
                        Intent intent = new Intent(mActivity, InboxDetailActivity.class);
                        intent.putExtra("inboxDetailUrl", item.url);
                        startActivity(intent);
                    }
                }
            });
        }
        loadingUtils.close();
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initData()
    {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        KJHttp kjHttp = new KJHttp();
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.TIMEOUT = 3 * 60 * 1000;
        kjHttp.setConfig(httpConfig);
        loadingUtils.show();
        kjHttp.get(GlobalContants.GETINBOXMSG_URL + "?method=getInboxMsg&mobile=13764233669", null, false, new HttpCallBack() {
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
                        Toast.makeText(mActivity, "获取数据失败" + strMsg, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String t) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<ArrayList<InboxMsgVM>>() {
                        }.getType();

                        loadList = gson.fromJson(t, type);
                        for (int i = 0; i < loadList.size(); i++) {
                            InboxMsgVM inbox = new InboxMsgVM();
                            inbox.msg = loadList.get(i).msg;
                            inbox.url = loadList.get(i).url;
                            try {
                                inbox.date = format.format(format.parse(loadList.get(i).date)).toString();
                            } catch (Exception ex) {

                            }
                            msgList.add(inbox);
                        }
                        if (loadList != null && loadList.size() > 0) {
                            minDateForPullUp = loadList.get(loadList.size() - 1).date;
                            maxDateForPullDown = loadList.get(0).date;
                            msgForImgHere = msgList.get(0).msg + msgList.get(0).date;
                            msgForPullUpHere = "";//第一次加载直接上拉不显示已读图标
                        }
                        lv_msg.setAdapter(new BaseAdapter() {
                            @Override
                            public int getCount() {
                                return msgList.size();
                            }

                            @Override
                            public InboxMsgVM getItem(int position) {
                                return msgList.get(position);
                            }

                            @Override
                            public long getItemId(int position) {
                                return position;
                            }

                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                ViewHolder viewHolder = null;
                                InboxMsgVM item = getItem(position);

                                if (convertView == null) {
                                    viewHolder = new ViewHolder();
                                    convertView = View.inflate(mActivity, R.layout.boxcontent, null);
                                    viewHolder.txt_msg = (TextView) convertView.findViewById(R.id.txtMsg);
                                    viewHolder.txt_date = (TextView) convertView.findViewById(R.id.txtDate);
                                    viewHolder.img_Msg = (ImageView) convertView.findViewById(R.id.imgMsg);
                                    viewHolder.img_Here = (ImageView) convertView.findViewById(R.id.img_here);
                                    convertView.setTag(viewHolder);
                                } else {
                                    viewHolder = (ViewHolder) convertView.getTag();
                                }

                                viewHolder.txt_msg.setText(item.msg);
                                viewHolder.txt_date.setText(item.date);
                                if (item.url == null || item.url == "") {
                                    viewHolder.img_Msg.setVisibility(View.GONE);
                                } else {
                                    viewHolder.img_Msg.setVisibility(View.VISIBLE);
                                }

                                return convertView;
                            }
                        });
                        loadingUtils.close();
                        lv_msg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                InboxMsgVM item = msgList.get(position - 1);
                                if (item.url != null && item.url != "") {
                                    Intent intent = new Intent(mActivity, InboxDetailActivity.class);
                                    intent.putExtra("inboxDetailUrl", item.url + "&mobile=131764233669");
                                    startActivity(intent);
                                }
                            }
                        });
                    }
                }
        );
    }

    private void pullRefresh()
    {
        if (msgList != null && msgList.size() > 0)
        {
            msgList.get(hereIndex).isHereVisible = false;
            msgList.get(0).isHereVisible = true;
        }
      final  SimpleDateFormat sdfFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        HttpParams params = new HttpParams();
        params.put("method","getMsgByAction");
        params.put("mobile","13764233669");
        params.put("actionStr","pullDown");
        params.put("dateStr",maxDateForPullDown);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd H:m:s sss");
        //format.parse()
        KJHttp kjHttp = new KJHttp();
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.TIMEOUT = 3 * 60 * 1000;
        kjHttp.setConfig(httpConfig);
        loadingUtils.show();
        kjHttp.post(GlobalContants.GETINBOXMSG_URL, params, false, new HttpCallBack() {
            long dStart;
            long dFinish;
            @Override
            public void onPreStart() {
              //  Object dStart;
                super.onPreStart();
                try {
                    dStart = sdfFormat.parse(sdfFormat.format(new Date())).getTime();
                }catch (Exception ex)
                {
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                try {
                    dFinish = sdfFormat.parse(sdfFormat.format(new Date())).getTime();
                    if((dFinish - dStart) / 1000 < 1)
                    {
                        Thread.sleep(1000);
                    }
                }catch (Exception ex)
                {
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                loadingUtils.close();
                lv_msg.onRefreshComplete();
                Toast.makeText(mActivity, "下拉加载数据失败" + strMsg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String t) {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<InboxMsgVM>>() {
                }.getType();

                loadList.clear();
                loadList = gson.fromJson(t, type);
                hereIndex = loadList.size();
                for (int i = 0; i < loadList.size();i++)
                {
                    InboxMsgVM inbox = new InboxMsgVM();
                    inbox.msg = loadList.get(i).msg;
                    inbox.url = loadList.get(i).url;
                    try {
                        inbox.date = sdfFormat.format(sdfFormat.parse(loadList.get(i).date)).toString();
                    }catch (Exception ex)
                    {

                    }
                    msgList.add(i, inbox);
                }
                adapterForData = new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return msgList.size();
                    }

                    @Override
                    public InboxMsgVM getItem(int position) {
                        return msgList.get(position);
                    }

                    @Override
                    public long getItemId(int position) {
                        return position;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        ViewHolder viewHolder = null;
                        InboxMsgVM item = getItem(position);

                        if (convertView == null) {
                            viewHolder = new ViewHolder();
                            convertView = View.inflate(mActivity, R.layout.boxcontent, null);
                            viewHolder.txt_msg = (TextView) convertView.findViewById(R.id.txtMsg);
                            viewHolder.txt_date = (TextView) convertView.findViewById(R.id.txtDate);
                            viewHolder.img_Msg = (ImageView) convertView.findViewById(R.id.imgMsg);
                            viewHolder.img_Here = (ImageView) convertView.findViewById(R.id.img_here);
                            convertView.setTag(viewHolder);
                        } else {
                            viewHolder = (ViewHolder) convertView.getTag();
                        }

                        viewHolder.txt_msg.setText(item.msg);
                        viewHolder.txt_date.setText(item.date);
                        if (item.url == null || item.url == "") {
                            viewHolder.img_Msg.setVisibility(View.GONE);
                        } else {
                            viewHolder.img_Msg.setVisibility(View.VISIBLE);
                        }

//                        String str = item.msg + item.date;
                        if(item.isHereVisible)
                        {
                            viewHolder.img_Here.setVisibility(View.VISIBLE);
                           // item.isHereVisible = false;
                        }
                        else
                        {
                            viewHolder.img_Here.setVisibility(View.GONE);
                            item.isHereVisible = false;
                        }


                        return convertView;
                    }
                };
                lv_msg.setAdapter(adapterForData);
                loadingUtils.close();
                if (loadList != null && loadList.size() > 0) {
                    maxDateForPullDown = loadList.get(0).date;
                }

                adapterForData.notifyDataSetChanged();
                lv_msg.setSelection(0);
                lv_msg.onRefreshComplete();

//                if(msgList != null && msgList.size() > 0)
//                {
//                    msgForImgHere = msgList.get(0).msg + msgList.get(0).date;
//                    msgForPullUpHere = msgList.get(loadList.size()).msg + msgList.get(loadList.size()).date;//上拉加载更多记录上一次已读的位置 避免上拉隐藏图标
//                }

                lv_msg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        InboxMsgVM item = msgList.get(position - 1);
                        if (item.url != null && item.url != "") {
                            Intent intent = new Intent(mActivity, InboxDetailActivity.class);
                            intent.putExtra("inboxDetailUrl", item.url + "&mobile=131764233669");
                            startActivity(intent);
                        }
                        }
                    });
            }
        });
    }

    private void loadMore() {
        final   SimpleDateFormat sdfFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        HttpParams params = new HttpParams();
        params.put("method","getMsgByAction");
        params.put("mobile","13764233669");
        params.put("actionStr","pullUp");
        params.put("dateStr",minDateForPullUp);
        KJHttp kjHttp = new KJHttp();
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.TIMEOUT = 3 * 60 * 1000;
        kjHttp.setConfig(httpConfig);
        loadingUtils.show();
        kjHttp.post(GlobalContants.GETINBOXMSG_URL, params, false, new HttpCallBack() {
            long dStart;
            long dFinish;
            @Override
            public void onPreStart() {
                super.onPreStart();
                try {
                    dStart = sdfFormat.parse(sdfFormat.format(new Date())).getTime();
                }catch (Exception ex)
                {
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                try {
                    dFinish = sdfFormat.parse(sdfFormat.format(new Date())).getTime();
                    if((dFinish - dStart) / 1000 < 1)
                    {
                        Thread.sleep(1000);
                    }
                }catch (Exception ex)
                {
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                loadingUtils.close();
                Toast.makeText(mActivity, "上拉加载数据失败" + strMsg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String t) {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<InboxMsgVM>>() {
                }.getType();

                loadList.clear();
                loadList = gson.fromJson(t, type);

                for (int i = 0; i < loadList.size();i++)
                {
                    InboxMsgVM inbox = new InboxMsgVM();
                    inbox.msg = loadList.get(i).msg;
                    inbox.url = loadList.get(i).url ;
                    try {
                        inbox.date = sdfFormat.format(sdfFormat.parse(loadList.get(i).date)).toString();
                    }catch (Exception ex)
                    {

                    }
                    msgList.add(msgList.size(),inbox);
                }
                adapterForData = new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return msgList.size();
                    }

                    @Override
                    public InboxMsgVM getItem(int position) {
                        return msgList.get(position);
                    }

                    @Override
                    public long getItemId(int position) {
                        return position;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        ViewHolder viewHolder = null;
                        InboxMsgVM item = getItem(position);

                        if (convertView == null) {
                            viewHolder = new ViewHolder();
                            convertView = View.inflate(mActivity, R.layout.boxcontent, null);
                            viewHolder.txt_msg = (TextView) convertView.findViewById(R.id.txtMsg);
                            viewHolder.txt_date = (TextView) convertView.findViewById(R.id.txtDate);
                            viewHolder.img_Msg = (ImageView) convertView.findViewById(R.id.imgMsg);
                            viewHolder.img_Here = (ImageView) convertView.findViewById(R.id.img_here);
                            convertView.setTag(viewHolder);
                        } else {
                            viewHolder = (ViewHolder) convertView.getTag();
                        }

                        viewHolder.txt_msg.setText(item.msg);
                        viewHolder.txt_date.setText(item.date);
                        if (item.url == null || item.url == "") {
                            viewHolder.img_Msg.setVisibility(View.GONE);
                        } else {
                            viewHolder.img_Msg.setVisibility(View.VISIBLE);
                        }

                        if(item.isHereVisible)
                        {
                            viewHolder.img_Here.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            viewHolder.img_Here.setVisibility(View.GONE);
                        }


                        return convertView;
                    }
                };
                lv_msg.setAdapter(adapterForData);
                loadingUtils.close();
                if(loadList != null && loadList.size() > 0) {
                    minDateForPullUp = loadList.get(loadList.size() - 1).date;
                }
                adapterForData.notifyDataSetChanged();
                lv_msg.setSelection(msgList.size() - 1);

                //lv_msg.onLoadMoreComplete();
                lv_msg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        InboxMsgVM item = msgList.get(position - 1);
                        if (item.url != null && item.url != "") {
                            Intent intent = new Intent(mActivity, InboxDetailActivity.class);
                            intent.putExtra("inboxDetailUrl", item.url + "&mobile=131764233669");
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }

    public class ViewHolder
    {
        public TextView txt_msg;
        public TextView txt_date;
        public ImageView img_Msg;
        public ImageView img_Here;
    }

}
