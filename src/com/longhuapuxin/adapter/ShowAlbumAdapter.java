package com.longhuapuxin.adapter;

import java.util.ArrayList;

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
import com.longhuapuxin.db.bean.ImageItem;
import com.longhuapuxin.u5.ClipActivity;
import com.longhuapuxin.u5.R;

public class ShowAlbumAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<ImageItem> imgList;
	private BitmapUtils bitmapUtils;
	private int screemW;

	public ShowAlbumAdapter(Context c, ArrayList<ImageItem> imgList, int screemW) {
		mContext = c;
		this.imgList = imgList;
		this.screemW = screemW;
		bitmapUtils = new BitmapUtils(mContext);
	}

	@Override
	public int getCount() {
		return imgList.size();
	}

	@Override
	public Object getItem(int position) {
		return imgList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private class ViewHolder {
		public ImageView imageView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_album_adapter, null);
			viewHolder.imageView = (ImageView) convertView
					.findViewById(R.id.image_item);
			android.widget.RelativeLayout.LayoutParams imageViewLayoutParams = (android.widget.RelativeLayout.LayoutParams) viewHolder.imageView
					.getLayoutParams();

			imageViewLayoutParams.height = screemW / 3
					- DensityUtil.px2dip(mContext, 20);
			viewHolder.imageView.setLayoutParams(imageViewLayoutParams);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		String path;
		if (imgList != null && imgList.size() > position)
			path = imgList.get(position).imagePath;
		else {
			path = "camera_default";
		}
		if (path.contains("camera_default")) {
			viewHolder.imageView
					.setImageResource(R.drawable.camera_no_pictures);
		} else {
			final ImageItem item = imgList.get(position);
			viewHolder.imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, ClipActivity.class);
					intent.putExtra("path", item.imagePath);
					intent.putExtra("type", "gallery");
					mContext.startActivity(intent);
				}
			});
			bitmapUtils
					.configDefaultLoadingImage(R.drawable.camera_no_pictures);
			bitmapUtils.configDefaultAutoRotation(true);
			bitmapUtils.display(viewHolder.imageView, item.imagePath);
		}

		return convertView;
	}

}
