package com.serrofortia.wallpaper;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.serrofortia.wallpaper.controller.AddUserTask;
import com.serrofortia.wallpaper.controller.BaseTaskListener;
import com.serrofortia.wallpaper.controller.GetRecommendationsTask;
import com.serrofortia.wallpaper.controller.RemoveRecommendationTask;
import com.serrofortia.wallpaper.model.Recommendation;
import com.serrofortia.wallpaper.network.model.requests.AddUserRequest;
import com.serrofortia.wallpaper.network.model.requests.GetRecommendationsRequest;
import com.serrofortia.wallpaper.network.model.requests.RemoveRecommendationRequest;
import com.serrofortia.wallpaper.network.model.responses.AddUserResponse;
import com.serrofortia.wallpaper.network.model.responses.BaseResponse;
import com.serrofortia.wallpaper.network.model.responses.GetRecommendationsResponse;
import com.serrofortia.wallpaper.ui.components.AlertBuilder;
import com.serrofortia.wallpaper.ui.components.AlertBuilder.ButtonListener;
import com.serrofortia.wallpaper.util.Loger;

public class RecommendationsActivity extends BaseSlidingActivity{

	
	
	protected final String TAG = this.getClass().getSimpleName();
	protected WallpaperApp mApp;
	protected MyAdapter mAdapter;
	protected Recommendation []  recommendations;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		setContentView(R.layout.recommendations);
    	
		mApp = (WallpaperApp)getApplication();

    	ListView listView = (ListView)findViewById(android.R.id.list);
    	mAdapter = new MyAdapter(this, 0);
    	listView.setAdapter(mAdapter);
    	GetRecommendationsTask task = new GetRecommendationsTask(this, new BaseTaskListener<GetRecommendationsResponse>() {
			
			@Override
			public void onSuccess(GetRecommendationsResponse response) {
				mApp.setRecommendations(response.recommendations);
				fillRecommendations();
    			
				
			}
			
			@Override
			public void onFailed(BaseResponse response) {
				Loger.e(TAG, "error in getting recommendations");
				
			}
		});
    	
    	GetRecommendationsRequest request = new GetRecommendationsRequest();
    	request.user_id = mApp.getUserDetails().id;
    	task.execute(request);
    	
    	fillRecommendations();
    	
    	listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				showRecommendationPopup(recommendations[position]);
				
			}
		});
    	
    	
    	
    }
    
    
    private void showRecommendationPopup(final Recommendation recommendation){
	    String recommenderName = recommendation.recommender_name;
		final String groupName = recommendation.group_name;
		final String userName = recommendation.user_name;
		String message = getString(R.string.friend_was_recommended_notification_message,userName,recommenderName,groupName);
		
		AlertBuilder.showMessage(mainView, this, message, true,
				new ButtonListener() {
					
					@Override
					public void onClick() {
						addContact(recommendation.group_id, recommendation.user_id, groupName, userName);
						
					}
				}, new ButtonListener() {
					
					@Override
					public void onClick() {
						removeRecommendation(recommendation.id);
						
					}
				},R.string.yes,R.string.no);
    }
    
    private void removeRecommendation(int recommendationId){
    	RemoveRecommendationTask task = new RemoveRecommendationTask(this,new BaseTaskListener<GetRecommendationsResponse>() {

			@Override
			public void onSuccess(GetRecommendationsResponse response) {
				mApp.setRecommendations(response.recommendations);
				fillRecommendations();
			
				
			}

			@Override
			public void onFailed(BaseResponse response) {
				
				
				
			}
		});
		
		
		RemoveRecommendationRequest request = new RemoveRecommendationRequest();
		request.id = recommendationId;
		request.user_id = mApp.getUserDetails().id;
		task.execute(request);
		
    }
    
    private void fillRecommendations(){
    	recommendations = mApp.getRecommendations();
    	mAdapter.clear();
    	if(recommendations != null){
    		mAdapter.addAll(recommendations);
    	}
    }

    
    private void addContact(int group_id ,int user_id,final String group_name,final String user_name){
		AddUserTask task = new AddUserTask(this,new BaseTaskListener<AddUserResponse>() {

			@Override
			public void onSuccess(AddUserResponse response) {
				mApp.updateGroup(response.group);
				mApp.setRecommendations(response.recommendations);

				fillRecommendations();
				String message = getString(R.string.friend_was_added,user_name,group_name);
				AlertBuilder.showMessage(mainView, RecommendationsActivity.this, message, false,null,null);
				
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


    class MyAdapter extends ArrayAdapter<Recommendation>{

		public MyAdapter(Context context, int resource) {
			super(context, resource);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = getLayoutInflater().inflate(R.layout.recommendation_item, parent,false);
				RecommendationHolder tag = new RecommendationHolder();
				tag.text = (TextView) convertView.findViewById(R.id.text);
				tag.groupName = (TextView) convertView.findViewById(R.id.groupName);
				tag.image = (NetworkImageView) convertView.findViewById(R.id.groupImage);


				convertView.setTag(tag);
			}
			RecommendationHolder holder = (RecommendationHolder)convertView.getTag();
			TextView text = holder.text;
			TextView groupName = holder.groupName;
			NetworkImageView image = holder.image;


			Recommendation recommendation = getItem(position);
			String _text = getString(R.string.friend_was_recommended_item,recommendation.user_name,recommendation.recommender_name);
			groupName.setText(recommendation.group_name);
			image.setImageUrl(recommendation.image, mApp.getImageLoader());
			text.setText(_text);
			
			
			return convertView;
			
		}
		
		
		
		
	}
	
	
	
	class RecommendationHolder{
		TextView groupName;
		TextView text;
		NetworkImageView image;
		
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
		((TextView)view.findViewById(R.id.text)).setText(R.string.recommendations);
		return view;
	}
}

    
    
    
	
	