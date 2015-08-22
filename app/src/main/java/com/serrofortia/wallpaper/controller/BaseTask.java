package com.serrofortia.wallpaper.controller;

import android.os.AsyncTask;

import com.serrofortia.wallpaper.BaseActivity;
import com.serrofortia.wallpaper.network.NetworkClient;
import com.serrofortia.wallpaper.network.model.requests.BaseRequest;
import com.serrofortia.wallpaper.network.model.responses.BaseResponse;
import com.serrofortia.wallpaper.ui.components.AlertLoading;

public abstract class BaseTask<T extends BaseRequest, U, V extends BaseResponse> extends AsyncTask<T, U, V>{

	protected BaseTaskListener<V> listener;
	protected NetworkClient networkClient;
	private boolean showLoading;
	private AlertLoading alertLoading;
	
	public BaseTask(BaseActivity activity, BaseTaskListener<V> listener,boolean showLoading){
		init(activity, listener, showLoading);
	}
	
	public BaseTask(BaseActivity activity,BaseTaskListener<V> listener){
		init(activity, listener, true);
	}
	
	
	private void init(BaseActivity activity,BaseTaskListener<V> listener,boolean showLoading){
		this.listener = listener;
		this.showLoading = showLoading;
		networkClient = new NetworkClient();
		alertLoading = new AlertLoading(activity.getContentView(),activity);
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(showLoading){
			alertLoading.show();
		}
	}
	
	@Override
	protected void onPostExecute(V result) {
		super.onPostExecute(result);
		if(showLoading){
			alertLoading.hide();
		}

		if(result != null && result.status == BaseResponse.STATUS_OK){
			listener.onSuccess(result);
		}else{
			listener.onFailed(result);
//			String errorMessage = "Erreur";
//			if(result != null){
//				errorMessage = result.message;
//			}
//			
//			Tools.showError(activity,errorMessage);

		}
	}
	
	

}
