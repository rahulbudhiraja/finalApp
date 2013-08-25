package com.tesseract.studio3d.Animation;

import java.util.Random;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.CornerPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.tesseract.studio3d.R;

public class FullScreenEditorView extends ImageView
{
	Mat fgMat,bgMat;
	Mat converted_fgMat,converted_bgMat;
	String bg_filter,fg_filter;
	
	Bitmap fgBmp,bgBmp;
	Paint paint;
	
	String[] imageFilters = { "sepia", "stark", "sunnyside", "cool", "worn",
			"grayscale","vignette","crush","sunny","night" };
	private String TAG="FullScreenEditor";


	public FullScreenEditorView(Context context,String filter1,String filter2)
	{
		super(context);
		
		initializeMats();
		
		
		fg_filter=filter1;
		bg_filter=filter2;
		
		Log.d(TAG,"selected "+fg_filter);
		Log.d(TAG,"selected "+bg_filter);
		
		// Load the mats from disk
		fgMat=Highgui.imread(Environment.getExternalStorageDirectory().getPath()+"/Studio3D/Layers/img_fg.png");
		bgMat=Highgui.imread(Environment.getExternalStorageDirectory().getPath()+"/Studio3D/Layers/img_bg.png");
		
		// Convert it into RGBA
		Imgproc.cvtColor(fgMat, converted_fgMat, Imgproc.COLOR_BGR2RGBA);
		Imgproc.cvtColor(bgMat, converted_bgMat, Imgproc.COLOR_BGR2RGBA);
		// Show it into the view ..
		
		fgBmp=Bitmap.createBitmap(fgMat.cols(),fgMat.rows(), 
        		 Bitmap.Config.ARGB_8888);
		
		bgBmp=Bitmap.createBitmap(bgMat.cols(),bgMat.rows(), 
       		 Bitmap.Config.ARGB_8888);
		
		Utils.matToBitmap(converted_fgMat, fgBmp);
	    Utils.matToBitmap(converted_bgMat, bgBmp);
	    
	    applyFiltertoBitmap(fgBmp,fg_filter);
	    applyFiltertoBitmap(bgBmp,bg_filter);
	    
	    fgBmp=BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()+"/Studio3D/Layers/img_fg.png");
	    bgBmp=BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()+"/Studio3D/Layers/img_bg.png");
	    
	    
	    applyFiltertoBitmap(fgBmp,fg_filter);
	    applyFiltertoBitmap(bgBmp,bg_filter);
	    
		// TODO Auto-generated constructor stub
	}
	
	public void configurePaint()
    {
    	paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		
     //   paint.setColor(Color.RED);                    // set the color
        paint.setStrokeWidth(3);               // set the size
        paint.setDither(true);                    // set the dither to true
        paint.setStyle(Paint.Style.STROKE);       // set to STOKE
        paint.setStrokeJoin(Paint.Join.ROUND);    // set the join to round you want
        paint.setStrokeCap(Paint.Cap.ROUND);      // set the paint cap to round too
        paint.setPathEffect(new CornerPathEffect(20) );   // set the path effect when they join.
        paint.setAntiAlias(true);  
        
        
        
    }
	public void initializeMats()
	{
		
		fgMat =new Mat();
		bgMat=new Mat();
		converted_fgMat=new Mat();
		converted_bgMat=new Mat();
		
	}
	
	 public void onDraw(Canvas canvas)
	    {
	    	super.onDraw(canvas);
	      
	    	
	    	canvas.drawBitmap(fgBmp, 0, 0,paint); 
		    canvas.drawBitmap(bgBmp, 0, 0,paint);
		    
	    }
	
		public void applyFiltertoBitmap(Bitmap imgViewBitmap,String filtName) 
		{
			// Bitmap
			// imgViewBitmap=((BitmapDrawable)CanvasImageViews.get(currentSelectedLayer).getDrawable()).getBitmap();
			//Bitmap imgViewBitmap = layerBitmaps.get(currentSelectedLayer);

			Bitmap modifiedBitmap = Bitmap.createScaledBitmap(imgViewBitmap,
					imgViewBitmap.getWidth(), imgViewBitmap.getHeight(), true);
			Canvas imageViewCanvas = new Canvas(modifiedBitmap);

			imageViewCanvas.drawBitmap(modifiedBitmap, 0, 0, new Paint());

			ColorMatrix cm = new ColorMatrix();

			String filterName = filtName;

			if (filterName.equalsIgnoreCase("stark")) {

				Paint spaint = new Paint();
				ColorMatrix scm = new ColorMatrix();

				scm.setSaturation(0);
				final float m[] = scm.getArray();
				final float c = 1;
				scm.set(new float[] { m[0] * c, m[1] * c, m[2] * c, m[3] * c,
						m[4] * c + 15, m[5] * c, m[6] * c, m[7] * c, m[8] * c,
						m[9] * c + 8, m[10] * c, m[11] * c, m[12] * c, m[13] * c,
						m[14] * c + 10, m[15], m[16], m[17], m[18], m[19] });

				spaint.setColorFilter(new ColorMatrixColorFilter(scm));
				Matrix smatrix = new Matrix();
				imageViewCanvas.drawBitmap(modifiedBitmap, smatrix, spaint);

				cm.set(new float[] { 1, 0, 0, 0, -90, 0, 1, 0, 0, -90, 0, 0, 1, 0,
						-90, 0, 0, 0, 1, 0 });

			} else if (filterName.equalsIgnoreCase("sunnyside")) {

				cm.set(new float[] { 1, 0, 0, 0, 10, 0, 1, 0, 0, 10, 0, 0, 1, 0,
						-60, 0, 0, 0, 1, 0 });
			} else if (filterName.equalsIgnoreCase("worn")) {

				cm.set(new float[] { 1, 0, 0, 0, -60, 0, 1, 0, 0, -60, 0, 0, 1, 0,
						-90, 0, 0, 0, 1, 0 });
			} else if (filterName.equalsIgnoreCase("grayscale")) {

				float[] matrix = new float[] { 0.3f, 0.59f, 0.11f, 0, 0, 0.3f,
						0.59f, 0.11f, 0, 0, 0.3f, 0.59f, 0.11f, 0, 0, 0, 0, 0, 1,
						0, };

				cm.set(matrix);

			} else if (filterName.equalsIgnoreCase("cool")) {

				cm.set(new float[] { 1, 0, 0, 0, 10, 0, 1, 0, 0, 10, 0, 0, 1, 0,
						60, 0, 0, 0, 1, 0 });

			} else if (filterName.equalsIgnoreCase("filter0")) {

				cm.set(new float[] { 1, 0, 0, 0, 30, 0, 1, 0, 0, 10, 0, 0, 1, 0,
						20, 0, 0, 0, 1, 0 });

			} else if (filterName.equalsIgnoreCase("filter1")) {

				cm.set(new float[] { 1, 0, 0, 0, -33, 0, 1, 0, 0, -8, 0, 0, 1, 0,
						56, 0, 0, 0, 1, 0 });

			} else if (filterName.equalsIgnoreCase("night")) {

				cm.set(new float[] { 1, 0, 0, 0, -42, 0, 1, 0, 0, -5, 0, 0, 1, 0,
						-71, 0, 0, 0, 1, 0 });

			} else if (filterName.equalsIgnoreCase("crush")) {

				cm.set(new float[] { 1, 0, 0, 0, -68, 0, 1, 0, 0, -52, 0, 0, 1, 0,
						-15, 0, 0, 0, 1, 0 });

			} else if (filterName.equalsIgnoreCase("filter4")) {

				cm.set(new float[] { 1, 0, 0, 0, -24, 0, 1, 0, 0, 48, 0, 0, 1, 0,
						59, 0, 0, 0, 1, 0 });

			} else if (filterName.equalsIgnoreCase("sunny")) {

				cm.set(new float[] { 1, 0, 0, 0, 83, 0, 1, 0, 0, 45, 0, 0, 1, 0, 8,
						0, 0, 0, 1, 0 });

			} else if (filterName.equalsIgnoreCase("filter6")) {

				cm.set(new float[] { 1, 0, 0, 0, 80, 0, 1, 0, 0, 65, 0, 0, 1, 0,
						81, 0, 0, 0, 1, 0 });

			} else if (filterName.equalsIgnoreCase("filter7")) {

				cm.set(new float[] { 1, 0, 0, 0, -44, 0, 1, 0, 0, 38, 0, 0, 1, 0,
						46, 0, 0, 0, 1, 0 });

			} else if (filterName.equalsIgnoreCase("filter8")) {

				cm.set(new float[] { 1, 0, 0, 0, 84, 0, 1, 0, 0, 63, 0, 0, 1, 0,
						73, 0, 0, 0, 1, 0 });

			} else if (filterName.equalsIgnoreCase("random")) {

				// pick an integer between -90 and 90 apply
				int min = -90;
				int max = 90;
				Random rand = new Random();

				int five = rand.nextInt(max - min + 1) + min;

				int ten = rand.nextInt(max - min + 1) + min;
				int fifteen = rand.nextInt(max - min + 1) + min;

				Log.d(TAG, "five " + five);
				Log.d(TAG, "ten " + ten);
				Log.d(TAG, "fifteen " + fifteen);

				cm.set(new float[] { 1, 0, 0, 0, five, 0, 1, 0, 0, ten, 0, 0, 1, 0,
						fifteen, 0, 0, 0, 1, 0 });

			} else if (filterName.equalsIgnoreCase("sepia")) {

				float[] sepMat = { 0.3930000066757202f, 0.7689999938011169f,
						0.1889999955892563f, 0, 0, 0.3490000069141388f,
						0.6859999895095825f, 0.1679999977350235f, 0, 0,
						0.2720000147819519f, 0.5339999794960022f,
						0.1309999972581863f, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1 };
				cm.set(sepMat);
			}
			else if(filterName.equalsIgnoreCase("vignette"))
			{

				Bitmap border = null;
				Bitmap scaledBorder = null;

				border = BitmapFactory.decodeResource(this.getResources(), R.drawable.vignette);

				int width = modifiedBitmap.getWidth();
				int height = modifiedBitmap.getHeight();

				scaledBorder = Bitmap.createScaledBitmap(border,width,height, false);
				if (scaledBorder != null && border != null) {
					imageViewCanvas.drawBitmap(scaledBorder, 0, 0, new Paint());
				}
			}

			Paint paint = new Paint();

			paint.setColorFilter(new ColorMatrixColorFilter(cm));
			Matrix matrix = new Matrix();

			if(!filterName.equalsIgnoreCase("vignette"))
				imageViewCanvas.drawBitmap(modifiedBitmap, matrix, paint);

			
	
		}

}
