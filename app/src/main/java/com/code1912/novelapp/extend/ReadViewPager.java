package com.code1912.novelapp.extend;

import android.content.Context;
import android.content.res.Configuration;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.apache.calcite.linq4j.Linq4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Code1912 on 2016/12/9.
 */

public class ReadViewPager extends FrameLayout {
	TextPaint textPaint =new TextPaint();
	int contentHeight;
	int contentWidth;
	String text;
	int lineHeight;
	FontSetting setting;
	int pageLineCount;
	boolean isClicked;
	PageAction onPageListener;
	int pageIndex=0;
	List<ReadView> viewList;
	int position=0;
	int lineSpacing=0;
	int orientation;
	public ReadViewPager(Context context) {
		super(context);
		this.setOnTouchListener((e,v)->onTextTouch(e,v));
		this.getOrientation();
	}

	public ReadViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnTouchListener((e,v)->onTextTouch(e,v));
		this.getOrientation();
	}

	public ReadViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.setOnTouchListener((e,v)->onTextTouch(e,v));
		this.getOrientation();
	}

	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Log.i("Direction Change:", "1111111111111111111");
		this.post(() -> {
			if ((newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE && this.contentHeight > this.contentWidth)
				|| (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT && this.contentHeight < this.contentWidth)) {
				int temp = this.contentHeight;
				this.contentHeight = this.contentWidth;
				this.contentWidth = temp;
			}
			Log.i("pager height:  ",String.valueOf(contentHeight));
			Log.i("pager width: ",String.valueOf(contentWidth));

			position = pageIndex;
			pageIndex = 0;
			createPages(text);
		});
	}

	private void   getOrientation(){
	   this.orientation =  this.getResources().getConfiguration().orientation;
	}
	public  void setOnPageListener(PageAction onPageListener){
		 this.onPageListener=onPageListener;
	}

	public  void executeOnPage(ActionDirection direction,int pageIndex){
		if( this.onPageListener==null){
			return;
		}
		this.onPageListener.onPage(direction,pageIndex);
	}

	public void setText(String textStr,FontSetting setting,int position) {
		this.text =  "\t\t"+textStr.replaceAll("\\r", "\r").replaceAll("\\t", "\t").replaceAll("\n","\t\t\n\t\t");
		this.text=setFontSpacing(this.text,setting.fontSpacing);
		this.position=position;
		this.pageLineCount=0;
		this. isClicked=false;
		this.  pageIndex=0;
		this.viewList=null;

		this.setting=setting;
		this.removeAllViews();
		getContentWidthHeight();


		textPaint.setTextSize(setting.fontSize);
		if (setting.color > 0) {
			textPaint.setColor(setting.color);
		}

		this.lineSpacing=setting.lineSpacing;
		this.lineHeight=(int)Math.ceil(  textPaint.getFontMetrics(null)  ) +setting.lineSpacing;
		this.pageLineCount=this.contentHeight/lineHeight;
		createPages(text);
	}

	private  String setFontSpacing(String originalText,int fontSpacing){
		if(fontSpacing==0){
			return  originalText;
		}
		String spacing="";
		for(int i = 0; i <fontSpacing; i++) {
			spacing+=" ";
		}
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < originalText.length(); i++) {
			String c =   originalText.charAt(i)+spacing;
			builder.append(c) ;
		}
		return  builder.toString();
	}
	private  void createPages(String text) {
		List<String> lines=convertToLines(text);
		List<List<String>> pageList=convertToPages(lines);
		//ViewGroup.LayoutParams layoutParams = getLayoutParams();
		//layoutParams.height = contentHeight;
		//layoutParams.width = contentWidth;
		viewList = new ArrayList<>();
		this.removeAllViews();
		for (List<String> strings : pageList) {
			ReadView view = new ReadView(getContext());
			view.post(() -> {
				view.setText(strings, textPaint, lineHeight,lineSpacing);
			});
			view.setVisibility(GONE);
			viewList.add(view);
		}
		if (viewList.size() > 0) {
			if (position > -1) {
				pageIndex =position < viewList.size()? position:(viewList.size()-1);
			}
			View view = viewList.get(pageIndex);
			view.post(() -> {
				view.setVisibility(VISIBLE);
			});
		}
		Linq4j.asEnumerable(viewList).foreach(p -> {
			this.addView(p);
			return p;
		});
	}

	private  void getContentWidthHeight(){
		contentHeight=this.getMeasuredHeight();
		contentWidth=this.getMeasuredWidth();
	}


	private List<List<String>> convertToPages(List<String> lineList){
		List<List<String>> pageLineList=new ArrayList<List<String>>();
		float tempPageHeight=0;
		List<String> tempLine=new ArrayList<>();
		String tempPage="";
		for (int i = 0; i < lineList.size(); i++) {
			String line = lineList.get(i);
			float height = lineHeight;
			if (tempPageHeight + height > contentHeight) {

				pageLineList.add(tempLine);
				tempPageHeight = 0;
				tempPage = "";
				tempLine=new ArrayList<>();
			}

			tempPageHeight = tempPageHeight + height;
			tempPage += line;
			tempLine.add(line);
			if ((i+1)==lineList.size()&&tempPageHeight<=contentHeight){
				pageLineList.add(tempLine);
			}
		}
		return  pageLineList;
	}
	private  List<String > convertToLines(String txt){
		List<String > lineList=new ArrayList<>();
		List<String> pList=	splitByP(txt);
		for (String s : pList) {
			List<String> temp= splitP2Line(s);
			if(temp.size()==0){
				continue;
			}
			lineList.addAll(temp);
		}
		return  lineList;
	}
	public List<String> splitByP(String datas) {
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
	public List<String> splitP2Line(String paragraphstr) {
		//textPaint.setSubpixelText(true);
		List<String> linesdata = new ArrayList<String>();
		String str = paragraphstr;
		int with = contentWidth;
		if(paragraphstr.trim().equals("")){
			//lineList.add(" ");
			return  linesdata;
		}
		while (str.length() > 0) {
			int count = textPaint.breakText(str, true, with, null);
			if (count <= str.length()) {
				String lineStr = str.substring(0, count);
				linesdata.add(lineStr);
				str = str.substring(count, str.length());
			} else {
				linesdata.add(str);
				str = "";
			}

		}
		return linesdata;
	}


	public boolean onTextTouch(View view, MotionEvent motionEvent) {
		if(isClicked)
		{
			return  true;
		}
		if(motionEvent.getAction()!= MotionEvent.ACTION_UP){
			return  true ;
		}
		isClicked=true;
		int height=this.getHeight();
		int centerHeight=height/5;
		int y = (int) motionEvent.getY();
		//to up
		if(y<(height-centerHeight)/2){
			toPageText(true);
		}
		//to down
		else if(y>(height+centerHeight)/2){
			toPageText(false);
		}
		else {
			executeOnPage(ActionDirection.TO_CENTER,-1);
		}
		isClicked=false;
		return  true;
	}

	private void toPageText(boolean isToUp){
		if(isToUp) {
			if (pageIndex == 0) {
				executeOnPage(ActionDirection.TO_PRE_CHAPTER,-1);
				return;
			}
			pageIndex--;
			setViewShow(pageIndex);
			executeOnPage(ActionDirection.TO_UP,pageIndex);
		}
		else {
			if(pageIndex==(viewList.size()-1)){
				executeOnPage(ActionDirection.TO_NEXT_CHAPTER,-1);
				return;
			}
			pageIndex++;
			setViewShow(pageIndex);
			executeOnPage(ActionDirection.TO_Down,pageIndex);
		}
	}

	private  void setViewShow(int index) {

		for (int i = 0; i < this.viewList.size(); i++) {
			View view = viewList.get(i);
			final int tempI = i;
			view.post(() -> {
				view.setVisibility(tempI == index ? VISIBLE : GONE);
			});
		}
	}

	public static   class  FontSetting {
		public float fontSize;
		public int lineSpacing;
		public int color;
		public int fontSpacing;

		public FontSetting() {

		}
		public FontSetting(int fontSize) {
			this.fontSize = fontSize;
		}
		public FontSetting(int fontSize, int lineSpacing) {
			this.fontSize = fontSize;
			this.lineSpacing = lineSpacing;
		}
		public FontSetting(int fontSize, int lineSpacing,int fontSpacing,int color) {
			this.fontSize = fontSize;
			this.lineSpacing = lineSpacing;
			this.fontSpacing=fontSpacing;
			this.color=color;
		}

		public FontSetting(int fontSize, int lineSpacing,int color) {
			this.fontSize = fontSize;
			this.lineSpacing = lineSpacing;
			this.color=color;
		}
	}

	public  interface PageAction{
		void onPage(ActionDirection action,int pageIndex);
	}
	public enum ActionDirection{
		TO_PRE_CHAPTER,
		TO_UP,
		TO_CENTER,
		TO_Down,
		TO_NEXT_CHAPTER,
	}

}

