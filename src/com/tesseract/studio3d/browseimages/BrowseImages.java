package com.tesseract.studio3d.browseimages;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import com.tesseract.studio3d.CustomFileObserver;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
     
        
        
        /**
         * On Click event for Single Gridview Item
         * */
        gridView.setOnItemClickListener(new OnItemClickListener() 
        {
          public void onItemClick(AdapterView<?> parent, View v,
              int position, long id) {
     
        	  
        	// Copy Images into the folder .
        	  
        		try {
					
        			copy(Environment.getExternalStorageDirectory().getPath()+"/Studio3D/images/cache/"+position+"/img_bg.png",Environment.getExternalStorageDirectory().getPath()+"/Studio3D/Layers/img_bg.png");
					
					copy(Environment.getExternalStorageDirectory().getPath()+"/Studio3D/images/cache/"+position+"/img_fg.png",Environment.getExternalStorageDirectory().getPath()+"/Studio3D/Layers/img_fg.png");
					
					copy(Environment.getExternalStorageDirectory().getPath()+"/Studio3D/images/cache/"+position+"/img_left.jpg",Environment.getExternalStorageDirectory().getPath()+"/Studio3D/img_left.jpg");
					
					copy(Environment.getExternalStorageDirectory().getPath()+"/Studio3D/images/cache/"+position+"/img_full.jpg",Environment.getExternalStorageDirectory().getPath()+"/Studio3D/img_full.jpg");
					
					copy(Environment.getExternalStorageDirectory().getPath()+"/Studio3D/images/cache/"+position+"/disp.png",Environment.getExternalStorageDirectory().getPath()+"/Studio3D/disp.png");
					
					
        		} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
            // Sending image id to FullScreenActivity
            Intent it = new Intent(getApplicationContext(), MainActivity.class);
            // passing array index
            it.putExtra("browseButtonClicked", true);
	        it.putExtra("foldernum", position);
            startActivity(it);
          }
          
        });
        
        
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
    
	public void copy(String src_loc,String dst_loc) throws IOException {
	   
		
		
		File src=new File(src_loc);
		File dst=new File(dst_loc);
		
		InputStream in = new FileInputStream(src);
	    OutputStream out = new FileOutputStream(dst);

	    // Transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) {
	        out.write(buf, 0, len);
	    }
	    in.close();
	    out.close();
	}
	

    
    public void onBackPressed() {
        //do your intent here
//    	Intent it=new Intent(this,StartScreen.class);
//    	this.startActivity(it);
    	
    	finish();
    }

   


}