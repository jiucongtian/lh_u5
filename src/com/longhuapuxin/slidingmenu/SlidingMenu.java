package com.longhuapuxin.slidingmenu;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.longhuapuxin.fragment.FragmentLeft;
import com.longhuapuxin.fragment.FragmentRight;
import com.longhuapuxin.fragment.MessageFragment;
import com.longhuapuxin.u5.MainActivity;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.view.SlideCutListView;
import com.nineoldandroids.view.ViewHelper;

public class SlidingMenu extends FrameLayout {
	/** 装菜单Fragment的容器 **/
	private FrameLayout mMenuContainer;
	/** 装主Fragment的容器 **/
	private FrameLayout mMainContainer;
	/** 装菜单Fragment的容器的id **/
	public static final int MENU_CONTAINER_ID = 1;
	/** 装主Fragment的容器的id **/
	public static final int MAIN_CONTAINER_ID = 2;
	/** 根容器的宽 **/
	private int mViewWidth;
	/** 菜单的宽 **/
	private int mMenuWidth;
	/** 菜单的宽占根容器的比例 **/
	private float mMenuRatio = 0.65f;
	/** 菜单的缩放比例 **/
	private float mMenuMinScale = 0.7f;
	/** 菜单的透明度比例 **/
	private float mMenuMinAlpha = 0.8f;
	/** 主视图的缩放比例 **/
	private float mMainMinScale = 0.8f;
	/** 菜单往左超出屏幕外的宽占菜单宽的比例 **/
	private float mMenuPadingRatio = 0.2f;
	private Scroller mScroller;
	private int mTouchSlop;
	/** 菜单关闭状态 **/
	private static final int STATE_MENU_CLOSE = 1;
	/** 正在滑动状态 **/
	private static final int STATE_SLIDING = 2;
	/** 菜单打开状态 **/
	private static final int STATE_MENU_OPEN = 3;
	/** 当前状态 **/
	private int mCurState = STATE_MENU_CLOSE;
	private SlideCutListView mSlideCutListView;

	public SlidingMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SlidingMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public SlidingMenu(Context context) {
		super(context);
		init();
	}

	@SuppressLint("NewApi")
	private void init() {
		// setBackground(getResources().getDrawable(R.drawable.profile_bg));
		// setBackgroundColor(getResources().getColor(R.color.translucent));
		mMenuContainer = new FrameLayout(getContext());
		mMenuContainer.setId(MENU_CONTAINER_ID);
		mMainContainer = new FrameLayout(getContext());
		mMainContainer.setId(MAIN_CONTAINER_ID);
		addView(mMenuContainer);
		addView(mMainContainer);

		mScroller = new Scroller(getContext());
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

	}

	private float mFirstEventX;

	@SuppressLint("NewApi")
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mFirstEventX = ev.getX();
			if (mSlideCutListView == null) {
				MainActivity aty = (MainActivity) getContext();
				FragmentRight fr = (FragmentRight) aty.getFragmentRight();
				FragmentLeft fl = (FragmentLeft) aty.getFragmentLeft();
				MessageFragment messageFragment = (MessageFragment) fr
						.getMessageFragment();
				View view = messageFragment.getView();
				if (view != null) {
					mSlideCutListView = (SlideCutListView) view
							.findViewById(R.id.messageListView);
					mSlideCutListView.closeItem();
				}
				if (ViewHelper.getScrollX(mMainContainer) != 0
						&& menuIsOpen() == false) {
					fl.getSetClickbleViewsFalse();
				} else {
					fl.getSetClickbleViewsTrue();
				}
			}
			if (mSlideCutListView != null) {
				mSlideCutListView.closeItem();
				mSlideCutListView
						.setSlideCutListViewState(com.longhuapuxin.view.SlideCutListView.OnSlideListener.SLIDE_STATUS_OFF);
			}

