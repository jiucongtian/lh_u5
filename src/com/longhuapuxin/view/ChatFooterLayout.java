package com.longhuapuxin.view;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.longhuapuxin.fragment.ExpressionFragment;
import com.longhuapuxin.u5.CreateEnvelopeActivity;
import com.longhuapuxin.u5.R;

public class ChatFooterLayout extends FrameLayout implements OnClickListener, ExpressionFragment.Expressionlistener {
    private LayoutInflater mInflater;
    private View mFooter;
    private EditText chatContent;
    private Context context;
    private ImageView send, unSend, expression, transaction, money;
    private FrameLayout mFragmentContainer;
    private FragmentManager fm = null;
    private boolean mIsGroup = false;
    private String mId;

    public ChatFooterLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context);
    }

    public void init(Context context) {
        fm = ((Activity)context).getFragmentManager();
        mInflater = LayoutInflater.from(context);
        mFooter = mInflater.inflate(R.layout.chat_footer, null);
        addView(mFooter);
        initViews();
    }


    private void initViews() {
        send = (ImageView) findViewById(R.id.send);
        unSend = (ImageView) findViewById(R.id.unsend);
        send.setOnClickListener(this);
        chatContent = (EditText) findViewById(R.id.chat_content);
        chatContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 0) {
                    send.setVisibility(GONE);
                    unSend.setVisibility(VISIBLE);
                } else {
                    unSend.setVisibility(GONE);
                    send.setVisibility(VISIBLE);

                }
            }
        });
        expression = (ImageView) findViewById(R.id.expression);
        expression.setOnClickListener(this);
        transaction = (ImageView) findViewById(R.id.transaction);
        transaction.setOnClickListener(this);
        money = (ImageView) findViewById(R.id.money);
        money.setOnClickListener(this);

        mFragmentContainer = (FrameLayout) findViewById(R.id.fragmentContainer);
        FragmentTransaction transaction = fm.beginTransaction();
        ExpressionFragment fragment = new ExpressionFragment(this);
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }

    public void initGroupId(String id, boolean isGroup) {
        this.mId = id;
        this.mIsGroup = isGroup;
    }


    private boolean softInputViewIsSpread;

    @Override
    public void onClick(View v) {
        if (v == send) {
            // if (mySoftInputViewListener != null) {
            // mySoftInputViewListener.softInputViewShow(chatContent);
            // }
            if (TextUtils.isEmpty(chatContent.getText().toString())) {
                Toast.makeText(context, "输入内容不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            sendMsgListener.sendMsg(chatContent.getText().toString());
            chatContent.setText("");
            mFragmentContainer.setVisibility(View.GONE);
            // httpRequestTalkTo();
        } else if (v == expression) {
            if(mFragmentContainer.getVisibility() == View.VISIBLE) {
                mFragmentContainer.setVisibility(View.GONE);
            } else {
                mFragmentContainer.setVisibility(View.VISIBLE);
            }

        } else if (v == money) {
            Intent intent = new Intent(context, CreateEnvelopeActivity.class);
//            String idType = mIsGroup ? "CircleId" : "SessionId";
            intent.putExtra(CreateEnvelopeActivity.IS_GROUP, mIsGroup);
            intent.putExtra(CreateEnvelopeActivity.ID, mId);
//            intent.putExtra(CreateEnvelopeActivity.COUNT, 12);
            context.startActivity(intent);
        }
    }

    @Override
    public void onExperssionSelected(String code) {
        chatContent.append(code);
//        mFragmentContainer.setVisibility(View.GONE);
    }

    public interface SoftInputViewListener {
        void softInputViewShow(View view);

        void softInputViewHide();
    }

    private SoftInputViewListener mySoftInputViewListener;

    public void setSoftInputViewListener(
            SoftInputViewListener mySoftInputViewListener) {
        this.mySoftInputViewListener = mySoftInputViewListener;
    }

    public interface SendMsgListener {
        void sendMsg(String msg);
    }

    private SendMsgListener sendMsgListener;

    public void setSendMsgListener(SendMsgListener sendMsgListener) {
        this.sendMsgListener = sendMsgListener;
    }


}
