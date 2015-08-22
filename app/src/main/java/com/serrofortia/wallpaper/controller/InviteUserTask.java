package com.serrofortia.wallpaper.controller;

import android.app.Activity;

import com.serrofortia.wallpaper.BaseActivity;
import com.serrofortia.wallpaper.network.model.requests.AddUserRequest;
import com.serrofortia.wallpaper.network.model.requests.InviteUserRequest;
import com.serrofortia.wallpaper.network.model.requests.RecommendUserRequest;
import com.serrofortia.wallpaper.network.model.responses.AddUserResponse;
import com.serrofortia.wallpaper.network.model.responses.BaseResponse;

public class InviteUserTask extends BaseTask<InviteUserRequest, Integer, BaseResponse>{

	public InviteUserTask(BaseActivity activity,BaseTaskListener<BaseResponse> listener) {
		super(activity,listener);
	}

	@Override
	protected BaseResponse doInBackground(InviteUserRequest... request) {
		return (BaseResponse)networkClient.inviteUser(request[0]);
	}

}
