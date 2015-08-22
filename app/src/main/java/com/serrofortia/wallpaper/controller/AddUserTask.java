package com.serrofortia.wallpaper.controller;

import android.app.Activity;

import com.serrofortia.wallpaper.BaseActivity;
import com.serrofortia.wallpaper.network.model.requests.AddUserRequest;
import com.serrofortia.wallpaper.network.model.responses.AddUserResponse;

public class AddUserTask extends BaseTask<AddUserRequest, Integer, AddUserResponse>{

	public AddUserTask(BaseActivity activity,BaseTaskListener<AddUserResponse> listener) {
		super(activity,listener);
	}

	@Override
	protected AddUserResponse doInBackground(AddUserRequest... request) {
		return (AddUserResponse)networkClient.addUser(request[0]);
	}

}
