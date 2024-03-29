package com.tesseract.studio3d.Animation;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Vector;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.tesseract.studio3d.CustomFileObserver;
import com.tesseract.studio3d.R;
import com.tesseract.studio3d.NewImageFilters.InterfaceClass;
import com.tesseract.studio3d.manualEdit.CanvasActivity;
import com.tesseract.studio3d.menu.MenuActivity;
import com.tesseract.studio3d.selectionscreen.MainScreen;
import com.tesseract.studio3d.social.SocialSharing;

public class AnimationActivity extends MenuActivity {

	Vector<ImageView> mimageViews;
	Vector<ImageView> layerViews;
	 static Vector<ImageView> CanvasImageViews;
	Vector<AnimationSet> mAnimations;
	static Vector<Bitmap> layerBitmaps;
	Vector<int[]> sizes;
	RelativeLayout activityLayout,fullScreenLayout;
	File seperatedLayersFolder;
	String TAG = "AnimationActivity";
	
	int currentSelectedLayer = 0;
	private Animation animFadeIn;
	Paint canvasPaint;

	// This has the different layers ,Each layer has an arraylist of different
	// images ..
	String[] imageFilters = { "none", "Retro", "Breeze","MonoTint","Glorify","Pensive" };
	

	//Vector<ArrayList<ImageToLoad>> Layers;
	LinearLayout layersLayout, filtersLayout;

	ScrollView layersScroll;

	private static HorizontalScrollView hs;

	Vector<Vector<ImageView>> filteredViews;
	boolean isStarted=false;


	double scaleValue=0.16;

	ImageButton next,focus,Replace,reset,full_screen,back,accept;
	CustomFileObserver fileObserver;
	
	Mat img1,img2;
	
	static int currentMode=0;
	
	boolean focusButtonClicked=false;
	float converted_xcoord,converted_ycoord;

	 private Mat mRgba;
		
	 public Mat disp;
	 public Mat finalImage;
	 public Mat limg;	
	 public Mat foreground,background;
	 
	public static Vector<Integer> selectedFilters; 
	 
	 Context activityContext;
	 
	 int menuSettings=Menu.FIRST;
		private int group1Id = 1;
		
		
	InterfaceClass newFiltersInterface;

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		activityContext=this;
		
