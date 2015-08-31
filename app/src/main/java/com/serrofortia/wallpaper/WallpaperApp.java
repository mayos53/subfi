package com.serrofortia.wallpaper;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.serrofortia.wallpaper.controller.BaseTaskListener;
import com.serrofortia.wallpaper.controller.GetContactsTask;
import com.serrofortia.wallpaper.controller.GetGroupsTask;
import com.serrofortia.wallpaper.model.Contact;
import com.serrofortia.wallpaper.model.Country;
import com.serrofortia.wallpaper.model.Group;
import com.serrofortia.wallpaper.model.Invitation;
import com.serrofortia.wallpaper.model.Recommendation;
import com.serrofortia.wallpaper.model.User;
import com.serrofortia.wallpaper.model.UserDetails;
import com.serrofortia.wallpaper.network.NetworkClient;
import com.serrofortia.wallpaper.network.model.requests.GetContactsRequest;
import com.serrofortia.wallpaper.network.model.requests.GetGroupsRequest;
import com.serrofortia.wallpaper.network.model.requests.RegisterRequest;
import com.serrofortia.wallpaper.network.model.responses.BaseResponse;
import com.serrofortia.wallpaper.network.model.responses.GetContactsResponse;
import com.serrofortia.wallpaper.network.model.responses.GetGroupsResponse;
import com.serrofortia.wallpaper.network.model.responses.RegisterResponse;
import com.serrofortia.wallpaper.ui.components.AlertLoading;
import com.serrofortia.wallpaper.util.BitmapLruCache;
import com.serrofortia.wallpaper.util.Loger;
import com.serrofortia.wallpaper.util.Tools;

public class WallpaperApp extends Application{

	
	protected final String TAG = this.getClass().getSimpleName();

	private final static String SHARED_PREFERENCES = "SHARED_PREFERENCES";
	private final static String PREF_USER_DETAILS = "PREF_USER_DETAILS";
	private final static String PREF_GROUP = "PREF_GROUP";
	private final static String PREF_CONFIRMED = "PREF_CONFIRMED";
	private final static String PREF_CONTACTS = "PREF_CONTACTS";
	private final static String PREF_RECOMMENDATIONS = "PREF_RECOMMENDATIONS";
	private final static String PREF_CONTACTS_VERSION= "PREF_CONTACTS_VERSION";
	private final static String PREF_INVITATIONS= "PREF_INVITATIONS";



	public final static String GCM_SENDER_ID="86769998288";
	
	
	private SharedPreferences mPrefs;
	private Gson gson;
	private ImageLoader imageLoader;
	private List<Country> countriesAlphabet;
	private Map<String,Country> countriesCode;

	
	
