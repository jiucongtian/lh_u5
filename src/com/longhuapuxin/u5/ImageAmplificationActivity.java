package com.longhuapuxin.u5;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;

public class ImageAmplificationActivity extends Activity {
	ViewPager imgViewPager;
	List<ImageView> imgContentList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_amplification);
		// intent得到数据
		getImgContent();
		// 放入listzhong
		initImgList();
		initViewPager();
	}

	private void initViewPager() {
		imgViewPager = (ViewPager) findViewById(R.id.imgViewPager);
	}

	private void initImgList() {
		imgContentList = new ArrayList<ImageView>();
	}

	private void getImgContent() {
		Intent intent = getIntent();
	}
}
