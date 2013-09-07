package com.tesseract.studio3d.refocus;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.tesseract.studio3d.menu.MenuActivity;

public class Refocus extends MenuActivity
{
	ImageView placeholderImage;
	ImageButton startFocusActivity,startPhotoActivity,startReplaceActivity;

	protected void onCreate(Bundle savedInstanceState) 
	{

		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(new FocusImageView(this));
		
	}
	
	



}