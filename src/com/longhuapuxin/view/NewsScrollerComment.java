package com.longhuapuxin.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.Scroller;

public class NewsScrollerComment extends FrameLayout {
	private Scroller mScroller;

	public NewsScrollerComment(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		mScroller = new Scroller(getContext());

	}

	public void smoothScrollTo(int scrollX, int scrollY) {
		int startX = this.getScrollX();
		int startY = this.getScrollY();
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
				int homeScrollX = mScroller.getCurrX();
				this.scrollTo(homeScrollX, mScroller.getCurrY());
				invalidate();
			}
		}
	}
}
