package com.longhuapuxin.u5;

import android.content.Intent;
import android.os.Bundle;

import com.longhuapuxin.fragment.CommentsFragment;
import com.longhuapuxin.fragment.ShowCommentsFragment;

/**
 * Created by ZH on 2016/3/11.
 * Email zh@longhuapuxin.com
 */
public class ShowCommentsActivity extends BaseActivity {

    private Intent mIntent;
    private String mFeedBackId, mFeedBackType,mTargetUserId,mTargetShopCode;
    private ShowCommentsFragment mShowCommentsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_comments);
        initHeader(R.string.showComments);
        init();
    }

    private void init() {
        mIntent = getIntent();
        mFeedBackId = mIntent.getStringExtra("FeedBackId");
        mFeedBackType = mIntent.getStringExtra("OrderType");
        mTargetUserId=mIntent.getStringExtra("TargetUserId");
        mTargetShopCode=mIntent.getStringExtra("TargetShopCode");
        mShowCommentsFragment = (ShowCommentsFragment) getFragmentManager()
                .findFragmentById(R.id.showCommentsFragment);
        mShowCommentsFragment.setType(mFeedBackType);
        mShowCommentsFragment.fetchData(mFeedBackId,mTargetUserId,mTargetShopCode);
    }
}
