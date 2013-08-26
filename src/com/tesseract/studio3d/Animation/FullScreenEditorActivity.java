package com.tesseract.studio3d.Animation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tesseract.studio3d.R;

public class FullScreenEditorActivity extends Activity 
{
	FullScreenEditorView theView;
	int menuSettings=Menu.FIRST;
	private int group1Id = 1;
	
	String[] imageFilters = { "sepia", "stark", "sunnyside", "cool", "worn",
			"grayscale","vignette","crush","sunny","night" };
	
	RelativeLayout activityLayout;
	
	ImageButton layer1Button,layer2Button;
	ImageView layersView;
	boolean layer1Butselected=false,layer2Butselected=false;
	
	protected void onCreate(Bundle savedInstanceState) 
	{

		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		Intent i = getIntent();
		
		
		
		
		   
//	    buttonClicked=i.getExtras().getBoolean("browseButtonClicked");
//	    
		
		activityLayout = new RelativeLayout(this);
		
		RelativeLayout.LayoutParams layoutParams2=new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		
		layersView=new FullScreenEditorView(this,i.getExtras().getString("filter_fg"),i.getExtras().getString("filter_bg"));
		layersView.setLayoutParams(layoutParams2);
		activityLayout.addView(layersView);
		
//		
//		layer1Button=new ImageButton(this);
//		
//		
//		layer1Button.setImageDrawable(getResources().getDrawable(R.drawable.brush_ntpressed));
//		layer1Button.setBackgroundColor(Color.TRANSPARENT);
//		
//		RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(
//				RelativeLayout.LayoutParams.WRAP_CONTENT,
//				RelativeLayout.LayoutParams.WRAP_CONTENT);
//		
//		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//
//		layoutParams.setMargins(0, 120,20, 0);
//		layer1Button.setLayoutParams(layoutParams);
//		layer1Button.setId(998877);
//		
//		activityLayout.addView(layer1Button);
//		layer1Button.setOnClickListener(buttonClickListener);
//		
//		layer2Button=new ImageButton(this);
//		
//		layer2Button.setImageDrawable(getResources().getDrawable(R.drawable.erase_ntpressed));
//		layer2Button.setBackgroundColor(Color.TRANSPARENT);
//		layoutParams.setMargins(0, 220,20, 0);
//		layer2Button.setLayoutParams(layoutParams);
//		layer2Button.setId(998878);
//		activityLayout.addView(layer2Button);
//		
//		layer1Button.setOnClickListener(buttonClickListener);
//		
		initializeButtons();
		setContentView(activityLayout);

	}
	
	public OnClickListener buttonClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
       
			if(v.getId()==998877)
			{
				layer1Butselected=!layer1Butselected;
				
				if(layer1Butselected)
					layer1Button.setImageDrawable(getResources().getDrawable(R.drawable.brush_pressed));
				else 
					layer1Button.setImageDrawable(getResources().getDrawable(R.drawable.brush_ntpressed));
			}
			
			else if(v.getId()==998878)
			{	
				layer2Butselected=!layer2Butselected;
				
				if(layer2Butselected)
					layer2Button.setImageDrawable(getResources().getDrawable(R.drawable.eraser_pressed));
				else 
					layer2Button.setImageDrawable(getResources().getDrawable(R.drawable.erase_ntpressed));
			}
		}
		
	};
	
	public void initializeButtons()
	{
		
layer1Button=new ImageButton(this);
		
		
		layer1Button.setImageDrawable(getResources().getDrawable(R.drawable.brush_ntpressed));
		layer1Button.setBackgroundColor(Color.TRANSPARENT);
		
		RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);

		layoutParams.setMargins(0, 120,20, 0);
		layer1Button.setLayoutParams(layoutParams);
		layer1Button.setId(998877);
		
		activityLayout.addView(layer1Button);
		layer1Button.setOnClickListener(buttonClickListener);
		
		
		layoutParams=new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		
		
		layer2Button=new ImageButton(this);
		
		layer2Button.setImageDrawable(getResources().getDrawable(R.drawable.erase_ntpressed));
		layer2Button.setBackgroundColor(Color.TRANSPARENT);
		
		layoutParams.addRule(RelativeLayout.BELOW, 998877);
		layoutParams.setMargins(0, 50,0, 50);
		layer2Button.setLayoutParams(layoutParams);
		layer2Button.setId(998878);
		activityLayout.addView(layer2Button);
		
		layer2Button.setOnClickListener(buttonClickListener);
		
	}
		
	
	

}
