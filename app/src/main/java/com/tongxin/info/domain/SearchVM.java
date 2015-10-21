package com.tongxin.info.domain;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/10/20.
 */
public class SearchVM {
    public int id;
    public String name;
    public ArrayList<SearchPrice> products;

    public class SearchPrice
    {
        public int ProductId;
        public String ProductName;
        public String LPrice;
        public String HPrice;
        public String Date;
        public String Change;
        public String isOrder;
    }
}
