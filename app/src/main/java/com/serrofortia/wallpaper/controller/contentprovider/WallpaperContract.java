package com.serrofortia.wallpaper.controller.contentprovider;


import android.net.Uri;
import android.provider.BaseColumns;


public class WallpaperContract {
	
	
	
	public static final String AUTHORITY = "com.wallpaper";
	private static final String CONTENT_BASE = "content://"+AUTHORITY+"/%s";
	
	public static final String DB_NAME = "wallpaper.db";
	public static final int DB_VERSION = 1;
	
	
	public static final class GroupDict implements BaseColumns{
		public static final String URI_SUFFIX = "groups";
		public static final Uri CONTENT_URI = Uri.parse(String.format(CONTENT_BASE, URI_SUFFIX));
		public static final String NAME = "NAME";
		public static final String TABLE_NAME = "groups";
    }
	
	public static final class UserDict implements BaseColumns{
		public static final String URI_SUFFIX = "users";
		public static final Uri CONTENT_URI = Uri.parse(String.format(CONTENT_BASE, URI_SUFFIX));
		public static final String NAME = "NAME";
		public static final String TABLE_NAME = "users";

		
    }
	
	public static final class UserGroupDict implements BaseColumns{
		public static final String GROUP_ID = "GROUP_ID";
		public static final String USER_ID = "USER_ID";
		public static final String ACTIVE = "ACTIVE";
		public static final String TABLE_NAME = "usergroups";

    }
	
	public static final class WallPaperDict implements BaseColumns{
		public static final String GROUP_ID = "GROUP_ID";
		public static final String TIME = "TIME";
		public static final String PATH = "PATH";
		public static final String ACTIVE = "ACTIVE";
		public static final String TABLE_NAME = "wallpapers";

    }
	
	
}