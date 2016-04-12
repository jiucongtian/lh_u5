package com.longhuapuxin.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.entity.ResponseGetAccount;
import com.longhuapuxin.entity.ResponseGetAccount.User.CareWhichShop;
import com.longhuapuxin.entity.ResponseGetAccount.User.CareWho;
import com.longhuapuxin.u5.CreateGroupActivity;
import com.longhuapuxin.u5.FindFromaddressActivity;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.u5.Settings;
import com.longhuapuxin.u5.U5Application;
import com.longhuapuxin.view.RoundCornerImageView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NewApi")
public class ContactsFragment extends Fragment implements OnClickListener {
	private FragmentManager fm = null;
	private List<Fragment> mFragments;
	private int[] mTvIds;
	private int[] mImgIds;
	private TextView[] mTvs;
	private ImageView[] mImgs;
	private List<CareWho> careWho;
	private List<CareWhichShop> careWhichShops;
	private ContactPersonFragment contactPersonFragment;
	private ContactShopFragment contactShopFragment;
	private MyGroupFragment myGroupFragment;
	RoundCornerImageView imgPortrait;
	U5Application app;
	FragmentListener mListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// WaitDialog.instance().showWaitNote(getActivity());
		View view = inflater.inflate(R.layout.fragment_contacts, null);
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		app = (U5Application) getActivity().getApplication();
		mListener = (FragmentListener) getActivity();
	}

	private void init() {
		imgPortrait = (RoundCornerImageView) getView().findViewById(
				R.id.imgPortrait);
		imgPortrait.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.optionClicked();
			}
		});

		View rightBtn = getView().findViewById(R.id.rightImageBtn);
		rightBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				final Dialog dialog = new Dialog(getActivity(), R.style.activity_translucent);
				dialog.setContentView(R.layout.dialog_attention);

				dialog.findViewById(R.id.parent).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog.dismiss();
					}
				});

				dialog.findViewById(R.id.layoutAddFriend).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(getActivity(), FindFromaddressActivity.class);
						startActivity(intent);
						dialog.dismiss();
					}
				});
				dialog.findViewById(R.id.layoutCreateGroup).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(getActivity(), CreateGroupActivity.class);
						startActivity(intent);
						dialog.dismiss();
					}
				});

				dialog.setCancelable(true);
				dialog.show();
			}
		});

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
		initDatas();
		initViews();
		switchTab(0);
		// WaitDialog.instance().hideWaitNote();
	}

	@Override
	public void onResume() {

		Log.d("", "-------fuck:onResume");
		super.onResume();
		app.ObservePortait(imgPortrait);

	}

	@Override
	public void onPause() {
		app.StopObservePortait(imgPortrait);
		super.onPause();
	}

	private void initDatas() {
		careWho = Settings.instance().User.getCareWho();
		contactPersonFragment = new ContactPersonFragment(careWho);
		careWhichShops = Settings.instance().User.getCareWhichShops();
		contactShopFragment = new ContactShopFragment(careWhichShops);
		myGroupFragment = new MyGroupFragment();
		initFragmentAndManager();
		// httpRequestGetAccount();
	}

	private void initViews() {

		mImgIds = new int[] { R.id.below1, R.id.below2, R.id.below3 };
		mTvIds = new int[] { R.id.tabText1, R.id.tabText2, R.id.tabText3 };
		mImgs = new ImageView[mImgIds.length];
		for (int i = 0; i < mImgIds.length; i++) {
			mImgs[i] = (ImageView) getView().findViewById(mImgIds[i]);
		}
		mTvs = new TextView[mTvIds.length];
		for (int i = 0; i < mTvIds.length; i++) {
			mTvs[i] = (TextView) getView().findViewById(mTvIds[i]);
			mTvs[i].setOnClickListener(this);
		}
	}

	private void initFragmentAndManager() {
		fm = getFragmentManager();
		mFragments = new ArrayList<Fragment>();
		mFragments.add(contactPersonFragment);
		mFragments.add(contactShopFragment);
		mFragments.add(myGroupFragment);
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

	private void switchTab(int tabPos) {
		FragmentTransaction transaction = fm.beginTransaction();
		if (mCurFragment == null) {
			// 如果当前显示的Fragment为空
			Fragment fragment = mFragments.get(tabPos);
			transaction.add(R.id.contact_fragment, fragment, fragment
					.getClass().getName());
			mCurFragment = fragment;
		} else {
			// 如果当前显示的Fragment不为空
			Fragment fragment = mFragments.get(tabPos);
			if (fm.findFragmentByTag(fragment.getClass().getName()) == null) {
				transaction.add(R.id.contact_fragment, fragment, fragment
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

	private void httpRequestGetAccount() {
		Settings.instance().load(getActivity());
		Param[] params = new Param[3];
		params[0] = new Param("UserId", Settings.instance().getUserId());
		params[1] = new Param("Token", Settings.instance().getToken());
		params[2] = new Param("TargetUserId", Settings.instance().getUserId());

		OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
				+ "/auth/getaccount", params,
				new OkHttpClientManager.ResultCallback<ResponseGetAccount>() {

					@Override
					public void onError(Request request, Exception e) {
						WaitDialog.instance().hideWaitNote();
					}

					@Override
					public void onResponse(ResponseGetAccount response) {
						if (response.isSuccess()) {
							careWho = response.getUser().getCareWho();
							careWhichShops = response.getUser()
									.getCareWhichShops();
							contactPersonFragment = new ContactPersonFragment(
									careWho);
							initFragmentAndManager();
							switchTab(0);
							WaitDialog.instance().hideWaitNote();
						} else {
//							Log.d("",
//									"------GetAccount error"
//											+ response.getErrorMessage());
//							Log.d("",
//									"------GetAccount error"
//											+ response.getErrorCode());
							Logger.info("------GetAccount error"
									+ response.getErrorMessage());
							Logger.info("------GetAccount error"
									+ response.getErrorCode());
						}
					}

				});

	}

}
