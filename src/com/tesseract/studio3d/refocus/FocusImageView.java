package com.tesseract.studio3d.refocus;

import java.util.Timer;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.tesseract.studio3d.utils.Structs;

public class FocusImageView extends ImageView {

	int circleCount=599; // start with a number that is %4 =3 ..so that 3 circles will be drawn first,then 2 etc ...
	Bitmap leftImg;
	Paint paint;
	private int converted_ycoord,converted_xcoord;
	public Mat finalImage,finalImageRGBA;
	public Mat limg;	
	public Mat foreground,background;
	boolean drawCircles;
	Context mContext;

	Handler mHandler;
	Timer circleUpdater;
	long timePassed=0;
	Bitmap tempBitmap;
	long startTime=0;
	boolean cancelThread=false;

	  public FocusImageView(Context context) 
	  {
	        super(context);
	        configurePaint();
			
			leftImg=BitmapFactory.decodeFile(Structs.left_img_path);
			finalImage=new Mat();
			Log.d("Imageview","Image Loaded");
			mContext=context;
			
			circleUpdater=new Timer();
			
			finalImageRGBA=new Mat();
			
			finalImage=Structs.mLeft.clone();
			Imgproc.cvtColor(finalImage, finalImageRGBA, Imgproc.COLOR_BGR2RGBA);
			
			tempBitmap=Bitmap.createBitmap(finalImageRGBA.cols(),finalImageRGBA.rows(), 
	         		 Bitmap.Config.ARGB_8888);
	        
			Utils.matToBitmap(finalImageRGBA,tempBitmap);
	
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
    	canvas.drawBitmap(tempBitmap, 0, 0,paint);

    	if(drawCircles)
    	for(int i=0;i<=circleCount%4;i++)
    		canvas.drawCircle(converted_xcoord, converted_ycoord, 20*i, paint);
    	
    	canvas.save();
    }
	
    public boolean onTouchEvent(MotionEvent event) {
 	   // TODO Auto-generated method stub
 	   
 	Log.d("X = "+event.getX(),"Y = "+event.getY());
 	
 	if(!drawCircles)
 	{
 		converted_xcoord=(int) event.getX();
 	 	converted_ycoord=(int) event.getY();
 	 	
 		drawCircles=true;
 	
 	new ProgressDialogClass().execute("");
 	invalidate();   
 	
 	}
 	return true; //processed
 	}

	class ProgressDialogClass extends AsyncTask<String, Void, String> {
	
		

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			Refocus(Structs.mRgba.getNativeObjAddr(), Structs.mDisparity.getNativeObjAddr(), finalImage.getNativeObjAddr(), (int)converted_xcoord, (int)converted_ycoord);
		 
			if(cancelThread)
				this.cancel(true);
			//
			return null;
		}
		
		
		protected void onPostExecute(String result) {
		
			Log.d("done", "done");

			drawCircles=false;	
			circleCount=599;
			
			// circleCount is set at 3 ,so we start drawing 3 circles then 
			
			mHandler.removeCallbacksAndMessages(null);
			
			tempBitmap=Bitmap.createBitmap(finalImageRGBA.cols(),finalImageRGBA.rows(), 
	         		 Bitmap.Config.ARGB_8888);
			
			//finalImageRGBA=finalImage.clone();
			Imgproc.cvtColor(finalImage, finalImageRGBA, Imgproc.COLOR_BGR2RGBA);
			Utils.matToBitmap(finalImageRGBA,tempBitmap);
			invalidate();
			
			Structs.finalImgRgba=finalImageRGBA.clone();
			
			// might want to change "executed" for the returned string passed
			// into onPostExecute() but that is upto you
		}

		@Override
		protected void onPreExecute() {

			circleCount=599;
			mHandler=new Handler();
			mHandler.postDelayed(r, 500);
			startTime=System.currentTimeMillis();
			cancelThread=false;
			
			invalidate();
		}

		@Override
		protected void onProgressUpdate(Void... values) 
		{
			
		}
		
		final Runnable r = new Runnable()
		{
		    public void run() 
		    {
		    	
		    	if(System.currentTimeMillis()-startTime>10000)
		    	   cancelThread=true;
		    	
		    	if(System.currentTimeMillis()-timePassed>500)
		    	{
			    	timePassed=System.currentTimeMillis();
			        circleCount--;
			       
		    	}
		    	 mHandler.postDelayed(this, 500);
		        invalidate();
		        Log.d("Running !","running: "+circleCount);
		        
		    }
		};
	}
	
	 
	   
	 static 
	 {
			System.loadLibrary("depth_magic");
	 }
	 
	 public native void Refocus(long matAddrRgba, long matAddrDisp, long matAddrfinalImage, int ji1, int ji2);
		
	 
	 
	
}
