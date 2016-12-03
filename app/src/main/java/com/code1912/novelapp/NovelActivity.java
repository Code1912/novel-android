package com.code1912.novelapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bumptech.glide.Glide;
import com.code1912.novelapp.adapter.ChapterListAdapter;
import com.code1912.novelapp.model.ChapterTitle;
import com.code1912.novelapp.model.CommonResponse;
import com.code1912.novelapp.model.Novel;
import com.code1912.novelapp.utils.Config;

import org.apache.calcite.linq4j.Linq4j;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Code1912 on 2016/12/1.
 */

public class NovelActivity extends AppCompatActivity {
    Novel novel;
    ListView listView;
    ChapterListAdapter listViewAdapter;
    OkHttpClient mOkHttpClient = new OkHttpClient();
    CommonResponse<ChapterTitle> result;
    ScrollView scrollView;

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

        setNovel();
        getChapterList();

        findViewById(R.id.btn_add).setOnClickListener(view -> {
            if(Config.getNovelListLinq().any(n->n.name.equals(novel.name)&&n.author_name.equals(novel.author_name))){
                return;
            }
            Intent intent = new Intent();
            intent.putExtra(Config.KEY,Config.ADD_NOVEL_KEY);
            intent.setAction(Config.BROADCAST_ADD_NOVEL);
            Bundle bundle = new Bundle();
            String str = JSON.toJSONString(novel);
            bundle.putString(Config.TRANSPORT_KEY, str);
            intent.putExtras(bundle);
            sendBroadcast(intent);
        });
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
        Glide.with(this).load(novel.image).into((ImageView) findViewById(R.id.novel_img));
    }

    private void SetText(int id, String text) {
        ((TextView) findViewById(id)).setText(text);
    }

    private void getChapterList() {
        if (novel == null) {
            return;
        }
        Request request = new Request.Builder()
                .url(Config.getChapterListUrl(novel.listPage_url[0]))
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String str = response.body().string();
                result = JSON.parseObject(str, new TypeReference<CommonResponse<ChapterTitle>>() {
                });
                runOnUiThread(() -> {
                    NovelActivity.this.listViewAdapter.addNovels(Linq4j.asEnumerable(result.resultList).reverse().take(15).toList());
                    setListViewHeightBasedOnChildren(listView);
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {

            }
        });
    }
}