	@Override
	public void onCreate() {
        if(!Tools.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
        mPrefs = getSharedPreferences(SHARED_PREFERENCES, 0);
		gson = new GsonBuilder().create();
		
		ImageLoader.ImageCache imageCache = new BitmapLruCache();
		imageLoader = new ImageLoader(Volley.newRequestQueue(this), imageCache);
	}


	public void saveUserDetails(UserDetails userDetails) {
		putObject(PREF_USER_DETAILS, userDetails);		
	}
	
	public UserDetails getUserDetails() {
		return (UserDetails)getObject(PREF_USER_DETAILS,UserDetails.class);
		
	}
	
	public ImageLoader getImageLoader(){
		return imageLoader;
	}
	
	
	
	public Group [] getGroups(){
		return (Group[])getObject(PREF_GROUP, Group[].class);	
	}
	
	public void setGroups(Group [] groups){
		 putObject(PREF_GROUP, groups);	
	}	
	
	public Recommendation [] getRecommendations(){
		return (Recommendation[])getObject(PREF_RECOMMENDATIONS, Recommendation[].class);	
	}
	
	public void setRecommendations(Recommendation [] recommendations){
		 putObject(PREF_RECOMMENDATIONS, recommendations);	
	}	
	
	public Invitation [] getInvitations(){
		return (Invitation[])getObject(PREF_INVITATIONS, Invitation[].class);	
	}
	
	public void setInvitations(Invitation [] invitations){
		 putObject(PREF_INVITATIONS, invitations);	
	}	

	
	
	
	
	private void putObject(String key,Object value){
		Editor editor = mPrefs.edit();
		editor.putString(key, gson.toJson(value));
		editor.commit();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object getObject(String key,Class _class){
		String str = mPrefs.getString(key,null);
		if(str != null){
			return gson.fromJson(str, _class);
		}
		return null;
	}


	public Group getGroupById(int groupId) {
		Group[] groups = getGroups();
		for(Group g:groups){
			if(g.id == groupId){
				return g;
			}
		}
		return null;
		
	}
	
	public void updateContacts(User[] contacts){
		putObject(PREF_CONTACTS, contacts);	
	}
	
	public User[]  getContacts(User [] excludeContacts){
		User [] contacts = (User[])getObject(PREF_CONTACTS, (User[].class));
		
		List<User> result = new ArrayList<User>();
		for(int i=0;i<contacts.length;i++){
			
			boolean find = false;
			for(int j=0;j<excludeContacts.length;j++){
				if(contacts[i].phone.equals(excludeContacts[j].phone)){
					find = true;
					break;
				}
				
			}
			if(!find){
				result.add(contacts[i]);
			}
		}
		return result.toArray(new User[0]);
		
		
	}

	
	
	public void updateGroup(Group group) {
		boolean found = false;
		Group[] groups = getGroups();
		for(int i=0;i< groups.length;i++){
			Group _group = groups[i];
			if(_group.id == group.id){
				groups[i] = group;
				setGroups(groups);
				found = true;
				break;
				
			}
		}
		if(!found){
			List<Group> list = new ArrayList<Group>();
			for(int i=0;i<groups.length;i++){
				list.add(groups[i]);

			}
			list.add(group);
			setGroups(list.toArray(new Group[0]));
		}
		return;
		
		
	}


	public boolean isConfirmed() {
		if(Tools.SMS){
			Boolean b =  (Boolean)getObject(PREF_CONFIRMED, Boolean.class);
			if(b != null){
				return b.booleanValue();
			}
			return false;
		}else{
			return true;
		}
	}
	
	public void setConfirmed() {
		 putObject(PREF_CONFIRMED, Boolean.valueOf(true));
	}
	
	
	public List<Country> getCountries(Context context){
		try{
			
			if(countriesAlphabet == null){
				List<Country> countries = parseCountries(context);
				Collections.sort(countries,new Comparator<Country>() {

						@Override
						public int compare(Country c1, Country c2) {
							return c1.name.compareTo(c2.name);
						}
					});
					countriesAlphabet = countries;
				}
				return countriesAlphabet;	
		
		
		}catch(Exception e){
			Loger.e(TAG, "", e);
		}
		return null;
			
			
	}
	
	public  Map<String,Country> getCountryCodes(){
		try {
			if (countriesCode == null) {
				countriesCode = new HashMap<String, Country>();
				List<Country> countries = parseCountries(this);
				for (Country country : countries) {
					countriesCode.put(country.code, country);
				}
			}
			return countriesCode;
		} catch (Exception e) {
			Loger.e(TAG, "", e);
		}
		return null;
			
			
	}
	
	private static List<Country> parseCountries(Context context) throws Exception{
		String strCountries = Tools.getRawResource(context, R.raw.countrycodes);
		JSONArray array = new JSONArray(strCountries);
		List<Country> countries = new ArrayList<Country>();

		for (int i = 0; i < array.length(); i += 2) {
			String code = (String) array.get(i + 1);
			String name = (String) array.get(i);
			countries.add(new Country(code,name));
		}
		
		return countries;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void registerToGCM() {
		
	    new AsyncTask() {
	    	@Override
			protected Object doInBackground(Object... params) {
				  String msg = "";
		            try {
		                
		                 GoogleCloudMessaging  gcm = GoogleCloudMessaging.getInstance(WallpaperApp.this);
		                 String registrationId = gcm.register(WallpaperApp.GCM_SENDER_ID);
		
		                 Loger.d("GCM", "Got registration from google: " + registrationId);

		                 
		                 int userId = getUserDetails().id;
		     			 RegisterRequest request = new RegisterRequest(userId,registrationId);
		                 NetworkClient networkClient = new NetworkClient();
			             RegisterResponse response = networkClient.register(request);
	                
			             if(response.isSuccessful()){
				             UserDetails details = getUserDetails();
				    		 details.registrationId = response.registrationId;
				    		 saveUserDetails(details);
			             }
			    		 
		            } catch (IOException ex) {
		                msg = "Error :" + ex.getMessage();
		                Loger.e(TAG,msg,ex);
		            }
		            return msg;
			}

	       

			
	    }.execute(null, null, null);
	    
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void getAllDataAndStartGroups(final BaseActivity activity){
    	
		final String curVersion = Tools.getCurrentContactsVersion(this);
		String lastVersion = getContactsVersion();

		if (lastVersion != null && curVersion != null
				&& curVersion.equals(lastVersion)) {
			getGroups(activity, true,null);
		} else {
			final AlertLoading alertLoading = new AlertLoading(activity.getContentView(), activity);
			alertLoading.show();

			new AsyncTask() {
				@Override
				protected Object doInBackground(Object... params) {
					Contact [] contacts = Tools.getAllPhoneNumbers(activity);
					return contacts;
				}

				protected void onPostExecute(Object result) {
					alertLoading.hide();
					GetContactsRequest request = new GetContactsRequest(
							(Contact []) result);

					GetContactsTask task = new GetContactsTask(activity,
							new BaseTaskListener<GetContactsResponse>() {

								@Override
								public void onSuccess(
										GetContactsResponse response) {
									putContactsVersion(curVersion);
									updateContacts(response.contacts);
									getGroups(activity, true,null);
								}

								@Override
								public void onFailed(BaseResponse response) {
									getGroups(activity, true,null);

								}
							});
					task.execute(request);
				};

			}.execute(null, null, null);
		}
		
    	
	}
	
	public void getGroups(final BaseActivity activity,boolean showLoading, final Bundle extras){
		GetGroupsRequest request = new GetGroupsRequest();
		request.userId = getUserDetails().id;
		GetGroupsTask task = new GetGroupsTask(activity,new BaseTaskListener<GetGroupsResponse>() {

			@Override
			public void onSuccess(GetGroupsResponse response) {
				setGroups(response.groups);
				Intent i = new Intent(activity,GroupsActivity.class);
				
				if(extras != null){
					Uri pictureUri = (Uri)extras.get(Intent.EXTRA_STREAM);
					if(pictureUri != null){
						i.putExtra(PaintActivity.EXTRA_PICTURE_URI, pictureUri);
					}else{
						i.putExtras(extras);
					}
					 
				}

				
				activity.startActivity(i);
				activity.finish();
				
			}

			@Override
			public void onFailed(BaseResponse response) {
				// TODO Auto-generated method stub
				
			}
		},showLoading);
		task.execute(request);
		
	}
	
	public void putContactsVersion(String version){
		putObject(PREF_CONTACTS_VERSION, version);
	}
	
	public String getContactsVersion(){
		return (String)getObject(PREF_CONTACTS_VERSION,String.class);
	}
	 
	
	
	
}
