 package com.tesseract.studio3d.replace;

import java.util.Timer;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import com.tesseract.studio3d.utils.Structs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class FullScreenLayout extends ImageView {
  
  Paint paint;
  Bitmap tempBitmap;
  
    public FullScreenLayout(Context context,Bitmap selectedBitmap) 
    {
          super(context);
          configurePaint();
          //tempBitmap=selectedBitmap;
//          tempBitmap = Bitmap.createBitmap(selectedBitmap, 0, 0, selectedBitmap.getWidth(),selectedBitmap.getHeight(),null, false);

          tempBitmap =Bitmap.createScaledBitmap(selectedBitmap, 960, 540, true);
  
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
    
    invalidate();
    canvas.save();
  }
}