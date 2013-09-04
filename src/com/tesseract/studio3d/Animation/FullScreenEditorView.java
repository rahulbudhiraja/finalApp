 package com.tesseract.studio3d.Animation;

import java.util.Random;
import java.util.Vector;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.CornerPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.tesseract.studio3d.R;

public class FullScreenEditorView extends ImageView
{
	Mat fgMat,bgMat;
	Mat converted_fgMat,converted_bgMat;
	Mat temporary_ones,fg_bandw_mask,bg_bandw_mask;
	static String bg_filter,fg_filter;
	
	static Bitmap fgBmp,bgBmp;

	Paint paint;
	
	String[] imageFilters = { "sepia", "stark", "sunnyside", "cool", "worn",
			"grayscale","vignette","crush","sunny","night" };
	private static String TAG="FullScreenEditor";

	// Conversion Mats initialization as discussed with Jay ...
	
	Mat fg_gray,bg_gray;

	Vector<Mat> rgbaMats_fg,rgbaMats_bg;
	Mat fg_alpha,bg_alpha;
	Mat leftImgMat;
	
	// New overlay bitmaps experiments ..
	
	Bitmap overlayBitmap;
	Paint layer1Paint,layer2Paint;
	ColorMatrix layer1cm,layer2cm;
	Canvas overlayCanvas,fgBmpCanvas,bgBmpCanvas;
	
	Bitmap temporaryMask,temporaryMask2;
	static Context ctxt;
	Paint maskPaint,blackPaint;
	Bitmap filteredBitmap,filteredCopy;
	Bitmap customCircleBitmap;
	Canvas c,c2,temporaryCanvas,temporaryCanvas2;
	int layerID;
	
	
	
	public FullScreenEditorView(Context context,String filter1,String filter2)
	{
		super(context);
		
		initializeMats();
		
		ctxt=context;
		
		fg_filter=filter1;
		bg_filter=filter2;
		
		layerID=1;
		
		Log.d(TAG,"selected "+fg_filter);
		Log.d(TAG,"selected "+bg_filter);

/** aint working 	
//		// Load the mats from disk
//			
//		fgMat=Highgui.imread(Environment.getExternalStorageDirectory().getPath()+"/Studio3D/Layers/img_fg.png");
//		bgMat=Highgui.imread(Environment.getExternalStorageDirectory().getPath()+"/Studio3D/Layers/img_bg.png");
//		
//		// Convert it into RGBA
//		Imgproc.cvtColor(fgMat, converted_fgMat, Imgproc.COLOR_BGR2RGBA);
//		Imgproc.cvtColor(bgMat, converted_bgMat, Imgproc.COLOR_BGR2RGBA);
//		// Show it into the view ..
//		
//		fgBmp=Bitmap.createBitmap(fgMat.cols(),fgMat.rows(),Bitmap.Config.ARGB_8888);
//		
//		bgBmp=Bitmap.createBitmap(bgMat.cols(),bgMat.rows(),Bitmap.Config.ARGB_8888);
//		
//		Utils.matToBitmap(converted_fgMat, fgBmp,true);
//	    Utils.matToBitmap(converted_bgMat, bgBmp,true);
//	    
	*/
	    
	    bgBmp=BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()+"/Studio3D/img_left.jpg");
	    fgBmp=BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()+"/Studio3D/img_left.jpg");
	    
	    
	    // getting the mats ..

		Utils.bitmapToMat(fgBmp,fgMat);
		Utils.bitmapToMat(bgBmp,bgMat);
		
		temporary_ones=Mat.ones(fgMat.rows(), fgMat.cols(), CvType.CV_8UC4);
		//temporary_ones.mul(temporary_ones, 255);
		temporary_ones.setTo(new Scalar(255,255,255,255));
		Highgui.imwrite("/mnt/sdcard/Studio3D/img_mask_temporary_ones.png",temporary_ones);
		
		
		// Converting bgrmats to rgba ...
		Imgproc.cvtColor(fgMat, converted_fgMat, Imgproc.COLOR_BGR2RGBA);
		Imgproc.cvtColor(bgMat, converted_bgMat, Imgproc.COLOR_BGR2RGBA);
	    
	    // Loading alpha .
		
		//Imgproc.cvtColor(fgMat, fg_gray, Imgproc.COLOR_BGR2GRAY);
		//Imgproc.cvtColor(bgMat, bg_gray, Imgproc.COLOR_BGR2GRAY);
		
		//converted_fgMat.copyTo(fg_alpha);
		//converted_bgMat.copyTo(bg_alpha);
		
		// Splitting the rgba ..
		
