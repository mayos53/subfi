package com.serrofortia.wallpaper.controller;

import android.app.Activity;

import com.serrofortia.wallpaper.BaseActivity;
import com.serrofortia.wallpaper.network.model.requests.AddGroupRequest;
import com.serrofortia.wallpaper.network.model.requests.GetRecommendationsRequest;
import com.serrofortia.wallpaper.network.model.requests.RemoveRecommendationRequest;
import com.serrofortia.wallpaper.network.model.responses.BaseResponse;
import com.serrofortia.wallpaper.network.model.responses.GetGroupsResponse;
import com.serrofortia.wallpaper.network.model.responses.GetRecommendationsResponse;

public class RemoveRecommendationTask extends BaseTask<RemoveRecommendationRequest, Integer, GetRecommendationsResponse>{

	public RemoveRecommendationTask(BaseActivity activity,BaseTaskListener<GetRecommendationsResponse> listener) {
		super(activity,listener);
	}

	@Override
	protected GetRecommendationsResponse doInBackground(RemoveRecommendationRequest... request) {
		return (GetRecommendationsResponse)networkClient.removeRecommendation(request[0]);
	}

}
