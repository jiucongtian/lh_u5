package com.longhuapuxin.u5;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import com.longhuapuxin.fragment.LabelListFragment;

public class LabelListActivity extends BaseActivity {

	private LabelListFragment mLabelListFragment;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_label_list);

		initHeader(R.string.label_title);
		mLabelListFragment = (LabelListFragment) getFragmentManager()
				.findFragmentById(R.id.label_list_fragment);

		// 获取标签名称
		Intent intent = getIntent();
		String labelName = intent.getStringExtra("labelName");
		mLabelListFragment.reFetchUserDate("", labelName, -1);
	}
}
