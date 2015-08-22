package com.serrofortia.wallpaper;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.serrofortia.wallpaper.controller.AuthenticateTask;
import com.serrofortia.wallpaper.controller.BaseTaskListener;
import com.serrofortia.wallpaper.model.Country;
import com.serrofortia.wallpaper.model.UserDetails;
import com.serrofortia.wallpaper.network.model.requests.AuthenticationRequest;
import com.serrofortia.wallpaper.network.model.responses.AuthenticationResponse;
import com.serrofortia.wallpaper.network.model.responses.BaseResponse;
import com.serrofortia.wallpaper.util.Tools;

public class RegisterActivity extends BaseActivity{

	protected final String TAG = this.getClass().getSimpleName();
	
	public final static String INTENT_EXTRA_EMAIL = "INTENT_EXTRA_EMAIL";

	protected WallpaperApp mApp;
	
	protected EditText etName;
	protected EditText etPhone;
	//protected EditText etMail;
	protected TextView tvCountryCode;
	
	protected String countryCode;
	//protected String email;

	
	private View btnOkRegister;
	


	TextView checkConditionsText;
	CheckBox checkBox;
	Spinner spCountry;

	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		mApp = (WallpaperApp)getApplication();
		
		
		
		
		
		etPhone = (EditText)findViewById(R.id.editPhone);
		etName = (EditText)findViewById(R.id.editName);
		
		//etMail = (EditText)findViewById(R.id.editMail);
		tvCountryCode = (TextView)findViewById(R.id.editCountryCode);
		
		
		checkConditionsText = (TextView)findViewById(R.id.conditions_link);
		
		
		//String text = "<a href=\"http://www.google.com\">Google</a>";
		//conditions.setText(Html.fromHtml(getResources().getString(R.string.conditions)));
		//conditions.setText(getResources().getString(R.string.conditions));
		checkConditionsText.setText(Html.fromHtml(getResources().getString(R.string.conditions)));
		checkConditionsText.setMovementMethod(LinkMovementMethod.getInstance()); 
		
		
		checkBox = (CheckBox)findViewById(R.id.checkConditions);
		spCountry = (Spinner)findViewById(R.id.countryCode);
		final List<Country> countries = mApp.getCountries(this);
		countries.add(0,new Country("-1", getString(R.string.choose_country)));
		
		spCountry.setAdapter(new ArrayAdapter<Country>(this,android.R.layout.simple_list_item_1,countries)); 
		spCountry.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, 
		            int position, long id) {
				if(position > 0){
					countryCode = countries.get(position).code;
					tvCountryCode.setText("+"+countryCode);
				}else{
					countryCode = null;
					tvCountryCode.setText("");

				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		etPhone.addTextChangedListener(new TextWatcher() {

	          public void afterTextChanged(Editable s) {
	            
	          }

	          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	          public void onTextChanged(CharSequence s, int start, int before, int count) {
	        	
	        	  
	          }
	       });
	    
//		email = getIntent().getStringExtra(INTENT_EXTRA_EMAIL);
//		if(email != null){
//			etMail.setVisibility(View.GONE);
//		}

	}
	
	
	


	@Override
	public View getTitleLayout() {
		ViewGroup view = (ViewGroup)getLayoutInflater().inflate(R.layout.title_register, null);
		btnOkRegister = view.findViewById(R.id.btnOkRegister);
		return view;
		
	}
	
	@Override
	public boolean onActionBarItemSelected(View action_item) {
		switch(action_item.getId()){
		case R.id.btnOkRegister:
			
			if(!validateFields()){
				Tools.showToast(this, R.string.validate_register_fields);
				return true;
			}
			if(!validateCheckConditions()){
				Tools.showToast(this, R.string.validate_conditions);
				return true;
			}
			
			Tools.hideKeyboard(this, etName);
			AuthenticateTask authenticateTask = new AuthenticateTask(RegisterActivity.this,new BaseTaskListener<AuthenticationResponse>() {

					@Override
					public void onSuccess(AuthenticationResponse response) {
						UserDetails userDetails = new UserDetails();
						userDetails.id = response.id;
						userDetails.name = response.name;
						userDetails.phone = response.phone;
						userDetails.countryCode = response.countryCode;

						mApp.saveUserDetails(userDetails);
					
						if(Tools.SMS){
							Intent i = new Intent(RegisterActivity.this,ConfirmActivity.class);
							startActivity(i);
							finish();
						}else{
							mApp.registerToGCM();
							mApp.getAllDataAndStartGroups(RegisterActivity.this);
						}
					
						
					}

					@Override
					public void onFailed(BaseResponse response) {
						
						
					}
				});
				
				AuthenticationRequest request = new AuthenticationRequest();
				request.name = etName.getText().toString(); 
				request.countryCode =  countryCode;
				request.phone = etPhone.getText().toString(); 
				authenticateTask.execute(request);
				
			}
		
		
			
		
		return true;
			
	}
	
		
	


	private boolean validateFields(){
		if(countryCode != null && etPhone.getText().length() > 0 && etName.getText().length() > 0){
			return true;
		}
		return false;
	}
	
	private boolean validateCheckConditions(){
		return checkBox.isChecked();
	}
	
	
	
}
