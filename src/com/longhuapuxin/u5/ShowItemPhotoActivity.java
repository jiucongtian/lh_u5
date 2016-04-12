package com.longhuapuxin.u5;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;

import com.longhuapuxin.adapter.ShowItemPhototAdapter;
import com.longhuapuxin.db.bean.ImageBucket;

public class ShowItemPhotoActivity extends BaseActivity {
	private ImageBucket dataList;
	private GridView showItemPhotoGridView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_item_photo);
		initHeader("");
		initDataList();
		initGridView();
	}

	private void initGridView() {
		int srceenW = this.getWindowManager().getDefaultDisplay().getWidth();
		int screenH = this.getWindowManager().getDefaultDisplay().getHeight();
		showItemPhotoGridView = (GridView) findViewById(R.id.show_item_photo_gridView);
		ShowItemPhototAdapter showAllPhototAdapter = new ShowItemPhototAdapter(
				this, dataList, srceenW);
		showItemPhotoGridView.setAdapter(showAllPhototAdapter);
	}

	private void initDataList() {
		Intent intent = getIntent();
		dataList = (ImageBucket) intent.getSerializableExtra("dataList");
	}
}
