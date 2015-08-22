package com.serrofortia.wallpaper.controller;

import com.serrofortia.wallpaper.network.model.responses.BaseResponse;

public interface BaseTaskListener <T extends BaseResponse> {

	public void onSuccess(T success);
	public void onFailed(BaseResponse response);
	
	
}
