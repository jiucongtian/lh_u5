package com.longhuapuxin.u5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
//import com.longhuapuxin.entity.ResponseMarkedLabels.Label;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Application;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.google.code.microlog4android.config.PropertyConfigurator;
import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.longhuapuxin.common.ImageUrlLoader;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.dao.SessionDao;
import com.longhuapuxin.db.bean.ChatSession;
import com.longhuapuxin.entity.Expression;
import com.longhuapuxin.entity.ResponseGetCircle;
import com.longhuapuxin.entity.ResponseGetSession;
import com.squareup.okhttp.Request;

//import com.longhuapuxin.entity.ResponseMarkedLabels.Label;

public class U5Application extends Application {

    public final static Integer MODIFY_LABEL = 0x01;
    public final static Integer USER_DETAIL = 0x02;
    private BitmapUtils bitmapUtils;
    private Gson gson;
    private List<Activity> activityList = new LinkedList();
    private Map<Integer, Object> params;
    private ArrayList<TextView> nickNames;
    private ArrayList<ImageView> portraits;

    private static final int MSG_CHANGENICK = 1;
    private static final int MSG_CHANGEPORTRAIT = 2;
    public boolean IsTakingPhoto, mCheckedUpgrade = false;
    public LocationClient mLocationClient;
    public MyLocationListener mMyLocationListener;
    public TextView trigger, exit;
    public Vibrator mVibrator;
    public Boolean mNetworkOk = true;

    private List<Expression.ExpressionNode> expressionList;

    public boolean ismCheckedUpgrade() {
        return mCheckedUpgrade;
    }

    public void setmCheckedUpgrade(boolean mCheckedUpgrade) {
        this.mCheckedUpgrade = mCheckedUpgrade;
    }

    public Object getParam(Integer key) {
        return params.get(key);
    }

    public void putParam(Integer key, Object param) {
        params.put(key, param);
    }

    private void initGson() {
        gson = new Gson();
    }

    public Gson getGson() {
        return gson;
    }

    public BitmapUtils getBitmapUtils() {
        return bitmapUtils;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PropertyConfigurator.getConfigurator(this).configure();
        Logger.info("U5Application:onCreate");
        nickNames = new ArrayList<TextView>();
        portraits = new ArrayList<ImageView>();

        initGson();
        bitmapUtils = new BitmapUtils(this);
        bitmapUtils.configDefaultLoadingImage(R.drawable.photo_error)
                .configDefaultLoadFailedImage(R.drawable.photo_error)
                .configDefaultAutoRotation(true);

        params = new HashMap<Integer, Object>();
        SDKInitializer.initialize(getApplicationContext());
        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        mVibrator = (Vibrator) getApplicationContext().getSystemService(
                Service.VIBRATOR_SERVICE);
        // 更新sessionDb的receiver
        UpdateSessionDBReceiver updateSessionDBReceiver = new UpdateSessionDBReceiver();
        IntentFilter SessionDBfilter = new IntentFilter();
        SessionDBfilter.addAction("com.longhuapuxin.updateSessiondb");
        registerReceiver(updateSessionDBReceiver, SessionDBfilter);
        makeExpressionList();
    }

    @Override
    public void onTerminate() {
        Logger.info("U5Application:onTerminate");
        super.onTerminate();
    }

    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public void exit() {
        Settings settings = Settings.instance();
        settings.setUserId("");
        settings.setToken("");
        settings.save(this);

        for (Activity activity : activityList) {
            activity.finish();
        }

        System.exit(0);
    }

    // observe nickname and portrait

    public void ObserveNickName(TextView text) {
        if (Settings.instance().User != null) {
            text.setText(Settings.instance().User.getNickName());
            if (nickNames.contains(text))
                return;
            nickNames.add(text);
        }
    }

    public void ObservePortait(ImageView portrait) {
        if (Settings.instance().User != null) {
            String path = Settings.instance().User.getPortrait();
            if (path != null && path.length() > 0) {
                ImageUrlLoader.fetchImageUrl(path, portrait, this);

            }
            if (portraits.contains(portrait))
                return;
            portraits.add(portrait);
        }
    }

    public void StopObserveNickName(TextView text) {
        if (nickNames.contains(text)) {
            nickNames.remove(text);
        }
    }

    public void StopObservePortait(ImageView portrait) {
        if (portraits.contains(portrait)) {
            portraits.remove(portrait);
        }

    }

