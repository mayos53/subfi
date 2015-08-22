package com.serrofortia.wallpaper.model;

public  class Event implements Dated{
	
	public final static int EVENT_TYPE_LEAVE_GROUP = 1;
	
	public int user_id;
	public String user_name;
	public int group_id;
	public int type;
	public long time;
	
	@Override
	public long getTime() {
		return time;
	}

	
	
	
	

}