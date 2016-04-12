package com.longhuapuxin.u5;

import java.util.ArrayList;
import java.util.List;

import com.longhuapuxin.adapter.CouponAdapter;
import com.longhuapuxin.db.bean.DiscountCoupon;
import com.longhuapuxin.view.PullToLoadListView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

public class DiscountCouponActivity  extends BaseActivity{
	 
	private ListView mListView;
	private ArrayList<DiscountCoupon> items;
	private CouponAdapter mListAdapter;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discountcoupon);
		init() ;
		loadData();
		 
	}
	private void init() {
	 
		initHeader(R.string.profile_my_discount);

		mListView = (ListView) findViewById(R.id.listview);
		items = new ArrayList<DiscountCoupon>();

		mListAdapter = new CouponAdapter(this, items, new CouponAdapter.OnClickListner() {
			
			@Override
			public void OnClick(DiscountCoupon item) {
				// TODO Auto-generated method stub
				Intent  intent =new Intent(DiscountCouponActivity.this, GoPayActivity.class);
				intent.putExtra("discountTicketId", item.getCode());
				startActivity(intent);
			}
		});
		mListView.setAdapter(mListAdapter);
		
		 
		 
	}
	private void loadData() {
		 	List<DiscountCoupon> coupons=Settings.instance().User.getDiscountCoupons();
			for (int i = 0; i < coupons.size(); i++) {
				mListAdapter.addItem(coupons.get(i));
			}
			 
			 
			mListAdapter.Refresh();
			
	}
}
