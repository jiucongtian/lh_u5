package com.longhuapuxin.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.longhuapuxin.common.HttpRequest.HttpRequestListener;
import com.longhuapuxin.entity.ResponseCheckThirdPartAccount;
import com.longhuapuxin.entity.ResponseImportAccount;
import com.longhuapuxin.u5.BindPhoneNumberActivity;
import com.longhuapuxin.u5.MainActivity;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.u5.Settings;
import com.longhuapuxin.u5.U5Application;
import com.longhuapuxin.u5.wxapi.WXEntryActivity;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class LoginQQAPI {
	// 1104755881
	private static final String APPID = "1104865550";

	private Tencent mTencent;
	public IUiListener loginListener;
	private IUiListener userInfoListener;
	private String scope;
	private Context context;
	private UserInfo userInfo;
	private Activity activityFrom;
	private HashMap<String, String> info;
	private TextView loginErrorTips;

	public LoginQQAPI(Context context, TextView loginErrorTips,
			Activity activityFrom) {
		// this.activityFrom = activityFrom;
		// this.activityTo = activityTo;
		// final Context ctxContext = context.getApplicationContext();
		// mAppid = APP_ID;
		// mQQAuth = QQAuth.createInstance(mAppid, ctxContext);
		// mTencent = Tencent.createInstance(mAppid, activity);
		this.loginErrorTips = loginErrorTips;
		this.context = context;
		this.activityFrom = activityFrom;
		info = new HashMap<String, String>();

	}

	public void login() {
		initData();
		// 如果session无效，就开始登录
		// if (!mTencent.isSessionValid()) {
		mTencent.login(activityFrom, scope, loginListener);
		// }
	}

	private void httpRequestCheckThirdPartAccount() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("ImportedAccount", "QQ"));
		new HttpRequest(new HttpRequestListener() {

			@Override
			public void onHttpRequestOK(int tag, byte[] data) {
				try {
					String json = new String(data, "utf-8");
					ResponseCheckThirdPartAccount responseCheckThirdPartAccount = ((U5Application) context)
							.getGson().fromJson(json,
									ResponseCheckThirdPartAccount.class);
					if (responseCheckThirdPartAccount.isSuccess()) {
						Log.d("", "-----有此账号");
						// welcomeLayout.setVisibility(View.INVISIBLE);
						// activityFrom.setTheme(R.style.transParent);
						if (responseCheckThirdPartAccount.isHasPhone()) {
							Log.d("", "-----有此账号且绑定手机号");
							Settings.instance().setUserId(
									responseCheckThirdPartAccount.getUserId());
							Settings.instance().setToken(
									responseCheckThirdPartAccount.getToken());
							Settings.instance().save(activityFrom);
							Intent intent = new Intent();
							intent.setClass(context.getApplicationContext(),
									MainActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(intent);
						} else {
							Log.d("", "-----有此账号但没绑定手机号");
							Intent intent = new Intent();
							intent.putExtra("whichLogin", "QQ");
							intent.setClass(context.getApplicationContext(),
									BindPhoneNumberActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(intent);
						}
						WaitDialog.instance().hideWaitNote();
					} else {
						Log.d("", "-----没有此账号");
						httpRequestImportaccount();
						WaitDialog.instance().hideWaitNote();
					}
				} catch (Exception e) {
					e.printStackTrace();
					loginErrorTips.setText(e.getMessage().toString());
					WaitDialog.instance().hideWaitNote();
				}
			}

			@Override
			public void onHttpRequestFailed(int tag) {

			}
		}).post(0, Settings.instance().getApiUrl()
				+ "/auth/checkthirdpartaccount/QQ-" + info.get("openid"),
				params);

	}

	private void httpRequestImportaccount() {
		WaitDialog.instance().showWaitNote(activityFrom);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("SecurityCode", Settings.instance()
				.getSecurityCode()));
		params.add(new BasicNameValuePair("NickName", info.get("nickname")));
		params.add(new BasicNameValuePair("ImportedAccount", "QQ"));
		params.add(new BasicNameValuePair("LastName", ""));
		params.add(new BasicNameValuePair("FirstName", ""));
		params.add(new BasicNameValuePair("Gender", info.get("gender")));
		params.add(new BasicNameValuePair("Country", "中国"));
		params.add(new BasicNameValuePair("Email", ""));
		params.add(new BasicNameValuePair("City", info.get("city")));
		params.add(new BasicNameValuePair("Longitude", ""));
		params.add(new BasicNameValuePair("Latitude", ""));
		params.add(new BasicNameValuePair("Portrait", info.get("portrait")));
		new HttpRequest(new HttpRequestListener() {

			@Override
			public void onHttpRequestOK(int tag, byte[] data) {
				try {
					String json = new String(data, "utf-8");
					ResponseImportAccount responseImportAccount = ((U5Application) context)
							.getGson().fromJson(json,
									ResponseImportAccount.class);
					if (responseImportAccount.isSuccess()) {
						Settings.instance().setUserId(
								responseImportAccount.getUserId());
						Log.d("",
								"--------UserId"
										+ responseImportAccount.getUserId());
						Settings.instance().setToken(
								responseImportAccount.getToken());
						Intent intent = new Intent();
						intent.putExtra("whichLogin", "QQ");
						intent.setClass(context.getApplicationContext(),
								BindPhoneNumberActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(intent);
					} else {
						loginErrorTips.setText(responseImportAccount
								.getErrorMessage().toString());
						Log.d("",
								"------Importaccount"
										+ responseImportAccount
												.getErrorMessage());
						Log.d("",
								"------Importaccount"
										+ responseImportAccount.getErrorCode());
					}
					WaitDialog.instance().hideWaitNote();
				} catch (Exception e) {
					e.printStackTrace();
					loginErrorTips.setText(e.getMessage().toString());
					WaitDialog.instance().hideWaitNote();
				}
			}

			@Override
			public void onHttpRequestFailed(int tag) {

			}
		}).post(0, Settings.instance().getApiUrl() + "/auth/importaccount",
				params);
	}

	private void initData() {
		mTencent = Tencent.createInstance(APPID, context);
		// 要所有权限，不用再次申请增量权限，这里不要设置成get_user_info,add_t
		scope = "all";
		loginListener = new IUiListener() {
			@Override
			public void onError(UiError e) {
				loginErrorTips.setText(e.errorMessage.toString());
			}

			/**
			 * {"ret":0,"pay_token":"D3D678728DC580FBCDE15722B72E7365",
			 * "pf":"desktop_m_qq-10000144-android-2002-",
			 * "query_authority_cost":448, "authority_cost":-136792089,
			 * "openid":"015A22DED93BD15E0E6B0DDB3E59DE2D",
			 * "expires_in":7776000, "pfkey":"6068ea1c4a716d4141bca0ddb3df1bb9",
			 * "msg":"", "access_token":"A2455F491478233529D0106D2CE6EB45",
			 * "login_cost":499}
			 */
			@Override
			public void onComplete(Object value) {
				WaitDialog.instance().showWaitNote(activityFrom);
				System.out.println("有数据返回..");
				if (value == null) {
					return;
				}

				try {
					JSONObject jo = (JSONObject) value;

					int ret = jo.getInt("ret");

					System.out.println("json=" + String.valueOf(jo));
					if (ret == 0) {

						String openID = jo.getString("openid");
						Log.d("", "--------------openid:" + openID);
						info.put("openid", openID);
						String accessToken = jo.getString("access_token");
						String expires = jo.getString("expires_in");
						mTencent.setOpenId(openID);
						mTencent.setAccessToken(accessToken, expires);
						userInfo = new UserInfo(context, mTencent.getQQToken());
						userInfo.getUserInfo(userInfoListener);
						WaitDialog.instance().hideWaitNote();
					}

				} catch (Exception e) {
					loginErrorTips.setText(e.getMessage().toString());
					WaitDialog.instance().hideWaitNote();
				}

			}

			@Override
			public void onCancel() {

			}
		};

		userInfoListener = new IUiListener() {

			@Override
			public void onError(UiError e) {
				loginErrorTips.setText(e.errorMessage.toString());
			}

			/**
			 * {"is_yellow_year_vip":"0","ret":0, "figureurl_qq_1":
			 * "http:\/\/q.qlogo.cn\/qqapp\/1104732758\/015A22DED93BD15E0E6B0DDB
			 * 3 E 5 9 D E 2 D \ / 4 0 " , "figureurl_qq_2":
			 * "http:\/\/q.qlogo.cn\/qqapp\/1104732758\/015A22DED93BD15E0E6B0DDB
			 * 3 E 5 9 D E 2 D \ / 1 0 0 " ,
			 * "nickname":"攀爬←蜗牛","yellow_vip_level":"0","is_lost":0,"msg":"",
			 * "city":"黄冈","
			 * figureurl_1":"http:\/\/qzapp.qlogo.cn\/qzapp\/1104732758
			 * \/015A22DED93BD15E0E6B0DDB3E59DE2D\/50", "vip":"0","level":"0",
			 * "figureurl_2":
			 * "http:\/\/qzapp.qlogo.cn\/qzapp\/1104732758\/015A22DED93BD15E0E6B
			 * 0 D D B 3 E 5 9 D E 2 D \ / 1 0 0
			 * " , "province":"湖北", "is_yellow_vip":"0","gender":"男",
			 * "figureurl":
			 * "http:\/\/qzapp.qlogo.cn\/qzapp\/1104732758\/015A22DED93BD15E0E6B
			 * 0 D D B 3 E 5 9 D E 2 D \ / 3 0 " }
			 */
			@Override
			public void onComplete(Object arg0) {
				WaitDialog.instance().showWaitNote(activityFrom);
				if (arg0 == null) {
					return;
				}
				try {
					JSONObject jo = (JSONObject) arg0;
					int ret = jo.getInt("ret");
					if (ret == 0) {
						String gender = jo.getString("gender");
						if (gender.equals("男")) {
							gender = "1";
						} else if (gender.equals("女")) {
							gender = "2";
						} else {
							gender = "0";
						}
						Log.d("", "--------------gender:" + gender);
						info.put("gender", gender);
						info.put("city", jo.getString("city"));
						info.put("portrait", jo.getString("figureurl_qq_2"));
						Settings.instance().setInfo(info);
						// httpRequestImportaccount();
						httpRequestCheckThirdPartAccount();
						WaitDialog.instance().hideWaitNote();

					}
				} catch (Exception e) {
					loginErrorTips.setText(e.getMessage().toString());
					WaitDialog.instance().hideWaitNote();
				}

			}

			@Override
			public void onCancel() {

			}
		};
	}
}
