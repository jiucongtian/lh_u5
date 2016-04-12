package com.longhuapuxin.u5;

import android.os.Bundle;
import android.widget.GridView;

import com.longhuapuxin.adapter.ShowImgFileAdapter;

public class ShowImgFileActivity extends BaseActivity {
	GridView imgFileGridView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_img_file);
		initHeader("");
		initGridView();
	}

	private void initGridView() {
		imgFileGridView = (GridView) findViewById(R.id.img_file_gridveiw);
		ShowImgFileAdapter showImgFileAdapter = new ShowImgFileAdapter(this);
		imgFileGridView.setAdapter(showImgFileAdapter);
	}

}
