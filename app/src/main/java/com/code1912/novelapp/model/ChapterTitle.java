package com.code1912.novelapp.model;

import com.orm.SugarRecord;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Code1912 on 2016/12/3.
 */

public class ChapterTitle extends SugarRecord implements Serializable {
    public String title;
    public String url;
    public long novel_id;
    public Date add_date;
}
