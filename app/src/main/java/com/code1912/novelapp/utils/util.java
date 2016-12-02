package com.code1912.novelapp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Code1912 on 2016/11/29.
 */

public class Util {
    public static String getStrTime(long cc_time,String format) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
// 例如：cc_time=1291778220
        re_StrTime = sdf.format(new Date(cc_time * 1000L));
        return re_StrTime; 
    }
}
