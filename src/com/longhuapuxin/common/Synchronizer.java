package com.longhuapuxin.common;

import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.entity.ResponseMarkedLabels;
import com.longhuapuxin.entity.ResponseNewSession;
import com.longhuapuxin.u5.ChatActivity;
import com.longhuapuxin.u5.Settings;
import com.longhuapuxin.u5.U5Application;
import com.squareup.okhttp.Request;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Synchronizer {

	// public interface SyncCallBack {
	// public void syncError();
	// public void syncSuccess();
	// }

	public static abstract class OnSyncCallBack {
		public abstract void syncError();

		public abstract void syncSuccess(Object object);
	}

	public static void syncMyLabel(final Context context,
			final OnSyncCallBack listener) {
		Param[] params = new Param[3];
		params[0] = new Param("TargetUserId", Settings.instance().getUserId());
		params[1] = new Param("UserId", Settings.instance().getUserId());
		params[2] = new Param("Token", Settings.instance().getToken());

		OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
				+ "/label/getmarkedlabels", params,
				new OkHttpClientManager.ResultCallback<ResponseMarkedLabels>() {

					@Override
					public void onError(Request request, Exception e) {
						Logger.info("Synchronizer.syncMyLabel.onError"
								+ e.toString());

						listener.syncError();
					}

					@Override
					public void onResponse(ResponseMarkedLabels response) {
						Logger.info("Synchronizer.syncMyLabel.onResponse"
								+ response);

						if (response.isSuccess()) {
							Settings.instance().User.updateLabels(response
									.getLabels());
							listener.syncSuccess(null);
						} else {
							listener.syncError();
						}
					}

				});
	}

	public static void httpRequestNewSession(final String targetUserId,
			final OnSyncCallBack listener) {
		Param[] params = new Param[3];
		params[0] = new Param("UserId", Settings.instance().getUserId());
		params[1] = new Param("Token", Settings.instance().getToken());
		params[2] = new Param("TargetUserId", targetUserId);

		OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
				+ "/im/newsession", params,
				new OkHttpClientManager.ResultCallback<ResponseNewSession>() {

					@Override
					public void onError(Request request, Exception e) {
						Log.d("", "----NewSessionFail");
					}

					@Override
					public void onResponse(ResponseNewSession response) {

						if (response.isSuccess()) {
							listener.syncSuccess(response.getSessionId());
						} else {
							listener.syncError();
						}
					}
				});
	}

}
