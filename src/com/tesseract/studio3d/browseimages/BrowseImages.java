package com.tesseract.studio3d.browseimages;

import java.io.File;

import com.tesseract.studio3d.R;
import com.tesseract.studio3d.StartScreen;
import com.tesseract.studio3d.Animation.MainActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;

public class BrowseImages extends Activity {

    public String[] allFiles;
    private String SCAN_PATH ;
    private static final String FILE_TYPE = "*/*";
    private MediaScannerConnection conn;
    GridView gridView;
    

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.browse);
        gridView= (GridView) findViewById(R.id.grid_view);
        
        File folder = new File(Environment.getExternalStorageDirectory()+"/Studio3D/images/cache");

        gridView.setAdapter(new ImagesAdapter(this,folder));
        
//  Include the below function ..
//    	Intent it = new Intent(this,MainActivity.class);
//    	
//		if (null != it)
//			{
//					
//				it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				it.putExtra("browseButtonClicked", "true");
//        
//				this.startActivity(it);
//				
//				Log.d("custom","starting");
//			}
//
//	}
    
    }
    
    public void onBackPressed() {
        //do your intent here
//    	Intent it=new Intent(this,StartScreen.class);
//    	this.startActivity(it);
    	
    	finish();
    }

   


}