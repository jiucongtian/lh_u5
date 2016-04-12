package com.longhuapuxin.adapter;

import java.util.Map;
import android.widget.BaseAdapter;

import com.longhuapuxin.db.bean.ImagePath;

public abstract class U5FetchImageAdapter extends BaseAdapter {

	protected Map<String, ImagePath> mImageMap;

	public void setmImageMap(Map<String, ImagePath> mImageMap) {
		this.mImageMap = mImageMap;
	}
}
