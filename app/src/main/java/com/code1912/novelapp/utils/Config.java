package com.code1912.novelapp.utils;

import com.code1912.novelapp.model.Novel;
import com.orm.SugarRecord;

import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Linq4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Code1912 on 2016/12/2.
 */

public class Config {
	public static String ApiHost = "http://www.code1912.cn:3000";
	public static final String TRANSPORT_KEY = "TRANSPORT_KEY";
	public static final String NOVEL_INFO = "NOVEL_INFO";
	public static final String CHAPTER_LIST = "CHAPTER_LIST";
	public static final String ADD_NOVEL_KEY = "ADD_NOVEL_KEY";
	public static final String KEY = "KEY";
	public static final String BROADCAST_ADD_NOVEL = "BROADCAST_ADD_NOVEL";
	public final static List<Novel> BookList=new ArrayList<>();

	public static Enumerable<Novel> getNovelListLinq(){
		return  Linq4j.asEnumerable(Config.BookList);
	}
	public static String getSearchUrl(String keyword, int pageIndex) {
		return String.format("%s/search?keyword=%s&pageIndex=%d&type=1&r=%d",
			Config.ApiHost,
			keyword,
			pageIndex,
			new Random().nextInt(20000));
	}

	public static String getChapterListUrl(String url) {
		return String.format("%s/chapterList?url=%s&type=1&r=%d",
			Config.ApiHost,
			url,
			new Random().nextInt(20000));
	}

	public static String getChapterInfoUrl(String url) {
		return String.format("%s/chapterInfo?url=%s&type=1&r=%d",
			Config.ApiHost,
			url,
			new Random().nextInt(20000));
	}
	static {
		List<Novel> list = Novel.listAll(Novel.class,"adddate desc");
		if (list != null&&list.size()>0) {
			BookList.addAll(list);
		}
	}
}
