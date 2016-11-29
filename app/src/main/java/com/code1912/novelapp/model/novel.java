package com.code1912.novelapp.model;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

/**
 * Created by Code1912 on 2016/11/29.
 */

public class Novel extends SugarRecord {
    @Unique
    public  String id;
    public  String author_name;
    public  long dateModified;
    public  String  description  ;
    public  String genre;
    public  String genre_index;
    public  String image;
    public String[]  listPage_url;
    public  String  name;
    public  String newestChapter_headline;
    public  String  newestChapter_url;
    public  long  totalClick;
    public  String  trialStatus;
    public  String updateStatus;
    public  String  url;
    public  long  wordCount;

    public Novel(){

    }
}


