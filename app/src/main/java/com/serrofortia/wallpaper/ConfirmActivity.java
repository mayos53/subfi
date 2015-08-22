package com.serrofortia.wallpaper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.serrofortia.wallpaper.controller.BaseTaskListener;
import com.serrofortia.wallpaper.controller.ConfirmCodeTask;
import com.serrofortia.wallpaper.controller.ResendCodeTask;
import com.serrofortia.wallpaper.network.model.requests.ConfirmCodeRequest;
import com.serrofortia.wallpaper.network.model.requests.ResendCodeRequest;
import com.serrofortia.wallpaper.network.model.responses.BaseResponse;
import com.serrofortia.wallpaper.ui.components.AlertBuilder;
import com.serrofortia.wallpaper.util.Tools;

public class ConfirmActivity extends BaseActivity{

	
	
	protected final String TAG = this.getClass().getSimpleName();
	protected WallpaperApp mApp;

    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.confirm);
    	
		mApp = (WallpaperApp)getApplication();

    	View btnConfirm = findViewById(R.id.btnConfirmCode);
    	//View btnResendCode = findViewById(R.id.btnResendCode);

    	
    	
    	final EditText etConfirm = (EditText)findViewById(R.id.editCode);
    	btnConfirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String code = etConfirm.getEditableText().toString();
				if(!Tools.isEmpty(code)){
					ConfirmCodeRequest request = new ConfirmCodeRequest();
					request.code = code;
					request.id = mApp.getUserDetails().id;
					
					ConfirmCodeTask task = new ConfirmCodeTask(ConfirmActivity.this, new BaseTaskListener<BaseResponse>() {

						@Override
						public void onSuccess(BaseResponse success) {
							mApp.setConfirmed();
							
							mApp.registerToGCM();
							mApp.getAllDataAndStartGroups(ConfirmActivity.this);
							
							
						}

						@Override
						public void onFailed(BaseResponse response) {
							Tools.showToast(ConfirmActivity.this, R.string.confirmation_failed);
							//AlertBuilder.showMessage(mainView,ConfirmActivity.this, R.string.confirmation_failed,false,null,null);

						}
					});
					task.execute(request);
				}
				
				
				
			}
		});
    	
    	
//    	btnResendCode.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				
//					ResendCodeRequest request = new ResendCodeRequest();
//					request.id = mApp.getUserDetails().id;
//					ResendCodeTask task = new ResendCodeTask(ConfirmActivity.this, new BaseTaskListener<BaseResponse>() {
//
//						@Override
//						public void onSuccess(BaseResponse success) {
//							AlertBuilder.showMessage(mainView,ConfirmActivity.this, R.string.code_was_sent, false,null,null);
//							
//						}
//
//						@Override
//						public void onFailed(BaseResponse response) {
//							//Tools.showError(ConfirmActivity.this, R.string.confirmation_failed);
//							
//						}
//					});
//					task.execute(request);
//				}
//				
//				
//				
//			
//		});
    }



	@Override
	public View getTitleLayout() {
		ViewGroup view = (ViewGroup)getLayoutInflater().inflate(R.layout.title_register, null);
		TextView title = (TextView)view.findViewById(R.id.title);
		title.setText(getString(R.string.title_confirmation));
		view.findViewById(R.id.btnOkRegister).setVisibility(View.GONE);
		
		return view;
	}
	
	@Override
	public boolean onActionBarItemSelected(View action_item) {
		
		return super.onActionBarItemSelected(action_item);
	}
}

    
    
    
	
	