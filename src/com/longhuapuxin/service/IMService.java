package com.longhuapuxin.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.longhuapuxin.common.DateFormate;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.dao.ChatMessageDao;
import com.longhuapuxin.dao.SessionDao;
import com.longhuapuxin.db.bean.ChatMessage;
import com.longhuapuxin.db.bean.ChatMsg;
import com.longhuapuxin.db.bean.ChatSession;
import com.longhuapuxin.db.bean.GroupMsg;
import com.longhuapuxin.db.bean.RedBag;
import com.longhuapuxin.entity.PushResponseDad;
import com.longhuapuxin.entity.PushResponsePayContent;
import com.longhuapuxin.entity.PushResponseShopAppointment;
import com.longhuapuxin.entity.ResponseDad;
import com.longhuapuxin.mqtt.MQTTCallBack;
import com.longhuapuxin.mqtt.MQTTClient;
import com.longhuapuxin.mqtt.MQTTMessage;
import com.longhuapuxin.mqtt.MQTTNIOClient;
import com.longhuapuxin.u5.MyLocationListener;
import com.longhuapuxin.u5.Settings;
import com.longhuapuxin.u5.U5Application;
import com.squareup.okhttp.Request;

import java.util.List;

public class IMService extends Service {
    /**
     * 消息
     */
    private static final int MSG_MYSELF = 0;
    private static final int MSG_OTHERS = 1;
    private static final int MSG_ReConnect = 2;
    private static  final  int MSG_LockScreen=99;
    private static  final int MSG_UnLockScreen=100;
    /**
     * 文件接收
     */

    private static final int FILE_RECIEVE = 3;
    public static final int MONEY_RECIEVE = 300;

    public LocationClient mLocationClient = null;
    private boolean hasLocation = false;
    private boolean hasSetLocation = false;
    private MyLocationListener.OnLocation onServiceLocation;
    private boolean NetBrokenHandled = false;
    private ScreenBroadcastReceiver screenBroadcastReceiver;

    public boolean IsConnected() {
        return mqttClient != null && mqttClient.Connected;
    }

    public class LocalBinder extends Binder {

        public IMService getService() {
            return IMService.this;
        }
    }

    private LocalBinder myBind = new LocalBinder();
    private MyLocationListener.OnLocation onLocation = new MyLocationListener.OnLocation() {

        @Override
        public void onError(String msg) {
            if (onServiceLocation != null) {
                onServiceLocation.onError(msg);
            }
        }

        @Override
        public void onLocated(BDLocation location, boolean isGps) {

            float longtitude = (float) location.getLongitude();
            float latitude = (float) location.getLatitude();
            String cityCode = location.getCityCode();
            String city = location.getCity();
            String address = location.getAddrStr();
            Settings.instance().address = address;
            Settings.instance().City = city;
            Settings.instance().CityCode = cityCode;
            Settings.instance().Latitude = latitude;
            Settings.instance().Lontitude = longtitude;
            Settings.instance().saveLocation(getApplication());
            hasLocation = true;
            UploadLocation();
            if (onServiceLocation != null) {
                onServiceLocation.onLocated(location, isGps);
            }
        }

    };

