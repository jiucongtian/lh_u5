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
 * Created by asus on 2016/1/6.
 */
public class SetAddressActivity extends BaseActivity implements View.OnClickListener {
    private EditText name, phone, address, detailAddress;
    private RelativeLayout defaultLayout;
    private TextView defaultTv, errorTx;
    private ImageView defaultImg;
    private Button ok;
    private MyAddress item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_address);
        initHeader("修改收货地址");
        initViews();
        adjustStatue();
    }

    private int id;

    private void adjustStatue() {
        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        if (id == -1) {
            errorTx.setText("数据加载失败，请重试");
            return;
        }
        MyAddressDao myAddressHelper = new MyAddressDao(this);
        item = myAddressHelper.get(id);
        name.setText(item.getName());
        phone.setText(item.getPhone());
        address.setText(item.getAddress());
        detailAddress.setText(item.getDetailAddress());
        if (item.isDefault()) {
            isDefault = true;
            defaultTv.setTextColor(getResources().getColor(R.color.orange));
            defaultImg.setImageResource(R.drawable.shop_product_select_sel);
        } else {
            isDefault = false;
            defaultTv.setTextColor(getResources().getColor(R.color.black));
            defaultImg.setImageResource(R.drawable.shop_product_select);
        }
    }

    private void initViews() {
        errorTx = (TextView) findViewById(R.id.error_tx);
        ok = (Button) findViewById(R.id.ok);
        ok.setOnClickListener(this);
        name = (EditText) findViewById(R.id.name);
        phone = (EditText) findViewById(R.id.phone);
        address = (EditText) findViewById(R.id.address);
        detailAddress = (EditText) findViewById(R.id.detail_address);
        defaultTv = (TextView) findViewById(R.id.default_tv);
        defaultImg = (ImageView) findViewById(R.id.default_img);
        defaultLayout = (RelativeLayout) findViewById(R.id.defalut_layout);
        defaultLayout.setOnClickListener(this);
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

    private boolean isDefault = false;

    @Override
    public void onClick(View v) {
        if (v == defaultLayout) {
            if (isDefault) {
                isDefault = false;
                defaultTv.setTextColor(getResources().getColor(R.color.black));
                defaultImg.setImageResource(R.drawable.shop_product_select);
            } else {
                isDefault = true;
                defaultTv.setTextColor(getResources().getColor(R.color.orange));
                defaultImg.setImageResource(R.drawable.shop_product_select_sel);
            }
        } else if (v == ok) {
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
            MyAddressDao myAddressHelper = new MyAddressDao(this);
            item.setName(name.getText().toString());
            item.setPhone(phone.getText().toString());
            item.setAddress(address.getText().toString());
            item.setDetailAddress(detailAddress.getText().toString());
            item.setIsDefault(isDefault);
            myAddressHelper.update(item);
            Intent intent = new Intent(SetAddressActivity.this, MyAddressActivity.class);
            startActivity(intent);
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
