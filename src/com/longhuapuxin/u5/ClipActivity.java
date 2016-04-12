package com.longhuapuxin.u5;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.longhuapuxin.common.ImageTools;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.view.ClipImageLayout;

public class ClipActivity extends BaseActivity {
	private ClipImageLayout mClipImageLayout;
	private String path;
	private String type;

	// private ProgressDialog loadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clip);
		// 这步必须要加
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		initHeader("移动和缩放");
		// loadingDialog = new ProgressDialog(this);
		// loadingDialog.setTitle("请稍后...");
		path = getIntent().getStringExtra("path");
		type = getIntent().getStringExtra("type");
		// || !(new File(path).exists())
		if (TextUtils.isEmpty(path)) {
			Toast.makeText(this, "图片加载失败", Toast.LENGTH_SHORT).show();
			return;
		}
		Bitmap bitmap = ImageTools.convertToBitmap(path, 600, 600);
		if (bitmap == null) {
			Toast.makeText(this, "图片加载失败", Toast.LENGTH_SHORT).show();
			return;
		}
		mClipImageLayout = (ClipImageLayout) findViewById(R.id.id_clipImageLayout);
		// bitmapUtils.configDefaultAutoRotation(true);
		// bitmapUtils.display(mClipImageLayout, item.imagePath);
		mClipImageLayout.setBitmap(bitmap);
		((Button) findViewById(R.id.id_action_clip))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// loadingDialog.show();
						WaitDialog.instance().showWaitNote(ClipActivity.this);
						if (type.equals("gallery")) {
							Bitmap bitmap = mClipImageLayout.clip();
							String path = Environment
									.getExternalStorageDirectory()
									+ "/U5/tmp_pic/"
									+ System.currentTimeMillis() + ".jpg";
							ImageTools.savePhotoToSDCard(bitmap, path);
							Intent intent = new Intent(ClipActivity.this,
									MyProfileActiviy.class);
							intent.putExtra("type", "gallery");
							intent.putExtra("path", path);
							startActivity(intent);
							// loadingDialog.dismiss();
							WaitDialog.instance().hideWaitNote();
						} else {
							new Thread(new Runnable() {
								@Override
								public void run() {
									Bitmap bitmap = mClipImageLayout.clip();
									String path = Environment
											.getExternalStorageDirectory()
											+ "/U5/tmp_pic/"
											+ System.currentTimeMillis()
											+ ".jpg";
									ImageTools.savePhotoToSDCard(bitmap, path);
									// loadingDialog.dismiss();
									WaitDialog.instance().hideWaitNote();
									Intent intent = new Intent();
									intent.putExtra("path", path);
									setResult(RESULT_OK, intent);
									finish();
								}
							}).start();
						}
					}
				});
	}

}
