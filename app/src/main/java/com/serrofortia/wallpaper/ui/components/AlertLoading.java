package com.serrofortia.wallpaper.ui.components;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.serrofortia.wallpaper.R;
import com.serrofortia.wallpaper.util.Tools;

public class AlertLoading {

	private View parent;
	private ImageView icon;
	private Animation rotateAnim;
	private PopupWindow pw;
	private Activity activity;

	public AlertLoading(View parent, Activity activity) {
		this.parent = parent;
		this.activity = activity;
		View view = LayoutInflater.from(activity).inflate(
				R.layout.popup_loading, null);
		int width = (int) Tools.convertDpToPixel(307, activity);
		int height = (int) Tools.convertDpToPixel(145, activity);

		pw = new PopupWindow(width, height);
		pw.setContentView(view);
		pw.setBackgroundDrawable(new BitmapDrawable());

		String message = activity.getResources().getString(
				R.string.loading_message);

		((TextView) view.findViewById(R.id.message)).setText(message);
		icon = ((ImageView) view.findViewById(R.id.icon));

		Drawable drawable = activity.getResources().getDrawable(
				R.drawable.icn_popup);
		int h = drawable.getIntrinsicHeight();
		int w = drawable.getIntrinsicWidth();

		int x = w / 2;
		int y = h / 2;

		rotateAnim = AnimationUtils.loadAnimation(activity, R.anim.anim_rotate);
		rotateAnim.setRepeatCount(Animation.INFINITE);

		// rotateAnim = new RotateAnimation(0, 360,x,y);
		// rotateAnim.setDuration(1500);
		// rotateAnim.setRepeatCount(Animation.INFINITE);
		// rotateAnim.setInterpolator(new AccelerateInterpolator());

	}

	public void show() {

		icon.startAnimation(rotateAnim);

		// Drawable d = new ColorDrawable(Color.RED);
		// d.setAlpha(255);
		//
		new Handler().postDelayed(new Runnable() {
			public void run() {
				pw.showAtLocation(parent, Gravity.CENTER, 0, 0);
			}
		}, 100);

		// activity.getWindow().setBackgroundDrawable(d);

	}

	public void hide() {
		// Drawable d = new ColorDrawable(Color.WHITE);
		//
		// activity.getWindow().setBackgroundDrawable(d);
		rotateAnim.cancel();
		pw.dismiss();

	}

}
