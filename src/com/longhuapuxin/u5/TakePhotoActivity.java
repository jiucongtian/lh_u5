package com.longhuapuxin.u5;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

public class TakePhotoActivity extends Activity {
	private Preview preview;
	private ImageView ivFocus;
	private int angle = 0;

	private PictureCallback pictureCallback = new PictureCallback() {
		// 该方法用于处理拍摄后的照片数据
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			camera.stopPreview();
			// data参数值就是照片数据，将这些数据以key-value形式保存，以便其他调用该Activity的程序可
			// 以获得照片数据
			String fileName = String.valueOf(Math.random()) + ".jpg";
			try {

				Bitmap img = BitmapFactory
						.decodeByteArray(data, 0, data.length);
				int width = img.getWidth();
				int height = img.getHeight();
				float scale = 1;
				if (width > Settings.MaxImageSize
						|| height > Settings.MaxImageSize) {
					if (width > height) {
						scale = ((float) Settings.MaxImageSize) / width;
						// height=height*2000/width;
						// width=2000;
					} else {
						scale = ((float) Settings.MaxImageSize) / height;
						// width=width*height/2000;
						// height=2000;
					}
				}

				Matrix matrix = new Matrix();

				// 缩放图片动作
				matrix.postScale(scale, scale);
				// 旋转图片 动作
				matrix.postRotate(angle);
				Bitmap resizedBitmap = Bitmap.createBitmap(img, 0, 0, width,
						height, matrix, true);
				img = null;

				String path = saveFile(resizedBitmap, fileName);
				resizedBitmap = null;
				getIntent().putExtra("FilePath", path);
				setResult(RESULT_OK, getIntent());
			 
				// 停止照片拍摄

				finish();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				setResult(1, getIntent());
			}
			U5Application app = (U5Application) TakePhotoActivity.this.getApplication();
			app.IsTakingPhoto=false;
			finish();

		}
	};

	public String saveFile(Bitmap img, String fileName) throws IOException {
		File dirFile = new File(Settings.ALBUM_PATH);
		if (!dirFile.exists()) {
			dirFile.mkdir();
		}
		String path = dirFile.getAbsoluteFile() + File.separator + fileName;
		File myCaptureFile = new File(path);
		myCaptureFile.createNewFile();
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(myCaptureFile));
		img.compress(Bitmap.CompressFormat.JPEG, 100, bos);

		bos.flush();
		bos.close();
		return path;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		U5Application app = (U5Application) TakePhotoActivity.this.getApplication();
		 
		if (!app.IsTakingPhoto) {
			finish();
			return;
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		preview = new Preview(this);
		setContentView(preview);

		ivFocus = new ImageView(this);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN)
			preview.takePicture();
		return super.onTouchEvent(event);
	}

	class Preview extends SurfaceView implements SurfaceHolder.Callback {

		private SurfaceHolder holder;
		private Camera camera;

		// Preview类的构造方法
		@SuppressWarnings("deprecation")
		public Preview(Context context) {
			super(context);
			// 获得SurfaceHolder对象
			holder = getHolder();
			// 指定用于捕捉拍照事件的SurfaceHolder.Callback对象
			holder.addCallback(this);
			// 设置SurfaceHolder对象的类型
			holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		// 开厹拍照时调用该方法
		@SuppressWarnings("deprecation")
		public void surfaceCreated(SurfaceHolder holder) {

			// 获得Camera对象
			camera = Camera.open();
			try {
				Camera.Parameters parameters = camera.getParameters();
				List<Size> sizes = parameters.getSupportedPictureSizes();
				if (getWindowManager().getDefaultDisplay().getOrientation() == 0)
					angle = 90;
				else
					angle = 0;
				for (int i = 0; i < sizes.size(); i++) {
					int width = sizes.get(i).width;
					int height = sizes.get(i).height;
					if (width > height) {
						 
						if (width < Settings.MaxImageSize) {

							parameters.setPictureSize(width, height);
							camera.setParameters(parameters);
							break;
						}
					} else {
						 
						if (height < Settings.MaxImageSize) {

							parameters.setPictureSize(width, height);
							camera.setParameters(parameters);
							break;
						}
					}

				}
				camera.setDisplayOrientation(angle);
				camera.setPreviewDisplay(holder);
				camera.startPreview();
				ivFocus.setImageResource(R.drawable.loading_13);
				LayoutParams layoutParams = new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
				ivFocus.setScaleType(ScaleType.CENTER);
				addContentView(ivFocus, layoutParams);
				ivFocus.setVisibility(VISIBLE);
				// 自动对焦
				camera.autoFocus(new AutoFocusCallback() {
					@Override
					public void onAutoFocus(boolean success, Camera camera) {
						if (success) {
							// success为true表示对焦成功，改变对焦状态图像（一个绿色的png图像）
							ivFocus.setImageResource(R.drawable.loading_1);
						} else {
							ivFocus.setImageResource(R.drawable.loading_13);
						}
					}
				});
			} catch (IOException exception) {
				// 释放手机摄像头
				camera.release();
				camera = null;
			}

		}

		// 停止拍照时调用该方法
		public void surfaceDestroyed(SurfaceHolder holder) {
			// 释放手机摄像头
			camera.release();
			camera = null;

		}

		// 拍照状态变化时调用该方法
		@SuppressWarnings("deprecation")
		public void surfaceChanged(final SurfaceHolder holder, int format,
				int w, int h) {
			try {
				Camera.Parameters parameters = camera.getParameters();
				// 设置照片格式
				parameters.setPictureFormat(PixelFormat.JPEG);

				// 根据屏幕方向设置预浏尺寸
				if (getWindowManager().getDefaultDisplay().getOrientation() == 0)
					parameters.setPreviewSize(h, w);
				else
					parameters.setPreviewSize(w, h);

				// 设置拍摄照片的实际分辨率，在本例中的分辨率是1024*768
				// parameters.setPictureSize(1024, 768);
				// 设置保存的图像大小

				// 开始拍照
				// camera.startPreview();
				// 准备用于表示对焦状态的图像（类似图14.7所示的对焦符号）

			} catch (Exception e) {
				Toast.makeText(TakePhotoActivity.this, e.getMessage(),
						Toast.LENGTH_LONG).show();
			}

		}

		// 停止拍照，并将拍摄的照片传入PictureCallback接口的onPictureTaken方法
		public void takePicture() {
			if (camera != null) {
				camera.takePicture(null, null, pictureCallback);
				// Toast.makeText(TakePhotoActivity.this,
				// "1",Toast.LENGTH_LONG).show();
			}
		}
	}
}
