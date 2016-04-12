package com.longhuapuxin.u5;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.longhuapuxin.adapter.ShopDiscountTicketAdapter;
import com.longhuapuxin.common.HttpRequest;
import com.longhuapuxin.common.HttpRequest.HttpRequestListener;
import com.longhuapuxin.entity.ResponseLoginByPhone;
import com.longhuapuxin.entity.ResponseShop;

public class DiscountAndPayActivity extends BaseActivity implements
		OnClickListener {
	// ListView shopDiscountTicketListView;
	View shopHead;

	// ImageView goPay, getDiscount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discount_and_pay);
		initHeader(R.string.title_shops);
		enableRightImage(R.string.shop_pay, new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		initShopInfo();
	}

	private void initHeaderInfo(ResponseShop responseShop) {
		TextView adress = (TextView) findViewById(R.id.adress);
		adress.setText(responseShop.getShop().getName());
		// TextView distance = (TextView) findViewById(R.id.distance);
		// distance.setText(responseShop.getShop().getName());
		TextView more_info_content = (TextView) findViewById(R.id.more_info_content);
		StringBuffer sb = new StringBuffer();
		if (responseShop.getShop().isHasParking()) {
			sb.append("停车位");
		}
		if (responseShop.getShop().isHasWifi()) {
			sb.append("     Wifi");
		}
		sb.append("     " + responseShop.getShop().getWorkTime());
		more_info_content.setText(sb.toString());
		// goPay = (ImageView) shopHead.findViewById(R.id.go_pay);
		// goPay.setOnClickListener(this);
		// getDiscount = (ImageView) shopHead
		// .findViewById(R.id.get_discount);
		// getDiscount.setOnClickListener(this);
		// TODO
		// if (true) {
		// goPay.setVisibility(View.VISIBLE);
		// }

	}

	private void initShopInfo() {
		Intent intent = getIntent();
		String shopCode = intent.getStringExtra("ShopCode");
		httpResponseShop(shopCode);
	}

	private void httpResponseShop(String shopCode) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("Token", Settings.instance()
				.getToken()));
		params.add(new BasicNameValuePair("UserId", Settings.instance()
				.getUserId()));
		params.add(new BasicNameValuePair("ShopCode", shopCode));
		new HttpRequest(new HttpRequestListener() {

			@Override
			public void onHttpRequestOK(int tag, byte[] data) {
				try {
					String json = new String(data, "utf-8");
					ResponseShop responseShop = ((U5Application) getApplication())
							.getGson().fromJson(json, ResponseShop.class);
					// Log.d("", "--------" + responseLoginByPhone);
					if (responseShop.isSuccess()) {
						initHeaderInfo(responseShop);
						initShopCommentListView(responseShop);
					} else {

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onHttpRequestFailed(int tag) {
			}
		}).post(0, Settings.instance().getApiUrl() + "/shop/shop", params);

	}

	private void initShopCommentListView(ResponseShop responseShop) {
		ListView shopDiscountTicketListView = (ListView) findViewById(R.id.shop_discount_ticket_listview);
		shopDiscountTicketListView.addHeaderView(shopHead);
		ShopDiscountTicketAdapter shopCommentAdapter = new ShopDiscountTicketAdapter(
				responseShop, getApplicationContext());
		shopDiscountTicketListView.setAdapter(shopCommentAdapter);
	}

	@Override
	public void onClick(View v) {
//		 if (v == getDiscount) {
//		 getDiscount.setVisibility(View.GONE);
//		 goPay.setVisibility(View.VISIBLE);
//		 } else if (v == goPay) {
//		 goPay.setVisibility(View.VISIBLE);
//		 Intent intent = new Intent(DiscountAndPayActivity.this,
//		 GoPayActivity.class);
//		 startActivity(intent);
//		 }
	}
}
