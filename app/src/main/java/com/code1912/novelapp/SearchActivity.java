package com.code1912.novelapp;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.alibaba.fastjson.JSON;
import com.code1912.novelapp.adapter.ListAdapter;
import com.code1912.novelapp.biz.NovelBiz;
import com.code1912.novelapp.extend.PullToRefreshListView;
import com.code1912.novelapp.model.CommonResponse;
import com.code1912.novelapp.model.Novel;
import com.code1912.novelapp.utils.Util;
import com.code1912.novelapp.viewholder.SearchItemViewHolder;
import com.code1912.novelapp.utils.Config;

import okhttp3.OkHttpClient;

/**
 * Created by Code1912 on 2016/11/28.
 */

public class SearchActivity extends ActivityBase implements   SearchView.OnQueryTextListener {
    private   Menu mMenu;
    OkHttpClient mOkHttpClient = new OkHttpClient();
    CommonResponse<Novel> result;
    PullToRefreshListView pullToRefreshListView;
    ListAdapter<Novel> adapter;
    String queryText;

    @Override
    protected void onCreate(Bundle intent) {
        super.onCreate(intent);
        setContentView(R.layout.activity_serach);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        //display white color back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> SearchActivity.this.finish());

        pullToRefreshListView =(PullToRefreshListView)findViewById(R.id.search_list);
        adapter=new ListAdapter(SearchActivity.this, SearchItemViewHolder.class,R.layout.activity_search_item);
        pullToRefreshListView.setAdapter(adapter);
        pullToRefreshListView.setOnLoadListener(()->{
            search(queryText);
        });
        pullToRefreshListView.setOnItemClickListener((adapterView, view, i, l) -> {
            Novel item=  (Novel) adapter.getItem(i) ;
            Intent newIntent = new Intent(SearchActivity.this,NovelActivity.class);
            Bundle bundle=new Bundle();
            String str=JSON.toJSONString(item);
            bundle.putString(Config.TRANSPORT_KEY,str);
            newIntent.putExtras(bundle);
           // newIntent.putExtra("atd",JSON.toJSONString(item));

            startActivity(newIntent);


        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem menuItem= menu.findItem(R.id.menu_btn_search);
        menuItem.setVisible(true);
        SearchView searchView =
                (SearchView) menuItem.getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(()->{
            this.adapter.removeAll();
            return  false;
        });

        //searchView.setSubmitButtonEnabled(true);

       searchView.onActionViewExpanded();
        searchView.setQueryHint("小说go");
       // searchView.setIconifiedByDefault(false);
        //toolbar.setNavigationIcon(R.drawable.ic_ab_back_holo_dark_am);

        return true;
    }



    @Override
    protected void onNewIntent(Intent intent) {
    //    search(getIntent().getStringExtra(SearchManager.QUERY));
    }

    private  void search(String keyword){
        int pageIndex=result==null?-1:result.pageIndex+1;
        NovelBiz.instance.search(keyword,pageIndex,(result,isSuccess)->{
            this.showLoading(false);
            SearchActivity.this.setRefreshing(false);
            SearchActivity.this.result=result;
            if(!isSuccess){
                return;
            }
            runOnUiThread(()->{
                SearchActivity.this.adapter.addDataList(result.resultList);
            });
        });
    }

    private  void setRefreshing(boolean refreshing){
        runOnUiThread(()->{
           //   this.swipeRefreshLayout.setRefreshing(refreshing);
            this.pullToRefreshListView.loadComplete();
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (Util.isNullOrEmpty(query)) {
            return true;
        }
        this.result=null;
        this.queryText=query;
        this.adapter.removeAll();
        this.showLoading(true);
        this.search(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        return false;
    }

}
