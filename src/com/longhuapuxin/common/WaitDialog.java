package com.longhuapuxin.common;

import com.longhuapuxin.u5.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;

public class WaitDialog {
	
	private static WaitDialog self = null;
	private Dialog dialog = null;
	private Activity mActivity;
	
	private WaitDialog() {
	}

	public static WaitDialog instance() {
		if (self == null) {
			self = new WaitDialog();
		}
		return self;
	}
	
//	public void showWaitNote(Context context) {
//		AlertDialog.Builder builder = new Builder(context); 
//		AlertDialog dialog = builder.create();
//		dialog.setCancelable(false);
//		dialog.show();
//		dialog.getWindow().setContentView(R.layout.transparent_note);
//		AnimationDrawable animation = (AnimationDrawable)dialog.getWindow().findViewById(R.id.waitingImage).getBackground();
//		animation.start();
//	}
	
	public void showWaitNote(Context context) {
		mActivity = (Activity) context;
		if(dialog == null) {
			dialog = new Dialog(context, R.style.activity_translucent);
			dialog.setContentView(R.layout.transparent_note);
			AnimationDrawable animation = (AnimationDrawable)dialog.findViewById(R.id.waitingImage).getBackground();
			animation.start();
			dialog.setCancelable(false);
			dialog.show();
		} else {
			AnimationDrawable animation = (AnimationDrawable)dialog.findViewById(R.id.waitingImage).getBackground();
			animation.start();
			dialog.show();
		}
	}
	
	public void hideWaitNote() {
		
		if(mActivity != null && !mActivity.isFinishing()) {
			if(dialog != null) {
				AnimationDrawable animation = (AnimationDrawable)dialog.findViewById(R.id.waitingImage).getBackground();
				animation.stop();
				dialog.dismiss();
				dialog = null;
			}
		}
		
	}
	
	
}
