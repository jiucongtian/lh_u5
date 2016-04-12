package com.longhuapuxin.u5;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.longhuapuxin.adapter.ProductAmountAdapter;
import com.longhuapuxin.entity.ResponseGetProducts;
import com.longhuapuxin.uppay.UpPayBaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.longhuapuxin.entity.ResponseGetProducts.*;

/**
 * Created by ZH on 2016/1/15.
 * Email zh@longhuapuxin.com
 */
public class MySubscriptionActivity extends BaseActivity implements ProductAmountAdapter.RefreshMapLinstener, View.OnClickListener {

    @ViewInject(R.id.listview)
    ListView mListView;
    private ProductAmountAdapter mAdapter;
    private Map<ResponseGetProducts.Product, Integer> mBookedMap;
    private List<ResponseGetProducts.Product> mProductslist;
    private TextView total;
    private double mTotal;
    private TextView nextStep;
    private String orderId;
    private final int Req_Pay=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_subscription);
        initHeader(R.string.appointment);
        ViewUtils.inject(this);
        orderId = getIntent().getStringExtra("orderId");
        total = (TextView) findViewById(R.id.total);
        nextStep = (TextView) findViewById(R.id.tvNextStep);
        nextStep.setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.listview);
        mProductslist = new ArrayList<ResponseGetProducts.Product>();
        mBookedMap = new HashMap<ResponseGetProducts.Product, Integer>();
        adjustData();
        mAdapter = new ProductAmountAdapter(this, mProductslist, mBookedMap);
        mAdapter.setRefreshMapLinstener(this);
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChangedWithImages();
        adjustTotal();
    }

    private void adjustTotal() {
        mTotal = 0;
        Iterator iter = mBookedMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            ResponseGetProducts.Product key = (ResponseGetProducts.Product) entry.getKey();
            Integer val = (Integer) entry.getValue();
            mTotal += Double.valueOf(key.getPrice()).doubleValue() * val;
        }
        total.setText("总价：" + mTotal);
    }

    private void adjustData() {
        ResponseGetProducts r = new ResponseGetProducts();
        ResponseGetProducts.Product p = null;
        Intent intent = getIntent();
        String appointProduct = intent.getStringExtra("product");
        String[] products = appointProduct.split(";");
        for (String product :
                products) {
            String[] pp = product.split(",");
            p = r.new Product();
            p.setCode(pp[0]);
            p.setName(pp[4]);
            p.setDescription("");
            p.setUnit(pp[3]);
            p.setPrice(pp[2]);
            p.setPhotos(pp[5]);
            mBookedMap.put(p, (int) Double.valueOf(pp[1]).doubleValue());
            mProductslist.add(p);
        }
    }


    @Override
    public void refreshMap() {
        adjustTotal();
    }

    @Override
    public void onClick(View v) {
        if (v == nextStep) {
            Intent intent = new Intent(MySubscriptionActivity.this, UpPayBaseActivity.class);
            intent.putExtra("money", "" + mTotal);
            intent.putExtra("orderType", "3");
            intent.putExtra("orderId", orderId);
            startActivityForResult(intent,Req_Pay);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==UpPayBaseActivity.RESULT_PAY){
            this.setResult(UpPayBaseActivity.RESULT_PAY);
            finish();
        }
    }

}
