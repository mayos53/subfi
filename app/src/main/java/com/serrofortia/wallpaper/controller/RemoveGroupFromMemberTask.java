package com.serrofortia.wallpaper.controller;

import com.serrofortia.wallpaper.BaseActivity;
import com.serrofortia.wallpaper.network.model.requests.RemoveGroupFromMemberRequest;
import com.serrofortia.wallpaper.network.model.responses.RemoveGroupFromMemberResponse;

public class RemoveGroupFromMemberTask extends BaseTask<RemoveGroupFromMemberRequest, Integer, RemoveGroupFromMemberResponse>{

	public RemoveGroupFromMemberTask(BaseActivity activity,BaseTaskListener<RemoveGroupFromMemberResponse> listener) {
		super(activity,listener);
	}

	@Override
	protected RemoveGroupFromMemberResponse doInBackground(RemoveGroupFromMemberRequest... request) {
		return (RemoveGroupFromMemberResponse)networkClient.removeGroupFromMember(request[0]);
	}

}
