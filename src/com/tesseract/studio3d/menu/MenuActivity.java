package com.tesseract.studio3d.menu;

import com.tesseract.studio3d.manualEdit.CanvasActivity;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public class MenuActivity extends Activity 
{
	int menuSettings=Menu.FIRST;
	private int group1Id = 1;
	private String filter_fg="cool",filter_bg="sepia";
	
	public boolean onCreateOptionsMenu(Menu menu) 
	{

	    menu.add(group1Id, menuSettings, menuSettings, "Edit Layers");
	  
	    return super.onCreateOptionsMenu(menu); 
    }
	public boolean onOptionsItemSelected(MenuItem item) {

	    switch (item.getItemId()) {

	case 1:
	    
		Intent it = new Intent(this,CanvasActivity.class);
		
		if (null != it)
			{
				it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				
				it.putExtra("filter_fg",filter_fg);
				it.putExtra("filter_bg", filter_bg);
			
				this.startActivity(it);
			}   
		break;

	default:
	    break;

	       }
	    return super.onOptionsItemSelected(item);
	}

  
	

}
