package com.longhuapuxin.u5;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.longhuapuxin.fragment.FragmentTransactionComplete;
import com.longhuapuxin.fragment.FragmentTransactionUnComplete;

public class OrderActivity extends BaseActivity implements OnClickListener {
	private FragmentManager fm = null;
	private List<Fragment> mFragments;
	private int[] mTvIds;
	private int[] mImgIds;
	private TextView[] mTvs;
	private ImageView[] mImgs;
	private FragmentTransactionComplete fragmentTransactionComplete;
	private FragmentTransactionUnComplete fragmentTransactionUnComplete;
	// 1.活动交易 3.已完成交易
	private int transactionStatus = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_order);
		initHeader(R.string.profile_my_order);
		initDatas();
		initViews();
		switchTab(0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 101) {
			refreshCompleteTransaction();
		}
	}

	private void initDatas() {
		if (fragmentTransactionComplete == null) {
			fragmentTransactionComplete = new FragmentTransactionComplete();
			fragmentTransactionUnComplete = new FragmentTransactionUnComplete();
			initFragmentAndManager();
		}
		// httpRequestGetAccount();
	}

	@SuppressLint("NewApi")
	private void initFragmentAndManager() {
		fm = getFragmentManager();
		mFragments = new ArrayList<Fragment>();
		mFragments.add(fragmentTransactionUnComplete);
		mFragments.add(fragmentTransactionComplete);
	}

	private void initViews() {

		mImgIds = new int[] { R.id.below1, R.id.below2 };
		mTvIds = new int[] { R.id.tabText1, R.id.tabText2 };
		mImgs = new ImageView[mImgIds.length];
		for (int i = 0; i < mImgIds.length; i++) {
			mImgs[i] = (ImageView) findViewById(mImgIds[i]);
		}
		mTvs = new TextView[mTvIds.length];
		for (int i = 0; i < mTvIds.length; i++) {
			mTvs[i] = (TextView) findViewById(mTvIds[i]);
			mTvs[i].setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		for (int i = 0; i < mTvs.length; i++) {
			if (mTvs[i] == v) {
				switchTab(i);
				break;
			}
		}
	}

	// 用来表示当前显示的Fragment对象
	private Fragment mCurFragment;

	@SuppressLint("NewApi")
	private void switchTab(int tabPos) {
		if (tabPos == 0) {
			transactionStatus = 1;
		} else {
			transactionStatus = 3;
		}
		FragmentTransaction transaction = fm.beginTransaction();
		if (mCurFragment == null) {
			// 如果当前显示的Fragment为空
			Fragment fragment = mFragments.get(tabPos);
			transaction.add(R.id.transaction_fragment, fragment, fragment
					.getClass().getName());
			mCurFragment = fragment;
		} else {
			// 如果当前显示的Fragment不为空
			Fragment fragment = mFragments.get(tabPos);
			if (fm.findFragmentByTag(fragment.getClass().getName()) == null) {
				transaction.add(R.id.transaction_fragment, fragment, fragment
						.getClass().getName());
				// hide隐藏，栈的顺序不变
				transaction.hide(mCurFragment);
				mCurFragment = fragment;
			} else {
				transaction.hide(mCurFragment);
				transaction.show(fragment);
				mCurFragment = fragment;
			}
		}
		transaction.commit();
		for (int i = 0; i < mTvs.length; i++) {
			if (i == tabPos) {
				mImgs[i].setVisibility(View.VISIBLE);
				mTvs[i].setTextColor(getResources().getColor(R.color.orange));
			} else {
				mImgs[i].setVisibility(View.INVISIBLE);
				mTvs[i].setTextColor(getResources().getColor(R.color.gray));
			}
		}

	}

	public void refreshCompleteTransaction() {
		fragmentTransactionComplete.refreshTransactionList();
	}

}
