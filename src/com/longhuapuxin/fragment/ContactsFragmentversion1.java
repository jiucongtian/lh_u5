package com.longhuapuxin.fragment;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.entity.ResponseGetAccount.User.CareWho;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.u5.Settings;
import com.longhuapuxin.u5.U5Application;

@SuppressLint("NewApi")
public class ContactsFragmentversion1 extends Fragment {
	private FragmentManager fm = null;
	private List<CareWho> careWho;
	private ContactPersonFragment contactPersonFragment;
	ImageView imgPortrait;
	U5Application app;
	FragmentListener mListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		WaitDialog.instance().showWaitNote(getActivity());
		View view = inflater.inflate(R.layout.fragment_contacts, null);
		imgPortrait = (ImageView) view.findViewById(R.id.imgPortrait);
		ImageButton mainLeftBtn = (ImageButton) view
				.findViewById(R.id.mainLeftBtn);
		mainLeftBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.optionClicked();
			}
		});
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		app = (U5Application) activity.getApplication();
		mListener = (FragmentListener) activity;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initDatas();
	}

	private boolean isFirst = true;

	@Override
	public void onResume() {
		super.onResume();
		app.ObservePortait(imgPortrait);
		if (contactPersonFragment != null && !isFirst) {
			contactPersonFragment.SortCareShops();
			contactPersonFragment.refreshData();
		}
		isFirst = false;
	}

	@Override
	public void onPause() {
		app.StopObservePortait(imgPortrait);
		super.onPause();
	}

	private void initDatas() {
		careWho = Settings.instance().User.getCareWho();
		contactPersonFragment = new ContactPersonFragment(careWho);
		initFragmentAndManager();
		addPersonContacts();
		WaitDialog.instance().hideWaitNote();
		// GetAccount();
	}

	private void initFragmentAndManager() {
		fm = getFragmentManager();
	}

	private void addPersonContacts() {
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.add(R.id.contact_fragment, contactPersonFragment);
		transaction.commit();

	}

}
