package com.code1912.novelapp.viewholder;

/**
 * Created by Code1912 on 2016/12/3.
 */
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.code1912.novelapp.R;

public class ChapterTitleHolder extends RecyclerView.ViewHolder {
    public TextView txtTitle;
    public ChapterTitleHolder(View itemView) {
        super(itemView);
        Init(itemView);
    }
    private   void Init(View view){
        this.txtTitle=(TextView)view.findViewById(R.id.chapter_title);
    }
}