//		Core.split(converted_fgMat, rgbaMats_fg);
		//fg_alpha=rgbaMats_fg.get(3);
 
//		rgbaMats_fg.set(3,rgbaMats_fg.get(0));
//		Core.merge(rgbaMats_fg, fg_alpha);
		
		//		
//		
//		Core.split(converted_bgMat, rgbaMats_bg);
//		
//		rgbaMats_bg.set(3,rgbaMats_bg.get(0));
//		Core.merge(rgbaMats_bg, bg_alpha);
	
		//bg_alpha=rgbaMats_bg.get(3);
//		

	   fgBmp=applyFiltertoBitmap(fgBmp,fg_filter);
	   bgBmp=applyFiltertoBitmap(bgBmp,bg_filter);
	    
	    
	  // overlayBitmap=Bitmap.createBitmap(fgBmp.getWidth(),fgBmp.getHeight(),Bitmap.Config.ARGB_8888);
	  // overlayBitmap.isMutable();
	   
	   makeBitmapTransparent();
	   
	   layer1Paint=findPaint(fg_filter);
	   layer2Paint=findPaint(bg_filter);
	    
//	   overlayCanvas=new Canvas(overlayBitmap);
//	   overlayCanvas.drawColor(Color.BLACK);
	   
	   
	   fgBmpCanvas=new Canvas(fgBmp);
	   bgBmpCanvas=new Canvas(bgBmp);
	   
	 //  temporaryMask= BitmapFactory.decodeResource(context.getResources(), R.drawable.temp_mask);
	   temporaryMask=Bitmap.createBitmap(fgBmp.getWidth(),fgBmp.getHeight(),Bitmap.Config.ARGB_8888);
	   temporaryMask2=Bitmap.createBitmap(fgBmp.getWidth(),fgBmp.getHeight(),Bitmap.Config.ARGB_8888);
	   Paint p=new Paint();
	   p.setColor(Color.WHITE);
	  
	   
	   temporaryCanvas=new Canvas(temporaryMask);
	   temporaryCanvas.drawColor(Color.BLACK);
	   

	   temporaryCanvas2=new Canvas(temporaryMask2);
	   temporaryCanvas2.drawColor(Color.BLACK);
	    
	   float[] src = {
			    0, 0, 0, 0, 255,
			    0, 0, 0, 0, 255,
			    0, 0, 0, 0, 255,
			    1, 1, 1, -1, 0,
			};
		ColorMatrix cm = new ColorMatrix(src);
		ColorMatrixColorFilter filter = new ColorMatrixColorFilter(cm);
		
		maskPaint = new Paint();
		maskPaint.setColorFilter(filter);
		maskPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
	   
		filteredBitmap = Bitmap.createBitmap(fgBmp.getWidth(), fgBmp.getHeight(), Config.ARGB_8888);
		
		filteredCopy=Bitmap.createScaledBitmap(filteredBitmap, fgBmp.getWidth(), fgBmp.getHeight(),true);
		configurePaint();
		
