package com.serrofortia.wallpaper.controller;

import com.serrofortia.wallpaper.BaseActivity;
import com.serrofortia.wallpaper.network.model.requests.ResendCodeRequest;
import com.serrofortia.wallpaper.network.model.responses.BaseResponse;

public class ResendCodeTask extends BaseTask<ResendCodeRequest, Integer, BaseResponse>{

	public ResendCodeTask(BaseActivity activity,BaseTaskListener<BaseResponse> listener) {
		super(activity,listener);
	}

	@Override
	protected BaseResponse doInBackground(ResendCodeRequest... request) {
		return (BaseResponse)networkClient.resendCode(request[0]);
	}

}
