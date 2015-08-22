package com.serrofortia.wallpaper.network.model.requests;

public class RegisterRequest extends BaseRequest{
	
	public int id;
	public String registrationId;
	
	public RegisterRequest(int id, String registrationId) {
		this.id = id;
		this.registrationId = registrationId;
	}
	
	
	
	 
}
