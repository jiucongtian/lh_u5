package com.longhuapuxin.view;

import com.longhuapuxin.u5.FeelingActivity;
import com.longhuapuxin.u5.R;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

public class MyPopupWindow extends PopupWindow implements OnClickListener {

	private View mRootLayout;
	private OnKeyListener mOnKeyListener;
	private Context mContext;

	public MyPopupWindow(Context context) {
		mContext = context;
		LayoutInflater inflater = LayoutInflater.from(context);
		mRootLayout = inflater.inflate(R.layout.popup_news, null);
		setContentView(mRootLayout);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.MATCH_PARENT);
		mOnKeyListener = new OnKeyListener() {

			@Override
			public boolean onKey(View view, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					dismiss();
				}
				return true;
			}
		};
		mRootLayout.findViewById(R.id.personalNews).setTag(1);
		mRootLayout.findViewById(R.id.shopNews).setTag(2);
		mRootLayout.findViewById(R.id.myNews).setTag(3);
		mRootLayout.findViewById(R.id.aboutMeNews).setTag(4);
		
		mRootLayout.findViewById(R.id.shopNews).setOnClickListener(this);
		mRootLayout.findViewById(R.id.personalNews).setOnClickListener(this);
		mRootLayout.findViewById(R.id.aboutMeNews).setOnClickListener(this);
		mRootLayout.findViewById(R.id.myNews).setOnClickListener(this);
				
		mRootLayout.setOnKeyListener(mOnKeyListener);
		mRootLayout.setFocusable(true);
		mRootLayout.setFocusableInTouchMode(true);
		
		mRootLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		setFocusable(true);
	}

	@Override
	public void update() {
		super.update();
	}

	@Override
	public void onClick(View v) {
		dismiss();
		int ownerType=Integer.parseInt( v.getTag().toString());
		Intent intent = new Intent(mContext,
				FeelingActivity.class);
		intent.putExtra("OwnerType", ownerType);
		mContext.startActivity(intent);
	}
	
}
