package com.longhuapuxin.fragment;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.longhuapuxin.common.Logger;
import com.longhuapuxin.dao.SessionDao;
import com.longhuapuxin.u5.MainActivity;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.u5.SetServiceUrlActivity;
import com.longhuapuxin.u5.U5Application;
import com.longhuapuxin.u5.U5Application.RecentSessionListener;
import com.longhuapuxin.view.MyPopupWindow;

@SuppressLint("NewApi")
public class FragmentRight extends Fragment implements OnClickListener,
		RecentSessionListener {

	/**
	 * 对应的Fragment的Class数组
	 **/
	private Fragment[] mFragments;

	/**
	 * 所有下面ImageButton id的数组
	 **/
	private int[] mImageButtonIds;
	/**
	 * 下面ImageButton的数组
	 **/
	private ImageButton[] mImageButtons;
	private FragmentManager fm = null;
	private ImageView mRedPoint;
	private MessageFragment messageFragment;
	private ImageView mCenterBtn;
	private PopupWindow mPop;


	@Override

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_main_fragment_right, null);
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initPopupWindow();
		initViews();

	}

	private void initViews() {
		mCenterBtn = (ImageView) getActivity().findViewById(R.id.imgFeeling);
		mCenterBtn.setOnClickListener(this);
		mRedPoint = (ImageView) getActivity().findViewById(R.id.redPoint);
		fm = getFragmentManager();
		messageFragment = new MessageFragment();
		mFragments = new Fragment[]{new LabelFragment(), messageFragment,
				new ShopFragment(), new ContactsFragment()};
		mImageButtonIds = new int[]{R.id.imgLabelBtn, R.id.imgMessageBtn,
				R.id.imgShopBtn, R.id.imgContactBtn};
		mImageButtons = new ImageButton[mImageButtonIds.length];
		for (int i = 0; i < mImageButtonIds.length; i++) {
			mImageButtons[i] = (ImageButton) getView().findViewById(
					mImageButtonIds[i]);
			mImageButtons[i].setOnClickListener(this);
		}
		// show label page.
		switchTab(0);
		getActivity().findViewById(R.id.imgLabelBtn).setSelected(true);
	}

	@Override
	public void onClick(View v) {
		if (v == mCenterBtn) {
			mPop.showAtLocation(
					getActivity().findViewById(R.id.fragmentContainer),
					Gravity.CENTER, 0, 0);
		}
		for (int i = 0; i < mImageButtons.length; i++) {
			if (v == mImageButtons[i]) {
				switchTab(i);
			}
		}
		int id = v.getId();
		getActivity().findViewById(R.id.imgMessageBtn).setSelected(
				id == R.id.imgMessageBtn);
		getActivity().findViewById(R.id.imgLabelBtn).setSelected(
				id == R.id.imgLabelBtn);
		getActivity().findViewById(R.id.imgContactBtn).setSelected(
				id == R.id.imgContactBtn);
		getActivity().findViewById(R.id.imgShopBtn).setSelected(
				id == R.id.imgShopBtn);

		// *********************test mode************************************
//		if (Logger.DEBUG) {
//			getActivity().findViewById(R.id.imageView1).setOnLongClickListener(
//					new View.OnLongClickListener() {
//
//						@Override
//						public boolean onLongClick(View v) {
//
//							Intent intent = new Intent(getActivity(),
//									SetServiceUrlActivity.class);
//							startActivity(intent);
//
//							return false;
//						}
//					});
//		}
		// *********************test mode************************************

	}

	// 用来表示当前显示的Fragment对象
	private Fragment mCurFragment;

	private void switchTab(int tabPos) {
		FragmentTransaction transaction = fm.beginTransaction();
		if (mCurFragment == null) {
			// 如果当前显示的Fragment为空
			Fragment fragment = mFragments[tabPos];
			transaction.add(R.id.fragmentContainer, fragment, fragment
					.getClass().getName());
			mCurFragment = fragment;
		} else {
			// 如果当前显示的Fragment不为空
			Fragment fragment = mFragments[tabPos];
			if (fm.findFragmentByTag(fragment.getClass().getName()) == null) {
				transaction.add(R.id.fragmentContainer, fragment, fragment
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

	}

	@Override
	public void updateRecentSession() {
		Logger.info("main activity fragment left updateRecentSession");
		updateRedPoint();
	}

	private void updateRedPoint() {
		SessionDao sessionHelper = new SessionDao(getActivity());

		if (sessionHelper.hasUnReadMsg()) {
			mRedPoint.setVisibility(View.VISIBLE);
		} else {
			mRedPoint.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		((U5Application) getActivity().getApplication())
				.removeSessionListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		((U5Application) getActivity().getApplication())
				.setRecentSessionListener(this);
		updateRedPoint();
	}

	public Fragment getMessageFragment() {
		return messageFragment;
	}

	@SuppressWarnings("deprecation")
	private void initPopupWindow() {

		mPop = new MyPopupWindow(getActivity());

	}
}
