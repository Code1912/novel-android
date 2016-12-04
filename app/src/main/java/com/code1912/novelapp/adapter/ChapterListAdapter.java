package com.code1912.novelapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.code1912.novelapp.R;
import com.code1912.novelapp.model.ChapterInfo;
import com.code1912.novelapp.viewholder.ChapterTitleHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Code1912 on 2016/12/3.
 */

public class ChapterListAdapter extends BaseAdapter {
    List<ChapterInfo> chapterInfos =new ArrayList<ChapterInfo>();
    Context context;
    LayoutInflater layoutInflater;

    public  ChapterListAdapter(Context context){
        this.context = context;
        this.layoutInflater=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return  this.chapterInfos.size();
    }

    @Override
    public Object getItem(int i) {
        return chapterInfos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void addDataList(List<ChapterInfo> dataList){
        if(dataList==null||dataList.size()==0){
            return;
        }
        this.chapterInfos.addAll(dataList);
        this.notifyDataSetChanged();
    }

    public List<ChapterInfo> getDataList(){
        return  new ArrayList<ChapterInfo>(this.chapterInfos);
    }
    public  void removeAllNovels(){
        if(chapterInfos ==null|| chapterInfos.size()==0){
            return;
        }
        this.chapterInfos.clear();
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
        holder.txtTitle.setText(this.chapterInfos.get(position).title);
        return convertView;
    }
}
