package com.longhuapuxin.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.longhuapuxin.entity.ResponseGetOrderList.Order;
import com.longhuapuxin.u5.AgreeTransactionActivity;
import com.longhuapuxin.u5.AgreementActivity;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.u5.Settings;
import com.longhuapuxin.uppay.UpPayBaseActivity;

public class TransactionUnCompleteAdapter extends BaseAdapter {
    private List<Order> orderListUnComplete;
    private Context context;
    private LayoutInflater mInfalte;
    private static final int START_ACTIVITY_REQUEST = 1;
    public static final int REQUEST_PAY = 2000;

    public TransactionUnCompleteAdapter(Context context,
                                        List<Order> orderListUnComplete) {
        mInfalte = LayoutInflater.from(context);
        this.orderListUnComplete = orderListUnComplete;
        this.context = context;
    }

    @Override
    public int getCount() {
        return orderListUnComplete.size();
    }

    @Override
    public Object getItem(int arg0) {
        return orderListUnComplete.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = mInfalte.inflate(R.layout.item_order_uncomplete, null);
            viewHolder.time = (TextView) view.findViewById(R.id.time);
            viewHolder.name = (TextView) view.findViewById(R.id.name);
            viewHolder.content = (TextView) view.findViewById(R.id.content);
            viewHolder.acceptTransaction = (TextView) view
                    .findViewById(R.id.accept_transaction);
            viewHolder.payTransaction = (TextView) view
                    .findViewById(R.id.pay_transaction);
            viewHolder.payStatus = (TextView) view
                    .findViewById(R.id.pay_status);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.acceptTransaction.setVisibility(View.GONE);
        viewHolder.payTransaction.setVisibility(View.GONE);
        viewHolder.payStatus.setVisibility(View.GONE);
        int myId = Integer.valueOf(Settings.instance().User.getId());
        if (orderListUnComplete.get(position).getPaymentStatus().equals("0")) {
            if (orderListUnComplete.get(position).getUserId1() == myId) {
                viewHolder.payTransaction.setVisibility(View.VISIBLE);
            } else {
                viewHolder.payStatus.setVisibility(View.VISIBLE);
                viewHolder.payStatus.setText("未付款");
            }

        } else if (orderListUnComplete.get(position).getPaymentStatus().equals("1")) {
            if (orderListUnComplete.get(position).getUserId1() == myId) {
                viewHolder.acceptTransaction.setVisibility(View.VISIBLE);
            } else {
                viewHolder.payStatus.setVisibility(View.VISIBLE);
                viewHolder.payStatus.setText("已付款");
            }
        }

        viewHolder.time.setText(orderListUnComplete.get(position)
                .getOrderTime().toString());
        viewHolder.name.setText(orderListUnComplete.get(position)
                .getNickName1());
        viewHolder.content.setText(orderListUnComplete.get(position)
                .getOrderNote());
        viewHolder.acceptTransaction.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,
                        AgreeTransactionActivity.class);
                intent.putExtra("name", orderListUnComplete.get(position)
                        .getNickName1());
                intent.putExtra("note", orderListUnComplete.get(position)
                        .getOrderNote());
                intent.putExtra("orderId", orderListUnComplete.get(position)
                        .getId());
                if (startActivityForResultListener != null) {
                    startActivityForResultListener.mStartActivity(intent, START_ACTIVITY_REQUEST, position);
                }
            }
        });
        viewHolder.payTransaction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UpPayBaseActivity.class);
//                intent.putExtra("money", orderListUnComplete.get(position).getTotal());
                intent.putExtra("orderType", "1");
                intent.putExtra("orderId", "" + orderListUnComplete.get(position).getId());
                intent.putExtra("money", "" + orderListUnComplete.get(position).getTotal());
                if (startActivityForResultListener != null) {
                    startActivityForResultListener.mStartActivity(intent, REQUEST_PAY, position);
                }
            }
        });
        return view;
    }

    public class ViewHolder {
        TextView time;
        TextView name;
        TextView content;
        TextView acceptTransaction, payTransaction, payStatus;
    }

    public interface StartActivityForResultListener {
        public void mStartActivity(Intent intent, int result, int position);
    }

    private StartActivityForResultListener startActivityForResultListener;

    public void setStartActivityForResultListener(
            StartActivityForResultListener startActivityForResultListener) {
        this.startActivityForResultListener = startActivityForResultListener;
    }
}
