package com.longhuapuxin.u5;

import android.content.Intent;
import android.os.Bundle;
import com.longhuapuxin.fragment.CommentsFragment;

/**
 * Created by ZH on 2016/3/9.
 * Email zh@longhuapuxin.com
 */
public class CommentsActivity extends BaseActivity implements CommentsFragment.SubmitCommentsListener {


    private CommentsFragment mCommentsFragment;
    private String mOrderType, mOrderId, mUserName, mOrderNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        initHeader(R.string.personalComments);

        mCommentsFragment = (CommentsFragment) getFragmentManager()
                .findFragmentById(R.id.commentsFragment);

        Intent intent = getIntent();
        mOrderType = intent.getStringExtra("OrderType");
        mOrderId = intent.getStringExtra("OrderId");
        mUserName = intent.getStringExtra("UserName");
        mOrderNote = intent.getStringExtra("OrderNote");

        mCommentsFragment.setupComments(this, mOrderType, mUserName, mOrderNote, mOrderId);
    }

    @Override
    public void callBack() {
        this.finish();
    }
}
