package com.longhuapuxin.u5;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.longhuapuxin.adapter.ChatMsgAdapter;
import com.longhuapuxin.adapter.ChatMsgAdapter.OnItemClickListener;
import com.longhuapuxin.common.DateFormate;
import com.longhuapuxin.common.ImageUrlLoader;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.dao.ChatMessageDao;
import com.longhuapuxin.dao.SessionDao;
import com.longhuapuxin.db.bean.ChatMessage;
import com.longhuapuxin.db.bean.ChatMsg;
import com.longhuapuxin.db.bean.ChatSession;
import com.longhuapuxin.entity.ResponseGetRecentSession;
import com.longhuapuxin.entity.ResponseGetSession;
import com.longhuapuxin.entity.ResponseNewOrder;
import com.longhuapuxin.entity.ResponseSearchLabel;
import com.longhuapuxin.entity.ResponseSearchLabel.User;
import com.longhuapuxin.entity.ResponseTalkTo;
import com.longhuapuxin.entity.ResponseViewMoneyEnvelope;
import com.longhuapuxin.service.IMService;
import com.longhuapuxin.service.IMService.IMListener;
import com.longhuapuxin.view.ChatFooterLayout;
import com.longhuapuxin.view.ChatFooterLayout.SendMsgListener;
import com.longhuapuxin.view.ChatFooterLayout.SoftInputViewListener;
import com.longhuapuxin.view.ChatHeaderLayout;
import com.longhuapuxin.view.ChatHeaderLayout.SenderUserLinstener;
import com.longhuapuxin.view.ChatRefreshListView;
import com.longhuapuxin.view.ChatRefreshListView.IOnRefreshListener;
import com.longhuapuxin.view.ChatRefreshListView.SoftInputViewListViewListener;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author zh
 * @date 2015-8-26
 */
