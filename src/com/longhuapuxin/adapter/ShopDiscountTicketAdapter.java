package com.longhuapuxin.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.longhuapuxin.entity.ResponseShop;
import com.longhuapuxin.u5.GoPayActivity;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.u5.Settings;

public class ShopDiscountTicketAdapter extends BaseAdapter implements
		OnClickListener {
	private ResponseShop responseShop;
	private LayoutInflater inflater;
	private List<Integer> getDiscountList;
	private List<Integer> goPayList;
	private HashMap<Integer, TextView> getDiscountMap;
	private HashMap<Integer, TextView> goPayMap;
	private Context context;

	public ShopDiscountTicketAdapter(ResponseShop responseShop, Context context) {
		this.responseShop = responseShop;
		this.context = context;
		inflater = LayoutInflater.from(context);
		getDiscountList = new ArrayList<Integer>();
		goPayList = new ArrayList<Integer>();
		getDiscountMap = new HashMap<Integer, TextView>();
		goPayMap = new HashMap<Integer, TextView>();
	}

	@Override
	public int getCount() {
		return 0;
//		return responseShop.getShop().getCoupons().size();
	}

	@Override
	public Object getItem(int position) {
//		return responseShop.getShop().getCoupons().get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_shop_discount_ticket,
					null);
			holder = new ViewHolder();
			holder.discount_ticket_name = (TextView) convertView
					.findViewById(R.id.discount_ticket_name);
			holder.discount_ticket_description = (TextView) convertView
					.findViewById(R.id.discount_ticket_description);
			holder.discount_ticket_time_start = (TextView) convertView
					.findViewById(R.id.discount_ticket_time_start);
			holder.discount_ticket_time_end = (TextView) convertView
					.findViewById(R.id.discount_ticket_time_end);
			holder.discount = (TextView) convertView
					.findViewById(R.id.discount);
			holder.get_discount = (TextView) convertView
					.findViewById(R.id.get_discount);
			holder.go_pay = (TextView) convertView.findViewById(R.id.go_pay);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// 设置值
//		holder.discount_ticket_name.setText(responseShop.getShop().getCoupons()
//				.get(position).getName());
//		holder.discount_ticket_description.setText(responseShop.getShop()
//				.getCoupons().get(position).getNote());
//		holder.discount_ticket_time_start.setText(responseShop.getShop()
//				.getCoupons().get(position).getActiveDate());
//		holder.discount_ticket_time_end.setText(responseShop.getShop()
//				.getCoupons().get(position).getDueTo());
//		holder.discount.setText(responseShop.getShop().getCoupons()
//				.get(position).getDiscount());
		//
		holder.get_discount.setId(position);
		holder.get_discount.setOnClickListener(this);
		if (!getDiscountList.contains(position)) {
			getDiscountList.add(position);
		}
		getDiscountMap.put(position, holder.get_discount);
		//
		holder.go_pay.setId(getCount() + position);
		holder.go_pay.setOnClickListener(this);
		if (!goPayList.contains(getCount() + position)) {
			goPayList.add(getCount() + position);
		}
		goPayMap.put(getCount() + position, holder.go_pay);
		// 设置按钮
//		for (String myDiscountId : Settings.instance().getDiscountId()) {
//			if (myDiscountId == responseShop.getShop().getCoupons()
//					.get(position).getCode()) {
//				getDiscountMap.get(position).setVisibility(View.GONE);
//				goPayMap.get(getCount() + position).setVisibility(View.VISIBLE);
//			}
//		}

		return convertView;
	}

	class ViewHolder {
		TextView discount_ticket_name;
		TextView discount_ticket_description;
		TextView discount_ticket_time_start;
		TextView discount_ticket_time_end;
		TextView discount;
		TextView get_discount;
		TextView go_pay;
	}

	@Override
	public void onClick(View v) {
		for (Integer position : getDiscountList) {
			if (v.getId() == position) {
				getDiscountMap.get(position).setVisibility(View.GONE);
				goPayMap.get(getCount() + position).setVisibility(View.VISIBLE);
			}
		}
		for (Integer position : goPayList) {
			if (v.getId() == position) {
				//
				Intent intent = new Intent(context, GoPayActivity.class);
//				intent.putExtra("discountTicketId", responseShop.getShop()
//						.getCoupons().get(position - getCount()).getCode());
				context.startActivity(intent);
			}
		}
	}
}
