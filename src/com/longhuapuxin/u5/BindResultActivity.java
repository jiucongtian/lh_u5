package com.longhuapuxin.u5;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.entity.ResponseCheckBank;
import com.squareup.okhttp.Request;


/**
 * Created by lsy on 2016/2/1.
 */
public class BindResultActivity  extends BaseActivity  {
    private TextView txtInfo,txtStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bindresult);
        TextView tvConfirm=(TextView)findViewById(R.id.tvConfirm);
        TextView tvCancel=(TextView)findViewById(R.id.tvBack);
        txtInfo=(TextView)findViewById(R.id.txtInfo);
        txtStatus=(TextView)findViewById(R.id.txtStatus);
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Settings.instance().setmVerify(getBaseContext(), null);
                finish();
            }
        });
        try{

            httpGetCheckBankResult();
        }
       catch (Exception e){
           e.printStackTrace();
       }
    }

    private void httpGetCheckBankResult() {
        WaitDialog.instance().showWaitNote(this);
        Settings setting = Settings.instance();
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[2];
        params[0] = new OkHttpClientManager.Param("Token", setting.getToken());
        params[1] = new OkHttpClientManager.Param("UserId", setting.getUserId());


        OkHttpClientManager
                .postAsyn(
                        setting.getApiUrl() + "/basic/getcheckbankresult",
                        params,
                        new OkHttpClientManager.ResultCallback<ResponseCheckBank>() {
                            @Override
                            public void onError(Request request, Exception e) {
                                e.printStackTrace();
                                WaitDialog.instance().hideWaitNote();
                            }

                            @Override
                            public void onResponse(
                                    ResponseCheckBank response) {
                                if (response.getErrorMessage()==null || response.getErrorMessage()=="") {
                                    txtStatus.setText(response.getStatusText());
                                    String  info="开 户 行："+response.getBankName()
                                            +"\n户　　名："+response.getRealName()
                                            +"\n银行账号："+response.getBankNo()
                                            +"\n所 在 地："+response.getBankLocation();
                                    txtInfo.setText(info);
                                    WaitDialog.instance().hideWaitNote();
                                } else {
                                    txtStatus.setText("查询出错");
                                    txtInfo.setText(response.getErrorMessage());
                                    WaitDialog.instance().hideWaitNote();
                                }
                            }
                        });
    }

}
