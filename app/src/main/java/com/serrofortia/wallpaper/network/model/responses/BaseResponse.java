package com.serrofortia.wallpaper.network.model.responses;

public class BaseResponse {
	
	public final static int STATUS_OK = 1;
	
	public int status;
	public String message;
	
	
	public boolean isSuccessful(){
		return status == STATUS_OK;
	}

}
