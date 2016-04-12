package com.longhuapuxin.u5;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.entity.ResponseDad;
import com.longhuapuxin.entity.ResponseGetProducts.Product;
import com.longhuapuxin.fragment.ConfirmBookFragment;
import com.longhuapuxin.fragment.SubscribeProductFragment;
import com.squareup.okhttp.Request;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by ZH on 2016/1/5.
 * Email zh@longhuapuxin.com
 */
public class BookProductDialogActivity extends BaseActivity implements View.OnClickListener {

    public final static int BOOKED_PRODUCTS = 0x01;
    public final static int SHOP_CODE = 0x02;

    private FragmentManager fm = null;
    public Order mOrder;

    private View mNoDataContainer;

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.tvBack:
                cancel();
                break;
        }
    }

    public enum BookStatus {
        NO_DATA,
        CONFIRM_AMOUNT,
        COMFIRM_DESC,
        SUBMIT_ORDER
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOrder = new Order();
        mOrder.bookedMap = getPram(BOOKED_PRODUCTS);
        mOrder.shopCode = getPram(SHOP_CODE);
        setContentView(R.layout.activity_book_product);

        init();
    }

    private void init() {
        fm = getFragmentManager();
        mNoDataContainer = findViewById(R.id.noDataContainer);
        findViewById(R.id.tvBack).setOnClickListener(this);
        if(mOrder.bookedMap.size() > 0) {
            stateManager(BookStatus.CONFIRM_AMOUNT);
        } else {
            stateManager(BookStatus.NO_DATA);
        }
    }

    private void showProductView() {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = new SubscribeProductFragment();
        ft.replace(R.id.fragmentContainer, fragment);
        ft.commit();
    }

    private void showDescView() {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = new ConfirmBookFragment();
        ft.replace(R.id.fragmentContainer, fragment);
        ft.commit();
    }

    private void submitNewAppoint() {
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[8];
        params[0] = new OkHttpClientManager.Param("UserId", Settings.instance().getUserId());
        params[1] = new OkHttpClientManager.Param("Token", Settings.instance().getToken());
        params[2] = new OkHttpClientManager.Param("ShopCode", mOrder.shopCode);
        params[3] = new OkHttpClientManager.Param("Time1", Order.getStrDate(mOrder.fromDate));
        params[4] = new OkHttpClientManager.Param("Time2", Order.getStrDate(mOrder.toDate));
        params[5] = new OkHttpClientManager.Param("Note", mOrder.note);
        params[6] = new OkHttpClientManager.Param("Products", mOrder.getStrProducts());
        params[7] = new OkHttpClientManager.Param("Address", mOrder.address);

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/shop/newappoint", params,
                new OkHttpClientManager.ResultCallback<ResponseDad>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("newappoint.onError"
                                + e.toString());
                    }

                    @Override
                    public void onResponse(ResponseDad response) {
                        Logger.info("onResponse is: "
                                + response.toString());

                        if (response.isSuccess()) {
                        }
                    }

                });
    }

    public Map<Product, Integer> getmBookedMap() {
        return mOrder.bookedMap;
    }

    public void setmBookedMap(Map<Product, Integer> mBookedMap) {
        mOrder.bookedMap = mBookedMap;
    }

    public void cancel() {
        finish();
    }
    public void nextStep() {
        stateManager(BookStatus.COMFIRM_DESC);
    }
    public void confirmOrder() {
        stateManager(BookStatus.SUBMIT_ORDER);
    }

    private void stateManager(BookStatus status) {
        switch (status) {
            case CONFIRM_AMOUNT:
                showProductView();
                break;
            case COMFIRM_DESC:
                showDescView();
                break;
            case SUBMIT_ORDER:
                submitNewAppoint();
                cancel();
                break;
            case NO_DATA:
            default:
                mNoDataContainer.setVisibility(View.VISIBLE);
                break;
        }
    }

    public static class Order {
        public String shopCode, note, address;
        public Date fromDate, toDate;
        public Map<Product, Integer> bookedMap;

        public static String getStrDate(Date date) {
            DateFormat df = new SimpleDateFormat("yy-MM-dd HH:mm");
            return df.format(date);
        }

        public String getStrProducts() {
            String ret = "";

            for(Product key : bookedMap.keySet()) {
                ret += key.getCode();
                ret += ",";
                ret += bookedMap.get(key);
                ret += ";";
            }
            ret = ret.substring(0,ret.length() - 1);
            return ret;
        }
    }
}
