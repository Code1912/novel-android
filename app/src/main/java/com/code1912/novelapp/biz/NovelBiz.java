package com.code1912.novelapp.biz;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.code1912.novelapp.ChapterInfoActivity;
import com.code1912.novelapp.SearchActivity;
import com.code1912.novelapp.model.ChapterInfo;
import com.code1912.novelapp.model.CommonResponse;
import com.code1912.novelapp.model.Novel;
import com.code1912.novelapp.utils.Config;
import com.code1912.novelapp.utils.Util;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Code1912 on 2016/12/4.
 */

public class NovelBiz {
	OkHttpClient  mOkHttpClient = new OkHttpClient();

	private  void query(String url, SuccessCallBack successCallBack, FailCallBack failCallBack){
		Request request = new Request.Builder()
			.url(url)
			.build();
		Call call = mOkHttpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				if (successCallBack != null) {
					successCallBack.onResponse(call, response);
				}
			}

			@Override
			public void onFailure(Call call, IOException e) {
				if (failCallBack != null) {
					failCallBack.onFailure(call, e);
				}
			}
		});
	}

	private void search(String keyword, int pageIndex, SuccessCallBack successCallBack, FailCallBack failCallBack) {
		 query(Config.getSearchUrl(keyword, pageIndex),successCallBack,failCallBack);
	}

	public  void getChapterList(String url, SuccessCallBack successCallBack, FailCallBack failCallBack){
		query(Config.getChapterListUrl(url),successCallBack,failCallBack);
	}

	private   void getChapterInfo(String url, SuccessCallBack successCallBack, FailCallBack failCallBack){
		query(Config.getChapterInfoUrl(url),successCallBack,failCallBack);
	}

	public void getChapterInfo(String url,ChapterInfoCallBack callBack) {
		query(Config.getChapterInfoUrl(url), (call, response) -> {
			try {
				String str = response.body().string();
				CommonResponse<ChapterInfo> result = JSON.parseObject(str, new TypeReference<CommonResponse<ChapterInfo>>() {
				});
				if (result == null || result.result == null) {
					callBack.onPost(null, false);
					return;
				}
				callBack.onPost(result.result, true);
			} catch (IOException e) {
				Log.e("Get ChapterInfo Error:", e.getMessage());
				callBack.onPost(null, false);
			}
		}, (c, error) -> {
			callBack.onPost(null, false);
		});
	}

	public interface SuccessCallBack {
		void onResponse(Call call, Response response) throws IOException;
	}

	public interface FailCallBack {
		void onFailure(Call call, IOException e);
	}
	public final static  NovelBiz instance=new NovelBiz();

	public interface ChapterInfoCallBack {
		void onPost(ChapterInfo str,boolean success);
	}
}

