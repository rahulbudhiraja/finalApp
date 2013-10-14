package com.tesseract.studio3d.NewImageFilters;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;

public class InterfaceClass 
{
	String TAG="com.tesseract.studio3d.instagramEffects";

	String[] imageFilters = { "none", "Retro", "Breeze","MonoTint","Glorify","Pensive" };
	
	Context activityContext;
	
	String imagePath;
	
	NewColorFilters findColorMatrix;
	Paint multiplyPaint;
	
	Bitmap bmptobeManipulated=null,bmpmutable=null,bmpmutable_bgLayer=null,bmpmutable_fgLayer=null,bmpmutable_upperfgLayer=null;
	Canvas bmpmutable_bgLayerCanvas,fgCanvas,combinedCanvas;
	
	Paint p,paint;
	
	public InterfaceClass()
	{
		findColorMatrix=new NewColorFilters();
	}
	public InterfaceClass(Context context,String path,int folderNum)
	{
		imagePath=path;
		activityContext=context;
		
		findColorMatrix=new NewColorFilters();
		
		applyInstagramEffectstoImage(imagePath,folderNum);
		
	}

	public void applyInstagramEffectstoImage(String imgFile,int folderNum) {
		// TODO Auto-generated method stub

		// Make an instagram version of the filters ..
		for (int i = 0; i < imageFilters.length; i++) {

			Bitmap tempbitmap = BitmapFactory.decodeFile(imgFile);
			
			// Apply Different Filters and save

//			Paint paint = new Paint();
//			ColorMatrix cm = new ColorMatrix();
			
			int width_x=(int) Math.round(0.22*tempbitmap.getWidth()),width_y=(int) Math.round(0.22*tempbitmap.getHeight());
			
			Bitmap resizedbitmap = Bitmap.createScaledBitmap(tempbitmap, width_x, width_y, true);
			
			Log.d(TAG,"Width=="+width_x);

			//Canvas canvas = new Canvas(resizedbitmap);

			applySpecificFiltertoimage(imageFilters[i], resizedbitmap, folderNum);

		}
		
	}
	
