package com.longhuapuxin.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.longhuapuxin.common.Logger;
import com.longhuapuxin.u5.BaseActivity;
import com.longhuapuxin.u5.CitiesActivity;
import com.longhuapuxin.u5.MipcaCaptureActivity;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.u5.Settings;
import com.longhuapuxin.u5.ShopListActivity;
import com.longhuapuxin.u5.U5Application;
import com.longhuapuxin.view.RoundCornerImageView;

@SuppressLint("NewApi")
public class ShopFragment extends Fragment implements OnClickListener {

	FragmentListener mListener;
	ImageView mSearchBtn;
	RoundCornerImageView imgPortrait;
	EditText mSearchText;
	View mView;
	TextView mRegionText;
	ScrollView mScrollView;
	Button shopPay;
	int mScrollY = 0;
	U5Application app;

	// private final static int RESULT_CITY = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Logger.debug("ShopFragment->onCreateView");
		mView = inflater.inflate(R.layout.fragment_shop, null);
		init();

		return mView;
	}

	private boolean isClickable = false;

	@Override
	public void onResume() {
		super.onResume();
		app.ObservePortait(imgPortrait);
		if (!isClickable) {
			Settings.instance().load(getActivity());
			shopPay.setClickable(true);
			isClickable = true;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		app.StopObservePortait(imgPortrait);
		super.onPause();
	}

	private void init() {
		mRegionText = (TextView) mView.findViewById(R.id.shop_region);
		mRegionText.setText(Settings.instance().getCity());

		mRegionText.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), CitiesActivity.class);
				startActivityForResult(intent, 0);
			}
		});
		imgPortrait = (RoundCornerImageView) mView
				.findViewById(R.id.imgPortrait);

		imgPortrait.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.optionClicked();
			}
		});

		shopPay = (Button) mView.findViewById(R.id.shopPay);
		shopPay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), MipcaCaptureActivity.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
				startActivity(intent);

//				Intent intent = new Intent(getActivity(), GoPayActivity.class);
//				startActivity(intent);
			}
		});

		delayLoad();
	}

	public void delayLoad() {
		ViewStub vs = (ViewStub) mView.findViewById(R.id.stub_import);
		vs.inflate();

		mSearchBtn = (ImageView) mView.findViewById(R.id.searchBtn);
		mSearchText = (EditText) mView.findViewById(R.id.search_editor);
		mSearchBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getActivity(),
						ShopListActivity.class);
				intent.putExtra("word", mSearchText.getText().toString());
				startActivity(intent);

				// Intent intent = new Intent(getActivity(),
				// SearchActivity.class);
				// intent.putExtra("search_type", SearchActivity.SEARCH_SHOP);
				// startActivity(intent);
			}
		});

		mView.findViewById(R.id.imgFood).setOnClickListener(this);
		mView.findViewById(R.id.imgClothes).setOnClickListener(this);
		mView.findViewById(R.id.imgInnovation).setOnClickListener(this);
		mView.findViewById(R.id.imgHappiness).setOnClickListener(this);
		mView.findViewById(R.id.bigImageFood).setOnClickListener(this);
		mView.findViewById(R.id.bigImageClothes).setOnClickListener(this);
		mView.findViewById(R.id.bigImageInnovation).setOnClickListener(this);
		mView.findViewById(R.id.bigImageHappiness).setOnClickListener(this);

		// initView(mShopCategories);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		app = (U5Application) activity.getApplication();
		mListener = (FragmentListener) activity;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		if (mScrollView != null) {
			if (hidden) {
				mScrollY = mScrollView.getScrollY();

			} else {
				mScrollView.smoothScrollTo(0, mScrollY);
			}
		}
		super.onHiddenChanged(hidden);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == BaseActivity.RESULT_CITY) {
			String cityName = data.getExtras().getString("name");
			mRegionText.setText(cityName);
		}
	}

	@Override
	public void onClick(View v) {

		String category = "";
		switch (v.getId()) {
		case R.id.bigImageFood:
		case R.id.imgFood:
			category = "0100";
			break;
		case R.id.imgClothes:
		case R.id.bigImageClothes:
			category = "0500";
			break;
		case R.id.imgInnovation:
		case R.id.bigImageInnovation:
			category = "0600";
			break;
		case R.id.imgHappiness:
		case R.id.bigImageHappiness:
			category = "0700";
			break;
		}


		Intent intent = new Intent(getActivity(), ShopListActivity.class);
		intent.putExtra("shop_category", category);
		startActivity(intent);
	}
}
