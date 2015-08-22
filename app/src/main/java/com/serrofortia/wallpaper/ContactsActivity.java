package com.serrofortia.wallpaper;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.serrofortia.wallpaper.controller.AddUserTask;
import com.serrofortia.wallpaper.controller.BaseTaskListener;
import com.serrofortia.wallpaper.controller.GetContactsTask;
import com.serrofortia.wallpaper.controller.InviteUserTask;
import com.serrofortia.wallpaper.controller.RecommendUserTask;
import com.serrofortia.wallpaper.model.Contact;
import com.serrofortia.wallpaper.model.Group;
import com.serrofortia.wallpaper.model.User;
import com.serrofortia.wallpaper.network.model.requests.AddUserRequest;
import com.serrofortia.wallpaper.network.model.requests.GetContactsRequest;
import com.serrofortia.wallpaper.network.model.requests.InviteUserRequest;
import com.serrofortia.wallpaper.network.model.requests.RecommendUserRequest;
import com.serrofortia.wallpaper.network.model.responses.AddUserResponse;
import com.serrofortia.wallpaper.network.model.responses.BaseResponse;
import com.serrofortia.wallpaper.network.model.responses.GetContactsResponse;
import com.serrofortia.wallpaper.ui.components.AlertBuilder;
import com.serrofortia.wallpaper.ui.components.AlertBuilder.ButtonListener;
import com.serrofortia.wallpaper.ui.components.AlertLoading;
import com.serrofortia.wallpaper.util.Tools;

public class ContactsActivity extends BaseActivity{

	
	
