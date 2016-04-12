package com.longhuapuxin.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.longhuapuxin.adapter.TransactionCompleteAdapter;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.db.bean.TransactionMsg;
import com.longhuapuxin.entity.ResponseGetCompletedOrder;
import com.longhuapuxin.entity.ResponseGetCompletedOrder.Order;
import com.longhuapuxin.entity.ResponseGetOrderList;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.u5.Settings;
import com.longhuapuxin.view.PullToRefreshListView;
import com.longhuapuxin.view.PullToRefreshListView.IOnLoadMoreListener;
import com.squareup.okhttp.Request;

@SuppressLint("NewApi")
public class FragmentTransactionComplete extends Fragment implements
        IOnLoadMoreListener {
    private TransactionCompleteAdapter transactionCompleteAdapter;
    private PullToRefreshListView transactionCompleteListview;
    private List<TransactionMsg> orderListComplete;
    private static final int DATA_LOAD_SIZE = 20;
    private int mCurrentIndex = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction_complete, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        httpRequestGetOrderList(3);
    }

    private void initViews() {
        transactionCompleteListview = (PullToRefreshListView) getView()
                .findViewById(R.id.transaction_complete_listview);
        transactionCompleteListview.setOnLoadMoreListener(this);
        transactionCompleteAdapter = new TransactionCompleteAdapter(
                getActivity(), orderListComplete);
        transactionCompleteListview.setAdapter(transactionCompleteAdapter);
    }

    private void getDatas(List<Order> orderList) {
        orderListComplete = new ArrayList<TransactionMsg>();
        for (Order order : orderList) {
            TransactionMsg transactionMsg = new TransactionMsg();
            transactionMsg.setName(order.getNickName1());
            transactionMsg.setTime(order.getOrderTime());
            transactionMsg.setNote(order.getOrderNote());
            transactionMsg.setContent(order.getComment());
            transactionMsg.setOpen(false);
            transactionMsg.setEstimate(order.getEstimate());
            transactionMsg.setType("1");
            transactionMsg.setId(order.getId());
            transactionMsg.setFeedBackId(order.getFeedBackId());
            orderListComplete.add(transactionMsg);
        }
    }

    private void addDatas(List<Order> orderList) {
        for (Order order : orderList) {
            TransactionMsg transactionMsg = new TransactionMsg();
            transactionMsg.setName(order.getNickName1());
            transactionMsg.setTime(order.getOrderTime());
            transactionMsg.setNote(order.getOrderNote());
            transactionMsg.setContent(order.getComment());
            transactionMsg.setOpen(false);
            transactionMsg.setEstimate(order.getEstimate());
            orderListComplete.add(transactionMsg);
        }
    }

    private void adjustListSize(int total) {
        if (total <= DATA_LOAD_SIZE) {
            transactionCompleteListview.onLoadMoreComplete(true);
        } else {
            transactionCompleteListview.onLoadMoreComplete(false);
            mCurrentIndex++;
        }
    }

    @Override
    public void OnLoadMore() {
        httpRequestGetOrderListNotFirst(3);
    }

    public void refreshTransactionList(){
        mCurrentIndex = 1;
        orderListComplete.clear();
        httpRequestGetOrderList(3);
    }

    private void httpRequestGetOrderList(final int transactionStatus) {
        WaitDialog.instance().showWaitNote(getActivity());
        Param[] params = new Param[5];
        params[0] = new Param("UserId", Settings.instance().getUserId());
        params[1] = new Param("Token", Settings.instance().getToken());
        params[2] = new Param("OrderStatus", String.valueOf(transactionStatus));
        params[3] = new Param("PageIndex", String.valueOf(mCurrentIndex));
        params[4] = new Param("PageSize", "20");

        OkHttpClientManager
                .postAsyn(
                        Settings.instance().getApiUrl() + "/order/getorderlist",
                        params,
                        new OkHttpClientManager.ResultCallback<ResponseGetCompletedOrder>() {

                            @Override
                            public void onError(Request request, Exception e) {
                                WaitDialog.instance().hideWaitNote();
                            }

                            @Override
                            public void onResponse(
                                    ResponseGetCompletedOrder response) {
                                if (response.isSuccess()) {
                                    getDatas(response.getOrders());
                                    WaitDialog.instance().hideWaitNote();
                                    initViews();
                                    adjustListSize(response.getTotal());
                                } else {
                                    WaitDialog.instance().hideWaitNote();
//                                    Log.d("",
//                                            "------httpRequestGetOrderList error"
//                                                    + response
//                                                    .getErrorMessage());
                                    Logger.info("------httpRequestGetOrderList error"
                                            + response
                                            .getErrorMessage());
                                    Logger.info("------httpRequestGetOrderList error"
                                            + response
                                            .getErrorCode());

//                                    Log.d("",
//                                            "------httpRequestGetOrderList error"
//                                                    + response.getErrorCode());
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

        OkHttpClientManager
                .postAsyn(
                        Settings.instance().getApiUrl() + "/order/getorderlist",
                        params,
                        new OkHttpClientManager.ResultCallback<ResponseGetCompletedOrder>() {

                            @Override
                            public void onError(Request request, Exception e) {
                                WaitDialog.instance().hideWaitNote();
                            }

                            @Override
                            public void onResponse(
                                    ResponseGetCompletedOrder response) {
                                if (response.isSuccess()) {
                                    if (response.getOrders().size() <= DATA_LOAD_SIZE) {
                                        transactionCompleteListview
                                                .onLoadMoreComplete(true);
                                    } else {
                                        transactionCompleteListview
                                                .onLoadMoreComplete(false);
                                        mCurrentIndex++;
                                    }

                                    // 刷新列表
                                    addDatas(response.getOrders());
                                    transactionCompleteAdapter
                                            .notifyDataSetChanged();
                                    WaitDialog.instance().hideWaitNote();
                                } else {
                                    WaitDialog.instance().hideWaitNote();
//                                    Log.d("",
//                                            "------httpRequestGetOrderList error"
//                                                    + response
//                                                    .getErrorMessage());
//                                    Log.d("",
//                                            "------httpRequestGetOrderList error"
//                                                    + response.getErrorCode());
                                    Logger.info("------httpRequestGetOrderList error"
                                            + response
                                            .getErrorMessage());
                                    Logger.info("------httpRequestGetOrderList error"
                                            + response
                                            .getErrorCode());
                                }
                            }

                        });

    }
}
