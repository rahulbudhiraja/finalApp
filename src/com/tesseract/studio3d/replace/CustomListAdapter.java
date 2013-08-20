package com.tesseract.studio3d.replace;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tesseract.studio3d.R;

class CustomListAdapter extends ArrayAdapter {

    private Context mContext;
    private int id;
    private List <String>items ;

    public CustomListAdapter(Context context, int textViewResourceId , List<String> list ) 
    {
        super(context, textViewResourceId, list);           
        mContext = context;
        id = textViewResourceId;
        items = list ;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent)
    {
        View mView = v ;
        if(mView == null){
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mView = vi.inflate(id, null);
        }

        TextView text = (TextView) mView.findViewById(R.id.listTextView);

        if(items.get(position) != null )
        {
        	text.setTextSize(20);
            text.setTextColor(Color.WHITE);
            text.setText(items.get(position));
            text.setBackgroundColor(Color.RED);
            
            
            int color = Color.argb( 200, 255, 64, 64 );
                text.setBackgroundColor( color );

        }

        return mView;
    }

}