package com.tesseract.studio3d.manualEdit;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import ColorFilters.ApplyFilterstoLayer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.tesseract.studio3d.CustomFileObserver;
import com.tesseract.studio3d.R;
import com.tesseract.studio3d.Animation.AnimationActivity;
import com.tesseract.studio3d.Animation.PointsImageView;

import com.tesseract.studio3d.utils.Structs;


public class Panel extends SurfaceView implements SurfaceHolder.Callback{
	private CanvasThread canvasthread;
	
	//Zoom & pan touch event
     int y_old=0,y_new=0;int zoomMode=0;
     float pinch_dist_old=0,pinch_dist_new=0;
     int zoomControllerScale=1;//new and old pinch distance to determine Zoom scale
        // These matrices will be used to move and zoom image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // Remember some things for zooming
        PointF start = new PointF();
        PointF mid = new PointF();
        float oldDist = 1f;
 
        // We can be in one of these 3 states
    static final int NONE = 0;
    static final int PAN = 1;
    static final int ZOOM = 2;
    int mode = NONE;
    
    Paint bluePaint,redPaint,transparentPaint;
    Bitmap leftImgBitmap;
    Bitmap overlayBitmap,BlueCirclesBmp,RedCirclesBmp;
    Canvas overlayBlueCanvas,overlayRedCanvas,combinedOverlayCanvas;

     private static final String TAG = "Debug Tag";
     
     float[] dst;
     float[] src;
     float[] matrixValues;
     
     float bmpWidth,bmpHeight;
     int originalbmpWidth,originalbmpHeight;
     float newDist;
     
     int toClear=2;
     float circleRadius=60;
      
     String[] imageFilters = { "sepia", "stark", "sunnyside", "cool", "worn",
 			"grayscale","vignette","crush","sunny","night" };
     
     static String bg_filter,fg_filter;
 	 Paint layer1Paint,layer2Paint;
     
 	 /* Variables used for Loading the image masks */

 	 Bitmap final_mask_fg,final_mask_bg;
 	 Canvas mask_fgCanvas,mask_bgCanvas;
 	 
   /***** mask stuff **/
     
     Bitmap mask,mask2;

	private Paint maskPaint,borderPaint;

	 /** OpenCV stuff **/
	
	Mat fgMat,bgMat;
	Mat converted_fgMat,converted_bgMat;
	Mat temporary_ones,fg_bandw_mask,bg_bandw_mask;
     
	Mat fg_gray,bg_gray;

	Vector<Mat> rgbaMats_fg,rgbaMats_bg;
	Mat fg_alpha,bg_alpha;
	Mat leftImgMat;
	
	Context activityContext;
	Boolean exitPressed;
	
	ProgressDialog conversionProgress;
 	 
     /** Old ,old old ,this will be used if the surfaceview is define in the xml file ..*/

	 public Panel(Context context,String filter1,String filter2) 
	 {
		   super(context);
		   activityContext=context;
		   exitPressed=false;
		   
		    getHolder().addCallback(this);
		    canvasthread = new CanvasThread(getHolder(), this);
		    setFocusable(true);
		    
		    bluePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		    bluePaint.setColor(Color.BLUE);
		    bluePaint.setStyle(Paint.Style.FILL);  
		    bluePaint.setStrokeWidth(-1);
		    
		    
		    redPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		    redPaint.setColor(Color.RED);
		    redPaint.setStyle(Paint.Style.FILL);  
		    
		    redPaint.setStrokeWidth(-1);
		    
		    borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		    borderPaint.setColor(Color.YELLOW);
		    borderPaint.setStyle(Paint.Style.STROKE);  
		    
		    borderPaint.setStrokeWidth(5);
		    
		    
		    transparentPaint=	new Paint(Paint.ANTI_ALIAS_FLAG);
		    transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		    transparentPaint.setStrokeWidth(-1);
		    transparentPaint.setStyle(Paint.Style.FILL); 
    
			leftImgBitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()+"/Studio3D/img_left.jpg");
			leftImgBitmap=Bitmap.createScaledBitmap(leftImgBitmap,960,540, true);
			
