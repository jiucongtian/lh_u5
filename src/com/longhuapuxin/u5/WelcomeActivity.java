package com.longhuapuxin.u5;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.longhuapuxin.common.HttpRequest;
import com.longhuapuxin.common.HttpRequest.HttpRequestListener;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.LoginQQAPI;
import com.longhuapuxin.common.LoginWXAPI;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.entity.ResponseGetAccount;
import com.longhuapuxin.entity.ResponseLoginByPhone;
import com.longhuapuxin.entity.ResponseSMS;
import com.longhuapuxin.u5.upgrade.UpgradeManager;
import com.squareup.okhttp.Request;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.Tencent;

public class WelcomeActivity extends Activity implements OnClickListener {
	private ImageView loginOtherQQ, loginOtherWX;
	private ImageView login;
	private EditText phoneNumber, mVerifyEditor;
	private TextView getVerification, canGetVerification, countVerification;
	private TextView attention;
	private LoginQQAPI loginQQ;
	private UpgradeManager upgradeManager;
	private String myLastPhoneNum = "0";
	private Timer timer = new Timer();
	private TimerTask task;
	private boolean NeedReturn = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);

		U5Application u5app = (U5Application) getApplication();
		if (!u5app.ismCheckedUpgrade()) {
			u5app.setmCheckedUpgrade(true);
			upgradeManager = new UpgradeManager(this);
			upgradeManager.CheckAndRefreshApp();
		}

		init();
		initPhoneNumber();
		((U5Application) getApplication()).addActivity(this);
		Intent intent = this.getIntent();
		NeedReturn = intent.getBooleanExtra("NeedReturn", false);
	}

	private void initPhoneNumber() {
		phoneNumber = (EditText) findViewById(R.id.phone_number);
		phoneNumber.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

				if (s.length() == 11) {
					if (!myLastPhoneNum
							.equals(phoneNumber.getText().toString())) {
						canGetVerification.setVisibility(View.VISIBLE);
						stopCount();
						countVerification.setVisibility(View.GONE);
					}
					if (countSecond != 60) {
						canGetVerification.setVisibility(View.GONE);
					} else {
						canGetVerification.setVisibility(View.VISIBLE);
					}
					// 提示获取验证码
					getVerification.setVisibility(View.GONE);
				} else {

					if (countSecond != 60) {
						getVerification.setVisibility(View.GONE);
					} else {
						getVerification.setVisibility(View.VISIBLE);
					}
					canGetVerification.setVisibility(View.GONE);
				}
			}
		});
	}

	private void init() {
		attention = (TextView) findViewById(R.id.attention);
		loginOtherWX = (ImageView) findViewById(R.id.login_ohter_wx);
		loginOtherWX.setOnClickListener(this);
		loginOtherQQ = (ImageView) findViewById(R.id.login_other_qq);
		loginOtherQQ.setOnClickListener(this);
		login = (ImageView) findViewById(R.id.login_bt);
		login.setOnClickListener(this);
		getVerification = (TextView) findViewById(R.id.get_verification_code);
		getVerification.setOnClickListener(this);
		mVerifyEditor = (EditText) findViewById(R.id.verification_code);
		canGetVerification = (TextView) findViewById(R.id.can_get_verification_code);
		canGetVerification.setOnClickListener(this);
		countVerification = (TextView) findViewById(R.id.count_verification_code);
		TextView txtAgreement = (TextView) findViewById(R.id.txtAgreement);
		String text = "用户协议";

		// 将text进行拆分
		SpannableString ss1 = new SpannableString(text);

		ss1.setSpan(new ClickableSpan() {
			@Override
			public void onClick(View widget) {
				Intent intent = new Intent(WelcomeActivity.this,
						AgreementActivity.class);
				startActivity(intent);
			}
		}, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		txtAgreement.setText("我已经阅读并同意");
		txtAgreement.append(ss1);

		txtAgreement.setMovementMethod(LinkMovementMethod.getInstance());

		// **********************test mode*************************************
		if (Logger.DEBUG) {
			ImageView view = (ImageView) findViewById(R.id.login_other_title);
			view.setOnLongClickListener(new View.OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {

					Intent intent = new Intent(WelcomeActivity.this,
							SetServiceUrlActivity.class);
					startActivity(intent);

					return false;
				}
			});
		}
		// **********************test mode*************************************

	}

	@Override
	public void onClick(View v) {
		if (v == loginOtherQQ) {
			// new LoginOtherQQ(getApplicationContext(), WelcomeActivity.this)
			// .login();
			// setTheme(R.style.translucent);
			WaitDialog.instance().showWaitNote(this);
			loginQQ = new LoginQQAPI(getApplicationContext(), attention,
					WelcomeActivity.this);
			WaitDialog.instance().hideWaitNote();
			loginQQ.login();
		} else if (v == login) {
			// Intent intent = new Intent(WelcomeActivity.this,
			// MainActivity.class);
			// startActivity(intent);
			CheckBox chkAgreement = (CheckBox) findViewById(R.id.chkAgreement);
			if (!chkAgreement.isChecked()) {
				Toast.makeText(WelcomeActivity.this, "请先阅读并同意用户协议",
						Toast.LENGTH_LONG).show();
				return;
			}
			httpRequestLogin();

		} else if (v == getVerification) {
			Toast.makeText(this, R.string.prompt_verify, Toast.LENGTH_LONG)
					.show();
		} else if (v == canGetVerification) {
			myLastPhoneNum = phoneNumber.getText().toString();
			hideSoftInputView();
			httpRequestVertification();
			canGetVerification.setVisibility(View.GONE);
			countVerification.setVisibility(View.VISIBLE);
			startCount();
		} else if (v == loginOtherWX) {
			new LoginWXAPI(getApplicationContext(), attention)
					.sendRequestToWx();
		}
	}

	private void httpRequestVertification() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("PhoneNumber", phoneNumber.getText()
				.toString()));
		params.add(new BasicNameValuePair("SecurityType", "ZC"));
		new HttpRequest(new HttpRequestListener() {

			@Override
			public void onHttpRequestOK(int tag, byte[] data) {
				try {
					String json = new String(data, "utf-8");
					Logger.info("sendsms result is: " + json);
					ResponseSMS responceSMS = ((U5Application) getApplication())
							.getGson().fromJson(json, ResponseSMS.class);
					if (responceSMS.isSuccess()) {
						// getVerification.setText(responceSMS
						// .getSecurityCode());
						Settings.instance().setSecurityCode(
								responceSMS.getSecurityCode());
						Log.d("",
								"------securitycode"
										+ responceSMS.getSecurityCode());
					} else {
						Log.d("",
								"------securitycodeFail"
										+ responceSMS.getErrorMessage());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onHttpRequestFailed(int tag) {
				Log.d("", "------" + "faile");
			}
		}).post(0, Settings.instance().getApiUrl() + "/auth/sendsms", params);
	}

	private void httpRequestGetAccount(final String userId, final String token) {
		Param[] params = new Param[3];
		params[0] = new Param("UserId", userId);
		params[1] = new Param("Token", token);
		params[2] = new Param("TargetUserId", userId);

		OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
				+ "/auth/getaccount", params,
				new OkHttpClientManager.ResultCallback<String>() {
					@Override
					public void onError(Request request, Exception e) {
						Toast.makeText(WelcomeActivity.this, e.getMessage(),
								Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}

					@Override
					public void onResponse(String u) {
						Logger.info("getaccount result is: " + u);
						try {

							ResponseGetAccount responseGetAccount = ((U5Application) getApplication())
									.getGson().fromJson(u,
											ResponseGetAccount.class);
							// Log.d("", "--------" + responseLoginByPhone);
							if (responseGetAccount.isSuccess()) {
								Settings.instance().setMyName(
										responseGetAccount.getUser()
												.getNickName());
								Settings.instance().User = responseGetAccount
										.getUser();
								Settings.instance().setManualyWithDraw(responseGetAccount.isManualyWithDraw());
								Settings.instance().setUserId(userId);
								Settings.instance().setToken(token);
								Settings.instance().save(WelcomeActivity.this);
								Log.d("", "------GetAccount success");
								if (!NeedReturn) {
									Intent intent = new Intent(
											WelcomeActivity.this,
											MainActivity.class);
									startActivity(intent);
								} else {
									finish();

								}

							} else {

								Settings.instance().User = null;
								Settings.instance().setUserId("");
								Settings.instance().setToken("");
								Settings.instance().save(WelcomeActivity.this);
								Log.d("", "------GetAccount error"
										+ responseGetAccount.getErrorMessage());
								Log.d("", "------GetAccount error"
										+ responseGetAccount.getErrorCode());
							}
						} catch (Exception ex) {
							Toast.makeText(WelcomeActivity.this,
									ex.getMessage(), Toast.LENGTH_LONG).show();
							Log.d("AA", ex.getMessage());
						}
					}
				});
		attention.setText("验证码错误");

	}

	private void httpRequestLogin() {
		Param[] params = new Param[5];
		params[0] = new Param("PhoneNumber", phoneNumber.getText().toString());
		params[1] = new Param("SecurityCode", mVerifyEditor.getText()
				.toString());
		Settings setting = Settings.instance();
		if (setting.City == null || setting.City.length() <= 0) {
			params[2] = new Param("City", "");
			params[3] = new Param("Longitude", "");
			params[4] = new Param("Latitude", "");
		} else {
			params[2] = new Param("BDCityCode", setting.CityCode);
			params[3] = new Param("Longitude",
					String.valueOf(setting.Lontitude));
			params[4] = new Param("Latitude", String.valueOf(setting.Latitude));
		}
		OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
				+ "/auth/loginbyphone", params,
				new OkHttpClientManager.ResultCallback<String>() {
					@Override
					public void onError(Request request, Exception e) {
						Toast.makeText(WelcomeActivity.this, e.getMessage(),
								Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}

					@Override
					public void onResponse(String u) {
						Logger.info("loginbyphone result is: " + u);
						try {
							ResponseLoginByPhone responseLoginByPhone = ((U5Application) getApplication())
									.getGson().fromJson(u,
											ResponseLoginByPhone.class);

							// Log.d("", "--------" + responseLoginByPhone);
							if (responseLoginByPhone.isSuccess()) {
								String userId = responseLoginByPhone
										.getUserId();
								String token = responseLoginByPhone.getToken();
								httpRequestGetAccount(userId, token);
							} else {
								Toast.makeText(WelcomeActivity.this,
										responseLoginByPhone.getErrorMessage(),
										Toast.LENGTH_LONG).show();
							}

						} catch (Exception ex) {
							Toast.makeText(WelcomeActivity.this,
									ex.getMessage(), Toast.LENGTH_LONG).show();
							Log.d("AA", ex.getMessage());
						}
					}
				});

	}

	/**
	 * 隐藏软键盘 hideSoftInputView
	 * 
	 * @Title: hideSoftInputView
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	public void hideSoftInputView() {
		InputMethodManager manager = ((InputMethodManager) this
				.getSystemService(Activity.INPUT_METHOD_SERVICE));
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	// 显示软键盘
	public void showSoftInputView(View view) {
		if (getCurrentFocus() != null)
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
					.showSoftInput(view, 0);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Bundle bundle = null;
		if (data != null) {
			bundle = data.getExtras();
			Set<String> keySet = bundle.keySet();
			for (String key : keySet) {
				Object value = bundle.get(key);
				Log.d("", "----------key" + key);
				Log.d("", "----------value" + value);
			}
		}
		if (requestCode == Constants.REQUEST_API) {
			if (resultCode == Constants.RESULT_LOGIN) {
				if (bundle != null && bundle.containsKey("key_error_msg")
						&& bundle.getString("key_error_msg") == null
						&& bundle.getInt("key_error_code") == 0) {
					attention.setVisibility(View.VISIBLE);
					attention.setText("内存紧张");
				} else {
					Tencent.handleResultData(data, loginQQ.loginListener);
				}
			}
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void onBackPressed() {
		((U5Application) getApplication()).exit();
	}

	private int countSecond = 60;

	private void startCount() {
		if (task == null) {
			task = new TimerTask() {
				@Override
				public void run() {
					// countVerification.setVisibility(View.GONE);
					// canGetVerification.setVisibility(View.VISIBLE);
					countSecond--;
					if (countSecond > 0) {
						runOnUiThread(new Runnable() {
							public void run() {
								countVerification.setText("验证码" + "("
										+ countSecond + ")");
							}
						});
					} else {
						runOnUiThread(new Runnable() {
							public void run() {
								countVerification.setVisibility(View.GONE);
								canGetVerification.setVisibility(View.VISIBLE);
								stopCount();
							}
						});
					}
				}
			};
			timer.schedule(task, 0, 1000);
		}
	}

	private void stopCount() {
		if (task != null) {
			task.cancel();
			task = null;
			countSecond = 60;
		}
	}

}
