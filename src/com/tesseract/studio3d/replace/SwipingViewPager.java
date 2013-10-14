package com.tesseract.studio3d.replace;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.tesseract.studio3d.R;


public class SwipingViewPager extends Activity {

	  
	  InputStream is;
	//  String[] placesArray;
	  
	 // private Vector<Bitmap>ImageList;
	  Bitmap Img;
	  Bitmap foregroundImg;
	  public static int clickPosition;
		
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		 setContentView(R.layout.viewpageractivity);
		   
		 
		 
	    ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
	    ImagePagerAdapter adapter = new ImagePagerAdapter();
	    viewPager.setAdapter(adapter);
//	    viewPager.setCurrentItem(MainListViewActivity.clickedImage);
	    viewPager.setCurrentItem(clickPosition);
	  }
	  
	  
	  

	  private class ImagePagerAdapter extends PagerAdapter {
	
	   

	    @Override 
	    public int getCount() {
	      //return mImages.length;
//	    	try {
//				return 10;//getAssets().list("images/Places").length;
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			return ReplaceActivity.placesArray.length;
	    }

	    @Override
	    public boolean isViewFromObject(View view, Object object) {
	      return view == ((ImageView) object);
	    }

	    @Override
	    public Object instantiateItem(ViewGroup container, int position) {
	      Context context = SwipingViewPager.this;

	      ImageView imageView = new ImageView(context);
	      int padding = context.getResources().getDimensionPixelSize(
	          R.dimen.padding_medium);
	      imageView.setPadding(padding, padding, padding, padding);
	      imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

	      imageView.setImageBitmap(ReplaceActivity.ImageList.get(position));
	      
	      ((ViewPager) container).addView(imageView, 0);
	      return imageView;
	    }

	    @Override
	    public void destroyItem(ViewGroup container, int position, Object object) {
	      ((ViewPager) container).removeView((ImageView) object);
	    }
	  }
	  
	 
	}