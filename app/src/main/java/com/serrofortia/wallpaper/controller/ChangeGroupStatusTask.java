package com.serrofortia.wallpaper.controller;

import com.serrofortia.wallpaper.BaseActivity;
import com.serrofortia.wallpaper.network.model.requests.ChangeGroupStatusRequest;
import com.serrofortia.wallpaper.network.model.responses.ChangeGroupStatusResponse;

public class ChangeGroupStatusTask extends BaseTask<ChangeGroupStatusRequest, Integer, ChangeGroupStatusResponse>{

	public ChangeGroupStatusTask(BaseActivity activity,BaseTaskListener<ChangeGroupStatusResponse> listener) {
		super(activity,listener);
	}

	@Override
	protected ChangeGroupStatusResponse doInBackground(ChangeGroupStatusRequest... request) {
		return (ChangeGroupStatusResponse)networkClient.changeGroupStatus(request[0]);
	}

}
