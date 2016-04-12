package com.longhuapuxin.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.longhuapuxin.common.ImageUrlLoader;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.common.Utils;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.entity.ResponseGetAccount.User.Label;
import com.longhuapuxin.entity.ResponseGetAccount;
import com.longhuapuxin.entity.ResponseRandomPeople;
import com.longhuapuxin.entity.ResponseSearchLabel;
import com.longhuapuxin.entity.ResponseTopLabel;
import com.longhuapuxin.entity.ResponseSearchLabel.User;
import com.longhuapuxin.entity.ResponseTopLabel.TopLabel;
import com.longhuapuxin.u5.EditLabelActivity;
import com.longhuapuxin.u5.LabelDetailActivity;
import com.longhuapuxin.u5.LabelListActivity;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.u5.SearchActivity;
import com.longhuapuxin.u5.Settings;
import com.longhuapuxin.u5.U5Application;
import com.longhuapuxin.view.RoundCornerImageView;
import com.squareup.okhttp.Request;

/**
 * @author zh
 * @date 2015-8-26
 */
@SuppressLint("NewApi")
public class LabelFragment extends Fragment implements OnClickListener {
	View mView;
	FragmentListener mListener;
	ImageView mMyLabelPhoto;
	RoundCornerImageView imgPortrait;
	TextView mMyLabelText;
	List<ResponseRandomPeople.People> mTopLabels;
	List<TopLabelView> mTopLabelView;
	List<Integer> mRandomList;
	ImageButton mChangeBtn;
	U5Application app;
	RelativeLayout mToplabelContainer;

	public class TopLabelView {
		TextView mName;
		TextView mCount;
		ImageView mPhoto;

		public TopLabelView(TextView mName, TextView mCount, ImageView mPhoto) {
			super();
			this.mName = mName;
			this.mCount = mCount;
			this.mPhoto = mPhoto;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Logger.debug("MainLabelFragment->onCreateView");
		mView = inflater.inflate(R.layout.fragment_label, null);
		mRandomList = new ArrayList<Integer>();
		init();
		// 放在mainActivity中
		// WaitDialog.instance().showWaitNote(getActivity());
		return mView;
	}

	private void init() {
		mTopLabelView = new ArrayList<TopLabelView>();
		imgPortrait = (RoundCornerImageView) mView
				.findViewById(R.id.imgPortrait);
		// imgPortrait.setOnClickListener(this);
		// ImageButton mainLeftBtn = (ImageButton) mView
		// .findViewById(R.id.mainLeftBtn);
		mToplabelContainer = (RelativeLayout) mView
				.findViewById(R.id.topLabelContainer);

		mMyLabelText = (TextView) mView.findViewById(R.id.myLabelText);
		mMyLabelPhoto = (ImageView) mView.findViewById(R.id.myLabelPhoto);

		// 换一换
		mChangeBtn = (ImageButton) mView.findViewById(R.id.changeBtn);
		mChangeBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				toggleMainLabel();
				setTopLabels();
			}
		});

		imgPortrait.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.optionClicked();
			}
		});

		mView.findViewById(R.id.searchBtn).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity(),
								SearchActivity.class);
						intent.putExtra("search_type",
								SearchActivity.SEARCH_LABLE);
						startActivity(intent);
					}
				});

		mView.findViewById(R.id.label_img_1).setOnClickListener(this);
		mView.findViewById(R.id.label_img_2).setOnClickListener(this);
		mView.findViewById(R.id.label_img_3).setOnClickListener(this);
		mView.findViewById(R.id.label_img_4).setOnClickListener(this);
		mView.findViewById(R.id.label_img_5).setOnClickListener(this);
		mView.findViewById(R.id.label_img_6).setOnClickListener(this);

		// 点击浏览我的标签
		mView.findViewById(R.id.myLabelBg).setOnClickListener(this);
		mView.findViewById(R.id.labelContentLeft).setOnClickListener(this);
