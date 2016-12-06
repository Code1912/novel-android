package com.code1912.novelapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.code1912.novelapp.adapter.ChapterListAdapter;
import com.code1912.novelapp.biz.NovelBiz;
import com.code1912.novelapp.model.ChapterInfo;
import com.code1912.novelapp.model.CommonResponse;
import com.code1912.novelapp.model.Novel;
import com.code1912.novelapp.utils.Config;
import com.code1912.novelapp.utils.Transporter;
import com.code1912.novelapp.utils.Util;

import org.apache.calcite.linq4j.Linq4j;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

/**
 * Created by Code1912 on 2016/12/1.
 */

public class NovelActivity extends ActivityBase {
    Novel novel;
    ListView listView;
    ChapterListAdapter listViewAdapter;
    OkHttpClient mOkHttpClient = new OkHttpClient();
    CommonResponse<ChapterInfo> result;
    ScrollView scrollView;
    List<ChapterInfo> allChapterInfoList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novel);
        scrollView = (ScrollView) findViewById(R.id.novel_scrollView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> NovelActivity.this.finish());

        listViewAdapter = new ChapterListAdapter(this);
        listView = (ListView) findViewById(R.id.novel_chapter_listView);
        listView.setAdapter(listViewAdapter);
        listView.setOnItemClickListener((v,v1,v2,v3)->onItemClick(v,v1,v2,v3));
        setNovel();
        getChapterList();

        findViewById(R.id.btn_add).setOnClickListener(view ->onBtnAdd(view));
        findViewById(R.id.btn_start_read).setOnClickListener(v->onBtnReadClick(v));
    }
    private  void onBtnAdd(View v){
        if(NovelBiz.instance.hasExistNovel(novel.name,novel.author_name)){
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Config.BROADCAST_ADD_NOVEL);
        intent.putExtra(Config.NOVEL_INFO, Transporter.instance.putObject(novel));
        intent.putExtra(Config.CHAPTER_LIST, Transporter.instance.putArray(allChapterInfoList));
        sendBroadcast(intent);
        showMsg("加入成功");
    }

    private  void onBtnReadClick(View v){
        if(allChapterInfoList.size()==0){
            return;
        }
        ChapterInfo info= allChapterInfoList.get(0);
        startReadActivity(info);
    }
    private void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(i<0){
            return;
        }
        ChapterInfo info=(ChapterInfo)listViewAdapter.getItem(i);
        startReadActivity(info);
    }
    private  void startReadActivity(ChapterInfo info){
        Intent intent = new Intent(NovelActivity.this, ChapterInfoActivity.class);
        novel.last_chapter_index=info.chapter_index;
        intent.putExtra(Config.NOVEL_INFO,Transporter.instance.putObject(novel));
        intent.putExtra(Config.CHAPTER_LIST,Transporter.instance.putArray(allChapterInfoList));
        intent.putExtra(Config.IS_TEMP_READ,true);
        startActivity(intent);
        this.finish();
    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    private void setNovel() {
        String str = getIntent().getExtras().getString(Config.TRANSPORT_KEY);
        novel = JSON.parseObject(str, Novel.class);
        if (novel == null) {
            return;
        }
        SetText(R.id.novel_title, novel.name);
        SetText(R.id.novel_author, novel.author_name);
        SetText(R.id.novel_type, novel.genre);
        SetText(R.id.novel_status, novel.updateStatus);
        SetText(R.id.novel_last_edit_date, novel.getLastEditDate());
        SetText(R.id.novel_new_chapter, novel.newestChapter_headline);
        SetText(R.id.novel_description, novel.description);
        SetText(R.id.novel_last_edit_date1, novel.getLastEditDate());
        ImageView imageView=(ImageView) findViewById(R.id.novel_img);
        Glide.clear(imageView);
        Glide.with(this).load(novel.image).into(imageView);
    }

    private void SetText(int id, String text) {
        ((TextView) findViewById(id)).setText(text);
    }

    private void getChapterList() {
        if (novel == null) {
            return;
        }
        NovelBiz.instance.getChapterList(novel.current_url,(list,isSuccess)->{
            if(!isSuccess){
                allChapterInfoList=new ArrayList<ChapterInfo>();
                scrollView.fullScroll(ScrollView.FOCUS_UP);
                return;
            }
            allChapterInfoList =list;
            runOnUiThread(() -> {
                NovelActivity.this.listViewAdapter.addDataList(Linq4j.asEnumerable(list).reverse().take(10).toList());
                setListViewHeightBasedOnChildren(listView);
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            });
        });

    }
}