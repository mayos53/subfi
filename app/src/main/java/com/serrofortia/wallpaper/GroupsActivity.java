package com.serrofortia.wallpaper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.serrofortia.wallpaper.controller.BaseTaskListener;
import com.serrofortia.wallpaper.controller.ChangeGroupStatusTask;
import com.serrofortia.wallpaper.controller.GroupsComparator;
import com.serrofortia.wallpaper.controller.RemoveGroupFromMemberTask;
import com.serrofortia.wallpaper.model.Group;
import com.serrofortia.wallpaper.network.model.requests.ChangeGroupStatusRequest;
import com.serrofortia.wallpaper.network.model.requests.RemoveGroupFromMemberRequest;
import com.serrofortia.wallpaper.network.model.responses.BaseResponse;
import com.serrofortia.wallpaper.network.model.responses.ChangeGroupStatusResponse;
import com.serrofortia.wallpaper.network.model.responses.RemoveGroupFromMemberResponse;
import com.serrofortia.wallpaper.util.Tools;

public class GroupsActivity extends BaseSlidingActivity{

	protected final String TAG = this.getClass().getSimpleName();
	protected WallpaperApp mApp;
	protected GroupsAdapter groupsAdapter;
	protected Group [] groups;
	private boolean modify;
	Uri pictureUri;
	
