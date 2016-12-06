package com.code1912.novelapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.code1912.novelapp.adapter.ListAdapter;
import com.code1912.novelapp.biz.NovelBiz;
import com.code1912.novelapp.model.ChapterInfo;
import com.code1912.novelapp.model.Novel;
import com.code1912.novelapp.utils.Config;
import com.code1912.novelapp.utils.Transporter;
import com.code1912.novelapp.utils.Util;
import com.code1912.novelapp.viewholder.ChapterListTitleViewHolder;

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
	Toolbar toolbar;
	boolean isTempRead;
	int currentChapterIndex=-1;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chapter_list);

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle("");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toolbar.setNavigationOnClickListener(v -> ChapterListActivity.this.finish());
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
		novel= Transporter.instance.getTransportData(getIntent().getStringExtra(Config.NOVEL_INFO));
		toolbar.setTitle(novel.name);
		chapterInfoList=Transporter.instance.getTransportData(getIntent().getStringExtra(Config.CHAPTER_LIST));
		int index=0;
		for (ChapterInfo info : chapterInfoList) {
			if(info.chapter_index==currentChapterIndex){
				info.is_current=true;
				break;
			}
			index++;
		}

		listAdapter.addDataList(chapterInfoList);
		listView.setOnItemClickListener((v,v1,v2,v3)->onItemClick(v,v1,v2,v3));
	         final  int finalIndex=index;
		listView.post(() -> listView.setSelection(finalIndex));
		if(isTempRead){
			refreshView.setVisibility(View.GONE);
		}
	}

	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		ChapterInfo info=(ChapterInfo)listAdapter.getItem(i);
		Intent intent=new Intent();
                intent.putExtra(Config.CURRENT_CHAPTER_INDEX,info.chapter_index);
		for (ChapterInfo item : chapterInfoList) {
			item.is_current=false;
		}
		intent.putExtra(Config.CHAPTER_LIST, Transporter.instance.putArray(chapterInfoList));
                this.setResult(Config.CHAPTER_LIST_ACTIVITY_RESULT,intent);
		this.finish();
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


	@Override
	protected    void showMsg(String str) {
		super.showMsg(str);
		showLoading(false);
	}


}
