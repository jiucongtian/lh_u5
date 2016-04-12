package com.longhuapuxin.u5;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.widget.Toast;

import com.longhuapuxin.adapter.PaymentAdapter;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.entity.ResponseGetConsumeList;
import com.longhuapuxin.entity.ResponseGetConsumeList.Payment;
import com.longhuapuxin.view.PullToRefreshListView;
import com.longhuapuxin.view.PullToRefreshListView.IOnLoadMoreListener;
import com.squareup.okhttp.Request;

public class PaymentActivity extends BaseActivity implements
		IOnLoadMoreListener {
	private PullToRefreshListView mListView;
	private List<Payment> payments;
	private PaymentAdapter mListAdapter;
	private static final int DATA_LOAD_SIZE = 20;
	private int mCurrentIndex = 1;

	@Override
	protected void onResume() {
		super.onResume();
		httpRequestGetConsumeList();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payment_list);
		initHeader(R.string.profile_my_payment);
		payments = new ArrayList<Payment>();
		httpRequestGetConsumeList();
	}

	private void initViews() {
		mListView = (PullToRefreshListView) findViewById(R.id.listWithDraw);
		mListAdapter = new PaymentAdapter(this, payments);
		mListView.setAdapter(mListAdapter);
		mListView.setOnLoadMoreListener(this);

	}

	private void adjustListSize(int total) {
		if (total <= DATA_LOAD_SIZE) {
			mListView.onLoadMoreComplete(true);
		} else {
			mListView.onLoadMoreComplete(false);
			mCurrentIndex++;
		}
	}

	private void httpRequestGetConsumeList() {
		WaitDialog.instance().showWaitNote(this);
		mCurrentIndex = 1;
		Settings setting = Settings.instance();
		Param[] params = new Param[4];
		params[0] = new Param("Token", setting.getToken());
		params[1] = new Param("UserId", setting.getUserId());

		params[2] = new Param("PageSize", "20");
		params[3] = new Param("PageIndex", String.valueOf(mCurrentIndex));

		OkHttpClientManager
				.postAsyn(
						setting.getApiUrl() + "/pay/getconsumelist",
						params,
						new OkHttpClientManager.ResultCallback<ResponseGetConsumeList>() {
							@Override
							public void onError(Request request, Exception e) {
								WaitDialog.instance().hideWaitNote();
								e.printStackTrace();
							}

							@Override
							public void onResponse(
									ResponseGetConsumeList response) {
								payments.clear();
								if (response.isSuccess()) {

									payments.addAll(response.getPayments());
									initViews();
									adjustListSize(response.getTotal());
								}
								mListAdapter.notifyDataSetChanged();
								WaitDialog.instance().hideWaitNote();
							}
						});

	}

	private void httpRequestGetConsumeListNotFirst() {
		WaitDialog.instance().showWaitNote(this);
		Settings setting = Settings.instance();
		Param[] params = new Param[4];
		params[0] = new Param("Token", setting.getToken());
		params[1] = new Param("UserId", setting.getUserId());

		params[2] = new Param("PageSize", "20");
		params[3] = new Param("PageIndex", String.valueOf(mCurrentIndex));

		OkHttpClientManager
				.postAsyn(
						setting.getApiUrl() + "/pay/getconsumelist",
						params,
						new OkHttpClientManager.ResultCallback<ResponseGetConsumeList>() {
							@Override
							public void onError(Request request, Exception e) {
								WaitDialog.instance().hideWaitNote();
								e.printStackTrace();
							}

							@Override
							public void onResponse(
									ResponseGetConsumeList response) {
								if (response.isSuccess()) {

									if (response.getPayments().size() <= DATA_LOAD_SIZE) {
										mListView.onLoadMoreComplete(true);
									} else {
										mListView.onLoadMoreComplete(false);
										mCurrentIndex++;
									}

									// 刷新列表
									payments.addAll(response.getPayments());
									mListAdapter.notifyDataSetChanged();
									// orderListUnComplete =
									// response.getOrders();
									WaitDialog.instance().hideWaitNote();
								} else {
									WaitDialog.instance().hideWaitNote();
								}
							}
						});

	}

	@Override
	public void OnLoadMore() {
		httpRequestGetConsumeListNotFirst();
	}
}
