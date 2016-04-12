package com.longhuapuxin.common;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.longhuapuxin.u5.WelcomeActivity;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class LoginWXAPI {
	// APP_ID替换为你的应用从官方网站申请的合法appid
	private static final String APP_ID = "wx93b8e5d6f4efcbe0";
	private static final String APP_SECRET = "9c091acecd67083a7c0e8e2df7cda515";
	// IWXAPI是第三方app和微信通信的openapi接口
	private IWXAPI api;
	private Context context;
	private TextView loginErrorTips;

	public LoginWXAPI(Context context, TextView loginErrorTips) {
		this.context = context;
		this.loginErrorTips = loginErrorTips;
	}

	// 注册到微信，使你的程序启动后微信响应你的程序
	private void regToWx() {
		// 通过WXAPIFactory获取IWXAPI实例
		api = WXAPIFactory.createWXAPI(context, APP_ID, true);
		// 将应用的appid注册到微信
		api.registerApp(APP_ID);
	}

	public void sendRequestToWx() {
		// 初始化一个WXTextObject对象
		// WXTextObject textObject=new WXTextObject();
		// textObject.text=
		// 用WXTextObject对象初始化一个WXMediaMessage对象
		// 构造一个Req
		// 调用api接口发送数据到微信
		regToWx();
		if (!api.isWXAppInstalled()) {
			loginErrorTips.setVisibility(View.VISIBLE);
			loginErrorTips.setText("微信客户端未安装，请请确认");
			// Toast.makeText(context, "微信客户端未安装，请请确认", 1).show();
			return;
		}
		final SendAuth.Req req = new SendAuth.Req();
		req.scope = "snsapi_userinfo";
		req.state = "wechat_sdk_demo_test";
		api.sendReq(req);
	}
}
