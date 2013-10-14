package com.tesseract.studio3d.Animation;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.Log;
import android.widget.ImageView;

import com.tesseract.studio3d.R;

public class OverlayTouchCanvas extends ImageView
{

	
	Bitmap img;
	Canvas imgCanvas,maskCanvas;
	ColorMatrix cm1;
	Paint maskPaint;
	Bitmap mask;
	private Paint layerPaint;
	private String TAG="OverlayTouchCanvas";
	
	Bitmap filteredBitmap;
	private Paint paint,blackPaint;
	Canvas c;
	Paint layer1Paint;
	int layerID;
	public static boolean activate=false;
	
	public OverlayTouchCanvas(Context context,Bitmap bmp,String fg_filter,int layerid) 
	{
		super(context);
		// TODO Auto-generated constructor stub
		
		   mask=Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),Bitmap.Config.ARGB_8888);
		   
		   layerID=layerid;
		   
		   
		   maskCanvas=new Canvas(mask);
		   maskCanvas.drawColor(Color.BLACK);
		   
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
		   	
			filteredBitmap = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Config.ARGB_8888);
			c = new Canvas(filteredBitmap);
			
			c.save();
			configurePaint();
		
			Paint p=new Paint();
			p.setColor(Color.WHITE);
			img=bmp;
			layer1Paint=findPaint(fg_filter);
			
			
	}
	
	public void configurePaint()
    {
    	paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    	paint.setColor(Color.WHITE);
		
     //   paint.setColor(Color.RED);                    // set the color
        paint.setStrokeWidth(3);  
// set the size        paint.setDither(true);                    // set the dither to true
        paint.setStyle(Paint.Style.FILL);       // set to STOKE
//
        
        blackPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
//        blackPaint.setColor(Color.BLACK);
//        blackPaint.setStrokeWidth(3);
//        blackPaint.setStyle(Paint.Style.FILL);
        
        blackPaint.setColor(Color.TRANSPARENT);
        blackPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        blackPaint.setStyle(Paint.Style.FILL);
        
    }
	
	public void onDraw(Canvas canvas)
	{
	   	super.onDraw(canvas);
	    
	    canvas.drawBitmap(img, 0, 0, null);
	    canvas.drawBitmap(mask, 0, 0, maskPaint);
	    
	    Log.d(TAG," something");
	   //	imgCanvas.drawBitmap(img,0,0,null);
//	   	imgCanvas.drawBitmap(temporaryMask, 0, 0, maskPaint);
	}
	
public void handleTouch(float x,float y)
{
	
	if(layerID==FullScreenEditorActivity.activeLayer)
		 	maskCanvas.drawCircle(x, y, 60, paint);
		
		else 
			maskCanvas.drawCircle(x, y, 60, blackPaint);
		
			maskCanvas.save();
	c.drawBitmap(mask, 0, 0, maskPaint);
	
	invalidate();
	
}
	
//	public boolean onTouchEvent(MotionEvent event) {
//	 	   // TODO Auto-generated method stub
//	 	   
//	 	Log.d("X = "+event.getX(),"Y = "+event.getY());
//	 	
//	 	Log.d(TAG,"Timings -- 1:"+System.currentTimeMillis());
//	 	
//	 		Log.d(TAG,"Layers ::: "+layerID);
//	 	
//	 	switch (event.getAction())
//	 	{
//	 	
//	 	case MotionEvent.ACTION_DOWN:
//	 	case MotionEvent.ACTION_MOVE:
//	 	case MotionEvent.ACTION_POINTER_DOWN:
//	 		
//	 		if(layerID==FullScreenEditorActivity.activeLayer)
//	 		 	maskCanvas.drawCircle(event.getX(), event.getY(), 60, paint);
//	 		
//	 		else 
//	 			maskCanvas.drawCircle(event.getX(), event.getY(), 60, blackPaint);
//	 		
//	 			maskCanvas.save();
//	 	c.drawBitmap(mask, 0, 0, maskPaint);
//	 	
//	 	break;
//		
//	 	}
//	 	
//	 	invalidate();
//	 	
//	 	return true;
//	 	
//	 	
//	}
	
	
	
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
		
		Paint localpaint = new Paint();

		localpaint.setColorFilter(new ColorMatrixColorFilter(cm));
		
		return localpaint;
		
		
	}
	
}
