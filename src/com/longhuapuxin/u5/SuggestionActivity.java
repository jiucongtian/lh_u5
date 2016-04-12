package com.longhuapuxin.u5;

import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.entity.ResponseDad;
import com.squareup.okhttp.Request;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class SuggestionActivity extends BaseActivity {
	private TextView txtContent,txtPhone;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_suggestion);
		init();
		 
	}
	private void init(){
		initHeader(R.string.profile_help);
		txtContent=(TextView)findViewById(R.id.txtContent);
		txtPhone=(TextView)findViewById(R.id.txtPhone);
		String phone=Settings.instance().User.getPhone();
		if(phone!=null && phone.length()>0){
		txtPhone.setText(phone);
		}
		enableRightTextBtn(R.string.Send, true, new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				SendSuggestion();
			}});
	}
	private void SendSuggestion() {
		
		String content=txtContent.getText().toString();
		if(content==null || content.length()<=0){
			Toast.makeText(
					SuggestionActivity.this,
					"请输入您的宝贵意见",
					Toast.LENGTH_LONG).show();
		}
		Param[] params=new Param[4];
		params[0] = new Param("UserId", Settings.instance().getUserId());
		params[1] = new Param("Token", Settings.instance().getToken());
		params[2] = new Param("Phone", txtPhone.getText().toString());
		params[3] = new Param("Content", txtContent.getText().toString());
		OkHttpClientManager
				.postAsyn(
						Settings.instance().getApiUrl() + "/maintain/suggest",
						params,
						new OkHttpClientManager.ResultCallback<ResponseDad>() {

							@Override
							public void onError(Request request, Exception e) {
							 
								Toast.makeText(
										SuggestionActivity.this,
										e.getMessage(),
										Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onResponse(
									ResponseDad response) {
								txtContent.setText("");
								Toast.makeText(
										SuggestionActivity.this,
										"您的建议已经提交",
										Toast.LENGTH_LONG).show();
							}

						});

	}
}
