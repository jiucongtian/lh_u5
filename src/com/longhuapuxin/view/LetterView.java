package com.longhuapuxin.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class LetterView extends View {
	/** 选中的字体颜色 **/
	private static final int TEXT_COLOR_SELECTED = 0xffffffff;
	/** 未选中的字体颜色 **/
	private static final int TEXT_COLOR_UNSELECTED = 0xff000000;
	/** View的背景颜色 **/
	private static final int BACKGROUND_COLOR = 0x44444444;
	/** 选中字母的背景颜色 **/
	private static final int TEXT_BACKGROUND_COLOR = 0xff0000ff;
	/** 字体大小 **/
	private static final int TEXT_SIZE = 20;
	/** 字母表 **/
	private static final String mLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ#";
	private Paint mPaint;
	private int mWidth, mHeight;
	private float mItemHeight;

	public LetterView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public LetterView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public LetterView(Context context) {
		super(context);
		init();
	}

	private void init() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);// 抗锯齿
		mPaint.setTextSize(TEXT_SIZE);
	}

	private RectF mTextRect;
	private float mTextBaseLine;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mWidth == 0) {
			mWidth = getWidth();
			mHeight = getHeight();
			mItemHeight = mHeight * 1f / mLetters.length();
			mTextRect = new RectF(0, 0, mWidth, mItemHeight);
			FontMetricsInt fontMetricsInt = mPaint.getFontMetricsInt();
			mTextBaseLine = mTextRect.top
					+ (mTextRect.bottom - mTextRect.top - fontMetricsInt.bottom + fontMetricsInt.top)
					/ 2 - fontMetricsInt.top;
		}
		// 判断是否画背景色
		if (isDrawBag) {
			canvas.drawColor(BACKGROUND_COLOR);
		}

		// 绘制选中字母所在的矩形
		mPaint.setColor(TEXT_BACKGROUND_COLOR);
		canvas.drawRect(0, selectedPos * mItemHeight, mWidth, (selectedPos + 1)
				* mItemHeight, mPaint);
		// 循环画字母
		for (int i = 0; i < mLetters.length(); i++) {
			String text = mLetters.charAt(i) + "";
			float x = (mWidth - mPaint.measureText(text)) / 2;
			float y = mTextBaseLine + i * mItemHeight;
			if (i == selectedPos) {
				mPaint.setColor(TEXT_COLOR_SELECTED);
			} else {
				mPaint.setColor(TEXT_COLOR_UNSELECTED);
			}
			canvas.drawText(text, x, y, mPaint);
		}
	}

	/** 是否绘制背景色 **/
	private boolean isDrawBag;
	/** 选中的位置 **/
	private int selectedPos;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int pos = (int) (event.getY() / mItemHeight);
		// 处理边界
		if (pos < 0 || pos > mLetters.length() - 1) {
			return true;
		}

		// 如果监听器对象不为null 且 触摸的位置跟上一次的位置不相等
		if (pos != selectedPos && listener != null) {
			// 回调
			listener.onLetterChange(mLetters.charAt(pos));
		}
		selectedPos = pos;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isDrawBag = true;
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			isDrawBag = false;
			break;
		}
		invalidate();// 重绘
		return true;
	}

	public interface OnLetterChangeListener {
		void onLetterChange(int letter);
	}

	private OnLetterChangeListener listener;

	public void setOnLetterChangeListener(OnLetterChangeListener listener) {
		this.listener = listener;
	}

	/**
	 * 暴露一个方法，让别人可以控制字母的选择
	 * 
	 * @param letter
	 */
	public void setSelectLetter(int letter) {
		for (int i = 0; i < mLetters.length(); i++) {
			if (letter == mLetters.charAt(i)) {
				selectedPos = i;
				invalidate();
				break;
			}
		}
	}
}
