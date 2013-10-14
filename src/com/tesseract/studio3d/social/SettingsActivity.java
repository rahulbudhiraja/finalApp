package com.tesseract.studio3d.social;

import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;


import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;
import com.tesseract.studio3d.R;


public class SettingsActivity extends FragmentActivity
{

	//Facebook facebookClass;
	 private LoginButton loginButton;
	 UiLifecycleHelper uiHelper;
	 private GraphUser user;
	 private PendingAction pendingAction = PendingAction.NONE;
	 
	 
	 private Session.StatusCallback callback = new Session.StatusCallback() {
	        @Override
	        public void call(Session session, SessionState state, Exception exception) {
	            onSessionStateChange(session, state, exception);
	        }
	    };
	    
	private enum PendingAction 
	{	
	        NONE,
	        POST_PHOTO,
	        POST_STATUS_UPDATE
	}
	
	protected void onCreate(Bundle savedInstanceState) 
	{
	
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.loginlayout);
		
   	    uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);

	    loginButton = (LoginButton) findViewById(R.id.login_button);
       
	    loginButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() 
	    {
            
            public void onUserInfoFetched(GraphUser user) {
               SettingsActivity.this.user = user;
               Log.d("User"," loggedin "+user);
                updateUI();
                // It's possible that we were waiting for this.user to be populated in order to post a
                // status update.
                handlePendingAction();
            }
        });
	
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	}
	
	   private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	        if (pendingAction != PendingAction.NONE &&
	                (exception instanceof FacebookOperationCanceledException ||
	                exception instanceof FacebookAuthorizationException)) {
	                new AlertDialog.Builder(SettingsActivity.this)
	                    .setTitle(R.string.cancelled)
	                    .setMessage(R.string.permission_not_granted)
	                    .setPositiveButton(R.string.ok, null)
	                    .show();
	            pendingAction = PendingAction.NONE;
	        } else if (state == SessionState.OPENED_TOKEN_UPDATED) {
	            handlePendingAction();
	        }
	        updateUI();
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
	
	  private void updateUI()
	  {
	        Session session = Session.getActiveSession();
	        
	        boolean enableButtons = (session != null && session.isOpened());

	    //    postPhotoButton.setEnabled(enableButtons);

	        // Put this in the Settings Activity ...
	        
//	        if (enableButtons && user != null) {
//	            profilePictureView.setProfileId(user.getId());
//
//	        } else {
//	            profilePictureView.setProfileId(null);
//
//	        }
	    }
	

}
