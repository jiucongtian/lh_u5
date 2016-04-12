package com.longhuapuxin.u5;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.dao.ChatMessageDao;
import com.longhuapuxin.db.bean.ChatMessage;
import com.longhuapuxin.entity.ResponseDad;
import com.squareup.okhttp.Request;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class OrderCompleteActivity extends BaseActivity {
    private int RESULT_CANCELED = 0;
    private int RESULT_CONFIRM = 1;
    private String orderId;
    private String note;
    private String nickName;
    private TextView txtComments;
    private RadioButton rbGood, rbNormal, rbBad;
    private String estimation = "1";// 1 好评 2中评 3差评

    private OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            switch (arg0.getId()) {
                case R.id.btnConfirm:
                    ConfirmOrder();
                    modifyIMContentForSender(orderId, "3");
                    break;
                case R.id.btnCancel:
                    setResult(RESULT_CANCELED, getIntent());
                    finish();
                    break;
                case R.id.rbGood:
                    estimation = "1";
                    rbNormal.setChecked(false);
                    rbBad.setChecked(false);
                    break;
                case R.id.rbNormal:
                    estimation = "2";
                    rbGood.setChecked(false);
                    rbBad.setChecked(false);
                    break;
                case R.id.rbBad:
                    estimation = "3";
                    rbGood.setChecked(false);
                    rbNormal.setChecked(false);
                    break;
            }
        }

    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_complete);
        initHeader(R.string.CompletedOrder);
        Bundle extras = getIntent().getExtras();
        orderId = extras.getString("OrderId");
        note = extras.getString("OrderNote");
        nickName = extras.getString("NickName");
        TextView txtNote = (TextView) findViewById(R.id.txtNote);
        TextView txtNickName = (TextView) findViewById(R.id.txtNickName);
        txtNote.setText(note);
        txtNickName.setText(nickName);

        Button btnConfirm = (Button) findViewById(R.id.btnConfirm);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        txtComments = (TextView) findViewById(R.id.txtComments);
        rbGood = (RadioButton) findViewById(R.id.rbGood);
        rbNormal = (RadioButton) findViewById(R.id.rbNormal);
        rbBad = (RadioButton) findViewById(R.id.rbBad);

        btnConfirm.setOnClickListener(onClickListener);
        btnCancel.setOnClickListener(onClickListener);
        rbGood.setOnClickListener(onClickListener);
        rbNormal.setOnClickListener(onClickListener);
        rbBad.setOnClickListener(onClickListener);
    }

    private void ConfirmOrder() {
        Settings setting = Settings.instance();

        Param[] params = new Param[5];
        params[0] = new Param("Token", setting.getToken());
        params[1] = new Param("UserId", setting.getUserId());
        params[2] = new Param("OrderId", orderId);
        params[3] = new Param("Estimate", estimation);
        params[4] = new Param("Note", txtComments.getText().toString());

        OkHttpClientManager.postAsyn(setting.getApiUrl()
                        + "/order/completeorder", params,
                new OkHttpClientManager.ResultCallback<String>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        Toast.makeText(OrderCompleteActivity.this,
                                e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(String u) {
                        try {

                            Gson gson = new GsonBuilder().setDateFormat(
                                    "yyyy-MM-dd HH:mm:ss").create();
                            ResponseDad res = gson.fromJson(u,
                                    ResponseDad.class);
                            if (res.isSuccess()) {
                                setResult(RESULT_CONFIRM, getIntent());
                                finish();

                            } else {
                                Toast.makeText(OrderCompleteActivity.this,
                                        res.getErrorMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception ex) {
                            Toast.makeText(OrderCompleteActivity.this,
                                    ex.getMessage(), Toast.LENGTH_LONG).show();
                            Logger.info(ex.getMessage());
//							Log.d("AA", ex.getMessage());
                        }
                    }
                });

    }

    private void modifyIMContentForSender(String orderId, String SendStatus) {
        ChatMessageDao msgHelper = new ChatMessageDao(this);
        List<ChatMessage> messageByOrderId = msgHelper
                .getMessageByOrderId(orderId);
        ChatMessage chatMessage = messageByOrderId.get(0);
        chatMessage.setSendStatus(SendStatus);
        msgHelper.updateMessageByOrderId(chatMessage);
    }
}
