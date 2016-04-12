package com.longhuapuxin.common;

import android.content.Context;

import com.longhuapuxin.entity.ResponseDad;
import com.longhuapuxin.entity.ResponseGetWalletOperation;
import com.longhuapuxin.u5.Settings;
import com.squareup.okhttp.Request;

/**
 * Created by asus on 2016/1/12.
 */
public class PayRequestTools {


    public static void httpRequestCompleteWalletOperation(Context context, String id, String status, String payWay, String payPassword) {
        WaitDialog.instance().showWaitNote(context);
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[5];
        params[0] = new OkHttpClientManager.Param("Id", id);
        params[1] = new OkHttpClientManager.Param("Status", status);
        params[2] = new OkHttpClientManager.Param("ResultText", "");
        params[3] = new OkHttpClientManager.Param("PayWay", payWay);
        params[4] = new OkHttpClientManager.Param("PayPassword", payPassword);

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/basic/completewalletoperation", params,
                new OkHttpClientManager.ResultCallback<ResponseDad>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("onError"
                                + e.toString());
                        WaitDialog.instance().hideWaitNote();
                    }

                    @Override
                    public void onResponse(ResponseDad response) {
                        Logger.info("onResponse"
                                + response);
                        if (response.isSuccess()) {
                            WaitDialog.instance().hideWaitNote();
                        } else {
                            Logger.info("httpRequestCompleteWalletOperation " + response.getErrorCode());
                            Logger.info("httpRequestCompleteWalletOperation " + response.getErrorMessage());
                            WaitDialog.instance().hideWaitNote();

                        }
                    }

                });
    }

    public static void httpRequestGetWalletOperation(Context context, String id) {
        WaitDialog.instance().showWaitNote(context);
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[1];
        params[0] = new OkHttpClientManager.Param("Id", id);

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/basic/getwalletoperation", params,
                new OkHttpClientManager.ResultCallback<ResponseGetWalletOperation>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("onError"
                                + e.toString());
                        WaitDialog.instance().hideWaitNote();
                    }

                    @Override
                    public void onResponse(ResponseGetWalletOperation response) {
                        Logger.info("onResponse"
                                + response);
                        if (response.isSuccess()) {
                            WaitDialog.instance().hideWaitNote();
                        } else {
                            Logger.info("httpRequestGetWalletOperation " + response.getErrorCode());
                            Logger.info("httpRequestGetWalletOperation " + response.getErrorMessage());
                            WaitDialog.instance().hideWaitNote();

                        }
                    }

                });
    }
}
