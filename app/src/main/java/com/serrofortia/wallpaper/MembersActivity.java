package com.serrofortia.wallpaper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.serrofortia.wallpaper.controller.BaseTaskListener;
import com.serrofortia.wallpaper.controller.RemoveMemberTask;
import com.serrofortia.wallpaper.model.Group;
import com.serrofortia.wallpaper.model.User;
import com.serrofortia.wallpaper.network.model.requests.RemoveMemberRequest;
import com.serrofortia.wallpaper.network.model.responses.BaseResponse;
import com.serrofortia.wallpaper.network.model.responses.RemoveMemberResponse;
import com.serrofortia.wallpaper.util.Tools;

public class MembersActivity extends BaseActivity{

	
	public final static int CONTACT_PICKER_RESULT = 1001;

	
	protected final String TAG = this.getClass().getSimpleName();
	protected WallpaperApp mApp;
	protected ContactsAdapter mAdapter;
    protected TextView txtName;
    protected Group group;

    protected TextView txtNumberParticipants;
    protected View btnAddMember;

    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.members);
    	mApp = (WallpaperApp)getApplication();
    	int groupId = getIntent().getExtras().getInt(GroupActivity.EXTRA_GROUP_ID,0);
		group = mApp.getGroupById(groupId);
		
    	ListView listView = (ListView)findViewById(android.R.id.list);
    	
    	mAdapter = new ContactsAdapter(this, 0);
		listView.setAdapter(mAdapter);
		if(group.isAdministrator(mApp.getUserDetails().id)){
			registerForContextMenu(listView);
		}

		
		txtNumberParticipants = (TextView)findViewById(R.id.title);
		
		
//		if(group.isAdministrator(mApp.getUserDetails().id)){
//			btnAddMember.setVisibility(View.VISIBLE);
//		}else{
//			btnAddMember.setVisibility(View.GONE);
//		}
		
		fillGroupDetails();
    	
    }
    
    public void onCreateContextMenu(ContextMenu menu, View v,
	        ContextMenuInfo menuInfo) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		User user = group.users[info.position];
		if(user.id != mApp.getUserDetails().id && group.isMember(user)){ 
			//don't allow to remove myself from group(I am the admin)
			//or if user is recommended
			menu.add(getString(R.string.remove_member,user.name));
		}
		
		

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
	            .getMenuInfo();
		User user = group.users[info.position];
		removeMember(group.id,user.id);
		return true;
	}
	
	private void removeMember(int groupId,int userId){
		RemoveMemberRequest request = new RemoveMemberRequest();
		request.id = userId;
		request.group_id = groupId;
		
		new RemoveMemberTask(this, new BaseTaskListener<RemoveMemberResponse>() {
			
			@Override
			public void onSuccess(RemoveMemberResponse response) {
				group = response.group;
				mApp.updateGroup(group);
				fillGroupDetails();
				
			}

			@Override
			public void onFailed(BaseResponse response) {
				// TODO Auto-generated method stub
				
			}
			
			
		}).execute(request);
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
				tag.admin = (TextView) convertView.findViewById(R.id.admin);
				tag.recommended = (TextView) convertView.findViewById(R.id.recommended);


				convertView.setTag(tag);
			}
			ContactHolder holder = (ContactHolder)convertView.getTag();
			TextView name = holder.name;
			name.setText(Tools.firstUpperCase(getItem(position).name));
			
			TextView admin = holder.admin;
			TextView recommended = holder.recommended;

			if(getItem(position).administrator){
				admin.setVisibility(View.VISIBLE);
				recommended.setVisibility(View.GONE);
			}else if(getItem(position).status == -1){
				admin.setVisibility(View.GONE);
				recommended.setVisibility(View.VISIBLE);	
			}else{
				admin.setVisibility(View.GONE);
				recommended.setVisibility(View.GONE);
			}
			
			
			
			return convertView;
			
		}
		
		
		
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case CONTACT_PICKER_RESULT:
				group = mApp.getGroupById(group.id);
				mApp.updateGroup(group);
				fillGroupDetails();

				break;
			}
		}
	}
   
	
	class ContactHolder{
		TextView name;
		TextView admin;
		TextView recommended;

	}



	@Override
	public View getTitleLayout() {
		ViewGroup view = (ViewGroup)getLayoutInflater().inflate(R.layout.title_members, null);
		txtName = (TextView)view.findViewById(R.id.group_title);
		btnAddMember = view.findViewById(R.id.btnAddMember);
		return view;
	}
	
	@Override
	public boolean onActionBarItemSelected(View action_item) {
		switch(action_item.getId()){
			case R.id.btnAddMember:
				Intent i = new Intent(MembersActivity.this, ContactsActivity.class);
				i.putExtra(GroupActivity.EXTRA_GROUP_ID, group.id);
				startActivityForResult(i,CONTACT_PICKER_RESULT);
			
			return true;
			case R.id.back:
				finish();
				
			return true;	
			
			
		
		}
		
		return super.onActionBarItemSelected(action_item);
	}
	
	private void fillGroupDetails(){
		 txtName.setText(group.name);
		 txtNumberParticipants.setText(getResources().getString(R.string.number_of_participants, ""+group.getCount()));
		 
		 mAdapter.clear();
		 for(User user:group.users){
			mAdapter.add(user);
		 }
	}
	
	
	
}