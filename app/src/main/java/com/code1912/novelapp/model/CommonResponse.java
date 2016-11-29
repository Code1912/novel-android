package com.code1912.novelapp.model;

import java.util.List;

/**
 * Created by Code1912 on 2016/11/30.
 */

public class CommonResponse<T> {
    public boolean success;
    public String message;
    public List<T> resultList;
    public T result;
    public int pageIndex;
    public int totalCount;
}
