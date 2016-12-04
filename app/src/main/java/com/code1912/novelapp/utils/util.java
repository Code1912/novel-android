package com.code1912.novelapp.utils;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.code1912.novelapp.model.Novel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Code1912 on 2016/11/29.
 */

public class Util {
    public static String getStrTime(long cc_time, String format) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
// 例如：cc_time=1291778220
        re_StrTime = sdf.format(new Date(cc_time * 1000L));
        return re_StrTime;
    }

    public    static  void toast(Context context,String msg){
        Toast   toast = Toast.makeText(context,
                msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
    public static void putObject2Bundle(Bundle bundle, String key, Object obj){
        String str = JSON.toJSONString(obj);
        bundle.putString(key,str);
    }


    public  static Date  getCurrentDate(){
        return  new Date(System.currentTimeMillis());
    }
    public static  boolean isNullOrEmpty(String str){
        if(str==null){
            return  true;
        }
        return  str.isEmpty();
    }
    @SuppressWarnings("unchecked")
    public static <T> T cloneTo(T src) throws RuntimeException {
        ByteArrayOutputStream memoryBuffer = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        T dist = null;
        try {
            out = new ObjectOutputStream(memoryBuffer);
            out.writeObject(src);
            out.flush();
            in = new ObjectInputStream(new ByteArrayInputStream(memoryBuffer.toByteArray()));
            dist = (T) in.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (out != null)
                try {
                    out.close();
                    out = null;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            if (in != null)
                try {
                    in.close();
                    in = null;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }
        return dist;
    }
}
