package com.serrofortia.wallpaper.controller;

import com.serrofortia.wallpaper.BaseActivity;
import com.serrofortia.wallpaper.network.model.requests.RecommendUserRequest;
import com.serrofortia.wallpaper.network.model.responses.BaseResponse;

public class RecommendUserTask extends BaseTask<RecommendUserRequest, Integer, BaseResponse>{

	public RecommendUserTask(BaseActivity activity,BaseTaskListener<BaseResponse> listener) {
		super(activity,listener);
	}

	@Override
	protected BaseResponse doInBackground(RecommendUserRequest... request) {
		return (BaseResponse)networkClient.recommendUser(request[0]);
	}

}
