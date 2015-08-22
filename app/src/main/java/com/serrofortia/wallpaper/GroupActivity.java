package com.serrofortia.wallpaper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.serrofortia.wallpaper.model.Dated;
import com.serrofortia.wallpaper.model.Dated.DatedComparator;
import com.serrofortia.wallpaper.model.Event;
import com.serrofortia.wallpaper.model.Group;
import com.serrofortia.wallpaper.model.Wallpaper;
import com.serrofortia.wallpaper.util.Tools;

public class GroupActivity extends BaseActivity{

	private final static int PHOTO_PICKER_RESULT = 1002;
	private final static int PHOTO_TAKEN_RESULT = 1004;

	public final static int PHOTO_SENT_RESULT = 1003;
	public final static int GROUP_ADDED= 1005;



	public final static String  EXTRA_GROUP_ID = "GROUP_ID";
	
	protected final String TAG = this.getClass().getSimpleName();
	protected WallpaperApp mApp;
	//protected ContactsAdapter contactsAdapter;
	protected WallpapersAdapter wallpapersAdapter;

    protected Group group;	

    
    protected TextView txtName;
    private PopupWindow popupMenu;
    private ImageView iconUpload;
    
    
    private View btnGallery;
    private View btnPhoto;
    

    
    
    
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group);
		
		mApp = (WallpaperApp)getApplication();
		
		
		
		int groupId = getIntent().getExtras().getInt(EXTRA_GROUP_ID,0);
		group = mApp.getGroupById(groupId);
		
		
		
	//	ListView listView = (ListView)findViewById(android.R.id.list);
		
//		contactsAdapter = new ContactsAdapter(this, 0);
//		listView.setAdapter(contactsAdapter);
		
		
		ListView listWallpapers = (ListView)findViewById(R.id.groupWallpapers);
		
		wallpapersAdapter = new WallpapersAdapter(this, 0);
		listWallpapers.setAdapter(wallpapersAdapter);
		
		
		View btnOpenUsers = findViewById(R.id.openUsers);
		btnOpenUsers.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(GroupActivity.this, MembersActivity.class);
				i.putExtra(EXTRA_GROUP_ID,group.id);
				startActivity(i);
				
			}
		});
		

		
		
		fillGroupDetails();
		
		
		View view = LayoutInflater.from(this).inflate(R.layout.add_wallpaper_options, null);
		int width = (int)Tools.convertDpToPixel(142, this);
		int height = (int)Tools.convertDpToPixel(62, this);
		
		popupMenu = new PopupWindow(width,height);
		popupMenu.setContentView(view);
		popupMenu.setBackgroundDrawable(new BitmapDrawable());
		popupMenu.setOutsideTouchable(true);
		
		
		btnGallery = view.findViewById(R.id.gallery);
		
		btnGallery.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				popupMenu.dismiss();
				Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PHOTO_PICKER_RESULT);
             
			}
		});
		
		btnPhoto = view.findViewById(R.id.photo);
		
		btnPhoto.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
                popupMenu.dismiss();
				Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); 
	            startActivityForResult(cameraIntent, PHOTO_TAKEN_RESULT);	

			}
		});
		
	}
	
	
	protected void showActivityChooser(){
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		final AlertDialog alertDialog = alert.create();
		
		alertDialog.setTitle(R.string.share_wallpaper);

		View view = LayoutInflater.from(this).inflate(R.layout.choose_action, null);
		ListView listView = (ListView)view.findViewById(android.R.id.list);
		String takePhotoOption = getResources().getString(R.string.take_photo);
		String pickFromGallery = getResources().getString(R.string.pick_gallery);
		
		listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,new String[]{takePhotoOption,pickFromGallery}));
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if(position == 0){
					Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); 
		            startActivityForResult(cameraIntent, PHOTO_TAKEN_RESULT);	
				}else{
					Intent intent = new Intent();
	                intent.setType("image/*");
	                intent.setAction(Intent.ACTION_GET_CONTENT);
	                startActivityForResult(intent, PHOTO_PICKER_RESULT);	
					
				}
				
				alertDialog.dismiss();
				
			}
		});
		
		
		alertDialog.setView(view);
		alertDialog.show();

		
		
	}
	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
			switch (requestCode) {
				
				
				case PHOTO_PICKER_RESULT:
				case PHOTO_TAKEN_RESULT:
					
				if (resultCode == RESULT_OK && null != data) {
					Uri selectedImage = data.getData();
					
					
					Intent intent = new Intent(this, PaintActivity.class);
					intent.putExtra(PaintActivity.EXTRA_PICTURE_URI, selectedImage);

					intent.putExtra(GroupActivity.EXTRA_GROUP_ID, group.id);

					startActivityForResult(intent,PHOTO_SENT_RESULT);



				}
			
			
					
					
				break;
				
				
				case PHOTO_SENT_RESULT:
					group = mApp.getGroupById(group.id);
					fillGroupDetails();
					
				break;	
				
			}
        }
			
			

    } 
    

	private void fillGroupDetails(){


		 txtName.setText(group.name);
		
//		contactsAdapter.clear();
//		for(User user:group.users){
//			contactsAdapter.add(user);
//		}
		
		wallpapersAdapter.clear();
		
		List<Dated> array = new ArrayList<Dated>();
		for(Wallpaper wallpaper:group.wallpapers){
			array.add(wallpaper);
		}
		for(Event event:group.events){
			array.add(event);
		}
		
		Collections.sort(array,new DatedComparator());
		
		wallpapersAdapter.addAll(array);
		if(group.getCount() > 1){
			iconUpload.setVisibility(View.VISIBLE);
		}else{
			iconUpload.setVisibility(View.GONE);
		}
		
		
	}
	
	
