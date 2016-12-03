package com.code1912.novelapp.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.code1912.novelapp.R;
import com.code1912.novelapp.model.Novel;

/**
 * Created by Code1912 on 2016/12/3.
 */

public class MainGridItemViewHolder extends  ViewHolderBase<Novel> {
	public ImageView image;
	public TextView txtTitle;
	public MainGridItemViewHolder(View itemView) {
		super(itemView);
		initView(itemView);
	}

	private  void initView(View view){
		this.image=(ImageView)view.findViewById(R.id.novel_img);
		this.txtTitle=(TextView)view.findViewById(R.id.novel_title);
	}
	@Override
	public void setViewInfo(Context context, Novel info) {
		Glide.with(context).load(info.image).into(this.image);
		this.txtTitle.setText(info.name);
	}
}
