package com.code1912.novelapp.utils;

import java.util.Random;

/**
 * Created by Code1912 on 2016/12/2.
 */

public class Config {
    public static  String ApiHost = "http://www.code1912.cn:3000";
    public   static final String TRANSPORT_KEY = "keyword";
    public static  String getSearchUrl(String keyword,int pageIndex){
        return String.format("%s/search?keyword=%s&pageIndex=%d&type=1&r=%d",
                Config.ApiHost,
                keyword,
                pageIndex,
                new Random().nextInt(20000));
    }
    public static  String getChapterListUrl(String url){
        return String.format("%s/chapterList?url=%s&type=1&r=%d",
                Config.ApiHost,
                url,
                new Random().nextInt(20000));
    }
    public static  String getChapterInfoUrl(String url){
        return String.format("%s/chapterInfo?url=%s&type=1&r=%d",
                Config.ApiHost,
                url,
                new Random().nextInt(20000));
    }
}
