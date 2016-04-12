package com.longhuapuxin.adapter;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.longhuapuxin.db.bean.BeanRecord;
import com.longhuapuxin.u5.R;

public class BillAdapter extends BaseAdapter {
    private List<BeanRecord> beanRecordList;
    private LayoutInflater inflater;
//    private DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public BillAdapter(Context context, List<BeanRecord> beanRecordList) {
        this.beanRecordList = beanRecordList;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return beanRecordList.size();
    }

    @Override
    public Object getItem(int position) {
        return beanRecordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.item_bill_list, null);
        }

        TextView time = (TextView) view.findViewById(R.id.time);
        TextView reason = (TextView) view.findViewById(R.id.reason);
        TextView id = (TextView) view.findViewById(R.id.id);
        TextView consume = (TextView) view.findViewById(R.id.consume);

        BeanRecord item = beanRecordList.get(position);
        time.setText(item.getRecordTime());
        reason.setText(item.getReason());
        consume.setText(String.valueOf(item.getBalancetChange1()));
        id.setText(item.getPaymentCode());
//        Double amount = item.getBalancetChange1() + item.getBalanceChange2();
//        if (amount > 0) {
//            txtAmount.setTextColor(txtAmount.getResources().getColor(
//                    R.color.dark_green));
//        } else {
//            txtAmount.setTextColor(txtAmount.getResources().getColor(
//                    R.color.orange));
//
//        }
//        txtAmount.setText(decimalFormat.format(amount));
        return view;
    }

    /**
     * 添加列表项
     *
     * @param item
     */
    public void addItem(BeanRecord item) {
        beanRecordList.add(item);

    }

    public void clearItem() {

        beanRecordList.clear();
    }

    public void Refresh() {
        notifyDataSetChanged();
    }
}
