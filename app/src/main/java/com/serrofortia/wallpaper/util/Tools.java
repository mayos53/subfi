package com.serrofortia.wallpaper.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.serrofortia.wallpaper.R;
import com.serrofortia.wallpaper.WallpaperApp;
import com.serrofortia.wallpaper.model.Contact;
import com.serrofortia.wallpaper.model.Country;

public class Tools {

	
	protected final static String TAG = Tools.class.getSimpleName();

	public static final boolean DEBUG = true;
	public static final boolean SMS = false;

	
	public static final String REGEX_DIGIT = "[0-9]+";


	private static ProgressDialog progressDialog;
	
	public static void showLoading(Activity activity){
		String message = activity.getResources().getString(R.string.loading_message);
		 progressDialog= ProgressDialog.show(activity,"",message , true);
	}
	
	
	public static void hideLoading(){
		if(progressDialog != null){
			progressDialog.dismiss();
		}
	}


//	public static void showError(View parent, Activity activity, String message) {
//		
//		AlertBuilder.showMessage(parent, activity, message, false, null, null);
//	}
//	
//	
//	
//	
//	
//	public static void showError(View parent, Activity activity, int stringResource) {
//		showError(parent,activity, activity.getString(stringResource));
//		
//	}
//
//
//	public static void showError(View parent, Activity activity) {
//		showError(parent, activity,"Error");
//	}
	
	
	public static void showToast(Activity activity,int textResourceId) {
		Toast toast = Toast.makeText(activity, textResourceId, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	
	
	
	public static String getFilePathCompressed(String path){
		int index = path.lastIndexOf(".");
		return path.substring(0, index)+"_compressed"+path.substring(index);
	}
	
	public static String compressFile(String path,CompressFormat format,int quality) throws Exception {
		FileOutputStream fos = null;
		String compressedPath = Tools.getFilePathCompressed(path);
		try {

			fos = new FileOutputStream(compressedPath);
			BitmapFactory.decodeFile(path).compress(format,	quality, fos);
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
		return compressedPath;

	}
	
	public static boolean deleteFile(String path){
		File file = new File(path);
		return file.delete();
	}
	
	public static boolean isEmpty(String str){
		return !(str != null && !str.equals(""));
	}
	
	public static String  getRawResource(Context context,int rawResource){
		 InputStream is = null;
		 try{
			 is = context.getResources().openRawResource(rawResource);
			 return convertInputStreamToString(is);
		 }catch(Exception e){
			 Loger.e(TAG, "", e);
		 }
		 return null;
	   
	}
	
	public static String extractCountryCode(String phoneNumber,Map<String,Country> countryCodes){
		if(phoneNumber.length() > 3 && countryCodes.containsKey(phoneNumber.substring(0,3))){
			return phoneNumber.substring(0,3);
		}
		if(phoneNumber.length() > 2 && countryCodes.containsKey(phoneNumber.substring(0,2))){
			return phoneNumber.substring(0,2);
		}
		if(phoneNumber.length() > 1 && countryCodes.containsKey(phoneNumber.substring(0,1))){
			return phoneNumber.substring(0,1);
		}
		
		return null;
	}
	
	
	
	public static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;

	}
	
	public static Contact []  getAllPhoneNumbers(Context context){
		
		
		List<Contact> list = new ArrayList<Contact>();
		
		ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                  String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                  String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                  if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                	  Cursor phones = cr.query( 
		                       ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, 
		                       ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id, 
		                       null, null);
		             phones.moveToFirst();
                    
		             String number = phones.getString(phones.getColumnIndex("data1"));
                     Contact contact = buildContact(context, name, number);
                     if(contact != null){
                    	list.add(contact);
                     }
		             phones.close();

                    
                }
            }
            cur.close();
        }
        //Collections.sort(list);
        return list.toArray(new Contact[0]);
	}
	
	
	private static Contact buildContact(Context context,String name,String number){
		
		WallpaperApp app =((WallpaperApp)context.getApplicationContext());
		String countryCode = app.getUserDetails().countryCode;
		Map<String, Country> countryCodes = app.getCountryCodes();
		
		number = number.replace("-", "").replaceAll("[\\xA0]", "").replace(" ","").replace("(", "").replace(")", "").trim();
		if (number.startsWith("+") || number.startsWith("00")) {
			if (number.startsWith("+")) {
				number = number.substring(1);
			} else if (number.startsWith("00")) {
				number = number.substring(2);

			}
			countryCode = Tools.extractCountryCode(number, countryCodes);

		} else {

			if (number.startsWith("0")) {
				number = number.substring(1);
			}
			number = countryCode + number;
		}
		if (countryCode != null){// && number.matches(REGEX_DIGIT)) {
			Loger.d(TAG,number);
			return new Contact(number);
		}
		return null;
		
		//return new Contact(PhoneNumberUtils.formatNumber(number));
        
        
	}
	
	
	public static Point getScreenDimensions(Context context){
//	    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//		Display display = wm.getDefaultDisplay(); 
//		int width = display.getWidth();
//		int height = display.getHeight();
       
		DisplayMetrics metrics = new DisplayMetrics(); 
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels; 
        int width = metrics.widthPixels;
		return new Point(width,height);
	}
	
