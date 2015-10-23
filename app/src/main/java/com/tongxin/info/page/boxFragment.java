package com.tongxin.info.page;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tongxin.info.R;
import com.tongxin.info.activity.InboxDetailActivity;
import com.tongxin.info.activity.MainActivity;
import com.tongxin.info.domain.InboxMsgVM;
import com.tongxin.info.domain.SearchItem;
import com.tongxin.info.domain.SearchVM;
import com.tongxin.info.global.GlobalContants;

import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;
import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/9/24.
 */
public class boxFragment extends Fragment {
    private Activity mActivity;
    private ListView lv_msg;
    private ArrayList<InboxMsgVM> msgList = new ArrayList<InboxMsgVM>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        initData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        container.removeAllViews();
        View view = View.inflate(mActivity, R.layout.inboxmsg, null);
        lv_msg = (ListView) view.findViewById(R.id.lvMsg);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initData()
    {
        KJHttp kjHttp = new KJHttp();
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.TIMEOUT = 3 * 60 * 1000;
        kjHttp.setConfig(httpConfig);
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
                        Toast.makeText(mActivity, "获取数据失败" + strMsg, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String t) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<ArrayList<InboxMsgVM>>() {
                        }.getType();

                        msgList = gson.fromJson(t,type);

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

                                if(convertView == null)
                                {
                                    viewHolder = new ViewHolder();
                                    convertView = View.inflate(mActivity, R.layout.boxcontent, null);
                                    viewHolder.txt_msg = (TextView) convertView.findViewById(R.id.txtMsg);
                                    viewHolder.txt_date = (TextView) convertView.findViewById(R.id.txtDate);
                                    viewHolder.img_Msg = (ImageView) convertView.findViewById(R.id.imgMsg);
//                                    viewHolder.msg_img = (TextView) convertView.findViewById(R.id.)
                                    convertView.setTag(viewHolder);
                                }
                                else {
                                    viewHolder = (ViewHolder) convertView.getTag();
                                }

                                viewHolder.txt_msg.setText(item.msg);
                                viewHolder.txt_date.setText(item.date);
                                if( item.url == null || item.url == "")
                                {
                                    viewHolder.img_Msg.setVisibility(View.GONE);
                                }
                                else
                                {
                                    viewHolder.img_Msg.setVisibility(View.VISIBLE);
                                }

                                return convertView;
                            }
                        });

                        lv_msg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                InboxMsgVM item = msgList.get(position);
                                Intent intent = new Intent(mActivity, InboxDetailActivity.class);
                                intent.putExtra("inboxDetailUrl",item.url);
                                startActivity(intent);
                            }
                        });
                    }
                }
        );
    }



    public class ViewHolder
    {
        public TextView txt_msg;
        public TextView txt_date;
        public ImageView img_Msg;
    }

}
