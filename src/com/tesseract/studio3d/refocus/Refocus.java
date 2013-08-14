package com.tesseract.studio3d.refocus;

import com.tesseract.studio3d.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

public class Refocus extends Activity
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