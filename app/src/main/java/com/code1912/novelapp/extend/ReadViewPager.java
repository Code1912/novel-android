package com.code1912.novelapp.extend;

import android.content.Context;
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
	TextPaint paint =new TextPaint();
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
	public ReadViewPager(Context context) {
		super(context);
		this.setOnTouchListener((e,v)->onTextTouch(e,v));

	}

	public ReadViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnTouchListener((e,v)->onTextTouch(e,v));
	}

	public ReadViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.setOnTouchListener((e,v)->onTextTouch(e,v));
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

	public void setText(String text,FontSetting setting,int position) {
		text = text.replace("\\r", "\r").replace("\\t", "\t").replace("\r", "");//.replace(" "," ");
		this.position=position;
		this.pageLineCount=0;
		this. isClicked=false;
		this.  pageIndex=0;
		this.viewList=null;

		this.setting=setting;
		this.text=text;
		this.removeAllViews();
		getContentWidthHeigth();
		this.lineHeight=(int)Math.ceil(  paint.getFontMetrics(null)  ) +setting.lineSpacing;
		this.pageLineCount=this.contentHeight/lineHeight;
		paint.setTextSize(setting.fontSize);
		if (setting.color > 0) {
			paint.setColor(setting.color);
		}
		createPages(text);
	}

	private  void createPages(String text) {
		List<String> lines=convertToLines(text);
		List<List<String>> pageList=convertToPages(lines);
		ViewGroup.LayoutParams layoutParams = getLayoutParams();
		layoutParams.height = contentHeight;
		layoutParams.width = contentWidth;
		viewList = new ArrayList<>();
		for (List<String> strings : pageList) {
			ReadView view = new ReadView(getContext());
			view.post(() -> {
				view.setText(strings, paint, lineHeight);
			});
			view.setVisibility(INVISIBLE);
			viewList.add(view);
		}
		if (viewList.size() > 0) {
			if(position>-1&&position<viewList.size())
			{	viewList.get(position).setVisibility(VISIBLE);
				pageIndex=position;
			}
			else {
				viewList.get(0).setVisibility(VISIBLE);
			}
		}
		Linq4j.asEnumerable(viewList).reverse().foreach(p -> {
			this.addView(p);
			return p;
		});
	}

	private  void getContentWidthHeigth(){
		contentHeight=this.getHeight();
		contentWidth=this.getWidth();
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
			int nums = paint.breakText(str, true, with, null);
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

		//txtContent.scrollTo(0,moveY);
	}
	private  void setViewShow(int index){

		for (int i = 0; i < this.viewList.size(); i++) {
			if(i==index){
				this.viewList.get(i).setVisibility(VISIBLE);
				//this.viewList.get(i).invalidate();
			}
			else{
				this.viewList.get(i).setVisibility(INVISIBLE);
			}
		}
	}


	public static   class  FontSetting {
		public float fontSize;
		public int lineSpacing;
		public int color;

		public FontSetting() {

		}
		public FontSetting(int fontSize) {
			this.fontSize = fontSize;
		}
		public FontSetting(int fontSize, int lineSpacing) {
			this.fontSize = fontSize;
			this.lineSpacing = lineSpacing;
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

