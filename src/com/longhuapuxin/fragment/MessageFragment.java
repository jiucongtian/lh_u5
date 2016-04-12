package com.longhuapuxin.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.longhuapuxin.adapter.MessageCanDeleteAdapter;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.dao.SessionDao;
import com.longhuapuxin.db.bean.ChatSession;
import com.longhuapuxin.u5.ChatActivity;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.u5.Settings;
import com.longhuapuxin.u5.U5Application;
import com.longhuapuxin.u5.U5Application.RecentSessionListener;
import com.longhuapuxin.view.RoundCornerImageView;
import com.longhuapuxin.view.SlideCutListView;
import com.longhuapuxin.view.SlideCutListView.OnSlideListener;
import com.longhuapuxin.view.SlideCutListView.SlideCutListViewCloseItemLinstener;
import com.longhuapuxin.view.SlideView;

@SuppressLint("NewApi")
public class MessageFragment extends Fragment implements OnSlideListener,
        RecentSessionListener, SlideCutListViewCloseItemLinstener {
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
    private SlideCutListView messageListView;
    private SlideView mLastSlideViewWithStatusOn;

    RoundCornerImageView imgPortrait;
    U5Application app;
    FragmentListener mListener;
    private MessageCanDeleteAdapter messageCanDeleteAdapter;
    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 0) {
                if (messageCanDeleteAdapter != null) {
                    messageCanDeleteAdapter.refresh();
                }
            }
            return false;
        }
    });

    // private List<MessageItem> mMessageItems = new ArrayList<MessageItem>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Logger.debug("MainMessageFragment->onCreateView");
        View view = inflater.inflate(R.layout.fragment_message, null);
        imgPortrait = (RoundCornerImageView) view
                .findViewById(R.id.imgPortrait);
        ((U5Application) getActivity().getApplicationContext())
                .setRecentSessionListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        WaitDialog.instance().showWaitNote(getActivity());
        getSessionsFromSessionDB();
        initViews(sessionData);
        WaitDialog.instance().hideWaitNote();
        // initViews();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        app = (U5Application) activity.getApplication();
        mListener = (FragmentListener) activity;
    }

    private boolean isFirst = true;

    @Override
    public void onResume() {
        super.onResume();
        app.ObservePortait(imgPortrait);
        if (!isFirst) {
            getSessionsFromSessionDB();
            messageCanDeleteAdapter.refresh();
        }
        isFirst = false;
    }

    private List<ChatSession> sessionData = new ArrayList<ChatSession>();

    // 得到每个sessionId最后一条数据
    private void getSessionsFromSessionDB() {
        sessionData.clear();
        // sessionData = new ArrayList<ChatSession>();
        SessionDao sessionHelper = new SessionDao(getActivity());
        if (sessionHelper.getSessionList().size() > 0) {
            for (String sessionId : sessionHelper.getSessionList()) {
                List<ChatSession> chatSessionList = sessionHelper
                        .getMessageBySession(sessionId);
                if (chatSessionList.size() > 0) {
                    ChatSession chatSession = chatSessionList
                            .get(chatSessionList.size() - 1);
                    sessionData.add(chatSession);
                }
            }
            Collections.sort(sessionData);
        }
    }

    @Override
    public void onPause() {
        app.StopObservePortait(imgPortrait);
        super.onPause();
        // messageCanDeleteAdapter.closeAllSlideView();
    }

    private void initViews(final List<ChatSession> sessionData) {
        imgPortrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.optionClicked();
            }
        });
        messageListView = (SlideCutListView) getView().findViewById(
                R.id.messageListView);
        messageCanDeleteAdapter = new MessageCanDeleteAdapter(getActivity(),
                sessionData);
        messageListView.setSlideCutListViewCloseItemLinstener(this);
        messageListView.setAdapter(messageCanDeleteAdapter);
        messageListView.setOnSlideListener(this);
        messageListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                messageCanDeleteAdapter.closeAllSlideView();
                ChatSession chatSession = sessionData.get(position);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                if (!chatSession.isGroup()) {
                    // if (Settings.instance().getUserId()
                    // .equals(mySessions.getUserId1())) {
                    // intent.putExtra("NickName", mySessions.getNickName1());
                    // intent.putExtra("NickName2", mySessions.getNickName2());
                    // intent.putExtra("UserId", mySessions.getUserId1());
                    // intent.putExtra("UserId2", mySessions.getUserId2());
                    // } else {
                    intent.putExtra("NickName", Settings.instance().getMyName());
                    intent.putExtra("NickName2", chatSession.getSenderNickName());
                    intent.putExtra("UserId", Settings.instance().getUserId());
                    intent.putExtra("UserId2", chatSession.getSenderUserId());
                    intent.putExtra("SessionId", chatSession.getSessionId());
                    intent.putExtra("Portrait2", chatSession.getSenderPortrait());
                    intent.putExtra("Gender", chatSession.getSenderGender());
                    intent.putExtra("isGroup", false);
                } else {
//					群聊
                    intent.putExtra("SessionId", chatSession.getSessionId());
                    intent.putExtra("GroupName", chatSession.getGroupName());
                    intent.putExtra("GroupNote", chatSession.getGroupNote());
                    intent.putExtra("OwnerId", chatSession.getOwnerId());
                    intent.putExtra("isGroup", true);

                }
                getActivity().startActivity(intent);
            }
        });
    }

    @Override
    public void onSlide(View view, int status) {
        // if (mLastSlideViewWithStatusOn != null
        // && mLastSlideViewWithStatusOn != view) {
        // slideCutListView.setListViewNotMove(true);
        // mLastSlideViewWithStatusOn.shrink();
        // }
        if (status == DOWN && mLastSlideViewWithStatusOn != null
                && mLastSlideViewWithStatusOn != view) {
            messageListView.setListViewNotMove(true);
            mLastSlideViewWithStatusOn.shrink();
            messageListView.setListViewNotMove(false);
        }

        if (status == SLIDE_STATUS_START_SCROLL) {
            mLastSlideViewWithStatusOn = (SlideView) view;
        }
    }

    @Override
    public void updateRecentSession() {
        Logger.info("message fragment updateRecentSession");
        if (messageCanDeleteAdapter != null) {
            getSessionsFromSessionDB();
            messageCanDeleteAdapter.refresh();
        }
    }

    @Override
    public void SlideCutListViewCloseItem() {
        messageCanDeleteAdapter.closeAllSlideView();
    }

}
