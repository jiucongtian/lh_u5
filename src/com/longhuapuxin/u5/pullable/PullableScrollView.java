package com.longhuapuxin.u5.pullable;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class PullableScrollView extends ScrollView implements Pullable
{
	private boolean canPullUp=false;
	private boolean canPullDown=false;
	public void PreventPullUp(){
		canPullUp=false;
	}
	public void AllowPull(){
		 canPullUp=true;
		 canPullDown=true;
	}
	public PullableScrollView(Context context)
	{
		super(context);
	}

	public PullableScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public PullableScrollView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	public boolean canPullDown()
	{
		if (getScrollY() == 0)
			return true && canPullDown;
		else
			return false && canPullDown;
	}

	@Override
	public boolean canPullUp()
	{
		if (getScrollY() >= (getChildAt(0).getHeight() - getMeasuredHeight()))
			return true && canPullUp;
		else
			return false && canPullUp;
	}

}