package com.tesseract.studio3d.replace;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.tesseract.studio3d.menu.MenuActivity;
import com.tesseract.studio3d.utils.Structs;

public class ReplaceActivity extends MenuActivity
{

	Button replaceButton;
	private String selectedImagePath;
	public Mat foreground,background;
	public Mat finalImage;
	RelativeLayout tempLayout;
	LinearLayout parentLayout;
	LoaderImageView customView;
	int startingIndex=111222,totalPlaces,positionIndex;
	ScrollView mainScrollView;
	
	public static String[] placesArray;
	String subFolder;
	
	Bitmap Img,foregroundImg;
	
	public static  Vector<Bitmap>ImageList;
	
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
	    parentLayout.setBackgroundColor(Color.BLACK);

		mainScrollView=new ScrollView(this);
		mainScrollView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mainScrollView.addView(parentLayout);
		
		Intent i = getIntent();
		
		subFolder=i.getExtras().getString("Category");
		Log.d("ReplaceActivity","Sub folder: "+subFolder);
		
		loadCategoryImages();
		
		addPlacesCards();
		 
	    setContentView(mainScrollView);
		
	    initializeMats();
	    
	    backgroundThread.start();
	   
	}
	
	public void loadCategoryImages()
	{
		 ImageList=new Vector<Bitmap>();
		 	try {
		 		
					placesArray=getAssets().list("images/Places");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		 	
		 	foregroundImg=BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()+"/Studio3D/Layers/img_fg.png");
		 	
		 	for (int i=0;i<placesArray.length;i++)
				 {
		 	
					try {
						InputStream is ;
						is = getAssets().open("images/Places/"+placesArray[i]);
						
						Img=BitmapFactory.decodeStream(is);
			    		Img=Bitmap.createScaledBitmap(Img, foregroundImg.getWidth(),foregroundImg.getHeight(), true);
			    		
			    		Canvas img=new Canvas(Img);
			    		img.drawBitmap(Img,0, 0, new Paint());
//			    		img.drawBitmap(foregroundImg,0,0,new Paint());
			    		
						Log.d("Tag",placesArray[i]);
			    		ImageList.add(Img);
			    		Img=null;
					
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			 
				 }
			 
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
			totalPlaces=getAssets().list("images/"+subFolder).length;
			Log.d("List","number: "+getAssets().list("images/"+subFolder).length);
			
			placesArray=getAssets().list("images/"+subFolder);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		/// Count the number of files and create that many image views ...
		for(int i=0;i<totalPlaces;i++)
		{
			
			Log.d("File names","name"+placesArray[i]);
			
		    
		    customLayoutParams= new RelativeLayout.LayoutParams(
		    		RelativeLayout.LayoutParams.WRAP_CONTENT,
		    		RelativeLayout.LayoutParams.WRAP_CONTENT);
		  
		    customLayoutParams.setMargins(3,3,3,3);
		    
		    if(i%2==0)
		    {
		      tempLayout=new RelativeLayout(this);
			  
//			  RelativeLayout.LayoutParams rlayoutParams = new RelativeLayout.LayoutParams(
//			    		RelativeLayout.LayoutParams.MATCH_PARENT, 
//			    		RelativeLayout.LayoutParams.WRAP_CONTENT);
//			    
		      
			  tempLayout.setLayoutParams(customLayoutParams);
			    
			  customView=new LoaderImageView(this);
			  customView.setBackgroundResource(R.drawable.border);
			  
			  customView.setId(startingIndex+i);
			 
			  customView.setLayoutParams(customLayoutParams);
			  
//			  customView.setLayoutParams(customLayoutParams);
			  tempLayout.addView(customView);
			  parentLayout.addView(tempLayout);
			  
		    }
		    
		    else
		    {
			  customView=new LoaderImageView(this);
			  customView.setBackgroundResource(R.drawable.border);
			  customView.setId(startingIndex+i);
			  
			  customLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			  customView.setLayoutParams(customLayoutParams);
			  tempLayout.addView(customView);
		    }
			  
			  tempLayout.invalidate();
			 
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
				InputStream is=getAssets().open("images/"+subFolder+"/"+placesArray[i]);
				// Get bitmap...
				
				Bitmap Img=BitmapFactory.decodeStream(is);
				
				Mat imgToLoad=new Mat();
				Utils.bitmapToMat(Img, imgToLoad);
				
				Imgproc.cvtColor(imgToLoad, imgToLoad, Imgproc.COLOR_RGBA2BGR);
		           
				Log.d("ReplaceActivity","val:  "+imgToLoad.cols()+"  "+imgToLoad.rows());
				Log.d("File name","i value:  "+i+"place :"+ placesArray[i]);
				
				Log.d("Sizes - -  - -","Structure sizes ;"+Structs.mRgba.cols()+"  "+Structs.mDisparity.cols()+"  "+finalImage.cols()+"    "+background.cols()+"    "+imgToLoad.cols()+"   "+foreground.cols());
				// Take this mat and pass it to getThresholdFunction
				getThreshold(Structs.mRgba.getNativeObjAddr(), Structs.mDisparity.getNativeObjAddr(), finalImage.getNativeObjAddr(), background.getNativeObjAddr(),foreground.getNativeObjAddr(),0,0,6,imgToLoad.getNativeObjAddr());
				
				// Get recomputed bitmap 
				Size dimensions = new Size();
				dimensions.width=940/2;
				dimensions.height=510/2;
				
				Imgproc.cvtColor(finalImage, finalImage, Imgproc.COLOR_BGR2RGBA);
	            
				Imgproc.resize(finalImage, finalImage,dimensions);
				final Bitmap tempBitmap=Bitmap.createBitmap(finalImage.cols(),finalImage.rows(), 
	            		 Bitmap.Config.ARGB_8888);
	             
	            Utils.matToBitmap(finalImage, tempBitmap);
	  
				// Update the UI and assign the bitmap to customview  
				customView=(LoaderImageView) findViewById(startingIndex+i);
				
				
				positionIndex=i;
				
				if(customView!=null)
				{
				runOnUiThread(new Runnable() {
				     public void run() {
				    	 customView.setImage(tempBitmap);
				    	 customView.setPosition(positionIndex);
					   //stuff that updates ui

				    }
				});
				
				}
				
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
