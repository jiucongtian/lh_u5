package com.longhuapuxin.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.longhuapuxin.u5.MainActivity;

/**
 * @blog http://blog.csdn.net/xiaanming
 * 
 * @author xiaanming
 * 
 */
public class SlideCutListView extends ListView {
	/**
	 * 当前滑动的ListView　position
	 */
	private int slidePosition;
	/**
	 * 手指按下X的坐标
	 */
	private int downY;
	/**
	 * 手指按下Y的坐标
	 */
	private int downX;
	/**
	 * 屏幕宽度
	 */
	private int screenWidth;
	/**
	 * ListView的item
	 */
	private SlideView itemView;
	/**
	 * 滑动类
	 */
	// private Scroller scroller;
	private static final int SNAP_VELOCITY = 600;
	/**
	 * 速度追踪对象
	 */
	private VelocityTracker velocityTracker;
	/**
	 * 是否响应滑动，默认为不响应
	 */
	private boolean isSlide = false;
	/**
	 * 认为是用户滑动的最小距离
	 */
	private int mTouchSlop;

	private int mHolderWidth = 120;

	private MainActivity aty;

	private OnSlideListener mOnSlideListener;

	public interface OnSlideListener {
		public static final int SLIDE_STATUS_OFF = 0;
		public static final int SLIDE_STATUS_START_SCROLL = 1;
		public static final int SLIDE_STATUS_ON = 2;
		public static final int DOWN = 3;

		/**
		 * @param view
		 *            current SlideView
		 * @param status
		 *            SLIDE_STATUS_ON or SLIDE_STATUS_OFF
		 */
		public void onSlide(View view, int status);
	}

	public void setOnSlideListener(OnSlideListener onSlideListener) {
		mOnSlideListener = onSlideListener;
	}

	public SlideCutListView(Context context) {
		this(context, null);
	}

