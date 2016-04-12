package com.longhuapuxin.u5;

import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.longhuapuxin.adapter.AppointAdapter;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.entity.ResponseGetAppoints;
import com.longhuapuxin.uppay.UpPayBaseActivity;
import com.longhuapuxin.view.PullToRefreshListView;
import com.squareup.okhttp.Request;

import java.util.List;

/**
 * Created by asus on 2016/1/5.
 */
public class AppointmentActivity extends BaseActivity implements
        PullToRefreshListView.IOnLoadMoreListener {
    private PullToRefreshListView mListView;
    private static final int DATA_LOAD_SIZE = 20;
    private int mCurrentIndex = 1;
    private List<ResponseGetAppoints.Appoint> appoints;
    private AppointAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);
        initHeader(R.string.appointment);
        httpRequestGeAppointmentList();
    }

    private void initViews() {
        mListView = (PullToRefreshListView) findViewById(R.id.listWithDraw);
        mListAdapter = new AppointAdapter(this, appoints);
        mListView.setAdapter(mListAdapter);
        mListView.setOnLoadMoreListener(this);

    }

    private void adjustListSize(int total) {
        if (total <= DATA_LOAD_SIZE) {
            mListView.onLoadMoreComplete(true);
        } else {
            mListView.onLoadMoreComplete(false);
            mCurrentIndex++;
        }
    }

    private void httpRequestGetAppointmentListNotFirst() {
        WaitDialog.instance().showWaitNote(this);
        Settings setting = Settings.instance();
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[4];
        params[0] = new OkHttpClientManager.Param("Token", setting.getToken());
        params[1] = new OkHttpClientManager.Param("UserId", setting.getUserId());

        params[2] = new OkHttpClientManager.Param("PageSize", "20");
        params[3] = new OkHttpClientManager.Param("PageIndex", String.valueOf(mCurrentIndex));

        OkHttpClientManager
                .postAsyn(
                        setting.getApiUrl() + "/shop/getappoints",
                        params,
                        new OkHttpClientManager.ResultCallback<ResponseGetAppoints>() {
                            @Override
                            public void onError(Request request, Exception e) {
                                WaitDialog.instance().hideWaitNote();
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(
                                    ResponseGetAppoints response) {
                                if (response.isSuccess()) {

                                    if (response.getAppoints().size() <= DATA_LOAD_SIZE) {
                                        mListView.onLoadMoreComplete(true);
                                    } else {
                                        mListView.onLoadMoreComplete(false);
                                        mCurrentIndex++;
                                    }

                                    // 刷新列表
                                    appoints.addAll(response.getAppoints());
                                    mListAdapter.notifyDataSetChanged();
                                    // orderListUnComplete =
                                    // response.getOrders();
                                    WaitDialog.instance().hideWaitNote();
                                } else {
                                    WaitDialog.instance().hideWaitNote();
                                }
                            }
                        });

    }

    private void httpRequestGeAppointmentList() {
        WaitDialog.instance().showWaitNote(this);
        Settings setting = Settings.instance();
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[4];
        params[0] = new OkHttpClientManager.Param("Token", setting.getToken());
        params[1] = new OkHttpClientManager.Param("UserId", setting.getUserId());

        params[2] = new OkHttpClientManager.Param("PageSize", "20");
        params[3] = new OkHttpClientManager.Param("PageIndex", String.valueOf(mCurrentIndex));

        OkHttpClientManager
                .postAsyn(
                        setting.getApiUrl() + "/shop/getappoints",
                        params,
                        new OkHttpClientManager.ResultCallback<String>() {
                            @Override
                            public void onError(Request request, Exception e) {
                                WaitDialog.instance().hideWaitNote();
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(
                                    String resultText) {
                                Gson gson=new Gson();
                                ResponseGetAppoints response=gson.fromJson(resultText,ResponseGetAppoints.class);
                                if (response.isSuccess()) {
                                    appoints = response.getAppoints();
                                    WaitDialog.instance().hideWaitNote();
                                    initViews();
                                    adjustListSize(response.getTotal());
                                } else {
                                    WaitDialog.instance().hideWaitNote();
                                }
                            }
                        });

    }

    @Override
    public void OnLoadMore() {
        httpRequestGetAppointmentListNotFirst();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== UpPayBaseActivity.RESULT_PAY){
            httpRequestGeAppointmentList();
        }
    }
}
