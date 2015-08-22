package com.serrofortia.wallpaper.model;


public class Group {

	public static final int GROUP_STATUS_DISABLED = 0;
	public static final int GROUP_STATUS_ENABLED = 1;
	
	
	public int id;
	public int status;
	public boolean administrator;
	
	public String name;
	public User[] users;

	public Wallpaper [] wallpapers;
	public Event [] events;

	public String image;
	public int time;
	
	public boolean isEnabled(int userId) {
		for(int i=0;i<users.length;i++){
			if(users[i].id == userId){
				return users[i].status == GROUP_STATUS_ENABLED;
			}
		}
		return false;
	}
	
	public boolean isAdministrator(int userId) {
		for(int i=0;i<users.length;i++){
			if(users[i].id == userId){
				return users[i].administrator;
			}
		}
		return false;
	}
	
	//get all members.don't count recommended only members
	public int getCount(){
		int count = 0;
		for(int i=0;i<users.length;i++){
			if(isMember(users[i])){
				count++;
			}
		}
		return count;
	}
	
	public boolean isMember(User user){
		return user.status != -1;
	}
}
