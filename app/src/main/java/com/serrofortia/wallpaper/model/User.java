package com.serrofortia.wallpaper.model;

public  class User implements Comparable<User>{
	public int id;
	public String name;
	public String phone;
	public String countryCode;
	public int status;
	public boolean administrator;
	
	
	public User(String name, String phone, String countryCode) {
		this.name = name;
		this.phone = phone;
		this.countryCode = countryCode;
	}
	@Override
	public int compareTo(User user) {
		return this.name.compareTo(user.name);
	}
	
	
	
	

}