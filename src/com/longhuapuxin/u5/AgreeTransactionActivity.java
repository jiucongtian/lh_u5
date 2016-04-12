package com.longhuapuxin.u5;

import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.entity.ResponseGetOrderList;
import com.squareup.okhttp.Request;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AgreeTransactionActivity extends BaseActivity implements
        OnClickListener {
    private static final int START_ACTIVITY_RESULT = 2;
    private ImageView evaluateRound1, evaluateRound2, evaluateRound3;
    private int estimate = 1;
    private Button confirm, cancel;
    private String intentName, intentNote, intentOrderId;
    private EditText words;
    private TextView name, note;
    private RelativeLayout evaluateGood, evaluateBad;
    private LinearLayout evaluateNormal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agree_transaction);
        initHeader("达成交易");
        initViews();
        getDatas();
    }

    private void getDatas() {
        Intent intent = getIntent();
        intentName = intent.getStringExtra("name");
        intentNote = intent.getStringExtra("note");
        intentOrderId = String.valueOf(intent.getIntExtra("orderId", 0));
        note.setText(intentNote);
        name.setText(intentName);
    }

    private void initViews() {
        evaluateRound1 = (ImageView) findViewById(R.id.evaluate_round_1);
        evaluateRound1.setOnClickListener(this);
        evaluateRound2 = (ImageView) findViewById(R.id.evaluate_round_2);
        evaluateRound2.setOnClickListener(this);
        evaluateRound3 = (ImageView) findViewById(R.id.evaluate_round_3);
        evaluateRound3.setOnClickListener(this);
        confirm = (Button) findViewById(R.id.confirm);
        confirm.setOnClickListener(this);
        cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        words = (EditText) findViewById(R.id.words);
        name = (TextView) findViewById(R.id.name);
        note = (TextView) findViewById(R.id.note);
        evaluateGood = (RelativeLayout) findViewById(R.id.evaluate_good);
        evaluateGood.setOnClickListener(this);
        evaluateBad = (RelativeLayout) findViewById(R.id.evaluate_bad);
        evaluateBad.setOnClickListener(this);
        evaluateNormal = (LinearLayout) findViewById(R.id.evaluate_normal);
        evaluateNormal.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == confirm) {
            httpRequestCompleteOrder();
        } else if (v == evaluateGood) {
            estimate = 1;
            evaluateRound1.setImageResource(R.drawable.login_round_sel);
            evaluateRound2.setImageResource(R.drawable.login_round);
        } else if (v == evaluateNormal) {
            estimate = 2;
            evaluateRound1.setImageResource(R.drawable.login_round);
            evaluateRound2.setImageResource(R.drawable.login_round_sel);
            evaluateRound3.setImageResource(R.drawable.login_round);
        } else if (v == evaluateBad) {
            estimate = 3;
            evaluateRound1.setImageResource(R.drawable.login_round);
            evaluateRound2.setImageResource(R.drawable.login_round);
            evaluateRound3.setImageResource(R.drawable.login_round_sel);
        } else if (v == cancel) {
            finish();
        }

    }

    private void httpRequestCompleteOrder() {
        WaitDialog.instance().showWaitNote(this);
        Param[] params = new Param[5];
        params[0] = new Param("UserId", Settings.instance().getUserId());
        params[1] = new Param("Token", Settings.instance().getToken());
        params[2] = new Param("OrderId", intentOrderId);
        params[3] = new Param("Estimate", String.valueOf(estimate));
        params[4] = new Param("Note", words.getText().toString());

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/order/completeorder", params,
                new OkHttpClientManager.ResultCallback<ResponseGetOrderList>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        WaitDialog.instance().hideWaitNote();
                    }

                    @Override
                    public void onResponse(ResponseGetOrderList response) {
                        if (response.isSuccess()) {
                            WaitDialog.instance().hideWaitNote();
                            Intent intent = new Intent();
                            intent.putExtra("orderId", intentOrderId);
                            setResult(START_ACTIVITY_RESULT, intent);
                            finish();
                        } else {
                            WaitDialog.instance().hideWaitNote();
//							Log.d("", "------httpRequestCompleteOrder error"
//									+ response.getErrorMessage());
                            Logger.info("------httpRequestCompleteOrder error"
                                    + response.getErrorMessage());
//							Log.d("", "------httpRequestCompleteOrder error"
//									+ response.getErrorCode());
                            Logger.info("------httpRequestCompleteOrder error"
                                    + response.getErrorCode());
                        }
                    }

                });

    }
}
