package com.tongxin.info.domain;

import java.util.ArrayList;

/**
 * Created by cc on 2015/11/5.
 */
public class SQDetailVM {
    public String name;
    public String quantity;
    public String price;
    public String contact;
    public String location;
    public String description;
    public String mobile;
    public ArrayList<SQDetailUrl> avatars;
    public String deliver;

    public class SQDetailUrl
    {
        public String avatar;
    }
}
