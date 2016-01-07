package com.tongxin.info.page;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tongxin.info.R;
import com.tongxin.info.activity.InboxDetailActivity;
import com.tongxin.info.domain.InboxMsgVM;
import com.tongxin.info.global.GlobalContants;
import com.tongxin.info.utils.ToastUtils;
import com.tongxin.info.utils.UserUtils;
import com.tongxin.info.utils.loadingUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;
import org.kymjs.kjframe.http.HttpParams;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

/**
 * Created by Administrator on 2015/9/24.
 */
public class boxFragment extends Fragment implements Serializable {
    private LinkedList<String> mListItems;
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
    private int hereIndex = 0;
    private String tel;
    private ListView actualListView;

    private TextView tv_headerTitle;
    private LinearLayout iv_ref;
    private Button btn_clear;
    private String pullMode;
    private Button btn_CancelSearch;
    ProgressDialog dialog;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("tel",tel);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        if(savedInstanceState!=null)
        {
            UserUtils userUtils = new UserUtils(mActivity);
            tel = savedInstanceState.getString("tel");
            UserUtils.Tel = tel;

        }
        else {
            UserUtils userUtils = new UserUtils(mActivity);
            tel = UserUtils.Tel;
        }

        dialog = new ProgressDialog(mActivity);
        //initData();
        // new GetDataTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        container.removeAllViews();

        final View view = View.inflate(mActivity, R.layout.inboxmsg, null);
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
                msg_searchTxt.setText("");
                adapterForData = new AppAdapter();
                lv_msg.setAdapter(adapterForData);
                initData();
                Intent intentCount = new Intent("com.tongxin.badge");
                intentCount.putExtra("count", 0);
                mActivity.sendBroadcast(intentCount);
            }
        });

        btn_clear = (Button) view.findViewById(R.id.btn_clearMsg);
        btn_clear.setVisibility(View.VISIBLE);
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msg_searchTxt.setText("");
                msgList.clear();
                loadList.clear();
                adapterForData = new AppAdapter();
//                adapterForData.notifyDataSetChanged();
                lv_msg.setAdapter(adapterForData);
            }
        });
//        lv_msg.getRefreshableView().setOnItemLongClickListener();
        footerView = View.inflate(mActivity, R.layout.inboxmsgfooter, null);
        lv_msg = (PullToRefreshListView) view.findViewById(R.id.lvMsg);
        lv_msg.setMode(PullToRefreshBase.Mode.BOTH);
//        lv_msg.seta
//        lv_msg.setLayoutAnimation(PullToRefreshBase.AnimationStyle.FLIP);
        actualListView = lv_msg.getRefreshableView();
        registerForContextMenu(actualListView);


        lv_searchRes = (ListView) view.findViewById(R.id.lv_searchRes);
        lv_searchRes.setVisibility(View.GONE);

        lv_msg.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = "最后更新：" + DateUtils.formatDateTime(mActivity, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

                if (refreshView.getCurrentMode() == PullToRefreshBase.Mode.PULL_FROM_START) {
                    pullMode = "pullDown";
                    new GetDataTask().execute();
                } else if (refreshView.getCurrentMode() == PullToRefreshBase.Mode.PULL_FROM_END) {
                    pullMode = "pullUp";
                    new GetDataTask().execute();
                }
            }

        });

        actualListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                InboxMsgVM msg = msgList.get(position - 1);
                Copy(msg.msg.trim());
                ToastUtils.ShowForCopy(mActivity, "内容已复制");
                return true;
            }
        });

        lv_searchRes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                InboxMsgVM msg = msgList.get(position - 1);
                Copy(msg.msg.trim());
                ToastUtils.ShowForCopy(mActivity, "内容已复制");
                return true;
            }
        });

        msg_searchTxt = (EditText) view.findViewById(R.id.msg_search);
        msg_searchTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (((actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) && event.getAction() == KeyEvent.ACTION_DOWN) || actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                    InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(msg_searchTxt.getWindowToken(), 0);
                    return true;
                } else {
                    return false;
                }
            }
        });
        msg_searchTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    btn_CancelSearch.setTextColor(Color.parseColor("#23B1EF"));
                    btn_CancelSearch.setEnabled(true);
                } else {
                    btn_CancelSearch.setTextColor(Color.GRAY);
                    btn_CancelSearch.setEnabled(false);
                }
            }
        });
        btn_CancelSearch = (Button) view.findViewById(R.id.btn_CancelSearch);
        btn_CancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msg_searchTxt.setText("");
                msg_searchTxt.clearFocus();
                btn_CancelSearch.setTextColor(Color.GRAY);
                btn_CancelSearch.setEnabled(false);
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(msg_searchTxt.getWindowToken(), 0);
//                if (loadList != null && msgList != null) {
//                    loadList.clear();
//                    msgList.clear();
//                }
                adapterForData = new AppAdapter();
                lv_msg.setAdapter(adapterForData);
//                initData();
            }
        });
        loadMoreBtn = (Button) footerView.findViewById(R.id.msg_loadMoreBtn);
        loadMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMore();
            }
        });

