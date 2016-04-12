package com.longhuapuxin.u5;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;

import com.longhuapuxin.adapter.ShowAlbumAdapter;
import com.longhuapuxin.common.AlbumHelper;
import com.longhuapuxin.db.bean.ImageBucket;
import com.longhuapuxin.db.bean.ImageItem;

public class ShowAlbumActivity extends BaseActivity implements OnClickListener {
	private GridView mImgGridView;
	private ArrayList<ImageItem> imgList;
	public static List<ImageBucket> imgBucketList;
	private AlbumHelper helper;
	private ShowAlbumAdapter imageAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_album);
		initDataList();
		initHeader();
		initFooter();
		initGridView();

	}

	private void initDataList() {
		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());

		imgBucketList = helper.getImagesBucketList(false);
		imgList = new ArrayList<ImageItem>();
		for (int i = 0; i < imgBucketList.size(); i++) {
			imgList.addAll(imgBucketList.get(i).imageList);
		}
	}

	private void initGridView() {
		mImgGridView = (GridView) findViewById(R.id.show_my_img_gridView);
		int srceenW = this.getWindowManager().getDefaultDisplay().getWidth();
		int screenH = this.getWindowManager().getDefaultDisplay().getHeight();
		imageAdapter = new ShowAlbumAdapter(this, imgList, srceenW);
		mImgGridView.setAdapter(imageAdapter);
		// mImgGridView.setOnScrollListener(onScrollListener);
		// initGridViewListener();
	}

	// OnScrollListener onScrollListener = new OnScrollListener() {
	//
	// @Override
	// public void onScrollStateChanged(AbsListView arg0, int arg1) {
	// switch (arg1) {
	// case OnScrollListener.SCROLL_STATE_FLING:
	// imageAdapter.setBusy(true);
	// break;
	// case OnScrollListener.SCROLL_STATE_IDLE:
	// imageAdapter.setBusy(false);
	// imageAdapter.notifyDataSetChanged();
	// break;
	// case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
	// imageAdapter.setBusy(false);
	// break;
	// }
	// }

	// @Override
	// public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
	//
	// }
	// };

	private void initFooter() {

	}

	private void initHeader() {
		initHeader("");
		enableRightTextBtn(R.string.albumAllPhoto, true, new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ShowAlbumActivity.this,
						ShowImgFileActivity.class);
				startActivity(intent);

			}
		});
	}

	@Override
	public void onClick(View v) {
	}
}