	public void applySpecificFiltertoimage(String filterName,
			Bitmap canvas_bitmap,int i) 
	{

		bmpmutable=canvas_bitmap.copy(Bitmap.Config.ARGB_8888, true);
		
		
	
		ColorMatrixColorFilter cmFilter=null;
		paint=new Paint();
		
		p=new Paint(Paint.ANTI_ALIAS_FLAG);
		
		//canvas.drawBitmap(canvas_bitmap, 0, 0, new Paint());
		
		if(filterName.equalsIgnoreCase("Breeze"))
		{
			cmFilter=findColorMatrix.implementCalmBreezeFilter();
		    p.setARGB(255, 252,255,235);
		}
		else if(filterName.equalsIgnoreCase("Retro"))
		{
			cmFilter=findColorMatrix.implementRetroColorfilter()  ;
			p.setARGB(255, 250,220,175);
		}
		
		else if(filterName.equalsIgnoreCase("Glorify"))
			cmFilter=findColorMatrix.implementGlorifyFilter();
		
		else if(filterName.equalsIgnoreCase("Pensive"))
		{   cmFilter=findColorMatrix.implementPensiveColorfilter();
			p.setARGB(255,252,243,214);
		}
		
		else if(filterName.equalsIgnoreCase("MonoTint"))
			cmFilter=findColorMatrix.implementMonoChromeFilter();
		
		bmpmutable_bgLayer=canvas_bitmap.copy(Bitmap.Config.ARGB_8888, true);
		bmpmutable_fgLayer=canvas_bitmap.copy(Bitmap.Config.ARGB_8888, true);
		
		 Paint matrixPaint=new Paint();
		    matrixPaint.setColorFilter(cmFilter);
		
	    bmpmutable_bgLayerCanvas=new Canvas(bmpmutable_bgLayer);
	    bmpmutable_bgLayerCanvas.drawBitmap(bmpmutable_bgLayer, 0,0, matrixPaint);
	    
		p.setStyle(Style.FILL);
		p.setFilterBitmap(false);
		
		multiplyPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
		multiplyPaint.setXfermode(new PorterDuffXfermode(Mode.MULTIPLY));
		multiplyPaint.setFilterBitmap(false);
		
		fgCanvas=new Canvas(bmpmutable_fgLayer);

// set the blending mode to multiply ..

		fgCanvas.drawRect(new Rect(0,0,bmpmutable.getWidth(),bmpmutable.getHeight()), p);
	
		combinedCanvas=new Canvas(bmpmutable);
		
		if(filterName.equalsIgnoreCase("MonoTint"))
			combinedCanvas.drawBitmap(bmpmutable,0,0,matrixPaint);
		
		else if(!filterName.equalsIgnoreCase("Glorify"))
		{
			combinedCanvas.drawBitmap(bmpmutable_fgLayer,0,0,null);
			combinedCanvas.drawBitmap(bmpmutable_bgLayer,0,0,multiplyPaint);
		}
		
		else if(filterName.equalsIgnoreCase("none"))
		{
			combinedCanvas.drawBitmap(bmpmutable, 0,0,new Paint());
		}
		
		else combinedCanvas.drawBitmap(bmpmutable,0,0,matrixPaint);
		
		// Draw Text ..
		
		paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setTextSize(20);
		String filename=filterName;
		filename=filename.substring(0, 1).toUpperCase() + filename.substring(1);
		
		//input.substring(0, 1).toUpperCase() + input.substring(1);
		
		combinedCanvas.drawText(filename, (float) (canvas_bitmap.getWidth()/2-10*filterName.length()/2),
				(float) (canvas_bitmap.getHeight()*0.3), paint);

		/* code... */

		String fileName = Environment.getExternalStorageDirectory()
				+ "/Studio3D/Layers/Filters/" + i + "/" + filterName + ".png";
	
		OutputStream stream = null;
		try {
			
			stream = new FileOutputStream(fileName);
			
			/*
			 * Write bitmap to file using JPEG or PNG and 80% quality hint for
			 * JPEG.
			 */
			bmpmutable.compress(CompressFormat.PNG, 100, stream);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			stream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.d("Filter name", "path " + filterName);
	}

public Bitmap applyFiltertoView(Bitmap modifiedBitmap,String filterName) 
{
	
	if(filterName.equalsIgnoreCase("none"))
		return modifiedBitmap;
	
	bmpmutable=modifiedBitmap.copy(Bitmap.Config.ARGB_8888, true);
	
	ColorMatrixColorFilter cmFilter=null;
	paint=new Paint();
	
	p=new Paint(Paint.ANTI_ALIAS_FLAG);
	
	//canvas.drawBitmap(canvas_bitmap, 0, 0, new Paint());
	
	if(filterName.equalsIgnoreCase("Breeze"))
	{
		cmFilter=findColorMatrix.implementCalmBreezeFilter();
	    p.setARGB(255, 252,255,235);
	}
	else if(filterName.equalsIgnoreCase("Retro"))
	{
		cmFilter=findColorMatrix.implementRetroColorfilter()  ;
		p.setARGB(255, 250,220,175);
	}
	
	else if(filterName.equalsIgnoreCase("Glorify"))
		cmFilter=findColorMatrix.implementGlorifyFilter();
	
	else if(filterName.equalsIgnoreCase("Pensive"))
	{   cmFilter=findColorMatrix.implementPensiveColorfilter();
		p.setARGB(255,252,243,214);
	}
	else if(filterName.equalsIgnoreCase("MonoTint"))
		cmFilter=findColorMatrix.implementMonoChromeFilter();

	
	bmpmutable_bgLayer=modifiedBitmap.copy(Bitmap.Config.ARGB_8888, true);
	bmpmutable_fgLayer=modifiedBitmap.copy(Bitmap.Config.ARGB_8888, true);
	
	 Paint matrixPaint=new Paint();
	    matrixPaint.setColorFilter(cmFilter);
	
    bmpmutable_bgLayerCanvas=new Canvas(bmpmutable_bgLayer);
    bmpmutable_bgLayerCanvas.drawBitmap(bmpmutable_bgLayer, 0,0, matrixPaint);
    
	p.setStyle(Style.FILL);
	p.setFilterBitmap(false);
	
	multiplyPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
	multiplyPaint.setXfermode(new PorterDuffXfermode(Mode.MULTIPLY));
	multiplyPaint.setFilterBitmap(false);
	
	fgCanvas=new Canvas(bmpmutable_fgLayer);

//set the blending mode to multiply ..

	fgCanvas.drawRect(new Rect(0,0,bmpmutable.getWidth(),bmpmutable.getHeight()), p);

	combinedCanvas=new Canvas(bmpmutable);
	
	if(filterName.equalsIgnoreCase("Glorify")||filterName.equalsIgnoreCase("MonoTint"))
	{
		combinedCanvas.drawBitmap(bmpmutable,0,0,matrixPaint);
	}
	else 
	{
		combinedCanvas.drawBitmap(bmpmutable_fgLayer,0,0,null);
		combinedCanvas.drawBitmap(bmpmutable_bgLayer,0,0,multiplyPaint);
	}
	
	//else combinedCanvas.drawBitmap(bmpmutable,0,0,matrixPaint);
	
	return bmpmutable;
	
}
	
}
