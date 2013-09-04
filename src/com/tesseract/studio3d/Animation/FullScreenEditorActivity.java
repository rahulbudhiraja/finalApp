package com.tesseract.studio3d.Animation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
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
	ImageButton saveButton;
	static Bitmap actualBitmap;
	
	static int activeLayer=0;
	OverlayTouchCanvas tCanvas1,tCanvas2;
	int layerId=0;
	
	MultiTouchControllerView mTouchView;
	static boolean activateViewMovement=true;
	
	protected void onCreate(Bundle savedInstanceState) 
	{

		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		Intent i = getIntent();
		
		System.gc();
		   
//	    buttonClicked=i.getExtras().getBoolean("browseButtonClicked");
//	    
		
		activityLayout = new RelativeLayout(this);
		
		RelativeLayout.LayoutParams layoutParams2=new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		
		
		//layersView=new FullScreenEditorView(this,i.getExtras().getString("filter_fg"),i.getExtras().getString("filter_bg"));
		//layersView.setLayoutParams(layoutParams2);
		
		ImageView backgroundView=new ImageView(this);
		actualBitmap=BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()+"/Studio3D/img_left.jpg");
		
		//actualBitmap=Bitmap.createBitmap(960,540,Bitmap.Config.ARGB_8888);
		
//		Canvas actualBitmapCanvas=new Canvas(actualBitmap);
//		actualBitmapCanvas.drawBitmap(((BitmapDrawable)AnimationActivity.CanvasImageViews.get(0).getDrawable()).getBitmap(),0,0,null);
//		actualBitmapCanvas.drawBitmap(((BitmapDrawable)AnimationActivity.CanvasImageViews.get(1).getDrawable()).getBitmap(),0,0,null);
//		actualBitmapCanvas.save();
		
		backgroundView.setImageBitmap(actualBitmap);
		backgroundView.setLayoutParams(layoutParams2);
		

//		activityLayout.addView(backgroundView);
		
//		activityLayout.addView(layersView);
		
		 
		
//		tCanvas1=new OverlayTouchCanvas(this,actualBitmap,i.getExtras().getString("filter_fg"),1);
//		tCanvas1.setLayoutParams(layoutParams2);
//		activityLayout.addView(tCanvas1);
//		
//		tCanvas2=new OverlayTouchCanvas(this,actualBitmap,i.getExtras().getString("filter_bg"),2);
//		tCanvas2.setLayoutParams(layoutParams2);
//		activityLayout.addView(tCanvas2);
		
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
		//
		mTouchView=new MultiTouchControllerView(this,actualBitmap);
		mTouchView.setLayoutParams(layoutParams2);
		activityLayout.addView(mTouchView);
	
		initializeButtons();
		
		setContentView(activityLayout);

	}
	
	protected void onResume() {
		super.onResume();
		//mTouchView.loadImages(this);
        mTouchView.loadImages(this,false);
		//mTouchView.setBitmap(actualBitmap);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		//mTouchView.unloadImages();
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
					     tCanvas1.activate=true;
					     tCanvas2.activate=false;
					     activateViewMovement=false;
					}
				else 
					{
						 layer1Button.setImageDrawable(getResources().getDrawable(R.drawable.brush_ntpressed_2));
					     tCanvas1.activate=false;
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
						tCanvas2.activate=true;
						tCanvas1.activate=false;
						
						activateViewMovement=false;
					}
				else 
					{
						layer2Button.setImageDrawable(getResources().getDrawable(R.drawable.eraser_ntpressed));
						tCanvas2.activate=false;
						activateViewMovement=true;
						activeLayer=0;
					}
			}
		}
		
	};
	
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
	
	public boolean onTouchEvent(MotionEvent event) {
	 	   // TODO Auto-generated method stub
	 	   
	 	Log.d("X = "+event.getX(),"Y = "+event.getY());
	 	
	 	//Log.d(TAG,"Timings -- 1:"+System.currentTimeMillis());
	 	//Log.d(TAG,"Layers ::: "+layerID);
	 	
	 	switch (event.getAction())
	 	{
	 	
	 	case MotionEvent.ACTION_DOWN:
	 	case MotionEvent.ACTION_MOVE:
	 	case MotionEvent.ACTION_POINTER_DOWN:
	 //		tCanvas1.handleTouch(event.getX(),event.getY());
	 	//	tCanvas2.handleTouch(event.getX(),event.getY());
	 
	 	}
	 return true;	
	}
		
	
	

}
