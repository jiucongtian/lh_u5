package com.longhuapuxin.u5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.longhuapuxin.common.HttpRequest;
import com.longhuapuxin.common.HttpRequest.HttpRequestListener;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.entity.ResponseCheckPhoneNumber;
import com.longhuapuxin.entity.ResponseSMS;
import com.longhuapuxin.entity.ResponseBindPhone;
import com.longhuapuxin.entity.ResponseImportAccount;
import com.longhuapuxin.u5.wxapi.WXEntryActivity;
import com.squareup.okhttp.Request;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BindPhoneNumberActivity extends BaseActivity implements
        OnClickListener {
    private String myLastPhoneNum = "0";
    private EditText phoneNumber;
    private TimerTask task;
    private Timer timer = new Timer();
    private LinearLayout getVerification, canGetVerification,
            countVerification;
    private EditText verificationCode;
    private TextView countVerificationText;
    private TextView error;
    private Button ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind);
        initHeader("手机号码");
        initViews();
        initPhoneNumber();
    }

    private void initViews() {
        ok = (Button) findViewById(R.id.ok);
        ok.setOnClickListener(this);
        error = (TextView) findViewById(R.id.error_tx);
        countVerificationText = (TextView) findViewById(R.id.count_verification_code_text);
        getVerification = (LinearLayout) findViewById(R.id.get_verification_code);
        verificationCode = (EditText) findViewById(R.id.verification_code);
        canGetVerification = (LinearLayout) findViewById(R.id.can_get_verification_code);
        canGetVerification.setOnClickListener(this);
        countVerification = (LinearLayout) findViewById(R.id.count_verification_code);
    }

    private void initPhoneNumber() {
        phoneNumber = (EditText) findViewById(R.id.phone_number);
        phoneNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() == 11) {
                    if (!myLastPhoneNum
                            .equals(phoneNumber.getText().toString())) {
                        canGetVerification.setVisibility(View.VISIBLE);
                        stopCount();
                        countVerification.setVisibility(View.GONE);
                    }
                    if (countSecond != 60) {
                        canGetVerification.setVisibility(View.GONE);
                    } else {
                        canGetVerification.setVisibility(View.VISIBLE);
                    }
                    // 提示获取验证码
                    getVerification.setVisibility(View.GONE);
                } else {

                    if (countSecond != 60) {
                        getVerification.setVisibility(View.GONE);
                    } else {
                        getVerification.setVisibility(View.VISIBLE);
                    }
                    canGetVerification.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == ok) {
            httpRequestBindPhoneNumber();

        } else if (v == canGetVerification) {
            myLastPhoneNum = phoneNumber.getText().toString();
            hideSoftInputView();
            httpResponseCheckPhoneNumber();
        }
    }

    private void httpRequestVertification() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("PhoneNumber", phoneNumber.getText()
                .toString()));
        params.add(new BasicNameValuePair("SecurityType", "ZC"));
        new HttpRequest(new HttpRequestListener() {

            @Override
            public void onHttpRequestOK(int tag, byte[] data) {
                try {
                    String json = new String(data, "utf-8");
                    ResponseSMS responceSMS = ((U5Application) getApplication())
                            .getGson().fromJson(json, ResponseSMS.class);
                    if (responceSMS.isSuccess()) {
                        // getVerification.setText(responceSMS
                        // .getSecurityCode());
                        Logger.info("-------" + responceSMS.getSecurityCode());
                        Settings.instance().setSecurityCode(
                                responceSMS.getSecurityCode());
                    } else {
                        error.setText(responceSMS.getErrorMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    error.setText(e.getMessage().toString());
                }
            }

            @Override
            public void onHttpRequestFailed(int tag) {

            }
        }).post(0, Settings.instance().getApiUrl() + "/auth/sendsms", params);
    }

    private void httpRequestBindPhoneNumber() {
        WaitDialog.instance().showWaitNote(this);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("PhoneNumber", phoneNumber.getText()
                .toString()));
        params.add(new BasicNameValuePair("SecurityCode", verificationCode
                .getText().toString()));
        params.add(new BasicNameValuePair("UserId", Settings.instance()
                .getUserId()));
        params.add(new BasicNameValuePair("Token", Settings.instance()
                .getToken()));
        new HttpRequest(new HttpRequestListener() {

            @Override
            public void onHttpRequestOK(int tag, byte[] data) {
                try {
                    String json = new String(data, "utf-8");
                    ResponseBindPhone responseBindPhone = ((U5Application) getApplication())
                            .getGson().fromJson(json, ResponseBindPhone.class);
//					Log.d("", "------BindPhoneNumber" + json);
                    Logger.info("------BindPhoneNumber" + json);
                    if (responseBindPhone.isSuccess()) {
//						Log.d("", "------BindPhoneNumberOk");
                        Logger.info("------BindPhoneNumberOk");
                        Settings.instance().setUserId(
                                responseBindPhone.getUserId());
                        Settings.instance().setToken(
                                responseBindPhone.getToken());
                        Settings.instance().save(BindPhoneNumberActivity.this);
                        Intent intent = new Intent(
                                BindPhoneNumberActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        WaitDialog.instance().hideWaitNote();
                    } else {
                        WaitDialog.instance().hideWaitNote();
                        error.setText(responseBindPhone.getErrorMessage());
//						Log.d("",
//								"------BindPhoneNumber"
//										+ responseBindPhone.getErrorCode());
                        Logger.info("------BindPhoneNumber"
                                + responseBindPhone.getErrorCode());
//						Log.d("",
//								"------BindPhoneNumber"
//										+ responseBindPhone.getErrorMessage());
                        Logger.info("------BindPhoneNumber"
                                + responseBindPhone.getErrorMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    WaitDialog.instance().hideWaitNote();
                    error.setText(e.getMessage());
                }
            }

            @Override
            public void onHttpRequestFailed(int tag) {

            }
        }).post(0, Settings.instance().getApiUrl() + "/auth/bindphone", params);
    }

    private void httpResponseCheckPhoneNumber() {
        WaitDialog.instance().showWaitNote(this);
        Param[] params = new Param[1];
        params[0] = new Param("PhoneNumber", phoneNumber.getText().toString());

        try {
            OkHttpClientManager.getHttpsDelegate().setCertificates(
                    getAssets().open("longhuapuxin.cer"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkHttpClientManager
                .postAsyn(
                        Settings.instance().getApiUrl()
                                + "/auth/checkphonenumber",
                        params,
                        new OkHttpClientManager.ResultCallback<ResponseCheckPhoneNumber>() {

                            @Override
                            public void onError(Request request, Exception e) {
                                Logger.info("------------onError");
//								Log.d("", "------------onError");
                                error.setText(e.getMessage().toString());
                                WaitDialog.instance().hideWaitNote();
                            }

                            @Override
                            public void onResponse(
                                    ResponseCheckPhoneNumber response) {
                                if (response.isPhoneHasRegistered()) {
                                    error.setVisibility(View.VISIBLE);
                                    error.setText("此手机号码已经绑定过");
                                    WaitDialog.instance().hideWaitNote();
                                } else {
                                    error.setVisibility(View.INVISIBLE);
                                    httpRequestVertification();
                                    canGetVerification.setVisibility(View.GONE);
                                    countVerification
                                            .setVisibility(View.VISIBLE);
                                    startCount();
                                    WaitDialog.instance().hideWaitNote();
                                }
                            }

                        });

    }

    /**
     * 隐藏软键盘 hideSoftInputView
     *
     * @param
     * @return void
     * @throws
     * @Title: hideSoftInputView
     * @Description: TODO
     */
    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this
                .getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onBackPressed() {
        ((U5Application) getApplication()).exit();
    }

    private int countSecond = 60;

    private void startCount() {
        if (task == null) {
            task = new TimerTask() {
                @Override
                public void run() {
                    countSecond--;
                    if (countSecond > 0) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                countVerificationText.setText("(" + countSecond
                                        + ")");
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                countVerification.setVisibility(View.GONE);
                                canGetVerification.setVisibility(View.VISIBLE);
                                stopCount();
                            }
                        });
                    }
                }
            };
            timer.schedule(task, 0, 1000);
        }
    }

    private void stopCount() {
        if (task != null) {
            task.cancel();
            task = null;
            countSecond = 60;
        }
    }

}
