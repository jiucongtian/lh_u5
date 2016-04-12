package com.longhuapuxin.u5;

import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.entity.ResponseDad;
import com.squareup.okhttp.Request;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class PasswordActivity extends BaseActivity {
    private View oldPwdPanel;
    private TextView txtOldPwd, txtNewPwd, txtNewPwd2, txtMsg;
    private int mPwdType = 1;// 1 signin password 2 paypassword
    private String mPwd;
    private ImageView oldPwdPanelLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        ((U5Application) getApplication()).addActivity(this);
        Intent intent = this.getIntent();
        if (intent != null) {
            mPwdType = intent.getIntExtra("PwdType", 1);
        }

        init();

    }

    private void init() {

        if (mPwdType == 1) {
            mPwd = Settings.instance().User.getPassword();
            initHeader(R.string.setpassword);
        } else {
            mPwd = Settings.instance().User.getPayPassword();
            initHeader(R.string.setpaypassword);

        }
        oldPwdPanelLine = (ImageView) findViewById(R.id.oldPwdPanelLine);
        oldPwdPanel = findViewById(R.id.oldPwdPanel);
        if (mPwd != null && mPwd.length() > 0) {
            oldPwdPanelLine.setVisibility(View.VISIBLE);
            oldPwdPanel.setVisibility(View.VISIBLE);
        } else {
            mPwd = "";
            oldPwdPanelLine.setVisibility(View.GONE);
            oldPwdPanel.setVisibility(View.GONE);

        }

        txtOldPwd = (TextView) findViewById(R.id.txtOldPwd);
        txtNewPwd = (TextView) findViewById(R.id.txtNewPwd);
        txtNewPwd2 = (TextView) findViewById(R.id.txtNewPwd2);
        txtMsg = (TextView) findViewById(R.id.txtMsg);
        findViewById(R.id.btnOk).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SavePwd();
            }

        });

        findViewById(R.id.btnGetPwd).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(PasswordActivity.this,
                        GetPasswordActivity.class);
                intent.putExtra("PwdType", mPwdType);
                startActivity(intent);
            }

        });
    }

    private void SavePwd() {
        txtMsg.setText("");

        if (mPwd != null && mPwd.length() > 0) {
            if (!TextUtils.equals(mPwd, txtOldPwd.getText().toString())) {
                txtMsg.setText("原密码不正确，请重新输入");
                return;
            }

        }
        String newPwd = txtNewPwd.getText().toString();
        if (newPwd.length() < 6) {
            txtMsg.setText("密码为6~16位");
            return;
        }
        if (newPwd == null || newPwd.length() <= 0) {
            txtMsg.setText("请输入新密码");
            return;

        } else {
            if (!newPwd.equals(txtNewPwd2.getText().toString())) {
                txtMsg.setText("新密码和确认密码不一致");
                return;
            }
        }
        SavePassword(newPwd);
    }

    private void SavePassword(final String newPassword) {
        WaitDialog.instance().showWaitNote(this);
        String api = "";
        Param[] params = new Param[4];
        if (mPwdType == 1) {
            api = "/auth/setpassword";

            params[0] = new Param("UserId", Settings.instance().getUserId());
            params[1] = new Param("Token", Settings.instance().getToken());
            params[2] = new Param("OldPassword", mPwd);
            params[3] = new Param("Password", newPassword);
        } else {
            api = "/pay/Setpaypassword";

            params[0] = new Param("UserId", Settings.instance().getUserId());
            params[1] = new Param("Token", Settings.instance().getToken());
            params[2] = new Param("OldPayPassword", mPwd);
            params[3] = new Param("PayPassword", newPassword);
        }

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl() + api,
                params, new OkHttpClientManager.ResultCallback<ResponseDad>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        txtMsg.setText(e.getMessage());
                        WaitDialog.instance().hideWaitNote();
                    }

                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onResponse(ResponseDad response) {
                        WaitDialog.instance().hideWaitNote();
                        if (response.isSuccess()) {
                            if (mPwdType == 1) {
                                Settings.instance().User
                                        .setPassword(newPassword);

                            } else {
                                Settings.instance().User
                                        .setPayPassword(newPassword);

                            }
                            txtMsg.setTextColor(R.color.green);
                            txtMsg.setText("密码已经保存");
                            txtOldPwd.setText("");
                            txtNewPwd.setText("");
                            txtNewPwd2.setText("");
                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    PasswordActivity.this.finish();
                                }
                            }, 500);
                        } else {
//                            txtMsg.setText(response.getErrorMessage());
                            txtMsg.setText("请重新输入密码");
                        }
                    }

                });

    }

//	@Override
//	public void onBackPressed() {
//		((U5Application) getApplication()).exit();
//	}
}
