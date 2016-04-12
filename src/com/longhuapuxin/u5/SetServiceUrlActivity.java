package com.longhuapuxin.u5;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SetServiceUrlActivity extends Activity implements OnClickListener {
	EditText mServiceUrl;
	EditText mServiceImgUrl;
	EditText mMqttUrl;
	EditText mToken;
	Button mCommit;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_service_url);
		
		mServiceUrl = (EditText) findViewById(R.id.serviceTxt);
		mServiceImgUrl = (EditText) findViewById(R.id.imageServiceTxt);
		mMqttUrl = (EditText) findViewById(R.id.mqtt);
		mToken = (EditText) findViewById(R.id.token);
		mCommit = (Button) findViewById(R.id.commit);
		mCommit.setOnClickListener(this);
		
		String serviceUrl = Settings.instance().getApiUrl();
		String imageUrl = Settings.instance().getImageUrl();
		String mqtt = Settings.instance().getMqttUrl();
		String token = Settings.instance().getToken();
		
		mServiceUrl.setText(serviceUrl);
		mServiceImgUrl.setText(imageUrl);
		mMqttUrl.setText(mqtt);
		mToken.setText(token);
		
		Button btn = (Button) findViewById(R.id.clear);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Settings.instance().setUserId("");
				Settings.instance().setToken("");
				Settings.instance().save(SetServiceUrlActivity.this);
				SetServiceUrlActivity.this.finish();
			}
		});
		
		
	}
	@Override
	public void onClick(View v) { 
		String serviceUrl = mServiceUrl.getText().toString();
		String imageUrl = mServiceImgUrl.getText().toString();
		String mqtt = mMqttUrl.getText().toString();
		Settings.instance().setApiUrl(serviceUrl);
		Settings.instance().setImageUrl(imageUrl);
		Settings.instance().setImageUrl(mqtt);
		this.finish();
	}
}
