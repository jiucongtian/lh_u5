package com.longhuapuxin.u5;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.db.bean.ChatMsg;
import com.longhuapuxin.entity.ResponseGetOrder;
import com.longhuapuxin.entity.ResponseGetOrder.Order;
import com.squareup.okhttp.Request;

public class TransactionReceiverActivity extends BaseActivity implements
		OnClickListener {
	public static final int TRANSCTION_RECEIVER_REQUEST = 6;
	public static final int TRANSCTION_RECEIVER_RESULT = 7;
	private TextView accept, refuse;
	private TextView user1, user2, time, text, money;
	private Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			if (msg.what == 0) {
				ResponseGetOrder response = (ResponseGetOrder) msg.obj;
				Order order = response.getOrder();
				user1.setText(order.getNickName1());
				user2.setText(response.getOrder().getNickName2());
				time.setText(response.getOrder().getOrderTime());
				text.setText(response.getOrder().getOrderNote());
				money.setText(response.getOrder().getAmount());
			}
			return false;
		}
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transaction_receiver);
		initHeader("");
		initViews();
		setData();
	}

	private void setData() {
		httpRequestGetOrder(((ChatMsg) getIntent().getSerializableExtra(
				"chatMsg")).getOrderId());
	}

	private void initViews() {
		accept = (TextView) findViewById(R.id.accept);
		accept.setOnClickListener(this);
		refuse = (TextView) findViewById(R.id.refuse);
		refuse.setOnClickListener(this);
		user1 = (TextView) findViewById(R.id.user1);
		user2 = (TextView) findViewById(R.id.user2);
		time = (TextView) findViewById(R.id.time);
		text = (TextView) findViewById(R.id.text);
		money = (TextView) findViewById(R.id.money);
	}

	@Override
	public void onClick(View v) {
		if (v == accept) {
			Intent intent = getIntent();
			ChatMsg chatMsg = (ChatMsg) intent.getSerializableExtra("chatMsg");
			chatMsg.setReceiverStatus("1");
			setResult(TRANSCTION_RECEIVER_RESULT, intent);
			finish();
		} else if (v == refuse) {
			Intent intent = getIntent();
			ChatMsg chatMsg = (ChatMsg) intent.getSerializableExtra("chatMsg");
			chatMsg.setReceiverStatus("2");
			setResult(TRANSCTION_RECEIVER_RESULT, intent);
			finish();
		}
	}

	private void httpRequestGetOrder(String orderId) {
		Param[] params = new Param[3];
		params[0] = new Param("UserId", Settings.instance().getUserId());
		params[1] = new Param("Token", Settings.instance().getToken());
		params[2] = new Param("OrderId", orderId);

		OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
				+ "/order/getOrder", params,
				new OkHttpClientManager.ResultCallback<ResponseGetOrder>() {

					@Override
					public void onError(Request request, Exception e) {
//						Log.d("", "----GetOrderFail");
						Logger.info("----GetOrderFail");
					}

					@Override
					public void onResponse(ResponseGetOrder response) {

						if (response.isSuccess()) {
							Message msg = new Message();
							msg.what = 0;
							msg.obj = response;
							handler.sendMessage(msg);
						} else {
//							Log.d("",
//									"----GetOrder" + response.getErrorMessage());
							Logger.info("----GetOrder" + response.getErrorMessage());
						}
					}
				});

	}

}
