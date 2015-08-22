package com.serrofortia.wallpaper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.google.gson.Gson;
import com.serrofortia.wallpaper.model.UserDetails;
import com.serrofortia.wallpaper.network.model.responses.BaseResponse;
import com.serrofortia.wallpaper.ui.components.AlertBuilder;
import com.serrofortia.wallpaper.ui.components.AlertBuilder.ButtonListener;
import com.serrofortia.wallpaper.util.Loger;
import com.serrofortia.wallpaper.util.Tools;


public class SplashActivity extends BaseActivity{
	
	WallpaperApp mApp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
//		
//		final AlertLoading alertLoading =  new AlertLoading(getContentView(), this);
//		new Handler().postDelayed(new Runnable() {
//		    public void run() {
//		    	alertLoading.show();
//		    	}
//		}, 100);
	
		
		
		
		
//		String picturePath = "/storage/emulated/0/Download/Lancement02.jpg";
//		Intent intent = new Intent(this, PaintActivity.class);
//		intent.putExtra(PaintActivity.EXTRA_PICTURE_PATH, picturePath);
//		
//		startActivity(intent);

		if(Tools.isNetworkConnected(this)){
			mApp = (WallpaperApp)getApplication();
			Intent i = null;
			UserDetails ud = mApp.getUserDetails();
			if(ud != null){
				if(!mApp.isConfirmed()){
					i = new Intent(SplashActivity.this,ConfirmActivity.class);
					startActivity(i);
					finish();
				}else{
					mApp.registerToGCM();
					mApp.getGroups(this,false,getIntent().getExtras());
					
				}
			}else{
				i = new Intent(SplashActivity.this,RegisterActivity.class);
				startActivity(i);
				finish();
			}
		}else{
			
			final Handler handler = new Handler();

			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					AlertBuilder.showMessage(getContentView(), SplashActivity.this,
							R.string.no_connection, false,
							new ButtonListener() {

								@Override
								public void onClick() {
									finish();

								}
							}, null);
				}
			}, 500);
		
			
			
			
		}
			
			
//		final String str = readAsset();
//		final String url = "http://ec2-54-191-37-134.us-west-2.compute.amazonaws.com:3000/users/get_contacts.json";
//		
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				BaseResponse response = postData(url,str,BaseResponse.class);
//				Loger.d("postData", response.message+" "+response.status);				
//			}
//		}).start();
		
		
		
		
	}

	@Override
	public View getTitleLayout() {
		return null;
	}
		
	public String readAsset(){
		BufferedReader reader = null;
		StringBuffer sb = new StringBuffer();
		try {
		    reader = new BufferedReader(
		        new InputStreamReader(getAssets().open("test")));

		    // do reading, usually loop until end of file reading  
		    String mLine = reader.readLine();
		    while (mLine != null) {
		       //process line
		    	sb.append(mLine);
		       mLine = reader.readLine();
		       
		    }
		} catch (IOException e) {
		    //log the exception
		} finally {
		    if (reader != null) {
		         try {
		             reader.close();
		         } catch (IOException e) {
		             //log the exception
		         }
		    }
		}
		return sb.toString();
	}
	
	public BaseResponse postData(String url, String json,Class responseClass) {
		InputStream inputStream = null;
		BaseResponse result = null;
		
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();

			// url with the post data
			HttpPost httpost = new HttpPost(url);

			
			
			// passes the results to a string builder/entity
			StringEntity se = new StringEntity(json);

			// sets the post request as the resulting string
			httpost.setEntity(se);
			// sets a request header so the page receving the request
			// will know what to do with it
			httpost.setHeader("Content-type", "application/json; charset=utf-8");

			
			HttpResponse response = httpclient.execute(httpost);
			inputStream = response.getEntity().getContent();
			if (inputStream != null){
				String str = Tools.convertInputStreamToString(inputStream);
				Log.d("postData","postData response : "+str);

				result = (BaseResponse)new Gson().fromJson(str, responseClass);
			}
			
		} catch (Exception e) {
			Log.e("postData", null, e);
		}
		return result;

	}
	
	
	
}


//package com.serrofortia.wallpaper;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//
//import com.serrofortia.wallpaper.model.UserDetails;
//import com.serrofortia.wallpaper.ui.components.AlertBuilder;
//import com.serrofortia.wallpaper.ui.components.AlertBuilder.ButtonListener;
//import com.serrofortia.wallpaper.util.Tools;
//
//public class SplashActivity extends Activity{
//	
//	WallpaperApp mApp;
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		
//		final View view = LayoutInflater.from(this).inflate(R.layout.splash, null);
//		setContentView(view);
//		
////		String picturePath = "/storage/emulated/0/Download/Lancement02.jpg";
////		Intent intent = new Intent(this, PaintActivity.class);
////		intent.putExtra(PaintActivity.EXTRA_PICTURE_PATH, picturePath);
////		
////		startActivity(intent);
//
//		if(Tools.isNetworkConnected(this)){
//			mApp = (WallpaperApp)getApplication();
//			Intent i = null;
//			UserDetails ud = mApp.getUserDetails();
//			if(ud != null){
//				if(!mApp.isConfirmed()){
//					i = new Intent(SplashActivity.this,ConfirmActivity.class);
//					startActivity(i);
//					finish();
//				}else{
//					mApp.registerToGCM();
//					mApp.getGroups(this,false,getIntent().getExtras());
//					
//				}
//			}else{
//				i = new Intent(SplashActivity.this,RegisterActivity.class);
//				startActivity(i);
//				finish();
//			}
//		}else{
//			Button btn = (Button)view.findViewById(R.id.button);
//			btn.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					AlertBuilder.showMessage(view,SplashActivity.this, R.string.no_connection, true, new ButtonListener() {
//						
//						@Override
//						public void onClick() {
//							finish();
//							
//						}
//					},new ButtonListener() {
//						
//						@Override
//						public void onClick() {
//							finish();
//							
//						}
//					});
//					
//				}
//			});
//			
//			
//		}
//			
//			
//			
//		
//	}
//		
//	
//	
//	
//	
//	
//	
//}
