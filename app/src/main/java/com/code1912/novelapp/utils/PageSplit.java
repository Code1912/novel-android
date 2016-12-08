package com.code1912.novelapp.utils;

import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Code1912 on 2016/12/8.
 */

public class PageSplit {
	List<String> lineList=new ArrayList<>();
	List<String> pageList=new ArrayList<>();
       public  List<List<String>> pageLineList=new ArrayList<List<String>>();
	int lineSpacing;

	TextPaint  textPaint;
	int containerHeight;
	int containerWidth;

	public  PageSplit(int containerWidth, int containerHeight,int lineSpacing) {


		this.lineSpacing = lineSpacing;
		this.containerHeight = containerHeight;
		this.containerWidth =containerWidth;

		convertToPages();
	}
	public  void appendText(String text,TextPaint textPaint){
		text = text.replace("\\r", "\r").replace("\\t", "\t").replace("\r", "");
		this.textPaint = textPaint;
		convertToLines(text);
		convertToPages();
	}

	public int getLineHeight1(String word){
	    return  (int)Math.ceil(  textPaint.getFontMetrics(null)  );
	}
	public float getLineHeight2(String word)
	{
		Paint.FontMetrics fm = textPaint.getFontMetrics();
		return   (float) (Math.ceil(fm.descent - fm.ascent) + 2+10) ;
	}
	public int getLineHeight(String word){
		return  (int)Math.ceil(  textPaint.getFontMetrics(null)  )+20;
	}
	private  int getWordWidth(String str){
		Rect rect = new Rect();
		textPaint.getTextBounds(str,0,str.length(), rect);
		int width = rect.width ();
		return width;
	}

	public List<String> getPageList(){
		return  this.pageList;
	}
	private void convertToPages(){
		float tempPageHeight=0;
		List<String> tempLine=new ArrayList<>();

		String tempPage="";
		for (int i = 0; i < lineList.size(); i++) {
			String line = lineList.get(i);
			float height = getLineHeight(line);
			if (tempPageHeight + height > containerHeight) {
				pageList.add(tempPage);
				pageLineList.add(tempLine);
				tempPageHeight = 0;
				tempPage = "";
				tempLine=new ArrayList<>();
			}

		       tempPageHeight = tempPageHeight + height;
			tempPage += line;
			tempLine.add(line);
			if ((i+1)==lineList.size()&&tempPageHeight<=containerHeight){
				pageList.add(tempPage);
				pageLineList.add(tempLine);
			}
		}
	}

	private  void convertToLines(String txt){
	       List<String> pList=	SeperateByParagph(txt);
		for (String s : pList) {
			List<String> temp= SeparateParagraphtoLines(s);
			if(temp.size()==0){
				continue;
			}
			lineList.addAll(temp);
		}
	}
	public List<String> SeperateByParagph(String datas) {

		List<String> paragphdatas = new ArrayList<String>();

		if (datas != null) {

			String[] ps = datas.split("\n");
			for (int i = 0; i < ps.length; i++) {

				ps[i] = ps[i] + "  ";

				paragphdatas.add(ps[i]);

			}

			if (paragphdatas.size() > 0) {

				String lastp = paragphdatas.get(paragphdatas.size() - 1).substring(0,
					paragphdatas.get(paragphdatas.size() - 1).length() - 1);
				paragphdatas.remove(paragphdatas.size() - 1);
				paragphdatas.add(lastp);
			}
		} else {

			paragphdatas.add(datas);
		}
		return paragphdatas;

	}
	public List<String> SeparateParagraphtoLines(String paragraphstr) {


		//textPaint.setSubpixelText(true);
		List<String> linesdata = new ArrayList<String>();
		String str = paragraphstr;
		int with = containerWidth;
		if(paragraphstr.trim().equals("")){
			//lineList.add(" ");
			return  linesdata;
		}
		while (str.length() > 0) {
			int nums = textPaint.breakText(str, true, with, null);
			if (nums <= str.length()) {
				String linnstr = str.substring(0, nums);
				linesdata.add(linnstr);
				str = str.substring(nums, str.length());
			} else {
				linesdata.add(str);
				str = "";
			}

		}
		return linesdata;
	}
}
