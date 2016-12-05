package com.code1912.novelapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.code1912.novelapp.adapter.ListAdapter;
import com.code1912.novelapp.biz.NovelBiz;
import com.code1912.novelapp.model.ChapterInfo;
import com.code1912.novelapp.model.Novel;
import com.code1912.novelapp.utils.Config;
import com.code1912.novelapp.utils.Util;
import com.code1912.novelapp.viewholder.ChapterListTitleViewHolder;

import org.apache.calcite.linq4j.Linq4j;

import java.util.List;

/**
 * Created by Code1912 on 2016/11/28.
 */

public class ChapterListActivity extends ActivityBase {
	Novel novel;
	List<ChapterInfo> chapterInfoList;
	ListAdapter<ChapterInfo> listAdapter;
	ListView listView;
	ImageView refreshView;
	boolean isTempRead;
	int currentChapterIndex=-1;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chapter_list);
		listAdapter=new ListAdapter(this, ChapterListTitleViewHolder.class,R.layout.activity_chapter_list_item);
		listView=(ListView) findViewById(R.id.chapter_list_listView);
		refreshView=(ImageView) findViewById(R.id.btn_refresh);
		refreshView.setOnClickListener(v->btnRefreshClick(v));
		listView.setAdapter(listAdapter);
		getData();
	}

	private void   getData(){
		isTempRead=getIntent().getBooleanExtra(Config.IS_TEMP_READ,true);
		currentChapterIndex=getIntent().getIntExtra(Config.CURRENT_CHAPTER_INDEX,-1);
		novel= JSON.parseObject(getIntent().getStringExtra(Config.NOVEL_INFO),Novel.class);
		chapterInfoList=JSON.parseObject(getIntent().getStringExtra(Config.CHAPTER_LIST),new TypeReference<List<ChapterInfo>>(){});
		Linq4j.asEnumerable(chapterInfoList).first(p->{
			if(p.chapter_index==currentChapterIndex){
				p.is_current=true;
			}
			return p.chapter_index==currentChapterIndex;
		});
		listAdapter.addDataList(chapterInfoList);
		if(isTempRead){
			refreshView.setVisibility(View.GONE);
		}
	}

	private  void btnRefreshClick(View v) {

		this.showLoading(true);
		NovelBiz.instance.getNewChapterList(novel.getId(), (list, success) -> {
			if (!success) {
		               showMsg( "更新失败");
				return;
			}
			if(list==null||list.size()==0){
				showMsg( "暂无更新");
				return;
			}
			for (ChapterInfo chapterInfo : list) {
				chapterInfo.novel_id=novel.getId();
				chapterInfo.add_date=Util.getCurrentDate();
			}
			ChapterInfo.saveInTx(list);
			chapterInfoList.addAll(list);
			runOnUiThread(()->{showLoading(false);});
		});
	}

	private  void showMsg(String str){
		runOnUiThread(() -> {
			Util.toast(ChapterListActivity.this, str);
			showLoading(false);
		});
	}


}
