package com.tongxin.info.global;

/**
 * Created by Administrator on 2015/9/21.
 */
public class GlobalContants {
    public static final String SERVER_URL = "http://api.shtx.com.cn";//服务器地址。http://api.shtx.com.cn  ---http://172.20.68.162:3838
    public static final String GETMARKETS_URL = SERVER_URL + "/Handlers/XHMarketHandler.ashx?method=getmarkets";
    public static final String GETHQPRICES_URL = SERVER_URL + "/Handlers/PriceHandler.ashx?method=getPrices";
    public static final String GETHQHISTORYPRICES_URL = SERVER_URL + "/Handlers/PriceHandler.ashx?method=getHistoryPrices";
    public static final String SEARCH_URL = SERVER_URL + "/Handlers/SearchHandler.ashx?method=getSearchResult";
    public static final String GETINBOXMSG_URL = SERVER_URL + "/Handlers/InboxMsgHandler.ashx";
    public static final String ORDER_URL = SERVER_URL + "/Handlers/orderHandler.ashx";
    public static final String CHECKVERSION_URL = SERVER_URL + "/Handlers/UpdateHandler.ashx?method=checkversion";
}
