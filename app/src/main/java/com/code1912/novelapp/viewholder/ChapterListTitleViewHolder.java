package com.code1912.novelapp.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.code1912.novelapp.R;
import com.code1912.novelapp.model.ChapterInfo;
import com.code1912.novelapp.model.Novel;

/**
 * Created by Code1912 on 2016/12/5.
 */

public class ChapterListTitleViewHolder extends ViewHolderBase<ChapterInfo> {
	public TextView txtTitle;
	public ImageView downedImg;
	public ImageView noDownedImg;
	public ImageView currentImage;
	public FrameLayout layout;
	public ChapterListTitleViewHolder(View itemView) {
		super(itemView);
		Init(itemView);
	}

	@Override
	public void setViewInfo(Context context, ChapterInfo info) {
		this.layout.setVisibility(View.VISIBLE);
		this.txtTitle.setText(info.title);
		this.currentImage.setVisibility(View.GONE);
		if (info.is_downloaded) {
			this.downedImg.setVisibility(View.VISIBLE);
			this.noDownedImg.setVisibility(View.GONE);
		} else {
			this.downedImg.setVisibility(View.GONE);
			this.noDownedImg.setVisibility(View.VISIBLE);
		}
		if (info.is_current) {
			this.downedImg.setVisibility(View.GONE);
			this.noDownedImg.setVisibility(View.GONE);
			this.currentImage.setVisibility(View.VISIBLE);
		}
	}

	private   void Init(View view){
		this.txtTitle=(TextView)view.findViewById(R.id.chapter_title);
		this.downedImg=(ImageView)view.findViewById(R.id.chapter_status_downed);
		this.noDownedImg=(ImageView)view.findViewById(R.id.chapter_status_no_down);
		this.currentImage=(ImageView)view.findViewById(R.id.chapter_status_current);
		this.layout=(FrameLayout) view.findViewById(R.id.item_img_layout);
	}
}
