package com.serrofortia.wallpaper.controller;

import android.app.Activity;

import com.serrofortia.wallpaper.BaseActivity;
import com.serrofortia.wallpaper.network.model.requests.GetGroupsRequest;
import com.serrofortia.wallpaper.network.model.responses.GetGroupsResponse;

public class GetGroupsTask extends BaseTask<GetGroupsRequest, Integer, GetGroupsResponse>{

	public GetGroupsTask(BaseActivity activity,BaseTaskListener<GetGroupsResponse> listener,boolean showLoading) {
		super(activity,listener,showLoading);
	}

	@Override
	protected GetGroupsResponse doInBackground(GetGroupsRequest... request) {
		return (GetGroupsResponse)networkClient.getGroups(request[0]);
	}

}
