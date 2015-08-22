package com.serrofortia.wallpaper;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.serrofortia.wallpaper.controller.AddUserTask;
import com.serrofortia.wallpaper.controller.BaseTaskListener;
import com.serrofortia.wallpaper.controller.GetInvitationsTask;
import com.serrofortia.wallpaper.model.Invitation;
import com.serrofortia.wallpaper.network.model.requests.AddUserRequest;
import com.serrofortia.wallpaper.network.model.requests.GetInvitationsRequest;
import com.serrofortia.wallpaper.network.model.responses.AddUserResponse;
import com.serrofortia.wallpaper.network.model.responses.BaseResponse;
import com.serrofortia.wallpaper.network.model.responses.GetInvitationsResponse;
import com.serrofortia.wallpaper.ui.components.AlertBuilder;
import com.serrofortia.wallpaper.ui.components.AlertBuilder.ButtonListener;
import com.serrofortia.wallpaper.util.Loger;

public class InvitationsActivity extends BaseSlidingActivity{

	
	
	protected final String TAG = this.getClass().getSimpleName();
	protected WallpaperApp mApp;
	protected MyAdapter mAdapter;
	protected Invitation []  invitations;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		setContentView(R.layout.recommendations);
    	
		mApp = (WallpaperApp)getApplication();

    	ListView listView = (ListView)findViewById(android.R.id.list);
    	mAdapter = new MyAdapter(this, 0);
    	listView.setAdapter(mAdapter);
    	GetInvitationsTask task = new GetInvitationsTask(this, new BaseTaskListener<GetInvitationsResponse>() {
			
			@Override
			public void onSuccess(GetInvitationsResponse response) {
				mApp.setInvitations(response.invitations);
				fillInvitations();
    			
				
			}
			
			@Override
			public void onFailed(BaseResponse response) {
				Loger.e(TAG, "error in getting invitations");
				
			}
		});
    	
    	GetInvitationsRequest request = new GetInvitationsRequest();
    	request.user_id = mApp.getUserDetails().id;
    	task.execute(request);
    	
    	fillInvitations();
    	
    	listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				showInvitationPopup(invitations[position]);
				
			}
		});
    	
    	
    	
    }
    
    
    private void showInvitationPopup(final Invitation invitation){
	    String administratorName = invitation.administrator_name;
		final String groupName = invitation.group_name;
		
		String message = getString(R.string.invited_notification_message,administratorName,groupName);
		
		AlertBuilder.showMessage(mainView, this, message, true,
				new ButtonListener() {
					
					@Override
					public void onClick() {
						addContact(invitation.group_id, mApp.getUserDetails().id, invitation.group_name, mApp.getUserDetails().name);
						
					}
				}, null);
    }
    
    private void fillInvitations(){
    	invitations = mApp.getInvitations();
    	mAdapter.clear();
    	if(invitations != null){
    		mAdapter.addAll(invitations);
    	}
    }

    
    private void addContact(int group_id ,int user_id,final String group_name,final String user_name){
		AddUserTask task = new AddUserTask(this,new BaseTaskListener<AddUserResponse>() {

			@Override
			public void onSuccess(AddUserResponse response) {
				mApp.updateGroup(response.group);
				mApp.setInvitations(response.invitations);

				fillInvitations();
				String message = getString(R.string.friend_was_added,user_name,group_name);
				AlertBuilder.showMessage(mainView, InvitationsActivity.this, message, false,null,null);
				
			}

			@Override
			public void onFailed(BaseResponse response) {
				
				
				
			}
		});
		
		
		AddUserRequest request = new AddUserRequest();
		request.group_id = group_id;
		request.id = user_id;
		task.execute(request);
		
	}


    class MyAdapter extends ArrayAdapter<Invitation>{

		public MyAdapter(Context context, int resource) {
			super(context, resource);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = getLayoutInflater().inflate(R.layout.invitation_item, parent,false);
				InvitationHolder tag = new InvitationHolder();
				tag.name = (TextView) convertView.findViewById(R.id.invitation_text);
				convertView.setTag(tag);
			}
			InvitationHolder holder = (InvitationHolder)convertView.getTag();
			TextView name = holder.name;
			
			Invitation invitation = getItem(position);
			String text = getString(R.string.invited_notification,invitation.administrator_name,invitation.group_name);
			
			name.setText(text);
			
			
			return convertView;
			
		}
		
		
		
		
	}
	
	
	
	class InvitationHolder{
		TextView name;
	}
	
	@Override
	public boolean onActionBarItemSelected(View action_item) {
		switch(action_item.getId()){
			case R.id.btnBack:
				finish();
		    return true;	

		}

		return super.onActionBarItemSelected(action_item);

	}


	@Override
	public View getTitleLayout() {
		ViewGroup view = (ViewGroup)getLayoutInflater().inflate(R.layout.title_invitations, null);
		((TextView)view.findViewById(R.id.text)).setText(R.string.invitations);
		return view;
	}
}

    
    
    
	
	