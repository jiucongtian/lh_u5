package com.longhuapuxin.u5;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.db.bean.VerifyBankCard;
import com.longhuapuxin.entity.ResponseDad;
import com.squareup.okhttp.Request;

/**
 * Created by ZH on 2016/1/13.
 * Email zh@longhuapuxin.com
 */
public class IdentifyBankCardDialogActivity extends BaseActivity {

    private static final int MAX_VERIFY_TIMES = 5;
    private VerifyBankCard mVerification;
    @ViewInject(R.id.etAmount)
    private EditText mAmountEt;
    @ViewInject(R.id.tvFailedTimes)
    private TextView mFailedTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify);
        ViewUtils.inject(this);
    }

    public void verifyAmount() {
        mVerification = Settings.instance().getmVerify();
        if(null != mVerification ) {
            if(mVerification.mAmount == Double.parseDouble(mAmountEt.getText().toString())) {
                submitCheckBank();
            } else {
                mVerification.mVerifiedTimes++;
                if(mVerification.mVerifiedTimes < MAX_VERIFY_TIMES) {
                    String promptMsg = getResources().getString(R.string.failedTimes);
                    promptMsg = String.format(promptMsg, mVerification.mVerifiedTimes, MAX_VERIFY_TIMES - mVerification.mVerifiedTimes);
                    mFailedTv.setText(promptMsg);
                } else {
                    Settings.instance().setmVerify(this.getBaseContext(),null );
                    finish();
                }
            }
        }
    }

    private void submitCheckBank() {
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[4];
        params[0] = new OkHttpClientManager.Param("UserId", Settings.instance().getUserId());
        params[1] = new OkHttpClientManager.Param("Token", Settings.instance().getToken());
        params[2] = new OkHttpClientManager.Param("Id", mVerification.mId);
        params[3] = new OkHttpClientManager.Param("Amount", mVerification.mAmount.toString());

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/basic/checkbank", params,
                new OkHttpClientManager.ResultCallback<String>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("newappoint.onError"
                                + e.toString());
                    }

                    @Override
                    public void onResponse(String response) {
                        Logger.info("onResponse is: "
                                + response.toString());

                        U5Application u5app = (U5Application) getApplication();
                        ResponseDad rep = u5app.getGson().fromJson(response, ResponseDad.class);
                        if (rep.isSuccess()) {
                            Settings.instance().setmVerify(IdentifyBankCardDialogActivity.this.getBaseContext(), null);
                        }
                        finish();
                    }

                });
    }


    @OnClick({R.id.tvConfirm, R.id.tvBack})
    public void onClick(View view) {
        Integer id = view.getId();
        switch (id) {
            case R.id.tvConfirm:
                verifyAmount();
                break;
            case R.id.tvBack:
                finish();
                break;
        }
    }
}
