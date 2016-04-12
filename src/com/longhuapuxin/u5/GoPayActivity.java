package com.longhuapuxin.u5;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.longhuapuxin.common.HttpRequest;
import com.longhuapuxin.common.HttpRequest.HttpRequestListener;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.entity.PushResponsePayContent;
import com.longhuapuxin.entity.ResponseGetAccount;
import com.longhuapuxin.service.IMService;
import com.longhuapuxin.service.IMService.IMPayCallBack;
import com.longhuapuxin.uppay.UpPayBaseActivity;
import com.longhuapuxin.view.BottomSlideView;
import com.longhuapuxin.zxing.EncodingHandler;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class GoPayActivity extends BaseActivity implements OnClickListener {
    private static final int MSG_NewPayment = 1;
    private static final int MSG_Connected = 2;

    private static final int BEAN_GOT = 0;
    private static final int ACT_ReConnect = 10;
    private static final int ACT_Exit = 11;
    private static final int ACT_Login = 12;
    private static final int MSG_HttpError = -1;
    public static final int RESULT_PAY = 1000;
    public static final int REQUEST_PAY = 2000;
    private ImageView scanCodeImg;
    private BottomSlideView payUI;
    private int payUIHeight;
    private String userId;
    private String discountTicketId;
    private char totalLength;
    private char vertificationCode;
    private String realCode;
    private char[] discountTicketIdArray;
    private char[] userIdArray;
    private PushResponsePayContent pushResponsePayContent;
    private TextView txtNetwork;
    private LinearLayout waitPay;
    private IMService myService;
    private ServiceConnection sc;
    private TextView consume, discount, shouldConsume, balance;
    private Button goPay;
    @SuppressLint("HandlerLeak")
    private Handler imHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_NewPayment:
                    consume.setText(pushResponsePayContent.getTotal() + "元");
                    discount.setText(pushResponsePayContent.getDiscount() + "折");
                    shouldConsume.setText("￥" + pushResponsePayContent.getAmount());
                    httpRequestGetAccount();
                    waitPay.setVisibility(View.GONE);
                    payUI.setVisibility(View.VISIBLE);
                    payUI.smoothScrollTo(0, 0, 500);
                    break;
                case MSG_Connected:
                    txtNetwork.setVisibility(View.GONE);
                    getScanCode(realCode);
                    break;
                case BEAN_GOT:
                    balance.setText(msg.obj.toString() + "元");
                    waitPay.setVisibility(View.GONE);
                    payUI.setVisibility(View.VISIBLE);
                    payUI.smoothScrollTo(0, 0, 500);
                    break;

                case ACT_ReConnect:
                    txtNetwork.setVisibility(View.VISIBLE);
                    txtNetwork.setText("与服务器的网络中断，正在重新连接");

                    break;
                case ACT_Exit:
                    myService.StopPayIM();

                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    GoPayActivity.this.finish();
                    break;
                case MSG_HttpError:

                    HandleHttpError(msg.arg1, (String) msg.obj);
                    break;
                case ACT_Login:

                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Intent intent = new Intent(GoPayActivity.this,
                            WelcomeActivity.class);
                    intent.putExtra("NeedReturn", true);
                    startActivity(intent);
                    break;
            }

            super.handleMessage(msg);
        }
    };


    private IMPayCallBack payCallBack = new IMPayCallBack() {

        @Override
        public void OnNewPay(PushResponsePayContent pushResponsePay) {
            Message msg = new Message();
            pushResponsePayContent = pushResponsePay;
            msg.what = MSG_NewPayment;
            imHandler.sendMessage(msg);
        }

        @Override
        public void OnNetWorkBreak(String errMessage) {
            imHandler.sendEmptyMessageDelayed(ACT_ReConnect, 10);

        }

        @Override
        public void OnConnected() {
            Message msg = new Message();
            msg.what = MSG_Connected;
            imHandler.sendMessage(msg);
        }

    };

    private int windowWidth, windowHeight;
    private AlertDialog dialog;

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(GoPayActivity.this, IMService.class);
        if (sc == null) {
            // getScanCode(realCode);
            sc = new ServiceConnection() {
                /*
                 * 只有在MyService中的onBind方法中返回一个IBinder实例才会在Bind的时候
                 * 调用onServiceConnection回调方法
                 * 第二个参数service就是MyService中onBind方法return的那个IBinder实例
                 * ，可以利用这个来传递数据
                 */
                @Override
                public void onServiceConnected(ComponentName name,
                                               IBinder service) {
                    myService = ((IMService.LocalBinder) service).getService();
                    myService.StartPayIM(payCallBack);
                    if (myService.IsConnected()) {
                        Message msg = new Message();
                        msg.what = MSG_Connected;
                        imHandler.sendMessage(msg);
                    } else {
                        myService.ReConnect(payCallBack);
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    /*
                     * SDK上是这么说的： This is called when the connection with the
					 * service has been unexpectedly disconnected that is, its
					 * process crashed. Because it is running in our same
					 * process, we should never see this happen.
					 * 所以说，只有在service因异常而断开连接的时候，这个方法才会用到
					 */

                    sc = null;
//					Log.i("TAG",
//							"onServiceDisconnected : ServiceConnection --->"
//									+ sc);
                    Logger.info("onServiceDisconnected : ServiceConnection --->"
                            + sc);
                }

            };
            bindService(intent, sc, Context.BIND_AUTO_CREATE);
        }

    }

    @Override
    protected void onStop() {

        super.onDestroy();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myService.StopPayIM();
        unbindService(sc);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_pay);
        initHeader(R.string.Payment);
        getWindowSize();
        initViews();
        initCode();

    }

    private void getWindowSize() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        windowWidth = dm.widthPixels;
        windowHeight = dm.heightPixels;
    }

    private void initCode() {
        userId = Settings.instance().getUserId();
        Intent intent = getIntent();
        if (intent.getStringExtra("discountTicketId") != null) {
            discountTicketId = intent.getStringExtra("discountTicketId");
        } else {
            discountTicketId = "0000000000000000000000";
        }
        Logger.info("--------discountTicketId:" + discountTicketId);
        Logger.info("--------userId:" + userId);
//		Log.d("", "--------discountTicketId:" + discountTicketId);
//		Log.d("", "--------userId:" + userId);
        codeArithmetic();
    }

    private void initViews() {
        scanCodeImg = (ImageView) findViewById(R.id.scan_code_img);
        payUI = (BottomSlideView) findViewById(R.id.pay_ui);
        txtNetwork = (TextView) findViewById(R.id.txtNetwork);
        waitPay = (LinearLayout) findViewById(R.id.waitpay);
        consume = (TextView) findViewById(R.id.consume);
        discount = (TextView) findViewById(R.id.discount);
        shouldConsume = (TextView) findViewById(R.id.should_consume);
        balance = (TextView) findViewById(R.id.amount);
        goPay = (Button) findViewById(R.id.go_pay);
        goPay.setOnClickListener(this);
    }

    private void codeArithmetic() {
        int length = discountTicketId.length() + userId.length() + 33 + 2;
        totalLength = (char) length;
//		Log.d("", "----totalLength" + totalLength);
        Logger.info("----totalLength" + totalLength);
        int totalAsc = 0;
        discountTicketIdArray = discountTicketId.toCharArray();
        userIdArray = userId.toCharArray();
        for (char i : discountTicketIdArray) {
            int asc = (int) i;
            totalAsc += asc;
        }
        for (char i : userIdArray) {
            int asc = (int) i;
            totalAsc += asc;
        }
        vertificationCode = (char) ((totalAsc + length) % 57 + 33);
        Log.d("", "-------vertificationCode  " + vertificationCode);
        Logger.info("----totalLength" + totalLength);
        realCode = "" + totalLength + discountTicketId + userId
                + vertificationCode;
//		Log.d("", "-------realCode  " + realCode);
        Logger.info("-------realCode  " + realCode);
    }

    private void getScanCode(String code) {
        try {
            if (!code.equals("")) {
                // 根据字符串生成二维码图片并显示在界面上，第二个参数为图片的大小（350*350）
                Bitmap qrCodeBitmap;
                qrCodeBitmap = EncodingHandler.createQRCode(code, 350);
                scanCodeImg.setImageBitmap(qrCodeBitmap);
            } else {
                Toast.makeText(GoPayActivity.this, "Text can not be empty",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onClick(View v) {
        if (v == goPay) {
            Intent intent = new Intent(GoPayActivity.this, UpPayBaseActivity.class);
            intent.putExtra("money", pushResponsePayContent.getAmount());
            intent.putExtra("orderType", "2");
            intent.putExtra("orderId", pushResponsePayContent.getCode());
            startActivityForResult(intent, REQUEST_PAY);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (pushResponsePayContent == null) {
                payUIHeight = payUI.getHeight();
                payUI.scrollTo(0, -payUIHeight);
            }

        }
    }

    private void httpRequestGetAccount() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("UserId", Settings.instance()
                .getUserId()));
        params.add(new BasicNameValuePair("Token", Settings.instance()
                .getToken()));
        params.add(new BasicNameValuePair("TargetUserId", Settings.instance()
                .getUserId()));
        new HttpRequest(new HttpRequestListener() {

            @Override
            public void onHttpRequestOK(int tag, byte[] data) {
                try {
                    String json = new String(data, "utf-8");
                    ResponseGetAccount responseGetAccount = ((U5Application) getApplication())
                            .getGson().fromJson(json, ResponseGetAccount.class);
//                    Log.d("", "--------" + json);
                    Logger.info("--------" + json);
                    if (responseGetAccount.isSuccess()) {
                        Message msg = new Message();
                        msg.what = BEAN_GOT;
                        Settings.instance().setManualyWithDraw(responseGetAccount.isManualyWithDraw());
                        msg.obj = responseGetAccount.getUser().getBalance();
                        imHandler.sendMessageDelayed(msg, 10);
                    } else {
                        Message msg = new Message();
                        msg.what = MSG_HttpError;
                        msg.arg1 = Integer.parseInt(responseGetAccount
                                .getErrorCode());
                        msg.obj = responseGetAccount.getErrorMessage();
                        imHandler.sendMessageDelayed(msg, 200);
//                        Log.d("",
//                                "------GetAccount"
//                                        + responseGetAccount.getErrorMessage());
//                        Log.d("",
//                                "------GetAccount"
//                                        + responseGetAccount.getErrorCode());
                        Logger.info("------GetAccount"
                                + responseGetAccount.getErrorMessage());
                        Logger.info("------GetAccount"
                                + responseGetAccount.getErrorCode());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onHttpRequestFailed(int tag) {
            }
        }).post(0, Settings.instance().getApiUrl() + "/auth/getaccount", params);

    }


    private void HandleHttpError(int errCode, String errMsg) {
        if (errCode == 401) {
            ShowResignDialog();
        } else {
            Toast.makeText(GoPayActivity.this, errMsg, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void ShowResignDialog() {

        AlertDialog.Builder builder = new Builder(GoPayActivity.this);

        builder.setMessage("请重新登录后进行支付，或者取消本次支付。");
        builder.setTitle("用户身份验证失败");
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                imHandler.sendEmptyMessageDelayed(ACT_Login, 10);
            }

        });
        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

                imHandler.sendEmptyMessageDelayed(ACT_Exit, 10);
            }

        });
        dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PAY || resultCode == RESULT_PAY) {
            GoPayActivity.this.finish();
        }
    }
}
