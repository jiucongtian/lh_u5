package com.longhuapuxin.u5;

import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.entity.ResponseDad;
import com.longhuapuxin.entity.ResponseGetAccount;
import com.squareup.okhttp.Request;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LoginPasswordSettingActivity extends BaseActivity {
	private LinearLayout havePassword, noPassword;
	private EditText oldPassword, havePasswordnNewPassword,
			havePasswordNewpasswordconfirm, noPasswordnNewPassword,
			noPasswordNewpasswordconfirm;
	private Button ok;
	private TextView error;
	private String oldPasswordEdit = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_password_setting);
		initHeader("登录密码");
		initViews();
	}

	private void initViews() {
		havePassword = (LinearLayout) findViewById(R.id.have_password);
		noPassword = (LinearLayout) findViewById(R.id.no_password);
		if (TextUtils.isEmpty(Settings.instance().User.getPassword())) {
			havePassword.setVisibility(View.GONE);
			noPassword.setVisibility(View.VISIBLE);
		} else {
			havePassword.setVisibility(View.VISIBLE);
			noPassword.setVisibility(View.GONE);
		}
		oldPassword = (EditText) findViewById(R.id.have_old_account);
		havePasswordnNewPassword = (EditText) findViewById(R.id.have_new_account);
		havePasswordNewpasswordconfirm = (EditText) findViewById(R.id.have_new_account_confirm);
		noPasswordnNewPassword = (EditText) findViewById(R.id.no_new_account);
		noPasswordNewpasswordconfirm = (EditText) findViewById(R.id.no_new_account_confirm);
		ok = (Button) findViewById(R.id.ok);
		error = (TextView) findViewById(R.id.error_tx);
	}

	private void httpRequestSetpassword() {
		if (TextUtils.isEmpty(Settings.instance().User.getPassword())) {
			// 第一次设置密码
			Param[] params = new Param[4];
			params[0] = new Param("UserId", Settings.instance().getUserId());
			params[1] = new Param("Token", Settings.instance().getToken());
			params[2] = new Param("OldPassword", oldPasswordEdit);
			params[3] = new Param("Passsword", noPasswordnNewPassword.getText()
					.toString());

			OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
					+ "/pay/setpassword", params,
					new OkHttpClientManager.ResultCallback<ResponseDad>() {
						@Override
						public void onError(Request request, Exception e) {
							e.printStackTrace();
						}

						@Override
						public void onResponse(ResponseDad response) {
						}
					});
		} else {

			Param[] params = new Param[4];
			params[0] = new Param("UserId", Settings.instance().getUserId());
			params[1] = new Param("Token", Settings.instance().getToken());
			params[2] = new Param("OldPassword", oldPassword.getText()
					.toString());
			params[3] = new Param("Passsword", havePasswordnNewPassword
					.getText().toString());

			OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
					+ "/pay/setpassword", params,
					new OkHttpClientManager.ResultCallback<ResponseDad>() {
						@Override
						public void onError(Request request, Exception e) {
							e.printStackTrace();
						}

						@Override
						public void onResponse(ResponseDad response) {
						}
					});
		}
	}
}
