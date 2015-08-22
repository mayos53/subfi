package com.serrofortia.wallpaper.controller;

import java.util.Comparator;

import com.serrofortia.wallpaper.model.Group;

public class GroupsComparator implements Comparator<Group>{

	private int userId;
	
	public GroupsComparator(int userId){
		this.userId = userId;
	}
	
	@Override
	public int compare(Group g1, Group g2) {
		if(g1.isEnabled(userId) && !g2.isEnabled(userId)){
			return -1;
		}else if(!g1.isEnabled(userId) && g2.isEnabled(userId)){
			return 1;
		}else{
			if(g1.time > g2.time){
				return -1;
			}else{
				return 1;
			}
	}
	}
}


