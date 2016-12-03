package com.code1912.novelapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.code1912.novelapp.adapter.ListAdapter;
import com.code1912.novelapp.model.ChapterTitle;
import com.code1912.novelapp.model.CommonResponse;
import com.code1912.novelapp.model.Novel;
import com.code1912.novelapp.utils.Config;
import com.code1912.novelapp.viewholder.MainGridItemViewHolder;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
        novelListAdapter.addDataList(Config.BookList);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark    );
        swipeRefreshLayout.setOnRefreshListener(()->{
            if(Config.BookList.size()==0){
                swipeRefreshLayout.setRefreshing(false);
                return;
            }

            novelListAdapter.getDataList().foreach(n->{
                n.refreshed=false;
                Refresh(n);
                return  n;
            });
        });
        registerBroadcast();
    }

    private  void registerBroadcast() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String key = intent.getStringExtra(Config.KEY);
                Log.i("key",key);
                Log.i("Config.ADD_NOVEL_KEY",Config.ADD_NOVEL_KEY);
                if (!Config.ADD_NOVEL_KEY.equals(key)) {
                    return;
                }
                String str = intent.getExtras().getString(Config.NOVEL_INFO);
                Novel novel = JSON.parseObject(str, Novel.class);
                novel.current_url=novel.listPage_url[0];
                if (novel == null) {
                    return;
                }
                novel.add_date = new Date(System.currentTimeMillis());


                str=intent.getExtras().getString(Config.CHAPTER_LIST);
                final List<ChapterTitle>  chapterTitleList=JSON.parseObject(str, new TypeReference<List<ChapterTitle>>(){});

                novel.read_chapter_count=0;
                novel.all_chapter_count=chapterTitleList.size();

                runOnUiThread(() -> {
		    Config.BookList.add(0,novel);
		    Long id= Novel.save(novel);
                    for (ChapterTitle chapterTitle : chapterTitleList) {
                        chapterTitle.novel_id=id;
                    }
                    novelListAdapter.addData(0,novel);
                    ChapterTitle.saveInTx(chapterTitleList);
		});
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Config.BROADCAST_ADD_NOVEL);
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private  void Refresh(Novel novel){
        Request request = new Request.Builder()
                .url(Config.getChapterListUrl(novel.current_url))
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String str = response.body().string();
                CommonResponse<ChapterTitle>     result = JSON.parseObject(str, new TypeReference<CommonResponse<ChapterTitle>>() {
                });
                novel.is_have_new=novel.all_chapter_count<result.resultList.size();
                novel.all_chapter_count=result.resultList.size();
                if(novel.is_have_new){
                    for (ChapterTitle chapterTitle : result.resultList) {
                        List<ChapterTitle> titles=  ChapterTitle.find(ChapterTitle.class," novelid=? and title=?",new String[]{String.valueOf(novel.getId()),chapterTitle.title});
                        if(titles!=null||titles.size()>0){
                            continue;
                        }
                        chapterTitle.novel_id= novel.getId();
                        ChapterTitle.save(chapterTitle);
                    }
                }
                Novel.save(novel);
                novel.refreshed=true;
                RefreshUI();
            }

            @Override
            public void onFailure(Call call, IOException e) {
                novel.refreshed=true;
                RefreshUI();
            }
        });
    }

    private  void RefreshUI(){
        if( !novelListAdapter.getDataList().all(p->p.refreshed)) {
            return;
        }
        runOnUiThread(()->{
            novelListAdapter.notifyDataSetChanged();

            swipeRefreshLayout.setRefreshing(false);
            Novel.saveInTx(novelListAdapter.getDataList().toList());
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
