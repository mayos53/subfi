package com.serrofortia.wallpaper;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ConditionsActivity extends BaseSlidingActivity{

	public final static String INTENT_FROM_MENU = "INTENT_FROM_MENU";
	
	protected final String TAG = this.getClass().getSimpleName();
	protected WallpaperApp mApp;
	
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.conditions);
    
    	
    	
    }
    
   
	
	@Override
	public boolean onActionBarItemSelected(View action_item) {
		switch(action_item.getId()){
			case R.id.back:
				finish();
		    return true;	

		}

		return super.onActionBarItemSelected(action_item);
	}


	@Override
	public View getTitleLayout() {
		ViewGroup view = null;
		if(!getIntent().getExtras().getBoolean(INTENT_FROM_MENU)){
			view = (ViewGroup)getLayoutInflater().inflate(R.layout.title_conditions, null);
		}else{
			view = (ViewGroup)getLayoutInflater().inflate(R.layout.title_invitations, null);
			((TextView)view.findViewById(R.id.text)).setText(R.string.conditions_title);
		}
		return view;
	}
}