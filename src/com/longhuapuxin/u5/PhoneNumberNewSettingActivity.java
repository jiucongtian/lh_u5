package com.longhuapuxin.u5;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.entity.ResponseDad;
import com.longhuapuxin.entity.ResponseSMS;
import com.squareup.okhttp.Request;

public class PhoneNumberNewSettingActivity extends BaseActivity implements
		OnClickListener {
	private String myLastPhoneNum = "0";
	private EditText newPhoneNumber;
	private TimerTask task;
	private Timer timer = new Timer();
	private LinearLayout getVerification, canGetVerification,
			countVerification;
	private EditText verificationCode;
	private TextView countVerificationText;
	private TextView error;
	private Button ok;
	private String oldPhoneNumber;
	private String oldVerificationCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_number_new_setting);
		((U5Application) getApplication()).addActivity(this);
		initHeader("手机号码");
		initViews();
		initPhoneNumber();
	}

	private void initViews() {
		oldPhoneNumber = getIntent().getStringExtra("oldPhone");
		oldVerificationCode = getIntent().getStringExtra("oldverificationCode");
		ok = (Button) findViewById(R.id.ok);
		ok.setOnClickListener(this);
		error = (TextView) findViewById(R.id.error_tx);
		countVerificationText = (TextView) findViewById(R.id.count_verification_code_text);
		getVerification = (LinearLayout) findViewById(R.id.get_verification_code);
		verificationCode = (EditText) findViewById(R.id.verification_code);
		canGetVerification = (LinearLayout) findViewById(R.id.can_get_verification_code);
		canGetVerification.setOnClickListener(this);
		countVerification = (LinearLayout) findViewById(R.id.count_verification_code);
	}

	private void initPhoneNumber() {
		newPhoneNumber = (EditText) findViewById(R.id.new_phone_number);
		newPhoneNumber.addTextChangedListener(new TextWatcher() {

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
					if (!myLastPhoneNum.equals(newPhoneNumber.getText()
							.toString())) {
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

	private int countSecond = 60;

	private void startCount() {
		if (task == null) {
			task = new TimerTask() {
				@Override
				public void run() {
					countSecond--;
					if (countSecond > 0) {
						runOnUiThread(new Runnable() {
							public void run() {
								countVerificationText.setText("(" + countSecond
										+ ")");
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

	@Override
	public void onClick(View v) {
		if (v == canGetVerification) {
			error.setText("");
			myLastPhoneNum = newPhoneNumber.getText().toString();
			httpRequestVertification();
			canGetVerification.setVisibility(View.GONE);
			countVerification.setVisibility(View.VISIBLE);
			startCount();
		} else if (v == ok) {
			error.setText("");
			if (verificationCode.getText().toString()
					.equals(Settings.instance().getSecurityCode())) {
				WaitDialog.instance().showWaitNote(this);
				httpRequestSetPhone();
			} else {
				error.setText("验证码错误");
			}
		}
	}

	private void httpRequestVertification() {
		Param[] params = new Param[2];
		params[0] = new Param("PhoneNumber", newPhoneNumber.getText()
				.toString());
		params[1] = new Param("SecurityType", "ZC");

		OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
				+ "/auth/sendsms", params,
				new OkHttpClientManager.ResultCallback<ResponseSMS>() {

					@Override
					public void onError(Request request, Exception e) {
						error.setText("" + e.getMessage());
					}

					@Override
					public void onResponse(ResponseSMS response) {

						if (response.isSuccess()) {
							Settings.instance().setSecurityCode(
									response.getSecurityCode());
//							Log.d("",
//									"----------newPhoneCode"
//											+ response.getSecurityCode());
							Logger.info("----------newPhoneCode"
									+ response.getSecurityCode());
						} else {
							error.setText("" + response.getErrorMessage());
						}
					}

				});
	}

	private void httpRequestSetPhone() {
		Param[] params = new Param[6];
		params[0] = new Param("Token", Settings.instance().getToken());
		params[1] = new Param("UserId", Settings.instance().getUserId());
		params[2] = new Param("OldPhone", oldPhoneNumber);
		params[3] = new Param("OldSecurityCode", oldVerificationCode);
		params[4] = new Param("NewPhone", newPhoneNumber.getText().toString());
		params[5] = new Param("NewSecurityCode", verificationCode.getText()
				.toString());

		OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
				+ "/auth/SetPhone", params,
				new OkHttpClientManager.ResultCallback<ResponseDad>() {

					@Override
					public void onError(Request request, Exception e) {
						WaitDialog.instance().hideWaitNote();
						error.setText("" + e.getMessage());
					}

					@Override
					public void onResponse(ResponseDad response) {

						if (response.isSuccess()) {
							WaitDialog.instance().hideWaitNote();
							Intent intent = new Intent(
									PhoneNumberNewSettingActivity.this,
									MainActivity.class);
							startActivity(intent);
						} else {
							WaitDialog.instance().hideWaitNote();
							error.setText("" + response.getErrorMessage());
						}
					}

				});
	}

	@Override
	public void onBackPressed() {
		((U5Application) getApplication()).exit();
	}
}
