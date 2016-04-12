package com.longhuapuxin.u5;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

public class U5DialogActivity extends Activity implements OnClickListener {

	View mBtnExit, mBtnGo2NetworkSetting;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_no_network);
		init();
		initView();
		
		NetworkStatusReceiver networkReceiver = new NetworkStatusReceiver();
		IntentFilter networkFilter = new IntentFilter();
		networkFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(networkReceiver, networkFilter);
	}

	private void initView() {
		mBtnExit.setOnClickListener(this);
		mBtnGo2NetworkSetting.setOnClickListener(this);
	}

	private void init() {
		mBtnGo2NetworkSetting = findViewById(R.id.lv_go_2_setting_network);
		mBtnExit = findViewById(R.id.lv_exit);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		U5Application app = (U5Application) getApplication();
		if(checkNetwork()) {
			finish();
		}
	}
	
	private boolean checkNetwork() {
		boolean result = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null) {
			NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

			if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
				result =  false;
			} else {// connect network
				result = true;
			}
		}
		return result;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		
		switch(id) {
		case R.id.lv_go_2_setting_network:
			Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");   
			startActivity(wifiSettingsIntent);
			break;
		case R.id.lv_exit:
			finish();
			((U5Application)getApplication()).exit();
			break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}
	
	private class NetworkStatusReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			if(checkNetwork()) {
				finish();
			}
		}
	}
}
