package com.longhuapuxin.u5;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TransactionSendActivity extends BaseActivity implements
		OnClickListener {
	private TextView request, cancel, user1, user2, time;
	private EditText text, money;
	public static final int TRANSCTION_RESULT = 5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transction);
		initHeader("");
		initViews();
		setData();
	}

	private void setData() {
		Intent intent = getIntent();
		user1.setText(intent.getStringExtra("nickName"));
		user2.setText(intent.getStringExtra("nickName2"));
		time.setText(intent.getStringExtra("time"));
	}

	private void initViews() {
		cancel = (TextView) findViewById(R.id.cancel);
		cancel.setOnClickListener(this);
		request = (TextView) findViewById(R.id.request);
		request.setOnClickListener(this);
		text = (EditText) findViewById(R.id.text);
		money = (EditText) findViewById(R.id.money);
		user1 = (TextView) findViewById(R.id.user1);
		user2 = (TextView) findViewById(R.id.user2);
		time = (TextView) findViewById(R.id.time);
	}

	@Override
	public void onClick(View v) {
		if (v == request) {
			if (TextUtils.isEmpty(text.getText().toString())
					|| TextUtils.isEmpty(money.getText().toString())) {
				Toast.makeText(this, "交易未填写完全", 0).show();
			} else {
				Intent intent = new Intent(TransactionSendActivity.this,
						ChatActivity.class);
				// intent.putExtra("Amount", money.getText().toString());
				// intent.putExtra("Note", text.getText().toString());
				intent.putExtra("Note", text.getText().toString());
				intent.putExtra("Amount", money.getText().toString());
				setResult(TRANSCTION_RESULT, intent);
				finish();
			}
		} else if (v == cancel) {
			finish();
		}
	}

}
