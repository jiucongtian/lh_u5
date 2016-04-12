package com.longhuapuxin.reciever;

import com.longhuapuxin.u5.MainActivity;
import com.longhuapuxin.u5.R;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

public class IMReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String sessionId = intent.getStringExtra("sessionId");
		
//		Utils.isActivityRunning(context, "")
		if(!isRunningForeground(context)) {
			notification(context);
		}
	}
	
	
	private boolean isRunningForeground (Context context)  
	{  
	    ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);  
	    ComponentName cn = am.getRunningTasks(1).get(0).topActivity;  
	    String currentPackageName = cn.getPackageName();  
	    if(!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals("com.longhuapuxin.u5"))  
	    {  
	        return true ;  
	    }  
	   
	    return false ;  
	} 
	
	private void notification(Context context) {
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE); 
        
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,  
                new Intent(context, MainActivity.class), 0);  
        // 下面需兼容Android 2.x版本是的处理方式  
        // Notification notify1 = new Notification(R.drawable.message,  
        // "TickerText:" + "您有新短消息，请注意查收！", System.currentTimeMillis());  
        Notification notify1 = new Notification();  
        notify1.icon = R.drawable.ic_launcher;  
        notify1.tickerText = "U5:您有新短消息，请注意查收！";  
        notify1.when = System.currentTimeMillis();
		notify1.defaults = Notification.DEFAULT_SOUND;
        notify1.setLatestEventInfo(context, "U5",  
                "您有新消息，请点击查看", pendingIntent);  
        notify1.number = 1;  
        notify1.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。  
        // 通过通知管理器来发起通知。如果id不同，则每click，在statu那里增加一个提示  
        manager.notify(1, notify1);  
        
        
        
	}

}
