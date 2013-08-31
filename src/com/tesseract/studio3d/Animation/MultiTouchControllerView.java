package com.tesseract.studio3d.Animation;

import java.util.ArrayList;

import org.metalev.multitouch.controller.MultiTouchController;
import org.metalev.multitouch.controller.MultiTouchController.MultiTouchObjectCanvas;
import org.metalev.multitouch.controller.MultiTouchController.PointInfo;
import org.metalev.multitouch.controller.MultiTouchController.PositionAndScale;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.tesseract.studio3d.R;

public class MultiTouchControllerView extends View implements MultiTouchObjectCanvas<MultiTouchControllerView.Img> {

	private static final int[] IMAGES = { R.drawable.bgtexture};

	private ArrayList<Img> mImages = new ArrayList<Img>();

	// --

	private MultiTouchController<Img> multiTouchController = new MultiTouchController<Img>(this);

	// --

	private PointInfo currTouchPoint = new PointInfo();

	private boolean mShowDebugInfo = true;

	private static final int UI_MODE_ROTATE = 1, UI_MODE_ANISOTROPIC_SCALE = 2;

	private int mUIMode = UI_MODE_ROTATE;

	// --

	private Paint mLinePaintTouchPointCircle = new Paint();

	private String TAG="MultiTouchView";
	Bitmap img1,img2;
	Canvas img1Canvas,img2Canvas;
	
	Paint whitePaint,bluePaint;

	// ---------------------------------------------------------------------------------------------------

	public MultiTouchControllerView(Context context,Bitmap bmp) {
		//this(context, null); // original 
		
		super(context);
		init(context,bmp);
	}

