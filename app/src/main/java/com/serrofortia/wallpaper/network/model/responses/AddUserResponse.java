package com.serrofortia.wallpaper.network.model.responses;

import com.serrofortia.wallpaper.model.Group;
import com.serrofortia.wallpaper.model.Invitation;
import com.serrofortia.wallpaper.model.Recommendation;

public class AddUserResponse extends BaseResponse{
	
	public Group group;
	public Recommendation [] recommendations;
	public Invitation [] invitations;


	
	
	
}
