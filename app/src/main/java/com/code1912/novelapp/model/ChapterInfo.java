package com.code1912.novelapp.model;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Code1912 on 2016/12/3.
 */

public class ChapterInfo extends SugarRecord implements Serializable {
    public String title;
    public String content;
    public String url;
    public long novel_id;
    public Date add_date;
    public  boolean is_readed;
    public int position;
    public  int chapter_index;
    public boolean is_downloaded;
    @Ignore
    public boolean is_current;
}
