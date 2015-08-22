package com.serrofortia.wallpaper.ui.components;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.serrofortia.wallpaper.R;
import com.serrofortia.wallpaper.util.Tools;

public class AlertBuilder {

	public interface ButtonListener {
		public void onClick();
	}

	public static void showMessage(View parent, Activity activity, int message,
			boolean cancelButton, final ButtonListener okListener,
			final ButtonListener cancelListener) {
		showMessage(parent, activity, activity.getString(message),
				cancelButton, okListener, cancelListener, 0, 0);
	}

	public static void showMessage(View parent, Activity activity,
			String message, boolean cancelButton,
			final ButtonListener okListener, final ButtonListener cancelListener) {
		showMessage(parent, activity, message, cancelButton, okListener,
				cancelListener, 0, 0);
	}

	public static void showMessage(View parent, Activity activity,
			String message, boolean cancelButton,
			final ButtonListener okListener,
			final ButtonListener cancelListener, int okButtonText,
			int cancelButtonText) {

		View view = LayoutInflater.from(activity).inflate(
				R.layout.popup_message, null);
		int width = (int) Tools.convertDpToPixel(307, activity);
		int height = (int) Tools.convertDpToPixel(145, activity);

		final PopupWindow pw = new PopupWindow(width, height);
		pw.setContentView(view);

		// TextView title = (TextView)view.findViewById(R.id.title);
		TextView text = (TextView) view.findViewById(R.id.text);

		// Yossi remove title
		// title.setVisibility(View.GONE);

		// title.setText("MESSAGE");
		text.setText(message);

		View viewYesNo = view.findViewById(R.id.yes_no);
		View viewYes = view.findViewById(R.id.yes);

		if (cancelButton) {
			viewYesNo.setVisibility(View.VISIBLE);
			viewYes.setVisibility(View.GONE);

			TextView ok = (TextView) viewYesNo.findViewById(R.id.btn_ok);
			TextView cancel = (TextView) viewYesNo
					.findViewById(R.id.btn_cancel);

			ok.setText(activity.getString(okButtonText > 0 ? okButtonText
					: android.R.string.yes));

			cancel.setText(activity
					.getString(cancelButtonText > 0 ? cancelButtonText
							: android.R.string.no));

			cancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					pw.dismiss();
					if (cancelListener != null) {
						cancelListener.onClick();
					}

				}
			});
			ok.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					pw.dismiss();
					if (okListener != null) {
						okListener.onClick();
					}

				}
			});

		} else {
			viewYesNo.setVisibility(View.GONE);
			viewYes.setVisibility(View.VISIBLE);

			TextView ok = (TextView) viewYes.findViewById(R.id.btn_ok);
			ok.setText(activity.getString(okButtonText > 0 ? okButtonText
					: android.R.string.yes));

			ok.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					pw.dismiss();
					if (okListener != null) {
						okListener.onClick();
					}

				}
			});

		}
		pw.showAtLocation(parent, Gravity.CENTER, 0, 0);

	}

}
