package com.code1912.novelapp.utils;

import android.util.Log;

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
	public static final String CHAPTER_INFO_SCREENSHOT ="CHAPTER_INFO_SCREENSHOT" ;
	public static String ApiHost = "http://127.0.0.1:3000";
	public static final String TRANSPORT_KEY = "TRANSPORT_KEY";
	public static final String NOVEL_INFO = "NOVEL_INFO";
	public static final String CHAPTER_LIST = "CHAPTER_LIST";
	public static final String CHAPTER_INFO = "CHAPTER_INFO";
	public static final String ADD_NOVEL_KEY = "ADD_NOVEL_KEY";
	public static final String NOTIFY_NOVEL_KEY = "ADD_NOVEL_KEY";
	public static final String KEY = "KEY";
	public static final String IS_TEMP_READ = "IS_TEMP_READ";
	public static final String CURRENT_CHAPTER_INDEX = "CURRENT_CHAPTER_INDEX";
	public static final String BROADCAST_ADD_NOVEL = "BROADCAST_ADD_NOVEL";
	public static final String BROADCAST_NOTIFY_NOVEL = "BROADCAST_NOTIFY_NOVEL";
	public static  final  int CHAPTER_LIST_ACTIVITY_RESULT=90;
	public static  final  int SETTING_ACTIVITY_RESULT=91;

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
		String tempUrl= String.format("%s/chapterInfo?url=%s&type=1&r=%d",
			Config.ApiHost,
			url,
			new Random().nextInt(20000));
		Log.i("URL-------------------",tempUrl);
		return  tempUrl;
	}
	static {

	}
}