//	@Override
//	protected void onResume() {
//		super.onResume();
//		//when return from contacts - show/hide icon depends if contacts list empty
//		group = mApp.getGroupById(group.id);
//		
//		
//		
//		
//		
//		
//	}

	
	
//	class ContactsAdapter extends ArrayAdapter<User>{
//
//		public ContactsAdapter(Context context, int resource) {
//			super(context, resource);
//		}
//		
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			if(convertView == null){
//				convertView = getLayoutInflater().inflate(R.layout.contact_item, parent,false);
//				ContactHolder tag = new ContactHolder();
//				tag.name = (TextView) convertView.findViewById(R.id.contactName);
//
//				convertView.setTag(tag);
//			}
//			ContactHolder holder = (ContactHolder)convertView.getTag();
//			TextView name = holder.name;
//
//			name.setText(getItem(position).name+" - "+getItem(position).phone);
//			//phone.setText(getItem(position).phone);
//
//			return convertView;
//			
//		}
//		
//		
//	}
//	
//	class ContactHolder{
//		TextView name;
//		TextView phone;
//
//	}
	
	class WallpapersAdapter extends ArrayAdapter<Dated>{

		private final static int TYPE_LEFT = 0;
		private final static int TYPE_RIGHT = 1;
		private final static int TYPE_EVENT = 2;


		
		public WallpapersAdapter(Context context, int resource) {
			super(context, resource);
		}
		
		@Override
        public int getItemViewType(int position) {
           if(getItem(position) instanceof Event){
        	   return TYPE_EVENT;
           }else{
        	   Wallpaper wallpaper = (Wallpaper)getItem(position);
        	   if(wallpaper.user.id == mApp.getUserDetails().id){
	        	   return TYPE_RIGHT;
	           }else{
	        	   return TYPE_LEFT;
	           }
           }
           
        }
 
        @Override
        public int getViewTypeCount() {
            return 3;
        }
 
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);
			 Object object = getItem(position);

			if(convertView == null){
				 switch (type) {
	                 case TYPE_LEFT:
	                 {
	                	 convertView = getLayoutInflater().inflate(R.layout.wallpaper_item_left, null);
	                	
	                 }
	                 break;
	                 case TYPE_RIGHT:
	                 {
	                	 convertView = getLayoutInflater().inflate(R.layout.wallpaper_item_right, null);
	                	
	                 }
	                 break;
	                 case TYPE_EVENT:
	                 {
	                	 convertView = getLayoutInflater().inflate(R.layout.event, null);
	                	
	                	 
	                 }
	                 break;
				 }
				
					
					if(object instanceof Wallpaper){
						WallpaperHolder holder = new WallpaperHolder(convertView);
						holder.populate((Wallpaper)object);
					}else{
						EventHolder holder = new EventHolder(convertView);
						holder.populate((Event)object);
					}
				
			}else{
				if(object instanceof Wallpaper){
					WallpaperHolder holder = (WallpaperHolder)convertView.getTag();
					holder.populate((Wallpaper)object);
				}else{
					EventHolder holder = (EventHolder)convertView.getTag();
					holder.populate((Event)object);
				}
			}
			
			
			
			
			//phone.setText(getItem(position).phone);

			return convertView;
			
		}
		
		
	}
	
	class WallpaperHolder{
		NetworkImageView imageView;
		TextView user;
		TextView time;
		TextView title;
		
		public WallpaperHolder(View view){
			imageView = (NetworkImageView) view.findViewById(R.id.groupImage);
			user = (TextView) view.findViewById(R.id.wallpaperUser);
			time = (TextView) view.findViewById(R.id.time);
			title = (TextView) view.findViewById(R.id.picTitle);
			view.setTag(this);
		}
		
		public void populate(Wallpaper wallpaper){
			
			imageView.setImageUrl(wallpaper.path, mApp.getImageLoader());
			if(wallpaper.user != null){
				user.setText(Tools.firstUpperCase(wallpaper.user.name));
			}
			
			if(wallpaper.timeSec != 0){
				time.setText(Tools.formatTime(wallpaper.timeSec));
			}
			
			if(wallpaper.title != null && !wallpaper.title.equals("")){
				title.setVisibility(View.VISIBLE);
				title.setText(wallpaper.title);
			}else{
				title.setVisibility(View.GONE);
			}
		}
	}
	
	class EventHolder{
		TextView text;
		
		public EventHolder(View view){
			text = (TextView) view.findViewById(R.id.text);
			view.setTag(this);
		}
		
		public void populate(Event event){
			String _text = getResources().getString(R.string.left_group,""+Tools.firstUpperCase(""+event.user_name));
			text.setText(_text);
		}
	}

	@Override
	public View getTitleLayout() {
		ViewGroup view = (ViewGroup)getLayoutInflater().inflate(R.layout.title_group, null);
		txtName = (TextView)view.findViewById(R.id.group_title);
		iconUpload = (ImageView)view.findViewById(R.id.btnAddWallpaper);

		return view;
	}
	
	@Override
	public boolean onActionBarItemSelected(View action_item) {
		switch(action_item.getId()){
			case R.id.btnAddWallpaper:
				//showActivityChooser();
				if(!popupMenu.isShowing()){
					popupMenu.showAsDropDown(getActionBar().getCustomView(),Tools.getScreenDimensions(this).x-popupMenu.getWidth(),0);
				}
			
			return true;
			
			case R.id.group_back:
				finish();
				
			return true;	
			
			
		}
		
		return super.onActionBarItemSelected(action_item);
	}

}