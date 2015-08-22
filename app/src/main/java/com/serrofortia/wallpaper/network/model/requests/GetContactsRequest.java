package com.serrofortia.wallpaper.network.model.requests;

import com.serrofortia.wallpaper.model.Contact;
import com.serrofortia.wallpaper.model.User;

public class GetContactsRequest extends BaseRequest{
	public Contact [] contacts;
	
	public GetContactsRequest(Contact []contacts){
		this.contacts = contacts;
	}
	
}
