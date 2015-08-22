package com.serrofortia.wallpaper.ui.components;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.serrofortia.wallpaper.R;
import com.serrofortia.wallpaper.util.FontHelper;
import com.serrofortia.wallpaper.util.FontHelper.Font;

public class TextViewExtended extends TextView {

	

	public TextViewExtended(Context context, AttributeSet attrs) {
		super(context, attrs);

		// To be displayed also in edit mode
		if (!isInEditMode())
		{
			

			// Get dictionary key
			TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.TextViewExtended, 0, 0);

			
			setCustomFont(context, attributes);
			
			
			
			attributes.recycle();
		}
		
		// Tools.RTL_CHAR
	}

	private void setCustomFont(Context ctx, TypedArray attributes) {

		// Get the attribute
		//TypedArray attributes = ctx.obtainStyledAttributes(attrs, R.styleable.TextViewExtended, 0, 0);

		if (attributes.hasValue(R.styleable.TextViewExtended_typefaceCustom)) {
			// Find the font according to index location
			Font fontToUse = Font.mapIntToValue(attributes.getInteger(R.styleable.TextViewExtended_typefaceCustom, 0));

			// Set the font as the font to be used in this view
			FontHelper fontHelper = FontHelper.getInstance(getContext().getApplicationContext().getResources().getAssets(),
					"");
			setTypeface(fontHelper.getFont(fontToUse));
		}
	}
	
	

	@Override
	public void setCompoundDrawablePadding(int pad) {
		super.setCompoundDrawablePadding(pad);
		
		
	}
}