    private Handler ObserverHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CHANGENICK:
                    UpdateNickNames();
                    break;
                case MSG_CHANGEPORTRAIT:
                    UpdatePortrait();
                    break;

            }

            super.handleMessage(msg);
        }
    };

    public void PublishNickNameChange(String nickName) {
        Settings.instance().User.setNickName(nickName);
        ObserverHandler.sendEmptyMessageDelayed(MSG_CHANGENICK, 200);

    }

    private void makeExpressionList() {
        String jsonString = readAssetFile(this, "expression.json");
        Expression expression = getGson().fromJson(jsonString,
                        Expression.class);
        expressionList = expression.getExpressions();
    }

    public List<Expression.ExpressionNode> getExpressionList() {
        return expressionList;
    }

    private String readAssetFile(Context context, String path) {
        AssetManager assetManager = context.getAssets();
        BufferedReader br = null;
        try {
            InputStream is = assetManager.open(path);
            br = new BufferedReader(new InputStreamReader(is));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private void UpdateNickNames() {
        String nickName = Settings.instance().User.getNickName();
        for (int i = nickNames.size() - 1; i > -1; i--) {
            TextView view = nickNames.get(i);
            if (view == null) {
                nickNames.remove(view);
            } else {
                view.setText(nickName);
            }
        }
    }

    public void PublishPortraitChange(String path) {
        // Settings.instance().setPortrait(path);
        Settings.instance().User.setPortrait(path);
        ObserverHandler.sendEmptyMessageDelayed(MSG_CHANGEPORTRAIT, 200);
    }

    private void UpdatePortrait() {
        String id = Settings.instance().User.getPortrait();
        if (id == null || id.length() <= 0) {
            return;
        }
        // String url = Settings.instance().getImageUrl()
        // + Settings.instance().User.getPortrait();
        for (int i = portraits.size() - 1; i > -1; i--) {
            ImageView view = portraits.get(i);
            if (view == null) {
                portraits.remove(view);
            } else {
                ImageUrlLoader.fetchImageUrl(id, view, this);
                // bitmapUtils.display(view, url);
            }
        }
    }

    public void registNetWorkReceiver() {
        new Handler().postDelayed(new Runnable() {
            public void run() {

                NetworkStatusReceiver networkReceiver = new NetworkStatusReceiver();
                IntentFilter networkFilter = new IntentFilter();
                networkFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
                registerReceiver(networkReceiver, networkFilter);
            }

        }, 2000);
    }

    private class NetworkStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {

                NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
                    if (mNetworkOk) {
//						Log.i("zhhhhhhh", "network is: " + mNetworkOk);
                        Logger.info("network is: " + mNetworkOk);
                        mNetworkOk = false;
                        if (isAppForground(context)) {
                            Intent dialogIntent = new Intent(getApplicationContext(), U5DialogActivity.class);
                            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(dialogIntent);
                        }
                    }
                } else {// connect network
                    mNetworkOk = true;
                }
            }
        }

        public boolean isAppForground(Context mContext) {
            ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
            List<RunningTaskInfo> tasks = am.getRunningTasks(1);
            if (!tasks.isEmpty()) {
                ComponentName topActivity = tasks.get(0).topActivity;
                if (!topActivity.getPackageName().equals(mContext.getPackageName())) {
                    return false;
                }
            }
            return true;
        }
    }


    private class UpdateSessionDBReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            modifySessionDB(intent.getStringExtra("sessionId"), intent.getBooleanExtra("isGroup", false));
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        // unregisterReceiver(mReceiver);
    }

    @SuppressLint("NewApi")
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        // unregisterReceiver(mReceiver);
    }

    private void httpRequestGetSession(String sessionId,
                                       final ChatSession chatSession) {
        Param[] params = new Param[3];
        params[0] = new Param("UserId", Settings.instance().getUserId());
        params[1] = new Param("Token", Settings.instance().getToken());
        params[2] = new Param("SessionId", sessionId);

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/im/getsession", params,
                new OkHttpClientManager.ResultCallback<ResponseGetSession>() {

                    @Override
                    public void onError(Request request, Exception e) {
//						Log.d("", "----GetSessionFail");
                        Logger.info("----GetSessionFail");
                    }

                    @Override
                    public void onResponse(ResponseGetSession response) {

                        if (response.isSuccess()) {
                            if (Settings.instance().getUserId()
                                    .equals(response.getSession().getUserId1())) {
                                chatSession.setSenderNickName(response
                                        .getSession().getNickName2());
                                chatSession.setSenderUserId(response
                                        .getSession().getUserId2());
                                chatSession.setSenderPortrait(response
                                        .getSession().getPortrait2());
                            } else {
                                chatSession.setSenderNickName(response
                                        .getSession().getNickName1());
                                chatSession.setSenderUserId(response
                                        .getSession().getUserId1());
                                chatSession.setSenderPortrait(response
                                        .getSession().getPortrait1());
                            }
                            SessionDao sessionHelper = new SessionDao(
                                    getApplicationContext());
                            sessionHelper.update(chatSession);
                            for (RecentSessionListener listener : listenerList) {
                                listener.updateRecentSession();
                            }
                            // recentSessionListener.updateRecentSession();
                        } else {
//                            Log.d("",
//                                    "----GetSession"
//                                            + response.getErrorMessage());
                            Logger.info("----GetSession"
                                    + response.getErrorMessage());
                        }
                    }
                });
    }

    private void modifySessionDB(String sessionId, boolean isGroup) {
        SessionDao sessionHelper = new SessionDao(this);
        List<ChatSession> messageBySession = sessionHelper
                .getMessageBySession(sessionId, isGroup);
        if (!isGroup) {
            if (messageBySession.size() > 0) {
                ChatSession chatSession = messageBySession.get(0);
                if (TextUtils.isEmpty(chatSession.getSenderNickName())
                        || TextUtils.isEmpty(chatSession.getSenderUserId())
                        || TextUtils.isEmpty(chatSession.getSenderPortrait())) {
                    httpRequestGetSession(chatSession.getSessionId(), chatSession);
                } else {
                    for (RecentSessionListener listener : listenerList) {
                        listener.updateRecentSession();
                    }
                    // if (recentSessionListener != null) {
                    // recentSessionListener.updateRecentSession();
                    // }
                }
            } else {
            }
        } else {
//            群聊
            if (messageBySession.size() > 0) {
                ChatSession chatSession = messageBySession.get(0);
                if (TextUtils.isEmpty(chatSession.getGroupName()) || TextUtils.isEmpty(chatSession.getGroupNote()) || TextUtils.isEmpty(chatSession.getOwnerId())) {
                    httpRequestGetCircle(chatSession.getSessionId(), chatSession);
                } else {
                    for (RecentSessionListener listener : listenerList) {
                        listener.updateRecentSession();
                    }
                    // if (recentSessionListener != null) {
                    // recentSessionListener.updateRecentSession();
                    // }
                }
            }
        }
    }

    public interface RecentSessionListener {
        public void updateRecentSession();
    }

    List<RecentSessionListener> listenerList = new ArrayList<RecentSessionListener>();

    public void setRecentSessionListener(
            RecentSessionListener recentSessionListener) {

        listenerList.add(recentSessionListener);
        // this.recentSessionListener = recentSessionListener;
    }

    public void removeSessionListener(RecentSessionListener listener) {
        listenerList.remove(listener);
    }