		activityLayout = new RelativeLayout(this);
		activityLayout.setBackgroundColor(Color.BLACK);
		activityLayout.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.animationactivity));

		
		fullScreenLayout = new RelativeLayout(this);
		
		mimageViews = new Vector<ImageView>();

		CanvasImageViews = new Vector<ImageView>();
		layerBitmaps = new Vector<Bitmap>();

		sizes = new Vector<int[]>();

		filteredViews = new Vector<Vector<ImageView>>();


		// Create that many ImageViews as much as there are in the
		// Tesseract/Layers
		seperatedLayersFolder = new File(
				Environment.getExternalStorageDirectory()
				+ "/Studio3D/Layers/");

		System.gc();
		selectedFilters=new Vector<Integer>();
		
		LoadFiles(seperatedLayersFolder);

		hs = new HorizontalScrollView(this);
		
		
		canvasPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		canvasPaint.setStyle(Paint.Style.STROKE);
		canvasPaint.setStrokeWidth(5);
		canvasPaint.setColor(Color.RED);
		canvasPaint.setAntiAlias(true);

		// initializeHorizontalScroller();
		initializenewHorizontallScrollView();
		animFadeIn=AnimationUtils.loadAnimation(this, R.anim.anim_fade_in);

		initializeVerticalScroller();
		
		initializeMats();
		
		logHeap();
		
		newFiltersInterface=new InterfaceClass();
		
		// setContentView(R.layout.timepasslayout);
		setContentView(activityLayout);

	}
	
	public static void logHeap() {
        Double allocated = new Double(Debug.getNativeHeapAllocatedSize())/new Double((1048576));
        Double available = new Double(Debug.getNativeHeapSize())/1048576.0;
        Double free = new Double(Debug.getNativeHeapFreeSize())/1048576.0;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);

        Log.d("tag", "debug ==");
        Log.d("tag", "debug.heap native: allocated " + df.format(allocated) + "MB of " + df.format(available) + "MB (" + df.format(free) + "MB free)");
        Log.d("tag", "debug.memory: allocated: " + df.format(new Double(Runtime.getRuntime().totalMemory()/1048576)) + "MB of " + df.format(new Double(Runtime.getRuntime().maxMemory()/1048576))+ "MB (" + df.format(new Double(Runtime.getRuntime().freeMemory()/1048576)) +"MB free)");
    }
	 static {
		    if (!OpenCVLoader.initDebug()) {
		        // Handle initialization error
		    	Log.d("error","error");
		    	}
		    	else  System.loadLibrary("depth_magic");
			    
		    
		}
	protected void onRestart() {
	    super.onRestart();  // Always call the superclass method first
	    
	    // Activity being restarted from stopped state    
	}
	
	protected void onStart() {
	    super.onStart();  // Always call the superclass method first
	}

	private void initializenewHorizontallScrollView() {

		filtersLayout = new LinearLayout(this);

		LinearLayout.LayoutParams llayoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);

		// llayoutParams.setMargins(150,300,0,0);

		filtersLayout.setLayoutParams(llayoutParams);

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		// layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
		layoutParams.setMargins(183, 0, 0, 0);

		// scroller.setLayoutParams(layoutParams);

		hs.setLayoutParams(layoutParams);

		File filtersFolder = new File(Environment.getExternalStorageDirectory()
				+ "/Studio3D/Layers/Filters/");
		File[] files = filtersFolder.listFiles();

		Log.d(TAG, "Number of filtered folders :" + files.length);
		// int count=0;


		LinearLayout.LayoutParams imgViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		imgViewParams.setMargins(5, 0, 0, 0);

		for (int count = 0; count < files.length; count++) {
			Vector<ImageView> images = new Vector<ImageView>();

			File layerFolder = new File(
					Environment.getExternalStorageDirectory()
					+ "/Studio3D/Layers/Filters/"
					+ String.valueOf(count) + "/");
			Log.d(TAG, "path" + layerFolder.getAbsolutePath());


			File[] layerFiles = layerFolder.listFiles();

			for (int filternameindex = 0; filternameindex < layerFiles.length; filternameindex++) {
				ImageView temp = new ImageView(this);
				temp.setImageBitmap(BitmapFactory.decodeFile(layerFolder
						.getAbsolutePath()
						+ "/"
						+ imageFilters[filternameindex] + ".png"));

				temp.setId(2000 + count * imageFilters.length + filternameindex);
				temp.setOnClickListener(filtersLayerClickListener);

				temp.setImageBitmap(  addBorder(  ((BitmapDrawable)temp.getDrawable()).getBitmap(),2,Color.WHITE));

				if(filternameindex!=0)
					temp.setLayoutParams(imgViewParams);

				images.add(temp);

				if (count == 0)
					filtersLayout.addView(temp);

				
			}

			filteredViews.add(images);

		}

		hs.addView(filtersLayout);
		activityLayout.addView(hs);

		hs.setVisibility(View.INVISIBLE);
		System.gc();

		// TODO Auto-generated method stub

	}

	private Bitmap addBorder(Bitmap bmp, int borderSize,int borderColor) {

		Log.d("Border","border");
		Bitmap bmpWithBorder = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

		Canvas canvas = new Canvas(bmpWithBorder);
		Paint bp= new Paint();
		bp.setColor(borderColor);//set a color
		bp.setStrokeWidth(borderSize);// set your stroke width
		// w and h are width and height of your imageview
		int w=bmp.getWidth(),h=bmp.getHeight();

		canvas.drawBitmap(bmp, 0, 0, new Paint());

		canvas.drawLine(0, 0, w,0,bp);
		canvas.drawLine(0, 0,0, h,bp);
		canvas.drawLine(w,h,w,0,bp);
		canvas.drawLine(w, h,0,h, bp);

		//   canvas.drawBitmap(bmp, rect, rect, paint);

		return bmpWithBorder;
	}

	private void initializeVerticalScroller() {
		layersLayout = new LinearLayout(this);

		LinearLayout.LayoutParams llayoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);

		layersLayout.setLayoutParams(llayoutParams);
		layersLayout.setOrientation(LinearLayout.VERTICAL);
		// layersLayout.setPadding(25,120,0,0);

		layersScroll = new ScrollView(this);

		layersScroll.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		layersScroll.setPadding(12, 130, 0, 0);
		// layersLayout.setSc

		layersScroll.addView(layersLayout);
		activityLayout.addView(layersScroll);

	}




	private void startAnimation() {

		int[] loc = new int[2];
		float xPos, yPos;

		
		// This will make the small layer icons ...

		for (int i = 0; i < mimageViews.size(); i++) {

			AnimationSet tempAnimation = new AnimationSet(true);

			mimageViews.get(i).getLocationOnScreen(loc);

			/*
			 * How is this evaluated,
			 * 
			 * The layers are going to be displayed from 25,150 onwards .Then
			 * after every image,we leave a space of 2.5*height(). So animation
			 */

			xPos = (-this.getWindowManager().getDefaultDisplay().getWidth() / 2)
					+ (float) 
					scaleValue * mimageViews.get(i).getWidth() / 2 + 25; // From

			yPos = (-this.getWindowManager().getDefaultDisplay().getHeight() / 2)
					+ (float) (150 + (0.5 + 2.5 )* i
							* (
									scaleValue * mimageViews.get(i).getHeight()) / 2);

			Log.d(TAG,
					"position " + 150 + (0.5 + i)
					* (
							scaleValue * mimageViews.get(i).getHeight()) / 2);
			// TranslateAnimation anim = new TranslateAnimation( 0,(float)
			// (mimageViews.get(i).getTop()-25) , 0,
			// 0+mimageViews.get(i).getLeft()+100*i );

			TranslateAnimation anim = new TranslateAnimation(0, xPos, 0, yPos);
			// anim.setDuration(1000);
			Log.d(TAG, "location - " + loc[0] + "  " + loc[1]);
			anim.setFillAfter(true);
			anim.setFillEnabled(true);

			ScaleAnimation scaleanimation = new ScaleAnimation(1, (float) 
					scaleValue,
					1, (float) 
					scaleValue, Animation.RELATIVE_TO_SELF, (float) 0.5,
					Animation.RELATIVE_TO_SELF, (float) 0.5);
			scaleanimation.setDuration(1000);
			scaleanimation.setFillEnabled(true);
			scaleanimation.setFillAfter(true);

			tempAnimation.addAnimation(scaleanimation);
			tempAnimation.addAnimation(anim);
			tempAnimation.setDuration(1000);

			tempAnimation.willChangeTransformationMatrix();

			// This takes care that the ImageViews stay in their final positions
			// after animating .
			tempAnimation.setFillEnabled(true);
			tempAnimation.setFillAfter(true);

			if (i == 0)
				tempAnimation.setAnimationListener(animFinishListener);

			mimageViews.get(i).startAnimation(tempAnimation);

		}

		// This will animate the Canvas Views in the middle ..
		
		

		for (int i = 0; i < CanvasImageViews.size(); i++) {
			AnimationSet tempAnimation = new AnimationSet(true);

			TranslateAnimation anim = new TranslateAnimation(0,90, 0, -60); // probably /30

			anim.setFillAfter(true);
			anim.setFillEnabled(true);

			ScaleAnimation scaleanimation = new ScaleAnimation(1, (float) 0.81,
					1, (float) 0.77, Animation.RELATIVE_TO_SELF, (float) 0.5,
					Animation.RELATIVE_TO_SELF, (float) 0.5);

			scaleanimation.setDuration(1000);
			scaleanimation.setFillEnabled(true);
			scaleanimation.setFillAfter(true);

			tempAnimation.addAnimation(scaleanimation);
			tempAnimation.addAnimation(anim);
			tempAnimation.setDuration(1000);

			tempAnimation.willChangeTransformationMatrix();

			// This takes care that the ImageViews stay in their final positions
			// after animating .
			tempAnimation.setFillEnabled(true);
			tempAnimation.setFillAfter(true);

			CanvasImageViews.get(i).startAnimation(tempAnimation);

		}

	}

	public void applyFiltertoView(int currentSelectedFilter) {
		// Bitmap
		// imgViewBitmap=((BitmapDrawable)CanvasImageViews.get(currentSelectedLayer).getDrawable()).getBitmap();
		Bitmap imgViewBitmap = layerBitmaps.get(currentSelectedLayer);

		Bitmap modifiedBitmap = Bitmap.createScaledBitmap(imgViewBitmap,
				imgViewBitmap.getWidth(), imgViewBitmap.getHeight(), true);
		
		modifiedBitmap=newFiltersInterface.applyFiltertoView(modifiedBitmap,imageFilters[currentSelectedFilter]);
				
		
		CanvasImageViews.get(currentSelectedLayer).setImageBitmap(
				modifiedBitmap);

	}

	/**
	 * Have to call the startAnimation from here and not onCreate because if to
	 * get an ImageView.getWidth(),we have to call it from onWindowFocusChanged
	 * and not from onCreate,start or resume Refer
	 * http://stackoverflow.com/questions
	 * /7924296/how-to-use-onwindowfocuschanged-method
	 */

	public void onBackPressed() {

//		String packageName = "com.android.camera"; //Or whatever package should be launched
//
//    	if(packageName.equals("com.android.camera")){ //Camera
//    	    try
//    	    {
//    	       
//    	    	Intent intent = getPackageManager().getLaunchIntentForPackage("com.android.camera");
//
//    	    	intent.putExtra("android.intent.extras.CAMERA_FACING", 2);
//    	    	intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//    	        //Log.d("Test","num"+Camera.getNumberOfCameras());
//    	        startActivity(intent);
//
//    	    }
//    	    catch(ActivityNotFoundException e){
//    	        Intent intent = new Intent();
//    	        ComponentName comp = new ComponentName("com.android.camera", "com.android.camera.CameraEntry");
//    	        intent.setComponent(comp);
//    	        startActivity(intent);
//    	    }
//    	}
//    	else{ //Any other
//    	    Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
//    	    startActivity(intent);
//    	}
		
		Intent it = new Intent(this,MainScreen.class);
		
		if (null != it)
			{
			
			it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			it.putExtra("computeDisparity", false);
    
				this.startActivity(it);
			}   	
}
	
	public void onWindowFocusChanged(boolean hasFocus) {

		if(!isStarted)
		{
			layersLayout.removeAllViews();
			startAnimation();
			isStarted=!isStarted;
		}
	}

	private void LoadFiles(File seperatedLayersFolder) {

		File[] files = seperatedLayersFolder.listFiles();

		Log.d(TAG, "Number of files:" + files.length);

		for (File file : files) {
			Log.d("File path:", "Path=" + file.getPath());
			// Create 2 ImageViews,1 for the layer and the other for the bitmap.

			if (file.getName().toUpperCase().endsWith(("JPG"))
					|| file.getName().toUpperCase().endsWith(("PNG"))) {
				mimageViews.add(createImageView(file.getPath()));
				CanvasImageViews.add(createImageView(file.getPath()));
				layerBitmaps.add(BitmapFactory.decodeFile(file.getPath()));

				selectedFilters.add(-1);
				Log.d(TAG,file.getPath());
			}

		}

	
		// Copy the ImageViews which will be drawn to the Canvas..

	}

	private ImageView createImageView(String imageLocation) {

		ImageView newimageView = new ImageView(this);

		Bitmap tempBitmap;

		tempBitmap = BitmapFactory.decodeFile(imageLocation);
		newimageView.setImageBitmap(tempBitmap);

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
  
		newimageView.setLayoutParams(layoutParams);

		/* Bug in Android 2.3.If this is not set,a trail is left behind */
		newimageView.setPadding(1, 1, 1, 1);

		activityLayout.addView(newimageView);

		Log.d(TAG, " Width " + newimageView.getWidth());
		System.gc();
		return newimageView;
	}

	public void resetBorders()
	{
		Iterator<Vector<ImageView>> itr = filteredViews.iterator();

		Log.d(TAG,"sizes "+filteredViews.size()+"  "+filteredViews.get(1).size());


		while(itr.hasNext())
		{


			Vector<ImageView> row= (Vector<ImageView>)itr.next();

			for(int i=0;i<row.size();i++)
			{
				ImageView temp=row.elementAt(i);
				temp.setImageBitmap(addBorder(  ((BitmapDrawable)temp.getDrawable()).getBitmap(),2,Color.WHITE));

			}


		}

		//		
		//		
		//		
		//		
		//		for(int i=0;i<filteredViews.size()-1;i++)
		//			{
		//				for(int j=0;j<filteredViews.get(i).size()-2;j++)
		//				{
		//					ImageView temp;
		//				    temp=(ImageView)findViewById(2000+i*imageFilters.length+j);
		//				    Log.d(TAG,"width "+temp.getWidth()+" height ="+temp.getHeight());
		//					temp.setImageBitmap(addBorder(  ((BitmapDrawable)temp.getDrawable()).getBitmap(),2,Color.WHITE));
		//	
		//				}
		//			
		//	}

	}

	AnimationListener animFinishListener = new AnimationListener() {

		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub

			/**
			 * This is a weird error.Android will make an exception when you
			 * change the view hierarchy in animationEnd.You have to postpone
			 * the call by using the handler
			 * .Refer:http://stackoverflow.com/questions
			 * /5569267/nullpointerexception
			 * -that-doesnt-point-to-any-line-in-my-code
			 */
			new Handler().post(new Runnable() {
				public void run() {
					// Resize and reassign the views to the Linear layout ..

					for (int i = 0; i < mimageViews.size(); i++) {

						mimageViews.get(i).setImageBitmap(
								Bitmap.createScaledBitmap(layerBitmaps.get(i),
										(int) Math.round((
												scaleValue * layerBitmaps
												.get(i).getWidth())),
												(int) Math.round((
														scaleValue * layerBitmaps
														.get(i).getHeight())), true));
						// layerBitmaps.get(i).createScaledBitmap(src, dstWidth,
						// dstHeight, filter)
						if(i==0)
							mimageViews.get(i).setImageBitmap(  addBorder(  ((BitmapDrawable)mimageViews.get(i).getDrawable()).getBitmap(),2,Color.RED));
						else mimageViews.get(i).setImageBitmap(  addBorder(  ((BitmapDrawable)mimageViews.get(i).getDrawable()).getBitmap(),2,Color.WHITE));

						Log.d(TAG,
								"Width small"
										+ (int) (2 * layerBitmaps.get(i)
												.getWidth()) / 10);

						int width = 2 * layerBitmaps.get(i).getWidth();
						width = width / 10;

						Log.d(TAG,
								"Width small ==  "
										+ Math.round((
												scaleValue * layerBitmaps
												.get(i).getWidth())));

						// mimageViews.get(i).setBackgroundColor(Color.WHITE);
						// mimageViews.get(i).setPadding(1, 1, 1, 1)

						if (i > 0)
							mimageViews.get(i).setPadding(
									0,
									(int) Math.round(1 * (
											scaleValue * mimageViews
											.get(i).getHeight()) / 2) - 30, 0,
											0);

						Log.d(TAG,
								"Height"
										+ (int) Math
										.round(2.5 * (
												scaleValue * mimageViews
												.get(i).getHeight()) / 2));

						Log.d(TAG,
								"sma height"
										+ (2.5 * (
												scaleValue * mimageViews.get(i)
												.getHeight()) / 2));

						activityLayout.removeView(mimageViews.get(i));

						mimageViews.get(i).setClickable(true);
						mimageViews.get(i).bringToFront();
						mimageViews.get(i).setOnTouchListener(
								imageLayerClickListener);

						mimageViews.get(i).setId(1000 + i); // Need to set an
						// id..1

						LinearLayout.LayoutParams imgViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
						imgViewParams.setMargins(0, 50*i, 0, 0);

						mimageViews.get(i).setLayoutParams(imgViewParams);

						layersLayout.addView(mimageViews.get(i));
						//layersLayout.setVisibility(View.INVISIBLE);

						Log.d(TAG,
								"width"
										+ ((int) (2 * layerBitmaps.get(i)
												.getWidth()) / 10));

					}
				}
			});

			/*
			 * Animate the HS
			 */

			hs.setAnimation(animFadeIn);
			layersLayout.setAnimation(animFadeIn);
			//layersLayout.setVisibility(View.VISIBLE);
			hs.setVisibility(View.VISIBLE);

			// Add the buttons ..
			addButtonstoActivity();
			
			// Add a touch listener to an imageview in the canvas .
			CanvasImageViews.get(0).setOnTouchListener(new TouchListener());

		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub

		}
	};

	public OnTouchListener imageLayerClickListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub

			// Log.d(TAG,"touched");

			if (event.getAction() == MotionEvent.ACTION_DOWN) {

				
				for (int i = 0; i < mimageViews.size(); i++) {

					Log.d(TAG,"View id="+i+" size"+mimageViews.size());
					ImageView temp2 =(ImageView)mimageViews.get(i);
					//temp2.setImageBitmap(addBorder(  ((BitmapDrawable)temp2.getDrawable()).getBitmap(),5,Color.WHITE));
					temp2.setPadding(0, 0,0,0);
					
					temp2.setPadding(3,3,3,3);
					temp2.setImageBitmap(addBorder(  ((BitmapDrawable)temp2.getDrawable()).getBitmap(),5,Color.WHITE));
					
					
					if (v.getId() == mimageViews.get(i).getId()) {
						currentSelectedLayer = i;

						// add a red border to show it is selected ..

						//temp2.setImageBitmap(layerBitmaps.get(0));
						//temp.setImageBitmap(addBorder(  ((BitmapDrawable)temp.getDrawable()).getBitmap(),5,Color.RED));
						temp2.setImageBitmap(addBorder(  ((BitmapDrawable)temp2.getDrawable()).getBitmap(),5,Color.RED));
//						addBorder(  ((BitmapDrawable)temp2.getDrawable()).getBitmap(),5,Color.RED);
						temp2.setPadding(3,3,3,3);
						temp2.setImageBitmap(addBorder(  ((BitmapDrawable)temp2.getDrawable()).getBitmap(),5,Color.RED));
						
						hs.removeAllViews();
						filtersLayout.removeAllViews();

						Log.d(TAG,
								"size i=" + i + "size of "
										+ filteredViews.size() + " : "
										+ filteredViews.get(i).size());

						for (int j = 0; j < filteredViews.get(i).size(); j++)
							filtersLayout.addView(filteredViews.get(i).get(j));

						//
						hs.addView(filtersLayout);
						hs.invalidate();

						Log.d(TAG, "passing");
					}
					


				}

//				ImageView temp =(ImageView)v;
//				temp.setImageBitmap(addBorder(  ((BitmapDrawable)temp.getDrawable()).getBitmap(),5,Color.RED));
//				temp.invalidate();
				
			}
			layersScroll.invalidate();

			return false;
		}

	};


	public OnClickListener filtersLayerClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			resetBorders();

			int id = v.getId() - 2000;
			applyFiltertoView(id % imageFilters.length);
			ImageView temp =(ImageView)v;
			temp.setImageBitmap(addBorder(  ((BitmapDrawable)temp.getDrawable()).getBitmap(),2,Color.RED));
			
			selectedFilters.setElementAt(id % imageFilters.length, currentSelectedLayer);
			
			Log.d(TAG,"filter at "+currentSelectedLayer+" = "+selectedFilters.get(currentSelectedLayer));
			Log.d(TAG, "ID" + id);

		}

	};
	
	protected void addButtonstoActivity() {
		// TODO Auto-generated method stub
//
//		full_screen=new ImageButton(this);
//		full_screen.setImageDrawable(getResources().getDrawable(R.drawable.fullscreen));
//		full_screen.setBackgroundColor(Color.TRANSPARENT);
//		RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(
//				RelativeLayout.LayoutParams.WRAP_CONTENT,
//				RelativeLayout.LayoutParams.WRAP_CONTENT);
//		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//
//		layoutParams.setMargins(0, 120,20, 0);
//		full_screen.setLayoutParams(layoutParams);
//		full_screen.setId(54345);
//		activityLayout.addView(full_screen);
//		full_screen.setOnClickListener(buttonClickListener);
//
//		layoutParams=new RelativeLayout.LayoutParams(
//				RelativeLayout.LayoutParams.WRAP_CONTENT,
//				RelativeLayout.LayoutParams.WRAP_CONTENT);
//		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
//
//		TextView valueTV = new TextView(this);
//		valueTV.setText("h");
//		valueTV.setId(54321);
//		valueTV.setLayoutParams(layoutParams);
//		valueTV.setVisibility(View.INVISIBLE);
//		activityLayout.addView(valueTV);
//
//		
		/* commenting these out .. */
		
//		Replace=new ImageButton(this);
//		Replace.setImageDrawable(getResources().getDrawable(R.drawable.replace));
//		Replace.setBackgroundColor(Color.TRANSPARENT);
//		layoutParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
//				RelativeLayout.LayoutParams.WRAP_CONTENT);
//		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
//		layoutParams.addRule(RelativeLayout.LEFT_OF, 54321);
//		
//
//		//layoutParams.setMargins(0, 0,40, 0);
//
//		Replace.setLayoutParams(layoutParams);
//		Replace.setId(987123);
//		
//		activityLayout.addView(Replace);

//		layoutParams=new RelativeLayout.LayoutParams(
//				RelativeLayout.LayoutParams.WRAP_CONTENT,
//				RelativeLayout.LayoutParams.WRAP_CONTENT);
//		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
//		layoutParams.addRule(RelativeLayout.RIGHT_OF, 54321);
//
//		layoutParams.setMargins(175, 0,0, 0);
//
//		focus=new ImageButton(this);
//		focus.setImageDrawable(getResources().getDrawable(R.drawable.bluricon));
//		focus.setLayoutParams(layoutParams);
//		focus.setBackgroundColor(Color.TRANSPARENT);
//		focus.setId(11223);
//		focus.setOnClickListener(buttonClickListener);	
//		activityLayout.addView(focus);
		
		RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		
		//layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);

		//layoutParams.addRule(RelativeLayout.BELOW, 54345);

		layoutParams.setMargins(40,0,0,20);

		
		
		reset=new ImageButton(this);

		reset.setId(9876);
		reset.setOnClickListener(buttonClickListener);
		reset.setImageDrawable(getResources().getDrawable(R.drawable.reset));
		reset.setLayoutParams(layoutParams);
		reset.setBackgroundColor(Color.TRANSPARENT);
		activityLayout.addView(reset);

		layoutParams=new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		
		layoutParams.setMargins(30, 0,0,0);
	
		back=new ImageButton(this);
		
		back.setOnClickListener(buttonClickListener);
		back.setImageDrawable(getResources().getDrawable(R.drawable.back));
		back.setLayoutParams(layoutParams);
		back.setBackgroundColor(Color.TRANSPARENT);
		back.setId(12021);
		
		activityLayout.addView(back);
		
		layoutParams=new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		layoutParams.setMargins(0, 0,20,0);		
		
//		accept=new ImageButton(this);
//		
//		accept.setOnClickListener(buttonClickListener);
//		accept.setImageDrawable(getResources().getDrawable(R.drawable.greencheck));
//		accept.setLayoutParams(layoutParams);
//		accept.setBackgroundColor(Color.TRANSPARENT);
//		accept.setId(12012);
//		
//		activityLayout.addView(accept);
	}

	public OnClickListener buttonClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
       
			if(v.getId()==54345)
			{
				Vector<ImageView> fullImgViews=new Vector<ImageView>();
				RelativeLayout.LayoutParams imgViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				fullScreenLayout.removeAllViews();
				setContentView(fullScreenLayout);
				
				for(int i=0;i<CanvasImageViews.size();i++)
					{   
					    ImageView temp=new ImageView(getBaseContext());
					    temp.setImageDrawable(CanvasImageViews.get(i).getDrawable());
					    temp.setLayoutParams(imgViewParams);
					    fullImgViews.add(temp);
						fullScreenLayout.addView(temp);
					}
				
				imgViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				
				ImageButton backButton;
				backButton=new ImageButton(getBaseContext());
				
				backButton.setBackgroundColor(Color.TRANSPARENT);
				backButton.setImageDrawable(getResources().getDrawable(R.drawable.normalscreen));
				
//				imgViewParams.setMargins(0, 0, 0, 0);
				imgViewParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				backButton.setLayoutParams(imgViewParams);
				fullScreenLayout.addView(backButton);
				
				backButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						setContentView(activityLayout); 
					}
				});
				//backButton.set
			}
			
			else if(v.getId()==9876)
			{
				
			for(int i=0;i<CanvasImageViews.size();i++)
			{
			
				CanvasImageViews.get(i).setImageBitmap(layerBitmaps.get(i));
				
			}
			resetBorders();

		}
			
			else if(v.getId()==12021)
			{
				
				Intent it=new Intent(activityContext,MainScreen.class);
				it.putExtra("computeDisparity", false);
			   
				activityContext.startActivity(it);
				
//				fileObserver=new CustomFileObserver(getBaseContext());
//				
//			  	String packageName = "com.android.camera"; //Or whatever package should be launched
//
//	        	if(packageName.equals("com.android.camera")){ //Camera
//	        	    try
//	        	    {
//	        	       
//	        	    	Intent intent = getPackageManager().getLaunchIntentForPackage("com.android.camera");
//
//	        	    	intent.putExtra("android.intent.extras.CAMERA_FACING", 2);
//	        	    	intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//	        	        //Log.d("Test","num"+Camera.getNumberOfCameras());
//	        	        startActivity(intent);
//
//	        	    }
//	        	    catch(ActivityNotFoundException e){
//	        	        Intent intent = new Intent();
//	        	        ComponentName comp = new ComponentName("com.android.camera", "com.android.camera.CameraEntry");
//	        	        intent.setComponent(comp);
//	        	        startActivity(intent);
//	        	    }
//	        	}
//	        	else{ //Any other
//	        	    Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
//	        	    startActivity(intent);
//	        	}
			}
			
			else if (v.getId()==12012)
			{
				
				
			
				
			}
			
			else if (v.getId()==11223)
			{
				// Change the focus
				
				focusButtonClicked=!focusButtonClicked;
				if(focusButtonClicked)
					focus.setImageDrawable(getResources().getDrawable(R.drawable.blur_focus));
				else focus.setImageDrawable(getResources().getDrawable(R.drawable.bluricon));
				
				// 
				
			
			}
			else if(v.getId()==987123)
			{
				// Replace the button 
				
				saveImgandSharePic();
			}
			
			
			
			
		}
	};
	
