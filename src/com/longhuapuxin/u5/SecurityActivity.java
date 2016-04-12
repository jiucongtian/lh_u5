package com.longhuapuxin.u5;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

public class SecurityActivity extends BaseActivity implements OnClickListener {
    private RelativeLayout loginAccountSetting, loginPasswordSetting,
            phoneNumberSetting, payPasswordSetting;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);
        initHeader(R.string.securitysetting);
        initViews();
    }

    private void initViews() {
        loginAccountSetting = (RelativeLayout) findViewById(R.id.login_account_setting);
        loginAccountSetting.setOnClickListener(this);
        loginPasswordSetting = (RelativeLayout) findViewById(R.id.login_password_setting);
        loginPasswordSetting.setOnClickListener(this);
        phoneNumberSetting = (RelativeLayout) findViewById(R.id.phone_number_setting);
        phoneNumberSetting.setOnClickListener(this);
        payPasswordSetting = (RelativeLayout) findViewById(R.id.pay_password_setting);
        payPasswordSetting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == loginAccountSetting) {
            Intent intent = new Intent(this, LoginAccountSettingActivity.class);
            startActivity(intent);
        } else if (v == loginPasswordSetting) {
            Intent intent = new Intent(SecurityActivity.this,
                    PasswordActivity.class);
            intent.putExtra("PwdType", 1);
            startActivity(intent);

        } else if (v == payPasswordSetting) {
            Intent intent = new Intent(SecurityActivity.this,
                    PasswordActivity.class);
            intent.putExtra("PwdType", 2);
            startActivity(intent);

        } else if (v == phoneNumberSetting) {
            Intent intent = new Intent(this, PhoneNumberOldSettingActivity.class);
            startActivity(intent);
        }

    }
}
