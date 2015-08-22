package com.serrofortia.wallpaper.controller;

import android.app.Activity;

import com.serrofortia.wallpaper.BaseActivity;
import com.serrofortia.wallpaper.network.model.requests.AddGroupRequest;
import com.serrofortia.wallpaper.network.model.requests.GetRecommendationsRequest;
import com.serrofortia.wallpaper.network.model.responses.GetGroupsResponse;
import com.serrofortia.wallpaper.network.model.responses.GetRecommendationsResponse;

public class GetRecommendationsTask extends BaseTask<GetRecommendationsRequest, Integer, GetRecommendationsResponse>{

	public GetRecommendationsTask(BaseActivity activity,BaseTaskListener<GetRecommendationsResponse> listener) {
		super(activity,listener);
	}

	@Override
	protected GetRecommendationsResponse doInBackground(GetRecommendationsRequest... request) {
		return (GetRecommendationsResponse)networkClient.getRecommendations(request[0]);
	}

}
