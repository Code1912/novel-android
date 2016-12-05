package com.code1912.novelapp.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.code1912.novelapp.R;

/**
 * Created by Code1912 on 2016/12/3.
 */

public abstract class ViewHolderBase<T> extends RecyclerView.ViewHolder {
	public ViewHolderBase(View itemView) {
		super(itemView);
	}

	public abstract void setViewInfo(Context context,T info);

	protected  OnItemChildClick<T> onItemChildClick;
	public  void setOnItemChildClick(OnItemChildClick<T> onItemChildClick){
		this.onItemChildClick=onItemChildClick;
	}
	protected  void  callItemChildClick(View v,T data,int id){
		if(this.onItemChildClick!=null)
			this.onItemChildClick.onClick(v,data,id);
	}
	 public interface  OnItemChildClick<T>{
		 public  void  onClick(View v,T data,int id);
	 }
}
