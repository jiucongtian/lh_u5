package com.longhuapuxin.u5;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.longhuapuxin.adapter.BillAdapter;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.db.bean.BeanRecord;
import com.longhuapuxin.entity.ResponseGetBeanRecord;
import com.longhuapuxin.view.PullToRefreshListView;
import com.squareup.okhttp.Request;
import java.util.List;

/**
 * Created by asus on 2016/1/13.
 */
public class MyBillListActivity extends BaseActivity implements
        PullToRefreshListView.IOnLoadMoreListener {
    private PullToRefreshListView mListView;
    private ImageView billListViewEmpty;
    private static final int DATA_LOAD_SIZE = 20;
    private int mCurrentIndex = 1;
    private List<BeanRecord> beanRecordList;
    private BillAdapter mListAdapter;
    private TextView mTotalTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bill_list);
        initHeader("流水账单");
        mTotalTv = (TextView) findViewById(R.id.tvTotal);
        mTotalTv.setText(Settings.instance().User.getBalance());
        httpRequestGetBeanRecord();
    }

    private void initViews() {
        billListViewEmpty = (ImageView) findViewById(R.id.bill_list_empty);
        mListView = (PullToRefreshListView) findViewById(R.id.bill_list);
        mListAdapter = new BillAdapter(this, beanRecordList);
        mListView.setAdapter(mListAdapter);
        mListView.setOnLoadMoreListener(this);
        mTotalTv.setText(Settings.instance().User.getBalance());
    }

    private void adjustListSize(int total) {
        if (total > 0) {
            if (billListViewEmpty.getVisibility() == View.VISIBLE) {
                billListViewEmpty.setVisibility(View.GONE);
            }
        }
        if (total <= DATA_LOAD_SIZE) {
            mListView.onLoadMoreComplete(true);
        } else {
            mListView.onLoadMoreComplete(false);
            mCurrentIndex++;
        }
    }

    private void httpRequestGetBeanRecord() {
        WaitDialog.instance().showWaitNote(this);
        Settings setting = Settings.instance();
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[5];
        params[0] = new OkHttpClientManager.Param("Token", setting.getToken());
        params[1] = new OkHttpClientManager.Param("UserId", setting.getUserId());
        params[2] = new OkHttpClientManager.Param("PageSize", "20");
        params[3] = new OkHttpClientManager.Param("PageIndex", String.valueOf(mCurrentIndex));
        params[4] = new OkHttpClientManager.Param("ShopCode", "");

        OkHttpClientManager
                .postAsyn(
                        setting.getApiUrl() + "/bean/getbeanrecord",
                        params,
                        new OkHttpClientManager.ResultCallback<ResponseGetBeanRecord>() {
                            @Override
                            public void onError(Request request, Exception e) {
                                WaitDialog.instance().hideWaitNote();
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(
                                    ResponseGetBeanRecord response) {
                                if (response.isSuccess()) {
                                    beanRecordList = response.getBeanRecords();
                                    Settings.instance().User.setBalance(String.valueOf( response.getBalance()));
                                    WaitDialog.instance().hideWaitNote();
                                    initViews();
                                    adjustListSize(response.getTotal());
                                } else {
                                    WaitDialog.instance().hideWaitNote();
                                }
                            }
                        });

    }

    private void httpRequestGetBeanRecordNotFirst() {
        WaitDialog.instance().showWaitNote(this);
        Settings setting = Settings.instance();
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[5];
        params[0] = new OkHttpClientManager.Param("Token", setting.getToken());
        params[1] = new OkHttpClientManager.Param("UserId", setting.getUserId());
        params[2] = new OkHttpClientManager.Param("PageSize", String.valueOf(DATA_LOAD_SIZE));
        params[3] = new OkHttpClientManager.Param("PageIndex", Integer.toString(mCurrentIndex));
        params[4] = new OkHttpClientManager.Param("ShopCode", "");

        OkHttpClientManager
                .postAsyn(
                        setting.getApiUrl() + "/bean/getbeanrecord",
                        params,
                        new OkHttpClientManager.ResultCallback<ResponseGetBeanRecord>() {
                            @Override
                            public void onError(Request request, Exception e) {
                                e.printStackTrace();
                                WaitDialog.instance().hideWaitNote();
                            }

                            @Override
                            public void onResponse(
                                    ResponseGetBeanRecord response) {
                                if (response.isSuccess()) {
                                    if (response.getBeanRecords().size() <= DATA_LOAD_SIZE) {
                                        mListView.onLoadMoreComplete(true);
                                    } else {
                                        mListView.onLoadMoreComplete(false);
                                        mCurrentIndex++;
                                    }

                                    // 刷新列表
                                    beanRecordList.addAll(response
                                            .getBeanRecords());
                                    mListAdapter.notifyDataSetChanged();
                                    WaitDialog.instance().hideWaitNote();
                                } else {
                                    WaitDialog.instance().hideWaitNote();
                                }
                            }
                        });

    }

    @Override
    public void OnLoadMore() {
        httpRequestGetBeanRecordNotFirst();
    }
}
