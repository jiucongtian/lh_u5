package com.longhuapuxin.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.Scroller;

public class BottomSlideView extends FrameLayout {
	Scroller mScroller;

	public BottomSlideView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mScroller = new Scroller(context);
	}

	public void smoothScrollTo(int desX, int desY, int duration) {
		int dx = desX - getScrollX();
		int dy = desY - getScrollY();
		mScroller.startScroll(getScrollX(), getScrollY(), dx, dy, duration);
		invalidate();
	}

	@Override
	public void computeScroll() {
		if (!mScroller.isFinished()) {
			if (mScroller.computeScrollOffset()) {
				int oldX = getScrollX();
				int oldY = getScrollY();
				int x = mScroller.getCurrX();
				int y = mScroller.getCurrY();
				if (oldX != x || oldY != y) {
					scrollTo(x, y);
				}
				invalidate();
			}
		}
	}
}
