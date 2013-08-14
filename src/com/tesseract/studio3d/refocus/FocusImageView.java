package com.tesseract.studio3d.refocus;

import org.opencv.core.Mat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.tesseract.studio3d.utils.Structs;

public class FocusImageView extends ImageView {

	int circleCount=0;
	Bitmap leftImg;
	Paint paint;
	private int converted_ycoord,converted_xcoord;
	public Mat finalImage;
	public Mat limg;	
	public Mat foreground,background;
	boolean drawCircles;
	
	  public FocusImageView(Context context) 
	  {
	        super(context);
	        configurePaint();
			
			leftImg=BitmapFactory.decodeFile(Structs.left_img_path);
			finalImage=new Mat();
			Log.d("Imageview","Image Loaded");
			
	  }
	  
	
	  public void configurePaint()
	    {
	    	paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			
	        paint.setColor(Color.WHITE);                    // set the color
	        paint.setStrokeWidth(3);               // set the size
	        paint.setDither(true);                    // set the dither to true
	        paint.setStyle(Paint.Style.STROKE);       // set to STOKE
	      //  paint.setStrokeJoin(Paint.Join.ROUND);    // set the join to round you want
	        paint.setStrokeCap(Paint.Cap.ROUND);      // set the paint cap to round too
	      
	        paint.setAntiAlias(true);  
	        
	        
	       
	        
	    }
    public void onDraw(Canvas canvas)
    {
    	super.onDraw(canvas);
    	canvas.drawBitmap(leftImg, 0, 0,paint);
    	
    	if(drawCircles)
    	{
    		canvas.drawCircle(converted_xcoord, converted_ycoord, 20, paint); 
    		canvas.drawCircle(converted_xcoord, converted_ycoord, 40, paint);
    		canvas.drawCircle(converted_xcoord, converted_ycoord, 60, paint);
    	}
    	
    	
    }
	
    public boolean onTouchEvent(MotionEvent event) {
 	   // TODO Auto-generated method stub
 	   
 	Log.d("X = "+event.getX(),"Y = "+event.getY());
 	converted_xcoord=(int) event.getX();
 	converted_ycoord=(int) event.getY();
 	
 	drawCircles=true;
 	
 	new ProgressDialogClass().execute("");
 	invalidate();   
 	return true; //processed
 	  }

	class ProgressDialogClass extends AsyncTask<String, Void, String> {

		

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			
			
			Refocus(Structs.mRgba.getNativeObjAddr(), Structs.mDisparity.getNativeObjAddr(), finalImage.getNativeObjAddr(), (int)converted_xcoord, (int)converted_ycoord);
			
			return null;
		}
		
		
		protected void onPostExecute(String result) {

//			conversionProgress.dismiss();

//			// startanimation ..
//			//PointsImageView.startAnimation(true);
//			Intent camera_intent=new Intent(mContext,AnimationActivity.class);
//		    mContext.startActivity(camera_intent);
//			
			
			Log.d("done", "done");

			drawCircles=false;	
			
			
			// might want to change "executed" for the returned string passed
			// into onPostExecute() but that is upto you
		}

		@Override
		protected void onPreExecute() {
		//	conversionProgress.setTitle("Processing Image");
//			conversionProgress
//					.setMessage("Please wait while we process your image ...");
//			conversionProgress.show();
			//invalidate();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}
	
	 
	   
	 static 
	 {
			System.loadLibrary("depth_magic");
	 }
	 
	 public native void Refocus(long matAddrRgba, long matAddrDisp, long matAddrfinalImage, int ji1, int ji2);
		
	
}
