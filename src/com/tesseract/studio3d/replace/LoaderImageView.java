package com.tesseract.studio3d.replace;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.tesseract.studio3d.utils.Structs;

public class LoaderImageView extends RelativeLayout {
    private Context     context;
    private ProgressBar progressBar;
    private ImageView   imageView;
    private Bitmap bmp;
    public int position;

    public LoaderImageView(final Context context) {
    super(context);
    instantiate(context);
    }
    
    public void setPosition(int i) {
    	// TODO Auto-generated method stub
    	position=i;
    	
    }

   private void instantiate(final Context _context) {
       context = _context;
       
       imageView = new ImageView(context);
       
       final int w = 940/2;
	   final int h = 510/2;

       Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
       bmp = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
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
       
       imageView.setOnClickListener(imageViewClickListener);
       
   }

   
   // ...

  
   public void setImage(Bitmap img)
   {
	   bmp=img;
	   imageView.setImageBitmap(img);
	   progressBar.setVisibility(View.INVISIBLE);
	   
   }
   
   public OnClickListener imageViewClickListener = new OnClickListener() {

	@Override
	public void onClick(View v) 
	{
		// TODO Auto-generated method stub
		if(Structs.selectedBitmap==null)
		Structs.selectedBitmap=Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),Bitmap.Config.ARGB_8888);
		
		Structs.selectedBitmap=bmp;
		Log.d("Id","id"+v.getId());
		
		Log.d("Id","position"+position);
		//  Intent i=new Intent(context,FullScreenLayoutActivity.class);
		  Intent i=new Intent(context,SwipingViewPager.class);		  		  
		  context.startActivity(i);
		SwipingViewPager.clickPosition=position;
		
	}

   };



}