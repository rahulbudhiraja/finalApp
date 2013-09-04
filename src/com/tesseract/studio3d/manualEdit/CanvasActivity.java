package com.tesseract.studio3d.manualEdit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.tesseract.studio3d.R;

public class CanvasActivity extends Activity {
    /** Called when the activity is first created. */
    
	RelativeLayout activityLayout;
	

	
	ImageButton layer1Button,layer2Button;
	ImageView layersView;
	boolean layer1Butselected=false,layer2Butselected=false;
	ImageButton saveButton;
	
	static boolean activateViewMovement=true;
	static int activeLayer=0;
	
	Panel SurfacePaintView;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
     
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		
		activityLayout = new RelativeLayout(this);
		
		RelativeLayout.LayoutParams layoutParams2=new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
	
		Intent i = getIntent();
		
		SurfacePaintView=new Panel(this,i.getExtras().getString("filter_fg"),i.getExtras().getString("filter_bg"));
		
		activityLayout.addView(SurfacePaintView, layoutParams2);
		
		initializeButtons();
		
        setContentView(activityLayout);
    }
	
	public void onPause(){
	    super.onPause();
	   CanvasThread.setRunning(false);
	    finish();
	}
    
	public void onResume()
	{
		super.onResume();
		  
		CanvasThread.setRunning(true); 
		
	}
    
	public void initializeButtons()
	{
		
		layer1Button=new ImageButton(this);
		
		layer1Button.setImageDrawable(getResources().getDrawable(R.drawable.brush_ntpressed_2));
		layer1Button.setBackgroundColor(Color.TRANSPARENT);
		
		RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);

		layoutParams.setMargins(0, 40,20, 0);
		layer1Button.setLayoutParams(layoutParams);
		layer1Button.setId(998877);
		
		activityLayout.addView(layer1Button);
		layer1Button.setOnClickListener(buttonClickListener);
		
		
		layoutParams=new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		
		
		layer2Button=new ImageButton(this);
		
		layer2Button.setImageDrawable(getResources().getDrawable(R.drawable.eraser_ntpressed));
		layer2Button.setBackgroundColor(Color.TRANSPARENT);
		
		layoutParams.addRule(RelativeLayout.BELOW, 998877);
		layoutParams.setMargins(0, 40,20, 50);
		layer2Button.setLayoutParams(layoutParams);
		layer2Button.setId(998878);
		activityLayout.addView(layer2Button);
		
		layer2Button.setOnClickListener(buttonClickListener);
		
		layoutParams=new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		
		saveButton =new ImageButton(this);
		saveButton.setImageDrawable(getResources().getDrawable(R.drawable.save_icon));
		saveButton.setBackgroundColor(Color.TRANSPARENT);
		 
		layoutParams.addRule(RelativeLayout.BELOW, 998878);
		layoutParams.setMargins(0, 30,20, 0);
		saveButton.setLayoutParams(layoutParams);
		saveButton.setId(998879);
		
		activityLayout.addView(saveButton);
		
		
	}
	public OnClickListener buttonClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
       
			if(v.getId()==998877)
			{
				layer1Butselected=!layer1Butselected;
				
				if(layer1Butselected)
					{    layer1Button.setImageDrawable(getResources().getDrawable(R.drawable.brush_pressed_2));
					     layer2Butselected=false;
					     layer2Button.setImageDrawable(getResources().getDrawable(R.drawable.eraser_ntpressed));
					     activeLayer=1;
					   
					     activateViewMovement=false;
					}
				else 
					{
						 layer1Button.setImageDrawable(getResources().getDrawable(R.drawable.brush_ntpressed_2));
					     
					     activateViewMovement=true;
					     activeLayer=0;
					}
			}
			
			else if(v.getId()==998878)
			{	
				layer2Butselected=!layer2Butselected;
				
				if(layer2Butselected)
					{
						layer2Button.setImageDrawable(getResources().getDrawable(R.drawable.eraser_pressed));
						layer1Butselected=false;
						layer1Button.setImageDrawable(getResources().getDrawable(R.drawable.brush_ntpressed_2));
						activeLayer=2;	
					
						
						activateViewMovement=false;
					}
				else 
					{
						layer2Button.setImageDrawable(getResources().getDrawable(R.drawable.eraser_ntpressed));
					
						activateViewMovement=true;
						activeLayer=0;
					}
			}
		}
		
	};
	
   

}