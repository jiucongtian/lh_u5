package com.longhuapuxin.common;

import android.content.Context;

public class DensityUtil {
	/**
	 * dip转像素
	 * @param context
	 * @param dpValue
	 * @return
	 */
	public static int dip2px(Context context,float dpValue){
		float scale = context.getResources().getDisplayMetrics().density;
		return (int)(dpValue*scale+0.5f);
	}
	/**
	 * 像素转dip
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context,float pxValue){
		float scale = context.getResources().getDisplayMetrics().density;
		return (int)(pxValue/scale+0.5f);
	}
	
}
