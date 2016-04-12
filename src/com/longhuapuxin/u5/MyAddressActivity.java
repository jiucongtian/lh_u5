package com.longhuapuxin.u5;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.longhuapuxin.adapter.AddressAdapter;
import com.longhuapuxin.dao.MyAddressDao;
import com.longhuapuxin.db.bean.MyAddress;
import com.longhuapuxin.view.SlideCutListViewBase;
import com.longhuapuxin.view.SlideView;

import java.util.Collections;
import java.util.List;

/**
 * Created by asus on 2016/1/5.
 */
public class MyAddressActivity extends BaseActivity implements SlideCutListViewBase.OnSlideListener {
    private SlideCutListViewBase addressListView;
    private AddressAdapter addressAdapter;
    private List<MyAddress> addresses;
    private SlideView mLastSlideViewWithStatusOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_address);
        initHeader("收货地址");
        enableRightTextBtn(R.string.add, true, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyAddressActivity.this, NewAddressActivity.class);
                startActivity(intent);
            }
        });
        adjustData();
        initViews();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        addressAdapter.refresh(refreshData());
    }

    private void adjustData() {
        MyAddressDao myAddressHelper = new MyAddressDao(this);
        addresses = myAddressHelper.fetchAll();
    }

    private List<MyAddress> refreshData() {
        MyAddressDao myAddressHelper = new MyAddressDao(this);
        addresses = myAddressHelper.fetchAll();
        return addresses;
    }

    private void initViews() {
        addressListView = (SlideCutListViewBase) findViewById(R.id.addressListView);
        addressAdapter = new AddressAdapter(this, addresses);
        addressListView.setAdapter(addressAdapter);
        addressListView.setOnSlideListener(this);
        addressListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                addressAdapter.closeAllSlideView();
                Intent intent = new Intent(MyAddressActivity.this, SetAddressActivity.class);
                intent.putExtra("id", addresses.get(i).getId());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onSlide(View view, int status) {
        if (status == DOWN && mLastSlideViewWithStatusOn != null
                && mLastSlideViewWithStatusOn != view) {
            addressListView.setListViewNotMove(true);
            mLastSlideViewWithStatusOn.shrink();
            addressListView.setListViewNotMove(false);
        }

        if (status == SLIDE_STATUS_START_SCROLL) {
            mLastSlideViewWithStatusOn = (SlideView) view;
        }
    }

    public void SlideCutListViewCloseItem() {
        addressAdapter.closeAllSlideView();
    }

}
