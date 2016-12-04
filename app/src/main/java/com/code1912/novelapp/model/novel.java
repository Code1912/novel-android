package com.code1912.novelapp.model;

import com.code1912.novelapp.utils.Util;
import com.orm.SugarRecord;
import com.orm.dsl.Unique;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Code1912 on 2016/11/29.
 */

public class Novel extends SugarRecord implements Serializable {
    public  String author_name;
    public  long dateModified;
    public  String  description  ;
    public  String genre;
    public  String genre_index;
    public  String image;
    public String[]  listPage_url;
    public String current_url;
    public  String  name;
    public  String newestChapter_headline;
    public  String  newestChapter_url;
    public  long  totalClick;
    public  String  trialStatus;
    public  String updateStatus;
    public  String  url;
    public  long  wordCount;
    public  long read_chapter_count;
    public int all_chapter_count;
    public boolean is_have_new;
    public long last_read_chapter_id;
    public  boolean refreshed;
    public Date add_date;
     public  String getLastEditDate(){
       return    Util.getStrTime(this.dateModified,"yyyy-MM-dd HH:MM:ss");
     }
    public Novel(){

    }
}