			if (mCurState == STATE_SLIDING) {
				if (!mScroller.isFinished()) {
					mScroller.forceFinished(false);
				}
			}
			if (mCurState == STATE_MENU_OPEN && ev.getX() >= mMenuWidth) {
				return true;
			}
			// 如果正在自动滑动，拦截MainContainer所在的区域
			if (mCurState == STATE_SLIDING
					&& ev.getX() > -mMainContainer.getScrollX()
							* mMenuWidth
							/ (((1 - (1 - mMenuRatio) / mMainMinScale) * mViewWidth))) {

				return true;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			// getSlideCutListView();
			// MainActivity aty = (MainActivity) getContext();
			// FragmentRight fr = (FragmentRight) aty.getFragmentRight();
			// MessageFragment messageFragment = (MessageFragment) fr
			// .getMessageFragment();
			// mSlideCutListView = (SlideCutListView) messageFragment.getView()
			// .findViewById(R.id.messageListView);
			if (mSlideCutListView == null) {
				MainActivity aty = (MainActivity) getContext();
				FragmentRight fr = (FragmentRight) aty.getFragmentRight();
				MessageFragment messageFragment = (MessageFragment) fr
						.getMessageFragment();
				View view = messageFragment.getView();
				if (view != null) {
					mSlideCutListView = (SlideCutListView) view
							.findViewById(R.id.messageListView);
					if (mSlideCutListView.getSlideCutListViewState() != com.longhuapuxin.view.SlideCutListView.OnSlideListener.SLIDE_STATUS_ON
							&& ev.getX() < mFirstEventX) {
						if (!intercept) {
							return false;
						}
					}
					if (mSlideCutListView.getSlideCutListViewState() == com.longhuapuxin.view.SlideCutListView.OnSlideListener.SLIDE_STATUS_ON) {
						if (!intercept) {
							return false;
						}
					}
				}
			}
			if (mSlideCutListView != null) {
				if (mSlideCutListView.getSlideCutListViewState() != com.longhuapuxin.view.SlideCutListView.OnSlideListener.SLIDE_STATUS_ON
						&& ev.getX() < mFirstEventX) {
					if (!intercept) {
						return false;
					}
				}
				if (mSlideCutListView.getSlideCutListViewState() == com.longhuapuxin.view.SlideCutListView.OnSlideListener.SLIDE_STATUS_ON) {
					if (!intercept) {
						return false;
					}
				}
			}
			if (Math.abs(ev.getX() - mFirstEventX) >= mTouchSlop) {
				mLastEventX = ev.getX();
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	private float mLastEventX;
	private float mMaxMoveX;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastEventX = event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			float offset = event.getX() - mLastEventX;
			int mainScrollX = (int) (mMainContainer.getScrollX() - offset);
			handleScroll(mainScrollX);
			mLastEventX = event.getX();

			float moveX = Math.abs(event.getX() - mFirstEventX);
			mMaxMoveX = Math.max(mMaxMoveX, moveX);
			break;
		case MotionEvent.ACTION_UP:
			if (mMaxMoveX < mTouchSlop && event.getX() >= mMenuWidth) {
				closeMenu();
				mMaxMoveX = 0;
				break;
			}
			mMaxMoveX = 0;
			if (ViewHelper.getScrollX(mMainContainer) == -(int) ((1 - (1 - mMenuRatio)
					/ mMainMinScale) * mViewWidth)) {
				break;
			}
			if (ViewHelper.getScrollX(mMainContainer) >= -(int) ((1 - (1 - mMenuRatio)
					/ mMainMinScale) * mViewWidth) / 2) {
				closeMenu();
			} else {
				openMenu();
			}
			break;
		}
		return true;
	}