			originalbmpWidth=leftImgBitmap.getWidth();
			originalbmpHeight=leftImgBitmap.getHeight();
			
			BlueCirclesBmp=Bitmap.createBitmap(leftImgBitmap.getWidth(),leftImgBitmap.getHeight(),Bitmap.Config.ARGB_8888);
			RedCirclesBmp=Bitmap.createBitmap(leftImgBitmap.getWidth(),leftImgBitmap.getHeight(),Bitmap.Config.ARGB_8888);
			
			overlayBlueCanvas=new Canvas(BlueCirclesBmp);
			overlayRedCanvas=new Canvas(RedCirclesBmp);
			
			overlayBitmap=Bitmap.createBitmap(leftImgBitmap.getWidth(),leftImgBitmap.getHeight(),Bitmap.Config.ARGB_8888);
			combinedOverlayCanvas=new Canvas(overlayBitmap);
			
			final_mask_fg=Bitmap.createBitmap(leftImgBitmap.getWidth(),leftImgBitmap.getHeight(),Bitmap.Config.ARGB_8888);
			final_mask_bg=Bitmap.createBitmap(leftImgBitmap.getWidth(),leftImgBitmap.getHeight(),Bitmap.Config.ARGB_8888);
			
			
			redPaint.setAlpha(130);
			bluePaint.setAlpha(130);
			   
			
			fg_filter=filter1;
			bg_filter=filter2;
			
			layer1Paint=findPaint(fg_filter);
			layer2Paint=findPaint(bg_filter);
			
			
			dst = new float[2];
			dst[0]=0;
			dst[1]=0;
			
			src=new float[2];
			src[0]=0;
			src[1]=0;
			
			matrixValues=new float[9];
			
			/** Mask stuff ...*/
			
			mask=BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/Studio3D/img_mask_fg.png");
			mask2=BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/Studio3D/img_mask_bg.png");	
			
			Log.d("Size",mask2.getWidth()+"  h="+mask2.getHeight());
			
			float[] alphaSrc = 
				{
				    0, 0, 0, 0, 255,
				    0, 0, 0, 0, 255,
				    0, 0, 0, 0, 255,
				    1, 1, 1, -1, 0,
				};
			
			ColorMatrix cm = new ColorMatrix(alphaSrc);
			ColorMatrixColorFilter filter = new ColorMatrixColorFilter(cm);
			
			maskPaint = new Paint();
			maskPaint.setColorFilter(filter);
			maskPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));

			overlayBlueCanvas.drawRect(new Rect(0,0,overlayBitmap.getWidth(),overlayBitmap.getHeight()), bluePaint);
			overlayRedCanvas.drawRect(new Rect(0,0,overlayBitmap.getWidth(),overlayBitmap.getHeight()), redPaint);
			
			overlayBlueCanvas.drawBitmap(BlueCirclesBmp, 0, 0, bluePaint);
			overlayBlueCanvas.drawBitmap(mask, 0, 0, maskPaint);
			
			overlayRedCanvas.drawBitmap(RedCirclesBmp, 0, 0, redPaint);
			overlayRedCanvas.drawBitmap(mask2, 0, 0, maskPaint);

			conversionProgress = new ProgressDialog(activityContext);
			
	 }
	 
	 @Override
	 
     public boolean onTouchEvent(MotionEvent event){
           
		  	 PanZoomWithTouch(event);
       
             invalidate();//necessary to repaint the canvas
             return true;
  }

	@Override
	public void onDraw(Canvas canvas) 
	{
		
		/* Works well */ 

		
		canvas.drawColor(Color.BLACK);
		if(!exitPressed)
			{
			
			
			canvas.drawBitmap(leftImgBitmap,matrix,null);
		    canvas.drawBitmap(BlueCirclesBmp,matrix,bluePaint);
		    canvas.drawBitmap(RedCirclesBmp,matrix,redPaint);
		    canvas.save();
		    
		    canvas.setMatrix(matrix);
		    canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), borderPaint);
		    canvas.restore();
			}
		    /* Draw a border */

        /* End */
		
		/* One way of working */
