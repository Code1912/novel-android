package com.code1912.novelapp.model;

import android.graphics.Color;

import com.code1912.novelapp.extend.ReadViewPager;
import com.orm.SugarRecord;

/**
 * Created by Code1912 on 2016/12/12.
 */

public class Setting extends SugarRecord {
	public  static  Setting instance;
	public  float fontSize;
	public  int fontSpacing;
	public  int fontColor;
	public int lineSpacing;
	static {
		instance=new Setting();
		instance.fontSize=60;
		instance.fontColor= Color.BLACK;
		instance.lineSpacing=30;
		instance.fontSpacing=0;
	}

	public ReadViewPager.FontSetting getFontSetting() {
		ReadViewPager.FontSetting setting = new ReadViewPager.FontSetting();
		setting.fontSize = instance.fontSize;
		setting.color = instance.fontColor;
		setting.lineSpacing = instance.lineSpacing;
		setting.fontSpacing = instance.fontSpacing;
		return setting;
	}
}
