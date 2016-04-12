package com.longhuapuxin.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

public class IsOnlineView extends RelativeLayout {
	private Scroller mScroller;

	public IsOnlineView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mScroller = new Scroller(context);

	}

	private void smoothScrollTo(int scrollX, int scrollY) {
		int startX = getScrollX();
		int startY = getScrollY();
		int dx = scrollX - startX;
		int dy = scrollY - startY;
		mScroller.startScroll(startX, startY, dx, dy, 800);
		invalidate();// 为了触发computeScroll方法
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		if (!mScroller.isFinished()) {
			if (mScroller.computeScrollOffset()) {
				scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
				invalidate();
			}
		} else {
			if (isOnline) {
				if (onlineRound != null && onlineText != null
						&& busyRound != null && busyText != null) {
					onlineRound.setVisibility(View.VISIBLE);
					onlineText.setVisibility(View.VISIBLE);
					busyRound.setVisibility(View.GONE);
					busyText.setVisibility(View.GONE);
				}
			} else {
				if (onlineRound != null && onlineText != null
						&& busyRound != null && busyText != null) {
					busyRound.setVisibility(View.VISIBLE);
					busyText.setVisibility(View.VISIBLE);
					onlineRound.setVisibility(View.GONE);
					onlineText.setVisibility(View.GONE);
				}
			}
		}
		super.computeScroll();
	}

	private boolean isOnline;
	private ImageView onlineRound, busyRound;
	private TextView onlineText, busyText;

	public void smoothToBusy(int onlineViewwidth, int onlineRoundWidth,
			ImageView onlineRound, ImageView busyRound, TextView onlineText,
			TextView busyText, boolean isOnline) {
		this.isOnline = isOnline;
		this.onlineRound = onlineRound;
		this.busyRound = busyRound;
		this.onlineText = onlineText;
		this.busyText = busyText;
		smoothScrollTo(onlineViewwidth - onlineRoundWidth, 0);
	}

	public void smoothToOnline(ImageView onlineRound, ImageView busyRound,
			TextView onlineText, TextView busyText, boolean isOnline) {
		this.isOnline = isOnline;
		this.onlineRound = onlineRound;
		this.busyRound = busyRound;
		this.onlineText = onlineText;
		this.busyText = busyText;
		smoothScrollTo(0, 0);
	}
}
