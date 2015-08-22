package com.serrofortia.wallpaper.controller.contentprovider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class DbHelper extends SQLiteOpenHelper {

	public DbHelper(Context context, int dbVersion) {
		super(context, WallpaperContract.DB_NAME, null, 
				dbVersion);
	}

	/** Called only once to create the database first time. */
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		
		String sqlCreateGroups = String.format("create table %s (%s INTEGER primary key, %s TEXT)",
											WallpaperContract.GroupDict.TABLE_NAME,
											WallpaperContract.GroupDict._ID,
											WallpaperContract.GroupDict.NAME
											); 
		
		
		String sqlCreateUsers = String.format("create table %s (%s INTEGER primary key,%s TEXT)",
				WallpaperContract.UserDict.TABLE_NAME,
				WallpaperContract.UserDict._ID,
				WallpaperContract.UserDict.NAME
				
	    );
		
		String sqlCreateUserGroups = String.format("create table %s (%s INTEGER,%s INTEGER)",
				WallpaperContract.UserGroupDict.TABLE_NAME,
				WallpaperContract.UserGroupDict.GROUP_ID,
				WallpaperContract.UserGroupDict.USER_ID
				
	    );
		
		String sqlCreateWallpapers = String.format("create table %s (%s TEXT,%s INTEGER,%s INTEGER,%s INTEGER)",
				WallpaperContract.WallPaperDict.TABLE_NAME,
				WallpaperContract.WallPaperDict.PATH,
				WallpaperContract.WallPaperDict.GROUP_ID,
				WallpaperContract.WallPaperDict.TIME,
				WallpaperContract.WallPaperDict.ACTIVE
				
	    );
		
		db.execSQL(sqlCreateGroups);
		db.execSQL(sqlCreateUserGroups);
		db.execSQL(sqlCreateUsers);
		db.execSQL(sqlCreateWallpapers);
		
		
		
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	
	
}