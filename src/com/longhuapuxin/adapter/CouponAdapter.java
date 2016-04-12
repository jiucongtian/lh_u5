package com.longhuapuxin.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.longhuapuxin.db.bean.DiscountCoupon;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.u5.R.color;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

 

public class CouponAdapter extends BaseAdapter {
	public interface OnClickListner {
		public void OnClick(DiscountCoupon item);
	}
	private ArrayList<DiscountCoupon> items;
	private LayoutInflater inflater;
	private OnClickListner callback;
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
	private String FormatDate(String datetxt){
		Date date;
		try {
			date = sdformat.parse(datetxt);
			return sdformat.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return "";
		}
		
	}
	public CouponAdapter(Context context, ArrayList<DiscountCoupon> items,OnClickListner callback) {
		this.items = items;
		this.callback=callback;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("NewApi") @Override
	public View getView(int position, View view, ViewGroup parent) {
		DiscountCoupon item = items.get(position);
		if (view == null) {
			view = inflater.inflate(R.layout.item_coupon_list, null);
			 
		}
		Resources resource=view.getResources();
		TextView txtInUse=(TextView)view.findViewById(R.id.txtInUse);
		LinearLayout layout=(LinearLayout)view.findViewById(R.id.layout);
		LinearLayout layoutLeft=(LinearLayout)view.findViewById(R.id.layout_left);
		layout.setBackgroundColor(resource.getColor(color.orange));
		layoutLeft.setBackground(resource.getDrawable(R.drawable.layout_right_line));
		txtInUse.setVisibility(View.VISIBLE);
		
		TextView txtShopName = (TextView) view.findViewById(R.id.txtShopName);
		TextView txtTitle = (TextView) view.findViewById(R.id.txtTitle);
		 
		TextView txtActiveDate = (TextView) view
				.findViewById(R.id.txtActiveDate);
		TextView txtDueTo = (TextView) view.findViewById(R.id.txtDueTo);
		TextView txtNote = (TextView) view.findViewById(R.id.txtNote);
		TextView txtDiscount = (TextView) view.findViewById(R.id.txtDiscount);
		txtShopName.setText(item.getShopName());
		txtTitle.setText(item.getCouponName());
		txtNote.setText(item.getCouponNote());
		 
		if (item.getActiveDate() != null) {
			txtActiveDate.setText(FormatDate(item.getActiveDate()));
		}
		if (item.getDueTo()  != null && item.isHasDueTo()) {
			txtDueTo = (TextView) view.findViewById(R.id.txtDueTo);
			txtDueTo.setText(FormatDate(item.getDueTo()));
		}
		Double discount = (double) ((100 -Double.parseDouble( item.getDiscount())) / 10);
		txtDiscount.setText(discount.toString());
		
		txtInUse.setTag(item);
		txtInUse.setOnClickListener(listener);
		return view;
	}
	OnClickListener listener=new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			DiscountCoupon item=(DiscountCoupon)arg0.getTag();
			if(callback!=null){
				callback.OnClick(item);
			}
		}
		
	};
	/**
	 * 添加列表项
	 * 
	 * @param item
	 */
	public void addItem(DiscountCoupon item) {
		items.add(item);

	}

	public void clearItem() {

		items.clear();
	}

	public void Refresh() {
		notifyDataSetChanged();
	}

}