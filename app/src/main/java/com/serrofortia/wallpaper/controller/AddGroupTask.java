package com.serrofortia.wallpaper.controller;

import com.serrofortia.wallpaper.BaseActivity;
import com.serrofortia.wallpaper.network.model.requests.AddGroupRequest;
import com.serrofortia.wallpaper.network.model.responses.GetGroupsResponse;

public class AddGroupTask extends BaseTask<AddGroupRequest, Integer, GetGroupsResponse>{

	public AddGroupTask(BaseActivity activity,BaseTaskListener<GetGroupsResponse> listener) {
		super(activity,listener);
	}

	@Override
	protected GetGroupsResponse doInBackground(AddGroupRequest... request) {
		return (GetGroupsResponse)networkClient.addGroup(request[0]);
	}

}
