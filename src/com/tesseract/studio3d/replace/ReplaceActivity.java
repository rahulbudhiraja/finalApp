package com.tesseract.studio3d.replace;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import com.tesseract.studio3d.R;
import com.tesseract.studio3d.Animation.PhotoActivity;
import com.tesseract.studio3d.R.string;
import com.tesseract.studio3d.utils.Structs;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ReplaceActivity extends Activity
{

	Button replaceButton;
	private String selectedImagePath;
	public Mat foreground,background;
	public Mat finalImage;
	RelativeLayout mainLayout;
	LoaderImageView customView;
	 
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		
	    customView=new LoaderImageView(this);
	   
	    mainLayout=new RelativeLayout(this);
	    //mainLayout.setBackgroundColor(Color.BLACK);
	    
	    RelativeLayout.LayoutParams llayoutParams = new RelativeLayout.LayoutParams(
	    		RelativeLayout.LayoutParams.WRAP_CONTENT,
	    		RelativeLayout.LayoutParams.WRAP_CONTENT);
	    //mainLayout=(RelativeLayout)findViewById(R.id.replacelayout);
	    mainLayout.setLayoutParams(llayoutParams);
	    mainLayout.addView(customView);
	    
	   // replaceButton=(Button)findViewById(R.id.browsetoReplace);
//	    replaceButton.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				 
//				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//			    intent.setType("image/*");
//			    startActivityForResult(intent, 1337);
//			  
//			}
//	    	
//	    });
	    
	   
	    setContentView(mainLayout);
		
	    initializeMats();
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
             
             getThreshold(Structs.mRgba.getNativeObjAddr(), Structs.mDisparity.getNativeObjAddr(), finalImage.getNativeObjAddr(), background.getNativeObjAddr(),foreground.getNativeObjAddr(),0,0,6,selectedImagePath);
             
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
	
	public native void getThreshold(long matAddrRgba, long matAddrDisp, long matAddrfinalImage,long matAddrBackground, long matAddrForeground, int ji1, int ji2,int choice,String imgPath);
	
}
