package com.code1912.novelapp;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.code1912.novelapp.adapter.SearchListAdapter;
import com.code1912.novelapp.extend.PullToRefreshListView;
import com.code1912.novelapp.model.CommonResponse;
import com.code1912.novelapp.model.Novel;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
/**
 * Created by Code1912 on 2016/11/28.
 */

public class SearchActivity extends AppCompatActivity implements   SearchView.OnQueryTextListener {
    private   Menu mMenu;
    OkHttpClient mOkHttpClient = new OkHttpClient();
    CommonResponse<Novel> result;
    PullToRefreshListView pullToRefreshListView;
    SearchListAdapter adapter;
    String queryText;
    @Override
    protected void onCreate(Bundle intent) {
        super.onCreate(intent);
        setContentView(R.layout.activity_serach);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //display white color back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    SearchActivity.this.finish();
            }
        });
      //  swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.search_swipeRefreshLayout);
     //   swipeRefreshLayout.setOnRefreshListener(()->{

      //  });

        pullToRefreshListView =(PullToRefreshListView)findViewById(R.id.search_list);
        adapter=new SearchListAdapter(SearchActivity.this);
        pullToRefreshListView.setAdapter(adapter);
        pullToRefreshListView.setOnLoadListener(()->{
            search(queryText);
        });
        getIntent().getStringExtra(SearchManager.QUERY);
        search(getIntent().getStringExtra(SearchManager.QUERY));
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
            this.adapter.removeAllNovels();
            return  false;
        });

        //searchView.setSubmitButtonEnabled(true);

       searchView.onActionViewExpanded();

        searchView.setQueryHint("小说go");
       // searchView.setIconifiedByDefault(false);
        //toolbar.setNavigationIcon(R.drawable.ic_ab_back_holo_dark_am);
       // toolbar.setNavigationOnClickListener(( view)->  SearchActivity.this.finish());
        return true;
    }



    @Override
    protected void onNewIntent(Intent intent) {
    //    search(getIntent().getStringExtra(SearchManager.QUERY));
    }

    private  void search(String keyword){
        int pageIndex=result==null?-1:result.pageIndex+1;
        Request request = new Request.Builder()
                .url(String.format("http://www.code1912.cn:3000/search?keyword=%s&pageIndex=%d&type=1",keyword,pageIndex))
                .build();
        Call   call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String str = response.body().string();
                Log.v("------------", str);
                result = JSON.parseObject(str, new TypeReference<CommonResponse<Novel>>(){});
                runOnUiThread(()->{
                    SearchActivity.this.adapter.addNovels(result.resultList);
                });
                SearchActivity.this.setRefreshing(false);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                SearchActivity.this.setRefreshing(false);
            }
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
        if (query == "") {
            return true;
        }
        this.adapter.removeAllNovels();
        this.search(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        return false;
    }

}