//		fetchTopLabels();
		fetchTopPersons();
	}

	@Override
	public void onResume() {
		super.onResume();
		app.ObservePortait(imgPortrait);
		List<Label> myLabelList = Settings.instance().User.getLabels();
		Label tmpLabel = null;

		for (Label label : myLabelList) {
			if (label.getIndex().equals("1")) {
				tmpLabel = label;
				break;
			}
		}

		if (tmpLabel == null) {
			mMyLabelText.setText(R.string.Set);
			mMyLabelPhoto
					.setImageResource(R.drawable.photo_label);
		} else {
			String photo = Utils.getFirstId(tmpLabel.getPhotos());
			if (TextUtils.isEmpty(photo)) {
				mMyLabelPhoto
						.setImageResource(R.drawable.photo_label);
			} else {
				ImageUrlLoader.fetchImageUrl(photo, mMyLabelPhoto,
						getActivity());
			}

			if (TextUtils.isEmpty(tmpLabel.getLabelName())) {
				mMyLabelText.setText(R.string.Set);
			} else {
				mMyLabelText.setText(tmpLabel.getLabelName());
			}
		}
	}

	@Override
	public void onPause() {
		app.StopObservePortait(imgPortrait);
		super.onPause();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		app = (U5Application) activity.getApplication();
		mListener = (FragmentListener) activity;
	}

	private void go2LabelList(int randomId) {
		int id = mRandomList.get(randomId);
		Intent intent = new Intent(getActivity(), LabelListActivity.class);
		intent.putExtra("labelName", mTopLabels.get(id).getLabelName());
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();

		// switch to theirs label page.
		switch (viewId) {
		case R.id.label_img_1:
			go2LabelList(0);
			break;
		case R.id.label_img_2:
			go2LabelList(1);
			break;
		case R.id.label_img_3:
			go2LabelList(2);
			break;
		case R.id.label_img_4:
			go2LabelList(3);
			break;
		case R.id.label_img_5:
			go2LabelList(4);
			break;
		case R.id.label_img_6:
			go2LabelList(5);
			break;

		case R.id.myLabelBg:
			// U5Application u5App = (U5Application)
			// getActivity().getApplication();
			// List<Label> myLabelList = u5App.getmMyLabels();
			//
			// //设置修改标签参数
			// if(myLabelList.size() > 0) {
			// Label firstLabel = myLabelList.get(0);
			// u5App.putParam(U5Application.MODIFY_LABEL, firstLabel);
			// } else {
			// u5App.putParam(U5Application.MODIFY_LABEL, null);
			// }
			//
			// Intent intent2 = new Intent(getActivity(),
			// ModifyLabelActivity.class);
			// startActivity(intent2);
			Intent intent = new Intent(getActivity(), EditLabelActivity.class);
			startActivity(intent);
			break;
		case R.id.labelContentLeft:
			ResponseGetAccount.User accountUser = Settings.instance().User;
			ResponseSearchLabel newer = new ResponseSearchLabel();
			User obj = newer.new User(accountUser);
			((U5Application) getActivity().getApplication()).putParam(
					U5Application.USER_DETAIL, obj);
			Intent goToDetailIntent = new Intent(getActivity(), LabelDetailActivity.class);
			startActivity(goToDetailIntent);
			break;
		}

	}





	private void fetchTopPersons() {
		Param[] params = new Param[3];
		params[0] = new Param("UserId", Settings.instance().getUserId());
		params[1] = new Param("Token", Settings.instance().getToken());
		params[2] = new Param("Total", "50");

		OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
						+ "/label/getpeoplerandom", params,
				new OkHttpClientManager.ResultCallback<ResponseRandomPeople>() {

					@Override
					public void onError(Request request, Exception e) {
						Logger.info("LabelFragment.getpeoplerandom.onError"
								+ e.toString());

						WaitDialog.instance().hideWaitNote();
					}

					@Override
					public void onResponse(ResponseRandomPeople response) {
						Logger.info("LabelFragment.getpeoplerandom.onResponse"
								+ response);

						if (response.isSuccess()) {

							mTopLabels = response.getPeople();
							if (mTopLabels.size() > 0) {
								setTopLabels();
							}
							Label tmpLabel = null;

							for (Label label : Settings.instance().User
									.getLabels()) {
								if (label.getIndex().equals("1")) {
									tmpLabel = label;
									break;
								}
							}

							if (tmpLabel == null) {
								mMyLabelText.setText(R.string.Set);
								mMyLabelPhoto
										.setImageResource(R.drawable.photo_label);
							} else {
								String photo = Utils.getFirstId(tmpLabel
										.getPhotos());
								if (photo.isEmpty()) {
									mMyLabelPhoto
											.setImageResource(R.drawable.photo_label);
								} else {
									ImageUrlLoader.fetchImageUrl(photo,
											mMyLabelPhoto, getActivity());
								}

								if (TextUtils.isEmpty(tmpLabel.getLabelName())) {
									mMyLabelText.setText(R.string.Set);
								} else {
									mMyLabelText.setText(tmpLabel
											.getLabelName());
								}
							}

							mView.findViewById(R.id.labelBodyLayout)
									.setVisibility(View.VISIBLE);
						}
						WaitDialog.instance().hideWaitNote();
					}

				});
	}















