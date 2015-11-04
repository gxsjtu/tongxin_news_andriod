package com.tongxin.info.page;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tongxin.info.R;
import com.tongxin.info.activity.HqDetailActivity;
import com.tongxin.info.activity.MainActivity;
import com.tongxin.info.domain.MarketGroup;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/9/24.
 */
public class hq_contentFragment extends Fragment {

    private Activity mActivity;
    private MarketGroup marketGroup;
    private ListView hq_lv;
    public int marketId;
    private int position;

    public static hq_contentFragment newInstance(int position)
    {
        hq_contentFragment f = new hq_contentFragment();
        Bundle b = new Bundle();
        b.putInt("position", position);
        f.setArguments(b);
        return f;
    }

    public MarketGroup getMarketGroup() {
        return marketGroup;
    }

    public void setMarketGroup(MarketGroup marketGroup) {
        this.marketGroup = marketGroup;
        marketId = marketGroup.id;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        position = getArguments().getInt("position");
        marketGroup = hqFragment.marketGroups.get(position);
    }

    public void refLV()
    {
        hq_lv.setSelection(0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hqview,null);
        hq_lv = (ListView) view.findViewById(R.id.hq_lv);
        //marketGroup = (MarketGroup) ((MainActivity)mActivity).data;
        initData();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void initData()
    {
        hq_lv.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return marketGroup.markets.size();
            }

            @Override
            public MarketGroup.Market getItem(int position) {
                return marketGroup.markets.get(position);
            }

            @Override
            public long getItemId(int position) {
                return marketGroup.markets.get(position).id;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                ViewHolder viewHolder = null;
                if(convertView == null)
                {
                    viewHolder = new ViewHolder();
                    convertView = View.inflate(mActivity,R.layout.hq_lv_item,null);
                    viewHolder.hq_lv_item_tv = (TextView) convertView.findViewById(R.id.hq_lv_item_tv);
                    convertView.setTag(viewHolder);
                }
                else
                {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                MarketGroup.Market market = getItem(position);
                if(market !=null)
                {
                    viewHolder.hq_lv_item_tv.setText(market.name);

                }

                return convertView;
            }
        });

        hq_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MarketGroup.Market market = marketGroup.markets.get(position);
                Intent intent = new Intent(mActivity, HqDetailActivity.class);
                intent.putExtra("marketId",market.id);
                intent.putExtra("marketName",market.name);
                startActivity(intent);
            }
        });
    }

    private static class ViewHolder
    {
        TextView hq_lv_item_tv;
    }
}
