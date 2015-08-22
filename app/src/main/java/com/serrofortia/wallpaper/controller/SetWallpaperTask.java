package com.serrofortia.wallpaper.controller;

import android.graphics.Bitmap.CompressFormat;

import com.serrofortia.wallpaper.BaseActivity;
import com.serrofortia.wallpaper.network.model.requests.SetWallpaperRequest;
import com.serrofortia.wallpaper.network.model.responses.SetWallpaperResponse;
import com.serrofortia.wallpaper.util.Tools;

public class SetWallpaperTask extends BaseTask<SetWallpaperRequest, Integer, SetWallpaperResponse>{

	private String editedPath;
	private String compressedPath;
	
	public SetWallpaperTask(BaseActivity activity,BaseTaskListener<SetWallpaperResponse> listener) {
		super(activity,listener);
	}

	@Override
	protected SetWallpaperResponse doInBackground(SetWallpaperRequest... request) {
		
		try {
			editedPath = request[0].path;
			compressedPath = Tools.compressFile(editedPath, CompressFormat.JPEG, 60);
			request[0].path = compressedPath; 
			return (SetWallpaperResponse)networkClient.setWallpaper(request[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	@Override
	protected void onPostExecute(SetWallpaperResponse result) {
		Tools.deleteFile(editedPath);
		Tools.deleteFile(compressedPath);
		super.onPostExecute(result);
	}

}
