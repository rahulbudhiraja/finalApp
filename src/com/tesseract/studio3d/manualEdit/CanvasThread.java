package com.tesseract.studio3d.manualEdit;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class CanvasThread extends Thread {
   private SurfaceHolder _surfaceHolder;
   private Panel _panel;
   private static boolean _run = false;

   public CanvasThread(SurfaceHolder surfaceHolder, Panel panel) {
       _surfaceHolder = surfaceHolder;
       _panel = panel;
   }

   public static void setRunning(boolean run) {
       _run = run;
   }

  

   public void run() {
       Canvas c;
       while (_run) {
           c = null;
           
           for(int i=0;i<2;i++)
           {
	            try 
	            {
	                c = _surfaceHolder.lockCanvas();
	                
	                synchronized (_surfaceHolder) {
	                    _panel.onDraw(c);
	                }
	            } 
	            finally {
	                // do this in a finally so that if an exception is thrown
	                // during the above, we don't leave the Surface in an
	                // inconsistent state
	                if (c != null) {
	                    _surfaceHolder.unlockCanvasAndPost(c);
	                }
	            }
           }
       }
   }
}
