package com.tongxin.info.domain;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/9/23.
 */
public class MarketGroup {
    public int id;
    public String name;
    public ArrayList<Market> markets;

    public class Market
    {
        public int id;
        public String name;
    }
}
