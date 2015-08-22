package com.serrofortia.wallpaper;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.serrofortia.wallpaper.util.Tools;

public abstract class BaseSlidingActivity extends BaseActivity {

	protected MenuDrawer mMenuDrawer;
	//private View btnInvitations;
	private View btnRecommendations;
	private View btnTerms;
	private View btnGroups;


	

	@Override
	public void onCreate(Bundle savedInstanceState) {

		mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.STATIC);
		super.onCreate(savedInstanceState);
		// THIS HAPPENS: mMenuDrawer.setContentView(getActivityLayout());

		// Set Menu view
		mMenuDrawer.setMenuView(R.layout.sliding_menu);
		
		mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);
		

		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		float getPix = Tools.convertDpToPixel(44.5f,this);
		mMenuDrawer.setMenuSize((int)(width - getPix));
//		mMenuDrawer.setDropShadow(getResources().getDrawable(R.drawable.shdw_upper_pane));
//		mMenuDrawer.setDropShadowSize(getResources().getDrawable(R.drawable.shdw_upper_pane).getIntrinsicWidth());

		

		btnGroups = mMenuDrawer.findViewById(R.id.btnGroups);
	//	btnInvitations = mMenuDrawer.findViewById(R.id.btnInvitations);
		btnRecommendations = mMenuDrawer.findViewById(R.id.btnRecommendations);
		btnTerms = mMenuDrawer.findViewById(R.id.btnTermsAndConditions);
		

		
		btnGroups.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				
				startActivity(new Intent(BaseSlidingActivity.this, GroupsActivity.class));
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						mMenuDrawer.closeMenu();
					}
				}, 1000);
				setPressed(btnGroups);
				
			}
			
		});
		
		/*
		btnInvitations.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				
				startActivity(new Intent(BaseSlidingActivity.this, InvitationsActivity.class));
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						mMenuDrawer.closeMenu();
					}
				}, 1000);
				setPressed(btnInvitations);

			}
		});
		*/
		
		btnRecommendations.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				
				startActivity(new Intent(BaseSlidingActivity.this, RecommendationsActivity.class));
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						mMenuDrawer.closeMenu();
					}
				}, 1000);
				setPressed(btnRecommendations);

			}
		});
		
		btnTerms.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(BaseSlidingActivity.this, ConditionsActivity.class);
				intent.putExtra(ConditionsActivity.INTENT_FROM_MENU, true);
				startActivity(intent);
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						mMenuDrawer.closeMenu();
					}
				}, 1000);
				setPressed(btnTerms);

			}
		});
		
		

			
		
		
		
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			mMenuDrawer.toggleMenu();
			return true;
		}

		return super.onKeyUp(keyCode, event);
	}
	
	@Override
	public boolean onActionBarItemSelected(View action_item) {
		if(action_item.getId() == R.id.menu){
			mMenuDrawer.toggleMenu();
			return true;
		}
		return super.onActionBarItemSelected(action_item);
	}

//	@Override
//	public void onHomeActionClick() {
//		mMenuDrawer.toggleMenu();
//		//dennis shar:category disabled,mAnalytics.sendEvent("analytics_event_ui_sidemenu_clicktoggle", null);
//	}

	
	
	

	// public void resetDrawerSlideMode(int position) {
	// mMenuDrawer.setTouchMode(position == 0 ? MenuDrawer.TOUCH_MODE_FULLSCREEN
	// : MenuDrawer.TOUCH_MODE_NONE);
	// }

	public boolean handleMenuState() {
		final int drawerState = mMenuDrawer.getDrawerState();
		if (drawerState == MenuDrawer.STATE_OPEN || drawerState == MenuDrawer.STATE_OPENING) {
			mMenuDrawer.closeMenu();
			return true;
		}

		return false;
	}

	
	public void setPressed(View item){
		btnGroups.setPressed(false);
		//btnInvitations.setPressed(false);
		btnRecommendations.setPressed(false);
		btnTerms.setPressed(false);

		item.setPressed(true);
		
	}
	

	@Override
	public void onBackPressed() {
		if(!handleMenuState()){
			super.onBackPressed();
		}
	}
	

	
}
