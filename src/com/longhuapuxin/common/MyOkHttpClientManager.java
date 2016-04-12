package com.longhuapuxin.common;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.common.OkHttpClientManager.ResultCallback;
import com.squareup.okhttp.Request;

public class MyOkHttpClientManager {
	public static void postAsyn(final Context context,String loadingMessage,String url,Param[] params,final OkHttpClientManager.ResultCallback<String> resultcallback){
//		main.ShowDialog(loadingMessage);
		try{
			OkHttpClientManager.getHttpsDelegate().setCertificates(context.getAssets().open("longhuapuxin.cer"));
			OkHttpClientManager.postAsyn(url, params,
				new OkHttpClientManager.ResultCallback<String>() {
					@Override
					public void onError(Request request, Exception e) {
						resultcallback.onError(request, e);
//						main.HideDialog();
					}

					@Override
					public void onResponse(String u) {
						resultcallback.onResponse(u);
//						main.HideDialog();
					}
				});
		}
		catch(Exception ex){
			Toast.makeText(context, ex.getMessage(),
					Toast.LENGTH_LONG).show();
			ex.printStackTrace();
			Log.d("AA", ex.getMessage());
		}
	}
	public static void deliveryResult(final Context context,String loadingMessage , Request request,final ResultCallback<String> callback)
	{
//		context.ShowDialog(loadingMessage);
		try {
			OkHttpClientManager.getClinet().setConnectTimeout(900, TimeUnit.SECONDS);
			OkHttpClientManager.getHttpsDelegate().setCertificates(context.getAssets().open("longhuapuxin.cer"));
			OkHttpClientManager.getInstance().deliveryResult(new ResultCallback<String>(){

				@Override
				public void onError(Request request, Exception e) {
					// TODO Auto-generated method stub
					callback.onError(request, e);
//					context.HideDialog();
				}

				@Override
				public void onResponse(String response) {
					// TODO Auto-generated method stub
					callback.onResponse(response);
//					context.HideDialog();
				}
				
			}, request);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
	}
}