//		temporaryCanvas.drawCircle(300, 300,100,paint);
//		temporaryCanvas2.drawCircle(600, 300,50,paint);
		
		c = new Canvas(filteredBitmap);
		c2 = new Canvas(filteredCopy);
		
		
		c.drawBitmap(fgBmp, 0, 0, layer1Paint);
	    c.drawBitmap(temporaryMask, 0, 0, maskPaint);
		
	    c2.drawBitmap(fgBmp, 0,0,layer2Paint);
	    c2.drawBitmap(temporaryMask2, 0, 0, maskPaint);
		
	    
		c.save();
		
		
		
		// TODO Auto-generated constructor stub
	}
	
	private void makeBitmapTransparent() {
		// TODO Auto-generated method stub
		
	}


	
	public void configurePaint()
    {
    	  paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    	  paint.setColor(Color.WHITE);
		  paint.setStrokeWidth(3);  
		  paint.setStyle(Paint.Style.FILL);   
		  
   //   paint.setColor(Color.RED);                    // set the color
         
// set the size        paint.setDither(true);                    // set the dither to true
              // set to STOKE
//        paint.setStrokeJoin(Paint.Join.ROUND);    // set the join to round you want
//        paint.setStrokeCap(Paint.Cap.ROUND);      // set the paint cap to round too
//        paint.setPathEffect(new CornerPathEffect(20) );   // set the path effect when they join.
//        paint.setAntiAlias(true);  
        
        
		  blackPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
		  blackPaint.setColor(Color.BLACK);
		  blackPaint.setStrokeWidth(3);
		  blackPaint.setStyle(Paint.Style.FILL);
        
//        blackPaint.setColor(Color.BLACK);
//        blackPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//        blackPaint.setStyle(Paint.Style.FILL);
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
	
	 public void onDraw(Canvas canvas)
	    {
	    	super.onDraw(canvas);
	        canvas.save();
	    	
//	    	canvas.drawBitmap(fgBmp, 0, 0,paint); 
//		    canvas.drawBitmap(bgBmp, 0, 0,paint);
//		    canvas.drawBitmap(overlayBitmap, 0, 0,paint);
//		    
//		    canvas.drawBitmap(filteredBitmap,0,0,paint);
		    //canvas.drawBitmap(fgBmp, 0, 0, layer1Paint);
		    //canvas.drawBitmap(temporaryMask, 0, 0, maskPaint);
	        
	        canvas.drawBitmap(filteredBitmap, 0,0, null);
	        canvas.drawBitmap(filteredCopy, 0,0, null);
	        
		  //  canvas.restore();
	    }
	
	 
	 public boolean onTouchEvent(MotionEvent event) {
	 	   // TODO Auto-generated method stub
	 	   
	 	Log.d("X = "+event.getX(),"Y = "+event.getY());
	 	
	 	Log.d(TAG,"Timings -- 1:"+System.currentTimeMillis());
	 	
	 	
	 	switch (event.getAction())
	 	{
	 	
	 	case MotionEvent.ACTION_DOWN:
	 	case MotionEvent.ACTION_MOVE:
	 	case MotionEvent.ACTION_POINTER_DOWN:
	 		
	 	if(layerID==FullScreenEditorActivity.activeLayer)	
	 		{
	 			temporaryCanvas.drawCircle(event.getX(), event.getY(), 60, paint);
	 			temporaryCanvas2.drawCircle(event.getX(), event.getY(), 60, blackPaint);
	 		
	 		}
	 	else {
	 		temporaryCanvas.drawCircle(event.getX(), event.getY(), 60, blackPaint);
	 		temporaryCanvas2.drawCircle(event.getX(), event.getY(), 60, paint);
	 	}
	 	
	 	temporaryCanvas.save();
	 	temporaryCanvas2.save();
	 	
	 	
 		c = new Canvas(filteredBitmap);
 		c2 = new Canvas(filteredCopy);
		
 		c.drawBitmap(fgBmp, 0, 0, layer1Paint);
	    c.drawBitmap(temporaryMask, 0, 0, maskPaint);
	    
		c2.drawBitmap(fgBmp, 0, 0, layer2Paint);
	    c2.drawBitmap(temporaryMask2, 0, 0, maskPaint);

	 
	 	break;
		
	 	}
//	 	if(FullScreenEditorActivity.activeLayer==1)
//	 		
//	 		fgBmpCanvas.drawCircle(event.getX(), event.getY(), 20, layer1Paint);
//	 	else if(FullScreenEditorActivity.activeLayer==2)
////	 		
//	 		bgBmpCanvas.drawCircle(event.getX(), event.getY(), 20, layer2Paint);
//	 		
//	 	overlayCanvas.drawCircle(event.getX(), event.getY(), 20, layer2Paint);
	 	//createCorrespondingMask(event.getX(),event.getY());
	 	
	 	
	 	
	 	// Alternative ..With the current Paint selected ,just draw the circle on the bitmap ... 
	 	 
	 	
	 	// Once the user clicks the save button 
	 	
	 	
	 	// Note : Change the way the color filter is applied ..
	 	
	 	
	 	invalidate(); 
	 	
		return true;
	 	
	 }
	 
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
//				imageViewCanvas.drawBitmap(modifiedBitmap, smatrix, spaint);

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

//				scaledBorder = Bitmap.createScaledBitmap(border,width,height, false);

				}
			
			Paint localpaint = new Paint();

			localpaint.setColorFilter(new ColorMatrixColorFilter(cm));
			
			return localpaint;
			
			
		}
	 public void createCorrespondingMask(float xTouchPos,float yTouchPos)
	 {
		 

			Core.circle(fg_bandw_mask, new Point(xTouchPos,yTouchPos), 20,new Scalar(255,255,255,255) ,-1);
		 	//rgbaMats_fg.set(3,fg_alpha);

		    Log.d(TAG,"Timings -- 2:"+System.currentTimeMillis());
			 
		 	Core.subtract(temporary_ones,fg_bandw_mask, bg_bandw_mask);
		 	
		 	Core.bitwise_and(fg_bandw_mask, leftImgMat,converted_fgMat);
		 	Core.bitwise_and(bg_bandw_mask, leftImgMat,converted_bgMat);
		 	
			Log.d(TAG,"Timings -- 3:"+System.currentTimeMillis());
		 	//Core.merge(rgbaMats_fg, converted_fgMat);
		
			Utils.matToBitmap(converted_fgMat, fgBmp);
			Utils.matToBitmap(converted_bgMat, bgBmp);
			
			Log.d(TAG,"Timings -- 4:"+System.currentTimeMillis());
			
			fgBmp=applyFiltertoBitmap(fgBmp,fg_filter);
			bgBmp=applyFiltertoBitmap(bgBmp,bg_filter);
		    
			Log.d(TAG,"Timings -- 5:"+System.currentTimeMillis());
			
		 	Highgui.imwrite("/mnt/sdcard/Studio3D/img_mask_converted_fgmat.png",converted_fgMat);
		 	Highgui.imwrite("/mnt/sdcard/Studio3D/img_mask_converted_bgmat.png",converted_bgMat);
			
		 	Log.d(TAG,"Timings -- 6:"+System.currentTimeMillis());
		 	
		 	
		 	
		 	
	 }
	 
		static public Bitmap applyFiltertoBitmap(Bitmap imgViewBitmap,String filtName) 
		{
			// Bitmap
			// imgViewBitmap=((BitmapDrawable)CanvasImageViews.get(currentSelectedLayer).getDrawable()).getBitmap();
			//Bitmap imgViewBitmap = layerBitmaps.get(currentSelectedLayer);

			Bitmap modifiedBitmap = Bitmap.createScaledBitmap(imgViewBitmap,imgViewBitmap.getWidth(), imgViewBitmap.getHeight(), true);
			Canvas imageViewCanvas = new Canvas(modifiedBitmap);

			imageViewCanvas.drawBitmap(modifiedBitmap, 0, 0, new Paint());

			ColorMatrix cm = new ColorMatrix();

			String filterName = filtName;
			
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
				imageViewCanvas.drawBitmap(modifiedBitmap, smatrix, spaint);

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

				border = BitmapFactory.decodeResource(ctxt.getResources(), R.drawable.vignette);

				int width = modifiedBitmap.getWidth();
				int height = modifiedBitmap.getHeight();

				scaledBorder = Bitmap.createScaledBitmap(border,width,height, false);
				if (scaledBorder != null && border != null) {
					imageViewCanvas.drawBitmap(scaledBorder, 0, 0, new Paint());
				}
			}

			Paint paint = new Paint();

			paint.setColorFilter(new ColorMatrixColorFilter(cm));
			Matrix matrix = new Matrix();

			if(!filterName.equalsIgnoreCase("vignette"))
				imageViewCanvas.drawBitmap(modifiedBitmap, matrix, paint);

			
			return modifiedBitmap;
		}

		public void initializeColormatrices()
		{

			//			if(FullScreenEditorActivity.activeLayer==1)
			
			
			
		}
		
		
		
}
