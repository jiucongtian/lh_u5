package com.longhuapuxin.u5;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.Utils;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.db.bean.VerifyBankCard;
import com.longhuapuxin.entity.ResponseGetAccount;
import com.longhuapuxin.entity.ResponseVerify;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZH on 2016/1/13.
 * Email zh@longhuapuxin.com
 */
public class BindBankActivity extends BaseActivity {

    @ViewInject(R.id.etLastName)
    private EditText mLastNameEt;

    @ViewInject(R.id.etFirstName)
    private EditText mFirstNameEt;

    @ViewInject(R.id.etIdCard)
    private EditText mIdCardEt;

    @ViewInject(R.id.etBankNo)
    private EditText mBankCardEt;

    @ViewInject(R.id.tvError)
    private TextView mErrorTv;

    @ViewInject(R.id.spn_BankName)
    private Spinner spnBankName;

    @ViewInject(R.id.spn_BankBranch)
    private Spinner spnBankBranch;

    private static final int VERIFY_MODE = 0x0001;
    private static final int  SetBank= 0x0002;
    public double mAmount;
    private String mLastName, mFirstName, mBankAccount, mIdNo;
    private String[] mBanks,mBankNames;
    private String mBankName,mBankBranch,mProvince,mCity;
    private ArrayAdapter mBankAdapter,mBankBranchAdapter;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case VERIFY_MODE:
                    if(Settings.instance().IsManualyWithdraw()){
                        Intent intent = new Intent(BindBankActivity.this, BindResultActivity.class);
                        startActivity(intent);
                    }
                    else{
                        Intent intent = new Intent(BindBankActivity.this, IdentifyBankCardDialogActivity.class);
                        startActivity(intent);
                    }

                    break;
                case SetBank:
                    List<String> banks=new ArrayList<String>();
                    int i=0;
                    for(String line :mBanks){
                        if(i++==0) continue;
                        String[] parts=line.split("\t");
                        String bankName=parts[0];
                        if(!banks.contains(bankName)){
                            banks.add(bankName);
                        }

                    }
                    mBankNames=banks.toArray(new String[0]);
                    mBankAdapter= new ArrayAdapter<String>(BindBankActivity.this,android.R.layout.simple_spinner_item,mBankNames);
                    mBankAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnBankName.setAdapter(mBankAdapter);

                    mBankAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    private void SetBranchs(){

        mBankBranch="";
        mProvince="";
        mCity="";
        List<String> branchs=new ArrayList<String>();
        if(!mBankName.equals("")){
           for(String line :mBanks){
               if(line.indexOf(mBankName)==0){
                   String[] parts=line.split("\t");
                   branchs.add(parts[1]);
               }
           }
        }
        mBankBranchAdapter=new ArrayAdapter<String>(BindBankActivity.this,android.R.layout.simple_spinner_item,branchs.toArray(new String[0]));
        mBankBranchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnBankBranch.setAdapter(mBankBranchAdapter);
        mBankBranchAdapter.notifyDataSetChanged();
    }

    class BankNameSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {

            mBankName=mBankNames[arg2];
            SetBranchs();
        }

