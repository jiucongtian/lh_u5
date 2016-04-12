package com.longhuapuxin.senabimage;

import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.u5.Settings;

public class ImageDetailFragment extends Fragment {
	private String mImageUrl;
	private ImageView mImageView;
	private ProgressBar progressBar;
	private PhotoViewAttacher mAttacher;

	public static ImageDetailFragment newInstance(String imageUrl) {
		final ImageDetailFragment f = new ImageDetailFragment();

		final Bundle args = new Bundle();
		args.putString("url", imageUrl);
		f.setArguments(args);

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageUrl = getArguments() != null ? getArguments().getString("url") : null;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
		mImageView = (ImageView) v.findViewById(R.id.image);
		mAttacher = new PhotoViewAttacher(mImageView);
		
		mAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {
			
			@Override
			public void onPhotoTap(View arg0, float arg1, float arg2) {
				getActivity().finish();
				getActivity().overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
			}
		});
		
		progressBar = (ProgressBar) v.findViewById(R.id.loading);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		BitmapUtils bmpUtils = new BitmapUtils(getActivity());

		bmpUtils/*.configDefaultLoadingImage(R.drawable.photo_error)
				.configDefaultLoadFailedImage(R.drawable.photo_error)*/
				.configDefaultAutoRotation(true);

		BitmapLoadCallBack<ImageView> callBack = new DefaultBitmapLoadCallBack<ImageView>() {

			@Override
			public void onLoadStarted(ImageView container, String uri, BitmapDisplayConfig config) {
				super.onLoadStarted(container, uri, config);
				progressBar.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadFailed(ImageView container, String uri, Drawable drawable) {
				super.onLoadFailed(container, uri, drawable);
				progressBar.setVisibility(View.GONE);
			}

			@Override
			public void onLoadCompleted(ImageView container, String uri, Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
				super.onLoadCompleted(container, uri, bitmap, config, from);
				progressBar.setVisibility(View.GONE);
				mAttacher.update();
			}
		};

		bmpUtils.display(mImageView, Settings.instance().getImageUrl() + mImageUrl, callBack);

	}

}
