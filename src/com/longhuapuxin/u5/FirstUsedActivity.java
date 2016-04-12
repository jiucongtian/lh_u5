package com.longhuapuxin.u5;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by ZH on 2016/3/16.
 * Email zh@longhuapuxin.com
 */
public class FirstUsedActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_used);
        findViewById(R.id.loginSetIv).setOnClickListener(this);
        findViewById(R.id.loginPassIv).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginSetIv:
                Intent intent = new Intent(this, MyProfileActiviy.class);
                startActivity(intent);
                finish();
                break;
            case R.id.loginPassIv:
                finish();
                break;
        }
    }
}
