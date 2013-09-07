package com.tesseract.studio3d.replace;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.tesseract.studio3d.menu.MenuActivity;
import com.tesseract.studio3d.utils.Structs;

public class FullScreenLayoutActivity extends MenuActivity {


  protected void onCreate(Bundle savedInstanceState) 
  {

    super.onCreate(savedInstanceState);

    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);

    setContentView(new FullScreenLayout(this,Structs.selectedBitmap));
    
  }
}