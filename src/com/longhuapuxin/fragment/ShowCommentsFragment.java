package com.longhuapuxin.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.longhuapuxin.adapter.CommentsAdapter;
import com.longhuapuxin.adapter.U5BaseAdapter;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.Utils;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.db.bean.Estimation;
import com.longhuapuxin.entity.ResponseShowComments;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.u5.Settings;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZH on 2016/3/11.
 * Email zh@longhuapuxin.com
 */
public class ShowCommentsFragment extends Fragment {

    private View mView;
    private ListView mCommentsLv;
    private CommentsAdapter mAdapter;
    private List<Estimation> mComments;
    private String mFeedBackType;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.fragment_show_comments, null);
        init();
        return mView;
    }

    private void init() {
        mComments = new ArrayList<Estimation>();
        mCommentsLv = (ListView) mView.findViewById(R.id.commentsLv);
        mAdapter = new CommentsAdapter(getActivity(), mComments);
        mAdapter.setmFeedBackType(mFeedBackType);
        mCommentsLv.setAdapter(mAdapter);
    }

    public void setType(String feedbackType) {
        mFeedBackType = feedbackType;
    }

    public void fetchData(String feedBackId,String targetUserId,String targetShopCode) {
        WaitDialog.instance().showWaitNote(getActivity());
        Settings setting = Settings.instance();
        if(feedBackId==null) feedBackId="";
        if(targetUserId==null) targetUserId="";
        if(targetShopCode==null) targetShopCode="";
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[7];
        params[0] = new OkHttpClientManager.Param("Token", setting.getToken());
        params[1] = new OkHttpClientManager.Param("UserId", setting.getUserId());
        params[2] = new OkHttpClientManager.Param("FeedBackId", feedBackId);
        params[3] = new OkHttpClientManager.Param("TargetUserId", targetUserId);
        params[4] = new OkHttpClientManager.Param("TargetShopCode", targetShopCode);
        params[5] = new OkHttpClientManager.Param("PageIndex", "1");
        params[6] = new OkHttpClientManager.Param("PageSize", "20");


        OkHttpClientManager
                .postAsyn(
                        setting.getApiUrl() + "/im/viewfeedback",
                        params,
                        new OkHttpClientManager.ResultCallback<ResponseShowComments>() {
                            @Override
                            public void onError(Request request, Exception e) {
                                e.printStackTrace();
                                WaitDialog.instance().hideWaitNote();
                            }

                            @Override
                            public void onResponse(
                                    ResponseShowComments response) {

                                if (response.isSuccess()) {

                                    mComments.addAll(response.getFeedBacks());
                                    mAdapter.setmFeedBackType(mFeedBackType);
                                    mAdapter.SetItems(mComments);
                                    mAdapter.notifyDataSetChanged();


                                }
                                WaitDialog.instance().hideWaitNote();
                            }
                        });
    }
}
