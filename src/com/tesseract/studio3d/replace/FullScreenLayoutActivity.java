package com.tesseract.studio3d.replace;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tesseract.studio3d.utils.Structs;

public class FullScreenLayoutActivity extends Activity {


	protected void onCreate(Bundle savedInstanceState) 
	{

		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		LinearLayout l=new LinearLayout(this);
		LayoutParams lparams=new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);  
		l.setLayoutParams(lparams);
		//l.addView(new FullScreenLayout(this,Structs.selectedBitmap));
		
		//addContentView(new FullScreenLayout(this,Structs.selectedBitmap),lparams);
		//ImageView bgImgView=new ImageView (this);
		//bgImgView.setImageBitmap(Structs.selectedBitmap);
		 
		l.setBackgroundDrawable(makeDrawable(Structs.selectedBitmap));
		
		//setContentView(l);
		//addContentView(bgImgView,lparams);
		l.addView(new FullScreenLayout(this,Structs.selectedBitmap),lparams);
		
		
		
		setContentView(l);
		
	}

	private Drawable makeDrawable(Bitmap selectedBitmap) {
		// TODO Auto-generated method stub
		
		
		Drawable d =new BitmapDrawable(getResources(),selectedBitmap);
		
		return d;
	}
}
