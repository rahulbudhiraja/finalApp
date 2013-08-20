package com.tesseract.studio3d.replace;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class LoaderImageView extends RelativeLayout {
    private Context     context;
    private ProgressBar progressBar;
    private ImageView   imageView;

    public LoaderImageView(final Context context) {
    super(context);
    instantiate(context);
    }

   private void instantiate(final Context _context) {
       context = _context;
       
       imageView = new ImageView(context);
       
       int w = 960/2, h = 540/2;

       Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
       Bitmap bmp = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
       imageView.setImageBitmap(bmp);
       
       progressBar = new ProgressBar(context);
       progressBar.setIndeterminate(true);
       progressBar.getIndeterminateDrawable().setColorFilter(0xFFFFFFFF,android.graphics.PorterDuff.Mode.MULTIPLY);
     
       RelativeLayout.LayoutParams rlayoutParams = new RelativeLayout.LayoutParams(
	    		RelativeLayout.LayoutParams.WRAP_CONTENT,
	    		RelativeLayout.LayoutParams.WRAP_CONTENT);
   

       rlayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
       rlayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
      
       progressBar.setLayoutParams(rlayoutParams);
       
       addView(progressBar);
       addView(imageView);

       this.setGravity(Gravity.CENTER);
   }

   // ...

   // Then, play with this method to show or hide your progressBar
   public void toggleVisibilty(boolean imgVisibility,boolean barVisibility) {
    
    
   }
   
   public void setImage(Bitmap img)
   {
	   imageView.setImageBitmap(img);
	   progressBar.setVisibility(View.INVISIBLE);
	   
   }

}