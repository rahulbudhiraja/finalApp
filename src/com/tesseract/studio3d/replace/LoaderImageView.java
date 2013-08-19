package com.tesseract.studio3d.replace;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
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
       progressBar = new ProgressBar(context);
       progressBar.setIndeterminate(true);

       addView(progressBar);
       addView(imageView);

       this.setGravity(Gravity.CENTER);
   }

   // ...

   // Then, play with this method to show or hide your progressBar
   public void toggleVisibilty(boolean imgVisibility,boolean barVisibility) {
    
    instantiate(context);
   }

}