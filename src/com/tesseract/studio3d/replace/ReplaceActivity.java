package com.tesseract.studio3d.replace;

import java.io.IOException;
import java.io.InputStream;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.tesseract.studio3d.R;
import com.tesseract.studio3d.utils.Structs;

public class ReplaceActivity extends Activity
{

	Button replaceButton;
	private String selectedImagePath;
	public Mat foreground,background;
	public Mat finalImage;
	RelativeLayout tempLayout;
	LinearLayout parentLayout;
	LoaderImageView customView;
	int startingIndex=111222,totalPlaces;
	ScrollView mainScrollView;
	String[] placesArray;
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
	    tempLayout=new RelativeLayout(this);
	    tempLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	    
	    parentLayout=new LinearLayout(this);
	    parentLayout.setOrientation(LinearLayout.VERTICAL);
	    
		mainScrollView=new ScrollView(this);
		mainScrollView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mainScrollView.addView(parentLayout);
		
		addPlacesCards();
		 
	    setContentView(mainScrollView);
		
	    initializeMats();
	    
	    backgroundThread.start();
	   
	}
	
	private boolean listAssetFiles(String path) {

	    String [] list;
	    try {
	        list = getAssets().list(path);
	        if (list.length > 0) {
	            // This is a folder
	            for (String file : list) {
	                if (!listAssetFiles(path + "/" + file))
	                    return false;
	            }
	        } else {
	            // This is a file
	            // TODO: add file name to an array list
	    }
	        } catch (IOException e) {
	        return false;
	    }

	    return true; 
	} 
	
	public  void initializeMats() {
		// TODO Auto-generated method stub
	    foreground=new Mat();
		background=new Mat();
		finalImage=new Mat();
    
	    	
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
	    super.onActivityResult(requestCode, resultCode, data);
	    
	    switch(requestCode)
	    {
	   
	     case 1337:
	    	 
	    	 Uri selectedImageUri = data.getData();
             selectedImagePath = getPath(selectedImageUri);
             
             Log.d("ReplaceActivity","Path: "+selectedImagePath);
             
//             getThreshold(Structs.mRgba.getNativeObjAddr(), Structs.mDisparity.getNativeObjAddr(), finalImage.getNativeObjAddr(), background.getNativeObjAddr(),foreground.getNativeObjAddr(),0,0,6,selectedImagePath);
             
	    }
	    
	}
  
	public String getPath(Uri uri) 
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
       
        return cursor.getString(column_index);
    }
	 
	static 
	{
		    if (!OpenCVLoader.initDebug()) {
		        // Handle initialization error
		    	Log.d("error","error");}
		    	else  System.loadLibrary("depth_magic");
			    
		    
	}
	
	public void addPlacesCards()
	{
		/// Open the Places folder in the image directory.
		totalPlaces=0;
		RelativeLayout.LayoutParams customLayoutParams;
		
		try {
			totalPlaces=getAssets().list("images/Places").length;
			Log.d("List","number: "+getAssets().list("images/Places").length);
			
			placesArray=getAssets().list("images/Places");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		/// Count the number of files and create that many image views ...
		for(int i=0;i<totalPlaces/2;i++)
		{
			
			Log.d("File names","name"+placesArray[i]);
			
		    tempLayout=new RelativeLayout(this);
		    tempLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			
			
			  customView=new LoaderImageView(this);
			  customView.setBackgroundResource(R.drawable.border);
			  customView.setId(startingIndex+i);
			  
			  customLayoutParams= new RelativeLayout.LayoutParams(
			    		RelativeLayout.LayoutParams.WRAP_CONTENT,
			    		RelativeLayout.LayoutParams.WRAP_CONTENT);
			  
			  customView.setLayoutParams(customLayoutParams);
			  tempLayout.addView(customView);

			  customView=new LoaderImageView(this);
			  customView.setBackgroundResource(R.drawable.border);
			  customView.setId(startingIndex+i+1);
			  
			  customLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			  
			  tempLayout.addView(customView);
			  tempLayout.invalidate();
			  parentLayout.addView(tempLayout);
			  
		}
		
		// Start loading the modified images in a thread and keep updating the layout ..
		
	}	
		
	Thread backgroundThread =new Thread()
	{
		
	 public void run()
	 {
		 for (int i=0;i<totalPlaces;i++)
		 {
			 try {
				InputStream is=getAssets().open("images/Places/"+placesArray[i]);
				// Get bitmap...
				
				Bitmap Img=BitmapFactory.decodeStream(is);
				
				Mat imgToLoad=new Mat();
				Utils.bitmapToMat(Img, imgToLoad);
				
				
				Log.d("ReplaceActivity","val:  "+imgToLoad.rows()+"  "+imgToLoad.cols());
				// Take this mat and pass it to getThresholdFunction
				getThreshold(Structs.mRgba.getNativeObjAddr(), Structs.mDisparity.getNativeObjAddr(), finalImage.getNativeObjAddr(), background.getNativeObjAddr(),foreground.getNativeObjAddr(),0,0,6,imgToLoad.getNativeObjAddr());
				
				// Get recomputed bitmap 
				
				Imgproc.cvtColor(imgToLoad, imgToLoad, Imgproc.COLOR_BGR2RGBA);
	            
				Bitmap tempBitmap=Bitmap.createBitmap(background.cols(),background.rows(), 
	            		 Bitmap.Config.ARGB_8888);
	             
	            Utils.matToBitmap(background, tempBitmap);
	             
	             
				
				
				// Update the UI and assign the bitmap tp cusomview  
				customView=(LoaderImageView) findViewById(startingIndex+i);
				
				customView.setImage(tempBitmap);
				// invalidate ;;
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
			 
			 
		 }
	 }
	};
	
	
//	public native void getThreshold(long matAddrRgba, long matAddrDisp, long matAddrfinalImage,long matAddrBackground, long matAddrForeground, int ji1, int ji2,int choice,String imgPath);
	public native void getThreshold(long matAddrRgba, long matAddrDisp, long matAddrfinalImage,long matAddrBackground, long matAddrForeground, int ji1, int ji2,int choice,long LoadedImageMat);
	
}