//	private void fetchTopLabels() {
//		Param[] params = new Param[2];
//		params[0] = new Param("UserId", Settings.instance().getUserId());
//		params[1] = new Param("Token", Settings.instance().getToken());
//
//		OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
//				+ "/label/getlabeltopten", params,
//				new OkHttpClientManager.ResultCallback<ResponseTopLabel>() {
//
//					@Override
//					public void onError(Request request, Exception e) {
//						Logger.info("ShopFragment.fetchTopLabels.onError"
//								+ e.toString());
//
//						WaitDialog.instance().hideWaitNote();
//					}
//
//					@Override
//					public void onResponse(ResponseTopLabel response) {
//						Logger.info("ShopFragment.fetchTopLabels.onResponse"
//								+ response);
//
//						if (response.isSuccess()) {
//
//							mTopLabels = response.getMarkTopten();
//							if (mTopLabels.size() > 0) {
//								setTopLabels();
//							}
//							Label tmpLabel = null;
//
//							for (Label label : Settings.instance().User
//									.getLabels()) {
//								if (label.getIndex().equals("1")) {
//									tmpLabel = label;
//									break;
//								}
//							}
//
//							if (tmpLabel == null) {
//								mMyLabelText.setText(R.string.Set);
//								mMyLabelPhoto
//										.setImageResource(R.drawable.label_home_content_default);
//							} else {
//								String photo = Utils.getFirstId(tmpLabel
//										.getPhotos());
//								if (photo.isEmpty()) {
//									mMyLabelPhoto
//											.setImageResource(R.drawable.label_home_content_default);
//								} else {
//									ImageUrlLoader.fetchImageUrl(photo,
//											mMyLabelPhoto, getActivity());
//								}
//
//								if (TextUtils.isEmpty(tmpLabel.getLabelName())) {
//									mMyLabelText.setText(R.string.Set);
//								} else {
//									mMyLabelText.setText(tmpLabel
//											.getLabelName());
//								}
//							}
//
//							mView.findViewById(R.id.labelBodyLayout)
//									.setVisibility(View.VISIBLE);
//						}
//						WaitDialog.instance().hideWaitNote();
//					}
//
//				});
//	}

	private void setTopLabels() {
		int size = mTopLabels.size();
		mRandomList.clear();
		while (mRandomList.size() < 6) {
			Random rdm = new Random(System.currentTimeMillis());
			int radomIndex = Math.abs(rdm.nextInt()) % size;
			boolean isExisted = false;
			for (int i : mRandomList) {
				if (i == radomIndex) {
					isExisted = true;
					break;
				}
			}

			if (!isExisted) {
				mRandomList.add(radomIndex);
			}
		}

		mTopLabelView.add(new TopLabelView((TextView) mView
				.findViewById(R.id.topLabelCount1), (TextView) mView
				.findViewById(R.id.topLabelName1), (ImageView) mView
				.findViewById(R.id.label_img_1)));
		mTopLabelView.add(new TopLabelView((TextView) mView
				.findViewById(R.id.topLabelCount2), (TextView) mView
				.findViewById(R.id.topLabelName2), (ImageView) mView
				.findViewById(R.id.label_img_2)));
		mTopLabelView.add(new TopLabelView((TextView) mView
				.findViewById(R.id.topLabelCount3), (TextView) mView
				.findViewById(R.id.topLabelName3), (ImageView) mView
				.findViewById(R.id.label_img_3)));
		mTopLabelView.add(new TopLabelView((TextView) mView
				.findViewById(R.id.topLabelCount4), (TextView) mView
				.findViewById(R.id.topLabelName4), (ImageView) mView
				.findViewById(R.id.label_img_4)));
		mTopLabelView.add(new TopLabelView((TextView) mView
				.findViewById(R.id.topLabelCount5), (TextView) mView
				.findViewById(R.id.topLabelName5), (ImageView) mView
				.findViewById(R.id.label_img_5)));
		mTopLabelView.add(new TopLabelView((TextView) mView
				.findViewById(R.id.topLabelCount6), (TextView) mView
				.findViewById(R.id.topLabelName6), (ImageView) mView
				.findViewById(R.id.label_img_6)));

		for (int i = 0; i < 6; i++) {
			TopLabelView viewGroup = mTopLabelView.get(i);
			int randomIndex = mRandomList.get(i);
			if (randomIndex >= mTopLabels.size())
				continue;
//			viewGroup.mCount.setText(mTopLabels.get(randomIndex).getCount());
			viewGroup.mName.setText(mTopLabels.get(randomIndex).getLabelName());
			
			if(TextUtils.isEmpty(mTopLabels.get(randomIndex).getPortrait())) {
				viewGroup.mPhoto.setImageResource(R.drawable.avatar_default_man);
			} else {
				ImageUrlLoader.fetchImageUrl(
						mTopLabels.get(randomIndex).getPortrait(), viewGroup.mPhoto,
						getActivity());
			}
			
			
		}
	}

	private void toggleMainLabel() {

		Animation animation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.main_label_rotate);

		animation.setAnimationListener(new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				hideTopTextBox(true);
				mChangeBtn.setClickable(false);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				hideTopTextBox(false);
				mChangeBtn.setClickable(true);
			}
		});

		mToplabelContainer.startAnimation(animation);
	}

	private void hideTopTextBox(boolean flag) {
		int visibility = flag ? View.INVISIBLE : View.VISIBLE;

		for (TopLabelView viewGroup : mTopLabelView) {
			viewGroup.mCount.setVisibility(View.GONE);
			viewGroup.mName.setVisibility(visibility);
		}
	}

}
