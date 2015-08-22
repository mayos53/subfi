package com.serrofortia.wallpaper.controller;

import android.app.Activity;

import com.serrofortia.wallpaper.BaseActivity;
import com.serrofortia.wallpaper.network.model.requests.ConfirmCodeRequest;
import com.serrofortia.wallpaper.network.model.responses.BaseResponse;

public class ConfirmCodeTask extends BaseTask<ConfirmCodeRequest, Integer, BaseResponse>{

	public ConfirmCodeTask(BaseActivity activity,BaseTaskListener<BaseResponse> listener) {
		super(activity,listener);
	}

	@Override
	protected BaseResponse doInBackground(ConfirmCodeRequest... request) {
		return (BaseResponse)networkClient.confirm(request[0]);
	}

}
