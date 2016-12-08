package com.code1912.novelapp.utils;

import android.graphics.Paint;
import android.graphics.Rect;
import android.text.SpannableStringBuilder;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.StyleSpan;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Code1912 on 2016/12/7.
 */

public class PageSplitter {
	private final int pageWidth;
	private final int pageHeight;

	private final int lineSpacingExtra;
	private final List<CharSequence> pages = new ArrayList<CharSequence>();
	private SpannableStringBuilder currentLine = new SpannableStringBuilder();
	private SpannableStringBuilder currentPage = new SpannableStringBuilder();
	private int currentLineHeight;
	private int pageContentHeight;
	private int currentLineWidth;
	private int textLineHeight;


	public PageSplitter(int pageWidth, int pageHeight , int lineSpacingExtra) {
		this.pageWidth = pageWidth;
		this.pageHeight = pageHeight;
		this.lineSpacingExtra = lineSpacingExtra;
	}

	public  void reset(){
		currentLine.clear();
		currentPage.clear();
		pages.clear();
	}

	public void append(String text, TextPaint textPaint) {
		float x=textPaint.getFontMetrics(null);
		textPaint.getFontSpacing();
		int n=getFontHeight1(textPaint);
		int n1=getFontHeight(textPaint);
		textLineHeight = (int) Math.ceil(x + lineSpacingExtra);
		String[] paragraphs = text.split("\n", -1);
		int i;
		for (i = 0; i < paragraphs.length - 1; i++) {
			appendText(paragraphs[i], textPaint);
			appendNewLine();
		}
		appendText(paragraphs[i], textPaint);
	}
	public int getFontHeight1(Paint paint)
	{
		Paint.FontMetrics fm = paint.getFontMetrics();
		return (int) Math.ceil(fm.descent - fm.top) + 2;
	}
	public int getFontHeight(TextPaint textPaint){

		Rect r = new Rect();
		char[] txt = "\n".toCharArray();
		 textPaint.getTextBounds(txt, 0, 2, r);
		return  r.height();
	}

	private void appendText(String text, TextPaint textPaint) {
		String[] words = text.split(" ", -1);
		int i;
		for (i = 0; i < words.length - 1; i++) {
			appendWord(words[i] + " ", textPaint);
		}
		appendWord(words[i], textPaint);
	}

	private void appendNewLine() {
		currentLine.append("\n");
		checkForPageEnd();
		appendLineToPage(textLineHeight);
	}

	private void checkForPageEnd() {
		if (pageContentHeight + currentLineHeight > pageHeight) {
			pages.add(currentPage);
			currentPage = new SpannableStringBuilder();
			pageContentHeight = 0;
		}
	}

	private void appendWord(String appendedText, TextPaint textPaint) {
		Rect rect = new Rect();
		int textWidth =getWordWidth(appendedText,textPaint);
		if (currentLineWidth + textWidth >= pageWidth) {
			checkForPageEnd();
			appendLineToPage(textLineHeight);
		}
		appendTextToLine(appendedText, textPaint, textWidth);
	}

	private  int getWordWidth(String str, TextPaint textPaint){
		Rect rect = new Rect();
		textPaint.getTextBounds(str,0,str.length(), rect);
		int width = rect.width ();
		return width;
	}
	private void appendLineToPage(int textLineHeight) {
		currentPage.append(currentLine);
		pageContentHeight += currentLineHeight;

		currentLine = new SpannableStringBuilder();
		currentLineHeight = textLineHeight;
		currentLineWidth = 0;
	}

	private void appendTextToLine(String appendedText, TextPaint textPaint, int textWidth) {
		currentLineHeight = Math.max(currentLineHeight, textLineHeight);
		currentLine.append(renderToSpannable(appendedText, textPaint));
		currentLineWidth += textWidth;
	}

	public List<CharSequence> getPages() {
		List<CharSequence> copyPages = new ArrayList<CharSequence>(pages);
		SpannableStringBuilder lastPage = new SpannableStringBuilder(currentPage);
		if (pageContentHeight + currentLineHeight > pageHeight) {
			copyPages.add(lastPage);
			lastPage = new SpannableStringBuilder();
		}
		lastPage.append(currentLine);
		copyPages.add(lastPage);
		return copyPages;
	}

	private SpannableString renderToSpannable(String text, TextPaint textPaint) {
		SpannableString spannable = new SpannableString(text);

		if (textPaint.isFakeBoldText()) {
			spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, spannable.length(), 0);
		}
		return spannable;
	}
}