public class ChatActivity extends Activity implements OnClickListener,
        IOnRefreshListener, SendMsgListener, IMListener,
        OnItemClickListener, SenderUserLinstener, ChatMsgAdapter.RedBagListener {
    /**
     * 消息
     */
    public static final int MSG_MYSELF = 0;
    public static final int MSG_OTHERS = 1;
    /**
     * 文件接收
     */
    public static final int FILE_SEND = 2;
    public static final int FILE_RECIEVE = 3;
    public static final int TRANSCTION_REQUEST = 4;
    public static final int TRANSCTION_RESULT = 5;
    public static final int TRANSCTION_RECEIVER_REQUEST = 6;
    public static final int TRANSCTION_RECEIVER_RESULT = 7;
    public static final int UPDATE_TALK_LIST = 8;
    public static final int UPDATE_ORDER_LIST = 9;
    public static final int LINSTENER_COMING = 10;
    public static final int LINSTENER_CHANGED_FOR_SENDER = 11;
    public static final int LINSTENER_CHANGED_FOR_RECEIVER = 12;
    private final static int RESULT_QUIT = -2;
    public static final int MONEY_RECIEVE = 300;
    public static final int MONEY_SEND = 301;
    private ImageView transctionRequest;
    private EditText mChatText;
    private ChatFooterLayout chatFooterLayout;
    private ChatHeaderLayout chatHeaderLayout;
    private boolean isGroup = false;
    //    给ChatFooterLayout设置键盘弹出或者
    private SoftInputViewListener mySoftInputViewListener = new SoftInputViewListener() {

        @Override
        public void softInputViewShow(View view) {
            showSoftInputView(view);
        }

        @Override
        public void softInputViewHide() {
            hideSoftInputView();
        }
    };
    private SoftInputViewListViewListener softInputViewListViewListener = new SoftInputViewListViewListener() {

        @Override
        public void softInputViewListViewHide() {
            hideSoftInputView();
        }
    };
    private ChatRefreshListView chatRefreshListView;
    private RefreshDataAsynTask mRefreshAsynTask;
    public List<ChatMsg> data = null;
    private ChatMsgAdapter chatMsgAdapter;
    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            // 发送消息
            if (msg.what == UPDATE_TALK_LIST) {
                mLastTime = DateFormate.String2Date(msg.getData().getString(
                        "time"));
                // Date date = mLastTime;
                ChatMsg chatMsg = new ChatMsg();
                chatMsg.setSessionId(sessionId);
                chatMsg.setText(msg.getData().getString("msg"));
                chatMsg.setOrderId("");
                chatMsg.setTime(msg.getData().getString("time"));
                chatMsg.setReceiverStatus("0");
                chatMsg.setSendStatus("0");
                chatMsg.setType(MSG_MYSELF);
                chatMsg.setIsGroup(isGroup);
                data.add(chatMsg);
                if (!isGroup) {
                    modifySessionDB(nickName2, userId2, portrait2, sessionId, msg
                                    .getData().getString("time"),
                            msg.getData().getString("msg"), isGroup);
                    myService.saveIMContentForSend(chatMsg);
                } else {
                    modifySessionDBForGroup(msg
                                    .getData().getString("time"),
                            msg.getData().getString("msg"), isGroup);
                    myService.saveIMContentForSend(chatMsg);
                }
                chatMsgAdapter.notifyDataSetChanged();
                chatRefreshListView.setSelection(chatMsgAdapter.getCount() - 1);
            }
            // 发送的order
            else if (msg.what == UPDATE_ORDER_LIST) {
                mLastTime = DateFormate.String2Date(msg.getData().getString(
                        "time"));
                ChatMsg chatMsg = new ChatMsg();
                chatMsg.setSessionId(sessionId);
                chatMsg.setText("");
                chatMsg.setOrderId(msg.getData().getString("orderId"));
                chatMsg.setTime(msg.getData().getString("time"));
                chatMsg.setReceiverStatus("0");
                chatMsg.setSendStatus("0");
                chatMsg.setType(FILE_SEND);
                data.add(chatMsg);
                modifySessionDB(nickName2, userId2, portrait2, sessionId, msg
                        .getData().getString("time"), "你请求的交易", isGroup);
                myService.saveIMContentForSend(chatMsg);
                chatMsgAdapter.notifyDataSetChanged();
                chatRefreshListView.setSelection(chatMsgAdapter.getCount() - 1);
            }
            // 收到消息
            else if (msg.what == LINSTENER_COMING) {
                ChatMsg chatMsg = (ChatMsg) msg.obj;
                if (sessionId.equals(chatMsg.getSessionId()) && isGroup == chatMsg.isGroup()) {
                    data.add(chatMsg);
                    mLastTime = DateFormate.String2Date(chatMsg.getTime());
                    chatMsgAdapter.refreshData(data);
                    chatRefreshListView.setSelection(chatMsgAdapter.getCount() - 1);
                }
            }
            // 改变order状态
            else if (msg.what == LINSTENER_CHANGED_FOR_SENDER) {
                // initData(getIMContent(msg.obj.toString()));
                if (isGroup == false) {
                    updateDateForSenderChatMsg(msg.getData().getString("orderId"),
                            msg.getData().getString("sendStatus"));
                }
            } else if (msg.what == LINSTENER_CHANGED_FOR_RECEIVER) {
                // initData(getIMContent(msg.obj.toString()));
                if (isGroup == false) {
                    updateDateForReceiverStatusChatMsg(
                            msg.getData().getString("orderId"), msg.getData()
                                    .getString("receiverStatus"));
                }
            }
            return false;
        }

    });
    private Date mLastTime = new Date(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.debug("ChatActivity->onCreate");
        setContentView(R.layout.activity_chat);
        isGroup = getIntent().getBooleanExtra("isGroup", false);
        if (data == null) {
            data = new ArrayList<ChatMsg>();
        }
        initHeader();
        initFooter();
    }

    private String sessionId;
    private String userId2;
    private String nickName;
    private String nickName2;
    private String portrait2;
    private String senderGender;

    private String groupName;
    private String groupNote;
    private String ownerId;

    private void initHeader() {
        chatHeaderLayout = (ChatHeaderLayout) findViewById(R.id.header);
        TextView name = (TextView) chatHeaderLayout.findViewById(R.id.name);
        Intent intent = getIntent();
        if (isGroup) {
            chatHeaderLayout.findViewById(R.id.attention_personal).setVisibility(View.GONE);
            chatHeaderLayout.findViewById(R.id.attention_group).setVisibility(View.VISIBLE);
            name.setText(intent.getStringExtra("GroupName"));
            sessionId = intent.getStringExtra("SessionId");
            groupName = intent.getStringExtra("GroupName");
            groupNote = intent.getStringExtra("GroupNote");
            ownerId = intent.getStringExtra("OwnerId");
            chatHeaderLayout.findViewById(R.id.attention_group).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ChatActivity.this, GroupDetailActivity.class);
                    intent.putExtra("groupId", sessionId);
                    intent.putExtra("groupOwnerId", ownerId);
                    intent.putExtra("groupName", groupName);
                    intent.putExtra("groupNote", groupNote);
                    startActivityForResult(intent, 0);

                }
            });

        } else {
            chatHeaderLayout.findViewById(R.id.attention_personal).setVisibility(View.VISIBLE);
            chatHeaderLayout.findViewById(R.id.attention_group).setVisibility(View.GONE);

            name.setText(intent.getStringExtra("NickName2"));
            sessionId = intent.getStringExtra("SessionId");
            userId2 = intent.getStringExtra("UserId2");
            nickName = intent.getStringExtra("NickName");
            if (nickName == null) {
                nickName = Settings.instance().getMyName();
            }
            nickName2 = intent.getStringExtra("NickName2");
            portrait2 = intent.getStringExtra("Portrait2");
            senderGender = intent.getStringExtra("Gender");
            chatHeaderLayout.setSenderUserLinstener(this);

        }
    }

    private void initRefresh() {
        chatRefreshListView = (ChatRefreshListView) findViewById(R.id.refresh_listview);
        chatRefreshListView.setOnRefreshListener(this);
        chatRefreshListView
                .setSoftInputViewListener(softInputViewListViewListener);
        chatMsgAdapter = new ChatMsgAdapter(this, data, portrait2, isGroup);
        chatRefreshListView.setAdapter(chatMsgAdapter);
        chatRefreshListView.requestFocus();
        chatMsgAdapter.setOnItemClickListener(this);
        chatMsgAdapter.setRedBagListener(this);
    }

    // private String sendTransactiontime;

    @Override
    public void onClick(View v) {
        // 发送order
        if (v == transctionRequest) {
            // data.add(new ChatMsg("test" + i, 2));
            Intent intent = new Intent(this, TransactionSendActivity.class);
            intent.putExtra("nickName2", nickName2);
            intent.putExtra("nickName", nickName);
            String sendTransactiontime = DateFormate.NowDate();
            // mLastTime = DateFormate.String2Date(sendTransactiontime);
            intent.putExtra("time", sendTransactiontime);
            startActivityForResult(intent, TRANSCTION_REQUEST);
        }
    }

    @SuppressLint("WrongViewCast")
    private void initFooter() {
        chatFooterLayout = (ChatFooterLayout) findViewById(R.id.footer);
        chatFooterLayout.setSendMsgListener(this);
        chatFooterLayout.setSoftInputViewListener(mySoftInputViewListener);
        chatFooterLayout.initGroupId(sessionId, isGroup);
        // mChatText = (EditText)
        // chatFooterLayout.findViewById(R.id.chat_content);
        // mChatText.setOnClickListener(this);
        transctionRequest = (ImageView) chatFooterLayout
                .findViewById(R.id.transaction);
        transctionRequest.setOnClickListener(this);
        if (isGroup) {
            transctionRequest.setVisibility(View.GONE);
        } else {
            transctionRequest.setVisibility(View.VISIBLE);

        }
    }

    // 显示软键盘
    public void showSoftInputView(View view) {
        if (getCurrentFocus() != null)
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                    .showSoftInput(view, 0);
    }

    /**
     * 隐藏软键盘 hideSoftInputView
     *
     * @param
     * @return void
     * @throws
     * @Title: hideSoftInputView
     * @Description: TODO
     */
    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this
                .getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private int index = 5;

    @Override
    public void myRedBag(String portrait, String who, String money, String note, final String orderId) {
        final RelativeLayout redBag = (RelativeLayout) findViewById(R.id.redBag);
        redBag.setVisibility(View.VISIBLE);
        ImageView redBagClose = (ImageView) findViewById(R.id.close);
        redBagClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                redBag.setVisibility(View.GONE);
            }
        });
        ImageView redBagPortrait = (ImageView) findViewById(R.id.portrait);
        ImageUrlLoader.fetchImageUrl(portrait, redBagPortrait, ChatActivity.this);
        TextView redBagWho = (TextView) findViewById(R.id.who);
        redBagWho.setText(who);
        TextView redBagMoney = (TextView) findViewById(R.id.red_money);
        redBagMoney.setText(money + "元");
        TextView redBagNote = (TextView) findViewById(R.id.note);
        redBagNote.setText(note);
        TextView redBagRecord = (TextView) findViewById(R.id.record);
        redBagRecord.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                redBag.setVisibility(View.GONE);
                httpRequestViewMoneyEnvelope(orderId);
            }
        });

    }

    class RefreshDataAsynTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            index++;
            // data.add(new ChatMsg("test" + index, 0));
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            chatMsgAdapter.refreshData(data);
            chatRefreshListView.onRefreshComplete();
            chatRefreshListView.setSelection(chatMsgAdapter.getCount() - 1);
        }

    }

    @Override
    public void OnRefresh() {
        mRefreshAsynTask = new RefreshDataAsynTask();
        mRefreshAsynTask.execute();
    }


    @Override
    public void sendMsg(String msg) {
//        if (TextUtils.isEmpty(msg)) {
//            Toast.makeText(this, "输入内容不能为空", Toast.LENGTH_SHORT).show();
//            return;
//        }
        if (!isGroup) {
            httpRequestTalkTo(msg);
        } else {
            httpRequestTalkToCircle(msg);
        }
    }

    private IMService myService;
    private ServiceConnection sc;
    private boolean isFirstIn = true;

    @Override
    protected void onResume() {
        super.onResume();

        if (isFirstIn) {
            getServiceConnect();
            initData(getIMContent(sessionId));
            initRefresh();
            chatRefreshListView.setSelection(chatMsgAdapter.getCount() - 1);
            isFirstIn = false;
        } else {
            adjustMLastTime();
            getServiceConnect();
            initData(getIMContent(sessionId));
            chatMsgAdapter.notifyDataSetChanged();
            chatRefreshListView.setSelection(chatMsgAdapter.getCount() - 1);
        }
        clearUnReadCount(sessionId);
    }

    private void adjustMLastTime() {
        ChatMessageDao msgHelper = new ChatMessageDao(this);
        List<ChatMessage> messageBySession = msgHelper
                .getMessageBySession(sessionId, isGroup);
        if (messageBySession.size() > 0) {
            mLastTime = messageBySession.get(messageBySession.size() - 1)
                    .getTime();
        }
    }

    private void getServiceConnect() {
        Intent intent = new Intent(ChatActivity.this, IMService.class);
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
                    myService.setIMListener(ChatActivity.this);
                    // int unreadCount = myService.GetCount();
                    // Toast.makeText(ChatActivity.this,
                    // "new message count" + unreadCount, 0).show();
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    sc = null;
                }

            };
        }
        bindService(intent, sc, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        // if (data.size() > 0) {
        // mLastTime = DateFormate.String2Date(data.get(data.size() - 1)
        // .getTime());
        // }
        super.onPause();
        // myService.StartCount();
        clearUnReadCount(sessionId);

        unbindService(sc);
    }

    private void clearUnReadCount(String id) {
        SessionDao sessionHelper = new SessionDao(this);
        List<ChatSession> sessionList = sessionHelper.getMessageBySession(id, isGroup);
        if (sessionList.size() > 0) {
            ChatSession session = sessionList.get(0);
            session.setUnReadCount(0);
            sessionHelper.update(session);
        }
    }

    public void initData(List<ChatMessage> messageBySession) {
        Collections.sort(messageBySession);
        for (ChatMessage chatMessage : messageBySession) {
            ChatMsg chatMsg = new ChatMsg();
            chatMsg.setSessionId(chatMessage.getSessionId());
            chatMsg.setText(chatMessage.getText());
            chatMsg.setOrderId(chatMessage.getOrderId());
            chatMsg.setTime(DateFormate.DateTime2String(chatMessage.getTime()));
            chatMsg.setReceiverStatus(chatMessage.getReceiverStatus());
            chatMsg.setSendStatus(chatMessage.getSendStatus());
            chatMsg.setType(chatMessage.getType());
            chatMsg.setSenderPortrait(chatMessage.getSenderPortrait());
            data.add(chatMsg);
        }
    }

    @Override
    public void IMIsComing(ChatMsg cMsg) {
        Message msg = new Message();
        msg.what = LINSTENER_COMING;
        msg.obj = cMsg;
        handler.sendMessage(msg);
    }

    private void httpRequestTalkTo(final String msg) {
        Param[] params = new Param[5];
        params[0] = new Param("UserId", Settings.instance().getUserId());
        params[1] = new Param("Token", Settings.instance().getToken());
        params[2] = new Param("TargetUserId", userId2);
        params[3] = new Param("Content", msg);
        params[4] = new Param("SessionId", sessionId);
        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/im/talkto", params,
                new OkHttpClientManager.ResultCallback<ResponseTalkTo>() {

                    @Override
                    public void onError(Request request, Exception e) {
//						Log.d("", "----TalkToFail");
                        Logger.info("----TalkToFail");
                    }

                    @Override
                    public void onResponse(ResponseTalkTo response) {

                        if (response.isSuccess()) {
//							Log.d("", "-------TalkToSuccess");
                            Logger.info("-------TalkToSuccess");
                            Message message = new Message();
                            message.what = UPDATE_TALK_LIST;
                            Bundle bundle = new Bundle();
                            bundle.putString("msg", msg);
                            bundle.putString("time", response.getTime());
                            message.setData(bundle);
                            handler.sendMessage(message);

                        } else {
                            Logger.info("----TalkTo" + response.getErrorMessage());
//							Log.d("", "----TalkTo" + response.getErrorMessage());
                        }
                    }
                });
    }

    private void httpResponseNewOrder(String money, String note) {
        Param[] params = new Param[8];
        params[0] = new Param("UserId", Settings.instance().getUserId());
        params[1] = new Param("Token", Settings.instance().getToken());
        params[2] = new Param("NickName", nickName);
        params[3] = new Param("UserId2", userId2);
        params[4] = new Param("NickName2", nickName2);
        params[5] = new Param("Amount", money);
        params[6] = new Param("Note", note);
        params[7] = new Param("SessionId", sessionId);

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/order/newOrder", params,
                new OkHttpClientManager.ResultCallback<ResponseNewOrder>() {

                    @Override
                    public void onError(Request request, Exception e) {
//                        Log.d("", "----NewOrderFail");
                        Logger.info("----NewOrderFail");
                    }

                    @Override
                    public void onResponse(ResponseNewOrder response) {

                        if (response.isSuccess()) {
                            Message msg = new Message();
                            msg.what = UPDATE_ORDER_LIST;
                            Bundle bundle = new Bundle();
                            bundle.putString("orderId", response.getOrder()
                                    .getId());
                            bundle.putString("time", response.getTime());
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        } else {
//                            Log.d("",
//                                    "----NewOrder" + response.getErrorMessage());
//                            Log.d("", "----NewOrder" + response.getErrorCode());
                            Logger.info("----NewOrder" + response.getErrorMessage());
                            Logger.info("----NewOrder" + response.getErrorCode());
                        }
                    }
                });
    }


    private void httpRequestTalkToCircle(final String msg) {
        Param[] params = new Param[4];
        params[0] = new Param("UserId", Settings.instance().getUserId());
        params[1] = new Param("Token", Settings.instance().getToken());
        params[2] = new Param("CircleId", sessionId);
        params[3] = new Param("Content", msg);
        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/im/talktocircle", params,
                new OkHttpClientManager.ResultCallback<ResponseTalkTo>() {

                    @Override
                    public void onError(Request request, Exception e) {
//						Log.d("", "----TalkToFail");
                        Logger.info("----TalkToCircleFail");
                    }

                    @Override
                    public void onResponse(ResponseTalkTo response) {

                        if (response.isSuccess()) {
//							Log.d("", "-------TalkToSuccess");
                            Logger.info("-------TalkToCircleSuccess");
                            Message message = new Message();
                            message.what = UPDATE_TALK_LIST;
                            Bundle bundle = new Bundle();
                            bundle.putString("msg", msg);
                            bundle.putString("time", response.getTime());
                            message.setData(bundle);
                            handler.sendMessage(message);

                        } else {
                            Logger.info("----TalkTo" + response.getErrorMessage());
//							Log.d("", "----TalkTo" + response.getErrorMessage());
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (resultCode == RESULT_QUIT) {
            finish();
        }

        // 接受我发送请求
        if (requestCode == TRANSCTION_REQUEST
                && resultCode == TRANSCTION_RESULT) {
            httpResponseNewOrder(intent.getStringExtra("Amount"),
                    intent.getStringExtra("Note"));
        }
        // 判断RECEIVER的消息我是否接受或拒绝
        else if (requestCode == TRANSCTION_RECEIVER_REQUEST
                && resultCode == TRANSCTION_RECEIVER_RESULT) {
            ChatMsg chatMsg = (ChatMsg) intent.getSerializableExtra("chatMsg");
            modifyIMContentForReceiver(chatMsg.getOrderId(),
                    chatMsg.getReceiverStatus());
            updateDateForThisChatMsg(chatMsg);
            if (chatMsg.getReceiverStatus().equals("1")) {
                httpRequestConfirmOrder("1", chatMsg.getOrderId());
            } else if (chatMsg.getReceiverStatus().equals("2")) {
                httpRequestConfirmOrder("2", chatMsg.getOrderId());
            }

        }
    }

    private void updateDateForThisChatMsg(ChatMsg chatMsg) {
        for (ChatMsg cMsg : data) {
            if (cMsg.getOrderId().equals(chatMsg.getOrderId())) {
                cMsg.setReceiverStatus(chatMsg.getReceiverStatus());
                chatMsgAdapter.notifyDataSetChanged();
                chatRefreshListView.setSelection(chatMsgAdapter.getCount() - 1);
            }
        }
    }

    private void updateDateForSenderChatMsg(String orderId, String sendStatus) {
        for (ChatMsg cMsg : data) {
            if (cMsg.getOrderId().equals(orderId)) {
                cMsg.setSendStatus(sendStatus);
                chatMsgAdapter.notifyDataSetChanged();
                chatRefreshListView.setSelection(chatMsgAdapter.getCount() - 1);
                break;
            }
        }
    }

    private void updateDateForReceiverStatusChatMsg(String orderId,
                                                    String receiverStatus) {
        for (ChatMsg cMsg : data) {
            if (cMsg.getOrderId().equals(orderId)) {
                cMsg.setReceiverStatus(receiverStatus);
                chatMsgAdapter.notifyDataSetChanged();
                chatRefreshListView.setSelection(chatMsgAdapter.getCount() - 1);
                break;
            }
        }
    }

    @Override
    public void onItemClick(ChatMsg chatMsg) {
        Intent intent = new Intent(this, TransactionReceiverActivity.class);
        intent.putExtra("chatMsg", chatMsg);
        startActivityForResult(intent, TRANSCTION_RECEIVER_REQUEST);
    }

    // 取指定条数
    public List<ChatMessage> getIMContent(String sessionId) {
        ChatMessageDao msgHelper = new ChatMessageDao(this);
        // Date date = mLastTime;
        List<ChatMessage> messageBySession = msgHelper.getMessageBySession(
                sessionId, mLastTime, isGroup);
        List<ChatMessage> messageBySessionLessthan50 = new ArrayList<ChatMessage>();
        if (messageBySession.size() >= 50) {
            for (int i = messageBySession.size() - 50; i < messageBySession
                    .size() - 1; i++) {
                messageBySessionLessthan50.add(messageBySession.get(i));
            }
            return messageBySessionLessthan50;
        }
        return messageBySession;
    }

    private void httpRequestConfirmOrder(String code, String orderId) {
        Param[] params = new Param[6];
        params[0] = new Param("UserId", Settings.instance().getUserId());
        params[1] = new Param("Token", Settings.instance().getToken());
        params[2] = new Param("OrderId", orderId);
        params[3] = new Param("Confirm", code);
        params[4] = new Param("Note", "");
        params[5] = new Param("SessionId", sessionId);

        OkHttpClientManager
                .postAsyn(
                        Settings.instance().getApiUrl() + "/order/confirmOrder",
                        params,
                        new OkHttpClientManager.ResultCallback<ResponseGetRecentSession>() {

                            @Override
                            public void onError(Request request, Exception e) {
//                                Log.d("", "----ConfirmOrderFail");
                                Logger.info("----ConfirmOrderFail");
                            }

                            @Override
                            public void onResponse(
                                    ResponseGetRecentSession response) {

                                if (response.isSuccess()) {

                                } else {
//                                    Log.d("",
//                                            "----ConfirmOrder"
//                                                    + response
//                                                    .getErrorMessage());
                                    Logger.info("----ConfirmOrder"
                                            + response
                                            .getErrorMessage());
                                }
                            }
                        });

    }

    @Override
    public void IMIsChangedForSenderStatus(String orderId, String sendStatus) {
        Message msg = new Message();
        msg.what = LINSTENER_CHANGED_FOR_SENDER;
        Bundle bundle = new Bundle();
        bundle.putString("orderId", orderId);
        bundle.putString("sendStatus", sendStatus);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    @Override
    public void IMIsChangedForReceiverStatus(String orderId,
                                             String receiverStatus) {
        Message msg = new Message();
        msg.what = LINSTENER_CHANGED_FOR_RECEIVER;
        Bundle bundle = new Bundle();
        bundle.putString("orderId", orderId);
        bundle.putString("receiverStatus", receiverStatus);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    private void modifyIMContentForReceiver(String orderId,
                                            String ReceiverStatus) {
        ChatMessageDao msgHelper = new ChatMessageDao(this);
        List<ChatMessage> messageByOrderId = msgHelper
                .getMessageByOrderId(orderId);
        ChatMessage chatMessage = messageByOrderId.get(0);
        chatMessage.setReceiverStatus(ReceiverStatus);
        msgHelper.updateMessageByOrderId(chatMessage);
    }

    private void httpRequestGetSession() {
        Param[] params = new Param[3];
        params[0] = new Param("UserId", Settings.instance().getUserId());
        params[1] = new Param("Token", Settings.instance().getToken());
        params[2] = new Param("SessionId", sessionId);

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/im/getsession", params,
                new OkHttpClientManager.ResultCallback<ResponseGetSession>() {

                    @Override
                    public void onError(Request request, Exception e) {
//                        Log.d("", "----GetRecentSessionFail");
                        Logger.info("----GetRecentSessionFail");
                    }

                    @Override
                    public void onResponse(ResponseGetSession response) {

                        if (response.isSuccess()) {

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

    private void modifySessionDB(String senderNickName, String senderUserId,
                                 String senderPortrait, String sessionId, String time, String text, boolean isGroup) {
        SessionDao sessionHelper = new SessionDao(this);

        List<ChatSession> sessionList = sessionHelper
                .getMessageBySession(sessionId, isGroup);

        ChatSession chatSession = null;
        // 有一个chatSession的时候
        if (sessionList.size() > 0) {
            chatSession = sessionList.get(0);
            chatSession.setIsGroup(isGroup);
            int count = chatSession.getUnReadCount();
            count++;
            chatSession.setUnReadCount(count);
            chatSession.setSenderNickName(senderNickName);
            chatSession.setSenderUserId(senderUserId);
            chatSession.setSenderPortrait(senderPortrait);
            chatSession.setSessionId(sessionId);
            chatSession.setTime(DateFormate.String2Date(time));
            chatSession.setText(text);
            sessionHelper.update(chatSession);
        } else {
            chatSession = new ChatSession();
            chatSession.setIsGroup(isGroup);
            chatSession.setUnReadCount(0);
            chatSession.setSenderNickName(senderNickName);
            chatSession.setSenderUserId(senderUserId);
            chatSession.setSenderPortrait(senderPortrait);
            chatSession.setSessionId(sessionId);
            chatSession.setTime(DateFormate.String2Date(time));
            chatSession.setText(text);
            if (senderGender != null) {
                chatSession.setSenderGender(senderGender);
            }
            sessionHelper.add(chatSession);
        }
    }

    private void modifySessionDBForGroup(String time, String text, boolean isGroup) {
        SessionDao sessionHelper = new SessionDao(this);

        List<ChatSession> sessionList = sessionHelper
                .getMessageBySession(sessionId, isGroup);

        ChatSession chatSession = null;
        // 有一个chatSession的时候
        if (sessionList.size() > 0) {
            chatSession = sessionList.get(0);
            chatSession.setIsGroup(isGroup);
            int count = chatSession.getUnReadCount();
            count++;
            chatSession.setUnReadCount(count);
            chatSession.setSessionId(sessionId);
            chatSession.setOwnerId(ownerId);
            chatSession.setGroupName(groupName);
            chatSession.setGroupNote(groupNote);
            chatSession.setTime(DateFormate.String2Date(time));
            chatSession.setText(text);
            sessionHelper.update(chatSession);
        } else {
            chatSession = new ChatSession();
            chatSession.setIsGroup(isGroup);
            chatSession.setUnReadCount(0);
            chatSession.setSessionId(sessionId);
            chatSession.setOwnerId(ownerId);
            chatSession.setGroupName(groupName);
            chatSession.setGroupNote(groupNote);
            chatSession.setTime(DateFormate.String2Date(time));
            chatSession.setText(text);
            sessionHelper.add(chatSession);
        }
    }

    private User mUser = new ResponseSearchLabel().new User();

    @Override
    public void getSenderUser() {
        setUserInfo(mUser);
        ((U5Application) getApplication()).putParam(U5Application.USER_DETAIL,
                mUser);
        Intent intent = new Intent(ChatActivity.this, LabelDetailActivity.class);
        startActivity(intent);
    }

    private void setUserInfo(User mUser) {
        mUser.setUserId(userId2);
        mUser.setNickName(nickName2);
        mUser.setPortrait(portrait2);
        mUser.setGender(senderGender);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent != null && intent.getBooleanExtra("isGroup", false)) {
            ChatMsg chatMsg = (ChatMsg) intent.getSerializableExtra("chatMsg");
            data.add(chatMsg);
            chatMsgAdapter.refreshData(data);
            chatRefreshListView.setSelection(chatMsgAdapter.getCount() - 1);
        }
    }

    private void httpRequestViewMoneyEnvelope(String MoneyEnvelopeId) {
        WaitDialog.instance().showWaitNote(ChatActivity.this);
        Settings setting = Settings.instance();
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[3];
        params[0] = new OkHttpClientManager.Param("Token", setting.getToken());
        params[1] = new OkHttpClientManager.Param("UserId", setting.getUserId());
        params[2] = new OkHttpClientManager.Param("MoneyEnvelopeId", MoneyEnvelopeId);

        OkHttpClientManager
                .postAsyn(
                        setting.getApiUrl() + "/im/viewMoneyEnvelope",
                        params,
                        new OkHttpClientManager.ResultCallback<ResponseViewMoneyEnvelope>() {
                            @Override
                            public void onError(Request request, Exception e) {
                                e.printStackTrace();
                                WaitDialog.instance().hideWaitNote();
                            }

                            @Override
                            public void onResponse(
                                    ResponseViewMoneyEnvelope response) {
                                if (response.isSuccess()) {
                                    Intent intent = new Intent(ChatActivity.this, MyEnvelopeActivity.class);
                                    intent.putExtra("portrait", response.getMoneyEnvelope().getPortrait());
                                    intent.putExtra("nickname", response.getMoneyEnvelope().getNickName());
                                    intent.putExtra("money", response.getMoney());
                                    intent.putExtra("note", response.getMoneyEnvelope().getNote());
                                    intent.putExtra("nicknames", response.getMoneyEnvelope().getNickNames());
                                    intent.putExtra("moneys", response.getMoneyEnvelope().getMoneyReceives());
                                    intent.putExtra("ids", response.getMoneyEnvelope().getUserIds());
                                    intent.putExtra("ownerid", ownerId);
                                    startActivity(intent);
                                } else {
                                }
                                WaitDialog.instance().hideWaitNote();
                            }
                        });

    }
}
