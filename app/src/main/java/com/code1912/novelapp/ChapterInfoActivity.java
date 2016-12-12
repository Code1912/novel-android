package com.code1912.novelapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextPaint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.code1912.novelapp.biz.NovelBiz;
import com.code1912.novelapp.extend.ReadView;
import com.code1912.novelapp.extend.ReadViewPager;
import com.code1912.novelapp.model.ChapterInfo;
import com.code1912.novelapp.model.Novel;
import com.code1912.novelapp.utils.Config;
import com.code1912.novelapp.utils.Transporter;
import com.code1912.novelapp.utils.Util;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.calcite.linq4j.Linq4j;

import java.util.List;

/**
 * Created by Code1912 on 2016/11/28.
 */

public class ChapterInfoActivity extends ActivityBase {

	//ScrollView scrollView;

	TextView txtTitle;
	ReadViewPager txtPager;
	ChapterInfo chapterInfo;
	Novel novel;
	Toolbar toolbar;
	TableLayout footer;
	boolean isShowToolBar;
	List<ChapterInfo> chapterList;
	boolean isTempRead = false;
	boolean isDownloadingAll = false;
	boolean isPagerClicked=true;
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_chapter_info);
		//scrollView = (ScrollView) findViewById(R.id.novel_scrollView);
		txtPager = (ReadViewPager) findViewById(R.id.txt_pager);
		txtTitle = (TextView) findViewById(R.id.novel_title);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		findViewById(R.id.btn_menu).setOnClickListener(v -> this.btnMenuClick(v));
		findViewById(R.id.btn_download).setOnClickListener(v -> this.btnDownloadClick(v));
		toolbar.setTitle("");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toolbar.setNavigationOnClickListener(v -> ChapterInfoActivity.this.finish());

		footer = (TableLayout) findViewById(R.id.chapter_footer);

		isTempRead = getIntent().getBooleanExtra(Config.IS_TEMP_READ, true);
		if (!getNovel()) {
			return;
		}
		getChapterList();
		getChapterInfo(false);

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		txtPager.setOnPageListener((d,p)->txtPagerOnPage(d,p));
	}

	private  void txtPagerOnPage(ReadViewPager.ActionDirection direction, int pageIndex){
		if(isPagerClicked){
			return;
		}
		isPagerClicked=true;
		if(direction== ReadViewPager.ActionDirection.TO_CENTER){
                        showToolBar(!isShowToolBar);
			isPagerClicked=false;
		}
		else  if(direction== ReadViewPager.ActionDirection.TO_PRE_CHAPTER){
                         toNext(-1);
			showToolBar(false);
		}
		else if(direction== ReadViewPager.ActionDirection.TO_NEXT_CHAPTER){
			toNext(1);
			showToolBar(false);
		}
		else {
			if(pageIndex>0&&!isTempRead){
				chapterInfo.position=pageIndex;
				chapterInfo.save();
			}
			if(isShowToolBar){
				showToolBar(false);
			}
			isPagerClicked=false;
		}


	}
	private void btnMenuClick(View v) {
		Intent newIntent = new Intent(ChapterInfoActivity.this, ChapterListActivity.class);

		newIntent.putExtra(Config.NOVEL_INFO, Transporter.instance.putObject(novel));

		List<ChapterInfo> tempList = Util.deepCloneArray(chapterList);
		for (ChapterInfo info : tempList) {
			info.content = null;
		}
		newIntent.putExtra(Config.CHAPTER_LIST, Transporter.instance.putArray(tempList
		));
		newIntent.putExtra(Config.IS_TEMP_READ, isTempRead);
		newIntent.putExtra(Config.CURRENT_CHAPTER_INDEX, chapterInfo.chapter_index);
		showToolBar(false);

		startActivityForResult(newIntent, Config.CHAPTER_LIST_ACTIVITY_RESULT);
	}

	private void btnDownloadClick(View v) {
		if (isTempRead) {
			return;
		}
		if (Linq4j.asEnumerable(chapterList).all(n -> n.is_downloaded)) {
			showMsg("已经全部下载完成了");
			return;
		}
		if (isDownloadingAll) {
			showMsg("已经开始下载.....");
			return;
		}

		isDownloadingAll = true;
		showMsg("已经开始下载.....");
		for (ChapterInfo info : chapterList) {
			if (info.is_downloaded) {
				continue;
			}

			NovelBiz.instance.getChapterInfo(info, !isTempRead, (res, v1) -> {

			});
		}

	}

	private void getChapterList() {
		if (isTempRead) {
			chapterList = Transporter.instance.getTransportData(getIntent().getStringExtra(Config.CHAPTER_LIST));
			return;
		}
		chapterList = NovelBiz.instance.getChapterListWithOutContentByNovelId(novel.getId(), novel.type);
		chapterList = Linq4j.asEnumerable(chapterList).orderBy(p -> p.getId()).toList();
	}

	public void showToolBar(boolean isShow) {
		isShowToolBar = isShow;
		int visibility = isShow ? View.VISIBLE : View.GONE;
		toolbar.setVisibility(visibility);
		footer.setVisibility(visibility);
	}

	private void toNext(int i) {
		int index = chapterList.indexOf(chapterInfo);
		if (i < 0 && index == 0) {
			Util.toast(this, "没有更多啦");
			isPagerClicked=false;
			return;
		}
		if (i > 0 && index == (chapterList.size() - 1)) {
			getMore();
			return;
		}

		chapterInfo = chapterList.get(index + i);
		chapterInfo.position=0;
		novel.last_chapter_index = chapterInfo.chapter_index;
		getChapterInfo(i<0);
	}

	private  void getMore() {
		NovelBiz.instance.getNewChapterList(novel.getId(), (list, success) -> {
			if (!success) {
				isPagerClicked=false;
				showMsg("没有更多了");
				return;
			}
			if (list == null || list.size() == 0) {
				isPagerClicked=false;
				showMsg("没有更多了");
				return;
			}
			for (ChapterInfo chapterInfo : list) {
				chapterInfo.novel_id = novel.getId();
				chapterInfo.add_date = Util.getCurrentDate();
			}
			ChapterInfo.saveInTx(list);
			chapterList.addAll(list);
			novel.is_have_new=false;
			toNext(1);
		});
	}
	private boolean getNovel() {
		novel = Transporter.instance.getTransportData(getIntent().getStringExtra(Config.NOVEL_INFO));
		if (novel == null) {
			Util.toast(ChapterInfoActivity.this, "获取小说信息失败");
			return false;
		}
		return true;
	}


	private void getChapterInfo( boolean isToPrevious) {
		this.showLoading(true);
		if (novel.last_chapter_index < 1) {
			chapterInfo = chapterList.get(0);
		} else {
			chapterInfo = Linq4j.asEnumerable(chapterList).firstOrDefault(p -> {
				return p.chapter_index == novel.last_chapter_index;
			});
		}
		if (chapterInfo == null) {
			isPagerClicked=false;
			Util.toast(ChapterInfoActivity.this, "获取章节信息失败");
			this.showLoading(false);
			return;
		}
		if (chapterInfo.getId() != null && chapterInfo.getId() > 0 && Util.isNullOrEmpty(chapterInfo.content)) {
			chapterInfo.content = NovelBiz.instance.getContentById(chapterInfo.getId());
		}
		if (!Util.isNullOrEmpty(chapterInfo.content)) {
			isPagerClicked=false;
			updateChapterInfo();
			refreshUI(isToPrevious);
			this.showLoading(false);
			return;
		}
		NovelBiz.instance.getChapterInfo(chapterInfo.url, (info, isSuccess) -> {
			isPagerClicked=false;
			this.showLoading(false);
			if (!isSuccess) {
				showMsg("获取信息失败");
				return;
			}
			chapterInfo.content = info.content;
			updateChapterInfo();
			refreshUI(isToPrevious);
		});
	}

	private void updateChapterInfo() {
		chapterInfo.is_readed = true;
		chapterInfo.is_downloaded = true;
		if (isTempRead) {
			return;
		}
		ChapterInfo.save(chapterInfo);
		novel.last_chapter_index = chapterInfo.chapter_index;
		novel.read_chapter_count = ChapterInfo.count(ChapterInfo.class, "isreaded='1' and novelid=?", new String[]{String.valueOf(novel.getId())});
		Novel.save(novel);
		notifyReadCountChanged();
	}

	private void refreshUI(boolean isToPrevious) {
		this.txtPager.post(() -> {
			String content = Util.isNullOrEmpty(chapterInfo.content) ? "" : chapterInfo.content;
			txtTitle.setText(chapterInfo.title);
			txtPager.setText(content, new ReadViewPager.FontSetting(55,30), isToPrevious?99999:chapterInfo.position);
		});
	}


	private void notifyReadCountChanged() {
		Intent intent = new Intent();
		intent.putExtra(Config.KEY, Config.NOTIFY_NOVEL_KEY);
		intent.setAction(Config.BROADCAST_NOTIFY_NOVEL);
		Bundle bundle = new Bundle();
		String str = JSON.toJSONString(novel);
		bundle.putString(Config.NOVEL_INFO, str);
		intent.putExtras(bundle);
		sendBroadcast(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Config.CHAPTER_LIST_ACTIVITY_RESULT) {
			int index = data.getIntExtra(Config.CURRENT_CHAPTER_INDEX, 0);
			chapterList = Transporter.instance.getTransportData(data.getStringExtra(Config.CHAPTER_LIST));
			chapterInfo = Linq4j.asEnumerable(chapterList).first(n -> n.chapter_index == index);
			novel.last_chapter_index = chapterInfo.chapter_index;
			chapterInfo.position=0;
			getChapterInfo(false);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}