	/**
	 * 处理滚动
	 * 
	 * @param mainScrollX
	 *            :计算得出的主视图scrollX的值
	 */
	private void handleScroll(int mainScrollX) {
		if (mainScrollX >= 0) {
			mainScrollX = 0;
		}
		if (mainScrollX <= -(int) ((1 - (1 - mMenuRatio) / mMainMinScale) * mViewWidth)) {
			mainScrollX = -(int) ((1 - (1 - mMenuRatio) / mMainMinScale) * mViewWidth);
		}
		ViewHelper.setScrollX(mMainContainer, mainScrollX);
		// 设置主容器的缩放
		ViewHelper.setPivotX(mMainContainer, mMainContainer.getWidth());
		ViewHelper.setPivotY(mMainContainer, mMainContainer.getHeight() / 2);
		ViewHelper
				.setScaleX(mMainContainer,
						1
								- (1 - mMainMinScale)
								* mainScrollX
								/ (-(int) ((1 - (1 - mMenuRatio)
										/ mMainMinScale) * mViewWidth)));
		ViewHelper
				.setScaleY(mMainContainer,
						1
								- (1 - mMainMinScale)
								* mainScrollX
								/ (-(int) ((1 - (1 - mMenuRatio)
										/ mMainMinScale) * mViewWidth)));

		float menuScrollX = mMenuWidth
				* mMenuPadingRatio
				- mMenuWidth
				* mainScrollX
				/ ((1 / mMenuPadingRatio)
						* -(1 - (1 - mMenuRatio) / mMainMinScale) * mViewWidth);
		ViewHelper.setScrollX(mMenuContainer, (int) menuScrollX);
		ViewHelper.setPivotX(mMenuContainer, 0);
		ViewHelper.setPivotY(mMenuContainer, mMenuContainer.getHeight());
		ViewHelper.setScaleX(mMenuContainer, mMenuMinScale - mainScrollX
				* (1f - mMenuMinScale)
				/ ((1 - (1 - mMenuRatio) / mMainMinScale) * mViewWidth));
		ViewHelper.setScaleY(mMenuContainer, mMenuMinScale - mainScrollX
				* (1f - mMenuMinScale)
				/ ((1 - (1 - mMenuRatio) / mMainMinScale) * mViewWidth));
		ViewHelper.setAlpha(mMenuContainer, mMenuMinScale - mainScrollX
				* (1f - mMenuMinAlpha)
				/ ((1 - (1 - mMenuRatio) / mMainMinScale) * mViewWidth));
		// ViewHelper.setAlpha(this, mMenuMinScale - mainScrollX
		// * (1f - mMenuMinAlpha)
		// / ((1 - (1 - mMenuRatio) / mMainMinScale) * mViewWidth));
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (mViewWidth == 0) {
			mViewWidth = getWidth();
			mMenuWidth = (int) (mViewWidth * mMenuRatio);
			LayoutParams params = new LayoutParams(mMenuWidth,
					LayoutParams.MATCH_PARENT);
			mMenuContainer.setLayoutParams(params);
		}
	}

	@Override
	public void computeScroll() {
		if (!mScroller.isFinished()) {
			if (mScroller.computeScrollOffset()) {
				int mainScrollX = mScroller.getCurrX();
				handleScroll(mainScrollX);
				invalidate();
			}
		} else {
			// if (mCurState == STATE_SLIDING) {
			if (mMainContainer.getScrollX() == 0) {
				mCurState = STATE_MENU_CLOSE;
			} else {
				mCurState = STATE_MENU_OPEN;
			}
		}
		// }
	}

	/**
	 * 让主视图平滑的滚动指定位置
	 * 
	 * @param scrollX
	 */
	public void smoothScrollTo(int scrollX) {
		mCurState = STATE_SLIDING;
		int startX = mMainContainer.getScrollX();
		int dx = scrollX - startX;
		int duration = 0;
		if (!isOpenMenu) {
			duration = dx * 800 / mMenuWidth;
		} else {
			duration = 500;
		}
		mScroller.startScroll(startX, 0, dx, 0, duration);
		invalidate();
	}

	/**
	 * 打开菜单
	 */
	private boolean isOpenMenu = false;

	public void openMenu() {
		isOpenMenu = true;
		smoothScrollTo(-(int) ((1 - (1 - mMenuRatio) / mMainMinScale) * mViewWidth));
	}

	/**
	 * 关闭菜单
	 */
	public void closeMenu() {
		isOpenMenu = false;
		smoothScrollTo(0);
	}

	public SlidingMenu setMenuRatio(float mMenuRatio) {
		this.mMenuRatio = mMenuRatio;
		return this;
	}

	public SlidingMenu setMenuMinScale(float mMenuMinScale) {
		this.mMenuMinScale = mMenuMinScale;
		return this;
	}

	public SlidingMenu setMainMinScale(float mMainMinScale) {
		this.mMainMinScale = mMainMinScale;
		return this;
	}

	public SlidingMenu setMenuPadingRatio(float mMenuPadingRatio) {
		this.mMenuPadingRatio = mMenuPadingRatio;
		return this;
	}

	private boolean intercept = true;

	public void setIntercept(boolean intercept) {
		this.intercept = intercept;
	}

	/**
	 * 判断菜单是否打开
	 * 
	 * @return
	 */
	public boolean menuIsOpen() {
		return mCurState == STATE_MENU_OPEN;

	}

	// private void getSlideCutListView() {
	// if (mSlideCutListView == null) {
	// MainActivity aty = (MainActivity) getContext();
	// FragmentRight fr = (FragmentRight) aty.getFragmentRight();
	// MessageFragment messageFragment = (MessageFragment) fr
	// .getMessageFragment();
	// mSlideCutListView = (SlideCutListView) messageFragment.getView()
	// .findViewById(R.id.messageListView);
	// }
	// }
}
