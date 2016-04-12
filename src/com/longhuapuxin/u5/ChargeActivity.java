package com.longhuapuxin.u5;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.db.bean.VerifyBankCard;
import com.longhuapuxin.entity.ResponseCharge;
import com.longhuapuxin.entity.ResponseVerify;
import com.longhuapuxin.uppay.UpPayBaseActivity;
import com.squareup.okhttp.Request;

/**
 * Created by ZH on 2016/1/15.
 * Email zh@longhuapuxin.com
 */
public class ChargeActivity extends BaseActivity {

    @ViewInject(R.id.etAmount)
    private EditText mAmount;

    public static final int RESULT_PAY = 1000;
    public static final int REQUEST_PAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge);
        initHeader(R.string.charge);
        ViewUtils.inject(this);
    }

    @OnClick(R.id.tvCharge)
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.tvCharge:
                String amount = mAmount.getText().toString();
                if(!TextUtils.isEmpty(amount)) {
                    bindBank(amount);
                }
            break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PAY || resultCode == RESULT_PAY) {
            finish();
        }
    }

    private void bindBank(final String amount) {
        WaitDialog.instance().showWaitNote(this);
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[3];
        params[0] = new OkHttpClientManager.Param("UserId", Settings.instance().getUserId());
        params[1] = new OkHttpClientManager.Param("Token", Settings.instance().getToken());
        params[2] = new OkHttpClientManager.Param("Quantity", amount);

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/bean/newpurchase", params,
                new OkHttpClientManager.ResultCallback<ResponseCharge>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("newappoint.onError"
                                + e.toString());
                        WaitDialog.instance().hideWaitNote();
                    }

                    @Override
                    public void onResponse(ResponseCharge response) {
                        Logger.info("onResponse is: "
                                + response.toString());
                        WaitDialog.instance().hideWaitNote();
                        if (response.isSuccess()) {

                            Intent intent = new Intent(ChargeActivity.this, UpPayBaseActivity.class);
                            intent.putExtra("money", amount);
                            intent.putExtra("orderType", "4");
                            intent.putExtra("orderId", response.getPurchaseCode());
                            startActivityForResult(intent, REQUEST_PAY);
                        }
                    }

                });
    }
}
