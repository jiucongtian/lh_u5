package com.longhuapuxin.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.longhuapuxin.common.ImageUrlLoader;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.common.Utils;
import com.longhuapuxin.entity.ResponseGetAccount;
import com.longhuapuxin.entity.ResponseGetAccount.User.CareWhichShop;
import com.longhuapuxin.entity.ResponseGetAccount.User.CareWho;
import com.longhuapuxin.entity.ResponseShop;
import com.longhuapuxin.entity.ResponseShopList.Shop;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.u5.Settings;
import com.longhuapuxin.u5.ShopDetailActivity;
import com.squareup.okhttp.Request;

public class ShopsListAdapter extends U5BaseAdapter<CareWhichShop> implements
		OnItemClickListener {
	private List<String> idList = new ArrayList<String>();
	private List<CareWhichShop> list = null;

	public ShopsListAdapter(Context context, List<CareWhichShop> list) {
		super(context, list);
		this.list = list;
		notifyDataSetChangedWithImages();
//		setIdList();
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public String getImageId(CareWhichShop item) {
		return item.getShopLogo();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CareWhichShop careWhichShop = list.get(position);
		ViewHolder viewHolder = null;

		if (convertView == null) {
			viewHolder = new ViewHolder();

			convertView = mInflater.inflate(R.layout.item_recommend, parent,
					false);
			viewHolder.imgView = (ImageView) convertView
					.findViewById(R.id.recommendImg);
			viewHolder.name = (TextView) convertView
					.findViewById(R.id.recommendName);
			viewHolder.parkMark = (ImageView) convertView
					.findViewById(R.id.stopMark);
			viewHolder.wifiMark = (ImageView) convertView
					.findViewById(R.id.wifiMark);
			viewHolder.duration = (TextView) convertView
					.findViewById(R.id.duration);
			viewHolder.distance = (TextView) convertView
					.findViewById(R.id.distance);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		bindImageView(viewHolder.imgView, careWhichShop.getShopLogo());

		viewHolder.name.setText(careWhichShop.getName());
		viewHolder.duration.setText(careWhichShop.getWorkTime());
		Double shopLatitude = careWhichShop.getLatitude();
		Double shopLongitude = careWhichShop.getLongitude();
		if (shopLatitude != null && shopLongitude != null) {
			Float myFloatLatitude = Settings.instance().getLatitude();
			Float myFlostLongitude = Settings.instance().getLontitude();
			Double myDoubleLatitude = Double.parseDouble(Float
					.toString(myFloatLatitude));
			Double myDoubleLongitude = Double.parseDouble(Float
					.toString(myFlostLongitude));

			String distance = Utils.getStrDistance(myDoubleLongitude,
					myDoubleLatitude, shopLongitude, shopLatitude);
			viewHolder.distance.setText(String.format(
					mContext.getString(R.string.shop_list_diatance), distance));
		} else {
			viewHolder.distance.setText("未知");
		}

		if (careWhichShop.isHasWIFI()) {
			viewHolder.wifiMark.setImageResource(R.drawable.shop_content_kou);
		} else {
			viewHolder.wifiMark.setImageResource(R.drawable.shop_content_bad);
		}
		if (careWhichShop.isHasParking()) {
			viewHolder.parkMark.setImageResource(R.drawable.shop_content_kou);
		} else {
			viewHolder.parkMark.setImageResource(R.drawable.shop_content_bad);
		}

		return convertView;

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(mContext, ShopDetailActivity.class);

		Shop shop = (Shop) parent.getAdapter().getItem(position);

		intent.putExtra("ShopCode", shop.getCode());
		intent.putExtra("ShopName", shop.getName());
		mContext.startActivity(intent);
	}

	private class ViewHolder {
		public ImageView imgView;
		public TextView name;
		public ImageView parkMark;
		public ImageView wifiMark;
		public TextView duration;
		public TextView distance;
	}

	public void httpRequsetShop(String Code) {
		Param[] params = new Param[3];
		params[0] = new Param("UserId", Settings.instance().getUserId());
		params[1] = new Param("Token", Settings.instance().getToken());
		params[2] = new Param("ShopCode", Code);

		OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
				+ "/shop/shop", params,
				new OkHttpClientManager.ResultCallback<ResponseShop>() {

					@Override
					public void onError(Request request, Exception e) {
						Logger.info("ContactShopFragment.httpRequsetShop.onError"
								+ e.toString());
					}

					@Override
					public void onResponse(ResponseShop response) {
						if (response.isSuccess()) {
						} else {
//							Log.d("",
//									"---------httpRequsetShop"
//											+ response.getErrorCode());
//							Log.d("",
//									"---------httpRequsetShop"
//											+ response.getErrorMessage());
							Logger.info("---------httpRequsetShop"
									+ response.getErrorCode());
							Logger.info("---------httpRequsetShop"
									+ response.getErrorMessage());
						}

					}

				});
	}

//	private void setIdList() {
//		for (CareWhichShop careWhichShop : list) {
//			idList.add(careWhichShop.getShopLogo());
//		}
//		ImageUrlLoader.fetchImageUrl(this, idList);
//	}

	public void updateListView(List<CareWhichShop> list) {
		this.list = list;
		notifyDataSetChanged();
	}

}
