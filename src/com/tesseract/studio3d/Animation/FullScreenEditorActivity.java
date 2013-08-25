package com.tesseract.studio3d.Animation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public class FullScreenEditorActivity extends Activity 
{
	FullScreenEditorView theView;
	int menuSettings=Menu.FIRST;
	private int group1Id = 1;
	
	String[] imageFilters = { "sepia", "stark", "sunnyside", "cool", "worn",
			"grayscale","vignette","crush","sunny","night" };

	
	protected void onCreate(Bundle savedInstanceState) 
	{

		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		Intent i = getIntent();
		 
		   
//	    buttonClicked=i.getExtras().getBoolean("browseButtonClicked");
//	    
		setContentView(new FullScreenEditorView(this,i.getExtras().getString("filter_fg"),i.getExtras().getString("filter_bg")));

	}
	

}
