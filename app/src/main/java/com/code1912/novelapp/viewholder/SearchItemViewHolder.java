package com.code1912.novelapp.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.code1912.novelapp.R;
import com.code1912.novelapp.model.Novel;

/**
 * Created by Code1912 on 2016/11/29.
 */

public   class SearchItemViewHolder extends ViewHolderBase<Novel> {
    public SearchItemViewHolder(View itemView) {
        super(itemView);
        initView(itemView);
    }
    private   void initView(View view){
        this.image=(ImageView)view.findViewById(R.id.novel_img);
        this.txtTitle=(TextView)view.findViewById(R.id.novel_title);
        this.txtDescription=(TextView)view.findViewById(R.id.novel_description);
        this.txtAuthor=(TextView)view.findViewById(R.id.novel_author);
        this.txtStatus=(TextView)view.findViewById(R.id.novel_status);
        this.txtType=(TextView)view.findViewById(R.id.novel_type);
        this.txtLastEditDate=(TextView)view.findViewById(R.id.novel_last_edit_date);
    }

    public ImageView image;
    public TextView txtTitle;
    public TextView txtDescription;
    public  TextView txtAuthor;
    public  TextView txtStatus;
    public  TextView txtType;
    public  TextView txtLastEditDate;
    public  void setViewInfo(Context context, Novel novel){
        Glide.clear(image);
        Glide.with(context).load(novel.image).into(this.image);
        this.txtTitle.setText(novel.name);
        this.txtDescription.setText(novel.description);
        this.txtStatus.setText(novel.updateStatus);
        this.txtType.setText(novel.genre);
        this.txtAuthor.setText(novel.author_name);
        this.txtLastEditDate.setText(novel.getLastEditDate());
    }

}
