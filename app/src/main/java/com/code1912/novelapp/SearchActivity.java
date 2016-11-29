package com.code1912.novelapp;

import android.os.Bundle;
import android.os.Debug;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.code1912.novelapp.adapter.SearchListAdapter;
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

public class SearchActivity extends AppCompatActivity {
    private   Menu mMenu;
    OkHttpClient mOkHttpClient = new OkHttpClient();
    CommonResponse<Novel> result;
    ListView searchList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serach);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        search("科技大仙宗");
        toolbar.setNavigationIcon(R.drawable.ic_ab_back_holo_dark_am);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchActivity.this.finish();
            }
        });

        searchList=(ListView)findViewById(R.id.search_list);

    }

    private  void search(String keyword){
        Request request = new Request.Builder()
                .url(String.format("http://www.code1912.cn:3000/search?keyword=%s&pageIndex=1&type=1",keyword))
                .build();
        Call   call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String str = response.body().string();
                Log.v("------------", str);
                result = JSON.parseObject(str, new TypeReference<CommonResponse<Novel>>(){});
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        searchList.setAdapter(new SearchListAdapter(SearchActivity.this,result.resultList));
                    }
                });

            }

            @Override
            public void onFailure(Call call, IOException e) {

            }
        });
    }
}
