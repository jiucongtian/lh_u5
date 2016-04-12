package com.longhuapuxin.u5;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.fragment.LabelListFragment;

public class SearchActivity extends BaseActivity implements OnClickListener {
	
	public static final int SEARCH_LABLE = 0x10;
	public static final int SEARCH_SHOP = 0x11;
	public static final int SEARCH_NONE = 0x12;
	private static final int CURRENT_CITY = -2;
	private static final int WHOLE_COUNTRY = -1;
	private static final int TEN_KM = 10;
	private EditText mSearchEditor;
	private MyTab mTab;
//	private View mTypeLayout;
	private View mSearchBtn;
	private int mDiatance;
	private LabelListFragment mLabelListFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		Logger.debug("SearchShopActivity->onCreate");
//		mSearchType = getIntent().getIntExtra("search_type", SEARCH_NONE);
//		mCurrentIndex = 1;
		
		init();
	}
	
	@SuppressLint("NewApi") private void init() {
		initHeader(R.string.shop_search);
		mLabelListFragment = (LabelListFragment) getFragmentManager().findFragmentById(R.id.label_list_fragment);
		
		mSearchBtn = findViewById(R.id.searchBtn);
		mSearchBtn.setOnClickListener(this);
		
		mTab = new MyTab(this);
		mTab.add(new TabObject(findViewById(R.id.tabText1),findViewById(R.id.below1),findViewById(R.id.tabRegion1)));
		mTab.add(new TabObject(findViewById(R.id.tabText2),findViewById(R.id.below2),findViewById(R.id.tabRegion2)));
		mTab.add(new TabObject(findViewById(R.id.tabText3),findViewById(R.id.below3),findViewById(R.id.tabRegion3)));
		
		findViewById(R.id.tabRegion1).setOnClickListener(this);
		findViewById(R.id.tabRegion2).setOnClickListener(this);
		findViewById(R.id.tabRegion3).setOnClickListener(this);

//		mTypeLayout = findViewById(R.id.type_layout);
//		mNoDataContainer = findViewById(R.id.noDataNote);
		// srarch editor.
		mSearchEditor = (EditText) findViewById(R.id.search_editor);
//		mSearchEditor.setFocusable(true);
//		mSearchEditor.setFocusableInTouchMode(true);
		mSearchEditor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					mSearchEditor.setText("");
				} else {
					if (mSearchEditor.getText().toString() == "") {
						mSearchEditor.setText(R.string.shop_search);
					}
				}
				
			}
		});
		
		// make sure the input method will be shown.
//	    Timer timer = new Timer();    
//		timer.schedule(new TimerTask() {
//
//			public void run()
//
//			{
//				mSearchEditor.requestFocus();
//				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//
//				inputManager.showSoftInput(mSearchEditor, 0);
//			}
//
//		}, 500);  
		
//		mTypeLayout.setVisibility(View.VISIBLE);
		onClick(findViewById(R.id.tabRegion2));
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
			
			for(TabObject object : mTabList) {
				if(object.mClickRegion.getId() == viewId) {
					((TextView)object.mText).setTextColor(mContext.getResources().getColor(R.color.orange));
					object.mBelow.setBackgroundColor(mContext.getResources().getColor(R.color.orange));
				} else {
					((TextView)object.mText).setTextColor(mContext.getResources().getColor(R.color.deep_gray));
					object.mBelow.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		//current city.
		case R.id.tabRegion1:
			mTab.triggerTab(v.getId());
			mDiatance = WHOLE_COUNTRY;
			mLabelListFragment.reFetchUserDate(mSearchEditor.getText().toString(), "", mDiatance);
			hideImm();
			break;
		case R.id.tabRegion2:
			mTab.triggerTab(v.getId());
			mDiatance = CURRENT_CITY;
			mLabelListFragment.reFetchUserDate(mSearchEditor.getText().toString(), "", mDiatance);
			hideImm();
			break;
		case R.id.tabRegion3:
			mTab.triggerTab(v.getId());
			mDiatance = TEN_KM;
			mLabelListFragment.reFetchUserDate(mSearchEditor.getText().toString(), "", mDiatance);
			hideImm();
			break;
		case R.id.searchBtn:
			mLabelListFragment.reFetchUserDate(mSearchEditor.getText().toString(), "", mDiatance);
			hideImm();
			break;
		}
	}
	
	public void hideImm() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
		imm.hideSoftInputFromWindow(mSearchEditor.getWindowToken(), 0);
	}
}
