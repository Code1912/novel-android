package com.code1912.novelapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.code1912.novelapp.biz.NovelBiz;
import com.code1912.novelapp.model.ChapterInfo;
import com.code1912.novelapp.model.CommonResponse;
import com.code1912.novelapp.model.Novel;
import com.code1912.novelapp.utils.Config;
import com.code1912.novelapp.utils.Transporter;
import com.code1912.novelapp.utils.Util;

import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Linq4j;

import java.util.List;

/**
 * Created by Code1912 on 2016/11/28.
 */

public class ChapterInfoActivity extends ActivityBase {

	ScrollView scrollView;
       TextView txtContent;
	TextView txtTitle;
	ChapterInfo chapterInfo;
	Novel novel;
	Toolbar toolbar;
	TableLayout footer;
	boolean isShowToolBar;
	List<ChapterInfo> chapterList;
	int positionY;
	boolean isTempRead=false;
	boolean isDownloadingAll=false;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chapter_info);
		scrollView = (ScrollView) findViewById(R.id.novel_scrollView);
		txtContent =(TextView)findViewById(R.id.chapter_content);
		txtTitle =(TextView)findViewById(R.id.novel_title);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		findViewById(R.id.btn_menu).setOnClickListener(v->this.btnMenuClick(v));
		findViewById(R.id.btn_download).setOnClickListener(v->this.btnDownloadClick(v));
		toolbar.setTitle("");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toolbar.setNavigationOnClickListener(v -> ChapterInfoActivity.this.finish());

		footer=(TableLayout) findViewById(R.id.chapter_footer);
		//scrollView.setOnTouchListener((v,m)->true);
		scrollView.setOnTouchListener((view, motionEvent) -> {
			if(motionEvent.getAction()!= MotionEvent.ACTION_UP){
				return  true ;
			}
		        int scrollHeight=scrollView.getMeasuredHeight();
			int centerHeight=scrollHeight/5;
			int y = (int) motionEvent.getY();
			//to up
			if(y<(scrollHeight-centerHeight)/2){
				scrollContent(true);
			}
			//to down
			else if(y>(scrollHeight+centerHeight)/2){
				scrollContent(false);
			}
			else {
				showToolBar(!isShowToolBar);
			}
			Log.i("Touch-------------:","");
			return  true;
		});
              /*  txtContent.setOnClickListener(view->{

			Log.i("Scroll---------",String.valueOf(scrollView.getMeasuredHeight()));
			Log.i("Text---------",String.valueOf(txtContent.getMeasuredHeight()));
			Log.i("Posit---------",String.valueOf(scrollView.getScrollY()));

		  //  scrollView.get
		});*/
		isTempRead=getIntent().getBooleanExtra(Config.IS_TEMP_READ,true);
		if(!getNovel()){
			return;
		}
		getChapterList();
		getChapterInfo();
	}
	private  void btnMenuClick(View v) {
		Intent newIntent = new Intent(ChapterInfoActivity.this, ChapterListActivity.class);

		newIntent.putExtra(Config.NOVEL_INFO, Transporter.instance.putObject(novel));

		List<ChapterInfo> tempList=Util.deepCloneArray(chapterList);
		for (ChapterInfo info : tempList) {
			info.content=null;
		}
		newIntent.putExtra(Config.CHAPTER_LIST, Transporter.instance.putArray(tempList
		));
		newIntent.putExtra(Config.IS_TEMP_READ, isTempRead);
		newIntent.putExtra(Config.CURRENT_CHAPTER_INDEX, chapterInfo.chapter_index);
		showToolBar(false);

		startActivityForResult(newIntent,Config.CHAPTER_LIST_ACTIVITY_RESULT);
	}

	private  void btnDownloadClick(View v){
		if(Linq4j.asEnumerable(chapterList).all(n->n.is_downloaded)){
			showMsg("已经全部下载完成了");
			return;
		}
		if(isDownloadingAll){
			showMsg("已经开始下载.....");
			return;
		}

		isDownloadingAll=true;
		showMsg("已经开始下载.....");
		for (ChapterInfo info : chapterList) {
			if(info.is_downloaded){
				continue;
			}
			NovelBiz.instance.getChapterInfo(info,!isTempRead,(res,v1)->{

			});
		}

	}

	private  void getChapterList(){
		if(isTempRead){
			chapterList=Transporter.instance.getTransportData(getIntent().getStringExtra(Config.CHAPTER_LIST));
			return;
		}
		chapterList=ChapterInfo.find(ChapterInfo.class,String.format( "novelid=%d and type=%d ",novel.getId(),novel.type),null,"","chapterindex asc","");
		chapterList=Linq4j.asEnumerable(chapterList).orderBy(p->p.getId()).toList();
	}

        public void showToolBar(boolean isShow){
		isShowToolBar=isShow;
		int visibility=isShow? View.VISIBLE:View.GONE;
		toolbar.setVisibility(visibility);
		footer.setVisibility(visibility);
	}
	private void  scrollContent(boolean isToUp){
		int scrollViewHeight=scrollView.getMeasuredHeight();
		int contentHeight=txtContent.getMeasuredHeight();

		if(isToUp){
			scrollViewHeight=-scrollViewHeight;
		}
		int currentY=scrollView.getScrollY();
		int moveY=currentY+scrollViewHeight;
		if(moveY<0){
			moveY=0;
			toNext(-1);
		}
		if(contentHeight<=moveY){
			moveY=contentHeight-scrollViewHeight;
			toNext(1);
			return;
		}
		chapterInfo.position=moveY;
		if(!isTempRead) {
			ChapterInfo.save(chapterInfo);
		}
		scrollView.scrollTo(0,moveY);
	}
	private  void toNext(int i) {

		int index = chapterList.indexOf(chapterInfo);
		if (i<0&&index == 0) {
			Util.toast(this, "没有更多啦");
			return;
		}
		if(i>0&&index==(chapterList.size()-1)){
			return;
		}
		chapterInfo.position=0;
		if(!isTempRead){
			chapterInfo.save(chapterInfo);
		}
		chapterInfo=chapterList.get(index+i);
		novel.last_chapter_index=chapterInfo.chapter_index;
		getChapterInfo();
	}
        private boolean getNovel(){
		novel=Transporter.instance.getTransportData(getIntent().getStringExtra(Config.NOVEL_INFO));
		if(novel==null){
			Util.toast(ChapterInfoActivity.this,"获取小说信息失败");
			return false;
		}
		return  true;
	}
	private  void getChapterInfo() {
		if (novel.last_chapter_index < 1) {
			chapterInfo = chapterList.get(0);
		} else {
			chapterInfo = Linq4j.asEnumerable(chapterList).firstOrDefault(p -> p.chapter_index == novel.last_chapter_index);
		}
		if (chapterInfo == null) {
			Util.toast(ChapterInfoActivity.this, "获取章节信息失败");
			return;
		}
		if (!Util.isNullOrEmpty(chapterInfo.content)) {
			updateChapterInfo();
			refreshUI();
			return;
		}
		NovelBiz.instance.getChapterInfo(chapterInfo.url, (info, isSuccess) -> {
			if (!isSuccess) {
				showMsg("获取信息失败");
				return;
			}
			chapterInfo.content = info.content;
			updateChapterInfo();
			refreshUI();
		});
	}

	private  void updateChapterInfo(){
		chapterInfo.is_readed=true;
		chapterInfo.is_downloaded=true;
		if(isTempRead){
			return;
		}
		ChapterInfo.save(chapterInfo);
		novel.last_chapter_index=chapterInfo.chapter_index;
		novel.read_chapter_count=ChapterInfo.count(ChapterInfo.class,"isreaded='1' and novelid=?",new String[]{  String.valueOf(novel.getId())});
		Novel.save(novel);
		notifyReadCountChanged();
	}

	private  void refreshUI(){
		runOnUiThread(()->{
			String content=Util.isNullOrEmpty(chapterInfo.content)?"":chapterInfo.content;
			txtTitle.setText(Html.fromHtml(chapterInfo.title));
			txtContent.setText (content.replace("\\n","\n").replace("\\r","\r").replace("\\t","\t"));
			scrollView.scrollTo(0,chapterInfo.position);
		});
	}

	private  void notifyReadCountChanged(){
		Intent intent = new Intent();
		intent.putExtra(Config.KEY,Config.NOTIFY_NOVEL_KEY);
		intent.setAction(Config.BROADCAST_NOTIFY_NOVEL);
		Bundle bundle = new Bundle();
		String str = JSON.toJSONString(novel);
		bundle.putString(Config.NOVEL_INFO, str);
		intent.putExtras(bundle);
		sendBroadcast(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==Config.CHAPTER_LIST_ACTIVITY_RESULT){
			 int index=data.getIntExtra(Config.CURRENT_CHAPTER_INDEX,0);
			 chapterList =Transporter.instance.getTransportData(data.getStringExtra(Config.CHAPTER_LIST));
                         chapterInfo= Linq4j.asEnumerable(chapterList).first(n->n.chapter_index==index);
			 novel.last_chapter_index=chapterInfo.chapter_index;
			 getChapterInfo();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}