package com.code1912.novelapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.code1912.novelapp.adapter.ListAdapter;
import com.code1912.novelapp.biz.NovelBiz;
import com.code1912.novelapp.model.ChapterInfo;
import com.code1912.novelapp.model.Novel;
import com.code1912.novelapp.utils.Config;
import com.code1912.novelapp.utils.Transporter;
import com.code1912.novelapp.utils.Util;
import com.code1912.novelapp.viewholder.MainGridItemViewHolder;

import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {
    OkHttpClient mOkHttpClient = new OkHttpClient();
    GridView gridView;
    ListAdapter<Novel> novelListAdapter;
    private BroadcastReceiver mBroadcastReceiver;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gridView=(GridView) findViewById(R.id.novel_grid);
        novelListAdapter=new ListAdapter(this, MainGridItemViewHolder.class,R.layout.activity_main_item);
        gridView.setAdapter(novelListAdapter);
        novelListAdapter.addDataList(Novel.listAll(Novel.class));
        novelListAdapter.setOnItemChildClick((v,data,id)->{
            if(id==R.id.icon_trash){
                int index=novelListAdapter.getList().indexOf(data);
                novelListAdapter.removeAt(index);
                data.delete();
                ChapterInfo.deleteAll(ChapterInfo.class,String.format("novelid=%d",data.getId()));
            }
            if(id==R.id.item_mask){
                hideTrash();
            }
        });

        gridView.setOnItemLongClickListener((v,v1,position,v3)-> {
            novelListAdapter.getDataList().foreach(n -> {
                n.isShowTrash = true;
                return n;
            });
            novelListAdapter.notifyDataSetChanged();
            return true;
        });

        gridView.setOnItemClickListener((v,v1,index,id)->{
            hideTrash();
            Novel novel=  (Novel) novelListAdapter.getItem(index) ;
            Intent intent = new Intent(MainActivity.this,ChapterInfoActivity.class);
            intent.putExtra(Config.NOVEL_INFO,Transporter.instance.putObject(novel));
            intent.putExtra(Config.IS_TEMP_READ,false);
            startActivity(intent);
        });

        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark    );
        swipeRefreshLayout.setOnRefreshListener(()->{
            if(novelListAdapter.getList().size()==0){
                swipeRefreshLayout.setRefreshing(false);
                return;
            }

            novelListAdapter.getDataList().foreach(n->{
                n.refreshed=false;
                refresh(n);
                return  n;
            });
        });
        registerBroadcast();
    }
    private  void hideTrash(){
        novelListAdapter.getDataList().foreach(n -> {
            n.isShowTrash = false;
            return n;
        });
        novelListAdapter.notifyDataSetChanged();
    }
    private  void registerBroadcast() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction()==Config.BROADCAST_ADD_NOVEL) {
                    addNovel(intent);
                    return;
                }
                if (intent.getAction()==Config.BROADCAST_NOTIFY_NOVEL) {
                    notifyReadCountChanged(intent);
                    return;
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Config.BROADCAST_ADD_NOVEL);
        intentFilter.addAction(Config.BROADCAST_NOTIFY_NOVEL);
        registerReceiver(mBroadcastReceiver, intentFilter);
    }
    private  void notifyReadCountChanged(Intent intent){
        String str = intent.getExtras().getString(Config.NOVEL_INFO);
        Novel novel = JSON.parseObject(str, Novel.class);
        if (novel == null) {
            return;
        }
        Novel item= novelListAdapter.getDataList().first(n->n.getId()==novel.getId());
        if(item==null){
            return;
        }
        item.last_chapter_index=novel.last_chapter_index;
        item.read_chapter_count=novel.read_chapter_count;
        runOnUiThread(()->{
            novelListAdapter.notifyDataSetChanged();
        });
    }
    private  void addNovel(Intent intent){
        String  dataId = intent.getStringExtra(Config.NOVEL_INFO);
        Novel novel = Transporter.instance.getTransportData(dataId);
        if (novel == null) {
            return;
        }
        novel.add_date = new Date(System.currentTimeMillis());
        dataId=intent.getStringExtra(Config.CHAPTER_LIST);
        final List<ChapterInfo> chapterInfoList =Transporter.instance.getTransportData(dataId);
        novel.read_chapter_count=0;
        novel.all_chapter_count= chapterInfoList.size();

        runOnUiThread(() -> {
            Long id= Novel.save(novel);
            for (ChapterInfo chapterInfo : chapterInfoList) {
                chapterInfo.novel_id=id;
                chapterInfo.add_date= Util.getCurrentDate();
            }
            novelListAdapter.addData(0,novel);
            ChapterInfo.saveInTx(chapterInfoList);
        });
    }

    private  void refresh(Novel novel){
        NovelBiz.instance.getChapterList(novel.current_url,(list,isSuccess)->{
             if(!isSuccess){
                 novel.save();
                 novel.refreshed=true;
                 refreshUI();
                 return;
             }
            novel.is_have_new=list.size()>novel.all_chapter_count;
            if(novel.is_have_new){
                for (ChapterInfo chapterInfo : list) {
                    List<ChapterInfo> titles=  ChapterInfo.find(ChapterInfo.class," novelid=? and chapter_index=?",new String[]{String.valueOf(novel.getId()), String.valueOf(chapterInfo.chapter_index)});
                    if(titles!=null||titles.size()>0){
                        continue;
                    }
                    chapterInfo.novel_id= novel.getId();
                    chapterInfo.add_date= Util.getCurrentDate();
                    ChapterInfo.save(chapterInfo);
                }
            }
            novel.refreshed=true;
            novel.all_chapter_count=list.size();
            novel.save();
            novel.refreshed=true;
            refreshUI();
        });
    }

    private  void refreshUI(){
        if( !novelListAdapter.getDataList().all(p->p.refreshed)) {
            return;
        }
        runOnUiThread(()->{
            novelListAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
         //   Novel.saveInTx(novelListAdapter.getDataList().toList());
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.main_search) {
            hideTrash();
            Intent intent = new Intent(MainActivity.this,SearchActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}
