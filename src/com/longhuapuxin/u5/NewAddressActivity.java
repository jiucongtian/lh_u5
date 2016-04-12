package com.longhuapuxin.u5;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.longhuapuxin.dao.MyAddressDao;
import com.longhuapuxin.db.bean.MyAddress;

import java.util.List;

/**
 * Created by asus on 2016/1/5.
 */
public class NewAddressActivity extends BaseActivity implements View.OnClickListener {
    private Button ok;
    private MyAddress item;
    private EditText name, phone, address, detailAddress;
    private boolean isDefault = false;
    private TextView defaultTv, errorTx;
    private ImageView defaultImg;
    private RelativeLayout defaultLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_address);
        initHeader("新建收货地址");
        initViews();
    }

    private void initViews() {
        ok = (Button) findViewById(R.id.ok);
        ok.setOnClickListener(this);
        name = (EditText) findViewById(R.id.name);
        phone = (EditText) findViewById(R.id.phone);
        address = (EditText) findViewById(R.id.location);
        detailAddress = (EditText) findViewById(R.id.address);
        defaultTv = (TextView) findViewById(R.id.default_tv);
        defaultImg = (ImageView) findViewById(R.id.default_img);
        defaultLayout = (RelativeLayout) findViewById(R.id.defalut_layout);
        defaultLayout.setOnClickListener(this);
        errorTx = (TextView) findViewById(R.id.error_tx);
        name.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                return (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });
        phone.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                return (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });
        address.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                return (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });
        detailAddress.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                return (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });

    }


    @Override
    public void onClick(View v) {
        if (v == ok) {
            errorTx.setText("");
            if (TextUtils.isEmpty(name.getText().toString())) {
                errorTx.setText("收货人姓名不能为空");
                return;
            } else if (TextUtils.isEmpty(phone.getText().toString())) {
                errorTx.setText("手机号码不能为空");
                return;
            } else if (phone.getText().toString().length() != 11) {
                errorTx.setText("请输入正确手机号");
                return;
            } else if (TextUtils.isEmpty(address.getText().toString())) {
                errorTx.setText("省、市、区不能为空");
                return;
            } else if (TextUtils.isEmpty(detailAddress.getText().toString())) {
                errorTx.setText("详细地址名不能为空");
                return;
            }
            if (isDefault) {
                clearDefault();
            }
            item = new MyAddress(name.getText().toString(), phone.getText().toString(), address.getText().toString(), detailAddress.getText().toString(), isDefault);
            MyAddressDao myAddressHelper = new MyAddressDao(this);
            myAddressHelper.add(item);
            Intent intent = new Intent(NewAddressActivity.this, MyAddressActivity.class);
            startActivity(intent);
        } else if (defaultLayout == v) {
            if (isDefault == false) {
                isDefault = true;
                defaultImg.setImageResource(R.drawable.shop_product_select_sel);
                defaultTv.setTextColor(getResources().getColor(R.color.orange));
            } else {
                isDefault = false;
                defaultImg.setImageResource(R.drawable.shop_product_select);
                defaultTv.setTextColor(getResources().getColor(R.color.grey));
            }
        }

    }

    private void clearDefault() {
        MyAddressDao myAddressHelper = new MyAddressDao(this);
        List<MyAddress> items = myAddressHelper.fetchAll();
        for (MyAddress item :
                items) {
            if (item.isDefault()) {
                item.setIsDefault(false);
                myAddressHelper.update(item);
            }
        }
    }
}
