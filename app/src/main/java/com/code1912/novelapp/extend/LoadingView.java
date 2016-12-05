package com.code1912.novelapp.extend;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.code1912.novelapp.ActivityBase;
import com.code1912.novelapp.R;

/**
 * Created by Code1912 on 2016/12/5.
 */

public class LoadingView extends LinearLayout {
	private ImageView imageView;
	private  boolean isShow=false;
	public LoadingView(Context context) {
		super(context);
		init(context);
	}

	public LoadingView(Context context, AttributeSet attrs) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater.from(context).inflate(R.layout.view_loading, this);
		imageView = (ImageView) findViewById(R.id.loading_img);
		if (context != null) {
			Glide.with(context)
				.load(R.drawable.loading).asGif().into(this.imageView);

		}
		this.bringToFront();
		this.setOnTouchListener((v,e)->{
			return  isShow;
		});
	       hide();
	}

	public void show() {
		this.setVisibility(VISIBLE);
		this.isShow=true;
	}

	public void hide() {
		this.setVisibility(GONE);
		this.isShow=false;
	}
}
