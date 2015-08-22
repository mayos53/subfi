package com.serrofortia.wallpaper.controller;

import com.serrofortia.wallpaper.BaseActivity;
import com.serrofortia.wallpaper.network.model.requests.RegisterRequest;
import com.serrofortia.wallpaper.network.model.responses.RegisterResponse;

public class RegistrationTask extends BaseTask<RegisterRequest, Integer, RegisterResponse>{

	public RegistrationTask(BaseActivity activity,BaseTaskListener<RegisterResponse> listener) {
		super(activity,listener);
	}

	@Override
	protected RegisterResponse doInBackground(RegisterRequest... request) {
		return (RegisterResponse)networkClient.register(request[0]);
	}

}
