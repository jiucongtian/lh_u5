package com.longhuapuxin.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.lidroid.xutils.BitmapUtils;
import com.longhuapuxin.common.DensityUtil;
import com.longhuapuxin.db.bean.ImageBucket;
import com.longhuapuxin.db.bean.ImageItem;
import com.longhuapuxin.u5.ClipActivity;
import com.longhuapuxin.u5.R;

public class ShowItemPhototAdapter extends BaseAdapter {
	private ImageBucket dataList;
	private LayoutInflater mInfalte;
	private Context mContext;
	private BitmapUtils bitmapUtils;
	private int srceenW;

	public ShowItemPhototAdapter(Context context, ImageBucket dataList,
			int srceenW) {
		mInfalte = LayoutInflater.from(context);
		this.srceenW = srceenW;
		this.dataList = dataList;
		this.mContext = context;
		bitmapUtils = new BitmapUtils(mContext);
	}

	@Override
	public int getCount() {
		return dataList.imageList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.imageList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInfalte.inflate(R.layout.item_item_photo_adapter,
					null);
			viewHolder.imageView = (ImageView) convertView
					.findViewById(R.id.image_item);
			android.widget.RelativeLayout.LayoutParams imageViewLayoutParams = (android.widget.RelativeLayout.LayoutParams) viewHolder.imageView
					.getLayoutParams();

			imageViewLayoutParams.height = srceenW / 3
					- DensityUtil.px2dip(mContext, 20);
			viewHolder.imageView.setLayoutParams(imageViewLayoutParams);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final ImageItem item = dataList.imageList.get(position);
		viewHolder.imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ClipActivity.class);
				intent.putExtra("path", item.imagePath);
				intent.putExtra("type", "gallery");
				mContext.startActivity(intent);
			}
		});
		bitmapUtils.configDefaultLoadingImage(R.drawable.camera_no_pictures);
		bitmapUtils.configDefaultAutoRotation(true);
		bitmapUtils.display(viewHolder.imageView, item.imagePath);

		return convertView;
	}

	private class ViewHolder {
		public ImageView imageView;
	}

}