//		combinedOverlayCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//		combinedOverlayCanvas.drawBitmap(leftImgBitmap,matrix,null);
//
//		combinedOverlayCanvas.drawBitmap(BlueCirclesBmp,matrix,bluePaint);
//		combinedOverlayCanvas.drawBitmap(RedCirclesBmp,matrix,redPaint);
//
//		    combinedOverlayCanvas.save();
//		    combinedOverlayCanvas.setMatrix(matrix);
//		    combinedOverlayCanvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), borderPaint);
//		    combinedOverlayCanvas.restore();
		    
//		canvas.drawColor(Color.BLACK);
//	    canvas.drawBitmap(overlayBitmap, new Matrix(), null);
		    
		//canvas.drawCircle(src[0], src[1], 30, bluePaint);
	}
	
	public void Render(Canvas canvas) 
	{
		//Log.d("ondraw", "lefutott");
		
		 
			canvas.drawColor(Color.BLACK);
		
			canvas.drawBitmap(leftImgBitmap,matrix,null);
		    canvas.drawBitmap(BlueCirclesBmp,matrix,layer1Paint);
		    canvas.drawBitmap(RedCirclesBmp,matrix,layer2Paint);
		    
	}
	
	 
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
	    
		canvasthread.setRunning(true);
	    canvasthread.start();
	    
	    Log.d(TAG,"Dimensions of the image "+leftImgBitmap.getWidth()+"  "+leftImgBitmap.getHeight());
		
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		boolean retry = true;
		canvasthread.setRunning(false);
		while (retry) {
			try {
				canvasthread.join();
				retry = false;
			} catch (InterruptedException e) {
				// we will try it again and again...
			}
		}

	}
	
	void PanZoomWithTouch(MotionEvent event){
		
		 if(CanvasActivity.activateViewMovement)
		 {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
     
     
        case MotionEvent.ACTION_DOWN://when first finger down, get first point
         savedMatrix.set(matrix);
         start.set(event.getX(), event.getY());
         Log.d(TAG, "mode=PAN");
         mode = PAN;
         break;
      case MotionEvent.ACTION_POINTER_DOWN://when 2nd finger down, get second point
         oldDist = spacing(event); 
         Log.d(TAG, "oldDist=" + oldDist);
         if (oldDist > 10f) {
            savedMatrix.set(matrix);
            midPoint(mid, event); //then get the mide point as centre for zoom
            mode = ZOOM;
            Log.d(TAG, "mode=ZOOM");
         }
         break;
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_POINTER_UP:       //when both fingers are released, do nothing
         mode = NONE;
         Log.d(TAG, "mode=NONE");
         break;
      case MotionEvent.ACTION_MOVE:     //when fingers are dragged, transform matrix for panning
         if (mode == PAN) {
            // ...
            matrix.set(savedMatrix);
            matrix.postTranslate(event.getX() - start.x,
                  event.getY() - start.y);
            Log.d(TAG,"Mapping rect");
                        //start.set(event.getX(), event.getY());
         }
         else if (mode == ZOOM) { //if pinch_zoom, calculate distance ratio for zoom
           newDist = spacing(event);
            Log.d(TAG, "newDist=" + newDist);
            if (newDist > 10f) {
               matrix.set(savedMatrix);
               float scale = newDist / oldDist;
               matrix.postScale(scale, scale, mid.x, mid.y);
            }
         }
         
         
         break;
        }
        
		 }
        
        src[0]=event.getX();
		src[1]=event.getY();
		

		matrix.invert(matrix); 
		matrix.mapPoints(dst, src);
		matrix.invert(matrix);
		
		matrix.getValues(matrixValues);
//		Log.d(TAG,"Matrix Scale Value : X =" +savedMatrix.MSCALE_X +"  Y =" +savedMatrix.MSCALE_Y );
//		Log.d(TAG,"Matrix Skew Value : X =" +savedMatrix.MSKEW_X +" Y =" +savedMatrix.MSKEW_Y);
//		Log.d(TAG,"Matrix Translate Value : X =" +matrix.MTRANS_X +" Y =" +matrix.MTRANS_Y);
		
		/*
		 * [Scale X, Skew X, Transform X
			Skew Y, Scale Y, Transform Y
			Perspective 0, Perspective 1, Perspective 2]
		 */
		
