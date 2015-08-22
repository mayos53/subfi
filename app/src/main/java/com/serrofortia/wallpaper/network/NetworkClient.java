package com.serrofortia.wallpaper.network;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.serrofortia.wallpaper.network.model.requests.AddGroupRequest;
import com.serrofortia.wallpaper.network.model.requests.AddUserRequest;
import com.serrofortia.wallpaper.network.model.requests.AuthenticationRequest;
import com.serrofortia.wallpaper.network.model.requests.BaseRequest;
import com.serrofortia.wallpaper.network.model.requests.ChangeGroupStatusRequest;
import com.serrofortia.wallpaper.network.model.requests.ConfirmCodeRequest;
import com.serrofortia.wallpaper.network.model.requests.GetContactsRequest;
import com.serrofortia.wallpaper.network.model.requests.GetGroupsRequest;
import com.serrofortia.wallpaper.network.model.requests.GetInvitationsRequest;
import com.serrofortia.wallpaper.network.model.requests.GetRecommendationsRequest;
import com.serrofortia.wallpaper.network.model.requests.InviteUserRequest;
import com.serrofortia.wallpaper.network.model.requests.RecommendUserRequest;
import com.serrofortia.wallpaper.network.model.requests.RegisterRequest;
import com.serrofortia.wallpaper.network.model.requests.RemoveGroupFromMemberRequest;
import com.serrofortia.wallpaper.network.model.requests.RemoveMemberRequest;
import com.serrofortia.wallpaper.network.model.requests.RemoveRecommendationRequest;
import com.serrofortia.wallpaper.network.model.requests.ResendCodeRequest;
import com.serrofortia.wallpaper.network.model.requests.SetWallpaperRequest;
import com.serrofortia.wallpaper.network.model.responses.AddUserResponse;
import com.serrofortia.wallpaper.network.model.responses.AuthenticationResponse;
import com.serrofortia.wallpaper.network.model.responses.BaseResponse;
import com.serrofortia.wallpaper.network.model.responses.ChangeGroupStatusResponse;
import com.serrofortia.wallpaper.network.model.responses.GetContactsResponse;
import com.serrofortia.wallpaper.network.model.responses.GetGroupsResponse;
import com.serrofortia.wallpaper.network.model.responses.GetInvitationsResponse;
import com.serrofortia.wallpaper.network.model.responses.GetRecommendationsResponse;
import com.serrofortia.wallpaper.network.model.responses.RegisterResponse;
import com.serrofortia.wallpaper.network.model.responses.RemoveGroupFromMemberResponse;
import com.serrofortia.wallpaper.network.model.responses.RemoveMemberResponse;
import com.serrofortia.wallpaper.network.model.responses.SetWallpaperResponse;
import com.serrofortia.wallpaper.util.Tools;

public class NetworkClient {

	protected final String TAG = this.getClass().getSimpleName();
	
	//Development
	//private final static String URL_BASE       = "http://ec2-54-191-37-134.us-west-2.compute.amazonaws.com:3003";
	
	//Production(Test)
	//
	private final static String URL_BASE       = "http://ec2-54-191-37-134.us-west-2.compute.amazonaws.com:3000";
	
	
	
	private final static String URL_AUTHENTICATION = URL_BASE + "/users.json";
	private final static String URL_REGISTRATION = URL_BASE + "/users/register.json";
	private final static String URL_GET_GROUPS = URL_BASE + "/groups/user/%d.json";
	private final static String URL_LOGIN = URL_BASE + "/users/login.json";

	
	private final static String URL_ADD_USER = URL_BASE + "/groups/save_user.json";
	private final static String URL_ADD_GROUP = URL_BASE + "/groups.json";
	private final static String URL_SET_WALLPAPER= URL_BASE + "/groups/save_wallpaper.json";
	private final static String URL_REMOVE_GROUP_FROM_MEMBER = URL_BASE + "/groups/remove_group_from_member.json";
	private final static String URL_REMOVE_MEMBER = URL_BASE + "/groups/remove_member_from_group.json";
	private final static String URL_CHANGE_GROUP_STATUS = URL_BASE + "/groups/change_group_status.json";
	private final static String URL_RECOMMEND_USER = URL_BASE + "/groups/recommend_user.json";
	private final static String URL_INVITE_USER = URL_BASE + "/groups/invite_user.json";
	private final static String URL_REMOVE_RECOMMENDATION = URL_BASE + "/groups/remove_recommendation.json";


	private final static String URL_GET_RECOMMENDATIONS = URL_BASE + "/groups/get_recommendations.json";
	private final static String URL_GET_INVITATIONS = URL_BASE + "/groups/get_invitations.json";



	
	private final static String URL_CONFIRM_CODE =  URL_BASE + "/users/confirm_registration.json";
	private final static String URL_RESEND_CODE = URL_BASE + "/users/resend_code.json";
	private final static String URL_GET_CONTACTS = URL_BASE + "/users/get_contacts.json";
	
	

	


	