	private TextView title;
	private View addGroup;
	private boolean isEditMode;
	private int editModeItem;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.groups);

		mApp = (WallpaperApp)getApplication();
		
		groups = mApp.getGroups();
		groupsAdapter = new GroupsAdapter(GroupsActivity.this,0);
		ListView listView = (ListView)findViewById(android.R.id.list);
		listView.setAdapter(groupsAdapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
			
				if(pictureUri == null){
					if(editModeItem == position){
						editModeItem = -1;
						groupsAdapter.notifyDataSetInvalidated();
					}else{
						Intent i = new Intent(GroupsActivity.this,GroupActivity.class);
						i.putExtra(GroupActivity.EXTRA_GROUP_ID, groupsAdapter.getItem(position).id);
						startActivity(i);
					}
				}else{
					Intent intent = new Intent(GroupsActivity.this, PaintActivity.class);
					intent.putExtra(PaintActivity.EXTRA_PICTURE_URI, pictureUri);

					intent.putExtra(GroupActivity.EXTRA_GROUP_ID, groupsAdapter.getItem(position).id);

					startActivityForResult(intent,GroupActivity.PHOTO_SENT_RESULT);
				}
				
			}
		});
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long groupId) {
				editModeItem = position;
				groupsAdapter.notifyDataSetInvalidated();
				return true;
			}
	
		});
		// registerForContextMenu(listView);

		handleIntentExtras(getIntent());
		editModeItem = -1;

		
	
		
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		handleIntentExtras(intent);
	}
	
	private void handleIntentExtras(Intent intent){
		if(intent.getExtras() != null){
			pictureUri =  (Uri)intent.getExtras().get(PaintActivity.EXTRA_PICTURE_URI);
			
		}
		
		if(pictureUri != null){
			setChooseMode(true);
		}else{
			setChooseMode(false);
			
			
			
		}
		
	 
	 
	 if(intent.getStringExtra("type") != null && intent.getStringExtra("type").equals("recommend")){
		 	Intent i = new Intent(this,RecommendationsActivity.class);
		 	startActivity(i);
     }else if(intent.getStringExtra("type") != null && intent.getStringExtra("type").equals("invite")){
		 	Intent i = new Intent(this,InvitationsActivity.class);
		 	startActivity(i);
     }
	}
	
	
	
	
	private void setChooseMode(boolean choose){
		if(choose){
			title.setText(getString(R.string.choose_group_title));
			addGroup.setVisibility(View.GONE);
		}else{
			title.setText(getString(R.string.groups_title));
			addGroup.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		fillGroups();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
        if (resultCode == RESULT_OK && requestCode == GroupActivity.PHOTO_SENT_RESULT) {
        	fillGroups();
        	pictureUri = null;
        	setChooseMode(false);
        }else  if (resultCode == RESULT_OK && requestCode == GroupActivity.GROUP_ADDED) {
        	fillGroups();
        }
				
	}


	class GroupsAdapter extends ArrayAdapter<Group>{

		public GroupsAdapter(Context context, int resource) {
			super(context, resource);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = getLayoutInflater().inflate(R.layout.group_item, parent,false);
				GroupHolder tag = new GroupHolder();
				tag.name = (TextView) convertView.findViewById(R.id.groupName);
				tag.count = (TextView) convertView.findViewById(R.id.badgeCount);
				tag.image = (NetworkImageView) convertView.findViewById(R.id.groupImage);
				tag.time = (TextView) convertView.findViewById(R.id.time);
				tag.parent = convertView;
				tag.arrow = (ImageView) convertView.findViewById(R.id.arrow);
				tag.enable = (ImageView) convertView.findViewById(R.id.enableGroup);
				tag.delete = (ImageView) convertView.findViewById(R.id.removeGroup);

				convertView.setTag(tag);
			}
			final GroupHolder holder = (GroupHolder)convertView.getTag();
			holder.id = groups[position].id;
			TextView name = holder.name;
			TextView count = holder.count;
			NetworkImageView image = holder.image;
			TextView time = holder.time;
			View _parent = holder.parent;

			
			Group group = getItem(position);
			name.setText(group.name);
			count.setText(""+group.getCount());
			
			if(group.image != null){
				image.setImageUrl(group.image, mApp.getImageLoader());
			}else{
				image.setImageUrl(null, mApp.getImageLoader());
			}
			
			if(group.time > 0){
				time.setVisibility(View.VISIBLE);
				time.setText(Tools.formatTime(group.time));
			}else{
				time.setVisibility(View.GONE);
			}
			
			if(group.isEnabled(mApp.getUserDetails().id)){
				((FrameLayout)_parent).setForeground(null);

			}else{
				((FrameLayout)_parent).setForeground(new ColorDrawable(getResources().getColor(R.color.group_disabled)));
			}
			
			if(editModeItem == position){
				holder.time.setVisibility(View.GONE);
				holder.arrow.setVisibility(View.GONE);
				holder.enable.setVisibility(View.VISIBLE);
				holder.delete.setVisibility(View.VISIBLE);
				((ViewGroup)holder.parent).getChildAt(0).setPressed(true);
				
				if(group.isEnabled(mApp.getUserDetails().id)){
					holder.enable.setImageResource(R.drawable.group_item_disable);
				}else{
					holder.enable.setImageResource(R.drawable.group_item_enable);
				}
				
				
				
				holder.enable.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View view) {
						
						Group group = mApp.getGroupById(((GroupHolder)((View)view.getParent().getParent()).getTag()).id);
						if(group.isEnabled(mApp.getUserDetails().id)){
							disableGroup(group.id);
						}else{
							enableGroup(group.id);
						}
					}
				});
				
				holder.delete.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View view) {
						removeGroup(((GroupHolder)((View)view.getParent().getParent()).getTag()).id);
						
					}
				});

			}else{
				
				((ViewGroup)holder.parent).getChildAt(0).setPressed(false);

				holder.time.setVisibility(View.VISIBLE);
				holder.arrow.setVisibility(View.VISIBLE);
				holder.enable.setVisibility(View.GONE);
				holder.delete.setVisibility(View.GONE);
				
			}
				
		    return convertView;
			
		}
		
		
		
		
	}
	
	
	
	class GroupHolder{
		View parent;
		TextView name;
		TextView count;
		NetworkImageView image;
		TextView time;
		ImageView arrow;
		ImageView enable;
		ImageView delete;
		int id;

	}



	@Override
	public View getTitleLayout() {
		ViewGroup view = (ViewGroup)getLayoutInflater().inflate(R.layout.title_groups, null);
		title = (TextView)view.findViewById(R.id.groups_title);
		addGroup = view.findViewById(R.id.btnAddGroup);
		return view;

	}
	
	@Override
	public boolean onActionBarItemSelected(View action_item) {
		switch(action_item.getId()){
			case R.id.btnAddGroup:
				Intent intent = new Intent(this, AddGroupActivity.class);
				startActivityForResult(intent, GroupActivity.GROUP_ADDED);
			return true;
			
			
		}
		
		return super.onActionBarItemSelected(action_item);
	}
	
	
	
	private void setEditMode(boolean mode){
		isEditMode = mode;
		if(isEditMode){
			
		}else{
		
		}
		
	}
	
	public void onCreateContextMenu(ContextMenu menu, View v,
	        ContextMenuInfo menuInfo) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		Group group =  groups[info.position];
		String groupName = group.name;
		menu.setHeaderTitle(getResources().getString(R.string.choose_group_action,groupName));
	
		if(group.isEnabled(mApp.getUserDetails().id)){
			menu.add(Menu.NONE,1,Menu.NONE,R.string.disable_group);
		}else{
			menu.add(Menu.NONE,1,Menu.NONE,R.string.enable_group);
		}
		
		
		menu.add(Menu.NONE,2,Menu.NONE,R.string.remove_group);


	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
	            .getMenuInfo();
		Group group = groups[info.position];
		if(item.getItemId() == 1){
			if(group.isEnabled(mApp.getUserDetails().id)){
				disableGroup(group.id);
			}else{
				enableGroup(group.id);
			}
			
		}else if(item.getItemId() == 2){
			removeGroup(group.id);
			

		}
	    return true;
	    
	 
	}
	
	
	private void removeGroup(int groupId){
		RemoveGroupFromMemberRequest request = new RemoveGroupFromMemberRequest();
		request.id = mApp.getUserDetails().id;
		request.group_id = groupId;
		new RemoveGroupFromMemberTask(this, new BaseTaskListener<RemoveGroupFromMemberResponse>() {
			
			@Override
			public void onSuccess(RemoveGroupFromMemberResponse response) {
				editModeItem = -1;
				mApp.setGroups(response.groups);
				fillGroups();
				
			}

			@Override
			public void onFailed(BaseResponse response) {
				// TODO Auto-generated method stub
				
			}
			
			
		}).execute(request);
	}
	
	private void disableGroup(int groupId){
		changeGroupStatus(groupId,Group.GROUP_STATUS_DISABLED);
	}
	
	private void enableGroup(int groupId){
		changeGroupStatus(groupId,Group.GROUP_STATUS_ENABLED);
	}
	
	private void changeGroupStatus(int groupId,int status){
		ChangeGroupStatusRequest request = new ChangeGroupStatusRequest();
		request.id = mApp.getUserDetails().id;
		request.group_id = groupId;
		request.status = status;
		new ChangeGroupStatusTask(this, new BaseTaskListener<ChangeGroupStatusResponse>() {
			
			@Override
			public void onSuccess(ChangeGroupStatusResponse response) {
				editModeItem = -1;
				mApp.setGroups(response.groups);
				fillGroups();
				
				
			}

			@Override
			public void onFailed(BaseResponse response) {
				// TODO Auto-generated method stub
				
			}
			
			
		}).execute(request);
	}
	
	private void fillGroups(){
		groups = mApp.getGroups();
		List<Group> list = Arrays.asList(groups);
		Collections.sort(list,new GroupsComparator(mApp.getUserDetails().id));
		groups = list.toArray(new Group[0]);
		
		groupsAdapter.clear();
		groupsAdapter.addAll(groups);
	}
	
//	@Override
//	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
//		
//	    menu.add(Menu.NONE,1,1,R.string.recommendations);
//	    menu.add(Menu.NONE,2,2,R.string.invitations);
//
//	    return super.onCreateOptionsMenu(menu);
//		
//		
//	}
//	
//	@Override
//	public boolean onOptionsItemSelected(
//			com.actionbarsherlock.view.MenuItem item) {
//		if(item.getItemId() == 1){
//			Intent intent = new Intent(this,RecommendationsActivity.class);
//			startActivity(intent);
//		}else if(item.getItemId() == 2){
//			Intent intent = new Intent(this,InvitationsActivity.class);
//			startActivity(intent);
//		}
//		return super.onOptionsItemSelected(item);
//	}
	
	   
}