private Bitmap addCircles(Bitmap bmp,float x,float y) {
	    
		Bitmap tempBitmap=Bitmap.createScaledBitmap(bmp, bmp.getWidth(),bmp.getHeight(), true);
	    Canvas canvas = new Canvas(tempBitmap);
//	    Paint bp= new Paint();
//	    bp.setColor(Color.WHITE);//set a color
//	    bp.setStrokeWidth(5);// set your stroke width
//	    
	    canvas.drawBitmap(tempBitmap, 0, 0, new Paint());
	    
	    canvas.drawCircle(x, y, 20, canvasPaint);
	    canvas.drawCircle(x, y, 40, canvasPaint);
	    canvas.drawCircle(x, y, 60, canvasPaint);
	    
	 //   canvas.drawBitmap(bmp, rect, rect, paint);
	    
	    return tempBitmap;
	}
	
	
	  
    protected void saveImgandSharePic() {
	// TODO Auto-generated method stub
	
    	img1=new Mat();img2=new Mat();
		// Save the canvas image ..
		Canvas saveImageCanvas;
//		Bitmap saveImageBitmap =Bitmap.createBitmap(CanvasImageViews.get(0).getWidth(), CanvasImageViews.get(0).getHeight(), Bitmap.Config.ARGB_8888);
		
		Drawable saveDrawable=CanvasImageViews.get(1).getDrawable();
		Bitmap	saveImageBitmap=((BitmapDrawable)saveDrawable).getBitmap();
		
		Utils.bitmapToMat(saveImageBitmap, img1);
		
		Bitmap mutableBitmap= saveImageBitmap.copy(Bitmap.Config.ARGB_8888, true);
		
		saveImageCanvas = new Canvas(mutableBitmap);
		
		saveDrawable=CanvasImageViews.get(0).getDrawable();
		
		Utils.bitmapToMat( ((BitmapDrawable)saveDrawable).getBitmap(), img2);
		
		saveImageCanvas.drawBitmap(((BitmapDrawable)saveDrawable).getBitmap(), 0, 0, null);
		
		saveImageCanvas.save();
		
		Mat addition = new Mat();
		
		Core.add(img1, img2, addition);
		Mat modified_mat=new Mat();
	//	
		Size dimensions = new Size();
		dimensions.width=0.6*addition.cols();
		dimensions.height=0.6*addition.rows();
		
		//Highgui.imwrite("/mnt/sdcard/Studio3D/combined.png",addition);
		Imgproc.cvtColor(addition, addition, Imgproc.COLOR_BGR2RGBA);
		Highgui.imwrite("/mnt/sdcard/Studio3D/combined_orig.png",addition);
		Imgproc.resize(addition, addition,dimensions);
		Highgui.imwrite("/mnt/sdcard/Studio3D/combined.png",addition);
		
		

		
		Intent intent = new Intent(getBaseContext(),SocialSharing.class);
		startActivity(intent);
}



	class TouchListener implements View.OnTouchListener{

		@Override
		public boolean onTouch(View v, MotionEvent event) {

		//	if(event.getAction() == MotionEvent.ACTION_DOWN&&focusButtonClicked) 
			if(event.getAction() == MotionEvent.ACTION_DOWN)
			{
				
				
				saveImgandSharePic();
				
//				Log.d(TAG,"X ="+(event.getRawX()-CanvasImageViews.get(1).getLeft())+"  Y= "+(event.getRawY()-CanvasImageViews.get(1).getTop())); // For landscape orientation,i.e max val of x is 800 and y max value is 480 ..
//
// 				
// 				
// 				// Pass these to the JNI function .. These will be the touch positions out of 500x500 .
// 				converted_xcoord=(event.getRawX()-CanvasImageViews.get(1).getLeft());
// 				converted_ycoord=(event.getRawY()-CanvasImageViews.get(1).getTop());
// 				
// 				backupBitmap=((BitmapDrawable)CanvasImageViews.get(1).getDrawable()).getBitmap();
// 				
 			//	CanvasImageViews.get(1).setImageBitmap(  addCircles( ((BitmapDrawable)CanvasImageViews.get(0).getDrawable()).getBitmap(),converted_xcoord,converted_ycoord));
 				
 			//	imageViewBitmap=backupBitmap; // not sure ?
// 				
 				// These will be the corresponding touch positions for the original image i.e touch positions in 500x500 are converted into 640x720 ..
// 				   converted_xcoord=(converted_xcoord/CanvasImageViews.get(0).getWidth())*500;
// 		           converted_ycoord=(converted_ycoord/CanvasImageViews.get(0).getHeight())*500;
// 		           Log.d(TAG, String.valueOf(converted_ycoord));
// 		           Log.d(TAG, String.valueOf(converted_xcoord));
// 		           Log.d(TAG, "converted");
// 		            //finalImage = new Mat();
// 		        
// 		           Log.d(TAG,"channels  "+mRgba.channels());
// 		          Log.d(TAG,"channels  "+disp.channels());
// 		            currentMode=1;
// 		            
// 		           new ComputeDisparity().execute("");
// 				   
// 					
// 		          
// 		            img1=new Mat();img2=new Mat();
// 					// Save the canvas image ..
// 					
//// 					Bitmap saveImageBitmap =Bitmap.createBitmap(CanvasImageViews.get(0).getWidth(), CanvasImageViews.get(0).getHeight(), Bitmap.Config.ARGB_8888);
// 					
// 					Drawable saveDrawable=CanvasImageViews.get(1).getDrawable();
// 					Bitmap	saveImageBitmap=((BitmapDrawable)saveDrawable).getBitmap();
// 					
// 					Utils.bitmapToMat(saveImageBitmap, img1);
// 					
// 				//	Bitmap mutableBitmap= saveImageBitmap.copy(Bitmap.Config.ARGB_8888, true);
// 					
// 					saveDrawable=CanvasImageViews.get(0).getDrawable();
// 					
// 					Utils.bitmapToMat( ((BitmapDrawable)saveDrawable).getBitmap(), img2);
// 					
// 					
// 					Core.add(img1, img2, mRgba);
// 					Imgproc.cvtColor(mRgba, mRgba, Imgproc.COLOR_RGBA2BGR);
 					
			   
			}
			return false;
			
			}
		}
    
    
    public  void initializeMats() {
		// TODO Auto-generated method stub
	    mRgba = PhotoActivity.mRgba;
		disp = PhotoActivity.disp;
		limg = new Mat();
		foreground=new Mat();
		background=new Mat();
		finalImage=new Mat();
    
	    	
	}
 private class ComputeDisparity extends AsyncTask<String, Void, String> {
    	
        @Override
        protected String doInBackground(String... params) {
        	Log.d("reached","reache");
        	// getDisparity(mRgba.getNativeObjAddr(), finalImage.getNativeObjAddr(), (int)converted_xcoord, (int)converted_ycoord);
// Commenting out for now ..
        	
        	   getThreshold(mRgba.getNativeObjAddr(), disp.getNativeObjAddr(), finalImage.getNativeObjAddr(), background.getNativeObjAddr(),foreground.getNativeObjAddr(),(int)converted_xcoord, (int)converted_ycoord,currentMode);
	           
	    	
              return "";
        }      

        @Override
        protected void onPostExecute(String result) {
           
            // progress.dismiss();
             
             Log.d(TAG,"blah");
             
//             String colVal = String.valueOf(finalImage.cols());
//	            Log.d("Cols", colVal);
//	            Highgui.imwrite(leftimgFile.getAbsolutePath(), finalImage);
//	            myBitmap = BitmapFactory.decodeFile(leftimgFile.getAbsolutePath());
//	            imageViewBitmap=myBitmap;
//	            mImageView.setImageBitmap(imageViewBitmap);
  
             
             /// Take the foreground and background mat and convert it to the imageview .
             
             Imgproc.cvtColor(background, background, Imgproc.COLOR_BGR2RGBA);
             Bitmap tempBitmap=Bitmap.createBitmap(background.cols(),background.rows(), 
            		 Bitmap.Config.ARGB_8888);
             
             Utils.matToBitmap(background, tempBitmap);
             
             CanvasImageViews.get(1).setImageBitmap(tempBitmap);
             
             Imgproc.cvtColor(foreground, foreground, Imgproc.COLOR_BGR2RGBA);
            
             Utils.matToBitmap(foreground, tempBitmap);
             
             CanvasImageViews.get(1).setImageBitmap(tempBitmap);
             
	         System.gc();
	    	  
		   //  mImageView.setImageBitmap(myBitmap);
		    
		       Log.d("done","done");
             
             // txt.setText(result);
             
              //might want to change "executed" for the returned string passed into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {
//        	   progress.setTitle("Processing Image");
//               progress.setMessage("Please wait while we process your image ...");
//               progress.show();
        	System.gc();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
        
  } 
 
// public boolean onCreateOptionsMenu(Menu menu) {
//
//	    menu.add(group1Id, menuSettings, menuSettings, "Edit Layers");
//	  
//	    return super.onCreateOptionsMenu(menu); 
//	    }

 @Override
public boolean onOptionsItemSelected(MenuItem item) {

	    switch (item.getItemId()) {

	case 1:
	    //write your code here

		deallocateMemory();
		
		Intent it = new Intent(this,CanvasActivity.class);
		
		 Bundle b = new Bundle();
		 
		 b.putString("filter1", imageFilters[selectedFilters.get(0)]);
		 b.putString("filter2", imageFilters[selectedFilters.get(1)]);
		 b.putInt("filterNum", selectedFilters.get(1));
		if (null != it)
			{
			
			it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Log.d(TAG,"filters are .filter_fg : "+imageFilters[selectedFilters.get(0)]+ " filter_bg :"+imageFilters[selectedFilters.get(1)]);
			
			it.putExtra("filterNum",selectedFilters.get(1));
			it.putExtra("android.intent.extra.INTENT", b);
			
			
			startActivity(it);
			}   
		break;

	default:
	    break;

	       }
	    return super.onOptionsItemSelected(item);
	}

    
 /** Freeing up precious memory ! **/
 
 void deallocateMemory()
 {
	
	 for(int i=0;i<layerBitmaps.size();i++)
		layerBitmaps.get(i).recycle();

	 for(int i=0;i<mimageViews.size();i++)
	 {
		 mimageViews.get(i).setImageDrawable(null);
		 
		 CanvasImageViews.get(i).setImageDrawable(null);
	 }
	 
	 
	 
	 Iterator<Vector<ImageView>> itr = filteredViews.iterator();

		while(itr.hasNext())
		{


			Vector<ImageView> row= (Vector<ImageView>)itr.next();

			for(int i=0;i<row.size();i++)
			{
				ImageView temp=row.elementAt(i);
				temp.setImageDrawable(null);
			}


		}

	 
	 
		 fullScreenLayout.removeAllViews();
		 activityLayout.removeAllViews();
		
	 
	 
	 
 }
 
    
	public native void getThreshold(long matAddrRgba, long matAddrDisp, long matAddrfinalImage,long matAddrBackground, long matAddrForeground, int ji1, int ji2,int choice);
	   
}
 