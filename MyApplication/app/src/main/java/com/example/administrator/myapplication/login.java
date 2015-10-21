package com.example.administrator.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/10/8.
 */
public class login extends Activity implements View.OnClickListener {
	public  ImageLoader imageLoader;
	public  String user_name = null;
	public String avatar_url = null ;
	public  Intent intent ;
	public static String mAppid = "222222";
	public static Tencent mTencent;
	private TextView mUserInfo;
	private ImageView mUserLogo;
	private UserInfo mInfo;
	Button mNewLoginButton ;
	public SharedPreferences sharedPreferences ;
	public  SharedPreferences.Editor editor ;
	private static boolean isServerSideLogin = false;
	private static final String TAG = MainActivity.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		mTencent = Tencent.createInstance(mAppid, this);
       imageLoader = ImageLoader.getInstance();
	   initView();

	}

	public  void initView(){
		 mNewLoginButton = (Button)findViewById(R.id.to_main);
		mNewLoginButton.setOnClickListener(this);
		mUserInfo = (TextView) findViewById(R.id.user_nickname);
		mUserLogo = (ImageView) findViewById(R.id.user_logo);
		updateLoginButton();
	}

	@Override
	public void onClick(View view) {
       //		Intent intent = new Intent(this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		//startActivity(intent);

		//onClickLogin();
      Intent intent = new Intent();
		intent.setClass(getApplicationContext(),MainActivity.class);
		startActivity(intent);


	}

	private void updateLoginButton() {
		if (mTencent != null && mTencent.isSessionValid()) {
			if (isServerSideLogin) {
				mNewLoginButton.setTextColor(Color.BLUE);
				mNewLoginButton.setText("登录");

			}
		} else {
			mNewLoginButton.setTextColor(Color.BLUE);
			mNewLoginButton.setText("登录");

		}
	}

	private void updateUserInfo() {
		if (mTencent != null && mTencent.isSessionValid()) {
			IUiListener listener = new IUiListener() {

				@Override
				public void onError(UiError e) {

				}

				@Override
				public void onComplete(final Object response) {
					Message msg = new Message();
					msg.obj = response;
					msg.what = 0;
					mHandler.sendMessage(msg);
					new Thread(){
						@Override
						public void run() {
							JSONObject json = (JSONObject)response;
							if(json.has("figureurl")){
								Bitmap bitmap = null;
								try {
									//bitmap = Util.getbitmap(json.getString("figureurl_qq_2"));
									avatar_url = json.getString("figureurl_qq_2") ;
								} catch (JSONException e) {

								}
								Message msg = new Message();
								msg.obj = bitmap;

								msg.what = 1;
								mHandler.sendMessage(msg);
							}


						}






					}.start();
				}

				@Override
				public void onCancel() {

				}
			};
			mInfo = new UserInfo(this, mTencent.getQQToken());
			mInfo.getUserInfo(listener);

		} else {
			mUserInfo.setText("");
			mUserInfo.setVisibility(android.view.View.GONE);
			mUserLogo.setVisibility(android.view.View.GONE);
		}
	}

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				JSONObject response = (JSONObject) msg.obj;
				if (response.has("nickname")) {
					try {
						mUserInfo.setVisibility(android.view.View.VISIBLE);
						//mUserInfo.setText(response.getString("nickname"));
						user_name = response.getString("nickname");
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}else if(msg.what == 1){
				//imageLoader = ImageLoader.getInstance();

				//Bitmap bitmap = (Bitmap)msg.obj;
				imageLoader.displayImage(avatar_url, mUserLogo);
				//mUserLogo.setImageBitmap(bitmap);
				mUserLogo.setVisibility(android.view.View.VISIBLE);
			finish_all();
			}


		}





	};

	/**
	 * 获取到相关信息后 就跳转至主页面
	 */
 	public   void finish_all(){

		sharedPreferences =this.getApplicationContext().getSharedPreferences("user_state", MODE_PRIVATE);
		editor = sharedPreferences.edit();
		editor.putString("user_name", user_name);
		editor.putString("avatar_url", avatar_url);
		editor.commit();

		intent = new Intent();

	intent.setClass(this, MainActivity.class);

	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);

	}

	private void onClickLogin() {
		if (!mTencent.isSessionValid()) {
			mTencent.login(this, "all", loginListener);
			isServerSideLogin = false;

		} else {
			if (isServerSideLogin) { // Server-Side 模式的登陆, 先退出，再进行SSO登陆
				mTencent.logout(this);
				mTencent.login(this, "all", loginListener);
				isServerSideLogin = false;

				return;
			}
			mTencent.logout(this);
			updateUserInfo();
			updateLoginButton();
		}
	}

	private void onClickServerSideLogin() {
		if (!mTencent.isSessionValid()) {
			mTencent.loginServerSide(this, "all", loginListener);
			isServerSideLogin = true;

		} else {
			if (!isServerSideLogin) { // SSO模式的登陆，先退出，再进行Server-Side模式登陆
				mTencent.logout(this);
				mTencent.loginServerSide(this, "all", loginListener);
				isServerSideLogin = true;

				return;
			}
			mTencent.logout(this);
			isServerSideLogin = false;
			updateUserInfo();
			updateLoginButton();
		}
	}

	public static String getAppid() {

			mAppid = "222222";


		return mAppid;
	}

	public static boolean ready(Context context) {
		if (mTencent == null) {
			return false;
		}
		boolean ready = mTencent.isSessionValid()
				&& mTencent.getQQToken().getOpenId() != null;
		if (!ready) {
			Toast.makeText(context, "login and get openId first, please!",
					Toast.LENGTH_SHORT).show();
		}
		return ready;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		mTencent.onActivityResultData(requestCode,resultCode,data,loginListener);
		if(requestCode == Constants.REQUEST_API) {
			if(resultCode == Constants.RESULT_LOGIN) {
				Tencent.handleResultData(data, loginListener);
				Log.d(TAG, "-->onActivityResult handle logindata");
			}
		} else if (requestCode == Constants.REQUEST_APPBAR) { //app内应用吧登录
			if (resultCode == Constants.RESULT_LOGIN) {
				updateUserInfo();
				updateLoginButton();

			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public static void initOpenidAndToken(JSONObject jsonObject) {
		try {
			String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
			String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
			String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
			if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
					&& !TextUtils.isEmpty(openId)) {
				mTencent.setAccessToken(token, expires);
				mTencent.setOpenId(openId);
			}
		} catch(Exception e) {
		}
	}

	IUiListener loginListener = new BaseUiListener() {
		@Override
		protected void doComplete(JSONObject values) {

			initOpenidAndToken(values);
			updateUserInfo();
			updateLoginButton();

		}
	};

	private class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(Object response) {
			if (null == response) {
				Toast.makeText(getApplicationContext(), "登录失败",Toast.LENGTH_LONG).show();
				return;
			}
			JSONObject jsonResponse = (JSONObject) response;
			if (null != jsonResponse && jsonResponse.length() == 0) {
				Toast.makeText(getApplicationContext(), "登录失败",Toast.LENGTH_LONG).show();
				return;
			}
			Toast.makeText(getApplicationContext(), "成功",Toast.LENGTH_LONG).show();


			doComplete((JSONObject) response);

		}

		protected void doComplete(JSONObject values) {

		}

		@Override
		public void onError(UiError e) {

		}

		@Override
		public void onCancel() {

		}
	}



}
