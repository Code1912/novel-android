package com.code1912.novelapp.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.code1912.novelapp.R;
import com.code1912.novelapp.extend.SuperBitmapImageViewTarget;
import com.code1912.novelapp.model.Novel;

/**
 * Created by Code1912 on 2016/12/3.
 */

public class MainGridItemViewHolder extends  ViewHolderBase<Novel> {
	public ImageView image;
	public ImageView ishasNewImg;
	public TextView txtTitle;
	public TextView txtRead;
	public TextView txtAll;
	public MainGridItemViewHolder(View itemView) {
		super(itemView);
		initView(itemView);
	}

	private  void initView(View view){
		this.image=(ImageView)view.findViewById(R.id.novel_img);
		this.txtTitle=(TextView)view.findViewById(R.id.novel_title);
		this.txtRead=(TextView)view.findViewById(R.id.novel_read_count);
		this.txtAll=(TextView)view.findViewById(R.id.novel_all_chapter_count);
		this.ishasNewImg=(ImageView)view.findViewById(R.id.icon_has_new);
	}
	@Override
	public void setViewInfo(Context context, Novel info) {
		Glide.with(context)
			.load(info.image)
			.centerCrop()
			.crossFade()
			.diskCacheStrategy(DiskCacheStrategy.SOURCE)
			.placeholder(R.mipmap.ic_launcher).into(this.image);
		this.txtTitle.setText(info.name);
		this.txtRead.setText(String.valueOf(info.read_chapter_count));
		this.txtAll.setText(String.valueOf(info.all_chapter_count));
		this.ishasNewImg.setVisibility(info.is_have_new ? View.VISIBLE : View.INVISIBLE);
	}
}