        public void onNothingSelected(AdapterView<?> arg0) {
            mBankName="";
            SetBranchs();
        }
    }
    class BankBranchSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            int i=0;
            for(String line :mBanks){
                if(line.indexOf(mBankName)==0){
                    if(i++==arg2){
                        String[] parts=line.split("\t");
                        mBankBranch=parts[1];
                        mProvince=parts[2];
                        if(parts.length>3){
                            mCity=parts[3];
                        }
                        else{
                            mCity="";
                        }
                        break;
                    }


                }
            }
        }

        public void onNothingSelected(AdapterView<?> arg0) {
            mBankBranch="";
            mProvince="";
            mCity="";
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_bank);
        LinearLayout ly_bankName=(LinearLayout)findViewById(R.id.ly_BankName);
        LinearLayout ly_bankBranch=(LinearLayout)findViewById(R.id.ly_BankBranch);

        initHeader(R.string.bindBank);
        ViewUtils.inject(this);
        if(Settings.instance().IsManualyWithdraw()){
            mBankNames=new String[0];

            spnBankName.setOnItemSelectedListener(new BankNameSelectedListener());


            spnBankBranch.setOnItemSelectedListener(new BankBranchSelectedListener());

            GetBankInfo();
        }
        else{
            ly_bankBranch.setVisibility(View.GONE);
            ly_bankName.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        VerifyBankCard verification = Settings.instance().getmVerify();

        if(verification != null) {
            ResponseGetAccount.User user=Settings.instance().User;
            if(verification.mBankCard.equals(user.getBankAccount())
                && verification.mFirstName.equals(user.getFirstName())
                    && verification.mLastName.equals(user.getLastName())
                    && verification.mIdCard.equals(user.getIdNo())
                    ){
                //缓存的银行卡信息和用户的一样，说明银行卡已经认证
                Settings.instance().setmVerify(getBaseContext(),null);
            }
            else{
                mHandler.sendEmptyMessageDelayed(VERIFY_MODE, 10);
            }

        }
    }

    @OnClick(R.id.tvNextStep)
    public void onClick(View view) {
        Integer id = view.getId();
        switch (id) {
            case R.id.tvNextStep:

                /*****************test*********************/
//                mAmount = 0.45;
//                Intent intent = new Intent(BindBankActivity.this, IdentifyBankCardDialogActivity.class);
//                putPram(IdentifyBankCardDialogActivity.AMOUNT, mAmount);
//                startActivity(intent);
                /***************************************/

                if(validateInput()) {
                    mLastName = mLastNameEt.getText().toString();
                    mFirstName = mFirstNameEt.getText().toString();
                    mBankAccount = mBankCardEt.getText().toString();
                    mIdNo = mIdCardEt.getText().toString();
                    bindBank();
                }
                break;
        }
    }

    private boolean validateInput() {
        String regex = "^.{1,100}$";

        if(!Utils.validation(mLastNameEt.getText().toString(), regex)) {
            mErrorTv.setText("请输入正确姓氏");
            return false;
        }
        if(!Utils.validation(mFirstNameEt.getText().toString(), regex)) {
            mErrorTv.setText("请输入正确名字");
            return false;
        }

        regex = "^(\\d{15}$|^\\d{18}$|^\\d{17}(\\d|X|x))$";
        if(!Utils.validation(mIdCardEt.getText().toString(), regex)) {
            mErrorTv.setText("请输入正确身份证号码");
            return false;
        }

        regex = "^\\d{13,19}$";
        if(!Utils.validation(mBankCardEt.getText().toString(), regex)) {
            mErrorTv.setText("请输入正确银行卡卡号");
            return false;
        }
        if(Settings.instance().IsManualyWithdraw()){
            if(mBankName.equals("")){

                mErrorTv.setText("请选择银行");
                return false;
            }
            if(mBankBranch.equals("")){

                mErrorTv.setText("请选择分行");
                return false;
            }
        }
        return true;
    }
    private void GetBankInfo(){
        OkHttpClientManager.getAsyn(Settings.instance().getApiUrl()
                + "/content/banks.txt", new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                WaitDialog.instance().hideWaitNote();
                mErrorTv.setText("获取银行信息失败");

            }

            @Override
            public void onResponse(String response) {
                WaitDialog.instance().hideWaitNote();
                mBanks=response.split("\r\n");

                mHandler.sendEmptyMessageDelayed(SetBank, 10);
            }
        });
    }
    private void bindBank() {
        WaitDialog.instance().showWaitNote(this);
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[11];
        params[0] = new OkHttpClientManager.Param("UserId", Settings.instance().getUserId());
        params[1] = new OkHttpClientManager.Param("Token", Settings.instance().getToken());
        params[2] = new OkHttpClientManager.Param("ShopCode", "");
        params[3] = new OkHttpClientManager.Param("LastName", mLastName);
        params[4] = new OkHttpClientManager.Param("FirstName", mFirstName);
        params[5] = new OkHttpClientManager.Param("BankAccount", mBankAccount);
        params[6] = new OkHttpClientManager.Param("IDNo", mIdNo);
        params[7] = new OkHttpClientManager.Param("BankName", mBankName);
        params[8] = new OkHttpClientManager.Param("BankBranch", mBankBranch);
        params[9] = new OkHttpClientManager.Param("Province", mProvince);
        params[10] = new OkHttpClientManager.Param("City", mCity);
        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/basic/bindbank", params,
                new OkHttpClientManager.ResultCallback<ResponseVerify>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("newappoint.onError"
                                + e.toString());
                        WaitDialog.instance().hideWaitNote();
                        mErrorTv.setText(R.string.bindFailed);
                    }

                    @Override
                    public void onResponse(ResponseVerify response) {
                        Logger.info("onResponse is: "
                                + response.toString());
                        if (response.isSuccess()) {
                            VerifyBankCard verification = new VerifyBankCard();
                            verification.mLastName = mLastName;
                            verification.mFirstName = mFirstName;
                            verification.mBankCard = mBankAccount;
                            verification.mIdCard = mIdNo;
                            verification.mVerifiedTimes = 0;
                            verification.BankName=mBankName;
                            verification.BankBranch=mBankBranch;
                            verification.Province=mProvince;
                            verification.City=mCity;
                            verification.mId = response.getId();
                            verification.mAmount = response.getAmout();
                            //Toast.makeText(BindBankActivity.this, verification.mAmount.toString(), Toast.LENGTH_SHORT).show();
                            Settings.instance().setmVerify(BindBankActivity.this.getBaseContext(), verification);

                            //TODO need to increase the times of bind bank card.

                            mHandler.sendEmptyMessageDelayed(VERIFY_MODE, 10);
                        } else {
                            mErrorTv.setText(R.string.bindFailed);
                        }
                        WaitDialog.instance().hideWaitNote();
                    }

                });
    }
}
