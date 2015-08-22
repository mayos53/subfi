package com.serrofortia.wallpaper.controller;

import com.serrofortia.wallpaper.BaseActivity;
import com.serrofortia.wallpaper.network.model.requests.RemoveMemberRequest;
import com.serrofortia.wallpaper.network.model.responses.RemoveMemberResponse;

public class RemoveMemberTask extends BaseTask<RemoveMemberRequest, Integer, RemoveMemberResponse>{

	public RemoveMemberTask(BaseActivity activity,BaseTaskListener<RemoveMemberResponse> listener) {
		super(activity,listener);
	}

	@Override
	protected RemoveMemberResponse doInBackground(RemoveMemberRequest... request) {
		return (RemoveMemberResponse)networkClient.removeMember(request[0]);
	}

}
