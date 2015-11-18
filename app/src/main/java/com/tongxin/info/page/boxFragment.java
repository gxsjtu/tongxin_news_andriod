package com.tongxin.info.page;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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
import android.widget.TextView;
import android.widget.Toast;

import com.costum.android.widget.LoadMoreListView;
import com.costum.android.widget.PullAndLoadListView;
import com.costum.android.widget.PullToRefreshListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tongxin.info.R;
import com.tongxin.info.activity.InboxDetailActivity;
import com.tongxin.info.activity.MainActivity;
import com.tongxin.info.domain.InboxMsgVM;
import com.tongxin.info.domain.SearchItem;
import com.tongxin.info.domain.SearchVM;
import com.tongxin.info.domain.SqListVM;
import com.tongxin.info.global.GlobalContants;
import com.tongxin.info.utils.UserUtils;
import com.tongxin.info.utils.loadingUtils;

import org.json.JSONException;
import org.json.JSONObject;
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
//    private ImageView msg_searchImg;
    private Button loadMoreBtn;
    private View footerView;
    loadingUtils loadingUtils;
    private int hereIndex = 0;
    private String tel;

    private TextView tv_headerTitle;
    private LinearLayout iv_ref;
    private Button btn_clear;
    private ArrayList<InboxMsgVM> resList = new ArrayList<InboxMsgVM>();//数据不够一页放入空白数据
    private LinearLayout ll_ForHeight;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        UserUtils userUtils = new UserUtils(mActivity);
        tel = userUtils.getTel();
        loadingUtils = new loadingUtils(mActivity);
        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        container.removeAllViews();
      final  View view = View.inflate(mActivity, R.layout.inboxmsg, null);
        ll_ForHeight = (LinearLayout)view.findViewById(R.id.ll_ForHeight);
        tv_headerTitle = (TextView) view.findViewById(R.id.tv_headerTitle);
        tv_headerTitle.setText("收件箱");
        iv_ref = (LinearLayout) view.findViewById(R.id.iv_ref);
        iv_ref.setVisibility(View.VISIBLE);
        iv_ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loadList != null && msgList != null) {
                    loadList.clear();
                    msgList.clear();
                }
                initData();
            }
        });
        btn_clear = (Button)view.findViewById(R.id.btn_clearMsg);
        btn_clear.setVisibility(View.VISIBLE);
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msgList.clear();
                loadList.clear();
                adapterForData = new AppAdapter();
