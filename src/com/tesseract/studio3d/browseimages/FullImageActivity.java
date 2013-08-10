package com.tesseract.studio3d.browseimages;

import java.io.File;

import com.tesseract.studio3d.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;
 
public class FullImageActivity extends Activity {
 
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.full_image);
 
    // get intent data
    Intent i = getIntent();
 
    // Selected image id
    int position = i.getExtras().getInt("id");
   // File folder = new File();

   // ImagesAdapter imageAdapter = new ImagesAdapter(this,folder);
 
    ImageView imageView = (ImageView) findViewById(R.id.full_image_view);
    imageView.setImageBitmap(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/Studio3D/images/cache/"+position+"/img_left.jpg"));
  
  }
 
}