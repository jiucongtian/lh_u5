package com.longhuapuxin.u5;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.entity.ResponseNewPayment;
import com.longhuapuxin.uppay.UpPayBaseActivity;
import com.squareup.okhttp.Request;

/**
 * Created by ZH on 2016/2/23.
 * Email zh@longhuapuxin.com
 */
public class ConfirmMoneyActivity extends BaseActivity implements View.OnClickListener {
    private EditText password;
    private TextView goPay;
    private String shopCode;
    private static boolean isPaying=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isPaying=false;
        setContentView(R.layout.activity_confirm_money);
        init();
        getData();

    }

    private void getData() {
        shopCode = getIntent().getStringExtra("shopCode");
    }

    private void init() {
        initHeader(R.string.Payment);
        password = (EditText) findViewById(R.id.password);
        goPay = (TextView) findViewById(R.id.GoPay);
        goPay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == goPay) {
            if( isPaying) return;
            if (!TextUtils.isEmpty(password.getText().toString())) {
                isPaying=true;
                httpRequestNewPayment(shopCode);
            }
        }
    }

    private void httpRequestNewPayment(final String shopCode) {
        WaitDialog.instance().showWaitNote(this);
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[5];
        params[0] = new OkHttpClientManager.Param("Token", Settings.instance().getToken());
        params[1] = new OkHttpClientManager.Param("UserId", Settings.instance().getUserId());
        params[2] = new OkHttpClientManager.Param("Total", password.getText().toString());
        params[3] = new OkHttpClientManager.Param("ShopCode", shopCode);
        params[4] = new OkHttpClientManager.Param("CustomerId", Settings.instance().getUserId());

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/pay/newpayment", params,
                new OkHttpClientManager.ResultCallback<ResponseNewPayment>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        WaitDialog.instance().hideWaitNote();
                        isPaying=false;
                    }

                    @Override
                    public void onResponse(ResponseNewPayment response) {

                        if (response.isSuccess()) {
                            Intent intent = new Intent(ConfirmMoneyActivity.this, UpPayBaseActivity.class);
                            intent.putExtra("money", "" + password.getText().toString());
                            intent.putExtra("orderType", "2");
                            intent.putExtra("orderId", response.getPaymentCode());
                            intent.putExtra("shopCode", shopCode);
                            startActivity(intent);

                        } else {
                            isPaying=false;
                            Toast.makeText(ConfirmMoneyActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        }
                        WaitDialog.instance().hideWaitNote();
                    }

                });
    }
}
