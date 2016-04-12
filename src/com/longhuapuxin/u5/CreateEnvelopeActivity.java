package com.longhuapuxin.u5;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.entity.ResponseNewMoneyEnvelope;
import com.longhuapuxin.uppay.UpPayBaseActivity;
import com.squareup.okhttp.Request;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ZH on 2016/1/26.
 * Email zh@longhuapuxin.com
 */
public class CreateEnvelopeActivity extends BaseActivity implements TextWatcher {

    public static final String ID = "id";
    public static final String IS_GROUP = "isGroup";
    private static final int DECIMAL_DIGITS = 2;
    private static final double MAX_AMOUNT = 10000;
    private String mId;
    private boolean mIsGroup;

    @ViewInject(R.id.etCount)
    private EditText mCountEt;

    @ViewInject(R.id.etTotal)
    private EditText mTotalEt;

    @ViewInject(R.id.etNote)
    private EditText mNoteEt;

    @ViewInject(R.id.tvMoney)
    private TextView mMoneyTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_envelope);
        init();
        initView();
    }

    private void init() {
        ViewUtils.inject(this);
        initHeader(R.string.sendLuckMondy);
        Intent intent = getIntent();
        mIsGroup = intent.getBooleanExtra(IS_GROUP, false);
        mId = intent.getStringExtra(ID);
    }

    private void initView() {
        /**
         *  设置小数位数控制
         */
        InputFilter lengthfilter = new InputFilter() {

            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                // 删除等特殊字符，直接返回
                if ("".equals(source.toString())) {
                    return null;
                }
                String dValue = dest.toString();
                String[] splitArray = dValue.split("\\.");
                if (splitArray.length > 0) {
                    StringBuilder sb = new StringBuilder(dest);
                    sb.replace(dstart, dend, String.valueOf(source));
                    String string = sb.toString();

                    if(string.equals(".")) {
                        return "0.";
                    }

                    try {
                        Double doubleValue = Double.valueOf(sb.toString());
                        if(doubleValue > MAX_AMOUNT) {
                            return "";
                        }
                    } catch (Exception exception) {
                        return "";
                    }
                }
                if (splitArray.length > 1) {
                    String dotValue = splitArray[1];
                    int diff = dotValue.length() + 1 - DECIMAL_DIGITS;
                    if (diff > 0) {
                        return source.subSequence(start, end - diff);
                    }
                }
                return null;
            }
        };

        mTotalEt.setFilters(new InputFilter[]{lengthfilter});
        mTotalEt.addTextChangedListener(this);

        if (mIsGroup) {
            mCountEt.setEnabled(true);
        } else {
            mCountEt.setText("1");
            mCountEt.setEnabled(false);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        DecimalFormat df = new DecimalFormat("0.00");
        String string = mTotalEt.getText().toString();
        Double d = TextUtils.isEmpty(string) ? 0 : Double.valueOf(string);
        mMoneyTv.setText(df.format(d));
    }

    @OnClick(R.id.btnSubmit)
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnSubmit:
                try {
                    Double amount = Double.valueOf(mTotalEt.getText().toString());
                    Double personLimit = Double.valueOf(mCountEt.getText().toString());
                    if (amount > 0 && personLimit > 0) {
                        submitPackage();
                    }
                } catch (Exception e) {
                }
                break;
        }
    }

    private void submitPackage() {
        WaitDialog.instance().showWaitNote(this);
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[6];
        final Settings setting = Settings.instance();
        String note = mNoteEt.getText().toString();
        final String finalNote = TextUtils.isEmpty(note) ? "恭喜发财" : note;
        String idType = mIsGroup ? "CircleId" : "SessionId";

        params[0] = new OkHttpClientManager.Param("UserId", setting.getUserId());
        params[1] = new OkHttpClientManager.Param("Token", setting.getToken());
        params[2] = new OkHttpClientManager.Param(idType, mId);
        params[3] = new OkHttpClientManager.Param("Amount", mTotalEt.getText().toString());
        params[4] = new OkHttpClientManager.Param("PersonLimit", mCountEt.getText().toString());
        params[5] = new OkHttpClientManager.Param("Note", finalNote);

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/im/newMoneyEnvelope", params,
                new OkHttpClientManager.ResultCallback<ResponseNewMoneyEnvelope>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("onError");
                        WaitDialog.instance().hideWaitNote();
                    }

                    @Override
                    public void onResponse(ResponseNewMoneyEnvelope response) {
                        Logger.info("success");
                        WaitDialog.instance().hideWaitNote();

                        if (response.isSuccess()) {
                            Intent intent = new Intent(CreateEnvelopeActivity.this, UpPayBaseActivity.class);
                            intent.putExtra("orderType", "7");
                            intent.putExtra("orderId", response.getId());
                            intent.putExtra("money", mTotalEt.getText().toString());
                            intent.putExtra("sessionId", mId);
                            intent.putExtra("note", finalNote);
                            intent.putExtra("isGroup", mIsGroup);
                            startActivity(intent);
                            finish();
                        }

                    }
                });
    }
}
