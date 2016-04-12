package com.longhuapuxin.fragment;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.longhuapuxin.adapter.TransactionUnCompleteAdapter;
import com.longhuapuxin.adapter.TransactionUnCompleteAdapter.StartActivityForResultListener;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.entity.ResponseGetOrderList;
import com.longhuapuxin.entity.ResponseGetOrderList.Order;
import com.longhuapuxin.u5.OrderActivity;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.u5.Settings;
import com.longhuapuxin.view.PullToRefreshListView;
import com.longhuapuxin.view.PullToRefreshListView.IOnLoadMoreListener;
import com.squareup.okhttp.Request;

@SuppressLint({"NewApi", "ValidFragment"})
public class FragmentTransactionUnComplete extends Fragment implements
        IOnLoadMoreListener, StartActivityForResultListener {
    private static final int DATA_LOAD_SIZE = 20;
    private static final int START_ACTIVITY_REQUEST = 1;
    private static final int START_ACTIVITY_RESULT = 2;
    public static final int RESULT_PAY = 1000;
    public static final int REQUEST_PAY = 2000;
    private int mCurrentIndex = 1;
    private List<Order> orderListUnComplete;
    private PullToRefreshListView orderUnCompleteListView;
    private TransactionUnCompleteAdapter transactionUnCompleteAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction_uncomplete, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        httpRequestGetOrderList(1);
    }

    private void initViews() {
        orderUnCompleteListView = (PullToRefreshListView) getView()
                .findViewById(R.id.order_uncomplete_listview);
        orderUnCompleteListView.setOnLoadMoreListener(this);
        transactionUnCompleteAdapter = new TransactionUnCompleteAdapter(
                getActivity(), orderListUnComplete);
        transactionUnCompleteAdapter.setStartActivityForResultListener(this);
        orderUnCompleteListView.setAdapter(transactionUnCompleteAdapter);
    }

    private void adjustListSize(int total) {
        if (total <= DATA_LOAD_SIZE) {
            orderUnCompleteListView.onLoadMoreComplete(true);
        } else {
            orderUnCompleteListView.onLoadMoreComplete(false);
            mCurrentIndex++;
        }
    }

    private void httpRequestGetOrderList(final int transactionStatus) {
        WaitDialog.instance().showWaitNote(getActivity());
        Param[] params = new Param[5];
        params[0] = new Param("UserId", Settings.instance().getUserId());
        params[1] = new Param("Token", Settings.instance().getToken());
        params[2] = new Param("OrderStatus", String.valueOf(transactionStatus));
        params[3] = new Param("PageIndex", String.valueOf(mCurrentIndex));
        params[4] = new Param("PageSize", "20");

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/order/getorderlist", params,
                new OkHttpClientManager.ResultCallback<ResponseGetOrderList>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        WaitDialog.instance().hideWaitNote();
                    }

                    @Override
                    public void onResponse(ResponseGetOrderList response) {
                        if (response.isSuccess()) {
                            // if (orderUnCompleteListView == null) {
                            // }
                            // if (response.getOrders().size() < DATA_LOAD_SIZE)
                            // {
                            // orderUnCompleteListView
                            // .onLoadMoreComplete(true);
                            // } else {
                            // orderUnCompleteListView
                            // .onLoadMoreComplete(false);
                            // mCurrentIndex++;
                            // }
                            //
                            // // 刷新列表
                            // orderListUnComplete.addAll(response.getOrders());
                            // transactionUnCompleteAdapter.notifyDataSetChanged();
                            orderListUnComplete = response.getOrders();
                            WaitDialog.instance().hideWaitNote();
                            initViews();
                            adjustListSize(response.getTotal());
                        } else {
                            WaitDialog.instance().hideWaitNote();
//							Log.d("", "------httpRequestGetOrderList error"
//									+ response.getErrorMessage());
//							Log.d("", "------httpRequestGetOrderList error"
//									+ response.getErrorCode());
                            Logger.info("------httpRequestGetOrderList error"
                                    + response.getErrorMessage());
                            Logger.info("------httpRequestGetOrderList error"
                                    + response.getErrorCode());
                        }
                    }

                });

    }

    private void httpRequestGetOrderListNotFirst(final int transactionStatus) {
        WaitDialog.instance().showWaitNote(getActivity());
        Param[] params = new Param[5];
        params[0] = new Param("UserId", Settings.instance().getUserId());
        params[1] = new Param("Token", Settings.instance().getToken());
        params[2] = new Param("OrderStatus", String.valueOf(transactionStatus));
        params[3] = new Param("PageIndex", String.valueOf(mCurrentIndex));
        params[4] = new Param("PageSize", "20");

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/order/getorderlist", params,
                new OkHttpClientManager.ResultCallback<ResponseGetOrderList>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        WaitDialog.instance().hideWaitNote();
                    }

                    @Override
                    public void onResponse(ResponseGetOrderList response) {
                        if (response.isSuccess()) {
                            if (response.getOrders().size() <= DATA_LOAD_SIZE) {
                                orderUnCompleteListView
                                        .onLoadMoreComplete(true);
                            } else {
                                orderUnCompleteListView
                                        .onLoadMoreComplete(false);
                                mCurrentIndex++;
                            }

                            // 刷新列表
                            orderListUnComplete.addAll(response.getOrders());
                            transactionUnCompleteAdapter.notifyDataSetChanged();
                            // orderListUnComplete = response.getOrders();
                            WaitDialog.instance().hideWaitNote();
                        } else {
                            WaitDialog.instance().hideWaitNote();
//							Log.d("", "------httpRequestGetOrderList error"
//									+ response.getErrorMessage());
//							Log.d("", "------httpRequestGetOrderList error"
//									+ response.getErrorCode());
                            Logger.info("------httpRequestGetOrderList error"
                                    + response.getErrorMessage());
                            Logger.info("------httpRequestGetOrderList error"
                                    + response.getErrorCode());
                        }
                    }

                });

    }

    @Override
    public void OnLoadMore() {
        httpRequestGetOrderListNotFirst(1);
    }

    private int position;

    @Override
    public void mStartActivity(Intent intent, int result, int position) {
        this.position = position;
        startActivityForResult(intent, result);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == START_ACTIVITY_REQUEST && resultCode == START_ACTIVITY_RESULT) {
            if (data != null) {
                for (Order order : orderListUnComplete) {
                    if (order.getId() == Integer.valueOf(data
                            .getStringExtra("orderId"))) {
                        orderListUnComplete.remove(order);
                        transactionUnCompleteAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        } else if (requestCode == REQUEST_PAY && resultCode == RESULT_PAY) {
//            orderListUnComplete.get(position).setPaymentStatus("1");
            orderListUnComplete.remove(position);
            transactionUnCompleteAdapter.notifyDataSetChanged();
            OrderActivity activity = (OrderActivity) getActivity();
            activity.refreshCompleteTransaction();
        }
    }
}
