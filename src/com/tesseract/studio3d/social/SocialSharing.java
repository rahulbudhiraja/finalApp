package com.tesseract.studio3d.social;

import java.util.Arrays;
import java.util.List;

import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.tesseract.studio3d.R;



import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SocialSharing extends FragmentActivity
{
	//Facebook facebookClass;
	
	ImageView filteredImageView;
	EditText captionText;
	RelativeLayout rellayout;
	ImageButton postPhotoButton;
	private PendingAction pendingAction = PendingAction.NONE;
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	

    
private enum PendingAction 
{	
        NONE,
        POST_PHOTO,
        POST_STATUS_UPDATE
}

private void handlePendingAction() {
    PendingAction previouslyPendingAction = pendingAction;
    // These actions may re-set pendingAction if they are still pending, but we assume they
    // will succeed.
    pendingAction = PendingAction.NONE;

    switch (previouslyPendingAction) {
        case POST_PHOTO:
      ;//      postPhoto();
            break;
        
    }
}

protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	
		setContentView(R.layout.sharing);
		
		filteredImageView=new ImageView(this);
		captionText=(EditText)findViewById(R.id.editText1);
		
	
		String fileName=Environment.getExternalStorageDirectory().getAbsolutePath()+"/Studio3D/combined.png";
		Bitmap canvasBitmap;

		canvasBitmap = BitmapFactory.decodeFile(fileName);
		
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		
		
		Log.d("studio3d","Width ="+canvasBitmap.getWidth()+ " height ="+canvasBitmap.getHeight());
		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
		layoutParams.addRule(RelativeLayout.BELOW, captionText.getId());

		
		filteredImageView.setImageBitmap(canvasBitmap);
		filteredImageView.setLayoutParams(layoutParams);
		
		rellayout=(RelativeLayout)findViewById(R.id.sharinglayout);
		rellayout.addView(filteredImageView);

		postPhotoButton=(ImageButton) findViewById(R.id.postpic);
		postPhotoButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	                onClickPostPhoto();
	            }
	        });
		
		
	}
public void onActivityResult(int requestCode, int resultCode, Intent data) {

     super.onActivityResult(requestCode, resultCode, data);
     Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
 }

	
	  private void onClickPostPhoto() {
	        performPublish(PendingAction.POST_PHOTO);
	        Log.d("Clicked","clicked");
	    }
	  
	  private void performPublish(PendingAction action) {
	        Session session = Session.getActiveSession();
	        if (session != null) {
	            pendingAction = action;
	            if (hasPublishPermission()) {
	                // We can do the action right away.
	                handlePendingAction();
	            } else {
	                // We need to get new permissions, then complete the action when we get called back.
	                session.requestNewPublishPermissions(new Session.NewPermissionsRequest(this, PERMISSIONS));
	            }
	        }
	    }
	  private boolean hasPublishPermission() {
	        Session session = Session.getActiveSession();
	        return session != null && session.getPermissions().contains("publish_actions");
	    }
	
}
