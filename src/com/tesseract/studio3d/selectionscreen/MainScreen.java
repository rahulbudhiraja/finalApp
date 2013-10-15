package com.tesseract.studio3d.selectionscreen;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.tesseract.studio3d.R;
import com.tesseract.studio3d.StartScreen;
import com.tesseract.studio3d.Animation.PhotoActivity;
import com.tesseract.studio3d.refocus.Refocus;
import com.tesseract.studio3d.replace.MainListViewActivity;
import com.tesseract.studio3d.utils.Structs;

public class MainScreen extends Activity
{
	ImageView placeholderImage;
	ImageButton startFocusActivity,startPhotoActivity,startReplaceActivity;
	Mat clickedImage;
	static String TAG="MainScreen";
	ProgressDialog conversionProgress;
	private Mat mRgba;
	private Mat disp;
	Bitmap tempBitmap;	
	Size desiredSize;
	Boolean computeDisparity=false;
	
	protected void onCreate(Bundle savedInstanceState) 
	{

		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.main_screen);
		
		
		
		
		placeholderImage=(ImageView)findViewById(R.id.placeholderView);
	
		this.getResources().getDrawable(R.drawable.placeholder).getIntrinsicWidth();
		int tempWidth=this.getResources().getDrawable(R.drawable.placeholder).getIntrinsicWidth(),tempHeight=this.getResources().getDrawable(R.drawable.placeholder).getIntrinsicHeight();
		
		Log.d(TAG,"width"+ tempWidth);
		Log.d(TAG,"height"+ tempHeight);
		
		clickedImage=null;
		clickedImage=Highgui.imread(Environment.getExternalStorageDirectory().getPath()+"/Studio3D/img_left.jpg");
		
		Structs.left_img_path=Environment.getExternalStorageDirectory().getPath()+"/Studio3D/img_left.jpg";
		Structs.mRgba=new Mat();
		Structs.mRgba = Highgui.imread(Environment.getExternalStorageDirectory().getPath()+"/Studio3D/img_full.jpg");	
		Structs.mDisparity=new Mat();
		Structs.mLeft=Highgui.imread(Structs.left_img_path);
		
		Log.d(TAG,"width"+clickedImage.cols());
		Log.d(TAG,"height"+clickedImage.rows());
		
		desiredSize=new Size();
		desiredSize.width=tempWidth;
		desiredSize.height=tempHeight;
		
		Imgproc.resize(clickedImage, clickedImage, desiredSize);
		Imgproc.cvtColor(clickedImage, clickedImage, Imgproc.COLOR_BGR2RGBA);
		
		tempBitmap=Bitmap.createBitmap(tempWidth,tempHeight, 
         		 Bitmap.Config.ARGB_8888);
        
		Utils.matToBitmap(clickedImage,tempBitmap);
		placeholderImage.setImageBitmap(tempBitmap);
				
		startFocusActivity=(ImageButton)findViewById(R.id.focusButton);
		startFocusActivity.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent i=new Intent(getBaseContext(),Refocus.class);
				startActivity(i);
			}
			 
		});
		
		startPhotoActivity=(ImageButton)findViewById(R.id.photoButton);
		startPhotoActivity.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent i=new Intent(getBaseContext(),PhotoActivity.class);
				startActivity(i);
			}
			 
		});
	
		startReplaceActivity=(ImageButton)findViewById(R.id.replaceButton);
		startReplaceActivity.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent i=new Intent(getBaseContext(),MainListViewActivity.class);
				startActivity(i);
			}
			 
		});
		conversionProgress = new ProgressDialog(this);

		 Intent i = getIntent();
		 
		   
		computeDisparity=i.getExtras().getBoolean("computeDisparity");
		    
		if(computeDisparity)   
		new ComputeDisparityDialog().execute("");

		
	}
	
	
	protected void onStart() 
	{
	    super.onStart();  // Always call the superclass method first
	    Log.d(TAG,"start");
	   
	    if(Structs.finalImgRgba!=null)
	    {
	    Mat convertedMat=new Mat();
		Imgproc.resize(Structs.finalImgRgba, convertedMat, desiredSize);
		
	    Utils.matToBitmap(convertedMat,tempBitmap);
		placeholderImage.setImageBitmap(tempBitmap);
	    }	
	    //
	}
	
	public void onBackPressed() 
	{
		Intent intent = new Intent(MainScreen.this, StartScreen .class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
		
	}
	
	protected void onResume() {
	    super.onResume();  // Always call the superclass method first
	    
	    Log.d(TAG,"resume");
	}
	
	 static {
		    if (!OpenCVLoader.initDebug()) {
		        
				// Handle initialization error
		    	Log.d(TAG,"error");
		    }
		    else System.loadLibrary("depth_magic");
		    
		}	

	 
	 public  void initializeMats() 
	 {
			// TODO Auto-generated method stub
		    mRgba =  Highgui.imread(Environment.getExternalStorageDirectory().getPath()+"/Studio3D/img_full.jpg");	
			disp = new Mat();
		
		    	
		} 
	 
	 
		class ComputeDisparityDialog extends AsyncTask<String, Void, String> {

			@Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
				
				runOnUiThread(new Runnable() {
				     public void run() {
				    	 conversionProgress.setMessage("Processing Image - Computing Disparity ");
							
							
				//stuff that updates ui

				    }
				});
				getDisparity(Structs.mRgba.getNativeObjAddr(),Structs.mDisparity.getNativeObjAddr());
				

				
			
				return null;
			}
		
			@Override
			protected void onPreExecute() {
			//	conversionProgress.setTitle("Processing Image");
				conversionProgress
						.setMessage("Please wait while we process your image ...");
				conversionProgress.show();
				
				initializeMats();
			}
			
			@Override
			protected void onPostExecute(String result) 
				{

				conversionProgress.dismiss();
			
			}

		}
		 public native void getDisparity(long matAddrRgba, long matAddrfinalImage);
		
}