//		for(int i=0;i<matrixValues.length;i++)
//		{
//			
//			Log.d(TAG,"For i value ="+i+" the corresponding value is"+matrixValues[i]);
//		}
		
//		bmpWidth=originalbmpWidth*matrixValues[0];
//		bmpHeight=originalbmpHeight*matrixValues[4];
		
		//Log.d(TAG,"the modified bitmap width and height"+bmpWidth+"   "+ bmpHeight);
		
		
//	    overlayCanvas.drawCircle(event.getX()*matrixValues[0]+matrixValues[2],event.getY()*matrixValues[4]+matrixValues[5],30,bluePaint);
		
		circleRadius=40/matrixValues[0];
		
		if(CanvasActivity.activeLayer==1)
	    { 
		  overlayRedCanvas.drawCircle(dst[0],dst[1], circleRadius-1, transparentPaint);
		  overlayBlueCanvas.drawCircle(dst[0],dst[1], circleRadius, bluePaint);
	    }
		if(CanvasActivity.activeLayer==2)
	    {
		  overlayBlueCanvas.drawCircle(dst[0],dst[1], circleRadius-1, transparentPaint);
	      overlayRedCanvas.drawCircle(dst[0],dst[1], circleRadius, redPaint);
	    
	    }
		
//	    overlayCanvas.drawCircle(dst[0],dst[1], 30, redPaint);
			//
