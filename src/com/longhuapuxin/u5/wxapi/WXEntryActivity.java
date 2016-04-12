package com.longhuapuxin.u5.wxapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.longhuapuxin.common.HttpRequest;
import com.longhuapuxin.common.HttpRequest.HttpRequestListener;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.entity.ResponseCheckThirdPartAccount;
import com.longhuapuxin.entity.ResponseImportAccount;
import com.longhuapuxin.u5.BindPhoneNumberActivity;
import com.longhuapuxin.u5.MainActivity;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.u5.Settings;
import com.longhuapuxin.u5.U5Application;
import com.longhuapuxin.u5.WelcomeActivity;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	private HashMap<String, String> info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String isClose = getIntent().getStringExtra("_wxapi_sendauth_resp_url");
		// Bundle bundle = intent.getExtras();
		// Set<String> keySet = bundle.keySet();
		// for (String key : keySet) {
		// Object value = bundle.get(key);
		// Log.d("", "----------key" + key);
		// Log.d("", "----------value" + value);
		//
		// }
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (TextUtils.isEmpty(isClose)) {
			finish();
		} else {
			WaitDialog.instance().showWaitNote(this);
			handleIntent(getIntent());
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		handleIntent(intent);
	}

	@Override
	public void onReq(BaseReq arg0) {

	}

	@Override
	public void onResp(BaseResp arg0) {

	}

	private void handleIntent(Intent intent) {
		SendAuth.Resp resp = new SendAuth.Resp(intent.getExtras());
		if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
			// 用户同意
			getTokenAndOpenId(resp.code);

		}
	}

	private void getUserInfo(String openID, String accessToken) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("access_token", accessToken));
		params.add(new BasicNameValuePair("openid", openID));
		new HttpRequest(new HttpRequestListener() {

			@Override
			public void onHttpRequestOK(int tag, byte[] data) {
				try {
					String response = new String(data, "utf-8");
					JSONObject json = new JSONObject(response);
					String nickName = json.getString("nickname");
					String gender = json.getString("sex");
					if (gender.equals("男")) {
						gender = "1";
					} else if (gender.equals("女")) {
						gender = "2";
					} else {
						gender = "0";
					}
					info = new HashMap<String, String>();
					info.put("openid", json.getString("openid"));
					info.put("nickname", json.getString("nickname"));
					info.put("gender", gender);
					info.put("city", json.getString("city"));
					info.put("portrait", json.getString("headimgurl"));
					Settings.instance().setInfo(info);
					httpRequestCheckThirdPartAccount();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onHttpRequestFailed(int tag) {
			}
		}).post(0, "https://api.weixin.qq.com/sns/userinfo", params);
	}

	private void getTokenAndOpenId(String code) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("appid", "wx93b8e5d6f4efcbe0"));
		params.add(new BasicNameValuePair("secret",
				"9c091acecd67083a7c0e8e2df7cda515"));
		params.add(new BasicNameValuePair("code", code));
		params.add(new BasicNameValuePair("grant_type", "authorization_code"));
		new HttpRequest(new HttpRequestListener() {

			@Override
			public void onHttpRequestOK(int tag, byte[] data) {
				try {
					String response = new String(data, "utf-8");
					JSONObject json = new JSONObject(response);
					String openID = json.getString("openid");
					String accessToken = json.getString("access_token");
					getUserInfo(openID, accessToken);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onHttpRequestFailed(int tag) {
			}
		}).post(0, "https://api.weixin.qq.com/sns/oauth2/access_token", params);
	}

	private void httpRequestCheckThirdPartAccount() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("ImportedAccount", "WX-" + info.get("openid")));
		new HttpRequest(new HttpRequestListener() {

			@Override
			public void onHttpRequestOK(int tag, byte[] data) {
				try {
					String json = new String(data, "utf-8");
					ResponseCheckThirdPartAccount responseCheckThirdPartAccount = ((U5Application) getApplicationContext())
							.getGson().fromJson(json,
									ResponseCheckThirdPartAccount.class);
					if (responseCheckThirdPartAccount.isSuccess()) {
						Log.d("", "-----有此账号");

						if (responseCheckThirdPartAccount.isHasPhone()) {
							Log.d("", "-----有此账号且绑定手机号");
							Settings.instance().setUserId(
									responseCheckThirdPartAccount.getUserId());
							Settings.instance().setToken(
									responseCheckThirdPartAccount.getToken());
							Settings.instance().save(WXEntryActivity.this);
							Intent intent = new Intent();
							intent.setClass(WXEntryActivity.this,
									MainActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
						} else {
							Log.d("", "-----有此账号但没绑定手机号");
							Intent intent = new Intent();
							intent.putExtra("whichLogin", "微信");
							intent.setClass(WXEntryActivity.this,
									BindPhoneNumberActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
						}
						WaitDialog.instance().hideWaitNote();
					} else {
						Log.d("", "-----没有此账号");
						httpRequestImportaccount();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onHttpRequestFailed(int tag) {

			}
		}).post(0, Settings.instance().getApiUrl()
				+ "/auth/checkthirdpartaccount/",
				params);

	}

	private void httpRequestImportaccount() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("SecurityCode", Settings.instance()
				.getSecurityCode()));
		params.add(new BasicNameValuePair("NickName", info.get("nickname")));
		params.add(new BasicNameValuePair("ImportedAccount", "WX" + info.get("openid")));
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
					ResponseImportAccount responseImportAccount = ((U5Application) getApplication())
							.getGson().fromJson(json,
									ResponseImportAccount.class);
					// Log.d("", "--------" + responseLoginByPhone);
					if (responseImportAccount.isSuccess()) {
						// WelcomeActivity.this.findViewById(R.id.welcome_activity);
						Intent intent = new Intent();
						intent.putExtra("whichLogin", "微信");
						intent.setClass(WXEntryActivity.this,
								BindPhoneNumberActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
						WaitDialog.instance().hideWaitNote();
					} else {
						WaitDialog.instance().hideWaitNote();
						Log.d("",
								"------"
										+ responseImportAccount
												.getErrorMessage());
						Log.d("",
								"------" + responseImportAccount.getErrorCode());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onHttpRequestFailed(int tag) {
				WaitDialog.instance().hideWaitNote();
			}
		}).post(0, Settings.instance().getApiUrl() + "/auth/importaccount",
				params);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		WaitDialog.instance().hideWaitNote();
	}

	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
		WaitDialog.instance().hideWaitNote();
	}

}