package com.code1912.novelapp.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Code1912 on 2016/12/3.
 */

public abstract class ViewHolderBase<T> extends RecyclerView.ViewHolder {
	public ViewHolderBase(View itemView) {
		super(itemView);
	}

	public abstract void setViewInfo(Context context,T info);
}
