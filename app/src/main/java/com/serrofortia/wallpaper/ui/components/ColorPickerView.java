/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.serrofortia.wallpaper.ui.components;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.serrofortia.wallpaper.R;
import com.serrofortia.wallpaper.util.Loger;
import com.serrofortia.wallpaper.util.Tools;



public class ColorPickerView extends RelativeLayout {
	
	private final static int INIT_COLOR = Color.RED;
	protected final String TAG = this.getClass().getSimpleName();
	private Context context;
	
	int topX;
	int topY;
	
	ImageView ivColors;
	
	public ColorPickerView(Context context) {
		super(context);
		inflate(context,R.layout.palette, this);
		
		
		
		
		this.context = context;
	}

		float []  hsv = new float []{0.5f,1f,1f};
      
	 	public interface OnColorChangedListener {
	        void colorChanged(int color);
	    }

	    private OnColorChangedListener mListener;
        

		
        public void setColorChangeListener(OnColorChangedListener listener){
        	mListener = listener;
        	mListener.colorChanged(INIT_COLOR);
        	invalidate();
        }
        
        @Override
        protected void onAttachedToWindow() {
           	super.onAttachedToWindow();
          
        }
        
        
        
        private int getRelativeLeft(View myView) {
            if (myView.getParent() == this)
                return myView.getLeft();
            else
                return myView.getLeft() + getRelativeLeft((View) myView.getParent());
        }

        private int getRelativeTop(View myView) {
            if (myView.getParent() == this)
                return myView.getTop();
            else
                return myView.getTop() + getRelativeTop((View) myView.getParent());
        }
        
        @Override
        public boolean onTouchEvent(MotionEvent event) {
        	  ivColors = (ImageView)findViewById(R.id.paint);
              topX = getRelativeLeft(ivColors);
              topY = getRelativeTop(ivColors);
           
        
        	float x = event.getX();
            float y = event.getY();
           
            int dX = (int)Tools.convertDpToPixel(15, context);
            int dY = (int)Tools.convertDpToPixel(5, context);
            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    mTrackingCenter = inCenter;
//                    if (inCenter) {
//                        mHighlightCenter = true;
//                        invalidate();
//                        break;
//                    }
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_UP:
                	if(x > topX - dX  && x < topX + ivColors.getWidth() + dX  
                	  && y > topY - dY && y < topY +  ivColors.getHeight() + dY){
	                	if(mListener != null){
	                		hsv[0]  = Math.max(0, Math.min(359,360 * (float)( ivColors.getHeight() - (y -  topY)) /  ivColors.getHeight()));
	                		
	                		int color = Color.HSVToColor(hsv);
	                		Loger.d(TAG, "hsv: "+hsv[0]+" color : "+color);
	                		mListener.colorChanged(color);
	                	}
	                	invalidate();
                	}
                break;
                    
            }
            return true;
        }
    }

 

    

