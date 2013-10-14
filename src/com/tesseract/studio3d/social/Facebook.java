	package com.tesseract.studio3d.social;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.ViewGroup;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;
import com.tesseract.studio3d.R;


public class Facebook {
	//// Facebook Variables .
	
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private final String PENDING_ACTION_BUNDLE_KEY = "com.facebook.samples.hellofacebook:PendingAction";

	
	  private enum PendingAction {
	        NONE,
	        POST_PHOTO,
	        POST_STATUS_UPDATE
	    }	
	
	private PendingAction pendingAction = PendingAction.NONE;
    private ViewGroup controlsContainer;
    public GraphUser user;
    private LoginButton loginButton;
    private ProfilePictureView profilePictureView;
    
    static UiLifecycleHelper uiHelper;
    Context activityContext;

    public Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
           ;// onSessionStateChange(session, state, exception);
        }
    };

    
    
    Facebook(Context context,Bundle instanceState)
    {
    	
	activityContext=context;
    	
    	
    }
    
    
    void initializeFacebook()
    {
    
    	
    }
    
  

    
    public void handlePendingAction() {
        PendingAction previouslyPendingAction = pendingAction;
        // These actions may re-set pendingAction if they are still pending, but we assume they
        // will succeed.
        pendingAction = PendingAction.NONE;

        switch (previouslyPendingAction) {
            case POST_PHOTO:
                //postPhoto();
                break;
            
        }
    }
    
    
    private boolean hasPublishPermission() {
        Session session = Session.getActiveSession();
        return session != null && session.getPermissions().contains("publish_actions");
    }

//    private void performPublish(PendingAction action) {
//        Session session = Session.getActiveSession();
//        if (session != null) {
//            pendingAction = action;
//            if (hasPublishPermission()) {
//                // We can do the action right away.
//                handlePendingAction();
//            } else {
//                // We need to get new permissions, then complete the action when we get called back.
//                session.requestNewPublishPermissions(new Session.NewPermissionsRequest(activityContext, PERMISSIONS));
//            }
//        }
//    }
//    
//    public void onClickPostPhoto() {
//        performPublish(PendingAction.POST_PHOTO);
//    }
//
//    private void postPhoto() {
//    	
//        if (hasPublishPermission()&&isImageClicked) {
//          //  Bitmap image = BitmapFactory.decodeResource(this.getResources(), R.drawable.icon);
//            Request request = Request.newUploadPhotoRequest(Session.getActiveSession(), ((BitmapDrawable)mImageView.getDrawable()).getBitmap(), new Request.Callback() {
//                @Override
//                public void onCompleted(Response response) {
//                	
//                    showPublishResultactivityContext.(getString(R.string.photo_post), response.getGraphObject(), response.getError());
//                }
//            });
//            request.executeAsync();
//        } else {
//            pendingAction = PendingAction.POST_PHOTO;
//        }
//    }
//    
//    private interface GraphObjectWithId extends GraphObject {
//        String getId();
//    }
    
    private interface GraphObjectWithId extends GraphObject {
        String getId();
    }
    
    private void showPublishResult(String message, GraphObject result, FacebookRequestError error) {
        String title = null;
        String alertMessage = null;
        if (error == null) {
            title = activityContext.getString(R.string.success);
            String id = result.cast(GraphObjectWithId.class).getId();
            alertMessage = activityContext.getString(R.string.successfully_posted_post, message, id);
        } else {
            title = activityContext.getString(R.string.error);
            alertMessage = error.getErrorMessage();
        }

        new AlertDialog.Builder(activityContext)
                .setTitle(title)
                .setMessage(alertMessage)
                .setPositiveButton(R.string.ok, null)
                .show();
    }


    
    
    
    public void printHashKey() {

        try {
            PackageInfo info = activityContext.getPackageManager().getPackageInfo("com.facebook.samples.hellofacebook",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("TEMPTAGHASH KEY:",
                        Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

    }
    
    

}