//                adapterForData.notifyDataSetChanged();
                lv_msg.setAdapter(adapterForData);
            }
        });
        footerView = View.inflate(mActivity, R.layout.inboxmsgfooter, null);
        lv_msg = (PullToRefreshListView) view.findViewById(R.id.lvMsg);
        lv_msg.addFooterView(footerView);
        lv_msg.setMinimumHeight(ll_ForHeight.getHeight());
        lv_searchRes = (ListView)view.findViewById(R.id.lv_searchRes);
        lv_searchRes.setVisibility(View.GONE);

        lv_msg.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                InboxMsgVM msg = msgList.get(position - 1);
                Copy(msg.msg.trim());
                Toast.makeText(mActivity, "内容已复制", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        lv_searchRes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                InboxMsgVM msg = msgList.get(position - 1);
                Copy(msg.msg.trim());
                Toast.makeText(mActivity, "内容已复制", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        msg_searchTxt = (EditText)view.findViewById(R.id.msg_search);
        msg_searchTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)  {

                if (((actionId== EditorInfo.IME_ACTION_SEND ||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER)) && event.getAction()==KeyEvent.ACTION_DOWN) || actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                    InputMethodManager imm = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(msg_searchTxt.getWindowToken(),0);
                    return true;
                }
                else {
                    return false;
                }
            }
        });
        loadMoreBtn = (Button)footerView.findViewById(R.id.msg_loadMoreBtn);
        loadMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMore();
            }
        });

        lv_msg.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {

            @Override
            public void onRefresh() {
                pullRefresh();
                Intent intentCount = new Intent("com.tongxin.badge");
                intentCount.putExtra("count", 0);
                mActivity.sendBroadcast(intentCount);
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
//            res.toLowerCase();
            for(int i = 0; i < msgList.size();i++) {
                if (msgList.get(i).msg.toLowerCase().contains(searchKey.toLowerCase())) {
                    loadList.add(msgList.get(i));
                }
            }
        }


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
                        intent.putExtra("title", "同鑫评论");
                        startActivity(intent);
                    }
                }
            });
        }
        loadingUtils.close();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initData()
    {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final SimpleDateFormat formatForDataNull = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss sss");

        final KJHttp kjHttp = new KJHttp();
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.TIMEOUT = 3 * 60 * 1000;
        kjHttp.setConfig(httpConfig);
        loadingUtils.show();
        kjHttp.get(GlobalContants.GETINBOXMSG_URL + "?method=getInboxMsg&mobile=" + tel, null, false, new HttpCallBack() {
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
                        Toast.makeText(mActivity, "获取数据失败，请稍后再试！", Toast.LENGTH_SHORT).show();
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
                        } else {
                            maxDateForPullDown = formatForDataNull.format(new Date());
                            minDateForPullUp = formatForDataNull.format(new Date());
                        }
//                        SetLVHeight();
                        adapterForData = new AppAdapter();
                        lv_msg.setAdapter(adapterForData);
                        loadingUtils.close();
                        lv_msg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                InboxMsgVM item = msgList.get(position - 1);
                                if (item.url != null && item.url != "") {
                                    Intent intent = new Intent(mActivity, InboxDetailActivity.class);
                                    intent.putExtra("inboxDetailUrl", item.url + "&mobile=" + tel);
                                    intent.putExtra("title", "同鑫评论");
                                    startActivity(intent);
                                }
                            }
                        });
                        kjHttp.get(GlobalContants.MessageInfo_URL + "?method=clearMessage&mobile=" + tel, null, false, new HttpCallBack() {
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
                                Toast.makeText(mActivity, "消息清零失败！", Toast.LENGTH_SHORT);
                            }

                            @Override
                            public void onSuccess(String t) {
                                try {
                                    JSONObject json = new JSONObject(t);
                                    String result = json.getString("result");
                                    if (result.equals("ok")) {
//                                        Toast.makeText(mActivity, "消息清零成功！", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(mActivity, "消息清零失败！", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
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
        params.put("mobile",tel);
        params.put("actionStr","pullDown");
        params.put("dateStr",maxDateForPullDown);
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss sss");
        //format.parse()
        final KJHttp kjHttp = new KJHttp();
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
                } catch (Exception ex) {
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                try {
                    dFinish = sdfFormat.parse(sdfFormat.format(new Date())).getTime();
                    if ((dFinish - dStart) / 1000 < 1) {
                        Thread.sleep(1000);
                    }
                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                loadingUtils.close();
                lv_msg.onRefreshComplete();
                Toast.makeText(mActivity, "下拉加载数据失败，请稍后再试！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String t) {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<InboxMsgVM>>() {
                }.getType();

                loadList.clear();
                loadList = gson.fromJson(t, type);
                hereIndex = loadList.size();
                for (int i = 0; i < loadList.size(); i++) {
                    InboxMsgVM inbox = new InboxMsgVM();
                    inbox.msg = loadList.get(i).msg;
                    inbox.url = loadList.get(i).url;
                    try {
                        inbox.date = sdfFormat.format(sdfFormat.parse(loadList.get(i).date)).toString();
                    } catch (Exception ex) {

                    }
                    msgList.add(i, inbox);
                }
                adapterForData = new AppAdapter();
                lv_msg.setAdapter(adapterForData);
                loadingUtils.close();
                if (loadList != null && loadList.size() > 0) {
                    maxDateForPullDown = loadList.get(0).date;
                }

                if (msgList == null || msgList.size() <= 0) {
                    maxDateForPullDown = format.format(new Date());
                }

                adapterForData.notifyDataSetChanged();
                lv_msg.setSelection(0);
                lv_msg.onRefreshComplete();

                lv_msg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        InboxMsgVM item = msgList.get(position - 1);
                        if (item.url != null && item.url != "") {
                            Intent intent = new Intent(mActivity, InboxDetailActivity.class);
                            intent.putExtra("inboxDetailUrl", item.url + "&mobile=" + tel);
                            intent.putExtra("title", "同鑫评论");
                            startActivity(intent);
                        }
                    }
                });

                kjHttp.get(GlobalContants.MessageInfo_URL + "?method=clearMessage&mobile=" + tel, null, false, new HttpCallBack() {
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
                        Toast.makeText(mActivity,"消息清零失败！",Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onSuccess(String t) {
                        try {
                            JSONObject json = new JSONObject(t);
                            String result = json.getString("result");
                            if (result.equals("ok")) {
//                                Toast.makeText(mActivity, "消息清零成功！", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mActivity, "消息清零失败！", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }

    private void loadMore() {
        final   SimpleDateFormat sdfFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss sss");
        HttpParams params = new HttpParams();
        params.put("method","getMsgByAction");
        params.put("mobile",tel);
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
                } catch (Exception ex) {
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                try {
                    dFinish = sdfFormat.parse(sdfFormat.format(new Date())).getTime();
                    if ((dFinish - dStart) / 1000 < 1) {
                        Thread.sleep(1000);
                    }
                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                loadingUtils.close();
                Toast.makeText(mActivity, "加载数据失败，请稍后再试！", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String t) {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<InboxMsgVM>>() {
                }.getType();

                loadList.clear();
                loadList = gson.fromJson(t, type);

                for (int i = 0; i < loadList.size(); i++) {
                    InboxMsgVM inbox = new InboxMsgVM();
                    inbox.msg = loadList.get(i).msg;
                    inbox.url = loadList.get(i).url;
                    try {
                        inbox.date = sdfFormat.format(sdfFormat.parse(loadList.get(i).date)).toString();
                    } catch (Exception ex) {

                    }
                    msgList.add(msgList.size(), inbox);
                }
                adapterForData = new AppAdapter();
                lv_msg.setAdapter(adapterForData);
                loadingUtils.close();
                if (loadList != null && loadList.size() > 0) {
                    minDateForPullUp = loadList.get(loadList.size() - 1).date;
                }
//                else {
//                    minDateForPullUp = format.format(new Date());
//                }
                adapterForData.notifyDataSetChanged();
                lv_msg.setSelection(msgList.size() - loadList.size());

                lv_msg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        InboxMsgVM item = msgList.get(position - 1);
                        if (item.url != null && item.url != "") {
                            Intent intent = new Intent(mActivity, InboxDetailActivity.class);
                            intent.putExtra("inboxDetailUrl", item.url + "&mobile=" + tel);
                            intent.putExtra("title","同鑫评论");
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }

    public void SetLVHeight()
    {
//        if(msgList == null || msgList.size() < 20)
//        {
//            resList.clear();
//            resList.addAll(msgList);
//            for(int i =0; i <(20 - msgList.size()); i ++)
//            {
//                InboxMsgVM vm = new InboxMsgVM();
//                vm.msg = "";
//                vm.date = "";
//                vm.url = "";
//                resList.add(vm);
//            }
//        }
    }

    public class AppAdapter extends BaseAdapter {
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

            if (item.isHereVisible) {
                viewHolder.img_Here.setVisibility(View.VISIBLE);
            } else {
                viewHolder.img_Here.setVisibility(View.GONE);
                item.isHereVisible = false;
            }


            return convertView;
        }
    }

    public class ViewHolder
    {
        public TextView txt_msg;
        public TextView txt_date;
        public ImageView img_Msg;
        public ImageView img_Here;
    }

    private void Copy(String text)
    {
        ClipboardManager cbm = (ClipboardManager)mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData myClip = ClipData.newPlainText("text", text);
        cbm.setPrimaryClip(myClip);
    }


}
