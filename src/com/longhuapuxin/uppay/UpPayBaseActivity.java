package com.longhuapuxin.uppay;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.longhuapuxin.alipay.PayInstance;
import com.longhuapuxin.common.DateFormate;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.dao.ChatMessageDao;
import com.longhuapuxin.dao.SessionDao;
import com.longhuapuxin.db.bean.ChatMessage;
import com.longhuapuxin.db.bean.ChatMsg;
import com.longhuapuxin.db.bean.ChatSession;
import com.longhuapuxin.entity.ResponseCheckPayPassword;
import com.longhuapuxin.entity.ResponseCompleteWalletOperation;
import com.longhuapuxin.entity.ResponseGetAccount;
import com.longhuapuxin.entity.ResponseGetUnionPayTN;
import com.longhuapuxin.entity.ResponseGetWalletOperation;
import com.longhuapuxin.entity.ResponseNewWalletOperation;
import com.longhuapuxin.entity.ResponseOpenBouns;
import com.longhuapuxin.u5.BaseActivity;
import com.longhuapuxin.u5.ChatActivity;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.u5.Settings;
import com.squareup.okhttp.Request;
import com.unionpay.UPPayAssistEx;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class UpPayBaseActivity extends BaseActivity implements
        Runnable {
    public static final String LOG_TAG = "PayDemo";
    private Context mContext = null;
    //    private ProgressDialog mLoadingDialog = null;
    private ImageView balancePoint, quickPoint, alipayPoint;
    private RelativeLayout balanceLayout, quickLayout, alipayLayout;
    private FrameLayout goPay;
    private TextView amount, consume;
    private EditText password;
    private TextView error;
    public static final int PLUGIN_VALID = 0;
    public static final int PLUGIN_NOT_INSTALLED = -1;
    public static final int PLUGIN_NEED_UPGRADE = 2;
    public static final int PAY_EXIT = 100;
    public static final int ALIPAY_FAIL = 101;
    public static final int ALIPAY_SUCCESS = 102;
    public static final int ALIPAY_CHECK = 103;
    public static final int UPPAY = 104;
    public static final int UPPAY_FAIL = 105;
    public static final int UPPAY_SUCCESS = 106;
    public static final int UPPAY_CHECK = 107;
    public static final int BALANCE_PASSWORD_CHECK = 108;
    public static final int BALANCE_SUCCESS = 109;
    public static final int BALANCE_FAIL = 110;
    public static final int BALANCE_PASSWORD_ERROR = 111;
    public static final int BALANCE_CHECK = 112;
    public static final int RESULT_PAY = 1000;
    public static final int MONEY_RECIEVE = 300;
    public static final int MONEY_SEND = 301;
    private String payWay = null;
    private String orderType, orderId, shopCode, money;
    //    private String balance;
    private String myId, myTN;
    private AlertDialog dialog;
    private int windowWidth, windowHeight;
    private int aliPayStatue = 0;
    private int upPayStatue = 0;
    private int balanceStatue = 0;
    private boolean isPaySuccess = false;
    private String time = null;
    private TextView shopName, shopPhone, otherName;
    private LinearLayout shopInfo, otherInfo;
    private ImageView shopInfoLine, otherInfoLine, discountLine1, discountLine2;
    private RelativeLayout discountLayout, redLayout;
    private ImageView discountPoint, redPoint;
    private TextView type, realDiscount;
    private int discountWay = 0;
    private LinearLayout discountHowMuch;
    private Handler payHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ALIPAY_FAIL:
                    aliPayStatue = 2;
                    httpRequestCompleteWalletOperation(mContext, myId, String.valueOf(msg.obj), payWay, ALIPAY_CHECK, aliPayStatue, false);
                    break;
                case ALIPAY_SUCCESS:
                    aliPayStatue = 1;
                    httpRequestCompleteWalletOperation(mContext, myId, String.valueOf(msg.obj), payWay, ALIPAY_CHECK, aliPayStatue, false);
                    break;
                case PAY_EXIT:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
//                    红包
                    if (isPaySuccess && orderType.equals("7")) {
                        Intent intent = getIntent();
                        String sessionId = intent.getStringExtra("sessionId");
                        String orderId = intent.getStringExtra("orderId");
                        boolean isGroup = intent.getBooleanExtra("isGroup", false);
                        String note = intent.getStringExtra("note");
                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setSessionId(sessionId);
                        chatMessage.setText(note);
                        chatMessage.setOrderId(orderId);
                        chatMessage.setTime(DateFormate.String2Date(time));
                        chatMessage.setType(MONEY_SEND);
                        chatMessage.setIsGroup(isGroup);
                        ChatMessageDao msgHelper = new ChatMessageDao(UpPayBaseActivity.this);
                        msgHelper.add(chatMessage);
                        saveSessionDBForSessionId(sessionId, time, "红包：" + note, isGroup);
                        sendBroad(sessionId, isGroup);
                        Intent intent1 = new Intent(UpPayBaseActivity.this, ChatActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("chatMsg", chatMessage2ChatMsg(chatMessage));
                        intent1.putExtras(bundle);
                        intent1.putExtra("isGroup", true);
                        startActivity(intent1);
                    }
//                    预约
                    if (isPaySuccess) {
                        setResult(RESULT_PAY); //intent为A传来的带有Bundle的intent，当然也可以自己定义新的Bundle
//                        finish();
                        UpPayBaseActivity.this.finish();
                    }
                    break;
                case ALIPAY_CHECK:
                    httpRequestGetWalletOperation(mContext, myId, ALIPAY_SUCCESS, ALIPAY_FAIL);
                    break;
                case UPPAY:
                    Log.e(LOG_TAG, " " + "" + msg.obj);
//                    if (mLoadingDialog.isShowing()) {
//                        mLoadingDialog.dismiss();
//                    }

                    String tn = "";
                    if (msg.obj == null || ((String) msg.obj).length() == 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("错误提示");
                        builder.setMessage("网络连接失败,请重试!");
                        builder.setNegativeButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.create().show();
                    } else {
                        tn = (String) msg.obj;
                        /*************************************************
                         * 步骤2：通过银联工具类启动支付插件
                         ************************************************/
                        doStartUnionPayPlugin(tn, mMode);
                    }
                    break;
                case UPPAY_SUCCESS:
                    upPayStatue = 1;
                    httpRequestCompleteWalletOperation(mContext, myId, String.valueOf(msg.obj), payWay, UPPAY_CHECK, upPayStatue, false);
                    break;
                case UPPAY_FAIL:
                    upPayStatue = 2;
                    httpRequestCompleteWalletOperation(mContext, myId, String.valueOf(msg.obj), payWay, UPPAY_CHECK, upPayStatue, false);
                    break;
                case UPPAY_CHECK:
                    httpRequestGetWalletOperation(mContext, myId, UPPAY_SUCCESS, UPPAY_FAIL);
                    break;
                case BALANCE_PASSWORD_CHECK:
                    httpRequestCheckPayPassword((String) msg.obj);
                    break;
                case BALANCE_SUCCESS:
                    balanceStatue = 1;
                    httpRequestCompleteWalletOperation(mContext, myId, String.valueOf(msg.obj), payWay, BALANCE_CHECK, balanceStatue, true);
                    break;
                case BALANCE_CHECK:
                    httpRequestGetWalletOperation(mContext, myId, BALANCE_SUCCESS, BALANCE_FAIL);
                    break;
                case BALANCE_FAIL:
                    balanceStatue = 2;
                    httpRequestCompleteWalletOperation(mContext, myId, String.valueOf(msg.obj), payWay, BALANCE_CHECK, balanceStatue, true);
                    break;
                case BALANCE_PASSWORD_ERROR:
                    showDialogPasswordFail();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /*****************************************************************
     * mMode参数解释： "00" - 启动银联正式环境 "01" - 连接银联测试环境
     *****************************************************************/
    private final String mMode = "00";
//    private static final String TN_URL_01 = "http://101.231.204.84:8091/sim/getacptn";

    private final View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v == balanceLayout) {
                balancePoint.setImageResource(R.drawable.home_content_pay_point);
                quickPoint.setImageResource(R.drawable.home_content_pay_point);
                alipayPoint.setImageResource(R.drawable.home_content_pay_point);
                payWay = "1";
                balancePoint.setImageResource(R.drawable.home_content_pay_point_sel);
            } else if (v == quickLayout) {
                balancePoint.setImageResource(R.drawable.home_content_pay_point);
                quickPoint.setImageResource(R.drawable.home_content_pay_point);
                alipayPoint.setImageResource(R.drawable.home_content_pay_point);
                payWay = "3";
                quickPoint.setImageResource(R.drawable.home_content_pay_point_sel);
            } else if (v == alipayLayout) {
                balancePoint.setImageResource(R.drawable.home_content_pay_point);
                quickPoint.setImageResource(R.drawable.home_content_pay_point);
                alipayPoint.setImageResource(R.drawable.home_content_pay_point);
                payWay = "2";
                alipayPoint.setImageResource(R.drawable.home_content_pay_point_sel);
            } else if (v == goPay) {
                if (payWay == null) {
                    return;
                } else if (payWay.equals("1")) {
//                    Message msg = new Message();
//                    msg.what = BALANCE_PASSWORD_CHECK;
//                    msg.obj = password.getText().toString();
//                    payHandler.sendMessage(msg);
                    showDialogBalance();
                } else if (payWay.equals("2")) {
                    String amountText;
                    if (discountWay == 1) {
                        amountText = Double.valueOf(money) * (100 - Double.valueOf(httpDiscount)) / 100 + "";
                    } else {
                        amountText = Double.valueOf(money) + "";
                    }
                    new PayInstance(UpPayBaseActivity.this, getApplicationContext())
                            .pay(v, "",
                                    amountText,
                                    myId,
                                    aliPayCallBack);
                } else if (payWay.equals("3")) {
                    /*************************************************
                     * 步骤1：从网络开始,获取交易流水号即TN
                     ************************************************/
//                    new Thread(UpPayBaseActivity.this).start();
                    String amount = null;
                    if (discountWay == 1) {
                        amount = Double.valueOf(money) * (100 - Double.valueOf(httpDiscount)) / 100 + "";
                    } else {
                        amount = Double.valueOf(money) + "";
                    }
                    httpRequestGetUnionPayTn(myId, amount);
                }
            } else if (v == discountLayout) {
                discountWay = 1;
                discountPoint.setImageResource(R.drawable.home_content_pay_point_sel);
                redPoint.setImageResource(R.drawable.home_content_pay_point);
            } else if (v == redLayout) {
                discountWay = 2;
                redPoint.setImageResource(R.drawable.home_content_pay_point_sel);
                discountPoint.setImageResource(R.drawable.home_content_pay_point);
            }


        }
    };
    private PayInstance.PayCallback aliPayCallBack = new PayInstance.PayCallback() {

        @Override
        public void OnSuccess() {
            Message msg = new Message();
            msg.what = ALIPAY_SUCCESS;
            msg.obj = "1";
            payHandler.sendMessage(msg);
        }

        @Override
        public void OnFailed() {
            Message msg = new Message();
            msg.what = ALIPAY_FAIL;
            msg.obj = "3";
            payHandler.sendMessage(msg);
        }

    };

    private void getWindowSize() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        windowWidth = dm.widthPixels;
        windowHeight = dm.heightPixels;
    }

    public void doStartUnionPayPlugin(String tn,
                                      String mode) {
        // mMode参数解释：
        // 0 - 启动银联正式环境
        // 1 - 连接银联测试环境

        int ret = UPPayAssistEx.startPay(this, null, null, tn, mode);
        if (ret == PLUGIN_NEED_UPGRADE || ret == PLUGIN_NOT_INSTALLED) {
            // 需要重新安装控件
            Log.e(LOG_TAG, " plugin not found or need upgrade!!!");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("完成购买需要安装银联支付控件，是否安装？");

            builder.setNegativeButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            UPPayAssistEx.installUPPayPlugin(UpPayBaseActivity.this);
                            dialog.dismiss();
                        }
                    });

            builder.setPositiveButton("取消",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();

        }
        Log.e(LOG_TAG, "" + ret);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_all_pay);
        initHeader("付款");
        httpRequestGetAccount();
    }

    private void adjust() {
        Intent intent = getIntent();
        money = intent.getStringExtra("money");
        orderType = intent.getStringExtra("orderType");
        orderId = intent.getStringExtra("orderId");
        shopCode = intent.getStringExtra("shopCode");
        if (shopCode == null) {
            shopCode = "";
        }
        if (orderType.equals("4") || orderType.equals("5")  ) {
            balanceLayout.setVisibility(View.GONE);
            shopInfo.setVisibility(View.GONE);
            shopInfoLine.setVisibility(View.GONE);
            otherInfo.setVisibility(View.GONE);
            otherInfoLine.setVisibility(View.GONE);

        }
        else if(orderType.equals("7")){

            shopInfo.setVisibility(View.GONE);
            shopInfoLine.setVisibility(View.GONE);
            otherInfo.setVisibility(View.GONE);
            otherInfoLine.setVisibility(View.GONE);
        }
        if (orderType.equals("2") || orderType.equals("3")) {
            otherInfo.setVisibility(View.GONE);
            otherInfoLine.setVisibility(View.GONE);
        }
        if (orderType.equals("1")) {
            shopInfo.setVisibility(View.GONE);
            shopInfoLine.setVisibility(View.GONE);
        }
        amount.setText(Settings.instance().User.getBalance());
        consume.setText("￥" + money);
        httpRequestNewWalletOperation(orderType, orderId, shopCode, money);
    }

    private void initViews() {
        balancePoint = (ImageView) findViewById(R.id.balance_point);
        quickPoint = (ImageView) findViewById(R.id.quick_point);
        alipayPoint = (ImageView) findViewById(R.id.alipay_point);
        balanceLayout = (RelativeLayout) findViewById(R.id.balance_pay);
        balanceLayout.setOnClickListener(mClickListener);
        quickLayout = (RelativeLayout) findViewById(R.id.quick_pay);
        quickLayout.setOnClickListener(mClickListener);
        alipayLayout = (RelativeLayout) findViewById(R.id.alipay);
        alipayLayout.setOnClickListener(mClickListener);
        goPay = (FrameLayout) findViewById(R.id.pay);
        goPay.setOnClickListener(mClickListener);
        amount = (TextView) findViewById(R.id.amount);
        consume = (TextView) findViewById(R.id.consume);
        shopInfo = (LinearLayout) findViewById(R.id.shop_info);
        otherInfo = (LinearLayout) findViewById(R.id.other_info);
        shopInfoLine = (ImageView) findViewById(R.id.shop_info_line);
        otherInfoLine = (ImageView) findViewById(R.id.other_info_line);
        discountLine1 = (ImageView) findViewById(R.id.discount_line1);
        discountLine2 = (ImageView) findViewById(R.id.discount_line2);
        discountLayout = (RelativeLayout) findViewById(R.id.discount_layout);
        discountLayout.setOnClickListener(mClickListener);
        redLayout = (RelativeLayout) findViewById(R.id.red_layout);
        redLayout.setOnClickListener(mClickListener);
        type = (TextView) findViewById(R.id.type);
        discountPoint = (ImageView) findViewById(R.id.discount_point);
        redPoint = (ImageView) findViewById(R.id.red_point);
        discountHowMuch = (LinearLayout) findViewById(R.id.discount_how_much);
        shopName = (TextView) findViewById(R.id.shop_name);
        shopPhone = (TextView) findViewById(R.id.shop_phone_layout);
        otherName = (TextView) findViewById(R.id.name);
        realDiscount = (TextView) findViewById(R.id.real_discount);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*************************************************
         * 步骤3：处理银联手机支付控件返回的支付结果
         ************************************************/
        if (data == null) {
            return;
        }

        /*
         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
         */
        String str = data.getExtras().getString("pay_result");
        if (str.equalsIgnoreCase("success")) {
            // 支付成功后，extra中如果存在result_data，取出校验
            // result_data结构见c）result_data参数说明
            if (data.hasExtra("result_data")) {
                String result = data.getExtras().getString("result_data");
                try {
                    JSONObject resultJson = new JSONObject(result);
                    String sign = resultJson.getString("sign");
                    String dataOrg = resultJson.getString("data");
                    // 验签证书同后台验签证书
                    // 此处的verify，商户需送去商户后台做验签
                    boolean ret = true;// RSAUtil.verify(dataOrg, sign, mMode);
                    if (dataOrg.indexOf("pay_result=success") == 0) {
                        // 验证通过后，显示支付结果
//                        msg = "支付成功！";
                        Message msg = new Message();
                        msg.what = UPPAY_SUCCESS;
                        msg.obj = "1";
                        payHandler.sendMessage(msg);
                    } else {
                        // 验证不通过后的处理
                        // 建议通过商户后台查询支付结果
//                        msg = "支付失败！";
                        Message msg = new Message();
                        msg.what = UPPAY_FAIL;
                        msg.obj = "3";
                        payHandler.sendMessage(msg);
                    }
                } catch (JSONException e) {
                }
            } else {
                // 未收到签名信息
                // 建议通过商户后台查询支付结果
//                msg = "支付成功！";
            }
        } else if (str.equalsIgnoreCase("fail")) {
//            msg = "支付失败！";
            Message msg = new Message();
            msg.what = UPPAY_FAIL;
            msg.obj = "3";
            payHandler.sendMessage(msg);
        } else if (str.equalsIgnoreCase("cancel")) {
//            msg = "用户取消了支付";
        }

    }

    @Override
    public void run() {
//        String tn = null;
//        InputStream is;
//        try {
//
//            String url = myTN;
//
//            URL myURL = new URL(url);
//            URLConnection ucon = myURL.openConnection();
//            ucon.setConnectTimeout(120000);
//            is = ucon.getInputStream();
//            int i = -1;
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            while ((i = is.read()) != -1) {
//                baos.write(i);
//            }
//
//            tn = baos.toString();
//            is.close();
//            baos.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        Message msg = new Message();
        msg.what = UPPAY;
        msg.obj = myTN;
        payHandler.sendMessage(msg);
    }

    private String httpDiscount = "0";
    private String httpMaxVal = "0";
    private String httpMinVal = "0";

    private void httpRequestNewWalletOperation(String orderType, String orderId, String shopCode, String amount) {
        WaitDialog.instance().showWaitNote(this);
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[6];
        params[0] = new OkHttpClientManager.Param("OrderType", orderType);
        params[1] = new OkHttpClientManager.Param("OrderId", orderId);
        params[2] = new OkHttpClientManager.Param("ShopCode", shopCode);
        params[3] = new OkHttpClientManager.Param("UserId", Settings.instance()
                .getUserId());
        params[4] = new OkHttpClientManager.Param("Token", Settings.instance()
                .getToken());
        params[5] = new OkHttpClientManager.Param("Total", amount);


        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/basic/newwalletoperation", params,
                new OkHttpClientManager.ResultCallback<ResponseNewWalletOperation>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("onError"
                                + e.toString());
                        WaitDialog.instance().hideWaitNote();
                    }

                    @Override
                    public void onResponse(final ResponseNewWalletOperation response) {
                        Logger.info("onResponse"
                                + response);
                        if (response.isSuccess()) {
                            myId = response.getId();
//                            myTN = response.getTN();
                            httpDiscount = response.getDiscount();
                            if (response.getShopName() != null) {
                                shopName.setText(response.getShopName());
                                shopPhone.setText(response.getPhone());
                            }
                            if (response.getNickName() != null) {
                                otherName.setText(response.getNickName());
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    goPay.setVisibility(View.VISIBLE);
                                    if (response.getDiscount().equals("0") && response.getBounsSettings() == null) {

                                    } else if (!response.getDiscount().equals("0") && response.getBounsSettings() != null) {
                                        type.setVisibility(View.VISIBLE);
                                        discountLine1.setVisibility(View.VISIBLE);
                                        discountLayout.setVisibility(View.VISIBLE);
//                                        discountLine2.setVisibility(View.VISIBLE);
//                                        redLayout.setVisibility(View.VISIBLE);
                                        realDiscount.setText((100 - Double.valueOf(response.getDiscount())) / 10 + "折");
                                        for (int i = 0; i < response.getBounsSettings().size(); i++) {
                                            if (Double.parseDouble(money) < Double.parseDouble(response.getBounsSettings().get(i).getAmountFrom())) {
                                                redLayout.setClickable(false);
                                                discountLine2.setVisibility(View.VISIBLE);
                                                redLayout.setVisibility(View.VISIBLE);
                                                TextView tv = new TextView(UpPayBaseActivity.this);
                                                tv.setPadding(0, 2, 0, 0);
                                                tv.setTextColor(getResources().getColor(R.color.shallow_gray));
                                                tv.setTextSize(12);
                                                tv.setText("满" + response.getBounsSettings().get(i).getAmountFrom() + "送" + response.getBounsSettings().get(i).getMinVal() + "-" + response.getBounsSettings().get(i).getMaxVal() + "红包");
                                                discountHowMuch.addView(tv);
                                                break;
                                            } else if (Double.parseDouble(money) >= Double.parseDouble(response.getBounsSettings().get(i).getAmountFrom())) {
                                                if (response.getBounsSettings().size() > i + 1 && Double.parseDouble(money) < Double.parseDouble(response.getBounsSettings().get(i + 1).getAmountFrom())) {
                                                    discountLine2.setVisibility(View.VISIBLE);
                                                    redLayout.setVisibility(View.VISIBLE);
                                                    TextView tv1 = new TextView(UpPayBaseActivity.this);
                                                    tv1.setPadding(0, 2, 0, 0);
                                                    tv1.setTextColor(getResources().getColor(R.color.orange));
                                                    tv1.setTextSize(12);
                                                    tv1.setText("满" + response.getBounsSettings().get(i).getAmountFrom() + "送" + response.getBounsSettings().get(i).getMinVal() + "-" + response.getBounsSettings().get(i).getMaxVal() + "红包");
                                                    discountHowMuch.addView(tv1);
                                                    TextView tv2 = new TextView(UpPayBaseActivity.this);
                                                    tv2.setPadding(0, 2, 0, 0);
                                                    tv2.setTextColor(getResources().getColor(R.color.shallow_gray));
                                                    tv2.setTextSize(12);
                                                    tv2.setText("满" + response.getBounsSettings().get(i + 1).getAmountFrom() + "送" + response.getBounsSettings().get(i + 1).getMinVal() + "-" + response.getBounsSettings().get(i + 1).getMaxVal() + "红包");
                                                    discountHowMuch.addView(tv2);
                                                    httpMinVal = response.getBounsSettings().get(i).getMinVal();
                                                    httpMaxVal = response.getBounsSettings().get(i).getMaxVal();
                                                    break;
                                                } else if (response.getBounsSettings().size() == i + 1) {
                                                    discountLine2.setVisibility(View.VISIBLE);
                                                    redLayout.setVisibility(View.VISIBLE);
                                                    TextView tv1 = new TextView(UpPayBaseActivity.this);
                                                    tv1.setPadding(0, 2, 0, 0);
                                                    tv1.setTextColor(getResources().getColor(R.color.orange));
                                                    tv1.setTextSize(12);
                                                    tv1.setText("满" + response.getBounsSettings().get(i).getAmountFrom() + "送" + response.getBounsSettings().get(i).getMinVal() + "-" + response.getBounsSettings().get(i).getMaxVal() + "红包");
                                                    discountHowMuch.addView(tv1);
                                                    httpMinVal = response.getBounsSettings().get(i).getMinVal();
                                                    httpMaxVal = response.getBounsSettings().get(i).getMaxVal();
                                                    break;
                                                }
                                            }
                                        }
                                    } else if (response.getDiscount().equals("0")) {
                                        for (int i = 0; i < response.getBounsSettings().size(); i++) {
                                            if (Double.parseDouble(money) < Double.parseDouble(response.getBounsSettings().get(i).getAmountFrom())) {
                                                discountLine2.setVisibility(View.VISIBLE);
                                                redLayout.setVisibility(View.VISIBLE);
                                                TextView tv = new TextView(UpPayBaseActivity.this);
                                                tv.setPadding(0, 2, 0, 0);
                                                tv.setTextColor(getResources().getColor(R.color.shallow_gray));
                                                tv.setTextSize(12);
                                                tv.setText("满" + response.getBounsSettings().get(i).getAmountFrom() + "送" + response.getBounsSettings().get(i).getMinVal() + "-" + response.getBounsSettings().get(i).getMaxVal() + "红包");
                                                discountHowMuch.addView(tv);
                                            } else if (Double.parseDouble(money) >= Double.parseDouble(response.getBounsSettings().get(i).getAmountFrom())
                                                    && (
                                                    i==response.getBounsSettings().size()-1
                                                            || Double.parseDouble(money) < Double.parseDouble(response.getBounsSettings().get(i + 1).getAmountFrom())
                                            )) {
                                                discountLine2.setVisibility(View.VISIBLE);
                                                redLayout.setVisibility(View.VISIBLE);
                                                TextView tv1 = new TextView(UpPayBaseActivity.this);
                                                tv1.setPadding(0, 2, 0, 0);
                                                tv1.setTextColor(getResources().getColor(R.color.orange));
                                                tv1.setTextSize(12);
                                                tv1.setText("满" + response.getBounsSettings().get(i).getAmountFrom() + "送" + response.getBounsSettings().get(i).getMinVal() + "-" + response.getBounsSettings().get(i).getMaxVal() + "红包");
                                                discountHowMuch.addView(tv1);
                                                if(i<response.getBounsSettings().size()-1) {
                                                    TextView tv2 = new TextView(UpPayBaseActivity.this);
                                                    tv2.setPadding(0, 2, 0, 0);
                                                    tv2.setTextColor(getResources().getColor(R.color.shallow_gray));
                                                    tv2.setTextSize(12);
                                                    tv2.setText("满" + response.getBounsSettings().get(i + 1).getAmountFrom() + "送" + response.getBounsSettings().get(i + 1).getMinVal() + "-" + response.getBounsSettings().get(i + 1).getMaxVal() + "红包");
                                                    discountHowMuch.addView(tv2);
                                                }
                                                httpMinVal = response.getBounsSettings().get(i).getMinVal();
                                                httpMaxVal = response.getBounsSettings().get(i).getMaxVal();
                                            }
                                        }
                                    } else if (response.getBounsSettings() == null) {
                                        realDiscount.setText(response.getDiscount() + "折");
                                        discountLine2.setVisibility(View.VISIBLE);
                                        redLayout.setVisibility(View.VISIBLE);
                                    }
                                    WaitDialog.instance().hideWaitNote();
                                }
                            });

                        } else {
                            Logger.info("httpRequestNewWalletOperation " + response.getErrorCode());
                            Logger.info("httpRequestNewWalletOperation " + response.getErrorMessage());
                            WaitDialog.instance().hideWaitNote();
                            showDialogRepay();
                        }
                    }

                });
    }

    public void httpRequestGetWalletOperation(Context context, String id, final int forWhatSuccess, final int forWhatFail) {
        showDialogPayLoad();
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[3];
        params[0] = new OkHttpClientManager.Param("Id", id);
        params[1] = new OkHttpClientManager.Param("UserId", Settings.instance()
                .getUserId());
        params[2] = new OkHttpClientManager.Param("Token", Settings.instance()
                .getToken());
        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/basic/getwalletoperation", params,
                new OkHttpClientManager.ResultCallback<ResponseGetWalletOperation>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("onError"
                                + e.toString());
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onResponse(ResponseGetWalletOperation response) {
                        Logger.info("onResponse"
                                + response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (response.isSuccess()) {
                            if (response.getStatus().equals("1")) {
                                Message msg = new Message();
                                msg.what = forWhatSuccess;
                                msg.obj = "1";
                                payHandler.sendMessage(msg);
                            } else {
                                Message msg = new Message();
                                msg.what = forWhatFail;
                                msg.obj = "3";
                                payHandler.sendMessage(msg);
                            }
                        } else {
                            Logger.info("httpRequestGetWalletOperationForAliPay " + response.getErrorCode());
                            Logger.info("httpRequestGetWalletOperationForAliPay " + response.getErrorMessage());

                        }
                    }

                });
    }

    private String bounsId = null;

    public void httpRequestCompleteWalletOperation(Context context, String id, String status, String payWay, final int whichCheck, final int statue, final boolean isBalance) {
        WaitDialog.instance().showWaitNote(context);
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[10];
        params[0] = new OkHttpClientManager.Param("Id", id);
        params[1] = new OkHttpClientManager.Param("Status", status);
        params[2] = new OkHttpClientManager.Param("ResultText", "");
        params[3] = new OkHttpClientManager.Param("PayWay", payWay);
        String passwordValue = "";
        if (password != null) {
            passwordValue = password.getText().toString();
        }
        params[4] = new OkHttpClientManager.Param("PayPassword", passwordValue);
        params[5] = new OkHttpClientManager.Param("UserId", Settings.instance()
                .getUserId());
        params[6] = new OkHttpClientManager.Param("Token", Settings.instance()
                .getToken());
        if (discountWay == 1) {
            params[7] = new OkHttpClientManager.Param("Discount", httpDiscount);
        } else if (discountWay == 2) {
            params[7] = new OkHttpClientManager.Param("Discount", "0");
        } else {
            params[7] = new OkHttpClientManager.Param("Discount", "0");
        }
        params[8] = new OkHttpClientManager.Param("MaxVal", httpMaxVal);
        params[9] = new OkHttpClientManager.Param("MinVal", httpMinVal);

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/basic/completewalletoperation", params,
                new OkHttpClientManager.ResultCallback<ResponseCompleteWalletOperation>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("onError"
                                + e.toString());
                        WaitDialog.instance().hideWaitNote();
                    }

                    @Override
                    public void onResponse(ResponseCompleteWalletOperation response) {
                        Logger.info("onResponse"
                                + response);
                        WaitDialog.instance().hideWaitNote();
                        if (response.isSuccess()) {
                            bounsId = response.getBounsId();
                            if (statue == 1) {
                                isPaySuccess = true;
                                time = response.getTime();
                                if (orderType.equals("4")) {
                                    double money1 = Double.valueOf(Settings.instance().User.getBalance());
                                    double money2 = Double.valueOf(money);
                                    double money3 = money1 + money2;
                                    DecimalFormat decimalFormat = new DecimalFormat("0.00");
                                    Settings.instance().User.setBalance(decimalFormat.format(money3));
                                } else if (isBalance && !orderType.equals("4")) {
                                    double money1 = Double.valueOf(Settings.instance().User.getBalance());
                                    double money2 = Double.valueOf(money);
                                    double money3 = money1 - money2;
                                    DecimalFormat decimalFormat = new DecimalFormat("0.00");
                                    Settings.instance().User.setBalance(decimalFormat.format(money3));
                                }
                                showDialogPaySuccess();
                            } else if (statue == 2) {
                                isPaySuccess = false;
                                showDialogPayFail(whichCheck);
                            }
                        } else {
                            Logger.info("httpRequestCompleteWalletOperation " + response.getErrorCode());
                            Logger.info("httpRequestCompleteWalletOperation " + response.getErrorMessage());

                        }
                    }

                }

        );
    }

    private void showDialogPaySuccess() {
        if (dialog != null) {
            dialog.dismiss();
        }
        View view = LayoutInflater.from(this).inflate(
                R.layout.show_dialog_pay_success, null);
        view.findFocus();
        dialog = new AlertDialog.Builder(this).create();
        // 这里要先调用show方法，在设置视图
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = windowWidth;
//        params.height = windowHeight;
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setContentView(view);
        LinearLayout successAffirm = (LinearLayout) view
                .findViewById(R.id.success_affirm);
        successAffirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                payHandler.sendEmptyMessage(PAY_EXIT);
            }
        });
        view.findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        TextView dialogShopName = (TextView) view
                .findViewById(R.id.shop_name);
        TextView amount = (TextView) view
                .findViewById(R.id.amount);
        amount.setText(money + "元");
        TextView consume = (TextView) view
                .findViewById(R.id.consume);
        if (discountWay == 1 && !httpDiscount.equals("0")) {
            consume.setText((Double.parseDouble(money) *(100- Double.parseDouble(httpDiscount)) / 100) + "");
        } else {
            consume.setText("" + Double.parseDouble(money));
        }
        TextView dialogShopPhone = (TextView) view
                .findViewById(R.id.shop_phone_layout);
        TextView dialogName = (TextView) view
                .findViewById(R.id.name);
        LinearLayout shopInfo = (LinearLayout) view
                .findViewById(R.id.shop_info);
        ImageView shopInfoLine = (ImageView) view
                .findViewById(R.id.shop_info_line);
        ImageView otherInfoLine = (ImageView) view
                .findViewById(R.id.other_info_line);
        ImageView redTypeLine = (ImageView) view
                .findViewById(R.id.red_type_line);
        ImageView discountTypeLine = (ImageView) view
                .findViewById(R.id.discount_type_line);
        LinearLayout discountType = (LinearLayout) view
                .findViewById(R.id.discount_type);
        LinearLayout redType = (LinearLayout) view
                .findViewById(R.id.red_type);
        final RelativeLayout redBigBag = (RelativeLayout) view.findViewById(R.id.redBigBag);
        final TextView redMoney = (TextView) view.findViewById(R.id.red_money);
        final ImageView redClose = (ImageView) view.findViewById(R.id.close);
        redClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redBigBag.setVisibility(View.GONE);
            }
        });
        if (orderType.equals("1")) {
            otherInfo.setVisibility(View.VISIBLE);
            otherInfoLine.setVisibility(View.VISIBLE);
        } else if (orderType.equals("2") || orderType.equals("3")) {
            shopInfo.setVisibility(View.VISIBLE);
            shopInfoLine.setVisibility(View.VISIBLE);
        }
        if (discountWay == 1) {
            TextView tvDiscount = (TextView) view.findViewById(R.id.discount);
            tvDiscount.setText(realDiscount.getText().toString());
            discountTypeLine.setVisibility(View.VISIBLE);
            discountType.setVisibility(View.VISIBLE);
        } else if (discountWay == 2) {
            redTypeLine.setVisibility(View.VISIBLE);
            redType.setVisibility(View.VISIBLE);

            view.findViewById(R.id.redBag).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    httpRequestOpenBouns(redBigBag, redMoney);
                }
            });
        }
        if (shopName.getText() != null) {
            dialogShopName.setText(shopName.getText());
            dialogShopPhone.setText(shopPhone.getText());
        }
        if (otherName.getText() != null) {
            dialogName.setText(otherName.getText());
        }

    }

    private void showDialogBalance() {
        if (dialog != null) {
            dialog.dismiss();
        }
        View view = LayoutInflater.from(this).inflate(
                R.layout.show_dialog_balance, null);
        dialog = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.show_dialog_balance, null);
        dialog.setView(layout);
        // 这里要先调用show方法，在设置视图
        dialog.show();
        password = (EditText) view.findViewById(R.id.password);
        error = (TextView) view.findViewById(R.id.error);
        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message msg = new Message();
                msg.what = BALANCE_PASSWORD_CHECK;
                msg.obj = password.getText().toString();
                payHandler.sendMessage(msg);
            }
        });
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(view);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = windowWidth / 3 * 2;
        params.height = windowHeight / 2 * 1;
        dialog.getWindow().setAttributes(params);
    }

    private void showDialogPasswordFail() {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = new AlertDialog.Builder(this).create();
        // 这里要先调用show方法，在设置视图
        dialog.show();
        View view = LayoutInflater.from(this).inflate(
                R.layout.show_dialog_pay_password, null);
        TextView passwordFail = (TextView) view
                .findViewById(R.id.password_fail);
        passwordFail.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(view);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = windowWidth / 3 * 2;
        params.height = windowHeight / 12 * 5;
        dialog.getWindow().setAttributes(params);
    }

    private void showDialogPayFail(final int whichCheck) {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = new AlertDialog.Builder(this).create();
        // 这里要先调用show方法，在设置视图
        dialog.show();
        View view = LayoutInflater.from(this).inflate(
                R.layout.show_dialog_pay_fail, null);
        TextView giveUpTransction = (TextView) view
                .findViewById(R.id.give_up_transction);
        giveUpTransction.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                payHandler.sendEmptyMessage(PAY_EXIT);
            }
        });
        TextView checkResult = (TextView) view.findViewById(R.id.check_result);
        checkResult.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Message msg = new Message();
                msg.what = whichCheck;
                payHandler.sendMessage(msg);

            }
        });
        dialog.setContentView(view);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = windowWidth / 3 * 2;
        params.height = windowHeight / 12 * 5;
        dialog.getWindow().setAttributes(params);
    }

    private void showDialogRepay() {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = new AlertDialog.Builder(this).create();
        // 这里要先调用show方法，在设置视图
        dialog.show();
        View view = LayoutInflater.from(this).inflate(
                R.layout.show_dialog_repay, null);
        TextView repay = (TextView) view
                .findViewById(R.id.repay);
        repay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dialog.setContentView(view);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = windowWidth / 3 * 2;
        params.height = windowHeight / 12 * 5;
        dialog.getWindow().setAttributes(params);
    }

    private List<ImageView> loadImgList;
    private ImageView load0, load1, load2, load3, load4, load5, load6;
    private int i = 0;

    private void showDialogPayLoad() {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = new AlertDialog.Builder(this).create();
        // 这里要先调用show方法，在设置视图
        dialog.show();
        View view = LayoutInflater.from(this).inflate(
                R.layout.show_dialog_pay_load, null);
        load0 = (ImageView) view.findViewById(R.id.load_img_1);
        load1 = (ImageView) view.findViewById(R.id.load_img_2);
        load2 = (ImageView) view.findViewById(R.id.load_img_3);
        load3 = (ImageView) view.findViewById(R.id.load_img_4);
        load4 = (ImageView) view.findViewById(R.id.load_img_5);
        load5 = (ImageView) view.findViewById(R.id.load_img_6);
        load6 = (ImageView) view.findViewById(R.id.load_img_7);
        loadImgList = new ArrayList<ImageView>();
        loadImgList.add(load0);
        loadImgList.add(load1);
        loadImgList.add(load2);
        loadImgList.add(load3);
        loadImgList.add(load4);
        loadImgList.add(load5);
        loadImgList.add(load6);
        dialog.setContentView(view);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = windowWidth / 3 * 2;
        params.height = windowHeight / 12 * 5;
        dialog.getWindow().setAttributes(params);
    }

    private void httpRequestGetAccount() {
        WaitDialog.instance().showWaitNote(mContext);
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[3];
        params[0] = new OkHttpClientManager.Param("UserId", Settings.instance()
                .getUserId());
        params[1] = new OkHttpClientManager.Param("Token", Settings.instance()
                .getToken());
        params[2] = new OkHttpClientManager.Param("TargetUserId", Settings.instance()
                .getUserId());

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/auth/getaccount", params,
                new OkHttpClientManager.ResultCallback<ResponseGetAccount>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("onError"
                                + e.toString());
                        WaitDialog.instance().hideWaitNote();
                    }

                    @Override
                    public void onResponse(ResponseGetAccount response) {

                        Logger.info("onResponse"
                                + response);
                        if (response.isSuccess()) {
                            Settings.instance().User = response.getUser();
                            getWindowSize();
                            initViews();
                            adjust();
                            WaitDialog.instance().hideWaitNote();
                        } else {
                            Logger.info("httpRequestGetAccount " + response.getErrorCode());
                            Logger.info("httpRequestGetAccount " + response.getErrorMessage());
                            WaitDialog.instance().hideWaitNote();
                        }
                    }

                }

        );

    }

    private void httpRequestCheckPayPassword(String password) {
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[3];
        params[0] = new OkHttpClientManager.Param("PayPassword", password);
        params[1] = new OkHttpClientManager.Param("UserId", Settings.instance().getUserId());
        params[2] = new OkHttpClientManager.Param("Token", Settings.instance().getToken());

        OkHttpClientManager
                .postAsyn(
                        Settings.instance().getApiUrl()
                                + "/pay/checkpaypassword",
                        params,
                        new OkHttpClientManager.ResultCallback<ResponseCheckPayPassword>() {

                            @Override
                            public void onError(Request request, Exception e) {

                            }

                            @Override
                            public void onResponse(
                                    ResponseCheckPayPassword response) {
                                if (response.isSuccess()) {
                                    if (response.isIsCorrect()) {
                                        Message msg = new Message();
                                        msg.what = BALANCE_SUCCESS;
                                        msg.obj = "1";
                                        payHandler.sendMessage(msg);

                                    } else {
                                        Message msg = new Message();
                                        msg.what = BALANCE_PASSWORD_ERROR;
                                        payHandler.sendMessage(msg);

                                    }
                                } else {
                                    Message msg = new Message();
                                    msg.what = BALANCE_FAIL;
                                    payHandler.sendMessage(msg);
                                }
                            }

                        });

    }

    protected void saveSessionDBForSessionId(String sessionId, String time,
                                             String text, boolean isGroup) {
        SessionDao sessionHelper = new SessionDao(this);
        List<ChatSession> sessionList = sessionHelper
                .getMessageBySession(sessionId, isGroup);
        ChatSession session = null;
        if (sessionList.size() > 0) {
            session = sessionList.get(0);
            session.setIsGroup(isGroup);
            int count = session.getUnReadCount();
            count++;
            session.setUnReadCount(count);
            session.setSessionId(sessionId);
            session.setTime(DateFormate.String2Date(time));
            session.setText(text);
            sessionHelper.update(session);
        } else {
            session = new ChatSession();
            session.setIsGroup(isGroup);
            session.setUnReadCount(1);
            session.setSessionId(sessionId);
            session.setTime(DateFormate.String2Date(time));
            session.setText(text);
            sessionHelper.add(session);
        }
    }

    private void sendBroad(String sessionId, boolean isGroup) {
        Intent intent = new Intent("com.longhuapuxin.updateSessiondb");
        intent.putExtra("sessionId", sessionId);
        intent.putExtra("isGroup", isGroup);
        sendBroadcast(intent);
    }

    private ChatMsg chatMessage2ChatMsg(ChatMessage chatMessage) {
        ChatMsg chatMsg = new ChatMsg();
        chatMsg.setSessionId(chatMessage.getSessionId());
        chatMsg.setText(chatMessage.getText());
        chatMsg.setOrderId(chatMessage.getOrderId());
        chatMsg.setTime(DateFormate.Date2String(chatMessage.getTime()));
        chatMsg.setReceiverStatus(chatMessage.getReceiverStatus());
        chatMsg.setSendStatus(chatMessage.getSendStatus());
        chatMsg.setType(chatMessage.getType());
        chatMsg.setIsGroup(chatMessage.isGroup());
        chatMsg.setSenderPortrait(chatMessage.getSenderPortrait());
        return chatMsg;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (payWay == "1") {
            balancePoint.setImageResource(R.drawable.home_content_pay_point_sel);
        } else if (payWay == "2") {
            alipayPoint.setImageResource(R.drawable.home_content_pay_point_sel);
        } else if (payWay == "3") {
            quickPoint.setImageResource(R.drawable.home_content_pay_point_sel);
        }
    }

    private void httpRequestOpenBouns(final RelativeLayout redBigBag, final TextView redMoney) {
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[3];
        params[0] = new OkHttpClientManager.Param("BounsId", bounsId);
        params[1] = new OkHttpClientManager.Param("UserId", Settings.instance()
                .getUserId());
        params[2] = new OkHttpClientManager.Param("Token", Settings.instance()
                .getToken());
        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/seller/openbouns", params,
                new OkHttpClientManager.ResultCallback<ResponseOpenBouns>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("onError"
                                + e.toString());
                    }

                    @Override
                    public void onResponse(ResponseOpenBouns response) {
                        Logger.info("onResponse"
                                + response);
                        if (response.isSuccess()) {
                            redBigBag.setVisibility(View.VISIBLE);
                            redMoney.setText(response.getAmount() + "");
                        } else {
                            Logger.info("httpRequestOpenBouns " + response.getErrorCode());
                            Logger.info("httpRequestOpenBouns " + response.getErrorMessage());

                        }
                    }

                });
    }

    private void httpRequestGetUnionPayTn(String id, String amount) {
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[4];
        params[0] = new OkHttpClientManager.Param("WalletOperationId", id);
        params[1] = new OkHttpClientManager.Param("Amount", amount);
        params[2] = new OkHttpClientManager.Param("Token", Settings.instance()
                .getToken());
        params[3] = new OkHttpClientManager.Param("UserId", Settings.instance()
                .getUserId());
        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/basic/getunionpaytn", params,
                new OkHttpClientManager.ResultCallback<ResponseGetUnionPayTN>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("onError"
                                + e.toString());
                    }

                    @Override
                    public void onResponse(ResponseGetUnionPayTN response) {
                        Logger.info("onResponse"
                                + response);
                        if (response.isSuccess()) {
                            myTN = response.getTN();
                            Message msg = new Message();
                            msg.what = UPPAY;
                            msg.obj = myTN;
                            payHandler.sendMessage(msg);
                        } else {
                            Logger.info("httpRequestGetUnionPayTn " + response.getErrorCode());
                            Logger.info("httpRequestGetUnionPayTn " + response.getErrorMessage());

                        }
                    }

                });
    }
}
