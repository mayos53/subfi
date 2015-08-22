package com.serrofortia.wallpaper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;

public abstract class BaseActivity extends SherlockActivity{
	
	
	
	ActionBar actionBar;
	protected View mainView;
	
	protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			
	// Action bar setup
			actionBar = getSherlock().getActionBar();
			
			//Yossi:
			// Change customView from LinearLayout to RelativeLayout like in xml
			// Change was made because we add line at bottom of ActionBar
			if (actionBar != null) {
				
				
				// Add LayoutParams to stretch custom view 
//				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//				params.weight = 1;
//				custom_view.setLayoutParams(params);

				View titleLayout = getTitleLayout();
				if(titleLayout != null){
					actionBar.setCustomView(titleLayout);
					actionBar.setDisplayShowCustomEnabled(true);
					actionBar.setDisplayShowTitleEnabled(false);
					actionBar.setDisplayShowHomeEnabled(false);
					// setHomeAction();
	
					bindClickListenerToEachItem((ViewGroup)titleLayout);
				}
			}

	}
	
	@Override
	public void setContentView(int layoutResId) {
		super.setContentView(layoutResId);
		mainView = LayoutInflater.from(this).inflate(layoutResId, null);
	}
	
	@Override
	public void setContentView(View view) {
		super.setContentView(view);
		mainView = view;
	}
	
	
	
	public View getContentView(){
		return mainView;
	}
	
	protected OnClickListener actionBarClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			onActionBarItemSelected(v);
		}
	};
	
	public void bindClickListenerToEachItem(ViewGroup actions_custom_layout) {
		// Yossi : 
		// In Item is ViewGroup bind clicklistener before children
		actions_custom_layout.setOnClickListener(actionBarClickListener);
		for (int i = 0; i < actions_custom_layout.getChildCount(); i++) {
			View view = actions_custom_layout.getChildAt(i);
			if (view instanceof ViewGroup && ((ViewGroup) view).getChildCount() > 0) {
				bindClickListenerToEachItem((ViewGroup) view);
			} else {
				actions_custom_layout.getChildAt(i).setOnClickListener(actionBarClickListener);
			}
		}
	}
	
	public boolean onActionBarItemSelected(View action_item) {
		return true;
	}
	
    
	
	public abstract View getTitleLayout();
}