	public MultiTouchControllerView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MultiTouchControllerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
		
	}

	private void init(Context context) {
		Resources res = context.getResources();
		
		for (int i = 0; i < IMAGES.length; i++)
			mImages.add(new Img(IMAGES[i], res));

		mLinePaintTouchPointCircle.setColor(Color.YELLOW);
		mLinePaintTouchPointCircle.setStrokeWidth(5);
		mLinePaintTouchPointCircle.setStyle(Style.STROKE);
		mLinePaintTouchPointCircle.setAntiAlias(true);
		setBackgroundColor(Color.BLACK);
		
		
		
	}
	
	private void init (Context context,Bitmap imgBitmap)
	{
		Resources res = context.getResources();
		
		
		mImages.add(new Img(new BitmapDrawable(imgBitmap),res));
		
		mLinePaintTouchPointCircle.setColor(Color.YELLOW);
		mLinePaintTouchPointCircle.setStrokeWidth(5);
		mLinePaintTouchPointCircle.setStyle(Style.STROKE);
		mLinePaintTouchPointCircle.setAntiAlias(true);
		setBackgroundColor(Color.BLACK);
		
		img1 =Bitmap.createBitmap(imgBitmap.getWidth(), imgBitmap.getHeight(), Config.ARGB_8888);
		img1Canvas=new Canvas(img1);
		
		img2 =Bitmap.createBitmap(imgBitmap.getWidth(), imgBitmap.getHeight(), Config.ARGB_8888);
		img2Canvas=new Canvas(img2);
		
		whitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		whitePaint.setColor(Color.WHITE);
		whitePaint.setStrokeWidth(3);  
		whitePaint.setStyle(Paint.Style.FILL);  
		
		bluePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		bluePaint.setColor(Color.BLUE);
		bluePaint.setStrokeWidth(3);  
		bluePaint.setStyle(Paint.Style.FILL);  
	}

	/** Called by activity's onResume() method to load the images */
	public void loadImages(Context context,boolean loadfromRes) {
		Resources res = context.getResources();
		int n = mImages.size();
		for (int i = 0; i < n; i++)
			mImages.get(i).load(res,loadfromRes);
	}
	
	public void setBitmap(Bitmap imgBitmap)
	{
		
		;
	}
	
	public void loadImage(Bitmap bm)
	{
		;
	}

	/** Called by activity's onPause() method to free memory used for loading the images */
	public void unloadImages() {
		int n = mImages.size();
		for (int i = 0; i < n; i++)
			mImages.get(i).unload();
	}

	// ---------------------------------------------------------------------------------------------------

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int n = mImages.size();
		
		for (int i = 0; i < n; i++)
			mImages.get(i).draw(canvas);
		
		
		if (mShowDebugInfo)
			drawMultitouchDebugMarks(canvas);
	}

	// ---------------------------------------------------------------------------------------------------

	public void trackballClicked() {
		mUIMode = (mUIMode + 1) % 3;
		invalidate();
	}

	private void drawMultitouchDebugMarks(Canvas canvas) {
		if (currTouchPoint.isDown()) {
			float[] xs = currTouchPoint.getXs();
			float[] ys = currTouchPoint.getYs();
			float[] pressures = currTouchPoint.getPressures();
			int numPoints = Math.min(currTouchPoint.getNumTouchPoints(), 2);
			
		if(mImages.get(0).minX<xs[0]&&mImages.get(0).maxX>xs[0]&&mImages.get(0).minY<ys[0]&&mImages.get(0).maxY>ys[0])	
		  {
			for (int i = 0; i < numPoints; i++)
		  		canvas.drawCircle(xs[i], ys[i], 50 + pressures[i] * 80, mLinePaintTouchPointCircle);
			if (numPoints == 2)
				canvas.drawLine(xs[0], ys[0], xs[1], ys[1], mLinePaintTouchPointCircle);
		  }
		
		
		if(FullScreenEditorActivity.activeLayer==1)
			img1Canvas.drawCircle(xs[0], ys[0],30,whitePaint);
		else if(FullScreenEditorActivity.activeLayer==2)
			img2Canvas.drawCircle(xs[0], ys[0],30,bluePaint);
		
		
		}
		
		Log.d(TAG,"status: "+currTouchPoint.getAction());
		Log.d(TAG,"touchDownStatus"+currTouchPoint.isDown());
		Log.d(TAG,"TouchPoint x "+currTouchPoint.getX());
		Log.d(TAG,"TouchPoint y"+ currTouchPoint.getY());
		
	}

	// ---------------------------------------------------------------------------------------------------

	/** Pass touch events to the MT controller */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return multiTouchController.onTouchEvent(event);
	}

	/** Get the image that is under the single-touch point, or return null (canceling the drag op) if none */
	public Img getDraggableObjectAtPoint(PointInfo pt) {
		float x = pt.getX(), y = pt.getY();
		int n = mImages.size();
		for (int i = n - 1; i >= 0; i--) {
			Img im = mImages.get(i);
			if (im.containsPoint(x, y))
				return im;
		}
		return null;
	}

	/**
	 * Select an object for dragging. Called whenever an object is found to be under the point (non-null is returned by getDraggableObjectAtPoint())
	 * and a drag operation is starting. Called with null when drag op ends.
	 */
	public void selectObject(Img img, PointInfo touchPoint) {
		currTouchPoint.set(touchPoint);
		if (img != null) {
			// Move image to the top of the stack when selected
			mImages.remove(img);
			mImages.add(img);
		} else {
			// Called with img == null when drag stops.
		}
		invalidate();
	}

	/** Get the current position and scale of the selected image. Called whenever a drag starts or is reset. */
	public void getPositionAndScale(Img img, PositionAndScale objPosAndScaleOut) {
		// FIXME affine-izem (and fix the fact that the anisotropic_scale part requires averaging the two scale factors)
		objPosAndScaleOut.set(img.getCenterX(), img.getCenterY(), (mUIMode & UI_MODE_ANISOTROPIC_SCALE) == 0,
				(img.getScaleX() + img.getScaleY()) / 2, (mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0, img.getScaleX(), img.getScaleY(),
				(mUIMode & UI_MODE_ROTATE) != 0, img.getAngle());
	}

	/** Set the position and scale of the dragged/stretched image. */
	public boolean setPositionAndScale(Img img, PositionAndScale newImgPosAndScale, PointInfo touchPoint) {
		currTouchPoint.set(touchPoint);
		 // Rahul :Comment this out ,if i
		
		Log.d(TAG,"status: "+touchPoint.getAction());
		Log.d(TAG,"touchDownStatus"+touchPoint.isDown());
		Log.d(TAG,"TouchPoint x "+touchPoint.getX());
		Log.d(TAG,"TouchPoint y"+ touchPoint.getY());
		
		
		if(FullScreenEditorActivity.activateViewMovement)
		{
			boolean ok = img.setPos(newImgPosAndScale);
			if (ok)
				;//invalidate();
		}
		invalidate();
		return false;
	}

	// ----------------------------------------------------------------------------------------------

	class Img {
		private int resId;

		private Drawable drawable;

		private boolean firstLoad;

		private int width, height, displayWidth, displayHeight;

		private float centerX, centerY, scaleX, scaleY, angle;

		private float minX, maxX, minY, maxY;

		private static final float SCREEN_MARGIN = 100;
		
		public Img(int resId, Resources res) {
			this.resId = resId;
			this.firstLoad = true;
			getMetrics(res);
		}
		
		// Rahul :adding function ..
		public Img(Drawable d,Resources res)
		{
			this.drawable=d;
			this.firstLoad = true;
			getMetrics(res);
		}
		
		private void getMetrics(Resources res) {
			DisplayMetrics metrics = res.getDisplayMetrics();
			// The DisplayMetrics don't seem to always be updated on screen rotate, so we hard code a portrait
			// screen orientation for the non-rotated screen here...
			// this.displayWidth = metrics.widthPixels;
			// this.displayHeight = metrics.heightPixels;
			this.displayWidth = res.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math.max(metrics.widthPixels,
					metrics.heightPixels) : Math.min(metrics.widthPixels, metrics.heightPixels);
			this.displayHeight = res.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math.min(metrics.widthPixels,
					metrics.heightPixels) : Math.max(metrics.widthPixels, metrics.heightPixels);
		}

		/** Called by activity's onResume() method to load the images */
		public void load(Resources res,boolean loadResource) {
			getMetrics(res);
			
			if(loadResource)
				this.drawable = res.getDrawable(resId);
			
			
			
			this.width = drawable.getIntrinsicWidth();
			this.height = drawable.getIntrinsicHeight();
			float cx, cy, sx, sy;
			if (firstLoad) {
				/* The below code randomizes the position of the Bitmap */
//				cx = SCREEN_MARGIN + (float) (Math.random() * (displayWidth - 2 * SCREEN_MARGIN));
//				cy = SCREEN_MARGIN + (float) (Math.random() * (displayHeight - 2 * SCREEN_MARGIN));
//				float sc = (float) (Math.max(displayWidth, displayHeight) / (float) Math.max(width, height) * Math.random() * 0.3 + 0.2);
//				sx = sy = sc;
				
				cx=displayWidth/2;cy=displayHeight/2;
				sx=(float) 1.5;
				sy=(float) 1.5;
				
				Log.d(TAG,"sx :  "+sx+ "sy:  "+sy);
				
				//sx=sy=1;
				
				firstLoad = false;
			} else {
				// Reuse position and scale information if it is available
				// FIXME this doesn't actually work because the whole activity is torn down and re-created on rotate
				cx = this.centerX;
				cy = this.centerY;
				sx = this.scaleX;
				sy = this.scaleY;
				// Make sure the image is not off the screen after a screen rotation
				if (this.maxX < SCREEN_MARGIN)
					cx = SCREEN_MARGIN;
				else if (this.minX > displayWidth - SCREEN_MARGIN)
					cx = displayWidth - SCREEN_MARGIN;
				if (this.maxY > SCREEN_MARGIN)
					cy = SCREEN_MARGIN;
				else if (this.minY > displayHeight - SCREEN_MARGIN)
					cy = displayHeight - SCREEN_MARGIN;
			}
			setPos(cx, cy, sx, sy, 0.0f);
		}

		// Rahul:Adding an alternative function ..
		
		
		
		
		/** Called by activity's onPause() method to free memory used for loading the images */
		public void unload() 
		{
			this.drawable = null;
		}

		/** Set the position and scale of an image in screen coordinates */
		public boolean setPos(PositionAndScale newImgPosAndScale) {
			return setPos(newImgPosAndScale.getXOff(), newImgPosAndScale.getYOff(), (mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0 ? newImgPosAndScale
					.getScaleX() : newImgPosAndScale.getScale(), (mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0 ? newImgPosAndScale.getScaleY()
					: newImgPosAndScale.getScale(), newImgPosAndScale.getAngle());
			// FIXME: anisotropic scaling jumps when axis-snapping
			// FIXME: affine-ize
			// return setPos(newImgPosAndScale.getXOff(), newImgPosAndScale.getYOff(), newImgPosAndScale.getScaleAnisotropicX(),
			// newImgPosAndScale.getScaleAnisotropicY(), 0.0f);
		}

		/** Set the position and scale of an image in screen coordinates */
		private boolean setPos(float centerX, float centerY, float scaleX, float scaleY, float angle) {
			float ws = (width / 2) * scaleX, hs = (height / 2) * scaleY;
			float newMinX = centerX - ws, newMinY = centerY - hs, newMaxX = centerX + ws, newMaxY = centerY + hs;
			if (newMinX > displayWidth - SCREEN_MARGIN || newMaxX < SCREEN_MARGIN || newMinY > displayHeight - SCREEN_MARGIN
					|| newMaxY < SCREEN_MARGIN)
				return false;
			this.centerX = centerX;
			this.centerY = centerY;
			this.scaleX = scaleX;
			this.scaleY = scaleY;
			this.angle = angle;
			this.minX = newMinX;
			this.minY = newMinY;
			this.maxX = newMaxX;
			this.maxY = newMaxY;
			return true;
		}

		/** Return whether or not the given screen coords are inside this image */
		public boolean containsPoint(float scrnX, float scrnY) {
			// FIXME: need to correctly account for image rotation
			return (scrnX >= minX && scrnX <= maxX && scrnY >= minY && scrnY <= maxY);
		}

		public void draw(Canvas canvas) 
		{
			canvas.save();
			float dx = (maxX + minX) / 2;
			float dy = (maxY + minY) / 2;
			drawable.setBounds((int) minX, (int) minY, (int) maxX, (int) maxY);
			
		
			canvas.translate(dx, dy);
			canvas.rotate(angle * 180.0f / (float) Math.PI);
			canvas.translate(-dx, -dy);
			
			drawable.draw(canvas);
			
			canvas.drawCircle(minX,minY, 30,mLinePaintTouchPointCircle);
			canvas.drawCircle(minX,maxY, 3,mLinePaintTouchPointCircle);
			canvas.drawCircle(maxX,minY, 10,mLinePaintTouchPointCircle);
			canvas.drawCircle(maxX,maxY, 10,mLinePaintTouchPointCircle);
			
			canvas.drawBitmap(img1, 0,0,null);
			canvas.drawBitmap(img2, 0,0,null);
			
			
			
			
			canvas.restore();
		}

		public Drawable getDrawable() {
			return drawable;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		public float getCenterX() {
			return centerX;
		}

		public float getCenterY() {
			return centerY;
		}

		public float getScaleX() {
			return scaleX;
		}

		public float getScaleY() {
			return scaleY;
		}

		public float getAngle() {
			return angle;
		}

		// FIXME: these need to be updated for rotation
		public float getMinX() {
			return minX;
		}

		public float getMaxX() {
			return maxX;
		}

		public float getMinY() {
			return minY;
		}

		public float getMaxY() {
			return maxY;
		}
	}
}

