package com.longhuapuxin.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.longhuapuxin.db.bean.TransactionMsg;
import com.longhuapuxin.u5.CommentsActivity;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.u5.ShowCommentsActivity;

public class TransactionCompleteAdapter extends BaseAdapter {
	private List<TransactionMsg> orderListComplete;
	private Activity context;
	private LayoutInflater mInfalte;
	private boolean isOpen = false;

	public TransactionCompleteAdapter(Activity context,
			List<TransactionMsg> orderListComplete) {
		mInfalte = LayoutInflater.from(context);
		this.orderListComplete = orderListComplete;
		this.context = context;

	}

	@Override
	public int getCount() {
		return orderListComplete.size();
	}

	@Override
	public Object getItem(int arg0) {
		return orderListComplete.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		final TransactionMsg transactionMsg = orderListComplete.get(position);
		ViewHolder viewHolder = null;
		if (view == null) {
			viewHolder = new ViewHolder();
			view = mInfalte.inflate(R.layout.item_order_complete, null);
			viewHolder.time = (TextView) view.findViewById(R.id.time);
			viewHolder.name = (TextView) view.findViewById(R.id.name);
			viewHolder.content = (TextView) view.findViewById(R.id.content);
			viewHolder.evaluateTransaction = (RelativeLayout) view
					.findViewById(R.id.evaluate_transaction);
			viewHolder.commentBtn = (TextView) view.findViewById(R.id.commentBtn);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.time.setText(transactionMsg.getTime());
		viewHolder.name.setText(transactionMsg.getName());
		viewHolder.content.setText(transactionMsg.getNote());


		String feedBackId = transactionMsg.getFeedBackId();
		if (TextUtils.isEmpty(feedBackId) || Integer.valueOf(feedBackId) <= 0) {
			viewHolder.commentBtn.setText("评价");
		} else {
			viewHolder.commentBtn.setText("查看评价");
		}

		viewHolder.evaluateTransaction
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						String feedBackId = transactionMsg.getFeedBackId();
						if (TextUtils.isEmpty(feedBackId) || Integer.valueOf(feedBackId) <= 0) {
							Intent intent = new Intent(context, CommentsActivity.class);
							intent.putExtra("OrderType", "1");
							intent.putExtra("OrderId", String.valueOf(transactionMsg.getId()));
							intent.putExtra("UserName", transactionMsg.getName());

							intent.putExtra("OrderNote", transactionMsg.getNote());
							context.startActivityForResult(intent, 101);
						} else {
							Intent intent = new Intent(context, ShowCommentsActivity.class);
							intent.putExtra("OrderType", "1");
							intent.putExtra("FeedBackId", transactionMsg.getFeedBackId());
							context.startActivityForResult(intent, 101);
						}


					}
				});
		return view;
	}

	public class ViewHolder {
		private TextView time;
		private TextView name;
		private TextView content, commentBtn;
		private RelativeLayout evaluateTransaction;
	}

}
