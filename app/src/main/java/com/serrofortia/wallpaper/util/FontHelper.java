package com.serrofortia.wallpaper.util;

import java.util.ArrayList;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.Log;

public class FontHelper {

	
	public static String FONTS_PATH = "fonts/";
	public static String CURRENT_LANG = "en";
	public static String LANG_ENG = "en";
	public static String LANG_IW = "iw";
	public static String LANG_IW2 = "he"; // Need to fix double ids
	public static String LANG_AR = "ar";
	private static String loaded_lang = "";

	public enum Font {
		HELVETICA_NEUE_LIGHT(0),
		HELVETICA_NEUE_REGULAR(1),
		HELVETICA_NEUE_BOLD(2);
		

//		protected String mPath;
		protected int mIndex;

		private Font(int attributeIndex) {
//			this.mPath = null;
			this.mIndex = attributeIndex;
		}
		
//		private Font(String path, int index) {
//			Log.e("FontHelper", "" + index + " : " + path);
//			this.mPath = path;
//			this.mIndex = index;
//		}

		public static Font mapIntToValue(int index) {
			// Find the requested font
			for (Font currFont : Font.values()) {
				if (currFont.mIndex == index) {
					return currFont;
				}
			}

			return null;
		}
	}

	// English fonts
	private static String[] en = {
			"fonts/en/Helvetica_Light.ttf",
			"fonts/en/Helvetica_Regular.otf",
			"fonts/en/Roboto_Bold.ttf"};
			
//	private static String[] iw = {
//			"fonts/iw/Narkis-Bold.ttf",
//			"fonts/iw/Narkis-Bold.ttf",
//			"fonts/iw/Narkis-Medium.ttf",
//			"fonts/iw/Narkis-Light.ttf",
//			"fonts/iw/Narkis-Medium.ttf",
//			"fonts/iw/Narkis-Medium.ttf" };
//	// Arabic fonts
//	private static String[] ar = {
//			"fonts/ar/Tahoma-Bold.ttf",
//			"fonts/ar/Tahoma-Bold.ttf",
//			"fonts/ar/Tahoma.ttf",
//			"fonts/ar/Tahoma.ttf",
//			"fonts/ar/Tahoma.ttf",
//			"fonts/ar/Tahoma.ttf" };

	private static FontHelper mInstance;
	private ArrayList<Typeface> mFonts;

	public static FontHelper getInstance(AssetManager assetManager, String lang) {

		// Set fonts lang
		if(lang!=null && (lang.equals(LANG_IW) || lang.equals(LANG_IW2) || lang.equals(LANG_AR))){
			CURRENT_LANG = lang;
		}else {
			CURRENT_LANG = LANG_ENG;
		}
				
		if (mInstance == null) {
			mInstance = new FontHelper(assetManager);
		}else{
			// If lang changed reload fonts
			mInstance.rebuild(assetManager);
		}

		
		return mInstance;
	}

	private FontHelper(AssetManager assetManager) {

		rebuild(assetManager);
	}

	public void rebuild(AssetManager assetManager) {
		
		// Load fonts ones per language 
		if(CURRENT_LANG.equals(loaded_lang))
			return;
		
		// Set last loaded lang flag
		loaded_lang = CURRENT_LANG;

		// Set current lang font path
		String[] _pathes;
//		if(CURRENT_LANG.equals(LANG_IW)||CURRENT_LANG.equals(LANG_IW2)){
//			_pathes = iw;
//		}else if(CURRENT_LANG.equals(LANG_AR)){
//			_pathes = ar;
//		}else {
//			_pathes = en;
//		}
		
		_pathes = en;
		
		Log.e("FontHelper", "Font lang: "+ loaded_lang);
		
		mFonts = new ArrayList<Typeface>();

		// Run on all fonts in enum
		for (Font currFont : Font.values()) {

			// This try-catch section solves known bug:
			// https://code.google.com/p/android/issues/detail?id=9904
			// By setting the default typeface if an error occured
			try {
				// Create the font from assets
				Typeface createFromAsset = Typeface.createFromAsset(assetManager, _pathes[currFont.mIndex]);
				Log.e("FontHelper", "loading font: "+ _pathes[currFont.mIndex]);
				// Add the font to the fonts array
				// The index in the array will be the same as in the attribute
				// file
				mFonts.add(currFont.mIndex, createFromAsset);
			} catch (Exception e) {
				mFonts.add(currFont.mIndex, Typeface.DEFAULT);
			}
		}
	}

	public Typeface getFont(Font fontToReturn) {
		return (mFonts.get(fontToReturn.mIndex));
	}
}
