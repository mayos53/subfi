package com.serrofortia.wallpaper.controller.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;



public class WallpaperProvider extends ContentProvider {

	private static final UriMatcher mMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	private static final int GROUPS = 1;


	static
	{
		mMatcher.addURI(WallpaperContract.AUTHORITY, WallpaperContract.GroupDict.URI_SUFFIX, GROUPS);
		//mMatcher.addURI(WallpaperContract.AUTHORITY, WallpaperContract.GroupDict.URI_SUFFIX.concat("/").
		//		concat(WallpaperContract.GroupDict.URI_BY_ID,)
		

		
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
