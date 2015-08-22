package com.serrofortia.wallpaper.controller;

import com.serrofortia.wallpaper.BaseActivity;
import com.serrofortia.wallpaper.network.model.requests.AuthenticationRequest;
import com.serrofortia.wallpaper.network.model.responses.AuthenticationResponse;

public class LoginTask extends BaseTask<AuthenticationRequest, Integer, AuthenticationResponse>{

	public LoginTask(BaseActivity activity,BaseTaskListener<AuthenticationResponse> listener) {
		super(activity,listener);
	}

	@Override
	protected AuthenticationResponse doInBackground(AuthenticationRequest... request) {
		return (AuthenticationResponse)networkClient.login(request[0]);
	}
	
	

}
