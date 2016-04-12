package com.longhuapuxin.u5;

import android.os.Bundle;

public class CommentActivity extends BaseActivity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);
		init();
	}

	private void init() {
		initHeader(R.string.title_comment);		
	}
}