//        lv_msg.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
//
//            @Override
//            public void onRefresh() {
//                pullRefresh();
//                Intent intentCount = new Intent("com.tongxin.badge");
//                intentCount.putExtra("count", 0);
//                mActivity.sendBroadcast(intentCount);
//            }
//        });
        initData();
        return view;
    }

    private void showLoading()
    {
        if(!dialog.isShowing()) {
            dialog.setCancelable(false);
            dialog.show();
            dialog.setContentView(R.layout.loading_layout);
        }
    }

    private void hideLoading()
    {
        if(dialog!=null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void search() {
        showLoading();
//        lv_msg.setVisibility(View.GONE);
        String searchKey = msg_searchTxt.getText().toString();
        loadList.clear();
        String res = "";
        if (searchKey != null) {
//            res = searchKey.replace(" ", "");
            res = searchKey.trim();
        }
        if ("".equals(res)) {
            loadList.addAll(msgList);
        } else {
//            res.toLowerCase();
            for (int i = 0; i < msgList.size(); i++) {
                if (msgList.get(i).msg.toLowerCase().contains(res.toLowerCase())) {
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
        actualListView.setAdapter(adapterForData);
        actualListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InboxMsgVM item = loadList.get(position - 1);
                if (item.url != null && item.url != "") {
                    Intent intent = new Intent(mActivity, InboxDetailActivity.class);
                    intent.putExtra("inboxDetailUrl", item.url+ "&mobile=" + UserUtils.Tel);
                    intent.putExtra("title", "同鑫评论");
                    intent.putExtra("descript", item.msg);
                    intent.putExtra("sharedicon", "");
                    startActivity(intent);
                }
            }
        });
//        if("".equals(res)) {
//            lv_searchRes.setVisibility(View.GONE);
//            lv_msg.setVisibility(View.VISIBLE);
//            lv_msg.setAdapter(adapterForData);
//        }
//        else {
//            lv_searchRes.setVisibility(View.VISIBLE);
//            lv_msg.setVisibility(View.GONE);
//            lv_searchRes.setAdapter(adapterForData);
//            lv_searchRes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    InboxMsgVM item = loadList.get(position);
//                    if (item.url != null && item.url != "") {
//                        Intent intent = new Intent(mActivity, InboxDetailActivity.class);
//                        intent.putExtra("inboxDetailUrl", item.url);
//                        intent.putExtra("title", "同鑫评论");
//                        startActivity(intent);
//                    }
//                }
//            });
//        }
        hideLoading();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initData() {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final SimpleDateFormat formatForDataNull = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss sss");

        final KJHttp kjHttp = new KJHttp();
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.TIMEOUT = 3 * 60 * 1000;
        kjHttp.setConfig(httpConfig);
        showLoading();
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
                        hideLoading();
                        if(maxDateForPullDown == null) {
                            maxDateForPullDown = formatForDataNull.format(new Date());
                        }

                        if(minDateForPullUp == null) {
                            minDateForPullUp = formatForDataNull.format(new Date());
                        }
                        ToastUtils.Show(mActivity, "获取数据失败，请稍后再试！");
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
                        adapterForData = new AppAdapter();

                        actualListView.setAdapter(adapterForData);
                        hideLoading();
                        actualListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                InboxMsgVM item = msgList.get(position - 1);
                                if (item.url != null && item.url != "") {
                                    Intent intent = new Intent(mActivity, InboxDetailActivity.class);
                                    intent.putExtra("inboxDetailUrl", item.url + "&mobile=" + tel);
                                    intent.putExtra("title", "同鑫评论");
                                    intent.putExtra("descript", item.msg);
                                    intent.putExtra("sharedicon", "");
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
                                ToastUtils.Show(mActivity, "消息清零失败！");
                            }

                            @Override
                            public void onSuccess(String t) {
                                try {
                                    JSONObject json = new JSONObject(t);
                                    String result = json.getString("result");
                                    if (result.equals("ok")) {
//                                        Toast.makeText(mActivity, "消息清零成功！", Toast.LENGTH_SHORT).show();
                                    } else {
                                        ToastUtils.Show(mActivity, "消息清零失败！");
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

    private void pullRefresh() {
        if (msgList != null && msgList.size() > 0) {
            msgList.get(hereIndex).isHereVisible = false;
            msgList.get(0).isHereVisible = true;
        }
        final SimpleDateFormat sdfFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final SimpleDateFormat formatForDataNull = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss sss");
        HttpParams params = new HttpParams();
        params.put("method", "getMsgByAction");
        params.put("mobile", tel);
        params.put("actionStr", "pullDown");
        params.put("dateStr", maxDateForPullDown);
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss sss");
        //format.parse()
        final KJHttp kjHttp = new KJHttp();
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.TIMEOUT = 3 * 60 * 1000;
        kjHttp.setConfig(httpConfig);

        kjHttp.post(GlobalContants.GETINBOXMSG_URL, params, false, new HttpCallBack() {

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
                lv_msg.onRefreshComplete();
                if(maxDateForPullDown == null) {
                    maxDateForPullDown = formatForDataNull.format(new Date());
                }

                if(minDateForPullUp == null) {
                    minDateForPullUp = formatForDataNull.format(new Date());
                }
                ToastUtils.Show(mActivity, "下拉加载数据失败，请稍后再试！");
            }

            @Override
            public void onSuccess(String t) {
//                Toast.makeText(mActivity, "下拉成功！", Toast.LENGTH_LONG).show();
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
                actualListView.setAdapter(adapterForData);
//                loadingUtils.close();
                if (loadList != null && loadList.size() > 0) {
                    maxDateForPullDown = loadList.get(0).date;
                }

                if (msgList == null || msgList.size() <= 0) {
                    maxDateForPullDown = format.format(new Date());
                }


                actualListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        InboxMsgVM item = msgList.get(position - 1);
                        if (item.url != null && item.url != "") {
                            Intent intent = new Intent(mActivity, InboxDetailActivity.class);
                            intent.putExtra("inboxDetailUrl", item.url + "&mobile=" + tel);
                            intent.putExtra("title", "同鑫评论");
                            intent.putExtra("descript", item.msg);
                            intent.putExtra("sharedicon", "");
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
                        ToastUtils.Show(mActivity, "消息清零失败！");
                    }

                    @Override
                    public void onSuccess(String t) {
                        try {
                            JSONObject json = new JSONObject(t);
                            String result = json.getString("result");
                            if (result.equals("ok")) {
//                                Toast.makeText(mActivity, "消息清零成功！", Toast.LENGTH_SHORT).show();
                            } else {
                                ToastUtils.Show(mActivity, "消息清零失败！");
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
        final SimpleDateFormat sdfFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final SimpleDateFormat formatForDataNull = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss sss");
        HttpParams params = new HttpParams();
        params.put("method", "getMsgByAction");
        params.put("mobile", tel);
        params.put("actionStr", "pullUp");
        params.put("dateStr", minDateForPullUp);
        KJHttp kjHttp = new KJHttp();
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.TIMEOUT = 3 * 60 * 1000;
        kjHttp.setConfig(httpConfig);
        kjHttp.post(GlobalContants.GETINBOXMSG_URL, params, false, new HttpCallBack() {

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
                if(maxDateForPullDown == null) {
                    maxDateForPullDown = formatForDataNull.format(new Date());
                }

                if(minDateForPullUp == null) {
                    minDateForPullUp = formatForDataNull.format(new Date());
                }
                ToastUtils.Show(mActivity, "加载数据失败，请稍后再试！");
            }

            @Override
            public void onSuccess(String t) {
//                Toast.makeText(mActivity, "上拉成功！", Toast.LENGTH_LONG).show();
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<InboxMsgVM>>() {
                }.getType();

                loadList.clear();
                loadList = gson.fromJson(t, type);
                if (msgList == null || msgList.size() <= 0)//清空后下拉再上拉需要加载出原来清空的数据 所以记录一次maxDate
                {
                    if (loadList != null && loadList.size() > 0) {
                        maxDateForPullDown = loadList.get(0).date;
                    }
                }
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
                actualListView.setAdapter(adapterForData);
                if (loadList != null && loadList.size() > 0) {
                    minDateForPullUp = loadList.get(loadList.size() - 1).date;
                }
//                if(msgList != null && msgList.size() > 0)
//                {
//                    try {
//                        maxDateForPullDown = format.format(format.parse(msgList.get(0).date));
//                    }
//                    catch (Exception ex) {
//
//                    }
//                }
                adapterForData.notifyDataSetChanged();
                actualListView.setSelection(msgList.size() - loadList.size());
                actualListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        InboxMsgVM item = msgList.get(position - 1);
                        if (item.url != null && item.url != "") {
                            Intent intent = new Intent(mActivity, InboxDetailActivity.class);
                            intent.putExtra("inboxDetailUrl", item.url + "&mobile=" + UserUtils.Tel);
                            intent.putExtra("title", "同鑫评论");
                            intent.putExtra("descript", item.msg);
                            intent.putExtra("sharedicon", "");
                            startActivity(intent);
                        }
                    }
                });
            }
        });
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

    public class ViewHolder {
        public TextView txt_msg;
        public TextView txt_date;
        public ImageView img_Msg;
        public ImageView img_Here;
    }

    private void Copy(String text) {
        ClipboardManager cbm = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData myClip = ClipData.newPlainText("text", text);
        cbm.setPrimaryClip(myClip);
    }

    private class GetDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            // Simulates a background job.
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            return new String[]{"aa", "aa"};
        }

        @Override
        protected void onPostExecute(String[] result) {
//            mListItems.addFirst("Added after refresh...");
            if ("pullDown".equals(pullMode)) {
                pullRefresh();
                Intent intentCount = new Intent("com.tongxin.badge");
                intentCount.putExtra("count", 0);
                mActivity.sendBroadcast(intentCount);
            } else if ("pullUp".equals(pullMode)) {
                loadMore();
            }
//            adapterForData.notifyDataSetChanged();

            // Call onRefreshComplete when the list has been refreshed.
            lv_msg.onRefreshComplete();

            super.onPostExecute(result);
        }
    }

}
