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

import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Linq4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

	public void search(String keyword, int pageIndex, SearchCallBack callBack ) {
		 query(Config.getSearchUrl(keyword, pageIndex), (call, response) -> {
			 try {
				 String str = response.body().string();
				 CommonResponse<Novel> result = JSON.parseObject(str, new TypeReference<CommonResponse<Novel>>() {
				 });
				 if (result == null ||result.resultList==null) {
					 callBack.onPost(null, false);
					 return;
				 }
				 callBack.onPost(result, true);
			 } catch (IOException e) {
				 Log.e("Search Novel Error:", e.getMessage());
				 callBack.onPost(null, false);
			 }
		 }, (c, error) -> {
			 callBack.onPost(null, false);
		 });
	}

	public void getChapterList(String url,ChapterListCallBack callBack) {
		query(Config.getChapterListUrl(url), (call, response) -> {
			try {
				String str = response.body().string();
				CommonResponse<ChapterInfo> result = JSON.parseObject(str, new TypeReference<CommonResponse<ChapterInfo>>() {
				});
				if (result == null || result.resultList == null) {
					callBack.onPost(null, false);
					return;
				}
				callBack.onPost(result.resultList, true);
			} catch (IOException e) {
				Log.e("Get ChapterList Error:", e.getMessage());
				callBack.onPost(null, false);
		}
		}, (c, error) -> {
			callBack.onPost(null, false);
		});
	}

	public void getChapterInfo(ChapterInfo info, boolean isNeedSave, ChapterInfoCallBack callBack){
		this.getChapterInfo(info.url,(result,success)->{
			if(result==null){
				callBack.onPost(null,false);
				return;
			}

			if(isNeedSave&&!Util.isNullOrEmpty(result.content)){
				info.content=result.content;
				info.is_downloaded=true;
				info.save();
				info.content="";
			}
			callBack.onPost(result,false);
		});
	}

	public List<ChapterInfo> getChapterListWithOutContentByNovelId(long novelId,int type){
		//ChapterInfo.executeQuery("VACUUM");
	    List<ChapterInfo>  list= 	ChapterInfo.findWithQuery(ChapterInfo.class,
		    String.format( "select id, title,url,novelId,addDate,isReaded,position,chapterIndex,isDownloaded,'' as content,type from  Chapter_Info where novelId=%d and type=%d order by  chapterIndex asc ",novelId,type));

	     return  list;
	}
	public  ChapterInfo  getChapterById(long chapterId){
		return      ChapterInfo.findById(ChapterInfo.class,chapterId);
	}
	public  String  getContentById(long chapterId){
                ChapterInfo chapterInfo=getChapterById(chapterId);
		return  chapterInfo!=null?chapterInfo.content:"";
	}
	public boolean hasExistNovel(String name,String author_name){
		if(Novel.count(Novel.class,String.format("name='%s' and authorname='%s'",name,author_name),null)>0){
			return true;
		}
		return  false;
	}
	public  void getNewChapterList(long novelId,ChapterListCallBack callBack) {
		Novel novel = Novel.findById(Novel.class,novelId);
		if (novel == null) {
			callBack.onPost(null, false);
			return;
		}
		getChapterList(novel.current_url, (resultList, isSuccess) -> {
			if (!isSuccess) {
				callBack.onPost(null, false);
				return;
			}
			List<ChapterInfo> oldChapters = ChapterInfo.find(ChapterInfo.class, String.format("novelid=%d", novel.getId()));
			if (oldChapters == null) {
				oldChapters = new ArrayList<ChapterInfo>();
			}
			Enumerable<ChapterInfo> queryList=Linq4j.asEnumerable(oldChapters);
			List<ChapterInfo> newList=Linq4j.asEnumerable(resultList).where(n->{
				return  !queryList.any(p->p.chapter_index==n.chapter_index);
			}).toList();
			callBack.onPost(newList,true);
		});
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

	public interface ChapterListCallBack {
		void onPost(List<ChapterInfo> list, boolean success);
	}

	public interface SearchCallBack {
		void onPost(CommonResponse<Novel> response, boolean success);
	}
}

