package com.longhuapuxin.u5;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.longhuapuxin.adapter.BillAdapter;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.db.bean.BeanRecord;
import com.longhuapuxin.entity.ResponseGetBeanRecord;
import com.longhuapuxin.view.PullToRefreshListView;
import com.longhuapuxin.view.PullToRefreshListView.IOnLoadMoreListener;
import com.squareup.okhttp.Request;

public class BillActivity extends BaseActivity implements IOnLoadMoreListener {

	private PullToRefreshListView mListView;
	private List<BeanRecord> beanRecordList;
	private BillAdapter mListAdapter;
	private int mCurrentIndex = 1;
	private static final int DATA_LOAD_SIZE = 20;
	private static final int ACTIVITY_REQUEST_CODE = 1030;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bill);
		initHeader(R.string.profile_my_bean);
		httpRequestGetBeanRecord();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mCurrentIndex = 1;
		httpRequestGetBeanRecord();
	}

	@Override
	protected void onResume() {
		((TextView) findViewById(R.id.txtBalance))
				.setText(Settings.instance().User.getBalance());
		httpRequestGetBeanRecord();
		super.onResume();
	}

	private void initViews() {
		enableRightTextBtn(R.string.Buy, true, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(BillActivity.this,
						BuyBeanActivity.class);
				startActivityForResult(intent, ACTIVITY_REQUEST_CODE);
			}

		});
		mListView = (PullToRefreshListView) findViewById(R.id.listview);
		mListView.setOnLoadMoreListener(this);

		mListAdapter = new BillAdapter(this, beanRecordList);
		mListView.setAdapter(mListAdapter);

	}

	private void adjustListSize(int total) {
		if (total <= DATA_LOAD_SIZE) {
			mListView.onLoadMoreComplete(true);
		} else {
			mListView.onLoadMoreComplete(false);
			mCurrentIndex++;
		}
	}

	private void httpRequestGetBeanRecord() {
		WaitDialog.instance().showWaitNote(this);
		Settings setting = Settings.instance();
		Param[] params = new Param[4];
		params[0] = new Param("Token", setting.getToken());
		params[1] = new Param("UserId", setting.getUserId());
		params[2] = new Param("PageSize", String.valueOf(DATA_LOAD_SIZE));
		params[3] = new Param("PageIndex", Integer.toString(mCurrentIndex));

		OkHttpClientManager
				.postAsyn(
						setting.getApiUrl() + "/bean/getbeanrecord",
						params,
						new OkHttpClientManager.ResultCallback<ResponseGetBeanRecord>() {
							@Override
							public void onError(Request request, Exception e) {
								e.printStackTrace();
								WaitDialog.instance().hideWaitNote();
							}

							@Override
							public void onResponse(
									ResponseGetBeanRecord response) {
								if (response.isSuccess()) {
									beanRecordList = response.getBeanRecords();
									initViews();
									adjustListSize(response.getTotal());
									WaitDialog.instance().hideWaitNote();
								} else {
									WaitDialog.instance().hideWaitNote();
								}
							}
						});

	}

	private void httpRequestGetBeanRecordNotFirst() {
		WaitDialog.instance().showWaitNote(this);
		Settings setting = Settings.instance();
		Param[] params = new Param[4];
		params[0] = new Param("Token", setting.getToken());
		params[1] = new Param("UserId", setting.getUserId());
		params[2] = new Param("PageSize", String.valueOf(DATA_LOAD_SIZE));
		params[3] = new Param("PageIndex", Integer.toString(mCurrentIndex));

		OkHttpClientManager
				.postAsyn(
						setting.getApiUrl() + "/bean/getbeanrecord",
						params,
						new OkHttpClientManager.ResultCallback<ResponseGetBeanRecord>() {
							@Override
							public void onError(Request request, Exception e) {
								e.printStackTrace();
								WaitDialog.instance().hideWaitNote();
							}

							@Override
							public void onResponse(
									ResponseGetBeanRecord response) {
								if (response.isSuccess()) {
									if (response.getBeanRecords().size() <= DATA_LOAD_SIZE) {
										mListView.onLoadMoreComplete(true);
									} else {
										mListView.onLoadMoreComplete(false);
										mCurrentIndex++;
									}

									// 刷新列表
									beanRecordList.addAll(response
											.getBeanRecords());
									mListAdapter.notifyDataSetChanged();
									WaitDialog.instance().hideWaitNote();
								} else {
									WaitDialog.instance().hideWaitNote();
								}
							}
						});

	}

	@Override
	public void OnLoadMore() {
		httpRequestGetBeanRecordNotFirst();
	}
}
