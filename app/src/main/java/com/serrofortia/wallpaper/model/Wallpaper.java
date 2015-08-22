package com.serrofortia.wallpaper.model;

public class Wallpaper implements Dated{
	
	public String path;
	public User user;
	public long timeSec;
	public String title;
	
	@Override
	public long getTime() {
		return timeSec;
	}
	

}