//	    Log.d(TAG,"the modified values are :"+(event.getX()-matrixValues[2])*matrixValues[0]+ "    "+(event.getY()/matrixValues[4]-matrixValues[5]));
//	    
//	    Log.d(TAG,"The Modified Touch Values based on the matrix are  :"+dst[0]+"   "+dst[1]);
	    
	 
		//overlayCanvas.drawCircle(event.getRawX()*matrixValues[0]+matrixValues[2],event.getRawY()*matrixValues[4]+matrixValues[5],30,bluePaint);
		//overlayCanvas.drawCircle(src[0], src[1], 40, bluePaint);
		
}

         /** Determine the space between the first two fingers */
   private float spacing(MotionEvent event) {
      // ...
      float x = event.getX(0) - event.getX(1);
      float y = event.getY(0) - event.getY(1);
      return FloatMath.sqrt(x * x + y * y);
   }

   /** Calculate the mid point of the first two fingers */
   private void midPoint(PointF point, MotionEvent event) {
      // ...
      float x = event.getX(0) + event.getX(1);
      float y = event.getY(0) + event.getY(1);
      point.set(x / 2, y / 2);
   }
   
   public void updateStates(){
	 //Dummy method() to handle the States from a blog ..
	 }
   
   
   
   /* Some methods for setting the paint */
   
	public Paint findPaint(String filterName)
	{
		
		ColorMatrix cm = new ColorMatrix();
		
		if (filterName.equalsIgnoreCase("stark")) {

			Paint spaint = new Paint();
			ColorMatrix scm = new ColorMatrix();

			scm.setSaturation(0);
			final float m[] = scm.getArray();
			final float c = 1;
			scm.set(new float[] { m[0] * c, m[1] * c, m[2] * c, m[3] * c,
					m[4] * c + 15, m[5] * c, m[6] * c, m[7] * c, m[8] * c,
					m[9] * c + 8, m[10] * c, m[11] * c, m[12] * c, m[13] * c,
					m[14] * c + 10, m[15], m[16], m[17], m[18], m[19] });

			spaint.setColorFilter(new ColorMatrixColorFilter(scm));
			Matrix smatrix = new Matrix();
//			imageViewCanvas.drawBitmap(modifiedBitmap, smatrix, spaint);

			cm.set(new float[] { 1, 0, 0, 0, -90, 0, 1, 0, 0, -90, 0, 0, 1, 0,
					-90, 0, 0, 0, 1, 0 });

		} else if (filterName.equalsIgnoreCase("sunnyside")) {

			cm.set(new float[] { 1, 0, 0, 0, 10, 0, 1, 0, 0, 10, 0, 0, 1, 0,
					-60, 0, 0, 0, 1, 0 });
		} else if (filterName.equalsIgnoreCase("worn")) {

			cm.set(new float[] { 1, 0, 0, 0, -60, 0, 1, 0, 0, -60, 0, 0, 1, 0,
					-90, 0, 0, 0, 1, 0 });
		} else if (filterName.equalsIgnoreCase("grayscale")) {

			float[] matrix = new float[] { 0.3f, 0.59f, 0.11f, 0, 0, 0.3f,
					0.59f, 0.11f, 0, 0, 0.3f, 0.59f, 0.11f, 0, 0, 0, 0, 0, 1,
					0, };

			cm.set(matrix);

		} else if (filterName.equalsIgnoreCase("cool")) {

			cm.set(new float[] { 1, 0, 0, 0, 10, 0, 1, 0, 0, 10, 0, 0, 1, 0,
					60, 0, 0, 0, 1, 0 });

		} else if (filterName.equalsIgnoreCase("filter0")) {

			cm.set(new float[] { 1, 0, 0, 0, 30, 0, 1, 0, 0, 10, 0, 0, 1, 0,
					20, 0, 0, 0, 1, 0 });

		} else if (filterName.equalsIgnoreCase("filter1")) {

			cm.set(new float[] { 1, 0, 0, 0, -33, 0, 1, 0, 0, -8, 0, 0, 1, 0,
					56, 0, 0, 0, 1, 0 });

		} else if (filterName.equalsIgnoreCase("night")) {

			cm.set(new float[] { 1, 0, 0, 0, -42, 0, 1, 0, 0, -5, 0, 0, 1, 0,
					-71, 0, 0, 0, 1, 0 });

		} else if (filterName.equalsIgnoreCase("crush")) {

			cm.set(new float[] { 1, 0, 0, 0, -68, 0, 1, 0, 0, -52, 0, 0, 1, 0,
					-15, 0, 0, 0, 1, 0 });

		} else if (filterName.equalsIgnoreCase("filter4")) {

			cm.set(new float[] { 1, 0, 0, 0, -24, 0, 1, 0, 0, 48, 0, 0, 1, 0,
					59, 0, 0, 0, 1, 0 });

		} else if (filterName.equalsIgnoreCase("sunny")) {

			cm.set(new float[] { 1, 0, 0, 0, 83, 0, 1, 0, 0, 45, 0, 0, 1, 0, 8,
					0, 0, 0, 1, 0 });

		} else if (filterName.equalsIgnoreCase("filter6")) {

			cm.set(new float[] { 1, 0, 0, 0, 80, 0, 1, 0, 0, 65, 0, 0, 1, 0,
					81, 0, 0, 0, 1, 0 });

		} else if (filterName.equalsIgnoreCase("filter7")) {

			cm.set(new float[] { 1, 0, 0, 0, -44, 0, 1, 0, 0, 38, 0, 0, 1, 0,
					46, 0, 0, 0, 1, 0 });

		} else if (filterName.equalsIgnoreCase("filter8")) {

			cm.set(new float[] { 1, 0, 0, 0, 84, 0, 1, 0, 0, 63, 0, 0, 1, 0,
					73, 0, 0, 0, 1, 0 });

		} else if (filterName.equalsIgnoreCase("random")) {

			// pick an integer between -90 and 90 apply
			int min = -90;
			int max = 90;
			Random rand = new Random();

			int five = rand.nextInt(max - min + 1) + min;

			int ten = rand.nextInt(max - min + 1) + min;
			int fifteen = rand.nextInt(max - min + 1) + min;

			Log.d(TAG, "five " + five);
			Log.d(TAG, "ten " + ten);
			Log.d(TAG, "fifteen " + fifteen);

			cm.set(new float[] { 1, 0, 0, 0, five, 0, 1, 0, 0, ten, 0, 0, 1, 0,
					fifteen, 0, 0, 0, 1, 0 });

		} else if (filterName.equalsIgnoreCase("sepia")) {

			float[] sepMat = { 0.3930000066757202f, 0.7689999938011169f,
					0.1889999955892563f, 0, 0, 0.3490000069141388f,
					0.6859999895095825f, 0.1679999977350235f, 0, 0,
					0.2720000147819519f, 0.5339999794960022f,
					0.1309999972581863f, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1 };
			cm.set(sepMat);
		}
		else if(filterName.equalsIgnoreCase("vignette"))
		{

			Bitmap border = null;
			Bitmap scaledBorder = null;

			border = BitmapFactory.decodeResource(this.getResources(), R.drawable.vignette);

//			scaledBorder = Bitmap.createScaledBitmap(border,width,height, false);

			}
		
		Paint localpaint = new Paint(Paint.ANTI_ALIAS_FLAG);

		localpaint.setColorFilter(new ColorMatrixColorFilter(cm));
		localpaint.setStrokeWidth(0);
		
		localpaint.setStyle(Style.FILL);
		
		
		return localpaint;
		
		
	}
	public void initializeMats()
	{
		
		fgMat =new Mat();
		bgMat=new Mat();
		converted_fgMat=new Mat();
		converted_bgMat=new Mat();
		fg_gray=new Mat();
		bg_gray=new Mat();
		
		fg_alpha=new Mat();
		bg_alpha=new Mat();
		
		rgbaMats_fg=new Vector<Mat>();
		rgbaMats_bg=new Vector<Mat>();
		
		fg_bandw_mask=new Mat();
		bg_bandw_mask=new Mat();
		
		fg_bandw_mask=Highgui.imread(Environment.getExternalStorageDirectory()+"/Studio3D/img_mask_fg.png");
		bg_bandw_mask=Highgui.imread(Environment.getExternalStorageDirectory()+"/Studio3D/img_mask_fg.png");
		
		Imgproc.cvtColor(fg_bandw_mask, fg_bandw_mask, Imgproc.COLOR_BGR2RGBA);
		Imgproc.cvtColor(bg_bandw_mask, bg_bandw_mask, Imgproc.COLOR_BGR2RGBA);
		
		Core.split(fg_bandw_mask,rgbaMats_fg);
		rgbaMats_fg.set(3,rgbaMats_fg.get(0));
		Core.merge(rgbaMats_fg, fg_bandw_mask);
		
		
//		Core.split(bg_bandw_mask,rgbaMats_bg);
//		rgbaMats_bg.set(3,rgbaMats_bg.get(0));
//		Core.merge(rgbaMats_bg, bg_bandw_mask);
		
		rgbaMats_fg=new Vector<Mat>();
		rgbaMats_bg=new Vector<Mat>();
		
		leftImgMat=new Mat();
		leftImgMat=Highgui.imread(Environment.getExternalStorageDirectory()+"/Studio3D/img_left.jpg");
		
		Imgproc.cvtColor(leftImgMat, leftImgMat, Imgproc.COLOR_BGR2RGBA);
		
	}

	void saveMaskandExit()
	{
		
		fg_bandw_mask=new Mat();
		bg_bandw_mask=new Mat();
		
		Utils.bitmapToMat(BlueCirclesBmp,fg_bandw_mask);
		Imgproc.cvtColor(fg_bandw_mask, fg_bandw_mask, Imgproc.COLOR_RGBA2GRAY);
		
		Mat binaryMat=new Mat();
		binaryMat.create(fg_bandw_mask.rows(), fg_bandw_mask.cols(),fg_bandw_mask.type() );
		
		Imgproc.threshold(fg_bandw_mask, binaryMat, 1, 255, Imgproc.THRESH_BINARY);
		Highgui.imwrite(Environment.getExternalStorageDirectory()+"/Studio3D/mask_revised_fg.png", binaryMat);

		Log.d(TAG,"Type : "+binaryMat.type());
		
		temporary_ones=Mat.ones(binaryMat.rows(), binaryMat.cols(), binaryMat.type());
		
		temporary_ones.setTo(new Scalar(255,255,255,255));
		 
	 	Core.subtract(temporary_ones,binaryMat, bg_bandw_mask);
	 	
		Highgui.imwrite(Environment.getExternalStorageDirectory()+"/Studio3D/mask_revised_bg.png", bg_bandw_mask);
		
		
		Mat leftImg=Highgui.imread((Environment.getExternalStorageDirectory().getPath()+"/Studio3D/img_left.jpg"));
		Imgproc.cvtColor(leftImg, leftImg, Imgproc.COLOR_BGR2BGRA);
		
		
		Mat mask1,mask2;
		mask1=new Mat(fg_bandw_mask.rows(), fg_bandw_mask.cols(),binaryMat.type());
		
		mask2=new Mat(fg_bandw_mask.rows(), fg_bandw_mask.cols(),binaryMat.type());
		
		leftImg.copyTo(mask1, binaryMat);
		leftImg.copyTo(mask2, bg_bandw_mask);

		Highgui.imwrite(Environment.getExternalStorageDirectory()+"/Studio3D/Layers/img_fg.png", mask1);
		Highgui.imwrite(Environment.getExternalStorageDirectory()+"/Studio3D/Layers/img_bg.png", mask2);
		
	
		Log.d(TAG,"Saved ..");
		
		deallocateStructs();
		canvasthread.setRunning(false);
	
		
		new ProgressDialogClass().execute("");

		System.gc();
		

	}

	private void deallocateStructs() 
	{
		// TODO Auto-generated method stub
		//leftImgBitmap=null;
		//BlueCirclesBmp=null;
		//RedCirclesBmp=null;
		overlayBitmap=null;
		final_mask_fg=null;
		final_mask_bg=null;
		mask=null;
		mask2=null;
		//exitPressed=true;
		
	}
	
	class ProgressDialogClass extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			
	
			((Activity) activityContext).runOnUiThread(new Runnable() {
			     public void run() {
			    	 conversionProgress.setMessage("Processing Image - Applying Color Filters ");
						
			//stuff that updates ui

			    }
			});
			
			applyColorFilterstoLayers();

			return "";

		}

		@Override
		protected void onPostExecute(String result) {

			conversionProgress.dismiss();
			
//			((CanvasActivity) activityContext).finish();
			Intent i=new Intent(activityContext,AnimationActivity.class);
					activityContext.startActivity(i);
					
		Log.d("done", "done");

			
			
			
			// might want to change "executed" for the returned string passed
			// into onPostExecute() but that is upto you
		}

		@Override
		protected void onPreExecute() {
		//	conversionProgress.setTitle("Processing Image");
			conversionProgress
					.setMessage("Updating Depth Map");
			conversionProgress.show();
			
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
		
	}
	
	public void applyColorFilterstoLayers() {

		File sdCard = Environment.getExternalStorageDirectory();
		File filtersdir = new File(Environment.getExternalStorageDirectory()
				+ "/Studio3D/Layers/Filters/");

		File seperatedLayersFolder = new File(Environment.getExternalStorageDirectory()
						+ "/Studio3D/Layers/");

		// Find all the files in the folder..

		File[] files = seperatedLayersFolder.listFiles();

		Log.d(TAG, "Number of files:" + files.length);
		int count = 0;
		
		for (File file : files) {

			// Log.d("File path:","Path="+file.getPath());

			if (file.getName().toUpperCase().endsWith(("JPG"))|| file.getName().toUpperCase().endsWith(("PNG"))) {
				
				File dir = new File(sdCard.getAbsolutePath()
						+ "/Studio3D/Layers/Filters/" + count + "/");

				Log.d(TAG,"File name"+file.getAbsolutePath());
				Log.d(TAG, "path" + dir.getPath());
				dir.mkdirs();

				ApplyFilterstoLayer Filters;
											
		    	Filters=new ApplyFilterstoLayer(activityContext,file.getAbsolutePath(),count);

				count++;
			}
		}
    
	}
	
	
}   