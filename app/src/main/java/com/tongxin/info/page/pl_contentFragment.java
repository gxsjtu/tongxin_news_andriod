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
import com.tongxin.info.activity.PingLunDetailActivity;
import com.tongxin.info.domain.MarketGroup;

/**
 * Created by Administrator on 2015/9/24.
 */
public class pl_contentFragment extends Fragment {

    private Activity mActivity;
    private MarketGroup marketGroup;
    private ListView pl_lv;
    private int position;

    public static pl_contentFragment newInstance(int position)
    {
        pl_contentFragment f = new pl_contentFragment();
        Bundle b = new Bundle();
        b.putInt("position", position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("position", position);
        outState.putSerializable("marketGroup",marketGroup);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        if(savedInstanceState!=null)
        {
            position = savedInstanceState.getInt("position");
            marketGroup = (MarketGroup) savedInstanceState.getSerializable("marketGroup");
        }
        else {
            position = getArguments().getInt("position");
            marketGroup = plFragment.marketGroups.get(position);
        }
    }

    public void refLV()
    {
        //pl_lv.setSelection(0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hqview,null);
        pl_lv = (ListView) view.findViewById(R.id.hq_lv);
        initData();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void initData()
    {
        pl_lv.setAdapter(new BaseAdapter() {
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

        pl_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MarketGroup.Market market = marketGroup.markets.get(position);
                Intent intent = new Intent(mActivity, PingLunDetailActivity.class);
                intent.putExtra("marketId",market.id);
                intent.putExtra("groupId", position);
                intent.putExtra("groupName",marketGroup.name);
                intent.putExtra("marketName",marketGroup.id);
                startActivity(intent);
            }
        });
    }

    private static class ViewHolder
    {
        TextView hq_lv_item_tv;
    }
}
