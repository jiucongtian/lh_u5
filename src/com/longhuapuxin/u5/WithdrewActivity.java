package com.longhuapuxin.u5;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.entity.ResponseGetAccount;
import com.squareup.okhttp.Request;

/**
 * Created by ZH on 2016/1/15.
 * Email zh@longhuapuxin.com
 */
public class WithdrewActivity extends BaseActivity {


    @ViewInject(R.id.tvTotal)
    private TextView mTotal;

    @ViewInject(R.id.tvBankName)
    private TextView mBankName;

    @ViewInject(R.id.tvBankNumber)
    private TextView mBankNo;

    @ViewInject(R.id.tvIdNo)
    private TextView mIdNo;

    @ViewInject(R.id.etAmount)
    private EditText mAmountEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrew);
        ViewUtils.inject(this);
        initHeader(R.string.withdrew);

        ResponseGetAccount.User user = Settings.instance().User;

        mTotal.setText(user.getBalance());
        mBankName.setText(user.getBankName());
        mBankNo.setText(user.getBankAccount());
        mIdNo.setText(user.getIdNo());
    }

    @OnClick(R.id.tvSubmitWithdrew)
    public void onClick(View view) {
        Integer id = view.getId();
        switch (id) {
            case R.id.tvSubmitWithdrew:
                String amount = mAmountEt.getText().toString();
                if(!TextUtils.isEmpty(amount)) {
                    submitWithdrew(amount);
                }
                break;
        }
    }

    private void submitWithdrew(String amount) {
        WaitDialog.instance().showWaitNote(this);
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[3];
        params[0] = new OkHttpClientManager.Param("UserId", Settings.instance().getUserId());
        params[1] = new OkHttpClientManager.Param("Token", Settings.instance().getToken());
        params[2] = new OkHttpClientManager.Param("Amount", amount);

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/seller/withdraw", params,
                new OkHttpClientManager.ResultCallback<String>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("newappoint.onError"
                                + e.toString());
                        WaitDialog.instance().hideWaitNote();
                    }

                    @Override
                    public void onResponse(String response) {
                        Logger.info("onResponse is: "
                                + response.toString());
                        WaitDialog.instance().hideWaitNote();
                    }

                });
    }
}
