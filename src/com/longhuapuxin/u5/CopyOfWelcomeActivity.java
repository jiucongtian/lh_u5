package com.longhuapuxin.u5;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.longhuapuxin.common.HttpRequest;
import com.longhuapuxin.common.HttpRequest.HttpRequestListener;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.LoginQQAPI;
import com.longhuapuxin.common.LoginWXAPI;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.entity.ResponseGetAccount;
import com.longhuapuxin.entity.ResponseLoginByPhone;
import com.longhuapuxin.entity.ResponseLoginByUserName;
import com.longhuapuxin.entity.ResponseSMS;
import com.longhuapuxin.u5.upgrade.UpgradeManager;
import com.squareup.okhttp.Request;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.Tencent;

public class CopyOfWelcomeActivity extends Activity implements OnClickListener {
    private LinearLayout loginQQ, loginWX;
    private RelativeLayout login;
    private LinearLayout loginBtn;
    private EditText loginPhoneNumber, loginPhoneVerificationCode;
    private EditText loginAccountNumber, loginAccountPassword;
    private LinearLayout getVerification, canGetVerification,
            countVerification;
    private TextView countVerificationText;
    private TextView loginErrorTips;
    private LoginQQAPI loginQQAPI;
    private UpgradeManager upgradeManager;
    private String myLastPhoneNum = "0";
    private Timer timer = new Timer();
    private TimerTask task;
    private boolean NeedReturn = false;
    private TextView loginAccountAttention;
    private LinearLayout loginPhoneTypeView, loginAccountTypeView;
    private TextView loginPhoneType, loginAccountType;
    private RelativeLayout loginPoint;
    private ImageView loginPointUnsel, loginPointSel;
    private TextView txtAgreement, mTvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_copy);
        U5Application u5app = (U5Application) getApplication();
        if (!u5app.ismCheckedUpgrade()) {
            u5app.setmCheckedUpgrade(true);
            upgradeManager = new UpgradeManager(this);
            upgradeManager.CheckAndRefreshApp();
        }

        init();
        initPhoneNumber();
        ((U5Application) getApplication()).addActivity(this);
        Intent intent = this.getIntent();
        NeedReturn = intent.getBooleanExtra("NeedReturn", false);
        mTvVersion.setText("V " + getAppVersionName());
