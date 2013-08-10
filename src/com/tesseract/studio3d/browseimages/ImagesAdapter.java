package com.tesseract.studio3d.browseimages;

import java.io.File;
import java.util.Vector;

import com.tesseract.studio3d.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
 
public class ImagesAdapter extends BaseAdapter {
  private Context mContext;
   Vector<Bitmap> storedImages;
 
  // Keep all Images in array
  public Integer[] mThumbIds = {
    
  };
 
  // Constructor
  public ImagesAdapter(Context c,File dirPath){
    this.mContext = c;
    storedImages=new Vector<Bitmap>();
    loadImages(dirPath);
  }
 
  private void loadImages(File dirPath) {
	// TODO Auto-generated method stub
	File[] dirFiles=dirPath.listFiles();
	
  for (int i=0;i<dirFiles.length;i++)
  {
	  File file = dirFiles[i];
	  Log.d("File name"," path "+file.getAbsolutePath());
		 
	  storedImages.add(BitmapFactory.decodeFile(file.getAbsolutePath()+"/img_left.jpg") ) ;
	 
	  
  }
  Log.d("IMAGE adapter"," length "+storedImages.size());
}

@Override
  public int getCount() 
  {
    return storedImages.size();
  }
 
  @Override
  public Object getItem(int position)
  {
    return storedImages.get(position);
  }
 
  @Override
  public long getItemId(int position) {
    return 0;
  }
 
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
	  
	  ImageView imageView ;
	  if(convertView==null)
	  {
		  
		  imageView= new ImageView(mContext);
	      imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
	      imageView.setLayoutParams(new GridView.LayoutParams(180, 180));
	      imageView.setPadding(8, 8, 8, 8);

	  }
	  
	  else imageView = (ImageView) convertView;
	  
	  imageView.setImageBitmap(storedImages.get(position));
      
        return imageView;
  }
 
}