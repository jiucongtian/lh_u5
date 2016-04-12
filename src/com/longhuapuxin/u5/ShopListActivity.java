package com.longhuapuxin.u5;

import java.util.ArrayList;
import java.util.List;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.longhuapuxin.adapter.RecommendListAdapter;
import com.longhuapuxin.common.ImageUrlLoader;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.entity.ResponseShopList;
import com.longhuapuxin.entity.ResponseShopList.Shop;
import com.longhuapuxin.view.PullToRefreshListView;
import com.longhuapuxin.view.PullToRefreshListView.IOnLoadMoreListener;
import com.squareup.okhttp.Request;

public class ShopListActivity extends BaseActivity implements OnClickListener,
		IOnLoadMoreListener {

	private static final int CURRENT_CITY = -2;
	private static final int WHOLE_COUNTRY = -1;
	private static final int TEN_KM = 10;
	private static final int DATA_LOAD_SIZE = 20;
	private MyTab mTab;
	private RecommendListAdapter mRecommentAdapter;
	private PullToRefreshListView mShopListView;
	private LinearLayout mShopListContainer;
	private View mSearchBoxContainer, mNoDataContainer;
	private List<Shop> mShopList;
	private String mShopCategory, mSearchString;
	private int mDiatance, mCurrentIndex;
	private EditText mSearchWord;

	public class MyTab {

		List<TabObject> mTabList;
		Context mContext;

		public MyTab(Context context) {
			mContext = context;
			mTabList = new ArrayList<TabObject>();
		}

		public void add(TabObject item) {
			mTabList.add(item);
		}

		public void triggerTab(int viewId) {

			for (TabObject object : mTabList) {
				if (object.mClickRegion.getId() == viewId) {
					((TextView) object.mText).setTextColor(mContext
							.getResources().getColor(R.color.orange));
					object.mBelow.setBackgroundColor(mContext.getResources()
							.getColor(R.color.orange));
				} else {
					((TextView) object.mText).setTextColor(mContext
							.getResources().getColor(R.color.deep_gray));
					object.mBelow.setBackgroundColor(mContext.getResources()
							.getColor(R.color.transparent));
				}
			}
		}
	}

	public class TabObject {
		View mText;
		View mBelow;
		View mClickRegion;

		public TabObject(View mText, View mBelow, View mClickRegion) {
			super();
			this.mText = mText;
			this.mBelow = mBelow;
			this.mClickRegion = mClickRegion;
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_list);
		mShopList = new ArrayList<Shop>();
 
		String category = getIntent().getStringExtra("shop_category");
		if (TextUtils.isEmpty(category)) {
			mShopCategory = "";
		} else {
			mShopCategory = category;
		}

		String word = getIntent().getStringExtra("word");
		if (TextUtils.isEmpty(word)) {
			mSearchString = "";
		} else {
			mSearchString = word;
		}
		init();
	}

	@SuppressLint("NewApi")
	private void init() {

		initHeader(R.string.title_shops);
		enableRightImage(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (mSearchBoxContainer.getVisibility()) {
				case View.INVISIBLE:
				case View.GONE:
					mSearchBoxContainer.setVisibility(View.VISIBLE);
					break;
				case View.VISIBLE:

				{
					if (!mSearchString.equals(mSearchWord.getText().toString())) {
						mSearchString = mSearchWord.getText().toString();
						mShopList.clear();
						mCurrentIndex = 1;
						fetchShopList();
					}
					
					InputMethodManager imm = (InputMethodManager) ShopListActivity.this
							 .getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(mSearchWord.getWindowToken(), 0);
					
					mSearchBoxContainer.setVisibility(View.GONE);
				}
					break;
				}

				// Intent intent = new Intent(ShopListActivity.this,
				// SearchActivity.class);
				// intent.putExtra("search_type", SearchActivity.SEARCH_SHOP);
				// startActivity(intent);
			}
		});

		mTab = new MyTab(this);
		mTab.add(new TabObject(findViewById(R.id.tabText1),
				findViewById(R.id.below1), findViewById(R.id.tabRegion1)));
		mTab.add(new TabObject(findViewById(R.id.tabText2),
				findViewById(R.id.below2), findViewById(R.id.tabRegion2)));
		mTab.add(new TabObject(findViewById(R.id.tabText3),
				findViewById(R.id.below3), findViewById(R.id.tabRegion3)));

		findViewById(R.id.tabRegion1).setOnClickListener(this);
		findViewById(R.id.tabRegion2).setOnClickListener(this);
		findViewById(R.id.tabRegion3).setOnClickListener(this);

		mNoDataContainer = findViewById(R.id.noDataNote);
		mSearchBoxContainer = findViewById(R.id.search_box_container);
		mSearchWord = (EditText) findViewById(R.id.search_editor);

		mShopListView = (PullToRefreshListView) findViewById(R.id.shopList);
		mRecommentAdapter = new RecommendListAdapter(this, mShopList);
		mRecommentAdapter.SetDefaultImageId(R.drawable.photo_shops);
		mShopListView.setAdapter(mRecommentAdapter);
		mShopListView.setOnItemClickListener(mRecommentAdapter);
		// mShopListView.setOnItemClickListener(this);
		mShopListView.setOnLoadMoreListener(this);

		mShopListContainer = (LinearLayout) findViewById(R.id.listContainer);
		LayoutTransition transition = new LayoutTransition();
		transition.setAnimator(LayoutTransition.APPEARING,
				transition.getAnimator(LayoutTransition.APPEARING));
		transition.setAnimator(LayoutTransition.CHANGE_APPEARING,
				transition.getAnimator(LayoutTransition.CHANGE_APPEARING));
		mShopListContainer.setLayoutTransition(transition);
		onClick(findViewById(R.id.tabRegion2));
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.tabRegion1:
			mDiatance = WHOLE_COUNTRY;
			mShopList.clear();
			mRecommentAdapter.notifyDataSetChanged();
			mCurrentIndex = 1;
			fetchShopList();
			break;
		case R.id.tabRegion2:
			mDiatance = CURRENT_CITY;
			mShopList.clear();
			mRecommentAdapter.notifyDataSetChanged();
			mCurrentIndex = 1;
			fetchShopList();
			break;
		case R.id.tabRegion3:
			mDiatance = TEN_KM;
			mShopList.clear();
			mRecommentAdapter.notifyDataSetChanged();
			mCurrentIndex = 1;
			fetchShopList();
			break;
		}

		mTab.triggerTab(v.getId());
	}
	
	
	private void checkNoData() {
		if(mShopList.size() == 0) {
			mNoDataContainer.setVisibility(View.VISIBLE);
			mShopListView.setVisibility(View.INVISIBLE);
		} else {
			mNoDataContainer.setVisibility(View.INVISIBLE);
			mShopListView.setVisibility(View.VISIBLE);
		}
	}

	public void fetchShopList() {
		Param[] params = new Param[10];
		params[0] = new Param("UserId", Settings.instance().getUserId());
		params[1] = new Param("Token", Settings.instance().getToken());
		params[2] = new Param("Distance", String.valueOf(mDiatance));
		params[3] = new Param("PageSize", String.valueOf(DATA_LOAD_SIZE));
		params[4] = new Param("PageIndex", String.valueOf(mCurrentIndex));
		params[5] = new Param("Longitude", Settings.instance().getLontitude()
				.toString());
		params[6] = new Param("Latitude", Settings.instance().getLatitude()
				.toString());
		params[7] = new Param("CategoryId", mShopCategory);
		params[8] = new Param("CityCode", "");
		params[9] = new Param("Word", mSearchString);

		OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
				+ "/shop/searchshop", params,
				new OkHttpClientManager.ResultCallback<ResponseShopList>() {

					@Override
					public void onError(Request request, Exception e) {
						Logger.info("ShopFragment.fetchRecommendShops.onError"
								+ e.toString());
						checkNoData();
					}

					@Override
					public void onResponse(ResponseShopList response) {
						if (response.isSuccess()) {
							if (response.getShops().size() < DATA_LOAD_SIZE) {
								mShopListView.onLoadMoreComplete(true);
							} else {
								mShopListView.onLoadMoreComplete(false);
								mCurrentIndex++;
							}

							// 刷新列表
							mShopList.addAll(response.getShops());
							mRecommentAdapter.notifyDataSetChangedWithImages();
						}
						
						checkNoData();
					}

				});
	}

	@Override
	public void OnLoadMore() {
		fetchShopList();
	}
}
