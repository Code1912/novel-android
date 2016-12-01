package com.code1912.novelapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.code1912.novelapp.R;
import com.code1912.novelapp.model.Novel;
import com.code1912.novelapp.search.SearchItemViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Code1912 on 2016/11/29.
 */

public class SearchListAdapter extends BaseAdapter  {
    List<Novel> novelList =new ArrayList<Novel>();
    Context context;
    LayoutInflater layoutInflater;
    public  SearchListAdapter(Context context){
        this.context = context;
        this.layoutInflater=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return  this.novelList.size();
    }

    @Override
    public Object getItem(int i) {
        return novelList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void addNovels(List<Novel> novels){
        if(novels==null||novels.size()==0){
            return;
        }
        this.novelList.addAll(novels);
        this.notifyDataSetChanged();
    }

    public  void removeAllNovels(){
        if(novelList==null||novelList.size()==0){
            return;
        }
        this.novelList.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        System.out.println("getView " + position + " " + convertView);
        SearchItemViewHolder holder = null;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.activity_search_item, null);
            holder = new SearchItemViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (SearchItemViewHolder)convertView.getTag();
        }
        holder.setNovelInfo(context,this.novelList.get(position));
        return convertView;
    }
}
