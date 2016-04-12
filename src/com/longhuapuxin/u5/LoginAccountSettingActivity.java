package com.longhuapuxin.u5;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.longhuapuxin.common.InputTool;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.entity.ResponseDad;
import com.squareup.okhttp.Request;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginAccountSettingActivity extends BaseActivity {
    private EditText txtName;
    private TextView txtMsg;
    private Button btnSave;
    private final int MSG_Finish = 1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_Finish:
                    Settings.instance().User.setUserName(txtName
                            .getText().toString());
                    txtMsg.setText("保存完毕");
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            LoginAccountSettingActivity.this.finish();
                        }
                    }, 500);

                    break;
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_account_setting);
        this.initHeader(R.string.sineinname);

        txtName = (EditText) findViewById(R.id.txtName);
        txtMsg = (TextView) findViewById(R.id.txtMsg);
        btnSave = (Button) findViewById(R.id.btnSave);
        if (Settings.instance().User.getUserName() != null) {
            txtName.setText(Settings.instance().User.getUserName());
        }
        btnSave.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SaveName();
            }
        });
    }

    private void SaveName() {
        Settings setting = Settings.instance();
        String name = txtName.getText().toString();
        if (name == null || name.length() <= 0) {
            txtMsg.setText("请输入账号");
            return;
        }
        Pattern p = Pattern.compile("^[a-zA-Z0-9_]{1,20}$");
        Matcher matcher = p.matcher(name);
        if (!matcher.find()) {
            txtMsg.setText("输入格式不正确");
            return;
        }
        if (InputTool.isContainNumber(name) && name.length() == 11) {
            txtMsg.setText("不能输入11位纯数字");
            return;
        }
        Param[] params = new Param[3];
        params[0] = new Param("Token", setting.getToken());
        params[1] = new Param("UserId", setting.getUserId());
        params[2] = new Param("UserName", name);

        OkHttpClientManager.postAsyn(setting.getApiUrl() + "/auth/setusername",
                params, new OkHttpClientManager.ResultCallback<ResponseDad>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        txtMsg.setText(e.getMessage());

                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(ResponseDad response) {
                        try {

                            if (response.isSuccess()) {
                                handler.sendEmptyMessage(MSG_Finish);

                            } else {
                                txtMsg.setText(response.getErrorMessage());
                            }
                        } catch (Exception ex) {
                            txtMsg.setText(ex.getMessage());

                        }
                    }
                });

    }
}
