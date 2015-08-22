package com.serrofortia.wallpaper;

import java.nio.charset.Charset;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.serrofortia.wallpaper.controller.AddGroupTask;
import com.serrofortia.wallpaper.controller.BaseTaskListener;
import com.serrofortia.wallpaper.controller.ConfirmCodeTask;
import com.serrofortia.wallpaper.controller.ResendCodeTask;
import com.serrofortia.wallpaper.network.model.requests.AddGroupRequest;
import com.serrofortia.wallpaper.network.model.requests.ConfirmCodeRequest;
import com.serrofortia.wallpaper.network.model.requests.ResendCodeRequest;
import com.serrofortia.wallpaper.network.model.responses.BaseResponse;
import com.serrofortia.wallpaper.network.model.responses.GetGroupsResponse;
import com.serrofortia.wallpaper.util.Loger;
import com.serrofortia.wallpaper.util.Tools;

public class AddGroupActivity extends BaseActivity{

	
	
	protected final String TAG = this.getClass().getSimpleName();
	protected WallpaperApp mApp;

    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.add_group);
    	
		mApp = (WallpaperApp)getApplication();

    	View btnConfirm = findViewById(R.id.btnAddGroup);

    	
    	
    	final EditText etAddGroup = (EditText)findViewById(R.id.editGroupName);
    	btnConfirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String groupName = etAddGroup.getEditableText().toString();
				if(!Tools.isEmpty(groupName)){
					AddGroupTask task = new AddGroupTask(AddGroupActivity.this,new BaseTaskListener<GetGroupsResponse>() {
						
						@Override
						public void onSuccess(
								GetGroupsResponse response) {
							mApp.setGroups(response.groups);
							setResult(RESULT_OK,getIntent());
							finish();
							
						}

						@Override
						public void onFailed(BaseResponse response) {
							
						}
					});
					AddGroupRequest request = new AddGroupRequest();
					request.name = groupName;
					request.user_id = mApp.getUserDetails().id;
					task.execute(request);
				}else{
					Tools.showToast(AddGroupActivity.this, R.string.group_name_empty);
				}
				
				
				
			}
		});
    	
    	
    	
    }



	@Override
	public View getTitleLayout() {
		ViewGroup view = (ViewGroup)getLayoutInflater().inflate(R.layout.title_add_group, null);
		
		
		return view;
	}
	
	@Override
	public boolean onActionBarItemSelected(View action_item) {
		if(action_item.getId() == R.id.group_back){
			finish();
			return true;
		}else{
		
		return super.onActionBarItemSelected(action_item);
		}
	}
}

    
    
    
	
	