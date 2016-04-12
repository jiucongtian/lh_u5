package com.longhuapuxin.adapter;

import java.io.Serializable;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.longhuapuxin.db.bean.ImageItem;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.u5.ShowAlbumActivity;
import com.longhuapuxin.u5.ShowItemPhotoActivity;

public class ShowImgFileAdapter extends BaseAdapter implements OnClickListener {
	private Context context;
	private BitmapUtils bitmapUtils;

	public ShowImgFileAdapter(Context context) {
		this.context = context;
		bitmapUtils = new BitmapUtils(context);
	}

	@Override
	public int getCount() {
		return ShowAlbumActivity.imgBucketList.size();
	}

	@Override
	public Object getItem(int position) {
		return ShowAlbumActivity.imgBucketList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_img_file_adapter, null);
			holder = new ViewHolder();
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.img_file);
			holder.fileName = (TextView) convertView
					.findViewById(R.id.file_name);
			holder.fileNum = (TextView) convertView.findViewById(R.id.file_num);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String path;
		path = ShowAlbumActivity.imgBucketList.get(position).imageList.get(0).imagePath;
		// 给folderName设置值为文件夹名称
		holder.fileName
				.setText(ShowAlbumActivity.imgBucketList.get(position).bucketName);

		// 给fileNum设置文件夹内图片数量
		holder.fileNum.setText(""
				+ ShowAlbumActivity.imgBucketList.get(position).count);
		holder.imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, ShowItemPhotoActivity.class);
				intent.putExtra("dataList",
						(Serializable) ShowAlbumActivity.imgBucketList
								.get(position));
				context.startActivity(intent);
			}
		});
		ImageItem item = ShowAlbumActivity.imgBucketList.get(position).imageList
				.get(0);
		bitmapUtils.configDefaultLoadingImage(R.drawable.camera_no_pictures);
		bitmapUtils.configDefaultAutoRotation(true);
		bitmapUtils.display(holder.imageView, item.imagePath);
		return convertView;
	}

	public class ViewHolder {
		public ImageView imageView;
		public TextView fileName;
		public TextView fileNum;
	}

	@Override
	public void onClick(View v) {
	}
}
