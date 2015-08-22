package com.serrofortia.wallpaper.controller;

import android.app.Activity;

import com.serrofortia.wallpaper.BaseActivity;
import com.serrofortia.wallpaper.network.model.requests.GetInvitationsRequest;
import com.serrofortia.wallpaper.network.model.responses.GetInvitationsResponse;

public class GetInvitationsTask extends BaseTask<GetInvitationsRequest, Integer, GetInvitationsResponse>{

	public GetInvitationsTask(BaseActivity activity,BaseTaskListener<GetInvitationsResponse> listener) {
		super(activity,listener);
	}

	@Override
	protected GetInvitationsResponse doInBackground(GetInvitationsRequest... request) {
		return (GetInvitationsResponse)networkClient.getInvitations(request[0]);
	}

}
