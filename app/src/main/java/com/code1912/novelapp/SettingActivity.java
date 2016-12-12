package com.code1912.novelapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.code1912.novelapp.utils.Config;
import com.mikepenz.iconics.context.IconicsLayoutInflater;

import org.apache.calcite.linq4j.Linq4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Code1912 on 2016/12/12.
 */

public class SettingActivity extends AppCompatActivity{
	ImageView imgBg;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {

         	 byte[] buffer=	 getImg();
		LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		imgBg=(ImageView) findViewById(R.id.img_bg);
		Glide.with(this).load(buffer).into(imgBg);

	}

	private byte[] getImg(){
		List<Byte> list=new ArrayList<>();
		try {
			String filePath= getIntent().getStringExtra(Config.CHAPTER_INFO_SCREENSHOT);

			FileInputStream fin= this.openFileInput(filePath);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			fin.close();
			for (byte b : buffer) {
				list.add(b);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		if(list.size()>0){
			byte[] buffer=new byte[list.size()];
			for (int i = 0; i < list.size(); i++) {
				buffer[i]=list.get(i);
			}
			return  buffer;
		}
		return  null;
	}
}
