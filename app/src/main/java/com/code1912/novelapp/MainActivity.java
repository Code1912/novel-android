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
import com.code1912.novelapp.adapter.ListAdapter;
import com.code1912.novelapp.model.Novel;
import com.code1912.novelapp.utils.Config;
import com.code1912.novelapp.viewholder.MainGridItemViewHolder;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

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
            //swipeRefreshLayout.setRefreshing(false);
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
                String str = intent.getExtras().getString(Config.TRANSPORT_KEY);
                Novel novel = JSON.parseObject(str, Novel.class);
                if (novel == null) {
                    return;
                }
                novel.addDate= new Date(System.currentTimeMillis());
                runOnUiThread(() -> {
                    Config.BookList.add(0,novel);
                    Novel.save(novel);
                    novelListAdapter.addData(0,novel);
                });
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Config.BROADCAST_ADD_NOVEL);
        registerReceiver(mBroadcastReceiver, intentFilter);
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