//	public static Bitmap convertToMutable(Bitmap imgIn) {
//	    try {
//	        //this is the file going to use temporally to save the bytes. 
//	        // This file will not be a image, it will store the raw image data.
//	        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp.tmp");
//
//	        //Open an RandomAccessFile
//	        //Make sure you have added uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
//	        //into AndroidManifest.xml file
//	        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
//
//	        // get the width and height of the source bitmap.
//	        int width = imgIn.getWidth();
//	        int height = imgIn.getHeight();
//	        Config type = imgIn.getConfig();
//
//	        //Copy the byte to the file
//	        //Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
//	        FileChannel channel = randomAccessFile.getChannel();
//	        MappedByteBuffer map = channel.map(MapMode.READ_WRITE, 0, imgIn.getRowBytes()*height);
//	        imgIn.copyPixelsToBuffer(map);
//	        //recycle the source bitmap, this will be no longer used.
//	        imgIn.recycle();
//	        System.gc();// try to force the bytes from the imgIn to be released
//
//	        //Create a new bitmap to load the bitmap again. Probably the memory will be available. 
//	        imgIn = Bitmap.createBitmap(width, height, type);
//	        map.position(0);
//	        //load it back from temporary 
//	        imgIn.copyPixelsFromBuffer(map);
//	        //close the temporary file and channel , then delete that also
//	        channel.close();
//	        randomAccessFile.close();
//
//	        // delete the temp file
//	        file.delete();
//
//	    } catch (FileNotFoundException e) {
//	        e.printStackTrace();
//	    } catch (IOException e) {
//	        e.printStackTrace();
//	    } 
//
//	    return imgIn;
//	}
	
	/**
	 * This method converts dp unit to equivalent pixels, depending on device density. 
	 * 
	 * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent px equivalent to dp depending on device density
	 */
	public static float convertDpToPixel(float dp, Context context){
	    Resources resources = context.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float px = dp * (metrics.densityDpi / 160f);
	    return px;
	}

	/**
	 * This method converts device specific pixels to density independent pixels.
	 * 
	 * @param px A value in px (pixels) unit. Which we need to convert into db
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent dp equivalent to px value
	 */
	public static float convertPixelsToDp(float px, Context context){
	    Resources resources = context.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float dp = px / (metrics.densityDpi / 160f);
	    return dp;
	}


	public static String formatTime(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Calendar cal = Calendar.getInstance();
		TimeZone tz = cal.getTimeZone();

		/* debug: is it local time? */
		sdf.setTimeZone(tz);
		
		
		return sdf.format(new Date(time * 1000l));
		
		
		
	}
	
	public static String getCurrentContactsVersion(Context context) {

        Cursor allContacts = context.getContentResolver().query(
                ContactsContract.RawContacts.CONTENT_URI, null, null, null, null);

        StringBuilder sbCurrentVersion = new StringBuilder();
        allContacts.moveToFirst();

        for (int i = 0; i < allContacts.getCount(); i++) {
            int col = allContacts.getColumnIndex(ContactsContract.RawContacts.VERSION);
            sbCurrentVersion.append(allContacts.getString(col));
            allContacts.moveToNext();
        }

        return sbCurrentVersion.toString();
    }
	
	public static String firstUpperCase(String input){
		String result = null;
		if(input != null){
			result = input.substring(0, 1).toUpperCase();
			if(input.length() > 1){
				result += input.substring(1);
			}
		}
		return result;

	}
	
	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			return false;
		} else
			return true;
	}
	
	public static void hideKeyboard(Context context, EditText et) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
	}

    public static String uriToFilename(Context context, Uri uri) {
        String path;
        if (Build.VERSION.SDK_INT < 11) {
            path = RealPathUtil.getRealPathFromURI_BelowAPI11(context, uri);
        } else if (Build.VERSION.SDK_INT < 19) {
            path = RealPathUtil.getRealPathFromURI_API11to18(context, uri);
        } else {
            path = RealPathUtil.getRealPathFromURI_API19(context, uri);
        }
        return path;
    }
	
	
}
