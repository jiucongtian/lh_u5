package com.longhuapuxin.u5;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by asus on 2016/1/5.
 */
public class AboutU5Activity extends BaseActivity implements View.OnClickListener {
    RelativeLayout aboutUs, help, agreement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_u5);
        initHeader("关于U5");
        initViews();
    }

    private void initViews() {
        aboutUs = (RelativeLayout) findViewById(R.id.about_us);
        help = (RelativeLayout) findViewById(R.id.help);
        agreement = (RelativeLayout) findViewById(R.id.agreement);
        aboutUs.setOnClickListener(this);
        help.setOnClickListener(this);
        agreement.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == aboutUs) {
            Intent intent = new Intent(this, AboutUsActivity.class);
            startActivity(intent);
        } else if (v == help) {
            Intent intent = new Intent(this, SuggestionActivity.class);
            startActivity(intent);
        } else if (v == agreement) {
            Intent intent = new Intent(this, AgreementActivity.class);
            startActivity(intent);
        }

    }
}