	protected final String TAG = this.getClass().getSimpleName();
	protected WallpaperApp mApp;
	protected ContactsAdapter mAdapter;
	protected Group group;
	protected AlertLoading alertLoading;
	
    
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.contacts);
    	
    	
    	ListView listView = (ListView)findViewById(android.R.id.list);
    	mAdapter = new ContactsAdapter(this, 0);
    	listView.setAdapter(mAdapter);
    	
    	mApp = (WallpaperApp)getApplication();
    	final int group_id = getIntent().getIntExtra(GroupActivity.EXTRA_GROUP_ID, 0);
    	
    	group = mApp.getGroupById(group_id);
    	
    	alertLoading = new AlertLoading(getTitleLayout(), this);
    	
    	
    	
    	
    	
    	listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				User user = mAdapter.getItem(position);
				
				if(group.isAdministrator(mApp.getUserDetails().id)){
					//inviteContact(group,user);
					addContact(group.id, user);
				}else{
					recommendContact(group, user);
				}
				
			}
		});
    	
    	
    	 final String curVersion = Tools.getCurrentContactsVersion(this);
    	 String lastVersion = mApp.getContactsVersion();
    	
    	if(lastVersion != null && curVersion != null  && curVersion.equals(lastVersion)){
    		mAdapter.addAll(mApp.getContacts(mApp.getGroupById(group_id).users));
    	}else{
    		alertLoading.show();
    		new AsyncTask() {
       	    	@Override
       			protected Object doInBackground(Object... params) {
       				 	Contact [] contacts = Tools.getAllPhoneNumbers(ContactsActivity.this);
       			    return contacts;   
       			}
       	    	
       	    	protected void onPostExecute(Object result) {
       	    		alertLoading.hide();
       	    		GetContactsRequest request = new GetContactsRequest((Contact[]) result);
       	 		
    	   	 		GetContactsTask task  =new GetContactsTask(ContactsActivity.this, new BaseTaskListener<GetContactsResponse>() {
    	   	 			
    	   	 			@Override
    	   	 			public void onSuccess(GetContactsResponse response) {
    	   	 				
    	   	 			    //save all contacts
    	   	 				mApp.putContactsVersion(curVersion);
    	   	 				mApp.updateContacts(response.contacts);
    	   	 				
    	   	 				
    	   	 				//fetch contacts and add only those that are not members and not recommended in the group
    	   	 			    User[] contacts = mApp.getContacts(mApp.getGroupById(group_id).users);
    	   	 				mAdapter.addAll(contacts);
    	   	 				
    	   	 			}
    	   	 			
    	   	 			@Override
    	   	 			public void onFailed(BaseResponse response) {
		   	 				mAdapter.addAll(mApp.getContacts(mApp.getGroupById(group_id).users));
    	   	 				
    	   	 			}
    	   	 		});
    	   	 		task.execute(request);
    	   	    	};

       	     
       	    }.execute(null, null, null);

    		
    	}
    	
    	
    	
    	
    }
    
    
    
    private void addContact(int group_id,User contact){
		AddUserTask task = new AddUserTask(ContactsActivity.this,new BaseTaskListener<AddUserResponse>() {

			@Override
			public void onSuccess(AddUserResponse response) {
				mApp.updateGroup(response.group);
				setResult(RESULT_OK,getIntent());
				finish();
				
			}

			@Override
			public void onFailed(BaseResponse response) {
				
			}
		});
		
		
		AddUserRequest request = new AddUserRequest();
		request.group_id = group_id;
		request.id = contact.id;
		task.execute(request);
		
	}
    
    private void recommendContact(final Group group,final User contact){
		RecommendUserTask task = new RecommendUserTask(ContactsActivity.this,new BaseTaskListener<BaseResponse>() {

			@Override
			public void onSuccess(BaseResponse response) {
				String message = getString(R.string.friend_was_recommended,contact.name,group.name);
				
				AlertBuilder.showMessage(mainView, ContactsActivity.this, message, false
						, new ButtonListener() {
							
							@Override
							public void onClick() {
								setResult(RESULT_OK,getIntent());
								finish();
							}
						}
						
						, null, R.string.yes,R.string.no);
				
				
				
			}

			@Override
			public void onFailed(BaseResponse response) {
				String message = getString(R.string.error);
						AlertBuilder.showMessage(mainView,
								ContactsActivity.this, message, false,
								new ButtonListener() {

									@Override
									public void onClick() {
										setResult(RESULT_OK, getIntent());
										finish();
									}
								}, null,0,0);
			}
		});
		
		
		RecommendUserRequest request = new RecommendUserRequest();
		request.group_id = group.id;
		request.id = contact.id;
		request.recommender_id = mApp.getUserDetails().id;
		task.execute(request);
		
	}
    
    private void inviteContact(final Group group,final User contact){
		InviteUserTask task = new InviteUserTask(ContactsActivity.this,new BaseTaskListener<BaseResponse>() {

			@Override
			public void onSuccess(BaseResponse response) {
				String message = getString(R.string.was_invited,contact.name,group.name);
				
				AlertBuilder.showMessage(mainView, ContactsActivity.this, message, false
						, new ButtonListener() {
							
							@Override
							public void onClick() {
								setResult(RESULT_OK,getIntent());
								finish();
							}
						}
						
						, null,0,0);
				
				
				
			}

			@Override
			public void onFailed(BaseResponse response) {
				String message = getString(R.string.error);
						AlertBuilder.showMessage(mainView,
								ContactsActivity.this, message, false,
								new ButtonListener() {

									@Override
									public void onClick() {
										setResult(RESULT_OK, getIntent());
										finish();
									}
								}, null);
			}
		});
		
		
		InviteUserRequest request = new InviteUserRequest();
		request.administrator_id = mApp.getUserDetails().id;
		request.group_id = group.id;
		request.user_id = contact.id;
		task.execute(request);
		
	}
    
    class ContactsAdapter extends ArrayAdapter<User>{

		public ContactsAdapter(Context context, int resource) {
			super(context, resource);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = getLayoutInflater().inflate(R.layout.contact_item, parent,false);
				ContactHolder tag = new ContactHolder();
				tag.name = (TextView) convertView.findViewById(R.id.contactName);
				convertView.setTag(tag);
			}
			ContactHolder holder = (ContactHolder)convertView.getTag();
			TextView name = holder.name;
			name.setText(Tools.firstUpperCase(getItem(position).name));
			
			
			return convertView;
			
		}
		
		
		
		
	}
	
	
	
	class ContactHolder{
		TextView name;
	}
	
	@Override
	public boolean onActionBarItemSelected(View action_item) {
		switch(action_item.getId()){
			case R.id.btnBack:
				finish();
		    return true;	

		}

		return true;
	}


	@Override
	public View getTitleLayout() {
		ViewGroup view = (ViewGroup)getLayoutInflater().inflate(R.layout.title_pick_contact, null);
		return view;
	}
}