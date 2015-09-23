package com.tongxin.info.base.impl;

import android.app.Activity;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tongxin.info.base.BasePager;
import com.tongxin.info.domain.MarketGroup;
import com.tongxin.info.global.GlobalContants;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/9/23.
 * 行情页面
 */
public class futurePager extends BasePager {
    public futurePager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        setTitle("实时行情");//设置标题
        setSlidingMenuEnable(true);//启用侧边栏

        getServerData();
    }

    private void getServerData()
    {
        KJHttp kjHttp = new KJHttp();

        kjHttp.get(GlobalContants.GETMARKETS_URL, new HttpCallBack() {
            @Override
            public void onFailure(int errorNo, String strMsg) {
                Toast.makeText(mActivity, "获取数据失败" + strMsg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String t) {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<MarketGroup>>() {
                }.getType();
                ArrayList<MarketGroup> marketGroups = gson.fromJson(t, type);
                Toast.makeText(mActivity, "总共" + marketGroups.size() + "条记录", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
