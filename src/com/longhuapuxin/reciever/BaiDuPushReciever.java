package com.longhuapuxin.reciever;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.baidu.android.pushservice.PushMessageReceiver;
import com.longhuapuxin.entity.PushResponseDad;
import com.longhuapuxin.entity.PushResponseNewOrder;
import com.longhuapuxin.entity.PushResponsePayContent;
import com.longhuapuxin.entity.PushResponseTalkTo;
import com.longhuapuxin.service.IMService;
import com.longhuapuxin.u5.GoPayActivity;
import com.longhuapuxin.u5.MainActivity;
import com.longhuapuxin.u5.U5Application;

public class BaiDuPushReciever extends PushMessageReceiver {

	@Override
	public void onBind(Context context, int errorCode, String appid,
			String userId, String channelId, String requestId) {
		Log.d("", "-------errorCode" + errorCode);
		if (errorCode == 0) {
			Log.d("", "---------bindin");
			// ((U5Application) context.getApplicationContext())
			// .setChannelId(channelId);
			// ((U5Application)
			// context.getApplicationContext()).setUserId(userId);
			updateContentForChannelIdAndUseId(context, channelId, userId);
		}
	}

	private void updateContentForChannelIdAndUseId(Context context,
			String channelId, String userId) {
		Intent intent = new Intent();
		intent.setClass(context.getApplicationContext(), MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("msgChannelId", channelId);
		intent.putExtra("msgUserId", userId);
		context.startActivity(intent);
	}

	@Override
	public void onDelTags(Context arg0, int arg1, List<String> arg2,
			List<String> arg3, String arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onListTags(Context arg0, int arg1, List<String> arg2,
			String arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMessage(Context context, String json, String arg2) {
		// 接受扫二维码后得到的支付信息
		// Intent intent = new Intent();
		// intent.setClass(context.getApplicationContext(),
		// GoPayActivity.class);
		// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// context.getApplicationContext().startActivity(intent);
		Log.d("", "-----------reciever" + json);
		PushResponseDad pushResponseDad = ((U5Application) context
				.getApplicationContext()).getGson().fromJson(json,
				PushResponseDad.class);
		if (pushResponseDad.getMessageCode().equals("301")) {
			PushResponsePayContent pushResponsePayContent = ((U5Application) context
					.getApplicationContext()).getGson().fromJson(
					pushResponseDad.getMessageBody(),
					PushResponsePayContent.class);
			Intent intent = new Intent(context.getApplicationContext(),
					GoPayActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("pushResponsePayContent", pushResponsePayContent);
			context.startActivity(intent);
		} else if (pushResponseDad.getMessageCode().equals("101")) {
			PushResponseTalkTo pushResponseTalkTo = ((U5Application) context
					.getApplicationContext()).getGson().fromJson(
					pushResponseDad.getMessageBody(), PushResponseTalkTo.class);
			Intent intent = new Intent(context.getApplicationContext(),
					IMService.class);
			intent.putExtra("sessionId", pushResponseTalkTo.getSessionId());
			intent.putExtra("sendContent", pushResponseTalkTo.getMessageText());
			intent.putExtra("sendTime", pushResponseTalkTo.getMessageSendTime());
			intent.putExtra("sendUserId", pushResponseTalkTo.getMessageUserId());
			intent.putExtra("isTransaction", false);
			context.startService(intent);
		} else if (pushResponseDad.getMessageCode().equals("201")) {
			PushResponseNewOrder pushResponseNewOrder = ((U5Application) context
					.getApplicationContext()).getGson().fromJson(
					pushResponseDad.getMessageBody(),
					PushResponseNewOrder.class);
			Intent intent = new Intent(context.getApplicationContext(),
					IMService.class);
			intent.putExtra("sessionId", pushResponseNewOrder.getSessionId());
			intent.putExtra("orderId", pushResponseNewOrder.getId());
			intent.putExtra("sendUserId", pushResponseNewOrder.getUserId2());
			intent.putExtra("isTransaction", true);
			context.startService(intent);
		} else if (pushResponseDad.getMessageCode().equals("202")) {
			PushResponseNewOrder pushResponseNewOrder = ((U5Application) context
					.getApplicationContext()).getGson().fromJson(
					pushResponseDad.getMessageBody(),
					PushResponseNewOrder.class);
			Intent intent = new Intent("com.longhuaouxin.transcationreceiver");
			intent.putExtra("orderId", pushResponseNewOrder.getId());
			intent.putExtra("isAccept", true);
			context.sendBroadcast(intent);
		} else if (pushResponseDad.getMessageCode().equals("203")) {
			PushResponseNewOrder pushResponseNewOrder = ((U5Application) context
					.getApplicationContext()).getGson().fromJson(
					pushResponseDad.getMessageBody(),
					PushResponseNewOrder.class);
			Intent intent = new Intent("com.longhuaouxin.transcationreceiver");
			intent.putExtra("orderId", pushResponseNewOrder.getId());
			intent.putExtra("isAccept", false);
			context.sendBroadcast(intent);
		}
		// 接受聊天
	}

	@Override
	public void onNotificationArrived(Context arg0, String arg1, String arg2,
			String arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNotificationClicked(Context arg0, String arg1, String arg2,
			String arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSetTags(Context arg0, int arg1, List<String> arg2,
			List<String> arg3, String arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnbind(Context arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub

	}

}
