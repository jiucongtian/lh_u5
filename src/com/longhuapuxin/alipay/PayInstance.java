package com.longhuapuxin.alipay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.longhuapuxin.u5.Settings;

public class PayInstance {
	private Activity activity;
	private PayCallback callBack;
	public interface PayCallback{
		public void OnSuccess();
		public void OnFailed();
	}
	public PayInstance(Activity activity, Context context) {
		this.activity = activity;
		getWindowSize();
	}

	// 商户PID
	public static final String PARTNER = "2088021655788101";
	// 商户收款账号
	public static final String SELLER = "longhuapuxin@163.com";
	// 商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBANhTtM6pFwcr2i9Mb0HH/oNOOhivhC0/qMjeSRJ59rLdNc0QaNSUeK9Cq+Hm0J06LtrZLVdzyKm6l7wVG2YlNr+/4PnHcb3WhcEFx78t+2luR9kYhq8BmmDIWa47JMrGBM03W5km9iTrxyB2itMBwxX+AW+xdESxt9EtRk/PHpCvAgMBAAECgYBBP2YemyOl9xUVBRHdnmvmwNaHEZFlcHkBNQKHPyAorM4IbvWfgLSx8AMV3N95PM5bFfw2D2crwmr3wMoF0h2jxCt912trqG5J1mUUDzpCfxELYygSkAT3TlHMaNFoyUyZxqrfbEc1m59skN9H7Evg8gFRURmlqvkdKJxrYY4BgQJBAPYEpEWRZPaS4LcwbqR07ln+S6EYc0Pe05szZ1Dy+t+RmfvCPmQhP+i6JQgoGT1lM9uAmy1jshvCEvVOTDQIhMECQQDhGqqb05bspCuU47gJYpxEo44pzA2b4pgaa9Ok3vHPMrrMk2IM6le7o5/TZ4vIseR+S7BCzkRhXobiEjNruEFvAkEAg2HRNJHEAGZJ+aq0u8DydT73tq1vCQTbrtuRxkosre589FU0qpaTIb8e/a8kY4RDYGra9C90s5w+MaDB01vlQQJBAJZxVATA7OVK8zWW27CqDvZwuNqGXbIJRs6hsdlGhyWLKfz/o1AubmQhfvezBGElQyiFPU/ouxq4Kj19HJCpnFECQQCcgsavHh6hapDluwUNf+WJSk284VPUizUSQs8U/VmdNJBgQy6QzcnOPWoWS7u0/bSrmeoLTFLrXSj13h/+LX6p";
	// 支付宝公钥
	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
 
	private void getWindowSize() {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
	}

	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 
	 */
	public void pay(View v, String shopName, String shopAmount , String paymentNo ,PayCallback callback ) {
		this.callBack=callback;
		// 订单
		String orderInfo = getOrderInfo("线下消费", "在" + shopName + "消费",shopAmount,paymentNo);

		// 对订单做RSA 签名
		String sign = sign(orderInfo);
		try {
			// 仅需对sign 做URL编码
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// 完整的符合支付宝参数规范的订单信息
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
				+ getSignType();

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(activity);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo);

				 
				PayResult payResult = new PayResult(result);

				 

				String resultStatus = payResult.getResultStatus();

				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					// Toast.makeText(activity, "支付成功",
					// Toast.LENGTH_SHORT).show();
					// stopLoad();
					// dialog.dismiss();
					callBack.OnSuccess();
					 
				} else {
					callBack.OnFailed();
				}
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}
	public void Purchase(View v,String orderTitle,  String amount , String paymentNo ,PayCallback callback ) {
		this.callBack=callback;
		// 订单
		String orderInfo = getOrderInfo("购买U豆", orderTitle,amount,paymentNo);

		// 对订单做RSA 签名
		String sign = sign(orderInfo);
		try {
			// 仅需对sign 做URL编码
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// 完整的符合支付宝参数规范的订单信息
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
				+ getSignType();

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(activity);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo);

				 
				PayResult payResult = new PayResult(result);

				 

				String resultStatus = payResult.getResultStatus();

				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					// Toast.makeText(activity, "支付成功",
					// Toast.LENGTH_SHORT).show();
					// stopLoad();
					// dialog.dismiss();
					callBack.OnSuccess();
					 
				} else {
					callBack.OnFailed();
				}
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}
	/**
	 * check whether the device has authentication alipay account.
	 * 查询终端设备是否存在支付宝认证账户
	 * 
	 */
	public void check(View v) {
		Runnable checkRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask payTask = new PayTask(activity);
				// 调用查询接口，获取查询结果
				boolean isExist = payTask.checkAccountIfExist();
				if(!isExist){
				Toast.makeText(activity, "您的手机上没有支付宝，请选择其他支付方式", Toast.LENGTH_SHORT)
				.show();
				}
			}
		};

		Thread checkThread = new Thread(checkRunnable);
		checkThread.start();

	}

	/**
	 * get the sdk version. 获取SDK版本号
	 * 
	 */
	// public void getSDKVersion() {
	// PayTask payTask = new PayTask(this);
	// String version = payTask.getVersion();
	// Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
	// }

	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	public String getOrderInfo(String subject, String body, String price, String payno) {

		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + payno + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		Settings.instance();
		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\""
				+ Settings.getBankServerUrl() + "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	 
	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}

  
 
}
