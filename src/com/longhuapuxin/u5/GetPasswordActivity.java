package com.longhuapuxin.u5;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.longhuapuxin.common.HttpRequest;
import com.longhuapuxin.common.HttpRequest.HttpRequestListener;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.entity.ResponseGetPassword;
import com.longhuapuxin.entity.ResponseSMS;

public class GetPasswordActivity extends BaseActivity {
    private LinearLayout getVerification, canGetVerification,
            countVerification;
    private TextView txtCode, txtMsg;
    private EditText oldPhoneNumber, verificationCode;
    private String myLastPhoneNum = "0";
    private Button btnOk;
    private DisplayMetrics dm;
    private TimerTask task;
    private Timer timer = new Timer();
    private int mPwdType = 1;// 1 signin password 2 paypassword
    private final int MSG_Finish = 1;
    private Dialog dialog = null;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_Finish:
                    String pwd = msg.obj.toString();
                    txtMsg.setText("保存完毕");
                    ShowPwd(pwd);
                    break;
            }
        }
    };

    private void ShowPwd(String pwd) {

        dialog = new AlertDialog.Builder(GetPasswordActivity.this).create();
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = (int) (dm.widthPixels * 0.8);
        dialog.getWindow().setAttributes(params);
        LayoutInflater inflaterDl = LayoutInflater.from(this);
        LinearLayout layout = (LinearLayout) inflaterDl.inflate(
                R.layout.dialog_findpwd, null);
        dialog.getWindow().setContentView(layout);
        TextView txt = ((TextView) layout.findViewById(R.id.textView1));
        txt.setText(pwd);
        // WindowManager m = getWindowManager();
        // Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        // WindowManager.LayoutParams p = getWindow().getAttributes(); //
        // 获取对话框当前的参数值
        // p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.8
        // dialog.getWindow().setAttributes(p);

        // 确定按钮
        Button btnOK = (Button) layout.findViewById(R.id.button1);
        btnOK.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                GetPasswordActivity.this.finish();
            }
        });
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getpassword);

        Intent intent = this.getIntent();
        if (intent != null) {
            mPwdType = intent.getIntExtra("PwdType", 1);
        }
        if (mPwdType == 1) {
            initHeader(R.string.findPassword);
        } else {
            initHeader(R.string.findPasPassword);

        }
        init();
        initPhoneNumber();

    }

    private void initPhoneNumber() {
        oldPhoneNumber = (EditText) findViewById(R.id.old_phone_number);
        oldPhoneNumber.addTextChangedListener(new TextWatcher() {

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
                    if (!myLastPhoneNum.equals(oldPhoneNumber.getText()
                            .toString())) {
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

    private void init() {
        dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        canGetVerification = (LinearLayout) findViewById(R.id.can_get_verification_code);
        countVerification = (LinearLayout) findViewById(R.id.count_verification_code);
        getVerification = (LinearLayout) findViewById(R.id.get_verification_code);
        txtCode = (TextView) findViewById(R.id.count_verification_code_text);
        oldPhoneNumber = (EditText) findViewById(R.id.old_phone_number);
        verificationCode = (EditText) findViewById(R.id.verification_code);
        txtMsg = (TextView) findViewById(R.id.txtMsg);
        btnOk = (Button) findViewById(R.id.btnOk);
        canGetVerification.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                myLastPhoneNum = oldPhoneNumber.getText().toString();
                canGetVerification.setVisibility(View.GONE);
                countVerification.setVisibility(View.VISIBLE);
                GetVertifyCode();
            }

        });

        btnOk.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                GetPassword();
            }

        });

    }

    private void GetVertifyCode() {
        // if (countSecond < 60)
        // return;
        // if (TextUtils.isEmpty(oldPhoneNumber.getText().toString())) {
        // WaitDialog.instance().hideWaitNote();
        // txtMsg.setText("请输入手机号码");
        // return;
        // }
        // if (oldPhoneNumber.getText().toString().length() != 11) {
        // WaitDialog.instance().hideWaitNote();
        // txtMsg.setText("请输入正确手机号码");
        // return;
        // }

        hideSoftInputView();
        httpRequestVertification();
        startCount();
    }

    private void GetPassword() {
        // if (countSecond < 60)
        // return;
        // if (TextUtils.isEmpty(oldPhoneNumber.getText().toString())) {
        // WaitDialog.instance().hideWaitNote();
        // txtMsg.setText("请输入手机号码");
        // return;
        // }
        // if (oldPhoneNumber.getText().toString().length() != 11) {
        // WaitDialog.instance().hideWaitNote();
        // txtMsg.setText("请输入正确手机号码");
        // return;
        // }
        if (TextUtils.isEmpty(verificationCode.getText().toString())) {
            WaitDialog.instance().hideWaitNote();
            txtMsg.setText("请输入验证码");
            return;
        }
        if (vertificationCode != null && !vertificationCode.equals(verificationCode.getText().toString())) {
            WaitDialog.instance().hideWaitNote();
            txtMsg.setText("验证码不正确，请重新输入");
            return;
        }
        WaitDialog.instance().showWaitNote(this);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("PhoneNumber", oldPhoneNumber
                .getText().toString()));
        params.add(new BasicNameValuePair("SecurityCode", verificationCode
                .getText().toString()));
        params.add(new BasicNameValuePair("Type", String.valueOf(mPwdType)));
        new HttpRequest(new HttpRequestListener() {

            @Override
            public void onHttpRequestOK(int tag, byte[] data) {
                try {

                    String json = new String(data, "utf-8");
                    Logger.info("sendsms result is: " + json);
                    ResponseGetPassword res = ((U5Application) getApplication())
                            .getGson()
                            .fromJson(json, ResponseGetPassword.class);
                    if (res.isSuccess()) {
                        Message msg = new Message();
                        if (mPwdType == 1) {
                            msg.obj = res.Password;
                        } else {
                            msg.obj = res.PayPassword;
                        }
                        msg.what = MSG_Finish;
                        handler.sendMessage(msg);
                    } else {
                        txtMsg.setText(res.getErrorMessage() + "");
//                        Log.d("",
//                                "------securitycodeFail"
//                                        + res.getErrorMessage());
                        Logger.info("------securitycodeFail"
                                + res.getErrorMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    txtMsg.setText("" + e.getMessage());
                }
                WaitDialog.instance().hideWaitNote();
            }

            @Override
            public void onHttpRequestFailed(int tag) {
                txtMsg.setText("请检查你的网络");
//                Log.d("", "------" + "faile");
                Logger.info("faile");
                WaitDialog.instance().hideWaitNote();
            }
        }).post(0, Settings.instance().getApiUrl() + "/auth/RetrievePassword",
                params);
    }

    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this
                .getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private String vertificationCode = null;

    private void httpRequestVertification() {
        WaitDialog.instance().showWaitNote(this);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("PhoneNumber", oldPhoneNumber
                .getText().toString()));
        params.add(new BasicNameValuePair("SecurityType", "ZC"));
        new HttpRequest(new HttpRequestListener() {

            @Override
            public void onHttpRequestOK(int tag, byte[] data) {
                try {

                    String json = new String(data, "utf-8");
                    Logger.info("sendsms result is: " + json);
                    ResponseSMS responceSMS = ((U5Application) getApplication())
                            .getGson().fromJson(json, ResponseSMS.class);
                    if (responceSMS.isSuccess()) {
                        // getVerification.setText(responceSMS
                        // .getSecurityCode());
                        Settings.instance().setSecurityCode(
                                responceSMS.getSecurityCode());
                        vertificationCode = responceSMS.getSecurityCode();
//                        Log.d("",
//                                "------securitycode"
//                                        + responceSMS.getSecurityCode());
                        Logger.info("------securitycode"
                                + responceSMS.getSecurityCode());
                    } else {
                        txtMsg.setText(responceSMS.getErrorMessage() + "");
//                        Log.d("",
//                                "------securitycodeFail"
//                                        + responceSMS.getErrorMessage());
                        Logger.info("------securitycodeFail"
                                + responceSMS.getErrorMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    txtMsg.setText("" + e.getMessage());
                }
                WaitDialog.instance().hideWaitNote();
            }

            @Override
            public void onHttpRequestFailed(int tag) {
                txtMsg.setText("请检查你的网络");
                Logger.info("faile");
//                Log.d("", "------" + "faile");
                WaitDialog.instance().hideWaitNote();
            }
        }).post(0, Settings.instance().getApiUrl() + "/auth/sendsms", params);
    }

    private int countSecond = 60;

    private void startCount() {
        getVerification.setBackgroundColor(this.getResources().getColor(
                R.color.shallow_gray));
        if (task == null) {
            task = new TimerTask() {
                @Override
                public void run() {
                    // countVerification.setVisibility(View.GONE);
                    // canGetVerification.setVisibility(View.VISIBLE);
                    countSecond--;
                    if (countSecond > 0) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                txtCode.setText("(" + countSecond + ")");
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            public void run() {

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
        getVerification.setBackgroundColor(this.getResources().getColor(
                R.color.orange));
        if (task != null) {
            task.cancel();
            task = null;
            countSecond = 60;
        }
    }

}
