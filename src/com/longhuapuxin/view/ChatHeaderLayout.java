package com.longhuapuxin.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.longhuapuxin.u5.ChatActivity;
import com.longhuapuxin.u5.R;

public class ChatHeaderLayout extends FrameLayout implements OnClickListener {
    private LayoutInflater mInflater;
    private View mHeader;
    private ImageView back, attentionPersonal, attentionGroup;
    TextView name;

    public ChatHeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {
        mInflater = LayoutInflater.from(context);
        mHeader = mInflater.inflate(R.layout.chat_header, null);
        addView(mHeader);
        initViews();
    }

    private void initViews() {
        back = (ImageView) mHeader.findViewById(R.id.back);
        back.setOnClickListener(this);
        attentionPersonal = (ImageView) mHeader
                .findViewById(R.id.attention_personal);
        attentionGroup = (ImageView) mHeader
                .findViewById(R.id.attention_group);
        attentionPersonal.setOnClickListener(this);
        attentionGroup.setOnClickListener(this);
        name = (TextView) findViewById(R.id.name);
    }

    @Override
    public void onClick(View v) {
        if (v == back) {
            ChatActivity chatAvtivity = (ChatActivity) getContext();
            chatAvtivity.onBackPressed();
        } else if (v == attentionPersonal) {
            if (senderUserLinstener != null) {
                senderUserLinstener.getSenderUser();
            }
        } else if (v == attentionGroup) {

        }
    }

    public interface SenderUserLinstener {
        public void getSenderUser();
    }

    public SenderUserLinstener senderUserLinstener;

    public void setSenderUserLinstener(SenderUserLinstener senderUserLinstener) {
        this.senderUserLinstener = senderUserLinstener;
    }
}
