package com.serrofortia.wallpaper.ui.components;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.serrofortia.wallpaper.R;
import com.serrofortia.wallpaper.util.Tools;

public class AlertEditText {
	
	
	public interface ButtonListener{
		public void onClick(String text);
	}
	
	
	
	public static void showMessage(View parent,Activity activity, int _title, final ButtonListener okListener) {
		
		View view = LayoutInflater.from(activity).inflate(R.layout.popup_edit_text, null);
		int width = (int)Tools.convertDpToPixel(307, activity);
		int height = (int)Tools.convertDpToPixel(145, activity);

		
		
		final PopupWindow pw = new PopupWindow(width, height);
		pw.setContentView(view);

		
		TextView title = (TextView)view.findViewById(R.id.title);
		final EditText text = (EditText)view.findViewById(R.id.text);
		

		
	    title.setText(_title);

		View viewYes = view.findViewById(R.id.yes);

			TextView ok = (TextView)viewYes.findViewById(R.id.btn_ok);
			ok.setText(activity.getString(android.R.string.yes));

			ok.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					pw.dismiss();
					if(okListener != null){
						pw.dismiss();
						okListener.onClick(text.getText().toString());
					}
					
					
				}
			});

			
		
		
			pw.showAtLocation(parent, Gravity.CENTER, 0, 0);
			pw.setFocusable(true);
			pw.update();

		
	}
	
	
	


}
