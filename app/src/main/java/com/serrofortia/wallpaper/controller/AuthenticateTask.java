package com.serrofortia.wallpaper.controller;

import android.app.Activity;

import com.serrofortia.wallpaper.BaseActivity;
import com.serrofortia.wallpaper.network.model.requests.AuthenticationRequest;
import com.serrofortia.wallpaper.network.model.responses.AuthenticationResponse;

public class AuthenticateTask extends BaseTask<AuthenticationRequest, Integer, AuthenticationResponse>{

	public AuthenticateTask(BaseActivity activity,BaseTaskListener<AuthenticationResponse> listener) {
		super(activity,listener);
	}

	@Override
	protected AuthenticationResponse doInBackground(AuthenticationRequest... request) {
		return (AuthenticationResponse)networkClient.authenticate(request[0]);
	}

}
