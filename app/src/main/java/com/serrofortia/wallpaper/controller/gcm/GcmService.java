package com.serrofortia.wallpaper.controller.gcm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.serrofortia.wallpaper.R;
import com.serrofortia.wallpaper.SplashActivity;
import com.serrofortia.wallpaper.util.Loger;
import com.serrofortia.wallpaper.util.Tools;


public class GcmService extends IntentService  {

	private final static String FOLDER_DESTINATION = "Subfi";
	private static final int NOTIFICATION_ID = 33;
	protected final String TAG = this.getClass().getSimpleName();

	public GcmService() {
		super("TAG");
		
		
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		try {
			Bundle extras = intent.getExtras();
			GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

			// The getMessageType() intent parameter must be the intent you
			// received
			// in your BroadcastReceiver.
			String messageType = gcm.getMessageType(intent);

			if (!extras.isEmpty()) { // has effect of unparcelling Bundle
				/*
				 * Filter messages based on message type. Since it is likely
				 * that GCM will be extended in the future with new message
				 * types, just ignore any message types you're not interested
				 * in, or that you don't recognize.
				 */
				if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
						.equals(messageType)) {
					sendNotification("Send error: " + extras.toString(),null);
				} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
						.equals(messageType)) {
					sendNotification("Deleted messages on server: "
							+ extras.toString(),null);
					// If it's a regular GCM message, do some work.
				} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
						.equals(messageType)) {
					
					if(extras.getString("type") != null && extras.getString("type").equals("recommend")){
						String recommenderName = extras.getString("recommender_name");
						String groupName = extras.getString("group_name");
						String userName = extras.getString("user_name");
						String message = getString(R.string.friend_was_recommended_notification,userName,recommenderName,groupName);
						sendNotification(message,extras);

						
					}else if(extras.getString("type") != null && extras.getString("type").equals("invite")){
						String administratorName = extras.getString("administrator_name");
						String groupName = extras.getString("group_name");
						String message = getString(R.string.invited_notification,administratorName,groupName);
						sendNotification(message,extras);

						
					}else{
					
						// This loop represents the service doing some work.
						String wallpaper = extras.getString("wallpaper_path");
						
						Loger.d(TAG, "Wallpaper url : "+wallpaper);
						WallpaperManager myWallpaperManager = WallpaperManager
								.getInstance(getApplicationContext());
						try {
							InputStream input = new URL(wallpaper).openStream();
							Point dim = Tools.getScreenDimensions(this);
							
							Bitmap bitmap = Bitmap.createBitmap(dim.y,dim.y,Config.ARGB_8888);
							
							Bitmap bitmapOriginalSize = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(input),dim.x,dim.y,true);
							
							Bitmap overlayBitmap = overlay(bitmap, bitmapOriginalSize);
							
							
							
							//myWallpaperManager.setWallpaperOffsetSteps(1, 1);
							//myWallpaperManager.suggestDesiredDimensions(dim.x/2, dim.y);
							myWallpaperManager.setBitmap(overlayBitmap);
							
							// Save file
							String destination_directory = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"
												+ FOLDER_DESTINATION;

							File directory = new File(destination_directory);
							if(!directory.exists()){
								directory.mkdir();
							}
							
							File file = new File(directory, new Random(System.currentTimeMillis()).nextInt(100000000)+10000+".jpg");
							FileOutputStream fOut = new FileOutputStream(file);
							
							bitmapOriginalSize.compress(Bitmap.CompressFormat.PNG, 100, fOut);
							fOut.flush();
							fOut.close();
							
							addGallery(file);

							
							
							
						} catch (Exception e) {
							Loger.e(TAG, "",e);
						}
					
					
			//		int group_id = extras.getInt("group_id");
			//		int user_id = extras.getInt("user_id");
					String title = extras.getString("title");
					
				//	WallpaperApp app = (WallpaperApp)getApplication();
				//	String groupName = app.getGroupById(group_id).name;
				//	String userName =  app.getUserById(user_id).name;  
	
					String msg = getResources().getString(R.string.notification_message,title);
	
					sendNotification(msg,null);
					Log.i(TAG, "Received: " + extras.toString());
					}
				}
			}
		} catch (Exception e) {
			Loger.e(TAG, "", e);
		} finally {
			//WakefulBroadcastReceiver.completeWakefulIntent(intent);
		}
	}
	
	
	private void addGallery(File file) {
	    Intent mediaScanIntent = new Intent(
	            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	   
	    Uri contentUri = Uri.fromFile(file);
	    mediaScanIntent.setData(contentUri);
	    this.sendBroadcast(mediaScanIntent);
	}
	
	private Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        int top = 0;
        int left = bmp1.getWidth()/2 - bmp2.getWidth()/2 - 2;
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, left,top, null);
        return bmOverlay;
    }
	
    private void sendNotification(String msg,Bundle extras) {
    	NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

    	Intent intent = new Intent(this, SplashActivity.class);
    	
    	if(extras != null){
    		intent.putExtras(extras);
    	}
    	
    	PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
    	

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setContentTitle(getString(R.string.app_name))
        .setContentText(msg)
        .setSmallIcon(R.drawable.ic_launcher).setAutoCancel(true);
        

        mBuilder.setContentIntent(contentIntent);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
	
	
	

	
	

	
	
}