    public void UploadLocation() {
        Settings setting = Settings.instance();
        if (setting.getUserId() == null || setting.getUserId().length() <= 0) {
            return;
        }
        if (!hasLocation || hasSetLocation) {
            return;
        }
        hasSetLocation = true;
        Param[] params = new Param[6];

        params[0] = new Param("UserId", setting.getUserId());
        params[1] = new Param("Token", setting.getToken());

        if (TextUtils.isEmpty(Settings.instance().CityCode)) {
            params[2] = new Param("BDCityCode", "");
        } else {
            params[2] = new Param("BDCityCode", Settings.instance().CityCode);
        }

        if (TextUtils.isEmpty(Settings.instance().City)) {
            params[3] = new Param("City", "");
        } else {
            params[3] = new Param("City", Settings.instance().City);
        }
        params[4] = new Param("Longtitude",
                String.valueOf(Settings.instance().Lontitude));
        params[5] = new Param("Latidue",
                String.valueOf(Settings.instance().Latitude));
        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/auth/SetLocation", params,
                new OkHttpClientManager.ResultCallback<ResponseDad>() {

                    @Override
                    public void onError(Request request, Exception e) {

                    }

                    @Override
                    public void onResponse(ResponseDad response) {

                        if (response.isSuccess()) {

                        }
                    }

                });

    }

    @Override
    public IBinder onBind(Intent arg0) {
        return myBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("TAG", "onUnbind~~~~~~~~~~~~~~~~");
        onServiceLocation = null;
        return super.onUnbind(intent);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        initLocation();
        Log.i("TAG", "onStart~~~~~~");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.info("onStartCommand~~~~~~~~~~~");
        // return START_STICKY;


        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("TAG", "onCreate~~~~~~~~~~");
        // 锁屏开关receiver
        screenBroadcastReceiver = new ScreenBroadcastReceiver();
        IntentFilter Screenfilter = new IntentFilter();
        Screenfilter.addAction(Intent.ACTION_SCREEN_ON);
        Screenfilter.addAction(Intent.ACTION_SCREEN_OFF);
       // Screenfilter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(screenBroadcastReceiver, Screenfilter);
    }

    public void ConnectToMQTT() {
        if (mqttClient == null || mqttClient.Connected == false) {
            new Thread(connectMQTTRunable).start();
        }
    }

    @Override
    public void onDestroy() {
        Log.i("TAG", "onDestroy~~~~~~~~~~~");
        unregisterReceiver(screenBroadcastReceiver);
        if (mqttClient != null && mqttClient.Connected) {
            mqttClient.Close(true);
            mqttClient = null;

        }

        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
        }

        Intent service = new Intent(this, IMService.class);
        this.startService(service);
        super.onDestroy();

    }

    private MQTTNIOClient mqttClient;

    public interface IMPayCallBack {
        public void OnNewPay(PushResponsePayContent pushResponsePayContent);

        public void OnNetWorkBreak(String errMessage);

        public void OnConnected();
    }

    private IMPayCallBack payCallBack;

    private MQTTCallBack mqttCallBack = new MQTTCallBack() {

        @Override
        public void OnMessage(MQTTMessage msg, String socketId) {
            switch (msg.MessageType) {
                case CONNACK:
                    mqttClient.Subscribe("Customer_"
                            + Settings.instance().getUserId());

                    break;
                case CONNECT:
                    break;
                case DISCONNECT:

                    break;
                case PINGREQ:
                    break;
                case PINGRESP:
                    break;
                case PUBACK:
                    break;
                case PUBCOMP:
                    break;
                case PUBLISH:

                    PushResponseDad pushResponseDad = ((U5Application) getApplicationContext())
                            .getGson().fromJson(msg.GetContent(),
                                    PushResponseDad.class);
                    if (pushResponseDad.getMessageCode().equals("301")) {
                        PushResponsePayContent pushResponsePayContent = ((U5Application) getApplicationContext())
                                .getGson().fromJson(
                                        pushResponseDad.getMessageBody(),
                                        PushResponsePayContent.class);
                        if (payCallBack != null) {
                            payCallBack.OnNewPay(pushResponsePayContent);
                        }
                    } else if (pushResponseDad.getMessageCode().equals("101")) {
                        Logger.info("receive chat message");
                        ChatMsg ChatMsg = ((U5Application) getApplicationContext())
                                .getGson().fromJson(
                                        pushResponseDad.getMessageBody(),
                                        ChatMsg.class);
                        ChatMsg.setReceiverStatus("0");
                        ChatMsg.setSendStatus("0");
                        ChatMsg.setType(MSG_OTHERS);
                        ChatMsg.setIsGroup(false);
                        ChatMessage chatMsg2ChatMessage = chatMsg2ChatMessage(ChatMsg);
                        saveSessionDBForSessionId(ChatMsg.getSessionId(),
                                ChatMsg.getTime(), ChatMsg.getText(), false);
                        saveIMContentForReceiver(chatMsg2ChatMessage, ChatMsg);
                        sendBroad(ChatMsg.getSessionId(), false);
                    } else if (pushResponseDad.getMessageCode().equals("201")) {
                        Logger.info("receive order message");
                        ChatMsg ChatMsg = ((U5Application) getApplicationContext())
                                .getGson().fromJson(
                                        pushResponseDad.getMessageBody(),
                                        ChatMsg.class);
                        ChatMsg.setReceiverStatus("0");
                        ChatMsg.setSendStatus("0");
                        ChatMsg.setType(FILE_RECIEVE);
                        ChatMsg.setIsGroup(false);
                        saveSessionDBForSessionId(ChatMsg.getSessionId(),
                                ChatMsg.getTime(), "请求与你的交易", false);
                        ChatMessage chatMsg2ChatMessage = chatMsg2ChatMessage(ChatMsg);
                        saveIMContentForReceiver(chatMsg2ChatMessage, ChatMsg);
                        sendBroad(ChatMsg.getSessionId(), false);
                    } else if (pushResponseDad.getMessageCode().equals("202")) {

                        ChatMsg ChatMsg = ((U5Application) getApplicationContext())
                                .getGson().fromJson(
                                        pushResponseDad.getMessageBody(),
                                        ChatMsg.class);
                        ChatMsg.setReceiverStatus("0");
                        ChatMsg.setSendStatus("1");
                        ChatMsg.setIsGroup(false);
                        ChatMessage chatMsg2ChatMessage = chatMsg2ChatMessage(ChatMsg);
                        modifyIMContentForSend(chatMsg2ChatMessage.getOrderId(),
                                "1");
                        IMListener.IMIsChangedForSenderStatus(
                                chatMsg2ChatMessage.getOrderId(), "1");
                    } else if (pushResponseDad.getMessageCode().equals("203")) {

                        ChatMsg ChatMsg = ((U5Application) getApplicationContext())
                                .getGson().fromJson(
                                        pushResponseDad.getMessageBody(),
                                        ChatMsg.class);
                        ChatMsg.setReceiverStatus("0");
                        ChatMsg.setSendStatus("2");
                        ChatMsg.setIsGroup(false);
                        ChatMessage chatMsg2ChatMessage = chatMsg2ChatMessage(ChatMsg);
                        modifyIMContentForSend(chatMsg2ChatMessage.getOrderId(),
                                "2");
                        IMListener.IMIsChangedForSenderStatus(
                                chatMsg2ChatMessage.getOrderId(), "2");
                    } else if (pushResponseDad.getMessageCode().equals("206")) {

                        ChatMsg ChatMsg = ((U5Application) getApplicationContext())
                                .getGson().fromJson(
                                        pushResponseDad.getMessageBody(),
                                        ChatMsg.class);
                        ChatMsg.setReceiverStatus("3");
                        ChatMsg.setSendStatus("0");
                        ChatMsg.setIsGroup(false);
                        ChatMessage chatMsg2ChatMessage = chatMsg2ChatMessage(ChatMsg);
                        modifyIMContentForReceiver(
                                chatMsg2ChatMessage.getOrderId(), "3");
                        IMListener.IMIsChangedForReceiverStatus(
                                chatMsg2ChatMessage.getOrderId(), "3");
                    } else if (pushResponseDad.getMessageCode().equals("402")) {
                        PushResponseShopAppointment pushResponseShopAppointment = ((U5Application) getApplicationContext())
                                .getGson().fromJson(
                                        pushResponseDad.getMessageBody(), PushResponseShopAppointment.class);
                        sendBroadForAppointment(pushResponseShopAppointment.getText());
                    } else if (pushResponseDad.getMessageCode().equals("102")) {
                        GroupMsg groupMsg = ((U5Application) getApplicationContext())
                                .getGson().fromJson(
                                        pushResponseDad.getMessageBody(), GroupMsg.class);
                        ChatMessage groupMsg2ChatMessage = groupMsg2ChatMessage(groupMsg);
                        SaveSessionDBForGroup(groupMsg);
                        saveIMContentForReceiver(groupMsg2ChatMessage, chatMessage2ChatMsg(groupMsg2ChatMessage));
                        sendBroad(groupMsg2ChatMessage.getSessionId(), true);


                    } else if (pushResponseDad.getMessageCode().equals("103")) {//发红包给个人
                        RedBag redBag = ((U5Application) getApplicationContext())
                                .getGson().fromJson(
                                        pushResponseDad.getMessageBody(), RedBag.class);
                        ChatMsg ChatMsg = new ChatMsg();
                        ChatMsg.setIsGroup(false);
                        ChatMsg.setSessionId(redBag.getSessionId());
                        ChatMsg.setTime(redBag.getTime());
                        ChatMsg.setText(redBag.getNote());
                        ChatMsg.setOrderId(redBag.getMoneyEnvelopeId());
                        ChatMsg.setType(MONEY_RECIEVE);
//                        ChatMsg.setSenderPortrait();
                        saveSessionDBForSessionId(redBag.getSessionId(), redBag.getTime(), "红包：" + redBag.getNote(), false);
                        ChatMessage chatMsg2ChatMessage = chatMsg2ChatMessage(ChatMsg);
                        saveIMContentForReceiver(chatMsg2ChatMessage, ChatMsg);
                        sendBroad(chatMsg2ChatMessage.getSessionId(), false);
                    } else if (pushResponseDad.getMessageCode().equals("104")) {//发红包给群
                        RedBag redBag = ((U5Application) getApplicationContext())
                                .getGson().fromJson(
                                        pushResponseDad.getMessageBody(), RedBag.class);
                        ChatMsg ChatMsg = new ChatMsg();
                        ChatMsg.setIsGroup(true);
                        ChatMsg.setSessionId(redBag.getCircleId());
                        ChatMsg.setTime(redBag.getTime());
                        ChatMsg.setText(redBag.getNote());
                        ChatMsg.setOrderId(redBag.getMoneyEnvelopeId());
                        ChatMsg.setType(MONEY_RECIEVE);
                        ChatMsg.setSenderPortrait(redBag.getPortrait());
                        saveSessionDBForSessionId(redBag.getCircleId(), redBag.getTime(), "红包：" + redBag.getNote(), true);
                        ChatMessage chatMsg2ChatMessage = chatMsg2ChatMessage(ChatMsg);
                        saveIMContentForReceiver(chatMsg2ChatMessage, ChatMsg);
                        sendBroad(chatMsg2ChatMessage.getSessionId(), true);
                    }

                    break;
                case PUBREC:
                    break;
                case PUBREL:
                    break;
                case Reserved:
                    break;
                case Reserved2:
                    break;
                case SUBACK:
                    break;
                case SUBSCRIBE:
                    break;
                case UNSUBACK:
                    break;
                case UNSUBSCRIBE:
                    break;
                default:
                    break;

            }
        }

        private Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_ReConnect:
                        new Thread(connectMQTTRunable).start();
                        break;

                }
            }
        };

        @Override
        public void OnNetWorkBreak(String errMessage) {
           Logger.info("数据链接失败"+errMessage);
            if (!NetBrokenHandled)
                return;
            if (payCallBack != null) {
                payCallBack.OnNetWorkBreak(errMessage);
            }
            handler.sendEmptyMessageDelayed(MSG_ReConnect, 2000);

        }

        @Override
        public void OnConnected() {
            Logger.info("已经连接到mqtt");

            if (payCallBack != null) {
                payCallBack.OnConnected();
            }
        }

    };

    public void StartPayIM(IMPayCallBack payCallBack) {

        this.payCallBack = payCallBack;

    }

    private void sendBroad(String sessionId, boolean isGroup) {
        Intent intent = new Intent("com.longhuapuxin.updateSessiondb");
        intent.putExtra("sessionId", sessionId);
        intent.putExtra("isGroup", isGroup);
        sendBroadcast(intent);
    }

    private void sendBroadForAppointment(String content) {
        Intent intent = new Intent("com.longhuapuxin.shopAppointment");
        intent.putExtra("content", content);
        sendBroadcast(intent);
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

    private void SaveSessionDBForGroup(GroupMsg msg) {
        SessionDao sessionHelper = new SessionDao(this);

        List<ChatSession> sessionList = sessionHelper
                .getMessageBySession(msg.getCircleId(), true);

        ChatSession chatSession = null;
        // 有一个chatSession的时候
        if (sessionList.size() > 0) {
            chatSession = sessionList.get(0);
            chatSession.setIsGroup(true);
            int count = chatSession.getUnReadCount();
            count++;
            chatSession.setUnReadCount(count);
            chatSession.setSessionId(msg.getCircleId());

            chatSession.setGroupName(msg.getCircleName());

            chatSession.setTime(DateFormate.String2Date(msg.getTime()));
            chatSession.setText(msg.getText());
            sessionHelper.update(chatSession);
        } else {
            chatSession = new ChatSession();
            chatSession.setIsGroup(true);
            chatSession.setUnReadCount(1);
            chatSession.setSessionId(msg.getCircleId());
            chatSession.setGroupName(msg.getCircleName());
            chatSession.setTime(DateFormate.String2Date(msg.getTime()));
            chatSession.setText(msg.getText());

            sessionHelper.add(chatSession);
        }
    }

    public void ReConnect(IMPayCallBack payCallBack) {
        this.payCallBack = payCallBack;
        Logger.info("即将重新连接");

        new Thread(connectMQTTRunable).start();
    }

    private Runnable connectMQTTRunable = new Runnable() {

        @Override
        public void run() {
            if (mqttClient == null) {
                mqttClient =MQTTNIOClient.instance (Settings.instance().getMqttUrl(),
                        Settings.MQTTPort, mqttCallBack);

            }
            Logger.info("正在连接");

            mqttClient.Connect();
            NetBrokenHandled = true;

        }

    };

    public void StopPayIM() {

        this.payCallBack = null;
    }

    private void initLocation() {
        mLocationClient = ((U5Application) getApplication()).mLocationClient;
        ((U5Application) getApplication()).mMyLocationListener
                .SetOnLocationCallBack(onLocation);

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
        // int span=1000;
        option.setScanSpan(0);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);// 可选，默认false,设置是否使用gps
        // option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(true);
        ;// 可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
       // option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    public void GetLocation(MyLocationListener.OnLocation onLocation) {
        this.onServiceLocation = onLocation;
        hasLocation = false;
        hasSetLocation = false;
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
        mLocationClient.start();
    }

    private void saveIMContentForReceiver(ChatMessage chatMessage,
                                          ChatMsg chatMsg) {
        ChatMessageDao msgHelper = new ChatMessageDao(this);
        ChatMessage cMessage = chatMessage;
        msgHelper.add(cMessage);
        if (IMListener != null) {
            IMListener.IMIsComing(chatMsg);
        }
    }

    private ChatMessage chatMsg2ChatMessage(ChatMsg chatMsg) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSessionId(chatMsg.getSessionId());
        chatMessage.setText(chatMsg.getText());
        chatMessage.setOrderId(chatMsg.getOrderId());
        chatMessage.setTime(DateFormate.String2Date(chatMsg.getTime()));
        chatMessage.setReceiverStatus(chatMsg.getReceiverStatus());
        chatMessage.setSendStatus(chatMsg.getSendStatus());
        chatMessage.setType(chatMsg.getType());
        chatMessage.setIsGroup(chatMsg.isGroup());
        chatMessage.setSenderPortrait(chatMsg.getSenderPortrait());
        return chatMessage;
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

    private ChatMessage groupMsg2ChatMessage(GroupMsg groupMsg) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSessionId(groupMsg.getCircleId());
        chatMessage.setText(groupMsg.getText());
        chatMessage.setTime(DateFormate.String2Date(groupMsg.getTime()));
        chatMessage.setOrderId("");
        chatMessage.setReceiverStatus("0");
        chatMessage.setSendStatus("0");
        chatMessage.setType(MSG_OTHERS);
        chatMessage.setIsGroup(true);
        chatMessage.setSenderPortrait(groupMsg.getPortrait());
        return chatMessage;
    }

    private void modifyIMContentForSend(String orderId, String SendStatus) {
        ChatMessageDao msgHelper = new ChatMessageDao(this);
        List<ChatMessage> messageByOrderId = msgHelper
                .getMessageByOrderId(orderId);
        ChatMessage chatMessage = messageByOrderId.get(0);
        chatMessage.setSendStatus(SendStatus);
        msgHelper.updateMessageByOrderId(chatMessage);
    }

    private void modifyIMContentForReceiver(String orderId,
                                            String receiverStatus) {
        ChatMessageDao msgHelper = new ChatMessageDao(this);
        List<ChatMessage> messageByOrderId = msgHelper
                .getMessageByOrderId(orderId);
        ChatMessage chatMessage = messageByOrderId.get(0);
        chatMessage.setReceiverStatus(receiverStatus);
        msgHelper.updateMessageByOrderId(chatMessage);
    }

    public void saveIMContentForSend(ChatMsg chatMsg) {
        ChatMessageDao msgHelper = new ChatMessageDao(this);
        msgHelper.add(chatMsg2ChatMessage(chatMsg));
    }


    public interface IMListener {
        public void IMIsComing(ChatMsg cMsg);

        public void IMIsChangedForSenderStatus(String orderId, String sendStatus);

        public void IMIsChangedForReceiverStatus(String orderId,
                                                 String reciverStatus);
    }

    private IMListener IMListener;

    public void setIMListener(IMListener IMListener) {
        this.IMListener = IMListener;
    }

    private Handler lockScreenHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MSG_LockScreen:
                    if (mqttClient != null && mqttClient.Connected) {
                        new Thread() {
                            @Override
                            public void run() {
                                mqttClient.Close(true);
                                mqttClient = null;
                            }
                        }.start();


                    }
                    break;
                case MSG_UnLockScreen:
                    ConnectToMQTT();
                    break;
            }
        }
    };
    /**
     * screen状态广播接收者
     */
    private class ScreenBroadcastReceiver extends BroadcastReceiver {
        private String action = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
                Log.d("", "-------SCREEN_ON");
                lockScreenHandler.sendEmptyMessage(MSG_UnLockScreen);

            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
                Log.d("", "-------SCREEN_OFF");
               lockScreenHandler.sendEmptyMessage(MSG_LockScreen);
            }
        }
    }

}
