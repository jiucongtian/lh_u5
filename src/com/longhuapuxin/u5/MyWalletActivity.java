package com.longhuapuxin.u5;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.longhuapuxin.entity.ResponseGetAccount;
import com.longhuapuxin.senabimage.ImagePagerActivity;

/**
 * Created by ZH on 2016/1/13.
 * Email zh@longhuapuxin.com
 */
public class MyWalletActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);
        ViewUtils.inject(this);
        initHeader(R.string.myWallet);
    }

    @OnClick({R.id.tvBindBank, R.id.tvWithDraw, R.id.myBillList, R.id.tvCharge})
    public void onClick(View view) {
        Integer id = view.getId();
        switch (id) {
            case R.id.tvBindBank:
                startActivity(new Intent(this, BindBankActivity.class));
                break;
            case R.id.tvWithDraw:
                ResponseGetAccount.User user = Settings.instance().User;
                if(!TextUtils.isEmpty(user.getBankAccount()) && !TextUtils.isEmpty(user.getBankName())) {
                    startActivity(new Intent(this, WithdrewActivity.class));
                } else {
                    Toast.makeText(this, "请先绑定银行卡", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.myBillList:
                startActivity(new Intent(this, MyBillListActivity.class));
//                startActivity(new Intent(this, ImagePagerActivity.class));
                break;
            case R.id.tvCharge:
                startActivity(new Intent(this, ChargeActivity.class));
                break;
        }
    }
}
