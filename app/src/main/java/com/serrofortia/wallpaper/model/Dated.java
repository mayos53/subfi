package com.serrofortia.wallpaper.model;

import java.util.Comparator;

public interface Dated{
	
	long getTime();
	public static class DatedComparator implements Comparator<Dated>{

		@Override
		public int compare(Dated d1, Dated d2) {
			 if(d1.getTime() < d2.getTime()){
				 return 1;
			 }else{
				 return -1;
			 }
		}
		
	}
	

}