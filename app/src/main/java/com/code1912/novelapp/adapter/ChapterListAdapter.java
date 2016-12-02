package com.code1912.novelapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.code1912.novelapp.R;
import com.code1912.novelapp.model.ChapterTitle;
import com.code1912.novelapp.search.ChapterTitleHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Code1912 on 2016/12/3.
 */

public class ChapterListAdapter extends BaseAdapter {
    List<ChapterTitle> chapterTitles =new ArrayList<ChapterTitle>();
    Context context;
    LayoutInflater layoutInflater;
    public  ChapterListAdapter(Context context){
        this.context = context;
        this.layoutInflater=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return  this.chapterTitles.size();
    }

    @Override
    public Object getItem(int i) {
        return chapterTitles.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void addNovels(List<ChapterTitle> dataList){
        if(dataList==null||dataList.size()==0){
            return;
        }
        this.chapterTitles.addAll(dataList);
        this.notifyDataSetChanged();
    }

    public  void removeAllNovels(){
        if(chapterTitles ==null|| chapterTitles.size()==0){
            return;
        }
        this.chapterTitles.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //  System.out.println("getView " + position + " " + convertView);
        ChapterTitleHolder holder = null;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.activity_chapter_list_item, null);
            holder = new ChapterTitleHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ChapterTitleHolder) convertView.getTag();
        }
        holder.txtTitle.setText(this.chapterTitles.get(position).title);
        return convertView;
    }
}
