package com.longhuapuxin.adapter;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.longhuapuxin.entity.ResponseGetConsumeList.Payment;
import com.longhuapuxin.u5.CommentsActivity;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.u5.ShopCommentsActivity;
import com.longhuapuxin.u5.ShowCommentsActivity;

public class PaymentAdapter extends BaseAdapter {
	private List<Payment> items;
	private LayoutInflater inflater;
	private Context mContext;

	public PaymentAdapter(Context context, List<Payment> items) {
		mContext = context;
		this.items = items;
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

	@SuppressLint("SimpleDateFormat")
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null) {
			view = inflater.inflate(R.layout.item_payment_list, null);
		}

		TextView txtShopName = (TextView) view.findViewById(R.id.txtShopName);
		TextView txtDate = (TextView) view.findViewById(R.id.txtDate);
		TextView txtStatus = (TextView) view.findViewById(R.id.txtStatus);
		TextView txtTotal = (TextView) view.findViewById(R.id.txtTotal);
		TextView txtDiscount = (TextView) view.findViewById(R.id.txtDiscount);
		TextView txtAmount = (TextView) view.findViewById(R.id.txtAmount);
		TextView go2Comments = (TextView) view.findViewById(R.id.go2CommentsTv);

		final Payment item = items.get(position);

		// txtDate.setText(new
		// SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(item
		// .getInsertDate()));
		txtDate.setText(item.getInsertDate());
		txtShopName.setText(item.getShopName());
		txtTotal.setText("消费￥:"
				+ new DecimalFormat("##0.00").format(item.getTotal()));

		if (item.getDiscount() > 0) {

			txtDiscount.setText("折扣:" + item.getDiscount().toString() + "%");

		} else {

			txtDiscount.setText("折扣:" + "无");
		}
		if (item.getSuccess()) {
			txtStatus.setText("已经付款");
			txtAmount.setTextColor(txtAmount.getResources().getColor(
					R.color.orange));
		} else {
			txtStatus.setText("尚未付款");
			txtAmount.setTextColor(txtAmount.getResources().getColor(
					R.color.gray));
		}

		txtAmount.setText(new DecimalFormat("##0.00").format(item.getAmount()));

		//此处已经有feedbackId
		if(item.getSuccess()) {
			if(TextUtils.isEmpty(item.getFeeBackId()) || Integer.valueOf(item.getFeeBackId()) <= 0) {
				go2Comments.setText("点击前往评价");
			} else {
				go2Comments.setText("查看评价");
			}

			go2Comments.setVisibility(View.VISIBLE);
			go2Comments.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {

					if(TextUtils.isEmpty(item.getFeeBackId()) || Integer.valueOf(item.getFeeBackId()) <= 0) {
						Intent intent = new Intent(mContext, ShopCommentsActivity.class);
						intent.putExtra("OrderType", "2");
						intent.putExtra("OrderId", item.getCode());
						intent.putExtra("UserName", item.getShopName());
						mContext.startActivity(intent);
					} else {
						Intent intent = new Intent(mContext, ShowCommentsActivity.class);
						intent.putExtra("OrderType", "2");
						intent.putExtra("FeedBackId", item.getFeeBackId());
						mContext.startActivity(intent);
					}


				}
			});
		} else {
			go2Comments.setVisibility(View.GONE);
		}

		return view;
	}

	/**
	 * 添加列表项
	 * 
	 * @param item
	 */
	public void addItem(Payment item) {
		items.add(item);

	}

	public void clearItem() {

		items.clear();
	}

	public void Refresh() {
		notifyDataSetChanged();
	}
}
