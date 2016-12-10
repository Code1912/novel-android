package com.code1912.novelapp.extend;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.code1912.novelapp.utils.Util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Code1912 on 2016/12/6.
 */

public class ReadView extends View {


	TextPaint textPaint=new TextPaint();
	List<String> text= new ArrayList<>();
	int lineHeight=0;
	float y=0;

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
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(text==null||text.size()==0){
			return;
		}
		 y=50;
		for (String s : text) {
			canvas.drawText(s, 0, y, textPaint);
			lineHeight = getLineHeight(s);
			y += lineHeight;
		}
	}
	public int getLineHeight(String word){
		return  (int)Math.ceil(  textPaint.getFontMetrics(null)  )+20;
	}




	public    void setText(List<String> text, TextPaint textPaint,int lineHeight){
		y=0;
		this.textPaint=textPaint;
		this.text=text;
		this.lineHeight=lineHeight;
		textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		invalidate();
	}

}
