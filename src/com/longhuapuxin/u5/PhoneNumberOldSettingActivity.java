package com.longhuapuxin.u5;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

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

import com.longhuapuxin.common.HttpRequest;
import com.longhuapuxin.common.HttpRequest.HttpRequestListener;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.entity.ResponseSMS;
import com.squareup.okhttp.Request;

public class PhoneNumberOldSettingActivity extends BaseActivity implements
		OnClickListener {
	private String myLastPhoneNum = "0";
	private EditText oldPhoneNumber;
	private TimerTask task;
	private Timer timer = new Timer();
	private LinearLayout getVerification, canGetVerification,
			countVerification;
	private EditText verificationCode;
	private TextView countVerificationText;
	private TextView error;
	private Button nextStep;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_number_old_setting);
		((U5Application) getApplication()).addActivity(this);
		initHeader("手机号码");
		initViews();
		initPhoneNumber();
	}

	private void initViews() {
		nextStep = (Button) findViewById(R.id.next_step);
		nextStep.setOnClickListener(this);
		error = (TextView) findViewById(R.id.error_tx);
		countVerificationText = (TextView) findViewById(R.id.count_verification_code_text);
		getVerification = (LinearLayout) findViewById(R.id.get_verification_code);
		verificationCode = (EditText) findViewById(R.id.verification_code);
		canGetVerification = (LinearLayout) findViewById(R.id.can_get_verification_code);
		canGetVerification.setOnClickListener(this);
		countVerification = (LinearLayout) findViewById(R.id.count_verification_code);
	}

	private void initPhoneNumber() {
		oldPhoneNumber = (EditText) findViewById(R.id.old_phone_number);
		oldPhoneNumber.addTextChangedListener(new TextWatcher() {

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
					if (!myLastPhoneNum.equals(oldPhoneNumber.getText()
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
			myLastPhoneNum = oldPhoneNumber.getText().toString();
			httpRequestVertification();
			canGetVerification.setVisibility(View.GONE);
			countVerification.setVisibility(View.VISIBLE);
			startCount();
		} else if (v == nextStep) {
			error.setText("");
			
			if (Settings.instance().getSecurityCode() != null && 
					Settings.instance().getSecurityCode()
					.equals(verificationCode.getText().toString())) {
				Intent intent = new Intent(this,
						PhoneNumberNewSettingActivity.class);
				intent.putExtra("oldPhone", oldPhoneNumber.getText().toString());
				intent.putExtra("oldverificationCode", verificationCode
						.getText().toString());
				startActivity(intent);
			} else {
				error.setText("验证码错误");
			}
		}
	}

	private void httpRequestVertification() {
		Param[] params = new Param[2];
		params[0] = new Param("PhoneNumber", oldPhoneNumber.getText()
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
//									"----------oldPhoneCode"
//											+ response.getSecurityCode());
							Logger.info("----------oldPhoneCode"
									+ response.getSecurityCode());
						} else {
							error.setText("" + response.getErrorMessage());
						}
					}

				});
	}

}
