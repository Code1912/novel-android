package com.code1912.novelapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.code1912.novelapp.extend.LoadingView;

/**
 * Created by Code1912 on 2016/12/5.
 */

public class ActivityBase extends AppCompatActivity {
	private ImageView imageView;
	LoadingView loadingView;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void setContentView(@LayoutRes int layoutResID) {
		super.setContentView(layoutResID);
		LinearLayout.LayoutParams params=new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
		loadingView = new LoadingView(this);
		getRootView().addView(loadingView);
	}

	private ViewGroup getRootView()
	{
		return ((ViewGroup)findViewById(android.R.id.content));
	}

	protected  void showLoading(boolean isShow){
		if(isShow)
			loadingView.show();
		else
			loadingView.hide();
	}
}
