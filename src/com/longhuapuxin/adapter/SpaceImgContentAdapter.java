package com.longhuapuxin.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class SpaceImgContentAdapter extends PagerAdapter {
	List<ImageView> imgContentList;

	public SpaceImgContentAdapter(List<ImageView> imgContentList) {
		this.imgContentList = imgContentList;
	}

	@Override
	public int getCount() {
		return imgContentList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ImageView imgContent = imgContentList.get(position);
		imgContent.setScaleType(ImageView.ScaleType.FIT_CENTER);
		container.addView(imgContent);
		return imgContent;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(imgContentList.get(position));
	}
}
