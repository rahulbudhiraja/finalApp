package com.tesseract.studio3d.replace;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.tesseract.studio3d.utils.Structs;

public class FullScreenLayout extends ImageView {
	
	Paint paint;
	Bitmap tempBitmap;
	static Bitmap leftImg,imgMask,leftImgModified;
//	Bitmap imgMask;
	Xfermode xfermode[]={new PorterDuffXfermode(PorterDuff.Mode.DST_IN),new PorterDuffXfermode(PorterDuff.Mode.DST_OVER),new PorterDuffXfermode(PorterDuff.Mode.SRC_IN),new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)};
	static Canvas BitmapCanvas,leftImgCanvas;
	static Paint p;
	
	  public FullScreenLayout(Context context,Bitmap selectedBitmap) 
	  {
	        super(context);
	        configurePaint();
	        //tempBitmap=selectedBitmap;
//	        tempBitmap = Bitmap.createBitmap(selectedBitmap, 0, 0, selectedBitmap.getWidth(),selectedBitmap.getHeight(),null, false);

	        tempBitmap =Bitmap.createScaledBitmap(selectedBitmap, 960, 540, true);
	       
	        Mat mLeft = new Mat();

	        Imgproc.cvtColor(Structs.mLeft, mLeft,  Imgproc.COLOR_BGR2RGBA);
	        
	        leftImg=Bitmap.createBitmap(Structs.mLeft.cols(),Structs.mLeft.rows(), 
           		 Bitmap.Config.ARGB_8888);

            Utils.matToBitmap(Structs.mLeft, leftImg);
          
            Log.d("Full screen layout"," width" +leftImg.getWidth());
	        
            leftImgModified=leftImg;
            
            createBitmap(960,540);
	        
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
	        paint.setStyle(Paint.Style.FILL);
	        
	        paint.setAntiAlias(true);  
	        	        
	    }
	  
  public void onDraw(Canvas canvas)
  {
  	super.onDraw(canvas);
 	//canvas.drawBitmap(tempBitmap, 0, 0,paint);
 	    
   
 // draw the src/dst example into our offscreen bitmap
  	// canvas.drawBitmap(tempBitmap, 0, 0,paint);   // dest 
  
  	canvas.drawBitmap(leftImg, 0, 0, paint);
  	paint.setXfermode(xfermode[0]);
  	canvas.drawBitmap(imgMask, 0, 0, paint); // src 
  	
//    paint.setXfermode(xfermode[1]);
//    canvas.drawBitmap(tempBitmap, 0, 0,paint);
//    
 paint.setXfermode(null);
 
  	invalidate();
  	canvas.save();
  }
  
  static void createBitmap(int w,int h)
  {
	  imgMask = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
      BitmapCanvas = new Canvas(imgMask);
   
      BitmapCanvas.drawColor(0, Mode.CLEAR);
      p = new Paint(Paint.ANTI_ALIAS_FLAG);
      
      BitmapCanvas.drawCircle(300, 320,100,p);
      
    
	  
  }
  
  public boolean onTouchEvent(MotionEvent event) 
  {
	   // TODO Auto-generated method stub
	   
	Log.d("X = "+event.getX(),"Y = "+event.getY());
	
	BitmapCanvas.drawCircle(event.getX(),event.getY(),20,p);
	//applyMasktoBitmap();
	invalidate();
	return true;
  }
  
  public void applyMasktoBitmap()
  {
	    leftImgModified=leftImg;
	    leftImgCanvas=new Canvas(leftImgModified);
	    
	    paint.setXfermode(xfermode[0]);
	    leftImgCanvas.drawBitmap(imgMask, 0, 0, paint);
      invalidate();
      

  }
  
  
  
  
  
  
}
