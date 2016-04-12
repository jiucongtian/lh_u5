package com.longhuapuxin.adapter;

import java.util.List;

import com.longhuapuxin.common.Utils;
import com.longhuapuxin.entity.ResponseShopList;
import com.longhuapuxin.entity.ResponseShopList.Shop;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.u5.Settings;
import com.longhuapuxin.u5.ShopDetailActivity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class RecommendListAdapter extends U5BaseAdapter<Shop> implements OnItemClickListener {

    public RecommendListAdapter(Context context, List<Shop> list) {
        super(context, list);

    }

    public void SetDefaultImageId(int id) {
        super.DefaultImageId = id;
    }

    @Override
    public String getImageId(Shop item) {
        return item.getLogo();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Shop recommend = (Shop) mDatas.get(position);
        if (TextUtils.isEmpty(recommend.getName())) {
            recommend.setName("");
        }
        if (TextUtils.isEmpty(recommend.getAddress())) {
            recommend.setAddress("");
        }
        if (TextUtils.isEmpty(recommend.getZip())) {
            recommend.setZip("");
        }
        if (TextUtils.isEmpty(recommend.getPhone())) {
            recommend.setPhone("");
        }
        if (TextUtils.isEmpty(recommend.getCity())) {
            recommend.setCity("");
        }
        if ((recommend.getLongitude() == null) /*|| (recommend.getLongitude() == 0)*/ ) {
            recommend.setLongitude(0.0);
        }
        if (recommend.getLatitude() == null /*|| recommend.getLatitude() == 0*/) {
            recommend.setLatitude(0.0);
        }
        if (TextUtils.isEmpty(recommend.getLogo())) {
            recommend.setLogo("");
        }
        if (TextUtils.isEmpty(recommend.getDescription())) {
            recommend.setDescription("");
        }
        if (TextUtils.isEmpty(recommend.getWorkTime())) {
            recommend.setWorkTime("");
        }
        if (TextUtils.isEmpty(recommend.getPhotos())) {
            recommend.setPhone("");
        }

        ViewHolder viewHolder = null;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.item_recommend,
                    parent, false);
            viewHolder.imgView = (ImageView) convertView.findViewById(R.id.recommendImg);
            viewHolder.name = (TextView) convertView.findViewById(R.id.recommendName);
            viewHolder.parkMark = (ImageView) convertView.findViewById(R.id.stopMark);
            viewHolder.wifiMark = (ImageView) convertView.findViewById(R.id.wifiMark);
            viewHolder.duration = (TextView) convertView.findViewById(R.id.duration);
            viewHolder.distance = (TextView) convertView.findViewById(R.id.distance);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        bindImageView(viewHolder.imgView, recommend.getLogo());
        viewHolder.name.setText(recommend.getName());
        viewHolder.duration.setText(recommend.getWorkTime());
        Double shopLatitude = recommend.getLatitude();
        Double shopLongitude = recommend.getLongitude();
        Float myFloatLatitude = Settings.instance().getLatitude();
        Float myFlostLongitude = Settings.instance().getLontitude();
        Double myDoubleLatitude = Double.parseDouble(Float.toString(myFloatLatitude));
        Double myDoubleLongitude = Double.parseDouble(Float.toString(myFlostLongitude));
        String distance = null;
        if (shopLatitude == 0.0 || shopLongitude == 0.0) {

            distance = "未知";
        } else {
            distance = Utils.getStrDistance(myDoubleLongitude, myDoubleLatitude, shopLongitude, shopLatitude);

        }
        viewHolder.distance.setText(String.format(mContext.getString(R.string.shop_list_diatance), distance));

        if (recommend.getHasWIFI()) {
            viewHolder.wifiMark.setImageResource(R.drawable.shop_content_kou);
        } else {
            viewHolder.wifiMark.setImageResource(R.drawable.shop_content_bad);
        }
        if (recommend.getHasParking()) {
            viewHolder.parkMark.setImageResource(R.drawable.shop_content_kou);
        } else {
            viewHolder.parkMark.setImageResource(R.drawable.shop_content_bad);
        }

        return convertView;

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Intent intent = new Intent(mContext,
                ShopDetailActivity.class);

        Shop shop = (Shop) parent.getAdapter().getItem(position);
        if(shop != null) {
            intent.putExtra("ShopCode", shop.getCode());
            intent.putExtra("ShopName", shop.getName());
            mContext.startActivity(intent);
        }
    }

    private class ViewHolder {
        public ImageView imgView;
        public TextView name;
        public ImageView parkMark;
        public ImageView wifiMark;
        public TextView duration;
        public TextView distance;
    }
}