//		getAppVersionName
    }

    private void initPhoneNumber() {
        loginPhoneNumber = (EditText) findViewById(R.id.login_phone_number);
        loginPhoneNumber.addTextChangedListener(new TextWatcher() {

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
                    if (!myLastPhoneNum.equals(loginPhoneNumber.getText()
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
        loginBtn = (LinearLayout) findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(this);
        txtAgreement = (TextView) findViewById(R.id.txtAgreement);
        txtAgreement.setOnClickListener(this);
        login = (RelativeLayout) findViewById(R.id.login);
        login.setOnClickListener(this);
        loginPointUnsel = (ImageView) findViewById(R.id.login_point_unsel);
        loginPointSel = (ImageView) findViewById(R.id.login_point_sel);
        loginPoint = (RelativeLayout) findViewById(R.id.login_point);
        loginPoint.setOnClickListener(this);
        loginPhoneType = (TextView) findViewById(R.id.login_phone_type);
        loginPhoneType.setOnClickListener(this);
        loginAccountType = (TextView) findViewById(R.id.login_account_type);
        loginAccountType.setOnClickListener(this);
        loginPhoneTypeView = (LinearLayout) findViewById(R.id.login_phone_type_view);
        loginAccountTypeView = (LinearLayout) findViewById(R.id.login_account_type_view);
        loginAccountAttention = (TextView) findViewById(R.id.login_account_attention);
        countVerificationText = (TextView) findViewById(R.id.count_verification_code_text);
        loginAccountNumber = (EditText) findViewById(R.id.login_account_number);
        loginAccountPassword = (EditText) findViewById(R.id.login_account_password);
        loginErrorTips = (TextView) findViewById(R.id.login_error_tips);
        loginWX = (LinearLayout) findViewById(R.id.login_wx);
        loginWX.setOnClickListener(this);
        loginQQ = (LinearLayout) findViewById(R.id.login_qq);
        loginQQ.setOnClickListener(this);
        getVerification = (LinearLayout) findViewById(R.id.get_verification_code);
        loginPhoneVerificationCode = (EditText) findViewById(R.id.login_phone_verification_code);
        canGetVerification = (LinearLayout) findViewById(R.id.can_get_verification_code);
        canGetVerification.setOnClickListener(this);
        countVerification = (LinearLayout) findViewById(R.id.count_verification_code);
        mTvVersion = (TextView) findViewById(R.id.tv_version);
        // TextView txtAgreement = (TextView) findViewById(R.id.txtAgreement);
        // String text = "用户协议";
        //
        // // 将text进行拆分
        // SpannableString ss1 = new SpannableString(text);
        //
        // ss1.setSpan(new ClickableSpan() {
        // @Override
        // public void onClick(View widget) {
        // Intent intent = new Intent(CopyOfWelcomeActivity.this,
        // AgreementActivity.class);
        // startActivity(intent);
        // }
        // }, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //
        // txtAgreement.setText("我已经阅读并同意");
        // txtAgreement.append(ss1);
        //
        // txtAgreement.setMovementMethod(LinkMovementMethod.getInstance());

        // **********************test mode*************************************
        if (Logger.DEBUG) {
            TextView view = (TextView) findViewById(R.id.login_error_tips);
            view.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {

                    Intent intent = new Intent(CopyOfWelcomeActivity.this,
                            SetServiceUrlActivity.class);
                    startActivity(intent);

                    return false;
                }
            });
        }
        // **********************test mode*************************************

    }

    private boolean isLoginPhoneType = true;
    private boolean isAcceptAgreement = true;

    @Override
    public void onClick(View v) {
        if (v == loginQQ) {
            // new LoginOtherQQ(getApplicationContext(), WelcomeActivity.this)
            // .login();
            // setTheme(R.style.translucent);
            WaitDialog.instance().showWaitNote(this);
            loginQQAPI = new LoginQQAPI(getApplicationContext(),
                    loginErrorTips, CopyOfWelcomeActivity.this);
            WaitDialog.instance().hideWaitNote();
            loginQQAPI.login();
        } else if (v == login || v == loginBtn) {
            WaitDialog.instance().showWaitNote(this);
            loginErrorTips.setText("");
            if (TextUtils.isEmpty(loginPhoneNumber.getText().toString())
                    && isLoginPhoneType) {
                WaitDialog.instance().hideWaitNote();
                loginErrorTips.setText("请输入手机号码");
                return;
            }
            if (loginPhoneNumber.getText().toString().length() != 11
                    && isLoginPhoneType) {
                WaitDialog.instance().hideWaitNote();
                loginErrorTips.setText("请输入正确手机号码");
                return;
            }
            if (TextUtils.isEmpty(loginPhoneVerificationCode.getText()
                    .toString()) && isLoginPhoneType) {
                WaitDialog.instance().hideWaitNote();
                loginErrorTips.setText("请输入验证码");
                return;
            }
            if (vertificationCode != null && !vertificationCode.equals(loginPhoneVerificationCode.getText().toString()) && isLoginPhoneType) {
                WaitDialog.instance().hideWaitNote();
                loginErrorTips.setText("验证码不正确，请重新输入");
                return;
            }
            if (TextUtils.isEmpty(loginAccountNumber.getText().toString())
                    && !isLoginPhoneType) {
                WaitDialog.instance().hideWaitNote();
                loginErrorTips.setText("请输入账号");
                return;
            }
            if (TextUtils.isEmpty(loginAccountPassword.getText().toString())
                    && !isLoginPhoneType) {
                WaitDialog.instance().hideWaitNote();
                loginErrorTips.setText("请输入密码");
                return;
            }
            if ((loginAccountPassword.getText().toString().length() < 6)
                    && !isLoginPhoneType) {
                WaitDialog.instance().hideWaitNote();
                loginErrorTips.setText("密码为6~16位");
                return;
            }
            if (!isAcceptAgreement) {
                WaitDialog.instance().hideWaitNote();
                loginErrorTips.setText("请阅读并同意用户协议");
                return;
            }
            if (isLoginPhoneType && isAcceptAgreement) {
                httpRequestLogin();
            } else if (!isLoginPhoneType && isAcceptAgreement) {
                httpRequestLoginByUserName();
            }
        } else if (v == canGetVerification) {
            loginErrorTips.setText("");
            myLastPhoneNum = loginPhoneNumber.getText().toString();
            hideSoftInputView();
            httpRequestVertification();
            canGetVerification.setVisibility(View.GONE);
            countVerification.setVisibility(View.VISIBLE);
            startCount();
        } else if (v == loginWX) {
            new LoginWXAPI(getApplicationContext(), loginErrorTips)
                    .sendRequestToWx();
        } else if (v == loginPhoneType) {
            if (loginPhoneTypeView.getVisibility() == View.GONE) {
                loginErrorTips.setText("");
                loginAccountType.setTextColor(getResources().getColor(
                        R.color.deep_gray));
                loginPhoneType.setTextColor(getResources().getColor(
                        R.color.orange));
                isLoginPhoneType = true;
                loginPhoneTypeView.setVisibility(View.VISIBLE);
                loginAccountTypeView.setVisibility(View.GONE);
                loginAccountAttention.setVisibility(View.GONE);
            }
        } else if (v == loginAccountType) {
            if (loginAccountTypeView.getVisibility() == View.GONE) {
                loginErrorTips.setText("");
                loginAccountType.setTextColor(getResources().getColor(
                        R.color.orange));
                loginPhoneType.setTextColor(getResources().getColor(
                        R.color.deep_gray));
                isLoginPhoneType = false;
                loginAccountTypeView.setVisibility(View.VISIBLE);
                loginPhoneTypeView.setVisibility(View.GONE);
                loginAccountAttention.setVisibility(View.VISIBLE);
            }

        } else if (v == loginPoint) {
            if (isAcceptAgreement) {
                loginPointUnsel.setVisibility(View.VISIBLE);
                loginPointSel.setVisibility(View.GONE);
                isAcceptAgreement = false;
            } else {
                loginPointUnsel.setVisibility(View.GONE);
                loginPointSel.setVisibility(View.VISIBLE);
                isAcceptAgreement = true;
            }
        } else if (v == txtAgreement) {
            Intent intent = new Intent(CopyOfWelcomeActivity.this,
                    AgreementActivity.class);
            startActivity(intent);
        }
    }

    private String vertificationCode = null;

    private void httpRequestVertification() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("PhoneNumber", loginPhoneNumber
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
                        loginErrorTips.setText(responceSMS.getErrorMessage()
                                + "");
//                        Log.d("",
//                                "------securitycodeFail"
//                                        + responceSMS.getErrorMessage());
                        Logger.info("------securitycodeFail"
                                + responceSMS.getErrorMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    loginErrorTips.setText("" + e.getMessage());
                }
            }

            @Override
            public void onHttpRequestFailed(int tag) {
                loginErrorTips.setText("请检查你的网络");
//                Log.d("", "------" + "faile");
                Logger.info("------fail");
            }
        }).post(0, Settings.instance().getApiUrl() + "/auth/sendsms", params);
    }

    private void httpRequestGetAccount(final String userId, final String token) {
        Param[] params = new Param[3];
        params[0] = new Param("UserId", userId);
        params[1] = new Param("Token", token);
        params[2] = new Param("TargetUserId", userId);

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/auth/getaccount", params,
                new OkHttpClientManager.ResultCallback<String>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        WaitDialog.instance().hideWaitNote();
                        loginErrorTips.setText(e.getMessage());
                    }

                    @Override
                    public void onResponse(String u) {
                        Logger.info("getaccount result is: " + u);
                        try {

                            ResponseGetAccount responseGetAccount = ((U5Application) getApplication())
                                    .getGson().fromJson(u,
                                            ResponseGetAccount.class);
                            // Log.d("", "--------" + responseLoginByPhone);
                            if (responseGetAccount.isSuccess()) {
                                Settings.instance().setMyName(
                                        responseGetAccount.getUser()
                                                .getNickName());
                                Settings.instance().User = responseGetAccount
                                        .getUser();
                                Settings.instance().setManualyWithDraw(responseGetAccount.isManualyWithDraw());
                                Settings.instance().setUserId(userId);
                                Settings.instance().setToken(token);
                                Settings.instance().save(
                                        CopyOfWelcomeActivity.this);
//                                Log.d("", "------GetAccount success");
                                Logger.info("------GetAccount success");
                                if (!NeedReturn) {
                                    Intent intent = new Intent(
                                            CopyOfWelcomeActivity.this,
                                            MainActivity.class);
                                    Settings.instance().User = responseGetAccount
                                            .getUser();
                                    startActivity(intent);
                                    WaitDialog.instance().hideWaitNote();
                                } else {
                                    finish();

                                }

                            } else {
                                Settings.instance().User = null;
                                Settings.instance().setUserId("");
                                Settings.instance().setToken("");
                                Settings.instance().save(
                                        CopyOfWelcomeActivity.this);
                                WaitDialog.instance().hideWaitNote();
//                                Log.d("", "------GetAccount error"
//                                        + responseGetAccount.getErrorMessage());
//                                Log.d("", "------GetAccount error"
//                                        + responseGetAccount.getErrorCode());
                                Logger.info("------GetAccount error"
                                        + responseGetAccount.getErrorMessage());
                                Logger.info("------GetAccount error"
                                        + responseGetAccount.getErrorCode());

                                loginErrorTips.setText(""
                                        + responseGetAccount.getErrorMessage());
                            }
                        } catch (Exception ex) {
                            WaitDialog.instance().hideWaitNote();
                            loginErrorTips.setText(ex.getMessage());
                            // Toast.makeText(CopyOfWelcomeActivity.this,
                            // ex.getMessage(), Toast.LENGTH_LONG).show();
                            // Log.d("AA", ex.getMessage());
                        }
                    }
                });
        // loginErrorTips.setText("验证码不正确，请重新输入。");

    }

    private void httpRequestLoginByUserName() {
        WaitDialog.instance().showWaitNote(this);
        Param[] params = new Param[5];
        params[0] = new Param("UserName", loginAccountNumber.getText()
                .toString());
        params[1] = new Param("Password", loginAccountPassword.getText()
                .toString());
        Settings setting = Settings.instance();
        if (setting.City == null || setting.City.length() <= 0) {
            params[2] = new Param("City", "");
            params[3] = new Param("Longitude", "");
            params[4] = new Param("Latitude", "");
        } else {
            params[2] = new Param("BDCityCode", setting.CityCode);
            params[3] = new Param("Longitude",
                    String.valueOf(setting.Lontitude));
            params[4] = new Param("Latitude", String.valueOf(setting.Latitude));
        }
        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/auth/Loginbyusername", params,
                new OkHttpClientManager.ResultCallback<String>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        WaitDialog.instance().hideWaitNote();
                        loginErrorTips.setText(e.getMessage());
                    }

                    @Override
                    public void onResponse(String u) {
                        Logger.info("Loginbyusername result is: " + u);
                        try {
                            ResponseLoginByUserName responseLoginByUserName = ((U5Application) getApplication())
                                    .getGson().fromJson(u,
                                            ResponseLoginByUserName.class);

                            if (responseLoginByUserName.isSuccess()) {
                                String userId = responseLoginByUserName
                                        .getUserId();
                                String token = responseLoginByUserName
                                        .getToken();
                                httpRequestGetAccount(userId, token);
                            } else {
                                WaitDialog.instance().hideWaitNote();
                                loginErrorTips.setText("请重新输入密码");
//                                loginErrorTips.setText(responseLoginByUserName
//                                        .getErrorMessage());
                            }

                        } catch (Exception ex) {
                            WaitDialog.instance().hideWaitNote();
                            loginErrorTips.setText(ex.getMessage());
                        }
                    }
                });

    }

    private void httpRequestLogin() {
        WaitDialog.instance().showWaitNote(this);
        Param[] params = new Param[6];
        params[0] = new Param("PhoneNumber", loginPhoneNumber.getText()
                .toString());
        params[1] = new Param("SecurityCode", loginPhoneVerificationCode
                .getText().toString());
        Settings setting = Settings.instance();
        if (setting.City == null || setting.City.length() <= 0) {
            params[2] = new Param("City", "");
            params[3] = new Param("BDCityCode", "");
            params[4] = new Param("Longitude", "");
            params[5] = new Param("Latitude", "");
        } else {
            params[2] = new Param("City", setting.City);
            params[3] = new Param("BDCityCode", setting.CityCode);
            params[4] = new Param("Longitude",
                    String.valueOf(setting.Lontitude));
            params[5] = new Param("Latitude", String.valueOf(setting.Latitude));
        }
        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/auth/loginbyphone", params,
                new OkHttpClientManager.ResultCallback<String>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        // Toast.makeText(CopyOfWelcomeActivity.this,
                        // e.getMessage(), Toast.LENGTH_LONG).show();
                        // e.printStackTrace();
                        WaitDialog.instance().hideWaitNote();
                        loginErrorTips.setText(e.getMessage());
                    }

                    @Override
                    public void onResponse(String u) {
                        Logger.info("loginbyphone result is: " + u);
                        try {
                            ResponseLoginByPhone responseLoginByPhone = ((U5Application) getApplication())
                                    .getGson().fromJson(u,
                                            ResponseLoginByPhone.class);

                            // Log.d("", "--------" + responseLoginByPhone);
                            if (responseLoginByPhone.isSuccess()) {
                                String userId = responseLoginByPhone
                                        .getUserId();
                                String token = responseLoginByPhone.getToken();
                                httpRequestGetAccount(userId, token);
                            } else {
                                WaitDialog.instance().hideWaitNote();
                                loginErrorTips.setText(responseLoginByPhone
                                        .getErrorMessage());
//                                loginErrorTips.setText("请重新输入密码");
                            }

                        } catch (Exception ex) {
                            WaitDialog.instance().hideWaitNote();
                            loginErrorTips.setText(ex.getMessage());
                            // Toast.makeText(CopyOfWelcomeActivity.this,
                            // ex.getMessage(), Toast.LENGTH_LONG).show();
                            // Log.d("AA", ex.getMessage());
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

    // 显示软键盘
    public void showSoftInputView(View view) {
        if (getCurrentFocus() != null)
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                    .showSoftInput(view, 0);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle bundle = null;
        if (data != null) {
            bundle = data.getExtras();
            Set<String> keySet = bundle.keySet();
            for (String key : keySet) {
                Object value = bundle.get(key);
                Logger.info("----------key" + key);
                Logger.info("----------value" + value);
//                Log.d("", "----------key" + key);
//                Log.d("", "----------value" + value);
            }
        }
        if (requestCode == Constants.REQUEST_API) {
            if (resultCode == Constants.RESULT_LOGIN) {
                if (bundle != null && bundle.containsKey("key_error_msg")
                        && bundle.getString("key_error_msg") == null
                        && bundle.getInt("key_error_code") == 0) {
                    loginErrorTips.setVisibility(View.VISIBLE);
                    loginErrorTips.setText("内存紧张");
                } else {
                    Tencent.handleResultData(data, loginQQAPI.loginListener);
                }
            }
            super.onActivityResult(requestCode, resultCode, data);
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
                    // countVerification.setVisibility(View.GONE);
                    // canGetVerification.setVisibility(View.VISIBLE);
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

    @Override
    protected void onPause() {
        super.onPause();
        WaitDialog.instance().hideWaitNote();
    }

    private String getAppVersionName() {
        String versionName = "";
        try {
            // ---get the package info---  
            PackageManager pm = this.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(this.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
        }
        return versionName;
    }

}
