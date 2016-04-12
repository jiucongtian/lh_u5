package com.longhuapuxin.u5;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.longhuapuxin.alipay.PayInstance;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.entity.ResponseCheckPurchase;
import com.longhuapuxin.entity.ResponseDad;
import com.longhuapuxin.entity.ResponseNewPurchase;
import com.squareup.okhttp.Request;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class BuyBeanActivity extends BaseActivity {

	private ArrayList<Integer> quantities;
	private ArrayList<Integer> presents;
	private ArrayList<RadioButton> radios;
	private TextView txtTotal;
	private int quantity = 50;
	private int present = 5;
	private Button btnPay;
	private String PO;
	private static final int MSG_PAYBYALI = 1;
	private static final int MSG_ALISUCCESS = 2;
	private static final int MSG_ALIFAILED = 3;
	private static final int MSG_CheckResult = 4;
	private static final int MSG_CheckResultSuccess = 5;
	private static final int MSG_CheckResultFailed = 6;
	private static final int MSG_ConfirmSuccess = 7;
 
	
	private static final int MSG_HttpError = -1;
	private static final int ACT_Exit = 11;
	private static final int ACT_Login = 12;
	private AlertDialog dialog;
	private int windowWidth, windowHeight;
	private Timer timer = new Timer();
	@SuppressLint("HandlerLeak") 
	private Handler handler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_PAYBYALI:
				PaybyAli();
				break;

			case ACT_Exit:

				if (dialog != null) {
					dialog.dismiss();
				}
				BuyBeanActivity.this.finish();
				break;
			case MSG_HttpError:

				HandleHttpError(msg.arg1, (String) msg.obj);
				break;
			case ACT_Login:

				if (dialog != null) {
					dialog.dismiss();
				}
				Intent intent = new Intent(BuyBeanActivity.this,
						WelcomeActivity.class);
				intent.putExtra("NeedReturn", true);
				startActivity(intent);
				break;
			case MSG_ALISUCCESS:
				httpRequestConfirmPayment((String)msg.obj);
				break;
			case MSG_ConfirmSuccess:
          	  showDialogPaySuccess();
          	  break;
			case MSG_ALIFAILED:
				showDialogPayFail((String)msg.obj);
				 break;
			case MSG_CheckResult:
				CheckPurchase();
				break;
			case MSG_CheckResultFailed:
				showDialogPayFail((String)msg.obj);
				break;
			case MSG_CheckResultSuccess:
				showDialogPaySuccess();
				break;
			}
		}
	};
	private void showDialogPayFail(String errorMsg) {
		if (dialog != null) {
			dialog.dismiss();
		}
		dialog = new Builder(this).create();
		// 这里要先调用show方法，在设置视图
		dialog.show();
		View view = LayoutInflater.from(this).inflate(
				R.layout.show_dialog_pay_fail, null);
		TextView txtMsg=(TextView) view.findViewById(R.id.txtMsg);
		if(errorMsg!=null && errorMsg.length()>0){
			txtMsg.setText(errorMsg);
		}
		TextView giveUpTransction = (TextView) view
				.findViewById(R.id.give_up_transction);
		giveUpTransction.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				handler.sendEmptyMessageDelayed(ACT_Exit,10);
			}
		});
		TextView checkResult = (TextView) view.findViewById(R.id.check_result);
		checkResult.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Message msg=new Message();
				msg.what=MSG_CheckResult;
				handler.sendMessage(msg);
				
			}
		});
		dialog.setContentView(view);
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = windowWidth / 3 * 2;
		params.height = windowHeight / 12 * 5;
		dialog.getWindow().setAttributes(params);
	}
	private void showDialogPaySuccess() {
		if (dialog != null) {
			dialog.dismiss();
		}
		double balance=Double.parseDouble( Settings.instance().User.getBalance());
		Settings.instance().User.setBalance(String.valueOf( balance+quantity+present));
		dialog = new Builder(this).create();
		// 这里要先调用show方法，在设置视图
		dialog.show();
		View view = LayoutInflater.from(this).inflate(
				R.layout.show_dialog_pay_success, null);
		View successAffirm = (View) view
				.findViewById(R.id.success_affirm);
		successAffirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				handler.sendEmptyMessageDelayed(ACT_Exit,10);
			}
		});
		dialog.setContentView(view);
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = windowWidth / 3 * 2;
		params.height = windowHeight / 12 * 5;
		dialog.getWindow().setAttributes(params);
	}

	private void HandleHttpError(int errCode, String errMsg) {
		if (errCode == 401) {
			ShowResignDialog();
		} else {
			Toast.makeText(BuyBeanActivity.this, errMsg, Toast.LENGTH_SHORT)
					.show();
		}
	}

	private void ShowResignDialog() {

		AlertDialog.Builder builder = new Builder(BuyBeanActivity.this);

		builder.setMessage("请重新登录后进行支付，或者取消本次支付。");
		builder.setTitle("用户身份验证失败");
		builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				handler.sendEmptyMessageDelayed(ACT_Login, 10);
			}

		});
		builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {

				handler.sendEmptyMessageDelayed(ACT_Exit, 10);
			}

		});
		dialog = builder.create();
		dialog.show();
	}

	private void PaybyAli() {
		//String.valueOf(quantity * 1)
		String title="购买" + String.valueOf(quantity)+"个U豆";
		if(present>0){
			title+="(赠送"+String.valueOf(present)+"个)";
		}
		new PayInstance(BuyBeanActivity.this, getApplicationContext()).Purchase(
				btnPay, title, (String.valueOf(quantity)), PO,
				aliPayCallBack);
	}

	private PayInstance.PayCallback aliPayCallBack = new PayInstance.PayCallback() {

		@Override
		public void OnSuccess() {
			Message msg = new Message();
			msg.what = MSG_ALISUCCESS;
			msg.obj=PO;
			handler.sendMessageDelayed(msg, 10);
		}

		@Override
		public void OnFailed() {
			Message msg = new Message();
			msg.what = MSG_ALIFAILED;
			msg.obj = "";
			handler.sendMessageDelayed(msg, 10);
		}

	};
	private void CheckPurchase() {
		showDialogPayLoad();
		Param[] params = new Param[3];
		params[0] = new Param("PurchaseId", PO);
		params[1] = new Param("UserId", Settings.instance().getUserId());
		params[2] = new Param("Token", Settings.instance().getToken());

		OkHttpClientManager
				.postAsyn(
						Settings.instance().getApiUrl() + "/bean/checkpurchase",
						params,
						new OkHttpClientManager.ResultCallback<ResponseCheckPurchase>() {

							@Override
							public void onError(Request request, Exception e) {
								dialog.dismiss();
								Toast.makeText(
										BuyBeanActivity.this,
										e.getMessage(),
										Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onResponse(
									ResponseCheckPurchase response) {
								try {
									dialog.dismiss();

									// Log.d("", "--------" +
									// responseLoginByPhone);
									if (response.isSuccess()) {
										if (response.isPurchaseConfirmed()) {
											Message msg = new Message();
											msg.what = MSG_CheckResultSuccess;
											handler.sendMessageDelayed(msg, 10);
										} else {
											Message msg = new Message();
											msg.what = MSG_CheckResultFailed;
											msg.obj = response
													.getErrorMessage();
											handler.sendMessageDelayed(msg, 10);
										}

									} else {
										if (response.getErrorCode() == "401") {
											Message msg = new Message();
											msg.what = MSG_HttpError;
											msg.arg1 = Integer
													.parseInt(response
															.getErrorCode());
											msg.obj = response
													.getErrorMessage();
											handler.sendMessageDelayed(msg, 200);
										} else {
											Toast.makeText(
													BuyBeanActivity.this,
													response.getErrorMessage(),
													Toast.LENGTH_SHORT).show();
										}
//										Log.d("",
//												"------"
//														+ response
//																.getErrorMessage());
										Logger.info("------"
												+ response
												.getErrorMessage());

									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

						});

	}
	private void httpRequestConfirmPayment(String ticket) {
		showDialogPayLoad();
		Param[] params = new Param[5];
		params[0] = new Param("PaymentCode", PO);
		params[1] = new Param("UserId", Settings.instance().getUserId());
		params[2] = new Param("Token", Settings.instance().getToken());
		params[3] = new Param("PurchaseId", PO);
		params[4] = new Param("Tikert", ticket);
		OkHttpClientManager
				.postAsyn(
						Settings.instance().getApiUrl() + "/bean/confirmpurchase",
						params,
						new OkHttpClientManager.ResultCallback<ResponseDad>() {

							@Override
							public void onError(Request request, Exception e) {
								dialog.dismiss();
								Toast.makeText(
										BuyBeanActivity.this,
										e.getMessage(),
										Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onResponse(
									ResponseDad response) {
								try {
									dialog.dismiss();
									Message msg = new Message();
									msg.what = MSG_ConfirmSuccess;
									handler.sendMessageDelayed(msg, 10);
									 
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

						});

	}

	private List<ImageView> loadImgList;
	private ImageView load0, load1, load2, load3, load4, load5, load6;
	private int i = 0;

	private void showDialogPayLoad() {
		if (dialog != null) {
			dialog.dismiss();
		}
		dialog = new Builder(this).create();
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

	private void getWindowSize() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		windowWidth = dm.widthPixels;
		windowHeight = dm.heightPixels;
	}
	private TimerTask task;
	
	public void startLoad() {
		if (task == null) {
			task = new TimerTask() {

				@Override
				public void run() {
					i++;
					for (final ImageView imageView : loadImgList) {
						if (loadImgList.get(i % 7) == imageView) {
							if (imageView != load6) {
								runOnUiThread(new Runnable() {
									public void run() {
										imageView
												.setImageResource(R.drawable.shop_content_loading_box_sel);
									}
								});

							} else {
								runOnUiThread(new Runnable() {
									public void run() {
										imageView
												.setImageResource(R.drawable.shop_content_loading_grass_sel);
									}
								});
							}
						} else {
							if (imageView != load6) {
								runOnUiThread(new Runnable() {
									public void run() {
										imageView
												.setImageResource(R.drawable.shop_content_loading_box);
									}
								});

							} else {
								runOnUiThread(new Runnable() {
									public void run() {
										imageView
												.setImageResource(R.drawable.shop_content_loading_grass);
									}
								});
							}
						}
					}
				}
			};
			timer.schedule(task, 100, 100);
		}
	}

	public void stopLoad() {
		if (task != null) {
			task.cancel();
			task = null;
		}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buybean);
		init();
		getWindowSize();
	}

	OnClickListener radioOnClick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			int idx = Integer.parseInt(arg0.getTag().toString());
			for (int i = 0; i < radios.size(); i++) {
				if (idx == i) {
					radios.get(i).setChecked(true);
					txtTotal.setText("￥" + quantities.get(i).toString());
					quantity = quantities.get(i);
					present = presents.get(i);
				} else {
					radios.get(i).setChecked(false);
				}
			}
		}
	};

	private void init() {

		initHeader(R.string.BuyBean);
		txtTotal = (TextView) findViewById(R.id.txtTotal);
		btnPay = (Button) findViewById(R.id.btnPay);
		quantities = new ArrayList<Integer>();
		presents = new ArrayList<Integer>();
		radios = new ArrayList<RadioButton>();
		RadioButton r = (RadioButton) findViewById(R.id.r1);
		r.setTag(0);
		r.setOnClickListener(radioOnClick);
		radios.add(r);
		r = (RadioButton) findViewById(R.id.r2);
		r.setTag(1);
		r.setOnClickListener(radioOnClick);
		radios.add(r);
		r = (RadioButton) findViewById(R.id.r3);
		r.setTag(2);
		r.setOnClickListener(radioOnClick);
		radios.add(r);
		r = (RadioButton) findViewById(R.id.r4);
		r.setTag(3);
		r.setOnClickListener(radioOnClick);
		radios.add(r);
		r = (RadioButton) findViewById(R.id.r5);
		r.setTag(4);
		r.setOnClickListener(radioOnClick);
		radios.add(r);

		quantities.add(1);
		quantities.add(50);
		quantities.add(100);
		quantities.add(200);
		quantities.add(500);

		presents.add(0);
		presents.add(5);
		presents.add(15);
		presents.add(40);
		presents.add(100);

		btnPay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				GetPurchaseOrder();
			}

		});
	}

	private void GetPurchaseOrder() {
		Param[] params = new Param[4];
		final Settings setting = Settings.instance();
		params[0] = new Param("UserId", setting.getUserId());
		params[1] = new Param("Token", setting.getToken());
		params[2] = new Param("Quantity", String.valueOf(quantity));
		params[3] = new Param("Present", String.valueOf(present));
		OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
				+ "/bean/newpurchase", params,
				new OkHttpClientManager.ResultCallback<ResponseNewPurchase>() {

					@Override
					public void onError(Request request, Exception e) {

					}

					@Override
					public void onResponse(ResponseNewPurchase response) {

						if (response.isSuccess()) {
							PO = response.getPurchaseCode();
							handler.sendEmptyMessage(MSG_PAYBYALI);
						} else {
							Toast.makeText(BuyBeanActivity.this,
									response.getErrorMessage(),
									Toast.LENGTH_SHORT).show();
						}
					}

				});
	}
}