//	public void test() {
//		View v = View.inflate(getApplicationContext(), R.layout.test, null);
//	     AlertDialog d = new AlertDialog.Builder(getApplicationContext())
//	                      .setTitle("tanchulai")
//	                    .setMessage("bucuo de tanchulai")
//	                      .setView(v)
//	                      .setPositiveButton("hhhhhhhhhh", new DialogInterface.OnClickListener() {
//	                          @Override
//	                          public void onClick(DialogInterface dialog, int which) {
////	                             // TODO Auto-generated method stub
////	                             homeApplication.setRssUpdateDialogShowFlag(true);
////	                             dialog.dismiss();
//	                                   }
//	                                })
//	                    .create();
//	     d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//	     //d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);
//	     d.show();
//	}

    //	public void test2() {
//		Intent test = new Intent(getApplicationContext(), U5DialogActivity.class);
//		test.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		startActivity(test);
//	}
    private void httpRequestGetCircle(String sessionId,
                                      final ChatSession chatSession) {
        Param[] params = new Param[3];
        params[0] = new Param("UserId", Settings.instance().getUserId());
        params[1] = new Param("Token", Settings.instance().getToken());
        params[2] = new Param("CircleId", sessionId);

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/im/getcircle", params,
                new OkHttpClientManager.ResultCallback<ResponseGetCircle>() {

                    @Override
                    public void onError(Request request, Exception e) {
//						Log.d("", "----GetSessionFail");
                        Logger.info("----GetSessionFail");
                    }

                    @Override
                    public void onResponse(ResponseGetCircle response) {

                        if (response.isSuccess()) {
                            chatSession.setGroupName(response.getCircle().getName());
                            chatSession.setGroupNote(response.getCircle().getNote());
                            chatSession.setOwnerId(response.getCircle().getOwnerId());
                            SessionDao sessionHelper = new SessionDao(
                                    getApplicationContext());
                            sessionHelper.update(chatSession);
                            for (RecentSessionListener listener : listenerList) {
                                listener.updateRecentSession();
                            }
                            // recentSessionListener.updateRecentSession();
                        } else {
//                            Log.d("",
//                                    "----GetSession"
//                                            + response.getErrorMessage());
                            Logger.info("----GetCircle"
                                    + response.getErrorMessage());
                        }
                    }
                });
    }
}
