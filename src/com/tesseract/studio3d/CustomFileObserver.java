package com.tesseract.studio3d;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.tesseract.studio3d.Animation.PhotoActivity;
import com.tesseract.studio3d.selectionscreen.MainScreen;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.os.FileObserver;
import android.util.Log;

public class CustomFileObserver 
{

	Context activityContext;
	static int numFiles;

	public CustomFileObserver(Context context)
	{
		activityContext=context;
		createLayersFolder();
		
		initializeFileObserver();
		
	}

	private void createLayersFolder() {
		// TODO Auto-generated method stub
		File filtersdir = new File(Environment.getExternalStorageDirectory()
				+ "/Studio3D/Layers/Filters/");

		filtersdir.mkdirs();
	}

	public void initializeFileObserver()
	{
		Log.d("com.tesseract.studio3d","Initializing");

		FileObserver observer = new FileObserver(android.os.Environment.getExternalStorageDirectory().toString() + "/DCIM/100MEDIA/") { // set up a file observer to watch this directory on sd card
		
			public void onEvent(int event, String file) {
				Log.d("fired","fired"+" Event:"+event);

				PackageManager pm = activityContext.getPackageManager();

				if(event == FileObserver.CLOSE_WRITE && !file.equals(".probe")){ // check if its a "create" and not equal to .probe because thats created every time camera is launched
					{
						Log.d("fds", "File created [" + android.os.Environment.getExternalStorageDirectory().toString() + "/DCIM/100MEDIA/" + file + "]");
					
						try
						{

							String jpsfilename=Environment.getExternalStorageDirectory().toString() + "/DCIM/100MEDIA/" + file;
							String jpgfilename=jpsfilename.substring(0,jpsfilename.length()-1)+"g";

							Log.d("File name","name "+jpsfilename);

							File from      = new File("",jpsfilename);
							File to        = new File("",jpgfilename);
							from.renameTo(to);

							Log.i("From path is", from.toString());
							Log.i("To path is", to.toString());

							BitmapFactory.Options option = new BitmapFactory.Options();
							option.inSampleSize =2;
							option.inScaled = true;

							Bitmap fullsize_bitmap=BitmapFactory.decodeFile(jpgfilename,option);
							Log.d("studio3d","width="+fullsize_bitmap.getWidth()+" Height= "+fullsize_bitmap.getHeight());

							Bitmap left_img=Bitmap.createBitmap(fullsize_bitmap, 0,0,fullsize_bitmap.getWidth()/2,fullsize_bitmap.getHeight());

							Bitmap right_img=Bitmap.createBitmap(fullsize_bitmap,fullsize_bitmap.getWidth()/2,0,fullsize_bitmap.getWidth()/2,fullsize_bitmap.getHeight());

							OutputStream outputStream,outputStream2,outputStreamFull;

							try { 
								outputStream = new FileOutputStream ( Environment.getExternalStorageDirectory().getPath()+"/Studio3D/img_left.jpg");
								left_img.compress(CompressFormat.JPEG, 100, outputStream);

								outputStream2 = new FileOutputStream ( Environment.getExternalStorageDirectory().getPath()+"/Studio3D/img_right.jpg");
								right_img.compress(CompressFormat.JPEG, 100, outputStream2);

								outputStreamFull = new FileOutputStream ( Environment.getExternalStorageDirectory().getPath()+"/Studio3D/img_full.jpg");
								fullsize_bitmap.compress(CompressFormat.JPEG, 100, outputStreamFull);

								
								File sdCardDirectory = Environment.getExternalStorageDirectory();
						        File storedImagesDir = new File (sdCardDirectory.getAbsolutePath()+"/Studio3D/images");
						       
						        if(!storedImagesDir.exists())
						        	storedImagesDir.mkdirs();
						        
								to.renameTo(new File(Environment.getExternalStorageDirectory()+"/Studio3D/images/"+storedImagesDir.listFiles().length+".jpg"));
								
								File thumbsImagesDir = new File (sdCardDirectory.getAbsolutePath()+"/Studio3D/images/cache");
							       
								 if(!thumbsImagesDir.exists())
									 thumbsImagesDir.mkdirs();
								
								numFiles=thumbsImagesDir.listFiles().length;
								File currentFolder=new File (sdCardDirectory.getAbsolutePath()+"/Studio3D/images/cache/"+numFiles);
								currentFolder.mkdirs();
							    
								outputStreamFull = new FileOutputStream ( sdCardDirectory.getAbsolutePath()+"/Studio3D/images/cache/"+numFiles+"/img_full.jpg");
								fullsize_bitmap.compress(CompressFormat.JPEG, 100, outputStreamFull);
								
								outputStream2 = new FileOutputStream (  sdCardDirectory.getAbsolutePath()+"/Studio3D/images/cache/"+numFiles+"/img_left.jpg");
								right_img.compress(CompressFormat.JPEG, 100, outputStream2);

							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} 

							Log.d("yp", "blah");

							//Intent it = new Intent("com.tesseract.studio3d.Animation.MainActivity");
							Intent it = new Intent(activityContext,MainScreen.class);
	
							if (null != it)
								{
										
									it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									it.putExtra("browseButtonClicked", false);
					        
									activityContext.startActivity(it);
									
									Log.d("custom","starting");
								}

						}

						catch (ActivityNotFoundException e)
						{;
						//oops, no such application
						}


					}// fileSaved = "New photo Saved: " + file;
				}
			}
		};

		observer.startWatching(); // start the observer
	}
	
	static public int getNumFiles()
	{
		return numFiles;
	}
	

}
