package com.tesseract.studio3d.replace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.tesseract.studio3d.R;
import com.tesseract.studio3d.refocus.Refocus;

public class MainListViewActivity extends Activity
{
	
	ListView mainList;
	
	String[] values = new String[] { "Places","Beach" };

    final ArrayList<String> list = new ArrayList<String>();
   
	protected void onCreate(Bundle savedInstanceState) 
	{
		
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	
		setContentView(R.layout.categorylist);
		
		mainList=(ListView)findViewById(R.id.categoryList);
		
		for (int i = 0; i < values.length; ++i) {
		      list.add(values[i]);
		    }
		
		CustomListAdapter listAdapter = new CustomListAdapter(MainListViewActivity.this , R.layout.custom_list , list);
		
		mainList.setAdapter(listAdapter);
		
		mainList.setOnItemClickListener(new OnItemClickListener() {
			  @Override
			  public void onItemClick(AdapterView<?> parent, View view,
			    int position, long id) {
			
				  Log.d("Category"," value "+values[position]);
				  Intent i=new Intent(getBaseContext(),ReplaceActivity.class);
				  i.putExtra("Category",values[position]);
				  
				  startActivity(i);
				  
			  }
			}); 
		
	}

}
