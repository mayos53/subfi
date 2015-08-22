package com.serrofortia.wallpaper.model;

public class Country {
	
	public static final int SORT_TYPE_ALPHABET = 0;
	public static final int SORT_TYPE_CODE = 1;
	
	public String code;
	public String name;
	
	public Country(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}

}
