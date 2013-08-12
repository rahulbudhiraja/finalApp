package com.tesseract.studio3d.social;

import java.util.Arrays;
import java.util.List;

import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.tesseract.studio3d.R;

public class SocialSharing extends FragmentActivity
{
	//Facebook facebookClass;
	
	ImageView filteredImageView;
	EditText captionText;
	RelativeLayout rellayout;
	ImageButton postPhotoButton;
	private PendingAction pendingAction = PendingAction.NONE;
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	Bitmap canvasBitmap;
	ImageButton facebookButton,twitterButton,settingsButton;
	
	boolean fbButtonSelected=false,twButtonSelected=false;
	
	private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(final Session session, final SessionState state, final Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
	
	int buttonAlpha=40;
    
private enum PendingAction 
{	
        NONE,
        POST_PHOTO,
        POST_STATUS_UPDATE
}

private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	// Check if the user is authenticated and
    // a deep link needs to be handled.
  if (state.isOpened()) {
		// Make the recipe list visible
	  buttonAlpha=255;		
    } else if (state.isClosed()) {
    	// Make the recipe list hidden
      buttonAlpha=25;
    }

postPhotoButton.setAlpha(buttonAlpha);

}

public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    uiHelper.onSaveInstanceState(outState);
}


public void onResume() {
    super.onResume();
    
    // For scenarios where the main activity is launched and user
	// session is not null, the session state change notification
	// may not be triggered. Trigger it if it's open/closed.
	Session session = Session.getActiveSession();
	if (session != null &&
			(session.isOpened() || session.isClosed()) ) {
		onSessionStateChange(session, session.getState(), null);
	}
	
    uiHelper.onResume();
}

@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    uiHelper.onActivityResult(requestCode, resultCode, data);
}

@Override
public void onPause() {
    super.onPause();
    uiHelper.onPause();
}

@Override
public void onDestroy() {
    super.onDestroy();
    uiHelper.onDestroy();
}

protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);
	    
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	
		setContentView(R.layout.sharing);
		
		filteredImageView=new ImageView(this);
		captionText=(EditText)findViewById(R.id.editText1);
		
	
		String fileName=Environment.getExternalStorageDirectory().getAbsolutePath()+"/Studio3D/combined.png";
		
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
	            	if(fbButtonSelected)
	                onClickPostPhoto();
	            }
	        });
		
		facebookButton=(ImageButton) findViewById(R.id.facebookButton);
		twitterButton=(ImageButton)findViewById(R.id.twitterButton);
		settingsButton=(ImageButton)findViewById(R.id.settingsButton);
		
		facebookButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	
                updateFacebookButton();
            }
        });
		
		
		twitterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                updateTwitterButton();
            }
        });
		
		
		settingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                launchSettingsActivity();
            }
        });
		
		
		
		postPhotoButton.setAlpha(buttonAlpha);
	}

protected void launchSettingsActivity() 
{
	Intent it=new Intent(this,SettingsActivity.class);
	this.startActivity(it);
	
}
protected void updateTwitterButton() {
	// TODO Auto-generated method stub
	twButtonSelected=!twButtonSelected;
}

protected void updateFacebookButton() {
	// TODO Auto-generated method stub
	
	
	fbButtonSelected=!fbButtonSelected;
	Log.d("Button slected","selected:"+fbButtonSelected);
	
	Log.d("facebook api"," "+hasPublishPermission());
	if(fbButtonSelected)
		{
		facebookButton.setImageResource(R.drawable.facebookbutton);
		//postPhotoButton.setVisibility(View.VISIBLE);
		buttonAlpha=255;
		}
	else {
		facebookButton.setImageResource(R.drawable.facebook_gray);
		fbButtonSelected=false;
		//postPhotoButton.setVisibility(View.INVISIBLE);
		buttonAlpha=25;
	}
	postPhotoButton.setAlpha(buttonAlpha);
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
	  

	    private void postPhoto() {
	        if (hasPublishPermission()) {
	          //  Bitmap image = BitmapFactory.decodeResource(this.getResources(), R.drawable.icon);
	            Request request = Request.newUploadPhotoRequest(Session.getActiveSession(),canvasBitmap, new Request.Callback() {
	                @Override
	                public void onCompleted(Response response) {
	                	
	                    showPublishResult(getString(R.string.photo_post), response.getGraphObject(), response.getError());
	                }
	            });
	            request.executeAsync();
	        } else {
	            pendingAction = PendingAction.POST_PHOTO;
	        }
	    }
	  private boolean hasPublishPermission() {
	        Session session = Session.getActiveSession();
	        Log.d("Values ","Session: "+session+session.getPermissions().contains("publish_actions"));
	        return session != null && session.getPermissions().contains("publish_actions");
	    }
	  private void handlePendingAction() {
		    PendingAction previouslyPendingAction = pendingAction;
		    // These actions may re-set pendingAction if they are still pending, but we assume they
		    // will succeed.
		    pendingAction = PendingAction.NONE;

		    switch (previouslyPendingAction) {
		        case POST_PHOTO:
		           postPhoto();	
		            break;
		        
		    }
		}
	  
	    private void showPublishResult(String message, GraphObject result, FacebookRequestError error) {
	        String title = null;
	        String alertMessage = null;
	        if (error == null) {
	            title = getString(R.string.success);
	            String id = result.cast(GraphObjectWithId.class).getId();
	            alertMessage = getString(R.string.successfully_posted_post, message, id);
	        } else {
	            title = getString(R.string.error);
	            alertMessage = error.getErrorMessage();
	        }

	        new AlertDialog.Builder(this)
	                .setTitle(title)
	                .setMessage(alertMessage)
	                .setPositiveButton(R.string.ok, null)
	                .show();
	    }
	    
	    private interface GraphObjectWithId extends GraphObject {
	        String getId();
	    }

	
}
