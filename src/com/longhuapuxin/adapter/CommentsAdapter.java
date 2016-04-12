package com.longhuapuxin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.longhuapuxin.common.Utils;
import com.longhuapuxin.db.bean.Estimation;
import com.longhuapuxin.u5.R;

import java.util.List;

/**
 * Created by lsy on 2016/3/14.
 */

public class CommentsAdapter extends U5BaseAdapter<Estimation>{
    public String getmFeedBackType() {
        return mFeedBackType;
    }

    public void setmFeedBackType(String mFeedBackType) {
        this.mFeedBackType = mFeedBackType;
    }

    private String mFeedBackType;
    public CommentsAdapter(Context context, List<Estimation> list) {
        super(context, list);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        Estimation item =  mDatas.get(i);

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_comment, viewGroup, false);
        }

        TextView nameTv = Utils.getAdapterView(convertView, R.id.nameTv);
        TextView noteTv = Utils.getAdapterView(convertView, R.id.noteTv);
        RatingBar summaryRb = Utils.getAdapterView(convertView, R.id.summaryRb);
        RatingBar serviceRb = Utils.getAdapterView(convertView, R.id.serviceRb);
        RatingBar priceRb = Utils.getAdapterView(convertView, R.id.priceRb);
        RatingBar skillRb = Utils.getAdapterView(convertView, R.id.skillRb);
        View professionContainer = Utils.getAdapterView(convertView, R.id.professionContainer);

        nameTv.setText(item.getNickName());
        noteTv.setText(item.getText());

        float service = Float.valueOf(item.getEstimate1());
        float price = Float.valueOf(item.getEstimate2());
        float profession = Float.valueOf(item.getEstimate3());

        serviceRb.setRating(service);
        priceRb.setRating(price);
        skillRb.setRating(profession);

        if(profession == 0) {
            summaryRb.setRating((service + price) / 2);
        } else {
            summaryRb.setRating((service + price + profession) / 3);
        }


        if(mFeedBackType.equals("1")) {
            professionContainer.setVisibility(View.VISIBLE);
        } else {
            professionContainer.setVisibility(View.GONE);
        }

        return convertView;

    }
}