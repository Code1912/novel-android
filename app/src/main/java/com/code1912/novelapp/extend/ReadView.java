package com.code1912.novelapp.extend;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.CharBuffer;

/**
 * Created by Code1912 on 2016/12/6.
 */

public class ReadView extends TextView {

	CharBuffer buffer = CharBuffer.allocate(20000);
	int position;
	public ReadView(Context context) {
		super(context);
	}

	public ReadView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ReadView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		resize();
	}

	/**
	 * 去除当前页无法显示的字
	 * @return 去掉的字数
	 */
	public int resize() {
		CharSequence oldContent = getText();
		CharSequence newContent = oldContent.subSequence(0, getCharNum());
		setText(newContent);
		return oldContent.length() - newContent.length();
	}

	/**
	 * 获取当前页总字数
	 */
	public int getCharNum() {
		return getLayout().getLineEnd(getLineNum());
	}

	/**
	 * 获取当前页总行数
	 */
	public int getLineNum() {
		Layout layout = getLayout();
		int topOfLastLine = getHeight() - getPaddingTop() - getPaddingBottom() - getLineHeight();
		return layout.getLineForVertical(topOfLastLine);
	}

	public void loadText(String str) {
		position=0;
		buffer.clear();
		buffer.wrap(str);
	}

	/**
	 * 从指定位置开始载入一页
	 */
	private void loadPage(int position) {
		buffer.position(position);
		this.setText(buffer);
	}

	/**
	 * 上一页按钮
	 */
	public void previewPageBtn(View view) {

	}

	/**
	 * 下一页按钮
	 */
	public void nextPageBtn(View view) {
		position += this.getCharNum();
		loadPage(position);
		this.resize();
	}


}