	public SlideCutListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SlideCutListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		screenWidth = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getWidth();
		mHolderWidth = Math.round(TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, mHolderWidth, getResources()
						.getDisplayMetrics()));
		// scroller = new Scroller(context);
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
		aty = (MainActivity) context;
	}

	/**
	 * 分发事件，主要做的是判断点击的是那个item, 以及通过postDelayed来设置响应左右滑动事件
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			addVelocityTracker(event);
			// 假如scroller滚动还没有结束，我们直接返回
			// if (!scroller.isFinished()) {
			// return super.dispatchTouchEvent(event);
			// }
			downX = (int) event.getX();
			downY = (int) event.getY();

			slidePosition = pointToPosition(downX, downY);

			// 无效的position, 不做任何处理
			if (slidePosition == AdapterView.INVALID_POSITION) {
				return super.dispatchTouchEvent(event);
			}
			// 获取我们点击的item view
			itemView = (SlideView) getChildAt(slidePosition
					- getFirstVisiblePosition());
			if (mOnSlideListener != null) {
				mOnSlideListener.onSlide(itemView, OnSlideListener.DOWN);
				if (getSlideCutListViewState() == OnSlideListener.SLIDE_STATUS_ON) {
					setSlideCutListViewState(OnSlideListener.SLIDE_STATUS_ON);
					aty.setIntercept(false);
				} else {
					setSlideCutListViewState(OnSlideListener.DOWN);
					aty.setIntercept(false);
				}
			}
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			addVelocityTracker(event);
			if (Math.abs(getScrollVelocity()) > SNAP_VELOCITY
					|| (Math.abs(event.getX() - downX) > mTouchSlop && Math
							.abs(event.getY() - downY) < mTouchSlop)) {
				isSlide = true;
				if (mOnSlideListener != null) {
					mOnSlideListener.onSlide(itemView,
							OnSlideListener.SLIDE_STATUS_START_SCROLL);
					if (getSlideCutListViewState() == OnSlideListener.SLIDE_STATUS_ON) {
						setSlideCutListViewState(OnSlideListener.SLIDE_STATUS_ON);
					} else {
						setSlideCutListViewState(OnSlideListener.SLIDE_STATUS_START_SCROLL);
					}
				}
				break;
			}
		}
		case MotionEvent.ACTION_UP:
			recycleVelocityTracker();
			break;
		}

		return super.dispatchTouchEvent(event);
	}

	// @Override
	// public boolean onInterceptTouchEvent(MotionEvent ev) {
	// switch (ev.getAction()) {
	// case MotionEvent.ACTION_DOWN:
	// // stopSliding();
	// aty.setIntercept(false);
	// break;
	// case MotionEvent.ACTION_UP:
	// // startSliding();
	// aty.setIntercept(true);
	// break;
	// }
	// return super.onInterceptTouchEvent(ev);
	// }

	/**
	 * 处理我们拖动ListView item的逻辑
	 */

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (listViewNotMove) {
			return true;
		}
		if (isSlide && slidePosition != AdapterView.INVALID_POSITION) {
			final int action = ev.getAction();
			int x = (int) ev.getX();
			switch (action) {
			case MotionEvent.ACTION_MOVE: {
				int deltaX = downX - x;
				int newScrollX = itemView.getScrollX() + deltaX;
				if (newScrollX < 0) {
					newScrollX = 0;
				} else if (newScrollX > mHolderWidth) {
					newScrollX = mHolderWidth;
				}
				itemView.scrollTo(newScrollX, 0);
				downX = x;
				break;
			}
			case MotionEvent.ACTION_UP: {
				int newScrollX = 0;
				if (itemView.getScrollX() - mHolderWidth * 0.5 > 0) {
					newScrollX = mHolderWidth;
				}
				itemView.smoothScrollTo(newScrollX, 0);
				// itemView.scrollTo(newScrollX, 0);
				if (mOnSlideListener != null) {
					mOnSlideListener.onSlide(itemView,
							newScrollX == 0 ? OnSlideListener.SLIDE_STATUS_OFF
									: OnSlideListener.SLIDE_STATUS_ON);
					if (newScrollX == 0) {
						setSlideCutListViewState(OnSlideListener.SLIDE_STATUS_OFF);
						// aty.setIntercept(true);
					} else {
						setSlideCutListViewState(OnSlideListener.SLIDE_STATUS_ON);
						// aty.setIntercept(false);
					}
				}
				// 手指离开的时候就不响应左右滚动
				isSlide = false;
				break;
			}
			}

			return true; // 拖动的时候ListView不滚动
		}

		// 否则直接交给ListView来处理onTouchEvent事件
		return super.onTouchEvent(ev);
	}

	// @Override
	// public void computeScroll() {
	// // 调用startScroll的时候scroller.computeScrollOffset()返回true，
	// if (scroller.computeScrollOffset()) {
	// // 让ListView item根据当前的滚动偏移量进行滚动
	// itemView.scrollTo(scroller.getCurrX(), scroller.getCurrY());
	// postInvalidate();
	// }
	// }

	/**
	 * 添加用户的速度跟踪器
	 * 
	 * @param event
	 */
	private void addVelocityTracker(MotionEvent event) {
		if (velocityTracker == null) {
			velocityTracker = VelocityTracker.obtain();
		}
		velocityTracker.addMovement(event);

	}

	/**
	 * 移除用户速度跟踪器
	 */
	private void recycleVelocityTracker() {
		if (velocityTracker != null) {
			velocityTracker.recycle();
			velocityTracker = null;
		}
	}

	/**
	 * 获取X方向的滑动速度,大于0向右滑动，反之向左
	 * 
	 * @return
	 */
	private int getScrollVelocity() {
		velocityTracker.computeCurrentVelocity(1000);
		int velocity = (int) velocityTracker.getXVelocity();
		return velocity;
	}

	private boolean listViewNotMove = false;

	public void setListViewNotMove(boolean listViewNotMove) {
		this.listViewNotMove = listViewNotMove;
	}

	private int slideCutListViewState = OnSlideListener.SLIDE_STATUS_OFF;

	public void setSlideCutListViewState(int slideCutListViewState) {
		this.slideCutListViewState = slideCutListViewState;
	}

	public int getSlideCutListViewState() {
		return slideCutListViewState;
	}

	private SlideCutListViewCloseItemLinstener slideCutListViewCloseItemLinstener;

	public interface SlideCutListViewCloseItemLinstener {
		void SlideCutListViewCloseItem();
	}

	public void setSlideCutListViewCloseItemLinstener(
			SlideCutListViewCloseItemLinstener slideCutListViewCloseItemLinstener) {
		this.slideCutListViewCloseItemLinstener = slideCutListViewCloseItemLinstener;
	}

	public void closeItem() {
		if (slideCutListViewCloseItemLinstener != null) {
			slideCutListViewCloseItemLinstener.SlideCutListViewCloseItem();
		}
	}
}