	public BaseResponse getData(String url,Object [] params,Class responseClass) {
		
		InputStream inputStream = null;
		BaseResponse result = null;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			
			url = String.format(url,params);
			
			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("Content-type", "application/json; charset=utf-8");
			Log.d(TAG,"getData url : "+url);
			HttpResponse httpResponse = httpclient.execute(httpGet);
			inputStream = httpResponse.getEntity().getContent();
			if (inputStream != null){
				String str = Tools.convertInputStreamToString(inputStream);
				Log.d(TAG,"getData reponse : "+str);

				result = (BaseResponse)new GsonBuilder().create().fromJson(str, responseClass);
			}
		} catch (Exception e) {
			Log.e("InputStream","", e);
		}

		return result;
	}

	

	public BaseResponse postData(String url, BaseRequest baseRequest,Class responseClass) {
		InputStream inputStream = null;
		BaseResponse result = null;
		
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();

			// url with the post data
			HttpPost httpost = new HttpPost(url);

			String json = new Gson().toJson(baseRequest);
			Log.d(TAG,"postData json length : "+json.length());
			
			Log.d(TAG,"postData url : "+url);
			
			Log.d(TAG,"postData request");
			longInfo(json);
			// passes the results to a string builder/entity
			StringEntity se = new StringEntity(json,"UTF-8");

			// sets the post request as the resulting string
			httpost.setEntity(se);
			// sets a request header so the page receving the request
			// will know what to do with it
			httpost.setHeader("Accept", "application/json");
			httpost.setHeader("Content-type", "application/json;charset=utf-8");

			
			HttpResponse response = httpclient.execute(httpost);
			inputStream = response.getEntity().getContent();
			if (inputStream != null){
				String str = Tools.convertInputStreamToString(inputStream);
				Log.d(TAG,"postData response : "+str);

				result = (BaseResponse)new Gson().fromJson(str, responseClass);
			}
			
		} catch (Exception e) {
			Log.e(TAG, null, e);
		}
		return result;

	}
	
	public  void longInfo(String str) {
	    if(str.length() > 4000) {
	        Log.d(TAG, str.substring(0, 4000));
	        longInfo(str.substring(4000));
	    } else
	        Log.d(TAG, str);
	}
	
	public BaseResponse postData(String url, Object[] params,
			Class responseClass) {
		Log.d(TAG,"postData url : "+url);
		InputStream inputStream = null;
		BaseResponse result = null;
		try {
			NameValuePair group = new BasicNameValuePair("wallpaper[group_id]",
					(String) params[0]);
			NameValuePair user = new BasicNameValuePair("wallpaper[user_id]",
							(String) params[1]);
			NameValuePair photo = new BasicNameValuePair("wallpaper[photo]",
					(String) params[2]);
			NameValuePair title = new BasicNameValuePair("wallpaper[title]",
					(String) params[3]);
			

			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpPost httpPost = new HttpPost(url);

			MultipartEntity entity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);

			
			entity.addPart(photo.getName(),
					new FileBody(new File(photo.getValue()), "image/jpeg"));

			entity.addPart(group.getName(), new StringBody(group.getValue()));
			entity.addPart(user.getName(), new StringBody(user.getValue()));
			entity.addPart(title.getName(), new StringBody(title.getValue(),Charset.forName("UTF-8")));


			httpPost.setEntity(entity);

			HttpResponse response = httpClient.execute(httpPost, localContext);
			inputStream = response.getEntity().getContent();
			if (inputStream != null) {
				String str = Tools.convertInputStreamToString(inputStream);
				Log.d(TAG,"postData response : "+str);

				result = (BaseResponse)new Gson().fromJson(str, responseClass);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
//	public BaseResponse postData(String url, Object[] params,
//			Class responseClass) {
//		Log.d(TAG,"postData url : "+url);
//		InputStream inputStream = null;
//		BaseResponse result = null;
//		try {
//			NameValuePair group = new BasicNameValuePair("wallpaper[group_id]",
//					(String) params[0]);
//			NameValuePair user = new BasicNameValuePair("wallpaper[user_id]",
//							(String) params[1]);
//			NameValuePair photo = new BasicNameValuePair("wallpaper[photo]",
//					(String) params[2]);
//			
//			
//			NameValuePair title = new BasicNameValuePair("wallpaper[title]",
//					(String) params[3]);
//			
//
//			HttpClient httpClient = new DefaultHttpClient();
//			HttpContext localContext = new BasicHttpContext();
//			HttpPost httpPost = new HttpPost(url);
//			
//			httpPost.setHeader("Content-type", "application/json; charset=utf-8");
//
//			MultipartEntity entity = new MultipartEntity(
//					HttpMultipartMode.BROWSER_COMPATIBLE);
//
//			
//			entity.addPart(photo.getName(),
//					new FileBody(new File(photo.getValue()), "image/jpeg"));
//
//			entity.addPart(group.getName(), new StringBody(group.getValue()));
//			entity.addPart(user.getName(), new StringBody(user.getValue()));
//			entity.addPart(title.getName(), new StringBody(title.getValue()));
//
//
//			httpPost.setEntity(entity);
//
//			HttpResponse response = httpClient.execute(httpPost, localContext);
//			inputStream = response.getEntity().getContent();
//			if (inputStream != null) {
//				String str = Tools.convertInputStreamToString(inputStream);
//				Log.d(TAG,"postData response : "+str);
//
//				result = new Gson().fromJson(str, responseClass);
//
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return result;
//	}
//		

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		AuthenticationResponse response = (AuthenticationResponse)postData(URL_AUTHENTICATION, request, AuthenticationResponse.class);
		return response;
	}
	
	public AuthenticationResponse login(AuthenticationRequest request) {
		AuthenticationResponse response = (AuthenticationResponse)postData(URL_LOGIN, request, AuthenticationResponse.class);
		return response;
	}
	
	public RegisterResponse register(RegisterRequest registerRequest) {
		RegisterResponse baseResponse = (RegisterResponse)postData(URL_REGISTRATION, registerRequest, RegisterResponse.class);
		return baseResponse;
	}

	public GetGroupsResponse getGroups(GetGroupsRequest request) {
		GetGroupsResponse response = (GetGroupsResponse)getData(URL_GET_GROUPS, new Integer[]{request.userId}, GetGroupsResponse.class);
		return response;

	}

	public GetGroupsResponse addGroup(AddGroupRequest request) {
		GetGroupsResponse response = (GetGroupsResponse)postData(URL_ADD_GROUP, request, GetGroupsResponse.class);
		return response;
	}

	public AddUserResponse addUser(AddUserRequest request) {
		AddUserResponse response = (AddUserResponse)postData(URL_ADD_USER, request, AddUserResponse.class);
		return response;
	}

	public SetWallpaperResponse setWallpaper(SetWallpaperRequest request) {
		SetWallpaperResponse response = (SetWallpaperResponse)postData(URL_SET_WALLPAPER, new String[]{String.valueOf(request.groupId),String.valueOf(request.user_id),request.path,request.title}, SetWallpaperResponse.class);
		return response;
	}

	public BaseResponse confirm(ConfirmCodeRequest request) {
		BaseResponse response = (BaseResponse)postData(URL_CONFIRM_CODE,request,  BaseResponse.class);
		return response;
		
	}
	
	public BaseResponse resendCode(ResendCodeRequest request) {
		BaseResponse response = (BaseResponse)postData(URL_RESEND_CODE,request,  BaseResponse.class);
		return response;
		
	}



	public GetContactsResponse getContacts(GetContactsRequest request) {
		GetContactsResponse response = (GetContactsResponse)postData(URL_GET_CONTACTS,request,  GetContactsResponse.class);
		return response;
		
	}
	
	public RemoveGroupFromMemberResponse removeGroupFromMember(RemoveGroupFromMemberRequest request) {
		RemoveGroupFromMemberResponse response = (RemoveGroupFromMemberResponse)postData(URL_REMOVE_GROUP_FROM_MEMBER,request,  RemoveGroupFromMemberResponse.class);
		return response;
		
	}
	
	public RemoveMemberResponse removeMember(RemoveMemberRequest request) {
		RemoveMemberResponse response = (RemoveMemberResponse)postData(URL_REMOVE_MEMBER,request,  RemoveMemberResponse.class);
		return response;
		
	}
	
	public ChangeGroupStatusResponse changeGroupStatus(ChangeGroupStatusRequest request) {
		ChangeGroupStatusResponse response = (ChangeGroupStatusResponse)postData(URL_CHANGE_GROUP_STATUS ,request,  ChangeGroupStatusResponse.class);
		return response;
		
	}
	
	public BaseResponse recommendUser(RecommendUserRequest request) {
		BaseResponse response = (BaseResponse)postData(URL_RECOMMEND_USER,request,  BaseResponse.class);
		return response;
		
	}
	
	public GetRecommendationsResponse getRecommendations(GetRecommendationsRequest request) {
		GetRecommendationsResponse response = (GetRecommendationsResponse)postData(URL_GET_RECOMMENDATIONS,request,  GetRecommendationsResponse.class);
		return response;
		
	}
	public GetInvitationsResponse getInvitations(GetInvitationsRequest request) {
		GetInvitationsResponse response = (GetInvitationsResponse)postData(URL_GET_INVITATIONS,request,  GetInvitationsResponse.class);
		return response;
		
	}
	
	public BaseResponse inviteUser(InviteUserRequest request) {
		BaseResponse response = (BaseResponse)postData(URL_INVITE_USER,request,  BaseResponse.class);
		return response;
		
	}



	public GetRecommendationsResponse removeRecommendation(RemoveRecommendationRequest removeRecommendationRequest) {
		GetRecommendationsResponse response = (GetRecommendationsResponse)postData(URL_REMOVE_RECOMMENDATION,removeRecommendationRequest,  GetRecommendationsResponse.class);
		return response;
	}
	
	

	

}
