package com.longhuapuxin.adapter;

import java.util.ArrayList;
import java.util.List;

import com.lidroid.xutils.BitmapUtils;
import com.longhuapuxin.common.ImageUrlLoader;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.u5.Settings;
import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public abstract class U5BaseAdapter<T> extends U5FetchImageAdapter {
	protected BitmapUtils bmpUtils;
	protected Context mContext;
	protected LayoutInflater mInflater;
	protected List<T> mDatas;
	public int DefaultImageId=R.drawable.photo_error;

	public U5BaseAdapter(Context context, List<T> list) {
		super();
		bmpUtils=new BitmapUtils(context);

		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mDatas = list;
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void SetItems(List<T> items){
		mDatas=items;
	}

	//easy to get view via res id. no need to create holder any more.
	public <T extends View> T getAdapterView(View convertView, int id) {
		SparseArray<View> viewHolder = (SparseArray<View>) convertView.getTag();
		if (viewHolder == null) {
			viewHolder = new SparseArray<View>();
			convertView.setTag(viewHolder);
		}
		View childView = viewHolder.get(id);
		if (childView == null) {
			childView = convertView.findViewById(id);
			viewHolder.put(id, childView);
		}
		return (T) childView;
	}

	public void notifyDataSetChangedWithImages() {
		List<String> photoIdList = new ArrayList<String>();
		for (T item : mDatas) {
			String photos = getImageId(item);
			if(!TextUtils.isEmpty(photos)) {
				String[] list = photos.split(",");
				for(String photo : list) {
					photoIdList.add(photo);
				}
			}
		}
		ImageUrlLoader.fetchImageUrl(this, photoIdList);
	}

	public String getImageId(T item) {
		return "";
	}

	public void bindImageView(ImageView imageView, String imageId) {
		bindImageView(imageView, imageId, false);
	}

	public void bindImageView(ImageView imageView, String imageId, boolean useOrigin) {
		bmpUtils.configDefaultLoadingImage(DefaultImageId)
 		.configDefaultLoadFailedImage(DefaultImageId)
 		.configDefaultAutoRotation(true);
		if((mImageMap != null) && (imageId != null) && (imageId != "")) {

			String imagePath = null;
			if(mImageMap.get(imageId) != null) {
				imagePath = useOrigin? mImageMap.get(imageId).origin : mImageMap.get(imageId).small;
			}

			if((imagePath != null) && (imagePath != "")) {

				bmpUtils.display(imageView, Settings.instance().getImageUrl() + imagePath);
			}

		} else if(imageId == null || imageId == "null" || imageId == "") {
			imageView.setImageResource(DefaultImageId);
		}
	}
}

