package com.tongxin.info.domain;

import android.database.SQLException;
import android.util.Log;

import com.tongxin.info.page.hqFragment;
import com.tongxin.info.page.plFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChannelManage {
    public static ChannelManage channelManage;
    /**
     * 默认的用户选择频道列表
     */
    public static ArrayList<ChannelItem> defaultUserChannels = new ArrayList<ChannelItem>();
    /**
     * 默认的其他频道列表
     */
    public static ArrayList<ChannelItem> defaultOtherChannels = new ArrayList<ChannelItem>();

    /**
     * 初始化频道管理类
     */
    public static ChannelManage getManage() {
        if (channelManage == null)
            channelManage = new ChannelManage();
        return channelManage;
    }

    /**
     * 清除所有的频道
     */
    public void deleteAllChannel() {

    }

    /**
     * 获取其他的频道
     *
     * @return 数据库存在用户配置 ? 数据库内的用户选择频道 : 默认用户选择频道 ;
     */
    public ArrayList<ChannelItem> getUserChannel() {
        return defaultUserChannels;
    }

    public void initChannel(String tag) {
        defaultUserChannels.clear();
        defaultOtherChannels.clear();
        ArrayList<MarketGroup> list = new ArrayList<MarketGroup>();
        if(tag.equals("hqFragment"))
        {
            list = hqFragment.allMarketGroups;
        }
        else
        {
            list = plFragment.allMarketGroups;
        }
        if (list.size() > 0) {
            int userOrderId = 1;
            int otherOrderId = 1;
            for (int i = 0; i < list.size(); i++) {
                MarketGroup group = list.get(i);
                if (group.inBucket.equals("true")) {
                    defaultUserChannels.add(new ChannelItem(group.id, group.name, userOrderId, 1, i));
                    userOrderId++;
                } else {
                    defaultOtherChannels.add(new ChannelItem(group.id, group.name, otherOrderId, 0, i));
                    otherOrderId++;
                }
            }
        }
    }

    /**
     * 获取其他的频道
     *
     * @return 数据库存在用户配置 ? 数据库内的其它频道 : 默认其它频道 ;
     */
    public ArrayList<ChannelItem> getOtherChannel() {
        return defaultOtherChannels;
    }

    /**
     * 保存用户频道到数据库
     *
     * @param userList
     */
    public void saveUserChannel(List<ChannelItem> userList) {
        for (int i = 0; i < userList.size(); i++) {

        }
    }

    /**
     * 保存其他频道到数据库
     *
     * @param otherList
     */
    public void saveOtherChannel(List<ChannelItem> otherList) {
        for (int i = 0; i < otherList.size(); i++) {

        }
    }
}

