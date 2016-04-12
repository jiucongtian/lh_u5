package com.longhuapuxin.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ImgAmplificationIdicators extends View {
	private int indicatorCount = 3;
	private int width, height;
	private int radius = 2;
	private Paint mPaintUnselected;
	private Paint mPaintSelected;
	private final static int selectedColor = 0xFF000000;
	private final static int unselectedColor = 0xFF666666;
	private int indicationSelected;

	public ImgAmplificationIdicators(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPaint();
	}

	private void initPaint() {
		mPaintUnselected = new Paint();
		mPaintUnselected.setColor(unselectedColor);
		mPaintSelected = new Paint();
		mPaintSelected.setColor(selectedColor);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		width = getMeasuredWidth();
		height = getMeasuredHeight();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		for (int i = 0; i < indicatorCount; i++) {
			if (indicatorCount == 1) {
				return;
			} else if (indicatorCount == 2) {
				canvas.drawCircle(width / 2, height / 2, radius,
						mPaintUnselected);
				canvas.drawCircle(width / 2 - 10, height / 2, radius,
						mPaintUnselected);
			} else if (indicatorCount == 3) {
				canvas.drawCircle(width / 2, height / 2, radius,
						mPaintUnselected);
				canvas.drawCircle(width / 2 + 10, height / 2, radius,
						mPaintUnselected);
				canvas.drawCircle(width / 2 - 10, height / 2, radius,
						mPaintUnselected);
			}
		}
		if (indicationSelected == 1) {
			canvas.drawCircle(width / 2 - 10, height / 2, radius,
					mPaintSelected);
		} else if (indicationSelected == 2) {
			canvas.drawCircle(width / 2, height / 2, radius, mPaintSelected);
		} else if (indicationSelected == 3) {
			canvas.drawCircle(width / 2 + 10, height / 2, radius,
					mPaintSelected);
		}

		super.onDraw(canvas);
	}

	public void setIndicatorCount(int indicatorCount) {
		this.indicatorCount = indicatorCount;
	}

	public void setIndicatorIsselected(int indicationSelected) {
		this.indicationSelected = indicationSelected;
	}
}
