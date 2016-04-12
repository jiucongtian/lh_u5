package com.longhuapuxin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.longhuapuxin.dao.MyAddressDao;
import com.longhuapuxin.db.bean.MyAddress;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.view.SlideView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2016/1/6.
 */
public class AddressAdapter extends BaseAdapter implements View.OnClickListener {
    private List<MyAddress> addresses;
    private Context mContext;
    private LayoutInflater mInflate;
    private List<Integer> deleteIds;
    private List<SlideView> slideViewList;

    public AddressAdapter(Context context, List<MyAddress> addresses) {
        this.addresses = addresses;
        this.mContext = context;
        mInflate = LayoutInflater.from(context);
        deleteIds = new ArrayList<Integer>();
        slideViewList = new ArrayList<SlideView>();
    }

    @Override
    public int getCount() {
        return addresses.size();
    }

    @Override
    public Object getItem(int i) {
        return addresses.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        SlideView slideView = (SlideView) convertView;
        if (slideView == null) {
            View itemView = mInflate.inflate(R.layout.item_my_address,
                    null);
            slideView = new SlideView(mContext);
            slideViewList.add(slideView);
            slideView.setContentView(itemView);

            holder = new ViewHolder(slideView);
            slideView.setTag(holder);
        } else {
            holder = (ViewHolder) slideView.getTag();
        }
        MyAddress item = addresses.get(position);
        holder.deleteHolder.setId(position);
        if (!deleteIds.contains(position)) {
            deleteIds.add(position);
        }
        holder.defaultTv.setVisibility(View.GONE);
        holder.defaultImg.setVisibility(View.INVISIBLE);
        holder.name.setTextColor(mContext.getResources().getColor(R.color.black));
        holder.phone.setTextColor(mContext.getResources().getColor(R.color.black));
        holder.defaultTv.setTextColor(mContext.getResources().getColor(R.color.black));
        holder.address.setTextColor(mContext.getResources().getColor(R.color.black));
        holder.parentLayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        if (item.isDefault()) {
            holder.defaultTv.setVisibility(View.VISIBLE);
            holder.defaultImg.setVisibility(View.VISIBLE);
            holder.name.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.phone.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.defaultTv.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.address.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.parentLayout.setBackgroundColor(mContext.getResources().getColor(R.color.shallow_gray));
        }
        holder.deleteHolder.setOnClickListener(this);
        holder.name.setText(item.getName());
        holder.phone.setText(item.getPhone());
        holder.address.setText(item.getAddress() + item.getDetailAddress());
        return slideView;
    }

    public class ViewHolder {
        TextView name, phone, defaultTv, address;
        RelativeLayout defaultImg;
        ViewGroup deleteHolder;
        LinearLayout parentLayout;

        ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.name);
            phone = (TextView) view.findViewById(R.id.phone);
            defaultTv = (TextView) view.findViewById(R.id.default_tv);
            address = (TextView) view.findViewById(R.id.address);
            defaultImg = (RelativeLayout) view.findViewById(R.id.default_img);
            deleteHolder = (ViewGroup) view.findViewById(R.id.holder);
            parentLayout = (LinearLayout) view.findViewById(R.id.parent_layout);
        }
    }

    @Override
    public void onClick(View v) {
        for (Integer position : deleteIds) {
            if (v.getId() == position) {
                MyAddress myAddress = addresses.get(position);
                addresses.remove(myAddress);
                deleteMyAddressDB(myAddress);
                closeSlideView(myAddress, position);
                notifyDataSetChanged();

            }
        }
    }

    private void closeSlideView(MyAddress myAddress, int position) {
        SlideView slideView = slideViewList.get(position);
        slideView.shrink();
    }

    private void deleteMyAddressDB(MyAddress myAddress) {
        MyAddressDao myAddressHelper = new MyAddressDao(mContext);
        myAddressHelper.delete(myAddress);
    }

    public void refresh(List<MyAddress> addresses) {
        this.addresses = addresses;
        notifyDataSetChanged();
    }

    public void closeAllSlideView() {
        for (SlideView slideView : slideViewList) {
            if (slideView.getScrollX() > 0) {
                slideView.shrink();
            }
        }
    }
}
