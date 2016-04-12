package com.longhuapuxin.u5;

import java.util.ArrayList;

import com.longhuapuxin.common.AsyncImageLoader;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher.ViewFactory;

public class GalleryActivity extends BaseActivity implements  OnTouchListener {
	

	public ArrayList<String> mUrls;
	public int mPosition;
 
	private AsyncImageLoader mImagLoader;

	/**
	 * ImagaSwitcher 的引用
	 */
	private ImageSwitcher mImageSwitcher;

	/**
	 * 当前选中的图片id序号
	 */
	private int mCurrentPosition;
	/**
	 * 按下点的X坐标
	 */
	private float mDownX;
	/**
	 * 装载点点的容器
	 */
	private LinearLayout mLinearLayout;
	/**
	 * 点点数组
	 */
	private ImageView[] tips;
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
	 
		mImageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher1);
		// 设置Factory
		mImageSwitcher.setFactory(viewFactory);
		mImageSwitcher.setOnTouchListener(this);
		mLinearLayout = (LinearLayout) findViewById(R.id.viewGroup);
		Intent intent=getIntent();
		mUrls=new ArrayList<String>();
		mPosition=0;
		if(intent!=null){
			mUrls=intent.getStringArrayListExtra("Urls");
			mPosition=intent.getIntExtra("Position",0);
			 
		}
		
		tips = new ImageView[mUrls.size()];
		for (int i = 0; i < mUrls.size(); i++) {
			ImageView mImageView = new ImageView(this);
			mImageView.setTag(i);
			mImageView.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					int pos=Integer.parseInt( arg0.getTag().toString());
					GotoImage(pos);
				}
				
			});
			tips[i] = mImageView;
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
			layoutParams.rightMargin = 3;
			layoutParams.leftMargin = 3;

			mImageView.setBackgroundResource(R.drawable.photo_view_point);
			mLinearLayout.addView(mImageView, layoutParams);

		}
		mImagLoader = new AsyncImageLoader(getApplicationContext());
		// 将图片缓存至外部文件中
		mImagLoader.setCache2File(true); // false
		// 设置外部缓存文件夹
		mImagLoader.setCachedDir(getCacheDir().getAbsolutePath());

		mCurrentPosition = mPosition;
		LoadImage(mCurrentPosition);
		setImageBackground(mCurrentPosition);

	}
	private ViewFactory viewFactory = new ViewFactory() {

		@SuppressWarnings("deprecation")
		@Override
		public View makeView() {
			// TODO Auto-generated method stub
			final ImageView i = new ImageView(GalleryActivity.this);
			i.setBackgroundColor(0xff000000);

			i.setScaleType(ImageView.ScaleType.FIT_CENTER);
			i.setLayoutParams(new ImageSwitcher.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

			return i;
		}
	};

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_gallery, container,
				false);
		return rootView;
	}

 

	private void LoadImage(int idx) {
		String imgUrl =Settings.instance().getImageUrl() + mUrls.get(idx);
		//GalleryActivity.this.ShowDialog("正在加载图片");
		mImagLoader.downloadImage(imgUrl, true,
				new AsyncImageLoader.ImageCallback() {

					@SuppressWarnings("deprecation")
					@Override
					public void onImageLoaded(Bitmap bitmap, String imageUrl) {
						// TODO Auto-generated method stub
						if (bitmap != null) {
							mImageSwitcher.setImageDrawable(new BitmapDrawable(
									bitmap));
						} else {
							// 下载失败，设置默认图片
						}
						//GalleryActivity.this.HideDialog();
					}
				});
	}

	/**
	 * 设置选中的tip的背景
	 * 
	 * @param selectItems
	 */
	private void setImageBackground(int selectItems) {
		for (int i = 0; i < tips.length; i++) {
			if (i == selectItems) {
				tips[i].setBackgroundResource(R.drawable.photo_view_point_sel);
			} else {
				tips[i].setBackgroundResource(R.drawable.photo_view_point);
			}
		}
	}
	private void GotoImage(int index){
		if(index>mCurrentPosition){
			mImageSwitcher.setInAnimation(AnimationUtils.loadAnimation(
					getApplication(), R.anim.right_in));
			mImageSwitcher.setOutAnimation(AnimationUtils
					.loadAnimation(getApplication(),
							R.anim.left_out));
			mCurrentPosition=index;
		}
		else if(index<mCurrentPosition){
			mImageSwitcher.setInAnimation(AnimationUtils.loadAnimation(
					getApplication(), R.anim.left_in));
			mImageSwitcher.setOutAnimation(AnimationUtils
					.loadAnimation(getApplication(),
							R.anim.right_out));
			mCurrentPosition=index;
		}
		else{
			return;
		}
		// 设置动画，这里的动画比较简单，不明白的去网上看看相关内容
		
		LoadImage(mCurrentPosition % mUrls.size());

		setImageBackground(mCurrentPosition);
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			// 手指按下的X坐标
			mDownX = event.getX();
			break;
		}
		case MotionEvent.ACTION_UP: {
			float lastX = event.getX();
			// 抬起的时候的X坐标大于按下的时候就显示上一张图片
			if (lastX > mDownX) {
				if (mCurrentPosition > 0) {
					// 设置动画，这里的动画比较简单，不明白的去网上看看相关内容
					mImageSwitcher.setInAnimation(AnimationUtils.loadAnimation(
							getApplication(), R.anim.left_in));
					mImageSwitcher.setOutAnimation(AnimationUtils
							.loadAnimation(getApplication(),
									R.anim.right_out));
					mCurrentPosition--;
					LoadImage(mCurrentPosition % mUrls.size());

					setImageBackground(mCurrentPosition);
				} else {
					// Toast.makeText(main.getApplication(), "已经是第一张",
					// Toast.LENGTH_SHORT).show();
				}
			}

			if (lastX < mDownX) {
				if (mCurrentPosition < mUrls.size() - 1) {
					mImageSwitcher.setInAnimation(AnimationUtils.loadAnimation(
							getApplication(), R.anim.right_in));
					mImageSwitcher.setOutAnimation(AnimationUtils
							.loadAnimation(getApplication(),
									R.anim.left_out));
					mCurrentPosition++;
					LoadImage(mCurrentPosition);

					setImageBackground(mCurrentPosition);
				} else {
					// Toast.makeText(main.getApplication(), "到了最后一张",
					// Toast.LENGTH_SHORT).show();
				}
			}
		}

			break;
		}

		return true;
	}

}
