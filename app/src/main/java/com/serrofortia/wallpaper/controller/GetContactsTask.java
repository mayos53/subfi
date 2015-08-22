package com.serrofortia.wallpaper.controller;

import com.serrofortia.wallpaper.BaseActivity;
import com.serrofortia.wallpaper.network.model.requests.GetContactsRequest;
import com.serrofortia.wallpaper.network.model.responses.GetContactsResponse;

public class GetContactsTask extends BaseTask<GetContactsRequest, Integer, GetContactsResponse>{

	public GetContactsTask(BaseActivity activity,BaseTaskListener<GetContactsResponse> listener) {
		super(activity,listener,true);
	}

	@Override
	protected GetContactsResponse doInBackground(GetContactsRequest... request) {
		return (GetContactsResponse)networkClient.getContacts(request[0]);
	}

}
