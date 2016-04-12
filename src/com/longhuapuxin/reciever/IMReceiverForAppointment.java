package com.longhuapuxin.reciever;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.longhuapuxin.u5.AppointmentActivity;
import com.longhuapuxin.u5.MainActivity;
import com.longhuapuxin.u5.R;

public class IMReceiverForAppointment extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String content = intent.getStringExtra("content");
        notification(context, content);
    }


    private void notification(Context context, String content) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, AppointmentActivity.class), 0);
        // 下面需兼容Android 2.x版本是的处理方式  
        // Notification notify1 = new Notification(R.drawable.message,  
        // "TickerText:" + "您有新短消息，请注意查收！", System.currentTimeMillis());
        Notification notify1 = new Notification();
        notify1.icon = R.drawable.ic_launcher;
        notify1.tickerText = "U5:您有新短消息，请注意查收！";
        notify1.when = System.currentTimeMillis();
        notify1.setLatestEventInfo(context, "U5",
                content, pendingIntent);
        //这个通知代表事件的号码
        notify1.number = 2;
        notify1.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。  
        // 通过通知管理器来发起通知。如果id不同，则每click，在statu那里增加一个提示  
        manager.notify(2, notify1);


    }

}
